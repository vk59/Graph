package space.ivankostylev.graph.test;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class CurrentTest {
    public static ArrayList<MomentTest> results = new ArrayList<>();
    public static Type itemsArrType = new TypeToken<ArrayList<MomentTest>>() {}.getType();

    public static void appendMomentTest(String moment) {
        MomentTest momentTest = getMomentFromString(moment);
        results.add(momentTest);
    }

    public static MomentTest getMomentFromString(String moment) {
        String[] megaString = moment.split("\n");
        String[] data = megaString[0].split(",");
        float time = Float.parseFloat(data[0]);
        float vol = Float.parseFloat(data[1]);
        float amp = Float.parseFloat(data[2]);
        return new MomentTest(time, vol, amp);
    }

    public static String convertTestsToJson(ArrayList current) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String currentTestJson = gson.toJson(current);
        return currentTestJson;
    }


    public static ArrayList<MomentTest> convertJsonToTests(String json){
        ArrayList<MomentTest> current = new Gson().fromJson(json, itemsArrType);
        return current;
    }

    public static String convertJsonToString(String json) {
            ArrayList<MomentTest> test = convertJsonToTests(json);
            StringBuilder results = new StringBuilder(test.get(0).getTime() + ", "
                    + test.get(0).getVoltage() + ", " + test.get(0).getAmperage());

            for (int i = 1; test.size() > i; i++){
                results.append("\n").append(test.get(i).getTime()).append(", ").append(test.get(i)
                        .getVoltage()).append(", ").append(test.get(i).getAmperage());
        }
        return results.toString();
    }

    public static ArrayList<MomentTest> getTestsFromFiles(Context context, int fileNumber) {
        ArrayList<MomentTest> simulation = new ArrayList<>();
        String currentFile;

        switch (fileNumber) {
            case 0:
                currentFile = "sample_input/cyclic.txt";
                break;
            case 1:
                currentFile = "sample_input/linear_sweep.txt";
                break;
            case 2:
                currentFile = "sample_input/sinusoid.txt";
                break;
            case 6:
                currentFile = "sample_input/stripping1.txt";
                break;
            case 7:
                currentFile = "sample_input/stripping2.txt";
                break;
            default:
                currentFile = "sample_input/constant_voltage.txt";
                break;
        }

        try {
            InputStreamReader input = new InputStreamReader(context.getAssets().open(currentFile));
            BufferedReader reader = new BufferedReader(input);
            String line = reader.readLine();
            simulation.add(getMomentFromString(line));
            while (line != null){
                simulation.add(getMomentFromString(line));
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return simulation;
    }
}

