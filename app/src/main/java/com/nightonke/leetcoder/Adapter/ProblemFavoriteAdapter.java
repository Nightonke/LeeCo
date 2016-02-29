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

public class ProblemFavoriteAdapter
        extends RecyclerView.Adapter<ProblemFavoriteAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Problem_Index> likeProblems;
    private OnLikeItemClickListener onLikeItemClickListener;
    private OnLikeItemLongClickListener onLikeItemLongClickListener;

    public ProblemFavoriteAdapter(ArrayList<Problem_Index> likeProblems, OnLikeItemClickListener onLikeItemClickListener, OnLikeItemLongClickListener onLikeItemLongClickListener) {
        this.likeProblems = likeProblems;
        this.onLikeItemClickListener = onLikeItemClickListener;
        this.onLikeItemLongClickListener = onLikeItemLongClickListener;
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
                onLikeItemClickListener.onLikeItemClick(position);
            }
        });
        holder.base.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onLikeItemLongClickListener.onLikeItemLongClick(likeProblems.get(position));
                return true;
            }
        });
        String title = likeProblems.get(position).getTitle();
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(
                        LeetCoderUtil.getTextDrawableString(title),
                        LeetCoderUtil.GetRandomColor());
        holder.drawable.setImageDrawable(drawable);
        holder.title.setText(title);
        holder.summary.setText(likeProblems.get(position).getSummary());
        int like = likeProblems.get(position).getLike();
        if (like == 1) {
            holder.like.setText(like + " Like");
        } else {
            holder.like.setText(like + " Likes");
        }
        String level = likeProblems.get(position).getLevel();
        holder.level.setText(level);
        if ("Easy".equals(level)) holder.levelIcon.setImageResource(R.drawable.icon_easy);
        else if ("Medium".equals(level)) holder.levelIcon.setImageResource(R.drawable.icon_medium);
        else if ("Hard".equals(level)) holder.levelIcon.setImageResource(R.drawable.icon_hard);
    }

    @Override
    public int getItemCount() {
        return likeProblems.size();
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
        public View divider;

        public ViewHolder(View v) {
            super(v);
            base = (LinearLayout)v.findViewById(R.id.base_layout);
            drawable = (ImageView) v.findViewById(R.id.imageview);
            title = (TextView)v.findViewById(R.id.title);
            summary = (TextView)v.findViewById(R.id.summary);
            like = (AutofitTextView)v.findViewById(R.id.like);
            level = (AutofitTextView)v.findViewById(R.id.level);
            levelIcon = (ImageView)v.findViewById(R.id.level_icon);
            divider = v.findViewById(R.id.divider);
        }
    }

    public interface OnLikeItemClickListener {
        void onLikeItemClick(int position);
    }

    public interface OnLikeItemLongClickListener {
        void onLikeItemLongClick(Problem_Index problemIndex);
    }

}
