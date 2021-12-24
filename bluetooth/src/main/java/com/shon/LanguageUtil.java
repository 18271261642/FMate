package com.shon;

import java.util.Locale;

public class LanguageUtil {
    public static String [] language={"中文","english","Deutsch（Deutschland","Italiano(Italia)","中文（繁體）","Español(Estados Unidos)","Português(Brasil)","日本語"
    ,"Français（France）","Русский","Türkçe","Українськa","한국어","  العربية","हिन्दी","Tiếng Việt","తెలుగు"
            ,"தமிழ்","اردو\u200E","Polski","فارسی\u200E = Fârsi","limba română","ภาษาไทย"
            ,"Nederlandse(Nederland)","slovenčina","Čeština","Magyar","slovenščina"
    };
    /**
     * 是否是中文
     *
     * @return
     */
    public static boolean isZH() {
        String language = Locale.getDefault().getLanguage();
        if (language == null) {
            return false;
        }
        return "zh".equalsIgnoreCase(language) ||
                "cn".equalsIgnoreCase(language) ||
                "hk".equalsIgnoreCase(language) ||
                "mo".equalsIgnoreCase(language) ||
                "tw".equalsIgnoreCase(language) ||
                language.toLowerCase().contains("zh")||
                language.toLowerCase().contains("cn")||
                language.toLowerCase().contains("hk")||
                language.toLowerCase().contains("mo")||
                language.toLowerCase().contains("tw");
    }
}
