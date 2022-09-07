package com.lyw.nio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

/**
 * 文件传输Server端
 *
 * @author LiuYaoWen
 * @date 2022/7/25 下午11:28
 */
public class NioReceiveServer {

    /**
     * 接收文件路径
     */
    private static final String RECEIVE_PATH = "/media/yufei/Data/NIOTest";

    private final Charset charset = StandardCharsets.UTF_8;

    static Logger logger = Logger.getLogger("com.lyw.nio.NioReceiveServer");

    /**
     * 服务端保存的客户端对象，对应一个客户端文件
     */
    static class Client {
        // 文件名称
        String filename;

        // 长度
        long fileLength;

        // 开始传输的时间
        long startTime;

        // 客户端的地址
        InetSocketAddress remoteAddress;

        // 输出的文件通道
        FileChannel outChannel;

        // 接收长度
        long receiveLength;

        public boolean isFinished() {
            return receiveLength >= fileLength;
        }

    }

    private final ByteBuffer buffer = ByteBuffer.allocate(1024);

    Map<SelectableChannel, Client> clientMap = new HashMap<>();

    public void startServer() throws IOException {
        // 获取选择器
        try (Selector selector = Selector.open()) {
            // 获取通道
            try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
                ServerSocket serverSocket = serverSocketChannel.socket();
                // 设置非阻塞
                serverSocketChannel.configureBlocking(false);
                InetSocketAddress address = new InetSocketAddress(18899);
                // 绑定连接
                serverSocket.bind(address);
                // 将通道注册到选择器上，并且注册的IO事件为“接收新连接”
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
                logger.info("serverChannel is listening...");
                // 轮询感兴趣的IO就绪事件（选择键集合）
                while (selector.select() > 0) {
                    Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                    while (it.hasNext()) {
                        SelectionKey selectedKey = it.next();
                        // 判断key是具体的什么事件，是否为新连接事件
                        if (selectedKey.isAcceptable()) {
                            // 若接收的事件是“新连接”，则获取客户端新连接
                            try (ServerSocketChannel server = (ServerSocketChannel) selectedKey.channel()) {
                                SocketChannel socketChannel = server.accept();
                                if (socketChannel == null) {
                                    continue;
                                }
                                // 客户端新连接，切换为非阻塞模式
                                socketChannel.configureBlocking(false);
                                // 将客户端新连接通道注册到Selector上
                                socketChannel.register(selector, SelectionKey.OP_READ);
                                Client client = new Client();
                                client.remoteAddress = (InetSocketAddress) socketChannel.getRemoteAddress();
                                clientMap.put(socketChannel, client);
                                logger.info(socketChannel.getRemoteAddress() + "连接成功...");
                            }
                        } else if (selectedKey.isReadable()) {
                            processData(selectedKey);
                        }
                        // NIO的特点是只会累加，已选择键的集合不会删除
                        // 如果哦不删除，下一次又会被select()函数选中
                        it.remove();
                    }
                }
            }
        }
    }

    private void processData(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        Client client = clientMap.get(socketChannel);
        int num;
        buffer.clear();
        while ((num = socketChannel.read(buffer)) > 0) {
            buffer.flip();
            // 客户端发送过来的，首先处理文件名
            if (null == client.filename) {
                if (buffer.capacity() < 4) {
                    continue;
                }
                int fileNameLen = buffer.getInt();
                byte[] fileNameBytes = new byte[fileNameLen];
                buffer.get(fileNameBytes);
                String fileName = new String(fileNameBytes, charset);
                File directory = new File(RECEIVE_PATH);
                if (!directory.exists()) {
                    boolean isSuccess = directory.mkdir();
                    if (!isSuccess) {
                        logger.warning("文件夹创建失败");
                    }
                }
                logger.info("NIO 传输目标dir: " + directory);
                client.filename = fileName;
                String fullName = directory.getAbsolutePath() + File.separatorChar + fileName;
                logger.info("NIO 传输目标文件: " + fullName);
                File file = new File(fullName.trim());
                if (!file.exists()) {
                    boolean isSuccess = file.createNewFile();
                    if (!isSuccess) {
                        logger.warning("文件创建失败");
                    }
                }
                try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                    client.outChannel = fileOutputStream.getChannel();
                    if (buffer.capacity() < 8) {
                        continue;
                    }
                    client.fileLength = buffer.getLong();
                    client.startTime = System.currentTimeMillis();
                    logger.info("NIO 传输开始：");
                    client.receiveLength += buffer.capacity();
                    if (buffer.capacity() > 0) {
                        // 写入文件
                        client.outChannel.write(buffer);
                    }
                    if (client.isFinished()) {
                        finished(key, client);
                    }
                    buffer.clear();
                }
            } else {
                // 客户端发送过来的，最后是文件内容
                client.receiveLength += buffer.capacity();
                // 写入文件
                client.outChannel.write(buffer);
                if (client.isFinished()) {
                    finished(key, client);
                }
                buffer.clear();
            }
        }
        key.cancel();
        if (num == -1) {
            finished(key, client);
            buffer.clear();
        }
    }

    private void finished(SelectionKey key, Client client) throws IOException {
        client.outChannel.close();
        logger.info("上传完毕");
        key.cancel();
        logger.info("文件接收成功，File Name：" + client.filename);
        long endTime = System.currentTimeMillis();
        logger.info("NIO IO 传输毫秒数： " + (endTime - client.startTime));
    }

    public static void main(String[] args) {
        NioReceiveServer server = new NioReceiveServer();
        try {
            server.startServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
