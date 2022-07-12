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
     * allocate()方法测试 --> 创建对应的实例对象
     *
     * @description 演示如何使用Buffer类的allocate()方法
     * @author LiuYaoWen
     * @date 2022/7/11 23:20
     */
    public static void allocateTest() {
        // 创建一个 intBuffer 实例对象
        intBuffer = IntBuffer.allocate(20);
        commonLog(intBuffer, "----------after allocate----------");
    }

    /**
     * put()方法测试 --> 将数据写入缓冲区中
     *
     * @author LiuYaoWen
     * @date 2022/7/12 22:11
     */
    public static void putTest() {
        // 创建一个 intBuffer 实例对象
        intBuffer = IntBuffer.allocate(20);
        int loop = 5;
        for (int i = 0; i < loop; i++) {
            // 将i写入缓冲区中
            intBuffer.put(i);
        }
        commonLog(intBuffer, "---------after putTest------------");
    }

    /**
     * flip()方法测试 --> 将缓冲区的写模式切换为读模式
     *
     * @author LiuYaoWen
     * @date 2022/7/12 22:17
     */
    public static void flipTest() {
        // 创建一个 intBuffer 实例对象
        intBuffer = IntBuffer.allocate(20);
        int loop = 5;
        for (int i = 0; i < loop; i++) {
            // 将i写入缓冲区中
            intBuffer.put(i);
        }
        intBuffer.flip();
        commonLog(intBuffer, "----------after flip-----------");
    }

    /**
     * get()方法测试 --> 从缓冲区读取数据
     *
     * @author LiuYaoWen
     * @date 2022/7/12 22:37
     */
    public static void getTest() {
        intBuffer = IntBuffer.allocate(20);
        int loop = 5;
        for (int i = 0; i < loop; i++) {
            intBuffer.put(i);
        }
        intBuffer.flip();
        // 先读取两个数
        loop = 2;
        for (int i = 0; i < loop; i++) {
            int j = intBuffer.get();
            logger.info("Buffer data: " + j);
        }
        commonLog(intBuffer, "------after get 2 int-----------");
        // 再读取三个数
        loop = 3;
        for (int i = 0; i < loop; i++) {
            int j = intBuffer.get();
            logger.info("Buffer data: " + j);
        }
        commonLog(intBuffer, "------after get 3 int-----------");
    }

    /**
     * 通用的日志输出
     *
     * @param intBuffer intBuffer对象
     * @param msg       日志信息
     * @author LiuYaoWen
     * @date 2022/7/12 22:33
     */
    public static void commonLog(IntBuffer intBuffer, String msg) {
        logger.info(msg);
        logger.info("position=" + intBuffer.position());
        logger.info("limit=" + intBuffer.limit());
        logger.info("capacity=" + intBuffer.capacity());
    }

    /**
     * rewind()方法测试 --> 重置position值，使得Buffer可以重新从头开始读取
     *
     * @author LiuYaoWen
     * @date 2022/7/12 22:48
     */
    public static void rewindTest() {
        getTest();
        intBuffer.rewind();
        commonLog(intBuffer, "------------after rewind-----------");
    }

    /**
     * mark()方法和reset()方法测试 --> 将从标记处重新开始读取数据
     *
     * @author LiuYaoWen
     * @date 2022/7/12 23:08
     */
    public static void markAndResetTest() {
        // 准备测试数据
        putTest();
        int loop = 5;
        intBuffer.flip();
        // 读取全部数据
        for (int i = 0; i < loop; i++) {
            if (i == 2) {
                // 在第三个数处标记一下，此时mark=2
                intBuffer.mark();
            }
            logger.info("Buffer data: " + intBuffer.get());
        }
        commonLog(intBuffer, "------after read------");
        // 将mark字段值赋值给position字段，reset之前，position为5，reset之后，position为2
        intBuffer.reset();
        commonLog(intBuffer, "-------after reset----------");
    }

    public static void main(String[] args) {
//        allocateTest();
//        putTest();
//        flipTest();
//        getTest();
//        rewindTest();
        markAndResetTest();
    }
}
