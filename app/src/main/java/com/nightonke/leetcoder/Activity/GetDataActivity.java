package com.nightonke.leetcoder.Activity;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nightonke.leetcoder.Utils.LeetCoderApplication;
import com.nightonke.leetcoder.Model.Problem;
import com.nightonke.leetcoder.Model.Problem_Index;
import com.nightonke.leetcoder.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.listener.SaveListener;

public class GetDataActivity extends AppCompatActivity {

    private Context mContext;

    private Button setting;
    private Button button;
    private Button eachProblem;
    private Button upload;
    private WebView webView;
    private WebView webView1;
    private int urlCounter = 0;

    private Random random = new Random();

    private List<BmobObject> problemIndices = new ArrayList<>();
    private List<BmobObject> problems = new ArrayList<>();
    private List<String> problemLinks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_data);

        mContext = this;

        setting = (Button)findViewById(R.id.set);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(mContext)
                        .title("Start Position")
                        .inputType(InputType.TYPE_CLASS_NUMBER)
                        .cancelable(false)
                        .positiveText("OK")
                        .negativeText("CANCEL")
                        .input("", "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                dialog.dismiss();
                                startPosition = Integer.valueOf(String.valueOf(input));
                                new MaterialDialog.Builder(mContext)
                                        .title("End Position")
                                        .inputType(InputType.TYPE_CLASS_NUMBER)
                                        .cancelable(false)
                                        .positiveText("OK")
                                        .negativeText("CANCEL")
                                        .input("", "", new MaterialDialog.InputCallback() {
                                            @Override
                                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                                maxCount = Integer.valueOf(String.valueOf(input));
                                            }
                                        }).show();
                            }
                        }).show();
            }
        });
        button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl("https://oj.leetcode.com/problemset/algorithms/");
            }
        });
        eachProblem = (Button)findViewById(R.id.problem);
        eachProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("LeetCoder", "Load " + problemLinks.get(urlCounter));
                webView1.loadUrl(problemLinks.get(urlCounter++));
            }
        });
        upload = (Button)findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
            }
        });
        webView = (WebView)findViewById(R.id.webview);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.addJavascriptInterface(new JavaScriptInterface(), "HTMLOUT");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webView.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
            }
        });
        webView1 = (WebView)findViewById(R.id.webview1);
        webView1.getSettings().setLoadWithOverviewMode(true);
        webView1.getSettings().setUseWideViewPort(true);
        webView1.getSettings().setJavaScriptEnabled(true);
        webView1.getSettings().setDomStorageEnabled(true);
        webView1.addJavascriptInterface(new JavaScriptInterface2(), "HTMLOUT");
        webView1.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webView1.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
            }
        });
    }

    private int count = 0;
    class JavaScriptInterface {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processHTML(String html) {
            getData(html);
        }
    }

    class JavaScriptInterface2 {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processHTML(String html) {
            getProblem(html);
            Message msg = new Message();
            msg.what = 2;
            handler.sendMessage(msg);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                count++;
                button.setText(count + "/" + (maxCount - startPosition));
            }
            if (msg.what == 2) {
                eachProblem.setText(problems.size() + "/" + (maxCount - startPosition));
            }
            if (msg.what == 3) {
                if (urlCounter >= problemLinks.size()) return;
                else {
                    Log.d("LeetCoder", "Load " + problemLinks.get(urlCounter));
                    webView1.loadUrl(problemLinks.get(urlCounter++));
                }
            }
            if (msg.what == 4) {
                upload.setText((uploadCount / 2) + "/" + maxCount);
            }
            if (msg.what == 5) {
                upload.setText("FINISH");
                if (upload.getText().toString().equals("FINISH")) upload.setText("FINISH-ALL");
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
    private final String LEVEL_START_STRING = "</td>\n" +
            "                  \n" +
            "                  <td value=\"";
    private final String LEVEL_END_STRING = "</td>\n" +
            "                  \n" +
            "                </tr>";

    private int startPosition = 10;
    private int maxCount = 20;
    private void getData(String html) {
        Log.d("LeetCoder", "get Data");
        int position = 0;
        int count = -1;
        while (position != -1) {
            Problem_Index problemIndex = new Problem_Index();
            position = html.indexOf(ID_START_STRING, position);

            if(position != -1){
                count++;

                if (count < startPosition) {
                    position++;
                    continue;
                }

                if (count >= maxCount) break;

                int endPosition = html.indexOf(ID_END_STRING, position);
                problemIndex.setId(Integer.valueOf(html.substring(position + ID_START_STRING.length(), endPosition)));

                position = html.indexOf(PROBLEM_LINK_START_STRING, position);
                endPosition = html.indexOf(PROBLEM_LINK_END_STRING, position);
                problemLinks.add("https://leetcode.com" + html.substring(position + PROBLEM_LINK_START_STRING.length(), endPosition));

                position += PROBLEM_LINK_START_STRING.length() + html.substring(position + PROBLEM_LINK_START_STRING.length(), endPosition).length() + 2;
                endPosition = html.indexOf(TITLE_END_STRING, position);
                problemIndex.setTitle(html.substring(position, endPosition));

                position = html.indexOf(LEVEL_START_STRING, position);
                position = html.indexOf("\">", position) + 2;
                endPosition = html.indexOf(LEVEL_END_STRING, position);
                problemIndex.setLevel(html.substring(position, endPosition));

                problemIndices.add(problemIndex);

                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        }


    }

    private final String QUESTION_ID_START_STRING = "<h3 style=\"display:inline-block;margin-top:0px;\">";
    private final String QUESTION_ID_END_STRING = ". ";
    private final String CONTENT_START_STRING = "<div class=\"row\">\n" +
            "          <div class=\"col-md-12\">\n" +
            "            <div class=\"question-content\">";
    private final String CONTENT_END_STRING = "</p>\n" +
            "              \n" +
            "                ";
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
    private final String DISCUSS_START_STRING = "<a class=\"btn btn-success btn-pad right-pad\" href=\"";
    private final String DISCUSS_END_STRING = "\">Discuss</a>\n" +
            "       ";
    private final String SUMMARY_START_STRING = "<meta name=\"description\" content=\"";

    private void getProblem(String html) {
        Log.d("LeetCoder", html);
        int position = 0;

        position = html.indexOf(QUESTION_ID_START_STRING, position);
        int endPosition = html.indexOf(QUESTION_ID_END_STRING, position);
        int id = Integer.valueOf(html.substring(position + QUESTION_ID_START_STRING.length(), endPosition));

        Problem problem = new Problem();
        problem.setId(id);

        position = html.indexOf(CONTENT_START_STRING, position);
        endPosition = html.indexOf(CONTENT_END_STRING, position);
        problem.setContent(html.substring(position, endPosition + CONTENT_END_STRING.length()) + "</div></div></div>");
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
        for (BmobObject b : problemIndices) {
            Problem_Index problemIndex = (Problem_Index) b;
            if (problemIndex.getId() == id) {
                problemIndex.setTags(tags);
                int c = html.indexOf(SUMMARY_START_STRING);
                String summary = html.substring(c + SUMMARY_START_STRING.length(), c + SUMMARY_START_STRING.length() + 50);
                if (summary.charAt(0) == '\n') summary = summary.substring(1);
                summary += "...";
                problemIndex.setSummary(summary);

                int like = 400 - id + random.nextInt(50) - 25;
                if ("Hard".equals(problemIndex.getLevel())) {
                    like += random.nextInt(30);
                } else if ("Medium".equals(problemIndex.getLevel())) {
                    like += random.nextInt(20);
                } else if ("Easy".equals(problemIndex.getLevel())) {
                    like += random.nextInt(10);
                }

                int accident = random.nextInt(100);
                if (accident < 5) {
                    problemIndex.setLike(random.nextInt(50) + 50);
                } else {
                    problemIndex.setLike(like);
                }

                break;
            }
        }

        int similarPosition = html.indexOf(SIMILAR_START_STRING, position);
        if (similarPosition != -1) {
            position = similarPosition;
            endPosition = html.indexOf(SIMILAR_END_STRING, position);
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
        }

        position = html.indexOf(DISCUSS_START_STRING, position);
        endPosition = html.indexOf(DISCUSS_END_STRING, position);
        problem.setDiscussLink("https://leetcode.com" + html.substring(position + DISCUSS_START_STRING.length(), endPosition));

        for (int i = 0; i < problemLinks.size(); i++) {
            if (((Problem_Index)problemIndices.get(i)).getId() == id) {
                problem.setProblemLink(problemLinks.get(i));
                break;
            }
        }

//        problem.show();
        problems.add(problem);

        Message msg = new Message();
        msg.what = 3;
        handler.sendMessage(msg);
    }

    private int uploadCount = 0;
    private void uploadData() {
        Collections.sort(problemIndices, new Comparator<BmobObject>() {
            @Override
            public int compare(BmobObject lhs, BmobObject rhs) {
                if (((Problem_Index)lhs).getId() < ((Problem_Index)rhs).getId()) return -1;
                else if (((Problem_Index)lhs).getId() > ((Problem_Index)rhs).getId()) return 1;
                else return 0;
            }
        });

        Collections.sort(problems, new Comparator<BmobObject>() {
            @Override
            public int compare(BmobObject lhs, BmobObject rhs) {
                if (((Problem)lhs).getId() < ((Problem)rhs).getId()) return -1;
                else if (((Problem)lhs).getId() > ((Problem)rhs).getId()) return 1;
                else return 0;
            }
        });

        new BmobObject().insertBatch(LeetCoderApplication.getAppContext(), problemIndices, new SaveListener() {
            @Override
            public void onSuccess() {
                Message msg = new Message();
                msg.what = 5;
                handler.sendMessage(msg);
                Log.d("LeetCoder", "Save S: " + msg);
            }
            @Override
            public void onFailure(int code, String msg) {
                Log.d("LeetCoder", "Save F: " + msg);
            }
        });

        new BmobObject().insertBatch(LeetCoderApplication.getAppContext(), problems, new SaveListener() {
            @Override
            public void onSuccess() {
                Message msg = new Message();
                msg.what = 5;
                handler.sendMessage(msg);
                Log.d("LeetCoder", "Save S: " + msg);
            }
            @Override
            public void onFailure(int code, String msg) {
                Log.d("LeetCoder", "Save F: " + msg);
            }
        });
    }

}
