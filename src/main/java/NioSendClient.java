import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/**
 * 文件传输Client端
 *
 * @author LiuYaoWen
 * @date 2022/7/26 下午11:35
 */
public class NioSendClient {

    static Logger logger = Logger.getLogger("NioSendClient");

    private final Charset charset = StandardCharsets.UTF_8;

    public void sendFile() throws IOException {
        File file = new File("/media/yufei/Data/wallpaper/test.txt");
        if (!file.exists()) {
            logger.warning("文件不存在");
            return;
        }
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            FileChannel fileChannel = fileInputStream.getChannel();
            try (SocketChannel socketChannel = SocketChannel.open()) {
                socketChannel.socket().connect(new InetSocketAddress("127.0.0.1", 18899));
                socketChannel.configureBlocking(false);
                logger.info("Client 成功连接服务端");
                while (!socketChannel.finishConnect()) {
                    logger.info("等待连接成功");
                }
                // 发送文件名称和长度
                ByteBuffer buffer = sendFileNameAngLength("test.txt", file, socketChannel);
                int length = sendContent(file, fileChannel, socketChannel, buffer);
                if (length == -1) {
                    socketChannel.shutdownOutput();
                }
                logger.info("===== 文件传输成功 ====");
            }
        }
    }

    public ByteBuffer sendFileNameAngLength(String destFile, File file, SocketChannel socketChannel) throws IOException {
        // 发送文件名称
        ByteBuffer fileNameByteBuffer = charset.encode(destFile);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        // 发送文件名称长度
        int fileNameLen = fileNameByteBuffer.capacity();
        buffer.putInt(fileNameLen);
        buffer.flip();
        socketChannel.write(buffer);
        buffer.clear();
        logger.info("Client 文件名称长度发送完成：" + fileNameLen);
        // 发送文件名称
        socketChannel.write(fileNameByteBuffer);
        logger.info("Client 文件名称发送完成：" + destFile);
        // 发送文件长度
        buffer.putLong(file.length());
        buffer.flip();
        socketChannel.write(buffer);
        buffer.clear();
        logger.info("Client 文件长度发送完成：" + file.length());
        return buffer;
    }

    public int sendContent(File file, FileChannel fileChannel, SocketChannel socketChannel, ByteBuffer buffer) throws IOException {
        logger.info("开始传输文件");
        int length;
        long progress = 0;
        while ((length = fileChannel.read(buffer)) > 0) {
            buffer.flip();
            socketChannel.write(buffer);
            buffer.clear();
            progress += length;
            logger.info("| " + (100 * progress / file.length()) + "%|");
        }
        return length;
    }

    public static void main(String[] args) {
        NioSendClient client = new NioSendClient();
        try {
            client.sendFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
