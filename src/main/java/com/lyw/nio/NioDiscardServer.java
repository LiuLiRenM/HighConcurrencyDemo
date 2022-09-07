package com.lyw.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Discard服务器 服务端
 *
 * @author LiuYaoWen
 * @date 2022/7/23 23:05
 */
public class NioDiscardServer {

    static Logger logger = Logger.getLogger("com.lyw.nio.NioDiscardServer");

    public static void startServer() throws IOException {
        // 获取选择器
        try (Selector selector = Selector.open()) {
            // 获取通道
            try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
                // 设置为非阻塞
                serverSocketChannel.configureBlocking(false);
                // 绑定连接
                serverSocketChannel.bind(new InetSocketAddress(18899));
                logger.info("服务器启动成功");
                // 将通道注册的“接受新连接”IO事件注册到选择器上
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
                // 轮询感兴趣的IO就绪事件（选择键集合）
                while (selector.select() > 0) {
                    // 获取选择键集合
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    for (SelectionKey selectedKey : selectionKeys) {
                        // 获取单个的选择键，并处理
                        // 判断key是具体的什么事件
                        if (selectedKey.isAcceptable()) {
                            // 若选择键的IO事件是“连接就绪”，就获取客户端连接
                            SocketChannel socketChannel = serverSocketChannel.accept();
                            // 将新连接切换为非阻塞模式
                            socketChannel.configureBlocking(false);
                            // 将新连接的通道的可读事件注册到选择器上
                            socketChannel.register(selector, SelectionKey.OP_READ);
                        } else if (selectedKey.isReadable()) {
                            // 若选择键的IO事件是“可读”，则读取数据
                            try (SocketChannel socketChannel = (SocketChannel) selectedKey.channel()) {
                                // 读取数据，然后丢弃
                                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                                int length;
                                while ((length = socketChannel.read(byteBuffer)) > 0) {
                                    byteBuffer.flip();
                                    logger.info(new String(byteBuffer.array(), 0, length));
                                    byteBuffer.clear();
                                }
                            }
                        }
                        // 移除选择键
                        selectionKeys.remove(selectedKey);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            startServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
