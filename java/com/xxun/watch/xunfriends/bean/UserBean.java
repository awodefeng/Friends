package com.xxun.watch.xunfriends.bean;

import java.io.Serializable;

/**
 * @author cuiyufeng
 * @Description: UserBean
 * @date 2019/1/21 14:57
 */
public class UserBean implements Serializable{
    private int code;
    private String nickName;//发布朋友圈使用该名称
    private String head;//头像显示使用该名称
    private byte [] headbyte; //处理过的头像Base64

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public byte[] getHeadbyte() {
        return headbyte;
    }

    public void setHeadbyte(byte[] headbyte) {
        this.headbyte = headbyte;
    }


}
