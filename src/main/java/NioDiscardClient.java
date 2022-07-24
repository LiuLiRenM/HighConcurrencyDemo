import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

/**
 * Discard服务器 客户端
 *
 * @author LiuYaoWen
 * @date 2022/7/24 22:20
 */
public class NioDiscardClient {

    static Logger logger = Logger.getLogger("NioDiscardClient");

    public static void startClient() throws IOException {
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 18899);
        // 获取通道
        try (SocketChannel socketChannel = SocketChannel.open(address)) {
            // 切换为非阻塞模式
            socketChannel.configureBlocking(false);
            // 不断自旋、等待连接完成，或者做一些其他的事情
            while (!socketChannel.finishConnect()) {
                logger.info("正在等待连接完成......");
            }
            logger.info("客户端连接成功");
            // 分配指定大小的缓冲区
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            byteBuffer.put("hell world!".getBytes());
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
            socketChannel.shutdownOutput();
        }
    }

    public static void main(String[] args) {
        try {
            startClient();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
