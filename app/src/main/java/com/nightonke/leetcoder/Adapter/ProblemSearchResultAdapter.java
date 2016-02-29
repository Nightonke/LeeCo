package com.nightonke.leetcoder.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.nightonke.leetcoder.Utils.LeetCoderUtil;
import com.nightonke.leetcoder.Model.Problem_Index;
import com.nightonke.leetcoder.R;

import java.util.ArrayList;

import me.grantland.widget.AutofitTextView;

/**
 * Created by Weiping on 2016/2/23.
 */

public class ProblemSearchResultAdapter
        extends RecyclerView.Adapter<ProblemSearchResultAdapter.ViewHolder> {

    private final int TYPE_LAST = 0;
    private final int TYPE_NORMAL = 1;

    private Context mContext;
    private ArrayList<Problem_Index> searchResults;
    private OnItemClickListener onItemClickListener;

    public ProblemSearchResultAdapter(ArrayList<Problem_Index> searchResults, OnItemClickListener onItemClickListener) {
        this.searchResults = searchResults;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) return TYPE_LAST;
        else return TYPE_NORMAL;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        switch (viewType) {
            case TYPE_NORMAL: {
                return new ViewHolder(LayoutInflater.from(mContext)
                        .inflate(R.layout.item_problem, parent, false));
            }
            case TYPE_LAST: {
                return new ViewHolder(LayoutInflater.from(mContext)
                        .inflate(R.layout.item_problem_search_last, parent, false));
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        switch (getItemViewType(position)) {
            case TYPE_NORMAL:
                holder.base.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(position);
                    }
                });
                String title = searchResults.get(position).getTitle();
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(
                                LeetCoderUtil.getTextDrawableString(title),
                                LeetCoderUtil.GetRandomColor());
                holder.drawable.setImageDrawable(drawable);
                holder.title.setText(title);
                holder.summary.setText(searchResults.get(position).getSummary());
                int like = searchResults.get(position).getLike();
                if (like == 1) {
                    holder.like.setText(like + " Like");
                } else {
                    holder.like.setText(like + " Likes");
                }
                String level = searchResults.get(position).getLevel();
                holder.level.setText(level);
                if ("Easy".equals(level)) holder.levelIcon.setImageResource(R.drawable.icon_easy);
                else if ("Medium".equals(level)) holder.levelIcon.setImageResource(R.drawable.icon_medium);
                else if ("Hard".equals(level)) holder.levelIcon.setImageResource(R.drawable.icon_hard);
                break;
            case TYPE_LAST:
                break;
        }
    }

    @Override
    public int getItemCount() {
        try {
            if (searchResults.size() == 0) {
                return 0;
            } else {
                return searchResults.size() + 1;
            }
        } catch (NullPointerException n) {
            n.printStackTrace();
            return 0;
        }

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
        public AutofitTextView like;
        public AutofitTextView level;
        public ImageView levelIcon;

        public ViewHolder(View v) {
            super(v);
            base = (LinearLayout)v.findViewById(R.id.base_layout);
            drawable = (ImageView) v.findViewById(R.id.imageview);
            title = (TextView)v.findViewById(R.id.title);
            summary = (TextView)v.findViewById(R.id.summary);
            like = (AutofitTextView)v.findViewById(R.id.like);
            level = (AutofitTextView)v.findViewById(R.id.level);
            levelIcon = (ImageView)v.findViewById(R.id.level_icon);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}
