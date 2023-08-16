package com.allvoicetranslator.language.translator.utils;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

public class TTS implements TextToSpeech.OnInitListener{

    private TextToSpeech textToSpeech;
    private Context context;
    private String languageCode, message;

    public TTS(Context context, String languageCode) {
        this.context = context;
        this.languageCode = languageCode;
        textToSpeech = new TextToSpeech(context, this);
    }

    public void speakText(String message){
        this.message = message;
        Bundle bundle = new Bundle();
        bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.FLAG_PLAY_SOUND);
        bundle.putInt(TextToSpeech.Engine.KEY_PARAM_VOLUME, AudioManager.FLAG_PLAY_SOUND);
        if (textToSpeech != null && !textToSpeech.isSpeaking()) {
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, bundle, null);
            return;
        }
        if (textToSpeech != null && textToSpeech.isSpeaking()){
            textToSpeech.stop();
            speakText(message);
        }
    }

    public void stopTTS(){
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(new Locale(languageCode));
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(context, "Language not supported", Toast.LENGTH_SHORT).show();
            } else {
                speakText(message);
            }
        } else {
            Toast.makeText(context, "TTS Initialization failed", Toast.LENGTH_SHORT).show();
        }
    }
}
