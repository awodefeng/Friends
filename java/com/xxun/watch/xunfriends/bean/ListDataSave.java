package com.xxun.watch.xunfriends.bean;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author cuiyufeng
 * @Description: ListDataSave
 * @date 2019/3/7 18:18
 */
public class ListDataSave {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    public static final String share_pref_name = "friends_share";
    public static final String share_pref_head_name = "friends_head_share";

    public ListDataSave(Context mContext) {
        preferences = mContext.getSharedPreferences(share_pref_name, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    /**
     * 保存朋友圈List
     * @ tag SharedPreferences 的key 在这里固定friendskey 读取的时候也是friendskey
     * @param datalist
     */
    public void setDataList(ArrayList<ListBean> datalist) {
        if (null == datalist || datalist.size() <= 0)
            return ;
        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(datalist);
        editor.clear();
        editor.putString("friendslist", strJson);//key value
        editor.commit();
    }

    /**
     * 获取朋友圈List
     * @ tag SharedPreferences 的文件名称
     * @return
     */
    public ArrayList<ListBean> getDataList() {
        ArrayList<ListBean> datalist=new ArrayList<ListBean>();
        String strJson = preferences.getString("friendslist", null);//key
        if (null == strJson) {
            return datalist;
        }
        Gson gson = new Gson();
        datalist = gson.fromJson(strJson, new TypeToken<List<ListBean>>() {
        }.getType());
        return datalist;
    }

    //这里的key先固定了 目前就一个地方用
    public static void setStringValue(Context mContext, String value) {
        final SharedPreferences preferences = mContext.getSharedPreferences("friendslist_fastkey", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        try {
            editor.putString("fast_friendslist_key", value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        editor.commit();
    }

    public static String getStringValue(Context mContext, String defValue) {
        return mContext.getSharedPreferences("friendslist_fastkey", Context.MODE_PRIVATE)
                .getString("fast_friendslist_key", defValue);
    }

    //头像缓存 + 点赞缓存
    public static void setShareValue(Context mContext,String key, String value) {
        final SharedPreferences preferences = mContext.getSharedPreferences(share_pref_head_name, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        try {
            editor.putString(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        editor.commit();
    }

    public static String getShareValue(Context mContext,String key, String defValue) {
        return mContext.getSharedPreferences(share_pref_head_name, Context.MODE_PRIVATE)
                .getString(key, defValue);
    }


    /**
     * 朋友圈列表头像保存
     */
    public static void putHashMapData(Context context,String key,HashMap<String,String> datas) {
        JSONArray mJsonArray = new JSONArray();
        Iterator<Map.Entry<String, String>> iterator = datas.entrySet().iterator();
        JSONObject object = new JSONObject();
        /**
         * 采用Iterator遍历HashMap
         Iterator it = hashMap.keySet().iterator();
         while(it.hasNext()) {
         String key = (String)it.next();
         System.out.println("key:" + key);
         System.out.println("value:" + hashMap.get(key));
         }
         */
        while (iterator.hasNext()) {
            Map.Entry<String,String> entry = iterator.next();
            try {
                object.put(entry.getKey(), entry.getValue());
            } catch (JSONException e) {
                Log.i("cui","setItemHeradList e"+e);
            }
        }
        mJsonArray.put(object);
        SharedPreferences sp = context.getSharedPreferences("friends_itemhead3",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, mJsonArray.toString()); // 这个文件由始至终只有一个内容 key先固定
        editor.commit();
    }

    public static HashMap<String, String> getHashMapData(Context context,String key) {
        HashMap<String, String> datas = new HashMap<>();
        SharedPreferences sp = context.getSharedPreferences("friends_itemhead3",Context.MODE_PRIVATE);
        String result = sp.getString(key, "");
        try {
            JSONArray array = new JSONArray(result);
            for (int i = 0; i < array.length(); i++) {
                JSONObject itemObject = array.getJSONObject(i);
                Log.i("cui","itemObject.tostring = "+itemObject.toString());
                JSONArray names = itemObject.names();
                Log.i("cui","namesarray = "+names.toString());
                if (names != null) {
                    for (int j = 0; j < names.length(); j++) {
                        String name = names.getString(j);
                        Log.i("cui","name = "+name);
                        String value = itemObject.getString(name);
                        Log.i("cui","value = "+value);
                        datas.put(name, value);
                    }
                }
            }

        } catch (JSONException e) {
            Log.i("cui","getHashMapData e"+e);
        }
        return datas;
    }





}

