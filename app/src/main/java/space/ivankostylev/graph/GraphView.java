package space.ivankostylev.graph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
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
    private volatile String xName = "";
    private volatile String yName = "";
    private volatile boolean isRemoved = false;

    private volatile ArrayList<ArrayList<Moment>> moments = new ArrayList<>();
    private volatile ArrayList<GraphData> allGraphData = new ArrayList<>();
    private volatile ArrayList<Integer> colors = new ArrayList<>();
    private volatile ArrayList<String> labels = new ArrayList<>();
    private volatile int pointsAll = 0;

    private volatile float minX;
    private volatile float maxX;
    private volatile float minY;
    private volatile float maxY;

    private volatile int drewGraphs = 0;
    private boolean isLegendEnable = true;

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
        moments.add(data.getData());
        colors.add(data.getColor());
        labels.add(data.getLabel());
        allGraphData.add(data);
        Log.d("ADDED DATA", "Data number " + moments.size());
        if (!isInitialized) {
            minX = data.getMinX();
            maxX = data.getMaxX();
            minY = data.getMinY();
            maxY = data.getMaxY();
        } else {
            if (minX > data.getMinX()) minX = data.getMinX();
            if (maxX < data.getMaxX()) maxX = data.getMaxX();
            if (minY > data.getMinY()) minY = data.getMinY();
            if (maxY < data.getMaxY()) maxY = data.getMaxY();
        }
    }

    public void removeGraphData(int index) {
        moments.remove(index);
        allGraphData.remove(index);
        colors.remove(index);
        labels.remove(index);
        isRemoved = true;
    }

    public void removeAllGraphData(int index) {
        allGraphData.removeAll(allGraphData);
        moments.removeAll(moments);
        colors.removeAll(colors);
        labels.removeAll(labels);
    }

    public long getCountGraphs() {
        return moments.size();
    }

    public void setAxisName(String xName, String yName) {
        this.xName = xName;
        this.yName = yName;
    }

    public void setLegendEnable(boolean isEnable) {
        this.isLegendEnable = isEnable;
    }

    public void drawGraph() {
        isGraphToDraw = true;
        if (isInitialized) {
            Log.d("GRAPH VIEW", "draw");
            setRunning(true);
            drawThread = new DrawThread(getContext(), getHolder());
            drawThread.start();
        }
    }

    public void setRunning(boolean run) {
        running = run;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
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
        private Canvas canvas;
        private SurfaceHolder surfaceHolder;

        private float screenWidth;
        private float screenHeight;
        private float padding;
        private float paddingY;
        private float paddingLegend;
        private float paddingBottom;

        private int countOfDivisions;

        private float stepGridX;
        private float stepGridY;


        public DrawThread(Context context, SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
        }

        @Override
        public void run() {
            while (running) {
                canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    try {
                        canvas.drawColor(Color.WHITE);
                        if (moments != null && moments.size() > 0 && isGraphToDraw) {
//                            if (isRemoved) {
//
//                            }
                            getMinMaxValues();
                            Log.d("X", "minX = " + minX + " :: " + "maxX = " + maxX);
                            Log.d("Y", "minY = " + minY + " :: " + "maxY = " + maxY);

                            setScreenSettings();
                            drawGrid();
                            drawText();
                            drawNamesOfAxis();
                            drawGraphs();
                            if (isLegendEnable) {
                                drawLegend();
                            }
                            running = false;
                        }
                        else {
                            setNoDataText();
                            running = false;
                        }

                    } finally {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }

        private void drawLegend() {
            float paddingForLabel = paddingLegend / moments.size();
            float sideOfSquare = 0.9f * paddingForLabel;
            float textSize = 0.9f * paddingForLabel;
            for (int i = 0; i < moments.size(); i++) {
                Paint legendRectanglePaint = new Paint();
                legendRectanglePaint.setColor(colors.get(i));
                int bottom = (int) (screenHeight + paddingY + padding + paddingForLabel * (i + 1));
                int left = (int) (padding * 1.5);
                int top = (int) (bottom - sideOfSquare);
                int right = (int) (left + sideOfSquare);
                Rect square = new Rect(left, top, right, bottom);
                canvas.drawRect(square, legendRectanglePaint);

                Paint legendTextPaint = new Paint();
                legendTextPaint.setTextSize(textSize);
                legendTextPaint.setColor(getResources().getColor(R.color.text));
                int startOfTextX = (int) (right + sideOfSquare * 0.5f);
                int startOfTextY = (int) (bottom);
                canvas.drawText(labels.get(i), startOfTextX, startOfTextY, legendTextPaint);
            }
        }

        private void setScreenSettings() {
            padding = (float) 0.025 * (canvas.getWidth() + canvas.getHeight());
            paddingY = padding * 0.5f;
            paddingLegend = 0;

            if (isLegendEnable) {
                paddingLegend += moments.size() * 0.5f * padding;
            }

            paddingBottom = padding * 0.5f;
            screenWidth = canvas.getWidth() - 2 * padding;
            screenHeight = canvas.getHeight() - padding - paddingY - max(paddingLegend, padding) - paddingBottom;

            countOfDivisions = 5;
        }

        private float max(float a, float b) {
            if (a > b) return a;
            return b;
        }

        // DRAWING
        private void drawGraphs() {
            // GRAPH DRAWING
            // GRAPH parameters
//                                Log.d("NEW MIN MAX VALUES", maxX + " :: " + minX + "  ;  " + maxY + " :: " + minY);
            float stepX = screenWidth / (maxX - minX);
            float stepY = screenHeight / (maxY - minY);

//                                Log.d("STEPS", "(" + stepX + ";" + stepY + ")");

            Paint paintGraph = new Paint();
            paintGraph.setStrokeWidth(3);
            paintGraph.setAntiAlias(true);

            int points = getMaximumOfSizes();
            for (int thisPoint = 0; thisPoint < points - 1; thisPoint++) {
                for (ArrayList<Moment> graph : moments) {
                    if (graph.size() > thisPoint + 1) {
                        Moment moment = graph.get(thisPoint);
                        Moment nextMoment = graph.get(thisPoint + 1);

                        int graphNum = moments.indexOf(graph);
                        paintGraph.setColor(colors.get(graphNum));

                        float startX = (moment.getX() - minX) * stepX + padding;
                        float startY = normalY((moment.getY() - minY) * stepY)
                                - max(padding, paddingLegend) - paddingY - paddingBottom;
                        float stopX = (nextMoment.getX() - minX) * stepX + padding;
                        float stopY = normalY((nextMoment.getY() - minY) * stepY)
                                - max(padding, paddingLegend) - paddingY - paddingBottom;
                        Log.d("GRAPH #", "#" + graphNum + "# " + startX + " :: " + stopY);

                        canvas.drawLine(startX, startY, stopX, stopY, paintGraph);
                    }
                }
        }
    }


        private void drawGrid() {

            // Grid
            // GRID Paint
            Paint paintGrid = new Paint();
            paintGrid.setColor(getResources().getColor(R.color.grid));
            paintGrid.setStrokeWidth(1f);
            paintGrid.setAntiAlias(true);

            // GRID Parameters
            stepGridX = screenWidth / countOfDivisions;
            stepGridY = screenHeight / countOfDivisions;
            for (int i = countOfDivisions; i >= 0; i--) {
                canvas.drawLine(padding, i * stepGridY + padding, padding + screenWidth,
                        padding + i * stepGridY, paintGrid);
                canvas.drawLine(padding + (countOfDivisions - i) * stepGridX,
                        padding, padding + (countOfDivisions - i) * stepGridX,
                        padding + screenHeight, paintGrid);
            }
        }


        private void drawText() {
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
            }
        }

        private void drawNamesOfAxis() {
            // names of Axis
            int nameAxisSize = (int) ((int) padding * 0.7f);

            Paint paintAxis = new Paint();
            paintAxis.setColor(getResources().getColor(R.color.text));
            paintAxis.setStrokeWidth(1f);
            paintAxis.setAntiAlias(true);
            paintAxis.setTextAlign(Paint.Align.CENTER);
            paintAxis.setTextSize(nameAxisSize);

            // X
            canvas.drawText(xName,
                    padding + screenWidth / 2,
                    screenHeight + 2 * padding + paddingY * 0.5f,
                    paintAxis);

            // Y
            float rotate_center_x = padding * 0.5f;
            float rotate_center_y = screenHeight / 2 + padding;
            float degrees = -90;

            canvas.rotate(degrees, rotate_center_x, rotate_center_y);
            canvas.drawText(yName,
                    padding * 0.5f,
                    screenHeight / 2 + padding,
                    paintAxis);
            canvas.rotate(-degrees, rotate_center_x, rotate_center_y);
        }

        private void setNoDataText() {
            Paint p = new Paint();
            p.setTextSize(getWidth() * 0.1f);
            p.setColor(Color.BLACK);
            p.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("There is no data", getWidth() / 2, getHeight() / 2, p);
        }

        private int getMaximumOfSizes() {
            int maximumOfSizes = 0;
            for (ArrayList<Moment> data : moments) {
                if (maximumOfSizes < data.size()) {
                    maximumOfSizes = data.size();
                }
            }
            return maximumOfSizes;
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
            maxX = -Float.MAX_VALUE;
            minX = Float.MAX_VALUE;
            maxY = -Float.MAX_VALUE;
            minY = Float.MAX_VALUE;
            for (GraphData graph : allGraphData) {
                if (graph.getMaxX() > maxX) maxX = graph.getMaxX();
                if (graph.getMinX()  < minX) minX = graph.getMinX();
                if (graph.getMaxY()  > maxY) maxY = graph.getMaxY();
                if (graph.getMinY()  < minY) minY = graph.getMinY();
            }
            isRemoved = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        return false;
    }
}