package com.nightonke.leetcoder.Fragment;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.nightonke.leetcoder.Activity.EditCommentActivity;
import com.nightonke.leetcoder.Activity.ProblemActivity;
import com.nightonke.leetcoder.BuildConfig;
import com.nightonke.leetcoder.Model.Vote;
import com.nightonke.leetcoder.Utils.LeetCoderApplication;
import com.nightonke.leetcoder.Utils.LeetCoderUtil;
import com.nightonke.leetcoder.Model.Comment;
import com.nightonke.leetcoder.Adapter.ProblemCommentAdapter;
import com.nightonke.leetcoder.R;
import com.nightonke.leetcoder.Model.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Weiping on 2016/1/8.
 */

public class ProblemCommentFragment extends Fragment
        implements
        View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        ProblemCommentAdapter.OnCardViewClickListener,
        ProblemCommentAdapter.OnContentLongClickListener,
        ProblemCommentAdapter.OnTargetClickListener,
        ProblemCommentAdapter.OnReplyClickListener,
        ProblemCommentAdapter.OnLikeClickListener {

    public static final int SORT_BY_DATE = 0;
    public static final int SORT_BY_DATE_REVERSELY = 1;
    public static final int SORT_BY_LIKES = 2;
    public static final int SORT_BY_LIKES_REVERSELY = 3;

    public static int sortType = 1;
    private boolean loading = false;

    private LinearLayoutManager linearLayoutManager;
    private SuperRecyclerView superRecyclerView;
    private ProblemCommentAdapter adapter;
    private List<Comment> comments = new ArrayList<>();

    private Boolean isRefreshing = false;
    private RelativeLayout reloadLayout;
    private ProgressBar progressBar;
    private TextView reload;

    private ProblemActivity activity;
    private Context mContext;

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);

        if (context instanceof ProblemActivity){
            activity = (ProblemActivity)context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View commentFragment = inflater.inflate(R.layout.fragment_problem_comment, container, false);

        reloadLayout = (RelativeLayout)commentFragment.findViewById(R.id.loading_layout);
        reloadLayout.setVisibility(View.VISIBLE);
        progressBar = (ProgressBar)commentFragment.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        reload = (TextView)commentFragment.findViewById(R.id.reload);
        reload.setText(mContext.getResources().getString(R.string.loading));
        reload.setOnClickListener(this);

        superRecyclerView = (SuperRecyclerView) commentFragment.findViewById(R.id.recyclerview);
        superRecyclerView.getSwipeToRefresh().setColorSchemeResources(R.color.colorPrimary);
        linearLayoutManager = new LinearLayoutManager(mContext);
        superRecyclerView.setLayoutManager(linearLayoutManager);
        superRecyclerView.setRefreshListener(this);
        superRecyclerView.getMoreProgressView().setDrawingCacheBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        adapter = new ProblemCommentAdapter(comments, this, this, this, this, this);
        superRecyclerView.setAdapter(adapter);
        superRecyclerView.setVisibility(View.INVISIBLE);

        return commentFragment;
    }

    public void setComment() {
        loading = true;
        setLoading();

        BmobQuery<Comment> query = new BmobQuery<Comment>();
        query.addWhereEqualTo("id", activity.problem.getId());
        query.setLimit(Integer.MAX_VALUE);
        query.findObjects(LeetCoderApplication.getAppContext(), new FindListener<Comment>() {
            @Override
            public void onSuccess(List<Comment> object) {
                if (BuildConfig.DEBUG) Log.d("LeetCoder", "Get comments: " + object.size());
                loading = false;
                reload.setText(mContext.getResources().getString(R.string.reload));  // for refreshing
                reloadLayout.setVisibility(View.GONE);

                comments = object;
                sort(sortType);
                superRecyclerView.setVisibility(View.VISIBLE);
            }
            @Override
            public void onError(int code, String msg) {
                if (BuildConfig.DEBUG) Log.d("LeetCoder", "Get comments failed: " + msg);
                loading = false;
                setReload();
            }
        });

        if (isRefreshing) {
            isRefreshing = false;
//            superRecyclerView.setRefreshing(false);
        }
    }

    public void setLoading() {
        if (isRefreshing) {
            return;
        }

        reloadLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        reload.setText(mContext.getResources().getString(R.string.loading));
    }

    public void setReload() {
        if (isRefreshing) {
            return;
        }

        reloadLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        reload.setText(mContext.getResources().getString(R.string.reload));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reload:
                if (reload.getText().toString().equals(mContext.getResources().getString(R.string.loading))) return;
                reload.setText(mContext.getResources().getString(R.string.loading));
                activity.reload();
                break;
        }
    }

    @Override
    public void onRefresh() {
        isRefreshing = true;
        onClick(reload);
    }

    public boolean isLoading() {
        return loading;
    }

    public boolean sort(int newSortType) {
        if (loading) return false;
        else {
            sortType = newSortType;
            switch (sortType) {
                case SORT_BY_DATE: sortByDate(); break;
                case SORT_BY_DATE_REVERSELY: sortByDateReversely(); break;
                case SORT_BY_LIKES: sortByLikes(); break;
                case SORT_BY_LIKES_REVERSELY: sortByLikesReversely(); break;
            }
            adapter = new ProblemCommentAdapter(comments, ProblemCommentFragment.this, ProblemCommentFragment.this, ProblemCommentFragment.this, ProblemCommentFragment.this, ProblemCommentFragment.this);
            superRecyclerView.setAdapter(adapter);
            return true;
        }
    }

    private void sortByDate() {
        if (comments == null) return;
        Collections.sort(comments, new Comparator<Comment>() {
            @Override
            public int compare(Comment lhs, Comment rhs) {
                Calendar l = LeetCoderUtil.bmobDateToCalendar(lhs.getUpdatedAt());
                Calendar r = LeetCoderUtil.bmobDateToCalendar(rhs.getUpdatedAt());
                if (l.before(r)) return -1;
                else if (r.before(l)) return 1;
                else return 0;
            }
        });
    }

    private void sortByDateReversely() {
        if (comments == null) return;
        Collections.sort(comments, new Comparator<Comment>() {
            @Override
            public int compare(Comment rhs, Comment lhs) {
                Calendar l = LeetCoderUtil.bmobDateToCalendar(lhs.getUpdatedAt());
                Calendar r = LeetCoderUtil.bmobDateToCalendar(rhs.getUpdatedAt());
                if (l.before(r)) return -1;
                else if (r.before(l)) return 1;
                else return 0;
            }
        });
    }

    private void sortByLikes() {
        if (comments == null) return;
        Collections.sort(comments, new Comparator<Comment>() {
            @Override
            public int compare(Comment rhs, Comment lhs) {
                int l = lhs.getLikers().size();
                int r = rhs.getLikers().size();
                if (l < r) return -1;
                else if (r < l) return 1;
                else return 0;
            }
        });
    }

    private void sortByLikesReversely() {
        if (comments == null) return;
        Collections.sort(comments, new Comparator<Comment>() {
            @Override
            public int compare(Comment lhs, Comment rhs) {
                int l = lhs.getLikers().size();
                int r = rhs.getLikers().size();
                if (l < r) return -1;
                else if (r < l) return 1;
                else return 0;
            }
        });
    }

    @Override
    public void onCardViewClick(int position) {
        if (BuildConfig.DEBUG) Log.d("LeetCoder", "onCardViewClick " + position);
    }

    @Override
    public void onContentLongClick(final TextView likeNumber, final int position) {
        if (BuildConfig.DEBUG) Log.d("LeetCoder", "onContentLongClick " + position);
        if (LeetCoderApplication.user != null && LeetCoderApplication.user.getUsername().equals(comments.get(position).getUserName())) {
            new MaterialDialog.Builder(mContext)
                    .title(R.string.operate_title)
                    .positiveText(R.string.operate_edit)
                    .negativeText(R.string.operate_delete)
                    .neutralText(R.string.operate_copy)
                    .forceStacking(true)
                    .onAny(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if (which == DialogAction.POSITIVE) {
                                // edit
                                if (LeetCoderApplication.user == null) {
                                    LeetCoderUtil.showToast(mContext, R.string.comment_not_login);
                                } else {
                                    Intent intent = new Intent(mContext, EditCommentActivity.class);
                                    intent.putExtra("id", comments.get(position).getId());
                                    intent.putExtra("title", comments.get(position).getTitle());
                                    intent.putExtra("content", comments.get(position).getContent());
                                    intent.putExtra("commentObjectId", comments.get(position).getObjectId());
                                    activity.startActivityForResult(intent, ProblemActivity.START_COMMENT);
                                }
                            } else if (which == DialogAction.NEGATIVE) {
                                // delete
                                comments.get(position).delete(LeetCoderApplication.getAppContext(), new DeleteListener() {
                                    @Override
                                    public void onSuccess() {
                                        LeetCoderUtil.showToast(mContext, R.string.comment_delete_successfully);
                                        comments.remove(position);
                                        setComment();
                                    }
                                    @Override
                                    public void onFailure(int i, String s) {
                                        if (BuildConfig.DEBUG) Log.d("LeetCoder", "Delete comment failed: " + s);
                                        LeetCoderUtil.showToast(mContext, R.string.comment_delete_failed);
                                    }
                                });
                            } else {
                                // copy content
                                ClipboardManager clipboard = (ClipboardManager)activity.getSystemService(Activity.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText(mContext.getResources().getString(R.string.comment_copy), comments.get(position).getContent());
                                clipboard.setPrimaryClip(clip);
                                LeetCoderUtil.showToast(mContext, R.string.comment_copy);
                            }
                        }
                    })
                    .show();
        } else {
            final boolean like = comments.get(position).getLikers().contains(LeetCoderApplication.user.getUsername());
            new MaterialDialog.Builder(mContext)
                    .title(R.string.operate_title)
                    .positiveText(R.string.operate_reply)
                    .negativeText(like ? R.string.operate_dislike : R.string.operate_like)
                    .neutralText(R.string.operate_copy)
                    .forceStacking(true)
                    .onAny(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if (which == DialogAction.POSITIVE) {
                                // reply
                                if (LeetCoderApplication.user == null) {
                                    LeetCoderUtil.showToast(mContext, R.string.comment_not_login);
                                } else {
                                    Intent intent = new Intent(mContext, EditCommentActivity.class);
                                    intent.putExtra("id", comments.get(position).getId());
                                    intent.putExtra("title", "");
                                    intent.putExtra("content", "");
                                    intent.putExtra("targetId", comments.get(position).getObjectId());
                                    intent.putExtra("targetTitle", comments.get(position).getTitle());
                                    activity.startActivityForResult(intent, ProblemActivity.START_COMMENT);
                                }
                            } else if (which == DialogAction.NEGATIVE) {
                                // like or dislike
                                onLikeClick(likeNumber, position);
                            } else {
                                // copy content
                                ClipboardManager clipboard = (ClipboardManager)activity.getSystemService(Activity.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText(mContext.getResources().getString(R.string.comment_copy), comments.get(position).getContent());
                                clipboard.setPrimaryClip(clip);
                                LeetCoderUtil.showToast(mContext, R.string.comment_copy);
                            }
                        }
                    })
                    .show();
        }

    }

    @Override
    public void onLikeClick(final TextView likeNumber, final int position) {
        if (BuildConfig.DEBUG) Log.d("LeetCoder", "onLikeClick " + position);
        if (LeetCoderApplication.user == null) {
            LeetCoderUtil.showToast(mContext, R.string.comment_not_login);
        } else {
            if (comments.get(position).getLikers().contains(LeetCoderApplication.user.getUsername())) {
                // dislike
                comments.get(position).getLikers().remove(LeetCoderApplication.user.getUsername());
                comments.get(position).update(LeetCoderApplication.getAppContext(), comments.get(position).getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        // find the vote
                        BmobQuery<Vote> query = new BmobQuery<Vote>();
                        query.addWhereEqualTo("userName", comments.get(position).getUserName());
                        query.setLimit(1);
                        query.findObjects(LeetCoderApplication.getAppContext(), new FindListener<Vote>() {
                            @Override
                            public void onSuccess(List<Vote> object) {
                                int votes = object.get(0).getVote();
                                final int targetVotes = (votes == 0 ? 0 : votes - 1);  // impossible less than zero
                                object.get(0).setVote(targetVotes);
                                // update the vote
                                object.get(0).update(LeetCoderApplication.getAppContext(), object.get(0).getObjectId(), new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        likeNumber.setText(comments.get(position).getLikers().size() + "");
                                        LeetCoderUtil.showToast(mContext, R.string.comment_dislike_successfully);
                                        if (LeetCoderApplication.user != null) {
                                            if (LeetCoderApplication.user.getUsername().equals(comments.get(position).getUserName())) {
                                                //dislike self comment
                                                LeetCoderApplication.user.setVotes(targetVotes);
                                            }
                                        }
                                    }
                                    @Override
                                    public void onFailure(int i, String s) {
                                        if (BuildConfig.DEBUG) Log.d("LeetCoder", "Dislike comment failed: " + s);
                                        LeetCoderUtil.showToast(mContext, R.string.comment_dislike_failed);
                                        comments.get(position).getLikers().add(LeetCoderApplication.user.getUsername());
                                    }
                                });
                            }
                            @Override
                            public void onError(int code, String msg) {
                                if (BuildConfig.DEBUG) Log.d("LeetCoder", "Dislike comment failed: " + msg);
                                LeetCoderUtil.showToast(mContext, R.string.comment_dislike_failed);
                                comments.get(position).getLikers().add(LeetCoderApplication.user.getUsername());
                            }
                        });
                    }
                    @Override
                    public void onFailure(int i, String s) {
                        if (BuildConfig.DEBUG) Log.d("LeetCoder", "Dislike comment failed: " + s);
                        LeetCoderUtil.showToast(mContext, R.string.comment_dislike_failed);
                        comments.get(position).getLikers().add(LeetCoderApplication.user.getUsername());
                    }
                });
            } else {
                // like
                comments.get(position).getLikers().add(LeetCoderApplication.user.getUsername());
                comments.get(position).update(LeetCoderApplication.getAppContext(), comments.get(position).getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        // find the vote
                        BmobQuery<Vote> query = new BmobQuery<Vote>();
                        query.addWhereEqualTo("userName", comments.get(position).getUserName());
                        query.setLimit(1);
                        query.findObjects(LeetCoderApplication.getAppContext(), new FindListener<Vote>() {
                            @Override
                            public void onSuccess(List<Vote> object) {
                                int votes = object.get(0).getVote();
                                final int targetVotes = votes + 1;
                                object.get(0).setVote(targetVotes);
                                // update the vote
                                object.get(0).update(LeetCoderApplication.getAppContext(), object.get(0).getObjectId(), new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        likeNumber.setText(comments.get(position).getLikers().size() + "");
                                        LeetCoderUtil.showToast(mContext, R.string.comment_like_successfully);
                                        if (LeetCoderApplication.user != null) {
                                            if (LeetCoderApplication.user.getUsername().equals(comments.get(position).getUserName())) {
                                                // like self comment
                                                LeetCoderApplication.user.setVotes(targetVotes);
                                            }
                                        }
                                    }
                                    @Override
                                    public void onFailure(int i, String s) {
                                        if (BuildConfig.DEBUG) Log.d("LeetCoder", "Like comment failed: " + s);
                                        LeetCoderUtil.showToast(mContext, R.string.comment_like_failed);
                                        comments.get(position).getLikers().remove(LeetCoderApplication.user.getUsername());
                                    }
                                });
                            }
                            @Override
                            public void onError(int code, String msg) {
                                if (BuildConfig.DEBUG) Log.d("LeetCoder", "Like comment failed: " + msg);
                                LeetCoderUtil.showToast(mContext, R.string.comment_like_failed);
                                comments.get(position).getLikers().remove(LeetCoderApplication.user.getUsername());
                            }
                        });
                    }
                    @Override
                    public void onFailure(int i, String s) {
                        if (BuildConfig.DEBUG) Log.d("LeetCoder", "Like comment failed: " + s);
                        LeetCoderUtil.showToast(mContext, R.string.comment_like_failed);
                        comments.get(position).getLikers().remove(LeetCoderApplication.user.getUsername());
                    }
                });
            }
        }
    }

    @Override
    public void onReplyClick(int position) {
        if (BuildConfig.DEBUG) Log.d("LeetCoder", "onReplyClick " + position);
        if (LeetCoderApplication.user == null) {
            LeetCoderUtil.showToast(mContext, R.string.comment_not_login);
        } else {
            Intent intent = new Intent(mContext, EditCommentActivity.class);
            intent.putExtra("id", comments.get(position).getId());
            intent.putExtra("title", "");
            intent.putExtra("content", "");
            intent.putExtra("targetId", comments.get(position).getObjectId());
            intent.putExtra("targetTitle", comments.get(position).getTitle());
            activity.startActivityForResult(intent, ProblemActivity.START_COMMENT);
        }
    }

    @Override
    public void onTargetClick(String objectId) {
        if (BuildConfig.DEBUG) Log.d("LeetCoder", "onTargetClick " + objectId);
        int position = 0;
        for (Comment comment : comments) {
            if (comment.getObjectId().equals(objectId)) {
                linearLayoutManager.scrollToPosition(position);
            }
            position++;
        }
    }
}
