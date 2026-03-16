package com.yhian.miprimeraapp.modelo;

import java.io.Serializable;

public class UbicacionData implements Serializable {
    public String userId;
    public double lat;
    public double lng;
    public long timestamp;

    public UbicacionData() {
    }

    public UbicacionData(String userId, double lat, double lng, long timestamp) {
        this.userId = userId;
        this.lat = lat;
        this.lng = lng;
        this.timestamp = timestamp;
    }
}
