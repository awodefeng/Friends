package com.xxun.watch.xunfriends.bean;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

/**
 * @author cuiyufeng
 * @Description: friendsBean
 * @date 2018/12/26 11:07
 */
public class FriendsBean implements Serializable {
    private int code;
    private ArrayList<ListBean>  List =new ArrayList<>();

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ArrayList<ListBean> getList() {
        return List;
    }

    public void setList(ArrayList<ListBean> list) {
        List = list;
    }
}
