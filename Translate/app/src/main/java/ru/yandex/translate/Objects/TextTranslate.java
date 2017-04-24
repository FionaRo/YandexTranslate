package ru.yandex.translate.Objects;

import java.io.Serializable;


public class TextTranslate implements Serializable {

    String original;
    String translate;
    String fromLang;
    String toLang;

    public TextTranslate() {
    }

    public TextTranslate(String original, String translate, String fromLang, String toLang) {
        this.original = original;
        this.translate = translate;
        this.fromLang = fromLang;
        this.toLang = toLang;
    }

    public TextTranslate(String original, String translate) {
        this.original = original;
        this.translate = translate;
        this.fromLang = "";
        this.toLang = "";
    }

    public void setPairTranslate(String orig, String tran) {
        original = orig;
        translate = tran;
    }

    public void setDirectionTranslate(String from, String to) {
        fromLang = from;
        toLang = to;
    }

    public String getOriginalText() {
        return original;
    }

    public String getTranslateText() {
        return translate;
    }

    public String getFromLang() {
        return fromLang;
    }

    public String getToLang() {
        return toLang;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) return false;
        if (object instanceof TextTranslate) {
            TextTranslate tr = (TextTranslate) object;
            if (original.equals(tr.getOriginalText())
                    && translate.equals(tr.getTranslateText()))
                return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return original.hashCode() + translate.hashCode();
    }

    @Override
    public String toString() {
        return original + " - " + translate;
    }
}
