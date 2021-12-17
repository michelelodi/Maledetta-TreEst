package it.unimi.maledettatreest.model;

import java.util.ArrayList;

public class LinesModel {
    private static LinesModel instance;
    private final ArrayList<Line> lines;

    private LinesModel(){
        lines = new ArrayList<>();
    }

    public static synchronized LinesModel getInstance(){
        if(instance == null) instance = new LinesModel();
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
}
