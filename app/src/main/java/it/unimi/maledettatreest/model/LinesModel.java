package it.unimi.maledettatreest.model;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

import it.unimi.maledettatreest.MainActivity;

public class LinesModel {

    private static LinesModel instance;

    private final ArrayList<Line> lines;
    private Direction selectedDir;

    private LinesModel(Context context){
        lines = new ArrayList<>();

        SharedPreferences prefs = context.getSharedPreferences(MainActivity.APP_PREFS,0);
        if(prefs.contains(Direction.DID))
            selectedDir = new Direction(prefs.getString(Line.LNAME,MainActivity.DOESNT_EXIST),
                                        prefs.getString(Direction.DID,MainActivity.DOESNT_EXIST),
                                        prefs.getString(Direction.SNAME,MainActivity.DOESNT_EXIST),
                                        prefs.getString(Direction.REVERSE_DID,MainActivity.DOESNT_EXIST),
                                        prefs.getString(Direction.REVERSE_SNAME,MainActivity.DOESNT_EXIST));
        else
            selectedDir = null;
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
    }
}
