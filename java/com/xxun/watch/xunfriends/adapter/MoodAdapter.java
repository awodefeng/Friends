package com.xxun.watch.xunfriends.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xxun.watch.xunfriends.R;
import com.xxun.watch.xunfriends.activity.MainActivity;
import com.xxun.watch.xunfriends.bean.ListBean;
import com.xxun.watch.xunfriends.widget.CircleTransform;
import com.xxun.watch.xunfriends.widget.GlideCircleTransform;

import java.util.ArrayList;

/**
 * @author cuiyufeng
 * @Description: MoodAdapter
 * @date 2018/12/21 18:48
 */
public class MoodAdapter extends BaseAdapter {

    private Activity context;
    private ArrayList<ListBean> moodlist;

    public MoodAdapter(Activity context, ArrayList<ListBean> list) {
        this.context = context;
        this.moodlist = list;
    }

    @Override
    public int getCount() {
        return moodlist != null && moodlist.size() > 0 ? moodlist.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return moodlist != null && moodlist.size() > 0 ? moodlist.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list, null);

            holder.iv_moodhead= (ImageView) convertView.findViewById(R.id.iv_moodhead);
            holder.tv_moodname = (TextView) convertView.findViewById(R.id.tv_moodname);
            holder.tv_moodtime = (TextView) convertView.findViewById(R.id.tv_moodtime);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ListBean bean= moodlist.get(position);
        holder.iv_moodhead.setBackgroundResource(bean.getDrawable());
        holder.tv_moodname.setText(bean.getContent());
        //Glide.with(context).load(R.mipmap.bg_findfriends).bitmapTransform(new GlideCircleTransform(context)).into(holder.iv_moodhead);
        return convertView;
    }

    class ViewHolder {
        ImageView iv_moodhead;
        TextView tv_moodname;
        TextView tv_moodtime;
    }

}