package space.ivankostylev.graph;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
        Button stopButton = findViewById(R.id.stopButton);
        graphView = findViewById(R.id.graph);
        Log.d("GRAPH VIEW", "found");
        setData();

        graphView.drawGraph();
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setData2();
                graphView.drawGraph();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setData2() {
        ArrayList<MomentTest> moments = CurrentTest.getTestsFromFiles(this, 0);
        ArrayList<Moment> moments1 = new ArrayList<Moment>();
        for (int i = 31; i < moments.size(); i ++) {
            Log.d("MAIN ADDED:", moments.get(i).getVoltage() + " " + moments.get(i).getAmperage());
            moments1.add(new Moment(moments.get(i).getVoltage(), moments.get(i).getAmperage()));
            graphView.addGraphData(new GraphData(moments1, getResources().getColor(R.color.grid2), " "));
        }
    }

    private void setData() {
        graphView.setAxisName("time, sec", "current, A");
        ArrayList<MomentTest> moments = CurrentTest.getTestsFromFiles(this, 0);
        ArrayList<Moment> moments1 = new ArrayList<Moment>();
        for (int i = 0; i < 30; i ++) {
            Log.d("MAIN ADDED:", moments.get(i).getVoltage() + " " + moments.get(i).getAmperage());
            moments1.add(new Moment(moments.get(i).getVoltage(), moments.get(i).getAmperage()));
        }
        graphView.addGraphData(new GraphData(moments1, getResources().getColor(R.color.graph), " "));
    }
}
