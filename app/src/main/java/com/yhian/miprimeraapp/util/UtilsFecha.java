package com.yhian.miprimeraapp.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UtilsFecha {
    public static String formatearFecha(long timestamp) {
        Date fecha = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat(
                "EEEE, d 'de' MMMM 'de' yyyy HH:mm",
                new Locale("es", "ES")
        );
        return sdf.format(fecha);
    }
}
