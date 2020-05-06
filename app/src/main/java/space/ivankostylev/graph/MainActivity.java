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
    private ArrayList<Entry> entries = new ArrayList<>();

    private GraphView graphView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button stopButton = findViewById(R.id.stopButton);
        graphView = findViewById(R.id.graph);
        setData();
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setData2();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setData2() {
        ArrayList<MomentTest> moments = CurrentTest.getTestsFromFiles(this, 0);
        for (int i = 31; i < moments.size(); i ++) {
            Log.d("MAIN ADDED:", moments.get(i).getVoltage() + " " + moments.get(i).getAmperage());
            graphView.addData(moments.get(i).getVoltage(), moments.get(i).getAmperage());
        }
    }

    private void setData() {
        graphView.setAxisName("time, sec", "current, A");
        ArrayList<MomentTest> moments = CurrentTest.getTestsFromFiles(this, 0);
        for (int i = 0; i < 30; i ++) {
            Log.d("MAIN ADDED:", moments.get(i).getVoltage() + " " + moments.get(i).getAmperage());
            graphView.addInitialData(moments.get(i).getVoltage(), moments.get(i).getAmperage());
        }
    }
}
