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

   private volatile boolean running = true;//флаг для остановки потока

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
        drawThread = new DrawThread(getContext(), getHolder());
        drawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // изменение размеров SurfaceView
    }

    public void requestStop() {
        running = false;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // уничтожение SurfaceView
        requestStop();
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
        private ArrayList<Entry> entries = new ArrayList<>();

        public DrawThread(Context context, SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
        }

        private void setData() {
            int x0 = 0;
            for (int x = x0; x <= 47; x ++){
                entries.add(new Entry(x, (float) (Math.sqrt(x)) ) );
            }
            entries.add(new Entry(2, (float) 5));
        }

        @Override
        public void run() {
            setData();
            while (running) {
                Canvas canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    try {
                        canvas.drawColor(Color.WHITE);

                        Paint paintGrid = new Paint();
                        paintGrid.setColor(getResources().getColor(R.color.grid));
                        paintGrid.setStrokeWidth(1f);
                        paintGrid.setAntiAlias(true);

                        float screenWidth = canvas.getWidth();
                        float screenHeight = canvas.getHeight();

                        Log.d("SIZE", screenWidth + " " + screenHeight);

                        // Grid
                        int countOfDivisions = 5;
                        float stepGridX = screenWidth / countOfDivisions;
                        float stepGridY = screenHeight / countOfDivisions;

                        for (int i = countOfDivisions; i > 0; i--) {
                            canvas.drawLine(0, i*stepGridY, screenWidth,
                                    i*stepGridY, paintGrid);
                            canvas.drawLine((countOfDivisions - i)*stepGridX, 0,
                                    (countOfDivisions - i)*stepGridX,
                                    screenHeight, paintGrid);
                        }

                        // Graph
                        float[][] area = getMinMaxValues();
                        float[] areaX = area[0];
                        float[] areaY = area[1];

                        Log.d("AREA", Arrays.deepToString(area));

                        float stepX = screenWidth / (areaX[1] - areaX[0]);
                        float stepY = screenHeight / (areaY[1] - areaY[0]);

                        Log.d("STEPS", "(" + stepX + ";" + stepY + ")");

                        Paint paintGraph = new Paint();
                        paintGraph.setColor(getResources().getColor(R.color.graph));
                        paintGraph.setStrokeWidth(3);
                        paintGraph.setAntiAlias(true);

                        float startX = (entries.get(0).getX() - areaX[0]) * stepX;
                        float startY = normalY((entries.get(0).getY() - areaY[0]) * stepY);

                        for (int i = 1; i < entries.size(); i++) {
                            float stopX = (entries.get(i).getX() - areaX[0]) * stepX;
                            float stopY = normalY((entries.get(i).getY() - areaY[0]) * stepY);
                            Log.d("LINE", "(" + startX + ";" + startY + ")");
                            canvas.drawLine(startX, startY, stopX, stopY, paintGraph);
                            startX = stopX;
                            startY = stopY;
                        }

                        // Text
                        Paint paintText = new Paint();
                        paintText.setColor(getResources().getColor(R.color.text));
                        paintText.setStrokeWidth(1f);
                        paintText.setAntiAlias(true);
                        paintText.setTextSize(15);

                        float cStepTextX = (areaX[1] - areaX[0]) / 5;
                        float cStepTextY = (areaY[1] - areaY[0]) / 5;


                        for (int i = countOfDivisions - 1; i > 0; i--) {
                            // X Axis
                            float valueX = (countOfDivisions - i) * cStepTextX + areaX[0];
                            canvas.drawText(Float.toString(roundFloat(valueX, 1)),
                                    (countOfDivisions - i) * stepGridX, screenHeight,
                                    paintText);

                            // Y Axis
                            float valueY = (countOfDivisions - i) * cStepTextY + areaY[0];
                            canvas.drawText(Float.toString(roundFloat(valueY, 1)),
                                    0, i * stepGridY,
                                    paintText);
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

        private float[][] getMinMaxValues(){
            float minX = entries.get(0).getX();
            float maxX = entries.get(0).getX();
            float minY = entries.get(0).getY();
            float maxY = entries.get(0).getY();

            for (Entry entry : entries){
                float currentX = entry.getX();
                float currentY = entry.getY();
                Log.d("DATA", currentX + ";" + currentY);
                if (currentX > maxX) maxX = currentX;
                if (currentX < minX) minX = currentX;
                if (currentY > maxY) maxY = currentY;
                if (currentY < minY) minY = currentY;
            }
            float[][] result = {{minX, maxX}, {minY, maxY}};
            return result;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        return false;
    }
}