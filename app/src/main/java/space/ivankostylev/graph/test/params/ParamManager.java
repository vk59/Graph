package space.ivankostylev.graph.test.params;

import java.util.ArrayList;

public class ParamManager {
    public static final String NAME_CYCLIC = "cyclic";
    public static final String NAME_LINEAR_SWEEP = "linear sweep";
    public static final String NAME_SINUSOID = "sinusoid";
    public static final String NAME_CONSTANT_VOLTAGE = "constant voltage";
    public static final String NAME_CHRONOAMPEROMETRY = "chronoamperometry";
    public static final String NAME_SQUARE_WAVE = "square wave";

    private static ParamTest[] paramsStandard = {
            new ParamTest("current range", "uA", 100, 1, 1000),
            new ParamTest("sample range", "Hz", 100, 0.1f, 200),
            new ParamTest("quiet time", "s", 0, 0, 3600),
            new ParamTest("quiet value", "V", 0, -10, 10)
    };

    private static ParamTest[] paramsCyclic = {
            new ParamTest("min value", "V", -1, -10, 10),
            new ParamTest("max value", "V", 1, -10, 10),
            new ParamTest("scan rate", "V/s", 0.5f, 0, 50),
            new ParamTest("cycles", "", 1, 1, 10000)
    };

    private static ParamTest[] paramsLinearSweep = {
            new ParamTest("min value", "V", -1, -10, 10),
            new ParamTest("max value", "V", 1, -10, 10),
            new ParamTest("scan rate", "V/s", 0.5f, 0, 50)
    };

    private static ParamTest[] paramsSinusoid = {
            new ParamTest("amplitude", "V", 1, 0, 10),
            new ParamTest("offset", "V", 0, -10, 10),
            new ParamTest("period", "s", 10, 0, 172800),
            new ParamTest("shift", "", 1, 0, 1),
            new ParamTest("cycles", "", 1, 1, 10000)
    };

    private static ParamTest[] paramsConstantVoltage = {
            new ParamTest("value", "V", 0, -10, 10),
            new ParamTest("duration", "s", 10, 0, 172800)
    };

    private static ParamTest[] paramsChronoamperometry = {
            new ParamTest("step 1 duration", "s", 10, 0, 172800),
            new ParamTest("step 1 value", "V", 0, -10, 10),
            new ParamTest("step 2 duration", "s", 10, 0, 172800),
            new ParamTest("step 2 value", "V", 0, -10, 10)
    };

    private static ParamTest[] paramsSquareWave = {
            new ParamTest("amplitude", "V", 0, 0, 10),
            new ParamTest("start value", "V", -0.5f, -10, 10),
            new ParamTest("final value", "V", 0.5f, -10, 10),
            new ParamTest("step value", "V", -0.02f, 0.001f, 2),
            new ParamTest("sample window", "fraction of pulse", 0.2f, 0, 1)
    };


    public static ArrayList<ParamTest> getParamsOfTest(String testName) {
        ArrayList<ParamTest> paramArray = new ArrayList<>();
        for (ParamTest paramTest : paramsStandard) {
            paramArray.add(paramTest);
        }
        ParamTest[] currentParams;
        switch (testName) {
            case NAME_CYCLIC:
                currentParams = paramsCyclic;
                break;
            case NAME_LINEAR_SWEEP:
                currentParams = paramsLinearSweep;
                break;
            case NAME_CHRONOAMPEROMETRY:
                currentParams = paramsChronoamperometry;
                break;
            case NAME_CONSTANT_VOLTAGE:
                currentParams = paramsConstantVoltage;
                break;
            case NAME_SINUSOID:
                currentParams = paramsSinusoid;
                break;
            default:
                currentParams = paramsSquareWave;
                break;
        }
        for (ParamTest paramTest : currentParams) {
            paramArray.add(paramTest);
        }
        return paramArray;
    }
}
