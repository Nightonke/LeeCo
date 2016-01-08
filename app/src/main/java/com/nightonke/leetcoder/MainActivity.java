package com.nightonke.leetcoder;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.sufficientlysecure.htmltextview.HtmlTextView;

public class MainActivity extends AppCompatActivity {

    private Context mContext;

    private LinearLayout content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        String contentString = "<div class=\"row\">\n" +
                "          <div class=\"col-md-12\">\n" +
                "            <div class=\"question-content\">\n" +
                "              <p><p>Write a program to solve a Sudoku puzzle by filling the empty cells.</p>\n" +
                "\n" +
                "<p>Empty cells are indicated by the character <code>'.'</code>.</p>\n" +
                "\n" +
                "<p>You may assume that there will be only one unique solution.</p>\n" +
                "\n" +
                "<p>\n" +
                "<img src=\"http://upload.wikimedia.org/wikipedia/commons/thumb/f/ff/Sudoku-by-L2G-20050714.svg/250px-Sudoku-by-L2G-20050714.svg.png\" /><br />\n" +
                "<p style=\"font-size: 11px\">A sudoku puzzle...</p>\n" +
                "</p>\n" +
                "\n" +
                "<p>\n" +
                "<img src=\"http://upload.wikimedia.org/wikipedia/commons/thumb/3/31/Sudoku-by-L2G-20050714_solution.svg/250px-Sudoku-by-L2G-20050714_solution.svg.png\" /><br />\n" +
                "<p style=\"font-size: 11px\">...and its solution numbers marked in red.\n" +
                "</p></p>\n" +
                "              \n" +
                "                <div>\n" +
                "                  <p><a href=\"/subscribe/\">Subscribe</a> to see which companies asked this question</p>\n" +
                "                </div>\n" +
                "              \n" +
                "\n" +
                "              \n" +
                "                <div>\n" +
                "                  <div id=\"tags\" class=\"btn btn-xs btn-warning\">Show Tags</div>\n" +
                "                  <span class=\"hidebutton\">\n" +
                "                    \n" +
                "                    <a class=\"btn btn-xs btn-primary\" href=\"/tag/backtracking/\">Backtracking</a>\n" +
                "                    \n" +
                "                    <a class=\"btn btn-xs btn-primary\" href=\"/tag/hash-table/\">Hash Table</a>\n" +
                "                    \n" +
                "                  </span>\n" +
                "                </div>\n" +
                "              \n" +
                "\n" +
                "              \n" +
                "                <div>\n" +
                "                  <div id=\"similar\" class=\"btn btn-xs btn-warning\">Show Similar Problems</div>\n" +
                "                  <span class=\"hidebutton\">\n" +
                "                    \n" +
                "                    <a class=\"btn btn-xs btn-primary\" href=\"/problems/valid-sudoku/\"> (E) Valid Sudoku</a>\n" +
                "                    \n" +
                "                  </span>\n" +
                "                </div>\n" +
                "              \n" +
                "\n" +
                "            </div>\n" +
                "          </div>\n" +
                "        </div>";

        TextView textView = (TextView)findViewById(R.id.textview);
        textView.setText(Html.fromHtml(contentString, new MyImageGetter(mContext, textView), new MyTagHandler()));

        ImageView imageView = (ImageView)findViewById(R.id.imageview);
        Picasso.with(mContext).load("https://upload.wikimedia.org/wikipedia/commons/thumb/f/ff/Sudoku-by-L2G-20050714.svg/250px-Sudoku-by-L2G-20050714.svg.png").into(imageView);

        content = (LinearLayout)findViewById(R.id.content);
        loadContent(contentString);
    }

    private void loadContent(String contentString) {

    }


}
