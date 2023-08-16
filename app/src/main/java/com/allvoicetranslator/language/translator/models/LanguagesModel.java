package com.allvoicetranslator.language.translator.models;

import java.io.Serializable;

public class LanguagesModel implements Serializable {
    private String LanguageName;
    private String LanguageCode, translateResult;
    private boolean isSelected;

    public LanguagesModel(String languageName, String languageCode, boolean isSelected) {
        LanguageName = languageName;
        LanguageCode = languageCode;
        this.isSelected = isSelected;
    }

    public String getTranslateResult() {
        return translateResult;
    }

    public void setTranslateResult(String translateResult) {
        this.translateResult = translateResult;
    }

    public void setLanguageName(String languageName) {
        LanguageName = languageName;
    }

    public void setLanguageCode(String languageCode) {
        LanguageCode = languageCode;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getLanguageName() {
        return LanguageName;
    }

    public String getLanguageCode() {
        return LanguageCode;
    }
}
