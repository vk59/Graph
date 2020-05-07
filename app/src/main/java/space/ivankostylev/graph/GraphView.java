package space.ivankostylev.graph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class GraphView extends SurfaceView implements SurfaceHolder.Callback{
    public DrawThread drawThread;

    private volatile boolean running = true; //флаг для остановки потока
    private volatile boolean isGraphToDraw = false;
    private volatile boolean isInitialized = false;
    private volatile int firstNewAddedIndex = 0;
    private volatile String xName = "";
    private volatile String yName = "";

    private volatile ArrayList<ArrayList<Moment>> entries = new ArrayList<>();
    private volatile ArrayList<Integer> colors = new ArrayList<>();
    private volatile ArrayList<String> labels = new ArrayList<>();
    private volatile int pointsAll = 0;

    private volatile float minX;
    private volatile float maxX;
    private volatile float minY;
    private volatile float maxY;

    private volatile int drewGraphs = 0;

    public GraphView(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    public GraphView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // создание SurfaceView
        Log.d("SURFACE", "created");
        drawThread = new DrawThread(getContext(), getHolder());
        setRunning(true);
        drawThread.start();
        isInitialized = true;
//        if (isGraphToDraw) {
//            drawGraph();
//        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // изменение размеров SurfaceView
    }

    public void addGraphData(GraphData data) {
        entries.add(data.getData());
        colors.add(data.getColor());
        labels.add(data.getLabel());
        if (!isInitialized) {
            minX = entries.get(0).get(0).getX();
            maxX = entries.get(0).get(0).getX();
            minY = entries.get(0).get(0).getY();
            maxY = entries.get(0).get(0).getY();
        }
    }

    public void removeGraphData(long index) {
        //
    }

    public long getQuantityOfGraphData() {
        return entries.size();
    }


//    public void addData(float x, float y) {
//        Log.d("ADDED DATA", x + " " + y);
//        entries.add(new Moment(x, y));
//        if (entries.size() == 1) {
//            minX = entries.get(0).getX();
//            maxX = entries.get(0).getX();
//            minY = entries.get(0).getY();
//            maxY = entries.get(0).getY();
//        }
//    }

    public void setAxisName(String xName, String yName) {
        this.xName = xName;
        this.yName = yName;
    }

    public void drawGraph() {
        isGraphToDraw = true;
        if (isInitialized) {
            Log.d("GRAPH VIEW", "draw");
            setRunning(true);
            drawThread = new DrawThread(getContext(), getHolder());
            drawThread.start();
            pointsAll += entries.get(drewGraphs).size();
            drewGraphs++;
        }
    }

    public void setRunning(boolean run) {
        running = run;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // уничтожение SurfaceView
        boolean retry = true;
        while (retry) {
            try {
                drawThread.join();
                retry = false;
            } catch (InterruptedException e) {
                //
            }
        }
    }

    public class DrawThread extends Thread {

        private SurfaceHolder surfaceHolder;

        public DrawThread(Context context, SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
        }

        @Override
        public void run() {
            while (running) {
                Canvas canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    try {
                        canvas.drawColor(Color.WHITE);

                        if (entries != null && entries.size() > 0 && isGraphToDraw) {

                            Paint paintGrid = new Paint();
                            paintGrid.setColor(getResources().getColor(R.color.grid));
                            paintGrid.setStrokeWidth(1f);
                            paintGrid.setAntiAlias(true);

                            float padding = (float) 0.025 * (canvas.getWidth() + canvas.getHeight());
                            float paddingY = padding * 0.5f;

                            float screenWidth = canvas.getWidth() - 2 * padding;
                            float screenHeight = canvas.getHeight() - 2 * padding - paddingY;


                            Log.d("SIZE", screenWidth + " " + screenHeight);

                            // Grid
                            int countOfDivisions = 5;
                            float stepGridX = screenWidth / countOfDivisions;
                            float stepGridY = screenHeight / countOfDivisions;

                            for (int i = countOfDivisions; i >= 0; i--) {
                                canvas.drawLine(padding, i * stepGridY + padding, padding + screenWidth,
                                        padding + i * stepGridY, paintGrid);
                                canvas.drawLine(padding + (countOfDivisions - i) * stepGridX,
                                        padding, padding + (countOfDivisions - i) * stepGridX,
                                        padding + screenHeight, paintGrid);
                            }

                            // Graph
                            getMinMaxValues();

                            Log.d("NEW MIN MAX VALUES", maxX + " :: " +  minX + "  ;  " + maxY + " :: " + minY);

                            float stepX = screenWidth / (maxX - minX);
                            float stepY = screenHeight / (maxY - minY);

                            Log.d("STEPS", "(" + stepX + ";" + stepY + ")");

                            Paint paintGraph = new Paint();
                            paintGraph.setColor(colors.get(drewGraphs));
                            paintGraph.setStrokeWidth(3);
                            paintGraph.setAntiAlias(true);

                            int k = drewGraphs;
                            float startX = (entries.get(k).get(0).getX() - minX) * stepX + padding;
                            float startY = normalY((entries.get(k).get(0).getY()  - minY) * stepY)
                                    - padding - paddingY;

                            for (int i = 1; i < entries.get(k).size(); i++) {
                                float stopX = (entries.get(k).get(i).getX() - minX) * stepX + padding;
                                float stopY = normalY((entries.get(k).get(i).getY() - minY) * stepY)
                                        - padding - paddingY;
                                Log.d("LINE", "(" + startX + ";" + startY + ")");
                                canvas.drawLine(startX, startY, stopX, stopY, paintGraph);
                                startX = stopX;
                                startY = stopY;
                            }

                            // Text
                            int textSize = (int) (padding * 0.5);
                            Paint paintTextHorizontal = new Paint();
                            paintTextHorizontal.setColor(getResources().getColor(R.color.text));
                            paintTextHorizontal.setStrokeWidth(1f);
                            paintTextHorizontal.setAntiAlias(true);
                            paintTextHorizontal.setTextAlign(Paint.Align.CENTER);
                            paintTextHorizontal.setTextSize(textSize);

                            Paint paintTextVertical = new Paint();
                            paintTextVertical.setColor(getResources().getColor(R.color.text));
                            paintTextVertical.setStrokeWidth(1f);
                            paintTextVertical.setAntiAlias(true);
                            paintTextVertical.setTextSize(textSize);

                            float cStepTextX = (maxX - minX) / 5;
                            float cStepTextY = (maxY - minY) / 5;

                            for (int i = countOfDivisions - 1; i > 0; i--) {
                                // X Axis
                                float valueX = (countOfDivisions - i) * cStepTextX + minX;
                                canvas.drawText(
                                        Float.toString(roundFloat(valueX, getDecimal(cStepTextX))),
                                        (countOfDivisions - i) * stepGridX + padding,
                                        screenHeight + padding + paddingY,
                                        paintTextHorizontal);

                                // Y Axis
                                float valueY = (countOfDivisions - i) * cStepTextY + minY;
                                canvas.drawText(
                                        Float.toString(roundFloat(valueY, getDecimal(cStepTextY))),
                                        0 + padding,
                                        i * stepGridY + padding,
                                        paintTextVertical);
                                firstNewAddedIndex = entries.get(k).size();
                            }

                            // names of Axis
                            int nameAxisSize = (int) ((int) padding * 0.7f);
                            // X
                            Paint paintAxisX = new Paint();
                            paintAxisX.setColor(getResources().getColor(R.color.text));
                            paintAxisX.setStrokeWidth(1f);
                            paintAxisX.setAntiAlias(true);
                            paintAxisX.setTextAlign(Paint.Align.CENTER);
                            paintAxisX.setTextSize(nameAxisSize);

                            canvas.drawText(xName,
                                    padding + screenWidth / 2,
                                    screenHeight + 2 * padding + paddingY * 0.5f,
                                    paintAxisX);

                            // Y
                            Paint paintAxisY = new Paint();
                            paintAxisY.setColor(getResources().getColor(R.color.text));
                            paintAxisY.setStrokeWidth(1f);
                            paintAxisY.setAntiAlias(true);
                            paintAxisY.setTextAlign(Paint.Align.CENTER);
                            paintAxisY.setTextSize(nameAxisSize);

                            float rotate_center_x = padding * 0.5f;
                            float rotate_center_y = screenHeight/2 + padding;
                            float degrees = -90;

                            canvas.rotate(degrees, rotate_center_x, rotate_center_y);
                            canvas.drawText(yName,
                                    padding * 0.5f,
                                    screenHeight / 2 + padding,
                                    paintAxisY);
                            canvas.rotate(-degrees, rotate_center_x, rotate_center_y);
                            running = false;
                        }
                        // if entries.size == 0 or null
                        else {
                            Paint p = new Paint();
                            p.setTextSize(getWidth()*0.1f);
                            p.setColor(Color.BLACK);
                            p.setTextAlign(Paint.Align.CENTER);
                            canvas.drawText("There is no data", getWidth() / 2, getHeight() / 2, p);
                            running = false;
                        }

                    } finally {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }

        private float normalY(float y){
            return getHeight() - y;
        }

        private float roundFloat(float a, int decimalPlaces){
            float b = 1;
            for (int i = 0; i<decimalPlaces; i++) { b *= 10; }
            int preRes = (int) Math.round(a * b);
            float result = ((float) preRes) / b;
            return result;
        }

        private int getDecimal(float interval){
            if ((int) (interval) / 10 >= 10) { return 0; }
            int countOfNum = 0;
            while(interval < 1) {
                interval *= 10;
                countOfNum ++;
            }
            return countOfNum + 1;
        }

        private void getMinMaxValues(){
            for (int i = 0; i < entries.get(drewGraphs).size(); i++) {
                float currentX = entries.get(drewGraphs).get(i).getX();
                float currentY = entries.get(drewGraphs).get(i).getY();

                if (currentX > maxX) maxX = currentX;
                if (currentX < minX) minX = currentX;
                if (currentY > maxY) maxY = currentY;
                if (currentY < minY) minY = currentY;
//
//            float[][] result = {{minX, maxX}, {minY, maxY}};
//            return result;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        return false;
    }
}