package com.nightonke.leetcoder.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.nightonke.leetcoder.R;
import com.nightonke.leetcoder.Utils.LeetCoderUtil;
import com.squareup.picasso.Picasso;

/**
 * Created by Weiping on 2016/3/2.
 */

public class HelpAdapter extends RecyclerView.Adapter<HelpAdapter.ViewHolder> {

    public static int[] TITLES = new int[] {
            R.string.about_app_title,
            R.string.basic_title,
            R.string.user_title,
            R.string.favorite_title,
            R.string.problem_title,
            R.string.solution_title,
            R.string.discuss_title,
            R.string.comment_title,
            R.string.other_title
    };

    public static int[] IMAGES = new int[] {
            R.drawable.leeco_show_white,
            R.drawable.basic,
            R.drawable.user,
            R.drawable.favorite,
            R.drawable.problem,
            R.drawable.solution,
            R.drawable.discuss,
            R.drawable.comment,
            R.drawable.other
    };
    
    public static int[] CONTENTS = new int[] {
            R.string.about_app_content,
            R.string.basic_content,
            R.string.user_content,
            R.string.favorite_content,
            R.string.problem_content,
            R.string.solution_content,
            R.string.discuss_content,
            R.string.comment_content,
            R.string.other_content
    };

    private Context mContext;
    private int width = 0;
    private SparseBooleanArray expandState = new SparseBooleanArray();

    public HelpAdapter(Context mContext) {
        this.mContext = mContext;
        width = LeetCoderUtil.GetScreenWidth(mContext) - LeetCoderUtil.dpToPx(40);
        for (int i = 0; i < TITLES.length; i++) {
            expandState.append(i, true);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_help, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);

        holder.nameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.expandableRelativeLayout.toggle();
                expandState.put(position, holder.expandableRelativeLayout.isExpanded());
            }
        });
        holder.expandableRelativeLayout.setExpanded(expandState.get(position));
        holder.expandableRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.expandableRelativeLayout.toggle();
                expandState.put(position, holder.expandableRelativeLayout.isExpanded());
            }
        });
        holder.name.setText(TITLES[position]);
        holder.detail.setText(CONTENTS[position]);

        Drawable d = ContextCompat.getDrawable(mContext, IMAGES[position]);
        ViewGroup.LayoutParams layoutParams = holder.image.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = width * d.getIntrinsicHeight() / d.getIntrinsicWidth();
        Picasso.with(mContext)
                .load(IMAGES[position])
                .resize(width, width * d.getIntrinsicHeight() / d.getIntrinsicWidth())
                .centerCrop()
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return TITLES.length;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout nameLayout;
        public TextView name;
        public ExpandableRelativeLayout expandableRelativeLayout;
        public ImageView image;
        public TextView detail;

        public ViewHolder(View v) {
            super(v);
            nameLayout = (LinearLayout)v.findViewById(R.id.name_layout);
            name = (TextView)v.findViewById(R.id.name);
            expandableRelativeLayout = (ExpandableRelativeLayout)v.findViewById(R.id.expandable_layout);
            image = (ImageView)v.findViewById(R.id.image);
            detail = (TextView)v.findViewById(R.id.detail);
        }
    }
}
