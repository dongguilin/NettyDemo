package com.guilin.netty.codec;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * Created by hadoop on 2015/12/6.
 */
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userName;

    private int userID;

    //使用进制编码
    public byte[] codeC() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);//分配一个字节缓冲区
        byte[] value = this.userName.getBytes();
        buffer.putInt(value.length);
        buffer.put(value);
        buffer.putInt(this.userID);
        buffer.flip();//反转些缓冲区，使缓冲区为一系列新的通道写入或相对获取 操作做好准备：它将限制设置为当前位置，然后将位置设置为 0。

        value = null;
        byte[] result = new byte[buffer.remaining()];//remaining返回当前位置与限制之间的元素数
        buffer.get(result);
        return result;
    }

    public byte[] codeC(ByteBuffer buffer) {
        buffer.clear();
        byte[] value = this.userName.getBytes();
        buffer.putInt(value.length);
        buffer.put(value);
        buffer.putInt(this.userID);
        buffer.flip();//反转些缓冲区，使缓冲区为一系列新的通道写入或相对获取 操作做好准备：它将限制设置为当前位置，然后将位置设置为 0。

        value = null;
        byte[] result = new byte[buffer.remaining()];//remaining返回当前位置与限制之间的元素数
        buffer.get(result);
        return result;
    }

    public UserInfo buildUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public UserInfo buildUserID(int userID) {
        this.userID = userID;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
}
