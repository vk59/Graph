package space.ivankostylev.graph.test.simulation;

import android.content.Context;
import android.os.Handler;

import java.util.ArrayList;

import space.ivankostylev.graph.test.CurrentTest;
import space.ivankostylev.graph.test.MomentTest;

public class TestSimulation {
    private int PERIOD = 10;
    private TestSimulationCallback callback;
    private ArrayList<MomentTest> testSimulationResult = new ArrayList<>();
    private int indexCurrentTest = 0;
    private Handler handler = new Handler();

    public void startSimulation(Context context, TestSimulationCallback callback, int testIndex) {
        if (testIndex == 6 || testIndex == 7) {
            PERIOD = 214;
        }
        else {
            PERIOD = 10;
        }
        testSimulationResult = CurrentTest.getTestsFromFiles(context, testIndex);
        this.callback = callback;
        handler.postDelayed(timeUpdaterRunnable, PERIOD);
    }

    public void stopSimulation() {
        callback = null;
        indexCurrentTest = 0;
        testSimulationResult.clear();
        handler.removeCallbacks(timeUpdaterRunnable);
    }

    private Runnable timeUpdaterRunnable = new Runnable() {
        public void run() {
            callback.onGetSimulationData(testSimulationResult.get(indexCurrentTest));
            if (indexCurrentTest < testSimulationResult.size() - 1) {
                indexCurrentTest++;
                handler.postDelayed(this, PERIOD);
            }
            else {
                stopSimulation();
            }
        }
    };
}
