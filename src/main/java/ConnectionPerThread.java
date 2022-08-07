import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Connection Per Thread模式示例代码
 *
 * @author LiuYaoWen
 * @date 2022/8/7 下午11:06
 */
public class ConnectionPerThread implements Runnable {

    @Override
    public void run() {
        try {
            // 服务器监听socket
            try (ServerSocket serverSocket = new ServerSocket(8080)) {
                while (!Thread.interrupted()) {
                    Socket socket = serverSocket.accept();
                    // 接收一个连接后，为socket连接，新建一个专属的处理器对象
                    Handler handler = new Handler(socket);
                    // 创建新线程，专门负责一个连接的处理
                    new Thread(handler).start();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class Handler implements Runnable {
        final Socket socket;

        Handler(Socket s) {
            socket = s;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    byte[] input = new byte[1024];
                    // 读取数据
                    int read = socket.getInputStream().read(input);
                    if (read != -1) {
                        System.out.println("数据读取失败");
                        break;
                    }
                    byte[] output = new byte[1024];
                    // 写入结果
                    socket.getOutputStream().write(output);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
