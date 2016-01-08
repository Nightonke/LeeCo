package com.nightonke.leetcoder;

import android.text.Editable;
import android.text.Html;
import android.util.Log;

import org.xml.sax.XMLReader;

/**
 * Created by Weiping on 2016/1/8.
 */
public class MyTagHandler implements Html.TagHandler {
    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        Log.d("LeetCoder", tag + " : " + output);
    }
}
