package com.nightonke.leetcoder.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.nightonke.leetcoder.Utils.LeetCoderApplication;
import com.nightonke.leetcoder.Utils.LeetCoderUtil;
import com.nightonke.leetcoder.R;

/**
 * Created by Weiping on 2016/2/25.
 */
public class TagGridViewAdapter extends BaseAdapter {
    private LayoutInflater inflater;

    public TagGridViewAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (LeetCoderApplication.categoriesTag == null) return 0;
        else return LeetCoderApplication.categoriesTag.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.item_tag, null);
            holder.iv = (ImageView) convertView.findViewById(R.id.tag_view);
            holder.tv = (TextView) convertView.findViewById(R.id.tag_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String logo = LeetCoderApplication.categories.get(position).size() + "";

        TextDrawable drawable = TextDrawable.builder().buildRound(logo, LeetCoderUtil.GetRandomColor());

        holder.iv.setImageDrawable(drawable);
        holder.tv.setText(LeetCoderApplication.categoriesTag.get(position));
        return convertView;
    }

    private class ViewHolder {
        ImageView iv;
        TextView tv;
    }
}