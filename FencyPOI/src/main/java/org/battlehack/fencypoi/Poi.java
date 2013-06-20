package org.battlehack.fencypoi;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.net.URI;

/**
 * Created by ligi on 6/11/13.
 */
public class Poi {

    private int lat;
    private int lon;
    private int id;
    private String name;
    private String description;

    private Uri uri;

    public Poi() {

    }
    public Poi(int lat, int lon, String name, String description) {
        this.lat = lat;
        this.lon = lon;
        this.name = name;
        this.description = description;
    }


    public int getID() {
        return id;
    }

    public int getLat() {

        return lat;
    }

    public double getLatDbl() {
        return (double) getLat() / 1E6;
    }


    public double getLonDbl() {
        return (double) getLon() / 1E6;
    }

    public int getLon() {
        return lon;
    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name=name;
    }

    public void setDescription(String description) {
        this.description=description;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
