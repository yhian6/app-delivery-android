package com.yhian.miprimeraapp;

import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;

public class helper {
    public static Context setLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration config = context.getResources().getConfiguration();
        config.setLocale(locale);

        return context.createConfigurationContext(config);
    }
}
