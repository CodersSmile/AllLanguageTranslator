package com.allvoicetranslator.language.translator.ads.Utils;

import static android.os.Build.VERSION.SDK_INT;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.allvoicetranslator.language.translator.BuildConfig;
import com.allvoicetranslator.language.translator.ads.FirebaseADHandlers.AdsJsonPOJO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Global {
    public static final String PLEASE_WAIT = "We are processing your request...";
    public static ArrayList<String> mSelectedImgPath = new ArrayList<>();
    public static long mLastClickTime = 0;
    static AlertDialog alertDialog;
    private static ProgressDialog dialog;
    private Context context;


    public Global(Context context) {
        this.context = context;
    }

    public static void hideProgressDialog() {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
                dialog = null;
            }

            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
                alertDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sout(String TagToString, Object whatToPrint) {
        if (BuildConfig.DEBUG) {
            System.out.println(TagToString + " " + whatToPrint);
        }
    }

    public static AdsJsonPOJO getAdsData(String json) {
        Type familyType = new TypeToken<AdsJsonPOJO>() {
        }.getType();
        return new Gson().fromJson(json, familyType);

    }

    public static void checkClick() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
    }

    public static boolean isLatestVersion() {
        return SDK_INT >= Build.VERSION_CODES.R;
    }

    public static Uri getContentMediaUri() {
        if (isLatestVersion()) {
            return MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
    }

    public static void printLog(String key, String val) {
        if (BuildConfig.DEBUG) Log.e(key + "*===", "===*" + val);
    }

    public static boolean isEmptyStr(String str) {
        if (str == null || str.trim().isEmpty() || str.equalsIgnoreCase("")) return true;
        return false;
    }

    public static String getRootFileForSave() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath();
        } else {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
    }

    public static Uri getUri(File file, Context context) {
        Uri contentUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            contentUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        } else {
            contentUri = Uri.fromFile(file);
        }
        return contentUri;
    }

    public static boolean isArrayListNull(ArrayList arrayList) {
        try {
            if (arrayList == null) {
                return true;
            } else if (arrayList != null && arrayList.size() <= 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
