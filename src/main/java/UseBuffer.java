import java.nio.IntBuffer;
import java.util.logging.Logger;

/**
 * Java NIO Buffer类详解
 *
 * @author LiuYaoWen
 * @date 2022/7/11 23:16
 */
public class UseBuffer {

    /**
     * 一个整型的Buffer静态变量
     */
    static IntBuffer intBuffer = null;

    static Logger logger = Logger.getLogger("UseBuffer");

    /**
     * allocate()方法测试
     *
     * @description 演示如何使用Buffer类的allocate()方法
     * @author LiuYaoWen
     * @date 2022/7/11 23:20
     */
    public static void allocateTest() {
        // 创建一个 intBuffer 实例对象
        intBuffer = IntBuffer.allocate( 20);
        logger.info("----------after allocate----------");
        logger.info("position=" + intBuffer.position());
        logger.info("limit=" + intBuffer.limit());
        logger.info("capacity=" + intBuffer.capacity());
    }

    public static void main(String[] args) {
        allocateTest();
    }
}
