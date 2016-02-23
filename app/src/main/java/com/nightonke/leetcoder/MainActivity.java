package com.nightonke.leetcoder;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Context mContext;

    private Button button;
    private WebView webView;

    private ArrayList<Problem_Index> problemIndices = new ArrayList<>();
    private ArrayList<Problem> problems = new ArrayList<>();
    private ArrayList<String> problemLinks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        button = (Button)findViewById(R.id.button);
        webView = (WebView)findViewById(R.id.webview);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JavaScriptInterface(), "HTMLOUT");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webView.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
            }
        });
    }

    private boolean isProblem = false;
    class JavaScriptInterface {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processHTML(String html) {
            if (isProblem) {

            } else {
                getData(html);
                isProblem = true;
            }

            Message msg = new Message();
            msg.what = 1;
            handler.sendMessage(msg);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                adapter = new ProblemDiscussFragmentAdapter(discusses, ProblemDiscussFragment.this);
                superRecyclerView.setAdapter(adapter);
                if (superRecyclerView != null) superRecyclerView.hideMoreProgress();
                end = false;
            }
        }
    };

    private final String ID_START_STRING = "</span>\n" +
            "                  </td>\n" +
            "                  <td>";
    private final String ID_END_STRING = "</td>\n" +
            "                    <td>";
    private final String PROBLEM_LINK_START_STRING = "</td>\n" +
            "                    <td>\n" +
            "                      <a href=\"";
    private final String PROBLEM_LINK_END_STRING = "\">";
    private final String TITLE_START_STRING = "";
    private final String TITLE_END_STRING = "</a>\n" +
            "                      ";
    private final String LEVEL_START_STRING = "                  \n" +
            "                  <td value='";
    private final String LEVEL_END_STRING = "</td>\n" +
            "                  \n" +
            "                </tr>";

    private void getData(String html) {
        int position = 0;
        int count = 0;
        while (position != -1) {
            Problem_Index problemIndex = new Problem_Index();
            position = html.indexOf(ID_START_STRING, position);

            if(position != -1){
                count++;

                int endPosition = html.indexOf(ID_END_STRING, position);
                problemIndex.setId(Integer.valueOf(html.substring(position + ID_START_STRING.length(), endPosition)));

                position = html.indexOf(PROBLEM_LINK_START_STRING, position);
                endPosition = html.indexOf(PROBLEM_LINK_END_STRING, position);
                problemLinks.add("https://leetcode.com" + html.substring(position + PROBLEM_LINK_START_STRING.length(), endPosition));

                position += PROBLEM_LINK_START_STRING.length() + html.substring(position + PROBLEM_LINK_START_STRING.length(), endPosition).length() + 2;
                endPosition = html.indexOf(TITLE_END_STRING, position);
                problemIndex.setTitle(html.substring(position, endPosition));

                position = html.indexOf(LEVEL_START_STRING, position);
                position = html.indexOf("'>", position) + 2;
                endPosition = html.indexOf(LEVEL_END_STRING, position);
                problemIndex.setLevel(html.substring(position, endPosition));

                webView.loadUrl(problemLinks.get(problemLinks.size() - 1));

                problemIndices.add(problemIndex);
            }
        }
    }

    private final String QUESTION_ID_START_STRING = "<input type=\"hidden\" name=\"question_id\" value=\"";
    private final String QUESTION_ID_END_STRING = "\" />";
    private final String CONTENT_START_STRING = "<div class=\"row\">\n"+
            "          <div class=\"col-md-12\">\n"+
            "            <div class=\"question-content\">";
    private final String CONTENT_END_STRING = "asked this question</p>\n" +
            "                </div>";
    private final String TAGS_START_STRING = "<div>\n" +
            "                  <div id=\"tags\" class=\"btn btn-xs btn-warning\">Show Tags</div>";
    private final String TAGS_END_STRING = "</a>\n" +
            "                    \n" +
            "                  </span>\n" +
            "                </div>\n" +
            "              \n" +
            "\n" +
            "              ";
    private final String SIMILAR_START_STRING = "<div>\n" +
            "                  <div id=\"similar\" class=\"btn btn-xs btn-warning\">Show Similar Problems</div>";
    private final String SIMILAR_END_STRING = "</a>\n" +
            "                    \n" +
            "                  </span>\n" +
            "                </div>\n" +
            "              \n" +
            "\n" +
            "            </div>";

    private void getProblem(String html) {
        int position = 0;

        position = html.indexOf(QUESTION_ID_START_STRING, position);
        int endPosition = html.indexOf(QUESTION_ID_END_STRING, position);
        int id = Integer.valueOf(html.substring(position + PROBLEM_LINK_START_STRING.length(), endPosition));

        Problem problem = new Problem();
        problem.setId(id);

        position = html.indexOf(CONTENT_START_STRING, position);
        endPosition = html.indexOf(CONTENT_END_STRING, position);
        problem.setContent(html.substring(position, endPosition) + "</div></div></div>");
        problem.setContent(problem.getContent().replaceAll("\\n", ""));

        position = html.indexOf(TAGS_START_STRING, position);
        endPosition = html.indexOf(TAGS_END_STRING, position);
        int tagPosition = position;
        ArrayList<String> tags = new ArrayList<>();
        while (tagPosition != -1 && tagPosition < endPosition) {
            tagPosition = html.indexOf("<a class=\"btn btn-xs btn-primary\" href=\"", tagPosition);
            if (tagPosition != -1 && tagPosition < endPosition) {
                int p = tagPosition + "<a class=\"btn btn-xs btn-primary\" href=\"".length();
                int q = html.indexOf("\">", p) + 2;
                tags.add(html.substring(q, html.indexOf("</a>", q)));
                tagPosition = q;
            }
        }
        for (Problem_Index problemIndex : problemIndices) {
            if (problemIndex.getId() == id) {
                problemIndex.setTags(tags);
                problemIndex.setSummary(problem.getContent().substring(problem.getContent().indexOf("<p><p>"), problem.getContent().indexOf("</p>")));
            }
        }

        position = html.indexOf(SIMILAR_START_STRING, position);
        endPosition = html.indexOf(SIMILAR_END_STRING, position);
        int similarPosition = position;
        ArrayList<String> similars = new ArrayList<>();
        while (similarPosition != -1 && similarPosition < endPosition) {
            similarPosition = html.indexOf("<a class=\"btn btn-xs btn-primary\" href=\"", similarPosition);
            if (similarPosition != -1 && similarPosition < endPosition) {
                int p = similarPosition + "<a class=\"btn btn-xs btn-primary\" href=\"".length();
                int q = html.indexOf("\">", p) + 2;
                similars.add(html.substring(q, html.indexOf("</a>", q)));
                similarPosition = q;
            }
        }
        problem.setSimilarProblems(similars);


        while (position != -1) {
            Problem_Index problemIndex = new Problem_Index();
            position = html.indexOf(ID_START_STRING, position) + "</div></div></div>";

            if(position != -1){
                count++;

                int endPosition = html.indexOf(ID_END_STRING, position);
                problemIndex.setId(Integer.valueOf(html.substring(position + ID_START_STRING.length(), endPosition)));

                position = html.indexOf(PROBLEM_LINK_START_STRING, position);
                endPosition = html.indexOf(PROBLEM_LINK_END_STRING, position);
                problemLinks.add("https://leetcode.com" + html.substring(position + PROBLEM_LINK_START_STRING.length(), endPosition));

                position += PROBLEM_LINK_START_STRING.length() + html.substring(position + PROBLEM_LINK_START_STRING.length(), endPosition).length() + 2;
                endPosition = html.indexOf(TITLE_END_STRING, position);
                problemIndex.setTitle(html.substring(position, endPosition));

                position = html.indexOf(LEVEL_START_STRING, position);
                position = html.indexOf("'>", position) + 2;
                endPosition = html.indexOf(LEVEL_END_STRING, position);
                problemIndex.setLevel(html.substring(position, endPosition));



                discusses.add(discuss);
            }
        }
    }



}
