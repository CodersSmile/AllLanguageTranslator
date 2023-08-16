package com.allvoicetranslator.language.translator.models;

import java.io.Serializable;

public class DataModel implements Serializable {

    private String name, value;
    private int icon;
    private boolean isPremium;

    public DataModel(String name, int icon, String value, boolean isPremium) {
        this.name = name;
        this.value = value;
        this.icon = icon;
        this.isPremium = isPremium;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
