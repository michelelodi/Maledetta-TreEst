package it.unimi.maledettatreest.model;

import android.util.Log;
import android.util.SparseArray;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import it.unimi.maledettatreest.MainActivity;

public class Line {

    public static final String LNAME = "lname";

    private static final String TAG = MainActivity.TAG_BASE + "Line";
    private static final String TERMINUS_1 = "terminus1";
    private static final String TERMINUS_2 = "terminus2";

    private ArrayList<Station> stations;
    private String name;
    private SparseArray<Direction> termini;

    public Line(JSONObject line){
        stations = null;

        try {
            name = line.getJSONObject(TERMINUS_1).getString(Station.SNAME) +
                    " - " + line.getJSONObject(TERMINUS_2).getString(Station.SNAME);

            Direction terminus1 = new Direction(name,
                                        line.getJSONObject(TERMINUS_1).getString(Direction.DID),
                                        line.getJSONObject(TERMINUS_1).getString(Station.SNAME),
                                        line.getJSONObject(TERMINUS_2).getString(Direction.DID),
                                        line.getJSONObject(TERMINUS_2).getString(Station.SNAME));

            Direction terminus2 = new Direction(name,
                                        line.getJSONObject(TERMINUS_2).getString(Direction.DID),
                                        line.getJSONObject(TERMINUS_2).getString(Station.SNAME),
                                        line.getJSONObject(TERMINUS_1).getString(Direction.DID),
                                        line.getJSONObject(TERMINUS_1).getString(Station.SNAME));

            termini = new SparseArray<>(2);
            termini.append(1,terminus1);
            termini.append(2,terminus2);
        } catch (JSONException e) { e.printStackTrace(); }
    }

    public ArrayList<Station> getStations() {
        return stations;
    }

    public String getName() {
        return name;
    }

    public Direction getTerminus(int terminus) {
        return (Direction) termini.get(terminus);
    }

    public void setStations(JSONArray stations){
        this.stations = new ArrayList<>();
        for(int i = 0; i < stations.length(); i++){
            try {
                this.stations.add(new Station(((JSONObject)stations.get(i)).getString(Station.SNAME),
                                                ((JSONObject)stations.get(i)).getString(Station.LAT),
                                                ((JSONObject)stations.get(i)).getString(Station.LON)));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG,e.toString());
            }
        }
    }
}
