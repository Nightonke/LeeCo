package com.nightonke.leetcoder.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nightonke.leetcoder.BuildConfig;
import com.nightonke.leetcoder.Model.APK;
import com.nightonke.leetcoder.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Weiping on 2016/2/28.
 */
public class AppUpdateManager {
    private static final String FILE_SEPARATOR = "/";
    private static final String FILE_PATH
            = Environment.getExternalStorageDirectory() + FILE_SEPARATOR
            + LeetCoderApplication.getAppContext().getResources().getString(R.string.app_name) + FILE_SEPARATOR;
    private static final String FILE_NAME
            = FILE_PATH
            + LeetCoderApplication.getAppContext().getResources().getString(R.string.app_name) + ".apk";

    private static final int UPDARE_TOKEN = 0x29;
    private static final int INSTALL_TOKEN = 0x31;

    private Context context;
    public static String spec = "";
    private int curProgress;
    private boolean isCancel;
    private String updateContent;

    public static boolean mustUpdate = false;

    private MaterialDialog progressDialog;

    public AppUpdateManager(Context context) {
        this.context = context;
    }

    private final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDARE_TOKEN:
                    progressDialog.setProgress(curProgress);
                    break;
                case INSTALL_TOKEN:
                    installApp();
                    break;
            }
        }
    };

    public boolean canBeUpdated = false;
    public void setCanBeUpdated(boolean canBeUpdated) {
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(LeetCoderApplication.getAppContext()).edit();
        editor.putBoolean("CAN_BE_UPDATED", canBeUpdated);
        editor.apply();
        this.canBeUpdated = canBeUpdated;
    }

    public boolean getCanBeUpdated() {
        this.canBeUpdated = PreferenceManager.
                getDefaultSharedPreferences(LeetCoderApplication.getAppContext())
                .getBoolean("CAN_BE_UPDATED", false);
        return this.canBeUpdated;
    }

    public boolean remindUpdate = true;
    public void setRemindUpdate(boolean remindUpdate) {
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(LeetCoderApplication.getAppContext()).edit();
        editor.putBoolean("REMIND_UPDATE", remindUpdate);
        editor.apply();
        this.remindUpdate = remindUpdate;
    }

    public boolean getRemindUpdate() {
        this.remindUpdate = PreferenceManager.
                getDefaultSharedPreferences(LeetCoderApplication.getAppContext())
                .getBoolean("REMIND_UPDATE", true);
        return this.remindUpdate;
    }

    /**
     * check update
     */
    public void checkUpdateInfo(final Boolean showInfo) {
        BmobQuery<APK> query = new BmobQuery<>();
        query.addWhereGreaterThan("version", LeetCoderApplication.VERSION);
        query.setLimit(Integer.MAX_VALUE);
        query.findObjects(LeetCoderApplication.getAppContext(), new FindListener<APK>() {
            @Override
            public void onSuccess(final List<APK> object) {
                if (object.size() == 0 && showInfo) {
                    if (BuildConfig.DEBUG) Log.d("LeetCoder", "This is newest version");
                    LeetCoderUtil.showToast(context, R.string.check_update_newest_get_toast);
                }
                BmobQuery<APK> tooOldQuery = new BmobQuery<>();
                tooOldQuery.addWhereEqualTo("version", LeetCoderApplication.VERSION);
                tooOldQuery.setLimit(1);
                tooOldQuery.findObjects(LeetCoderApplication.getAppContext(), new FindListener<APK>() {
                    @Override
                    public void onSuccess(List<APK> objectTooOld) {
                        if (objectTooOld.get(0).getTooOld()) {
                            if (BuildConfig.DEBUG) Log.d("LeetCoder", "Must update");
                            mustUpdate = true;
                        } else {
                            if (BuildConfig.DEBUG) Log.d("LeetCoder", "Needn't update");
                            mustUpdate = false;
                        }
                        if (object.size() > 0) {
                            if (BuildConfig.DEBUG) Log.d("LeetCoder", "There is newer version");
                            int max = -1;
                            int maxPosition = 0;
                            for (int i = 0; i < object.size(); i++) {
                                if (object.get(i).getVersion() > max) {
                                    max = object.get(i).getVersion();
                                    maxPosition = i;
                                }
                            }
                            spec = object.get(maxPosition).getFileUrl();
                            updateContent = object.get(maxPosition).getInfo();
                            setCanBeUpdated(true);
                            if (getRemindUpdate()) showNoticeDialog();
                        } else {
                            if (BuildConfig.DEBUG) Log.d("LeetCoder", "No newer version");
                            setCanBeUpdated(false);
                        }
                    }
                    @Override
                    public void onError(int code, String msg) {
                        mustUpdate = false;
                        if (object.size() > 0) {
                            int max = -1;
                            int maxPosition = 0;
                            for (int i = 0; i < object.size(); i++) {
                                if (object.get(i).getVersion() > max) {
                                    max = object.get(i).getVersion();
                                    maxPosition = i;
                                }
                            }
                            spec = object.get(maxPosition).getFileUrl();
                            updateContent = object.get(maxPosition).getInfo();
                            setCanBeUpdated(true);
                            if (getRemindUpdate()) showNoticeDialog();
                        } else {
                            setCanBeUpdated(false);
                        }
                    }
                });
            }
            @Override
            public void onError(int code, String msg) {
            }
        });
    }

    private void showNoticeDialog() {
        if (mustUpdate) {
            new MaterialDialog.Builder(context)
                    .title(R.string.check_update_title)
                    .content(getContent())
                    .positiveText(R.string.check_update_update)
                    .cancelable(false)
                    .onAny(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                            if (dialogAction == DialogAction.POSITIVE) {
                                showDownloadDialog();
                                materialDialog.dismiss();
                            }
                        }
                    })
                    .show();
        } else {
            new MaterialDialog.Builder(context)
                    .title(R.string.check_update_title)
                    .content(getContent())
                    .positiveText(R.string.check_update_update)
                    .negativeText(R.string.check_update_cancel)
                    .neutralText(R.string.check_update_dont_remind_this_version)
                    .onAny(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                            if (dialogAction == DialogAction.POSITIVE) {
                                showDownloadDialog();
                                materialDialog.dismiss();
                            } else if (dialogAction == DialogAction.NEGATIVE) {

                            } else {
                                setRemindUpdate(false);
                            }
                        }
                    })
                    .show();
        }
    }

    private String getContent() {
        if (updateContent == null) updateContent = "";
        updateContent = updateContent.replaceAll("\\$\\$\\$", "\n");
        return context.getResources().getString(R.string.check_update_content) + "\n" + updateContent;
    }

    private void showDownloadDialog() {

        if (mustUpdate) {
            progressDialog = new MaterialDialog.Builder(context)
                    .title(R.string.check_update_downloading_title)
                    .content(R.string.check_update_downloading_content)
                    .progress(false, 100, true)
                    .cancelable(false)
                    .show();
        } else {
            progressDialog = new MaterialDialog.Builder(context)
                    .title(R.string.check_update_downloading_title)
                    .content(R.string.check_update_downloading_content)
                    .progress(false, 100, true)
                    .negativeText("取消")
                    .onAny(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                            if (dialogAction == DialogAction.NEGATIVE) {
                                materialDialog.dismiss();
                                isCancel = false;
                            }
                        }
                    })
                    .show();
        }

        downloadApp();
    }

    private void downloadApp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                InputStream in = null;
                FileOutputStream out = null;
                HttpURLConnection conn = null;
                try {
                    url = new URL(spec);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    long fileLength = conn.getContentLength();
                    in = conn.getInputStream();
                    File filePath = new File(FILE_PATH);
                    if(!filePath.exists()) {
                        filePath.mkdir();
                    }
                    out = new FileOutputStream(new File(FILE_NAME));
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    long readedLength = 0l;
                    while((len = in.read(buffer)) != -1) {
                        // 用户点击“取消”按钮，下载中断
                        if(isCancel) {
                            break;
                        }
                        out.write(buffer, 0, len);
                        readedLength += len;
                        curProgress = (int) (((float) readedLength / fileLength) * 100);
                        handler.sendEmptyMessage(UPDARE_TOKEN);
                        if(readedLength >= fileLength) {
                            progressDialog.dismiss();
                            // 下载完毕，通知安装
                            handler.sendEmptyMessage(INSTALL_TOKEN);
                            break;
                        }
                    }
                    out.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if(out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(conn != null) {
                        conn.disconnect();
                    }
                }
            }
        }).start();
    }

    private void installApp() {
        File appFile = new File(FILE_NAME);
        if(!appFile.exists()) {
            return;
        }
        // 跳转到新版本应用安装页面
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + appFile.toString()), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
