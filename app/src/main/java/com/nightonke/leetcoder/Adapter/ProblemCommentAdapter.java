package com.nightonke.leetcoder.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableLayout;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.nightonke.leetcoder.Utils.LeetCoderUtil;
import com.nightonke.leetcoder.Model.Comment;
import com.nightonke.leetcoder.R;

import java.util.List;

/**
 * Created by Weiping on 2016/2/23.
 */
public class ProblemCommentAdapter
        extends RecyclerView.Adapter<ProblemCommentAdapter.ViewHolder> {

    private final int FIRST = 0;
    private final int NORMAL = 1;
    private final int LAST = 2;

    private Context mContext;
    private List<Comment> comments;
    private SparseBooleanArray expandState = new SparseBooleanArray();

    private OnCardViewClickListener onCardViewClickListener;
    private OnTargetClickListener onTargetClickListener;
    private OnContentLongClickListener onContentLongClickListener;
    private OnReplyClickListener onReplyClickListener;
    private OnLikeClickListener onLikeClickListener;

    public ProblemCommentAdapter(
            List<Comment> comments,
            OnCardViewClickListener onCardViewClickListener,
            OnContentLongClickListener onContentLongClickListener,
            OnTargetClickListener onTargetClickListener,
            OnReplyClickListener onReplyClickListener,
            OnLikeClickListener onLikeClickListener) {
        this.comments = comments;
        this.onCardViewClickListener = onCardViewClickListener;
        this.onContentLongClickListener = onContentLongClickListener;
        this.onTargetClickListener = onTargetClickListener;
        this.onReplyClickListener = onReplyClickListener;
        this.onLikeClickListener = onLikeClickListener;

        for (int i = 0; i < comments.size(); i++) {
            expandState.append(i, true);
        }
    }

    private void onClickButton(final ExpandableLayout expandableLayout) {
        expandableLayout.toggle();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        switch (viewType) {
            case FIRST: {
                return new ViewHolder(LayoutInflater.from(mContext)
                        .inflate(R.layout.item_comment_first, parent, false));
            }
            case NORMAL: {
                return new ViewHolder(LayoutInflater.from(mContext)
                        .inflate(R.layout.item_comment_normal, parent, false));
            }
            case LAST: {
                return new ViewHolder(LayoutInflater.from(mContext)
                        .inflate(R.layout.item_comment_last, parent, false));
            }
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return FIRST;
        else if (position == getItemCount() - 1) return LAST;
        else return NORMAL;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int p) {
        switch (getItemViewType(p)) {
            case FIRST:
                break;
            case NORMAL:
                final int position = p - 1;
                final Comment comment = comments.get(position);
                holder.base.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickButton(holder.expandLayout);
                        onCardViewClickListener.onCardViewClick(position);
                    }
                });
                holder.base.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        onContentLongClickListener.onContentLongClick(holder.likeNumber, position);
                        return true;
                    }
                });
                holder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickButton(holder.expandLayout);
                    }
                });
                holder.expandLayout.setListener(new ExpandableLayoutListenerAdapter() {
                    @Override
                    public void onPreOpen() {
                        LeetCoderUtil.createRotateAnimator(holder.button, 0f, 180f).start();
                        expandState.put(position, true);
                    }

                    @Override
                    public void onPreClose() {
                        LeetCoderUtil.createRotateAnimator(holder.button, 180f, 0f).start();
                        expandState.put(position, false);
                    }
                });
                holder.reply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onReplyClickListener.onReplyClick(position);
                    }
                });
                holder.like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onLikeClickListener.onLikeClick(holder.likeNumber, position);
                    }
                });

                holder.title.setText(comment.getTitle());
                holder.button.setRotation(expandState.get(position) ? 180f : 0f);
                holder.expandLayout.setExpanded(expandState.get(position));
                final String targetId = comment.getTargetComment();
                if (targetId != null && !"".equals(targetId)) {
                    holder.targetLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onTargetClickListener.onTargetClick(targetId);
                        }
                    });

                    for (Comment c : comments) {
                        if (c.getObjectId().equals(targetId)) {
                            holder.targetTitle.setText(c.getTitle());
                        }
                    }
                } else {
                    holder.targetLayout.setVisibility(View.GONE);
                }
                holder.content.setText(comment.getContent());
                holder.date.setText(LeetCoderUtil.bmobDateToMyDate(comment.getUpdatedAt()));
                holder.writer.setText(comment.getUserName());
                holder.replyNumber.setText(comment.getReplies().size() + "");
                holder.likeNumber.setText(comment.getLikers().size() + "");
                holder.index.setText((position + 1) + "");
                break;
            case LAST:
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (comments.size() == 0) return 0;
        else return comments.size() + 2;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout base;
        public TextView title;
        public RelativeLayout button;
        public LinearLayout targetLayout;
        public TextView targetTitle;
        public ExpandableRelativeLayout expandLayout;
        public TextView content;
        public TextView date;
        public TextView writer;
        public FrameLayout reply;
        public TextView replyNumber;
        public FrameLayout like;
        public TextView likeNumber;
        public TextView index;

        public ViewHolder(View v) {
            super(v);
            base = (LinearLayout)v.findViewById(R.id.base_layout);
            title = (TextView)v.findViewById(R.id.title);
            button = (RelativeLayout)v.findViewById(R.id.button);
            targetLayout = (LinearLayout)v.findViewById(R.id.target_layout);
            targetTitle = (TextView)v.findViewById(R.id.target_title);
            expandLayout = (ExpandableRelativeLayout)v.findViewById(R.id.expandableLayout);
            content = (TextView)v.findViewById(R.id.content);
            date = (TextView)v.findViewById(R.id.date);
            writer = (TextView)v.findViewById(R.id.username);
            reply = (FrameLayout)v.findViewById(R.id.reply_layout);
            replyNumber = (TextView)v.findViewById(R.id.reply_number);
            like = (FrameLayout)v.findViewById(R.id.like_layout);
            likeNumber = (TextView)v.findViewById(R.id.like_number);
            index = (TextView)v.findViewById(R.id.index);
        }
    }

    public interface OnCardViewClickListener {
        void onCardViewClick(int position);
    }

    public interface OnTargetClickListener {
        void onTargetClick(String objectId);
    }

    // copy or edit
    public interface OnContentLongClickListener {
        void onContentLongClick(TextView likeNumber, int position);
    }

    public interface OnReplyClickListener {
        void onReplyClick(int position);
    }

    public interface OnLikeClickListener {
        void onLikeClick(TextView likeNumber, int position);
    }

}
