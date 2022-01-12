package it.unimi.maledettatreest.model;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import it.unimi.maledettatreest.MainActivity;

public class LinesModel {

    private static LinesModel instance;

    private final ArrayList<Line> lines;
    private Direction selectedDir = null;
    private final SharedPreferences prefs;

    private LinesModel(Context context){
        lines = new ArrayList<>();

        prefs = context.getSharedPreferences(MainActivity.APP_PREFS,0);
        if(prefs.contains(Direction.DID))
            selectedDir = new Direction(prefs.getString(Line.LNAME,MainActivity.DOESNT_EXIST),
                                        prefs.getString(Direction.DID,MainActivity.DOESNT_EXIST),
                                        prefs.getString(Station.SNAME,MainActivity.DOESNT_EXIST),
                                        prefs.getString(Direction.REVERSE_DID,MainActivity.DOESNT_EXIST),
                                        prefs.getString(Direction.REVERSE_SNAME,MainActivity.DOESNT_EXIST));
    }

    public static synchronized LinesModel getInstance(Context context){
        if(instance == null) instance = new LinesModel(context);
        return  instance;
    }

    public Line get(int index){
        return lines.get(index);
    }

    public int getSize(){
        return lines.size();
    }

    public void addLine(Line line){
        lines.add(line);
    }

    public Direction getSelectedDir(){
        return selectedDir;
    }

    public void setSelectedDir(Direction selectedDir){
        this.selectedDir = selectedDir;
        prefs.edit().putString(Line.LNAME, selectedDir.getLname())
                .putString(Direction.DID, selectedDir.getDid())
                .putString(Station.SNAME, selectedDir.getSname())
                .putString(Direction.REVERSE_SNAME, selectedDir.getReverseSname())
                .putString(Direction.REVERSE_DID, selectedDir.getReverseDid()).apply();
    }

    public Line getLineByName(String lineName){
        for(Line l : lines)
            if(l.getName().equals(lineName)) return l;
        return null;
    }

    public void setLine(Line line){
        for(int i = 0; i < lines.size(); i++)
            if(lines.get(i).getName().equals(line.getName()))
                lines.set(i, line);
    }
}
