package com.lyw.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * 使用FileChannel复制文件 demo
 *
 * @author LiuYaoWen
 * @date 2022/7/13 22:51
 */
public class FileNioCopyDemo {

    static Logger logger = Logger.getLogger("com.lyw.nio.FileNioCopyDemo");

    public static void nioCopyResourceFile() throws IOException {
        String srcPath = "E:\\NIOTest\\1.txt";
        String destPath = "E:\\NIOTest\\2.txt";
        nioCopyFile(srcPath, destPath);
    }

    /**
     * 复制源文件内容到目标文件中（NIO方式）
     *
     * @param srcPath  源文件
     * @param destPath 目标文件
     * @author LiuYaoWen
     * @date 2022/7/13 22:56
     */
    public static void nioCopyFile(String srcPath, String destPath) throws IOException {
        File srcFile = new File(srcPath);
        File destFile = new File(destPath);
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            if (!destFile.exists()) {
                boolean isCreated = destFile.createNewFile();
                if (!isCreated) {
                    logger.warning("create file failed!");
                }
            }
            long startTime = System.currentTimeMillis();
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(destFile);
            inChannel = fis.getChannel();
            outChannel = fos.getChannel();
            // 新建Buffer，处于写模式下
            ByteBuffer buf = ByteBuffer.allocate(1024);
            while (inChannel.read(buf) != -1) {
                // buf第一次模式切换，翻转buf，从写模式编程读模式
                buf.flip();
                int outLength;
                while ((outLength = outChannel.write(buf)) != 0) {
                    logger.info("number of bytes written: " + outLength);
                }
                // buf第二次模式切换，清除buf，变成写模式
                buf.clear();
            }

            outChannel.force(true);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Objects.requireNonNull(outChannel).close();
            fos.close();
            inChannel.close();
            Objects.requireNonNull(fis).close();
        }
    }

    public static void main(String[] args) {
        try {
            nioCopyResourceFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
