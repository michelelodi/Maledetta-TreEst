package it.unimi.maledettatreest.model;

public class Station {
    public final static String SNAME = "sname";
    public final static String LAT = "lat";
    public final static String LON = "lon";

    private String sname, lat, lon;

    public Station(String sname, String lat, String lon) {
        this.sname = sname;
        this.lat = lat;
        this.lon = lon;
    }

    public String getSname() {
        return sname;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }
}
