package com.nightonke.leetcoder.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nightonke.leetcoder.Fragment.ProblemDiscussFragment;
import com.nightonke.leetcoder.Model.Discuss;
import com.nightonke.leetcoder.R;

import java.util.ArrayList;

import me.grantland.widget.AutofitTextView;

/**
 * Created by Weiping on 2016/2/23.
 */
public class ProblemDiscussAdapter
        extends RecyclerView.Adapter<ProblemDiscussAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Discuss> discusses;
    private OnItemClickListener onItemClickListener;

    public ProblemDiscussAdapter(ArrayList<Discuss> discusses, OnItemClickListener onItemClickListener) {
        this.discusses = discusses;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_discuss, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.base.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position);
            }
        });
        int vote, answer, view;
        switch (ProblemDiscussFragment.sortType) {
            case ProblemDiscussFragment.SORT_BY_TIME:
            case ProblemDiscussFragment.SORT_BY_HOT:
            case ProblemDiscussFragment.SORT_BY_VOTE:
                holder.numberBackground.setBackgroundResource(R.drawable.background_vote);
                holder.number.setText(discusses.get(position).getVote());
                vote = stringToInt(discusses.get(position).getVote());
                if (Math.abs(vote) == 1) holder.numberText.setText("vote");
                else holder.numberText.setText("votes");
                holder.otherNumber1.setText(discusses.get(position).getAnswer());
                answer = stringToInt(discusses.get(position).getAnswer());
                if (Math.abs(answer) == 1) holder.otherNumberText1.setText(" answer ");
                else holder.otherNumberText1.setText(" answers ");
                holder.otherNumber2.setText(discusses.get(position).getView());
                view = stringToInt(discusses.get(position).getView());
                if (Math.abs(view) == 1) holder.otherNumberText2.setText(" view");
                else holder.otherNumberText2.setText(" views");
                break;
            case ProblemDiscussFragment.SORT_BY_ANSWER:
                holder.numberBackground.setBackgroundResource(R.drawable.background_answer);
                holder.number.setText(discusses.get(position).getAnswer());
                answer = stringToInt(discusses.get(position).getAnswer());
                if (Math.abs(answer) == 1) holder.numberText.setText("answer");
                else holder.numberText.setText("answers");
                holder.otherNumber1.setText(discusses.get(position).getVote());
                vote = stringToInt(discusses.get(position).getVote());
                if (Math.abs(vote) == 1) holder.otherNumberText1.setText(" vote ");
                else holder.otherNumberText1.setText(" votes ");
                holder.otherNumber2.setText(discusses.get(position).getView());
                view = stringToInt(discusses.get(position).getView());
                if (Math.abs(view) == 1) holder.otherNumberText2.setText(" view");
                else holder.otherNumberText2.setText(" views");
                break;
            case ProblemDiscussFragment.SORT_BY_VIEW:
                holder.numberBackground.setBackgroundResource(R.drawable.background_view);
                holder.number.setText(discusses.get(position).getView());
                view = stringToInt(discusses.get(position).getView());
                if (Math.abs(view) == 1) holder.numberText.setText("view");
                else holder.numberText.setText("views");
                holder.otherNumber1.setText(discusses.get(position).getVote());
                vote = stringToInt(discusses.get(position).getVote());
                if (Math.abs(vote) == 1) holder.otherNumberText1.setText(" vote ");
                else holder.otherNumberText1.setText(" votes ");
                holder.otherNumber2.setText(discusses.get(position).getAnswer());
                answer = stringToInt(discusses.get(position).getAnswer());
                if (Math.abs(answer) == 1) holder.otherNumberText2.setText(" answer");
                else holder.otherNumberText2.setText(" answers");
                break;
        }
        holder.title.setText(discusses.get(position).getTitle());
        holder.date.setText(discusses.get(position).getDate());
        holder.asker.setText(discusses.get(position).getAsker());
    }

    private int stringToInt(String string) {
        if (string.charAt(0) == '+') {
            try {
                return Integer.valueOf(string.substring(1));
            } catch (NumberFormatException n) {
                return 0;
            }

        }
        else {
            try {
                return Integer.valueOf(string);
            } catch (NumberFormatException n) {
                return 0;
            }
        }
    }

    @Override
    public int getItemCount() {
        return discusses.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout base;
        public AutofitTextView number;
        public View numberBackground;
        public TextView numberText;
        public TextView title;
        public TextView date;
        public TextView asker;
        public TextView otherNumber1;
        public TextView otherNumberText1;
        public TextView otherNumber2;
        public TextView otherNumberText2;

        public ViewHolder(View v) {
            super(v);
            base = (LinearLayout)v.findViewById(R.id.base_layout);
            number = (AutofitTextView)v.findViewById(R.id.number);
            numberBackground = v.findViewById(R.id.number_background);
            numberText = (TextView)v.findViewById(R.id.number_text);
            title = (TextView)v.findViewById(R.id.title);
            date = (TextView)v.findViewById(R.id.date);
            asker = (TextView)v.findViewById(R.id.asker);
            otherNumber1 = (TextView)v.findViewById(R.id.other_number_1);
            otherNumberText1 = (TextView)v.findViewById(R.id.other_number_text_1);
            otherNumber2 = (TextView)v.findViewById(R.id.other_number_2);
            otherNumberText2 = (TextView)v.findViewById(R.id.other_number_text_2);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}
