package com.nightonke.leetcoder;

import java.util.ArrayList;

/**
 * Created by Weiping on 2016/1/8.
 */

public class Problem {

    private int id;
    private String title;
    private String level;
    private String content;
    private ArrayList<String> tags;
    private ArrayList<String> similarProblems;
    private String problemLink;
    private String discussLink;
    private String mySolution;
    private int star;

    public void createTestProblem() {
        id = 37;
        title = "Sudoku Solver";
        level = "Hard";
        content = "<div class=\"row\">\n" +
                "          <div class=\"col-md-12\">\n" +
                "            <div class=\"question-content\">\n" +
                "              <p><p>Write a program to solve a Sudoku puzzle by filling the empty cells.</p>\n" +
                "\n" +
                "<p>Empty cells are indicated by the character <code>'.'</code>.</p>\n" +
                "\n" +
                "<p>You may assume that there will be only one unique solution.</p>\n" +
                "<p>\n" +
                "<img src=\"http://upload.wikimedia.org/wikipedia/commons/thumb/f/ff/Sudoku-by-L2G-20050714.svg/250px-Sudoku-by-L2G-20050714.svg.png\" /><br />\n" +
                "<p style=\"font-size: 11px\">A sudoku puzzle...</p>\n" +
                "</p>\n" +
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
        tags = new ArrayList<>();
        tags.add("Backtracking");
        tags.add("Hash Table");
        similarProblems = new ArrayList<>();
        similarProblems.add("(E) Valid Sudoku");
        problemLink = "https://leetcode.com/problems/sudoku-solver/";
        discussLink = "https://leetcode.com/discuss/questions/oj/sudoku-solver";
        mySolution = "struct pos_poss {\n" +
                "    int ii;\n" +
                "    int jj;\n" +
                "    int poss;\n" +
                "    pos_poss() {}\n" +
                "    pos_poss(int ni, int nj, int np) {\n" +
                "        ii = ni;\n" +
                "        jj = nj;\n" +
                "        poss = np;\n" +
                "    }\n" +
                "};\n" +
                "\n" +
                "bool bmp(pos_poss a, pos_poss b) {\n" +
                "    return a.poss < b.poss;\n" +
                "}\n" +
                "\n" +
                "class Solution {\n" +
                "  public:\n" +
                "    Solution() {}\n" +
                "    void solveSudoku(vector<vector<char> > &board) {\n" +
                "        for (int i = 0; i < board.size(); i++) {\n" +
                "            for (int j = 0; j < board[i].size(); j++) {\n" +
                "                s[i][j] = board[i][j] - '0';\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        h = board.size();\n" +
                "        w = board[0].size();\n" +
                "        numP = 0;\n" +
                "        finishPos = 0;\n" +
                "        finish = false;\n" +
                "        prun();\n" +
                "        DFS(0);\n" +
                "\n" +
                "        for (int i = 0; i < h; i++) {  // write in\n" +
                "            for (int j = 0; j < w; j++) {\n" +
                "                board[i][j] = s[i][j] + '0';\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "    void prun() {\n" +
                "        for (int i = 0; i < h; i++) {\n" +
                "            for (int j = 0; j < w; j++) {\n" +
                "                if (s[i][j] == -2) {\n" +
                "                    bool isExist[10];\n" +
                "                    for (int k = 1; k <= 9; isExist[k++] = false);\n" +
                "                    for (int k = 0; k < h; k++) {  // from up to down\n" +
                "                        if (s[k][j] != -2) {\n" +
                "                            isExist[s[k][j]] = true;\n" +
                "                        }\n" +
                "                    }\n" +
                "                    for (int k = 0; k < w; k++) {  // from left to right\n" +
                "                        if (s[i][k] != -2) {\n" +
                "                            isExist[s[i][k]] = true;\n" +
                "                        }\n" +
                "                    }\n" +
                "                    short blockHMax = (i / 3 + 1) * 3;  // in a block\n" +
                "                    short blockHMin = i / 3 * 3;\n" +
                "                    short blockWMax = (j / 3 + 1) * 3;\n" +
                "                    short blockWMin = j / 3 * 3;\n" +
                "                    for (int k1 = blockHMin; k1 < blockHMax; k1++) {\n" +
                "                        for (int k2 = blockWMin; k2 < blockWMax; k2++) {\n" +
                "                            if (s[k1][k2] != -2) {\n" +
                "                                isExist[s[k1][k2]] = true;\n" +
                "                            }\n" +
                "                        }\n" +
                "                    }\n" +
                "                    int possibility = 9;\n" +
                "                    for (int k = 1; k <= 9; k++) {\n" +
                "                        if (isExist[k]) {\n" +
                "                            possibility -= 1;\n" +
                "                        }\n" +
                "                    }\n" +
                "                    pp[numP++] = pos_poss(i, j, possibility);\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "        sort(pp, pp + numP, bmp);  // from small possibility to big\n" +
                "    }\n" +
                "    void DFS(int nowPos) {\n" +
                "        if (nowPos == numP) {\n" +
                "            finish = true;\n" +
                "            return;\n" +
                "        }\n" +
                "        bool isExist[10];\n" +
                "        for (int k = 1; k <= 9; isExist[k++] = false);\n" +
                "        for (int k = 0; k < h; k++) {  // from up to down\n" +
                "            if (s[k][pp[nowPos].jj] != -2) {\n" +
                "                isExist[s[k][pp[nowPos].jj]] = true;\n" +
                "            }\n" +
                "        }\n" +
                "        for (int k = 0; k < w; k++) {  // from left to right\n" +
                "            if (s[pp[nowPos].ii][k] != -2) {\n" +
                "                isExist[s[pp[nowPos].ii][k]] = true;\n" +
                "            }\n" +
                "        }\n" +
                "        short blockHMax = (pp[nowPos].ii / 3 + 1) * 3;  // in a block\n" +
                "        short blockHMin = pp[nowPos].ii / 3 * 3;\n" +
                "        short blockWMax = (pp[nowPos].jj / 3 + 1) * 3;\n" +
                "        short blockWMin = pp[nowPos].jj / 3 * 3;\n" +
                "        for (int k1 = blockHMin; k1 < blockHMax; k1++) {\n" +
                "            for (int k2 = blockWMin; k2 < blockWMax; k2++) {\n" +
                "                if (s[k1][k2] != -2) {\n" +
                "                    isExist[s[k1][k2]] = true;\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "        for (int k = 1; k <= 9; k++) {  // check every one\n" +
                "            if (!isExist[k]) {\n" +
                "                s[pp[nowPos].ii][pp[nowPos].jj] = k;\n" +
                "                DFS(nowPos + 1);\n" +
                "                if (finish) {  // if not finish\n" +
                "                    return;\n" +
                "                }\n" +
                "                s[pp[nowPos].ii][pp[nowPos].jj] = -2;\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "  private:\n" +
                "    int w, h;\n" +
                "    int s[9][9];\n" +
                "    pos_poss pp[81];\n" +
                "    int numP;\n" +
                "    int block[9][9];\n" +
                "    int finishPos;\n" +
                "    bool finish;\n" +
                "};";
        star = 52;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDiscussLink() {
        return discussLink;
    }

    public void setDiscussLink(String discussLink) {
        this.discussLink = discussLink;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMySolution() {
        return mySolution;
    }

    public void setMySolution(String mySolution) {
        this.mySolution = mySolution;
    }

    public String getProblemLink() {
        return problemLink;
    }

    public void setProblemLink(String problemLink) {
        this.problemLink = problemLink;
    }

    public ArrayList<String> getSimilarProblems() {
        return similarProblems;
    }

    public void setSimilarProblems(ArrayList<String> similarProblems) {
        this.similarProblems = similarProblems;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
