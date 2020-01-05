package com.xxun.watch.xunfriends.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cuiyufeng
 * @Description: ListBean
 * @date 2018/12/26 11:14
 */
public class ListBean implements Serializable {
    private String EID;//发布该条朋友圈的设备ID，服务端发布时会自行写入
    private String Type;//类型
    private String Content;//文本内容 //或者表情名称
    private String srcUrl;//资源URL
    private String miniUrl;//资源微缩图URL
    private ArrayList<LikelistBean> likelist =new ArrayList<>();//点赞列表
    private LocationBean.LocationPL loc;
    private String nickName;

    private String Key;//本条朋友圈的Key唯一ID，服务端自行写入

    private String timestamp;
    //本地表情列表
    private int drawable;//表情图标
    public ListBean(){

    }
    public ListBean (int drawable,String Content){
        this.drawable=drawable;
        this.Content=Content;
    }

    public String getEID() {
        return EID;
    }

    public void setEID(String EID) {
        this.EID = EID;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        this.Type = type;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        this.Content = content;
    }

    public String getSrcUrl() {
        return srcUrl;
    }

    public void setSrcUrl(String srcUrl) {
        this.srcUrl = srcUrl;
    }

    public String getMiniUrl() {
        return miniUrl;
    }

    public void setMiniUrl(String miniUrl) {
        this.miniUrl = miniUrl;
    }

    public ArrayList<LikelistBean> getLikelist() {
        return likelist;
    }

    public void setLikelist(ArrayList<LikelistBean> likelist) {
        this.likelist = likelist;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        this.Key = key;
    }

    public LocationBean.LocationPL getLoc() {
        return loc;
    }

    public void setLoc(LocationBean.LocationPL loc) {
        this.loc = loc;
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

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    @Override
    public String toString() {
        return "ListBean{" +
                "EID='" + EID + '\'' +
                ", Type='" + Type + '\'' +
                ", Content='" + Content + '\'' +
                ", srcUrl='" + srcUrl + '\'' +
                ", miniUrl='" + miniUrl + '\'' +
                ", likelist=" + likelist +
                ", loc=" + loc +
                ", nickName='" + nickName + '\'' +
                ", Key='" + Key + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}