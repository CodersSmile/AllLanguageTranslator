package com.allvoicetranslator.language.translator.utils;

import static com.allvoicetranslator.language.translator.ads.FirebaseADHandlers.MyApplication.getRequestQueue;
import static com.allvoicetranslator.language.translator.ads.FirebaseADHandlers.MyApplication.getTranslate;
import static com.allvoicetranslator.language.translator.utils.Const.languagesModelList;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.allvoicetranslator.language.translator.ads.FirebaseADHandlers.MyApplication;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translation;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.allvoicetranslator.language.translator.BuildConfig;
import com.allvoicetranslator.language.translator.models.DictionaryModel;
import com.allvoicetranslator.language.translator.models.LanguagesModel;

public class Utility {

    private static String encode = "";

    public static void setFullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController insetsController = activity.getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars());
            }
        } else {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    public static void addLanguages(LanguagesModel model){
        languagesModelList.add(model);
        HashSet<LanguagesModel> hashSet = new HashSet<>(languagesModelList);
        languagesModelList.clear();
        languagesModelList.addAll(hashSet);
    }

    public static LiveData<String> getSingleTranslate(LanguagesModel sourceModel, LanguagesModel targetModel, String value) {
        MutableLiveData<String> data = new MutableLiveData<>();
        try {
            encode = URLEncoder.encode(value, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        AsyncTask.execute(() -> {
            Translation translation = getTranslate().translate(value, Translate.TranslateOption.sourceLanguage(sourceModel.getLanguageCode()), Translate.TranslateOption.targetLanguage(targetModel.getLanguageCode()),
                    // Use "base" for standard edition, "nmt" for the premium model.
                    Translate.TranslateOption.model("base"));
            String text = translation.getTranslatedText();
            data.postValue(text);
        });
        return data;
    }

    public static LiveData<List<LanguagesModel>> getMultiTranslate(LanguagesModel sourceModel, String value) {
        List<LanguagesModel> list = new ArrayList<>();
        MutableLiveData<List<LanguagesModel>> data = new MutableLiveData<>();
        try {
            encode = URLEncoder.encode(value, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        AsyncTask.execute(() -> {
            for (LanguagesModel model : languagesModelList){
                Translation translation = getTranslate().translate(value,
                        Translate.TranslateOption.sourceLanguage(sourceModel.getLanguageCode()),
                        Translate.TranslateOption.targetLanguage(model.getLanguageCode()),
                        // Use "base" for standard edition, "nmt" for the premium model.
                        Translate.TranslateOption.model("base"));
                String text = translation.getTranslatedText();
                model.setTranslateResult(text);
                list.add(model);
            }
            data.postValue(list);
        });

        return data;
    }

    public static LiveData<DictionaryModel> getDefinition(String text){
        MutableLiveData<DictionaryModel> data = new MutableLiveData<>();
        String url = "https://api.dictionaryapi.dev/api/v2/entries/en/"+text;
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
                    try {
                        Log.e("run: ", response.get(0).toString());
//                        JSONObject object = (JSONObject) response.get(0);
                        DictionaryModel model = new Gson().fromJson(response.get(0).toString(), DictionaryModel.class);
                        Log.e( "run: ", model.getWord());
                        data.postValue(model);
                    } catch (Exception e){
                        e.printStackTrace();
                        data.postValue(null);
                    }
                }, error -> {
                    data.postValue(null);
                }){
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return super.getParams();
                    }
                };

                getRequestQueue().add(request);
            }
        });

        return data;
    }


    public static LiveData<List<LanguagesModel>> getLanguages(){
        MutableLiveData<List<LanguagesModel>> data = new MutableLiveData<>();
        data.setValue(MyApplication.getPreferences().getLanguages());

        return data;
    }

    public static void openKeyboardWithLanguage(Activity activity, String language) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(activity.getCurrentFocus(), InputMethodManager.SHOW_IMPLICIT);
            inputMethodManager.setAdditionalInputMethodSubtypes(inputMethodManager.getCurrentInputMethodSubtype().getMode(), new InputMethodSubtype[]{getSubtypeForLanguage(inputMethodManager, language)});
        }
    }

    private static InputMethodSubtype getSubtypeForLanguage(InputMethodManager inputMethodManager, String language) {
        InputMethodSubtype subtype = null;
        InputMethodInfo inputMethodInfo = inputMethodManager.getInputMethodList().get(0); // Assuming only one input method is enabled
        for (InputMethodSubtype ims : inputMethodManager.getEnabledInputMethodSubtypeList(inputMethodInfo, true)) {
            String locale = ims.getLocale();
            if (locale.startsWith(language)) {
                subtype = ims;
                break;
            }
        }
        return subtype;
    }

    public static void copyToClipboard(Context context, String text) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager != null) {
            ClipData clipData = ClipData.newPlainText(BuildConfig.APPLICATION_ID, text);
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
        }
    }

    public static String pasteFromClipboard(Context context) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager != null && clipboardManager.hasPrimaryClip()) {
            ClipData clipData = clipboardManager.getPrimaryClip();
            ClipData.Item item = clipData.getItemAt(0);
            CharSequence text = item.getText();
            if (text != null) {
                return text.toString();
            }
        }
        return null;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
