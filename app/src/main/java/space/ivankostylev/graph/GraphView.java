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
import java.util.Arrays;

public class GraphView extends SurfaceView implements SurfaceHolder.Callback{
    public DrawThread drawThread;

    private volatile boolean running = true; //флаг для остановки потока
    private volatile int firstNewAddedIndex = 0;

    private volatile ArrayList<Entry> entries = new ArrayList<>();

    private volatile float minX;
    private volatile float maxX;
    private volatile float minY;
    private volatile float maxY;

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
        redrawGraph();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // изменение размеров SurfaceView
    }

    public void addInitialData(float x, float y) {
        Log.d("ADDED DATA", x + " " + y);
        entries.add(new Entry(x, y));
        if (entries.size() == 1) {
            minX = entries.get(0).getX();
            maxX = entries.get(0).getX();
            minY = entries.get(0).getY();
            maxY = entries.get(0).getY();
        }
    }

    public void addData(float x, float y) {
        Log.d("ADDED DATA", x + " " + y);
        entries.add(new Entry(x, y));
        if (entries.size() == 1) {
            minX = entries.get(0).getX();
            maxX = entries.get(0).getX();
            minY = entries.get(0).getY();
            maxY = entries.get(0).getY();
        }
        if (entries.size() > 0) {
            redrawGraph();
        }
    }

    public void redrawGraph() {
        setRunning(true);
//        if (drawThread != null && drawThread.isAlive()){
//            drawThread.start();
//        }
//        else {
            drawThread = new DrawThread(getContext(), getHolder());
            drawThread.start();
//        }
    }

    public void setRunning(boolean run) {
        running = run;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // уничтожение SurfaceView
        setRunning(false);
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

                        if (entries.size() > 0 && entries != null) {

                            Paint paintGrid = new Paint();
                            paintGrid.setColor(getResources().getColor(R.color.grid));
                            paintGrid.setStrokeWidth(1f);
                            paintGrid.setAntiAlias(true);

                            float padding = (float) 0.01 * (canvas.getWidth() + canvas.getHeight());
                            float paddingY = padding * 1.5F;

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
                            paintGraph.setColor(getResources().getColor(R.color.graph));
                            paintGraph.setStrokeWidth(3);
                            paintGraph.setAntiAlias(true);

                            float startX = (entries.get(0).getX() - minX) * stepX + padding;
                            float startY = normalY((entries.get(0).getY() - minY) * stepY)
                                    - padding - paddingY;

                            for (int i = 1; i < entries.size(); i++) {
                                float stopX = (entries.get(i).getX() - minX) * stepX + padding;
                                float stopY = normalY((entries.get(i).getY() - minY) * stepY)
                                        - padding - paddingY;
                                Log.d("LINE", "(" + startX + ";" + startY + ")");
                                canvas.drawLine(startX, startY, stopX, stopY, paintGraph);
                                startX = stopX;
                                startY = stopY;
                            }

                            // Text
                            int textSize = (int) (paddingY);
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
                                        i * stepGridY + padding - 5,
                                        paintTextVertical);
                                firstNewAddedIndex = entries.size();
                            }
                        }
                        // if entries.size == 0 or null
                        else {
                            Paint p = new Paint();
                            p.setTextSize(30f);
                            canvas.drawText("There is no data", 0, 0, p);
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
            for (int i = firstNewAddedIndex; i < entries.size(); i++) {
                float currentX = entries.get(i).getX();
                float currentY = entries.get(i).getY();

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