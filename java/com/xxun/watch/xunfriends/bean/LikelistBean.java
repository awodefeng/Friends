package com.xxun.watch.xunfriends.bean;

import java.io.Serializable;

/**
 * @author cuiyufeng
 * @Description: likelistBean
 * @date 2019/1/1 14:55
 */
public class LikelistBean  implements Serializable {
    private String EID;//点赞好友EID
    private String timestamp;//点赞时间
    private String nickName;//点赞设备昵称，如没有为空串
    private String head;//点赞设备头像的base64，如没有为空串

    public String getEID() {
        return EID;
    }

    public void setEID(String EID) {
        this.EID = EID;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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

    @Override
    public String toString() {
        return "LikelistBean{" +
                "EID='" + EID + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", nickName='" + nickName + '\'' +
                ", head='" + head + '\'' +
                '}';
    }
}