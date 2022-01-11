package it.unimi.maledettatreest.model;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

public class Line {

    public static final String LNAME = "lname";
    private static final String TERMINUS_1 = "terminus1";
    private static final String TERMINUS_2 = "terminus2";

    private ArrayList<Station> stations;
    private String name;
    private HashMap<String,Direction> termini;

    public Line(JSONObject line){
        stations = null;

        try {
            name = line.getJSONObject(TERMINUS_1).getString(Direction.SNAME) +
                    " - " + line.getJSONObject(TERMINUS_2).getString(Direction.SNAME);

            Direction terminus1 = new Direction(name, line.getJSONObject(TERMINUS_1)
                                                                    .getString(Direction.DID),
                                                        line.getJSONObject(TERMINUS_1)
                                                                .getString(Direction.SNAME),
                                                        line.getJSONObject(TERMINUS_2)
                                                                .getString(Direction.DID),
                                                        line.getJSONObject(TERMINUS_2)
                                                                .getString(Direction.SNAME));

            Direction terminus2 = new Direction(name, line.getJSONObject(TERMINUS_2)
                                                        .getString(Direction.DID),
                                                        line.getJSONObject(TERMINUS_2)
                                                                .getString(Direction.SNAME),
                                                        line.getJSONObject(TERMINUS_1)
                                                                .getString(Direction.DID),
                                                        line.getJSONObject(TERMINUS_1)
                                                                .getString(Direction.SNAME));

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

    public Direction getTerminus(int terminus) {
        if(terminus == 1) return termini.get(TERMINUS_1);
        if(terminus == 2) return termini.get(TERMINUS_2);
        return null;
    }

    public void setStations(ArrayList<Station> stations) {
        this.stations = stations;
    }
}
