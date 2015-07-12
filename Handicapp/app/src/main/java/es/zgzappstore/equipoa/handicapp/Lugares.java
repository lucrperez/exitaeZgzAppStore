package es.zgzappstore.equipoa.handicapp;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Manuel on 12/07/2015.
 */
public class Lugares {

    int id;
    String title, description, type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LatLng getGeometry() {
        return geometry;
    }

    public void setGeometry(LatLng geometry) {
        this.geometry = geometry;
    }

    LatLng geometry;
}
