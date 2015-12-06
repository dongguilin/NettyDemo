package com.guilin.netty.codec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

/**
 * Created by hadoop on 2015/12/6.
 * 对比测试jdk序列化机制编码与二进制编码
 */
public class TestUserInfo {

    public static void main(String[] args) throws IOException {
        sizeTest();
        performanceTest();
    }

    //编码所占空间大小测试
    public static void sizeTest() throws IOException {
        UserInfo info = new UserInfo();
        info.buildUserID(100).buildUserName("Welcome to Netty");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bos);
        os.writeObject(info);
        os.flush();
        os.close();
        byte[] b = bos.toByteArray();
        System.out.println("The jdk serializable length is:" + b.length);//jdk序列化机制编码大小
        bos.close();

        System.out.println("------------------------");
        System.out.println("The byte array serializable length is:" + info.codeC().length);//二进制编码大小
    }

    //性能测试
    public static void performanceTest() throws IOException {
        UserInfo info = new UserInfo();
        info.buildUserID(100).buildUserName("Welcome to Netty");
        int loop = 100000;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream os = null;
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < loop; i++) {
            bos = new ByteArrayOutputStream();
            os = new ObjectOutputStream(bos);

            os.writeObject(info);
            os.flush();
            os.close();
            byte[] b = bos.toByteArray();
            bos.close();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("The jdk serializable cost time is:" + (endTime - startTime) + " ms");
        System.out.println("---------------------");

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        startTime = System.currentTimeMillis();
        for (int i = 0; i < loop; i++) {
            byte[] b = info.codeC(buffer);
        }
        endTime = System.currentTimeMillis();
        System.out.println("The byte array serializable cost time is:" + (endTime - startTime) + " ms");
    }

}
