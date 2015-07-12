package es.zgzappstore.equipoa.handicapp;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Manuel on 12/07/2015.
 */
public class Parkings {

    public LatLng getGeometry() {
        return geometry;
    }

    public void setGeometry(LatLng geometry) {
        this.geometry = geometry;
    }

    String title;
    LatLng geometry;
}
