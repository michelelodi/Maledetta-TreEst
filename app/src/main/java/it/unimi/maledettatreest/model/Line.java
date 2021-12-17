package it.unimi.maledettatreest.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

import it.unimi.maledettatreest.MainActivity;

public class Line {
    public static final String DID = "did";
    public static final String LNAME = "lname";
    public static final String REVERSE_DID = "reverseDid";
    public static final String REVERSE_SNAME = "reverseSname";
    public static final String SNAME = "sname";

    private static final String TAG = MainActivity.TAG_BASE + "Line";
    private static final String TERMINUS_1 = "terminus1";
    private static final String TERMINUS_2 = "terminus2";

    private ArrayList<Station> stations;
    private String name;
    private HashMap<String,HashMap<String,String>> termini;

    public Line(JSONObject line){
        Log.d(TAG,"Building Line from JSON");
        stations = null;
        try {
            name = line.getJSONObject(TERMINUS_1).getString(SNAME) + " - " + line.getJSONObject(TERMINUS_2).getString(SNAME);

            HashMap<String,String> terminus1 = new HashMap<>();
            terminus1.put(SNAME,line.getJSONObject(TERMINUS_1).getString(SNAME));
            terminus1.put(DID,line.getJSONObject(TERMINUS_1).getString(DID));
            terminus1.put(LNAME,name);
            terminus1.put(REVERSE_DID,line.getJSONObject(TERMINUS_2).getString(DID));
            terminus1.put(REVERSE_SNAME,line.getJSONObject(TERMINUS_2).getString(SNAME));
            HashMap<String,String> terminus2 = new HashMap<>();
            terminus2.put(SNAME,line.getJSONObject(TERMINUS_2).getString(SNAME));
            terminus2.put(DID,line.getJSONObject(TERMINUS_2).getString(DID));
            terminus2.put(LNAME,name);
            terminus2.put(REVERSE_DID,line.getJSONObject(TERMINUS_1).getString(DID));
            terminus2.put(REVERSE_SNAME,line.getJSONObject(TERMINUS_1).getString(SNAME));

            termini = new HashMap<>();
            termini.put(TERMINUS_1,terminus1);
            termini.put(TERMINUS_2,terminus2);
        } catch (JSONException e) { e.printStackTrace(); }
    }

    public ArrayList<Station> getStations() {
        return stations;
    }

    public String getName() {
        return name;
    }

    public HashMap<String, String> getTerminus(int terminus) {
        if(terminus == 1) return termini.get(TERMINUS_1);
        if(terminus == 2) return termini.get(TERMINUS_2);
        return null;
    }

    public void setStations(ArrayList<Station> stations) {
        this.stations = stations;
    }
}
