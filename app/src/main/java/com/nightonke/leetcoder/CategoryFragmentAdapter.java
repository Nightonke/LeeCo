package com.nightonke.leetcoder;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;

import me.grantland.widget.AutofitTextView;

/**
 * Created by Weiping on 2016/2/23.
 */
public class CategoryFragmentAdapter
        extends RecyclerView.Adapter<CategoryFragmentAdapter.ViewHolder> {

    private Context mContext;
    private int pagePosition;
    private OnItemClickListener onItemClickListener;

    public CategoryFragmentAdapter(int pagePosition, OnItemClickListener onItemClickListener) {
        this.pagePosition = pagePosition;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_problem, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.base.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position);
            }
        });
        String title = LeetCoderApplication.categories.get(pagePosition).get(position).getTitle();
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(
                        LeetCoderUtil.getTextDrawableString(title),
                        LeetCoderUtil.GetRandomColor());
        holder.drawable.setImageDrawable(drawable);
        holder.title.setText(title);
        holder.summary.setText(LeetCoderApplication.categories.get(pagePosition).get(position).getSummary());
        int like = LeetCoderApplication.categories.get(pagePosition).get(position).getLike();
        if (like == 1) {
            holder.like.setText(like + " Like");
        } else {
            holder.like.setText(like + " Likes");
        }
    }

    @Override
    public int getItemCount() {
        return LeetCoderApplication.categories.get(pagePosition).size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout base;
        public ImageView drawable;
        public TextView title;
        public TextView summary;
        public TextView like;

        public ViewHolder(View v) {
            super(v);
            base = (LinearLayout)v.findViewById(R.id.base_layout);
            drawable = (ImageView) v.findViewById(R.id.imageview);
            title = (TextView)v.findViewById(R.id.title);
            summary = (TextView)v.findViewById(R.id.summary);
            like = (TextView)v.findViewById(R.id.like);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}
