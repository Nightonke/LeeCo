package com.nightonke.leetcoder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;

/**
 * Created by Weiping on 2016/2/24.
 */
public class CategoryFragment extends Fragment
        implements
        CategoryFragmentAdapter.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    public static final int SORT_BY_ID = 0;
    public static final int SORT_BY_ID_REVERSE = 1;
    public static final int SORT_BY_NAME = 2;
    public static final int SORT_BY_NAME_REVERSE = 3;
    public static final int SORT_BY_LEVEL = 4;
    public static final int SORT_BY_LEVEL_REVERSE = 5;
    public static final int SORT_BY_LIKE = 6;
    public static final int SORT_BY_LIKE_REVERSE = 7;

    public static int sortType = SORT_BY_ID;

    private Context mContext;

    private int categoryPosition = -1;

    private SwipeRefreshLayout swipeRefreshLayout;
    private OnRefreshListener onRefreshListener;

    private SuperRecyclerView superRecyclerView;
    private CategoryFragmentAdapter adapter;

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);

        if (context instanceof OnRefreshListener){
            onRefreshListener = (OnRefreshListener)context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View categoryFragment = inflater.inflate(R.layout.fragment_category, container, false);
        superRecyclerView = (SuperRecyclerView)categoryFragment.findViewById(R.id.recyclerview);
        swipeRefreshLayout = (SwipeRefreshLayout)categoryFragment.findViewById(R.id.refresh_layout);
        return categoryFragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        categoryPosition = FragmentPagerItem.getPosition(getArguments());

        superRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new CategoryFragmentAdapter(categoryPosition, this);
        superRecyclerView.setAdapter(adapter);

        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(mContext, R.color.colorPrimary));
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(mContext, ProblemActivity.class);
        intent.putExtra("categoryPosition", categoryPosition);
        intent.putExtra("problemPosition", position);
        intent.putExtra("id", LeetCoderApplication.categories.get(categoryPosition).get(position).getId());
        mContext.startActivity(intent);
    }

    @Override
    public void onRefresh() {
        onRefreshListener.onRefresh(swipeRefreshLayout);
    }

    public interface OnRefreshListener {
        void onRefresh(SwipeRefreshLayout swipeRefreshLayout);
    }

    public void stopRefresh() {
        if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
    }

    public void notifySort() {
        adapter = new CategoryFragmentAdapter(categoryPosition, this);
        superRecyclerView.setAdapter(adapter);
    }
}
