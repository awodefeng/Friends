package com.xxun.watch.xunfriends.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jixiang on 2017/9/15.
 */
public class SyncArrayBean {
    /**
     * number : 13817873481
     * contactsType : 0
     * oldupdateTS : null
     * attri : 0
     * id : 20170915144759289
     * name : 爸爸
     * contact_weight : 0
     * user_gid : 9630244E70BC936E56049C4DB07A3596
     * updateTS : 20170915144821387
     * user_eid : 5E0FBF12F7728F1B40F71855998DA312
     * sub_number : 666666666666666666
     */

    @SerializedName("number")
    public String number;
    @SerializedName("contactsType")
    public int contactsType;
    @SerializedName("oldupdateTS")
    public String oldupdateTS;
    @SerializedName("attri")
    public int attri;
    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;
    @SerializedName("contact_weight")
    public int contactWeight;
    @SerializedName("user_gid")
    public String userGid;
    @SerializedName("updateTS")
    public String updateTS;
    @SerializedName("user_eid")
    public String userEid;
    @SerializedName("sub_number")
    public String subNumber;
    @SerializedName("avatar")
    public String avatar;

    public static SyncArrayBean objectFromData(String str) {

        return new Gson().fromJson(str, SyncArrayBean.class);
    }

    public static SyncArrayBean objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), SyncArrayBean.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<SyncArrayBean> arraySyncArrayBeanFromData(String str) {

        Type listType = new TypeToken<ArrayList<SyncArrayBean>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<SyncArrayBean> arraySyncArrayBeanFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<SyncArrayBean>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }
}
