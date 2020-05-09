package space.ivankostylev.graph;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.vk59.graphviewlibrary.GraphData;
import com.vk59.graphviewlibrary.GraphView;
import com.vk59.graphviewlibrary.Moment;

import java.util.ArrayList;

import space.ivankostylev.graph.test.CurrentTest;
import space.ivankostylev.graph.test.MomentTest;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Moment> entries = new ArrayList<>();

    private GraphView graphView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button stopButton = findViewById(R.id.stopButton);
        graphView = findViewById(R.id.graph);
        Log.d("GRAPH VIEW", "found");
        setData();
        graphView.drawGraph();
        setData2();
        graphView.drawGraph();
        setData3();
        graphView.drawGraph();
        setData4();
        graphView.drawGraph();

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setData2();
                graphView.drawGraph();
                final int index = 0;
                stopButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            graphView.removeGraphData(index);
                            graphView.drawGraph();
                        } catch (Exception e) { }
                    }
                });
            }
        });

        findViewById(R.id.draw_3_graph).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setData3();
                graphView.drawGraph();
            }
        });

        findViewById(R.id.graph4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setData4();
                graphView.drawGraph();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setData4() {
        graphView.setAxisName("time, sec", "current, A");
        ArrayList<MomentTest> moments = CurrentTest.getTestsFromFiles(this, 0);
        ArrayList<Moment> moments1 = new ArrayList<Moment>();
        for (int i = 200; i < moments.size(); i ++) {
            Log.d("MAIN ADDED:", moments.get(i).getVoltage() + " " + moments.get(i).getAmperage());
            moments1.add(new Moment(moments.get(i).getVoltage(), moments.get(i).getAmperage()));
        }
        graphView.addGraphData(new GraphData(moments1, Color.BLUE, "4rh graph"));
    }

    private void setData3() {
        graphView.setAxisName("time, sec", "current, A");
        ArrayList<MomentTest> moments = CurrentTest.getTestsFromFiles(this, 0);
        ArrayList<Moment> moments1 = new ArrayList<Moment>();
        for (int i = 100; i <= 200; i ++) {
            Log.d("MAIN ADDED:", moments.get(i).getVoltage() + " " + moments.get(i).getAmperage());
            moments1.add(new Moment(moments.get(i).getVoltage(), moments.get(i).getAmperage()));
        }
        graphView.addGraphData(new GraphData(moments1, Color.GREEN, "New graph"));
    }

    private void setData2() {
        ArrayList<MomentTest> moments = CurrentTest.getTestsFromFiles(this, 0);
        ArrayList<Moment> moments1 = new ArrayList<Moment>();
        for (int i = 30; i <= 100; i ++) {
            Log.d("MAIN ADDED:", moments.get(i).getVoltage() + " " + moments.get(i).getAmperage());
            moments1.add(new Moment(moments.get(i).getVoltage(), moments.get(i).getAmperage()));
        }
        graphView.addGraphData(new GraphData(moments1, getResources().getColor(R.color.graph2), "Something else"));
    }

    private void setData() {
        graphView.setAxisName("time, sec", "current, A");
        ArrayList<MomentTest> moments = CurrentTest.getTestsFromFiles(this, 0);
        ArrayList<Moment> moments1 = new ArrayList<Moment>();
        for (int i = 0; i <= 30; i ++) {
            Log.d("MAIN ADDED:", moments.get(i).getVoltage() + " " + moments.get(i).getAmperage());
            moments1.add(new Moment(moments.get(i).getVoltage(), moments.get(i).getAmperage()));
        }
        graphView.addGraphData(new GraphData(moments1, getResources().getColor(R.color.graph), "Graph 1"));
    }
}
