package com.allvoicetranslator.language.translator.utils;

import java.util.ArrayList;
import java.util.List;

import com.allvoicetranslator.language.translator.models.LanguagesModel;

public class Const {

    public static final String INPUT = "input";
    public static final String OUTPUT = "output";
    public static final String MULTI_OUTPUT = "multi_output";
    public static String TEXTEXTRACT = "";

    public static final LanguagesModel englishModel = new LanguagesModel("English", "en", false);
    public static final LanguagesModel hindiModel = new LanguagesModel("Hindi", "hi", false);

    public static List<LanguagesModel> languagesModelList = new ArrayList<>();
    public static LanguagesModel IN_SELECTED_LANGUAGE = null;
    public static LanguagesModel OUT_SELECTED_LANGUAGE = null;

    public static LanguagesModel SELECTED_LANGUAGE = null;

}
