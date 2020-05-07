package space.ivankostylev.graph;

import java.util.ArrayList;

public class GraphData {
    private int color;
    private String label;
    private ArrayList<Moment> data;

    public GraphData(ArrayList<Moment> data, int color, String label) {
        this.data = data;
        this.color = color;
        this.label = label;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setData(ArrayList<Moment> data) {
        this.data = data;
    }

    public void addData(float x, float y) {
        this.data.add(new Moment(x, y));
    }

    public ArrayList<Moment> getData() {
        return data;
    }
}
