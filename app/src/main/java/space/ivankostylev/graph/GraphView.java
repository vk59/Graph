package space.ivankostylev.graph;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;

public class GraphView extends SurfaceView {

    private Bitmap bmp;

    private ArrayList<Entry> entries = new ArrayList<>();

    private SurfaceHolder holder;

    public GraphView(Context context) {
        super(context);
//        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_android);
        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @SuppressLint("WrongCall")
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                int x0 = -5;
                for (int x = x0; x <= 5; x ++){
                    entries.add(new Entry(x, (float) 0.1*x*x*x));
                }
                entries.add(new Entry(2, (float) 5));
                Canvas canvas = holder.lockCanvas();
                onDraw(canvas);
                holder.unlockCanvasAndPost(canvas);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    protected void onDraw(Canvas canvas){
        canvas.drawColor(Color.WHITE);
        Paint p = new Paint();
        p.setColor(getResources().getColor(R.color.colorPrimary));
        p.setStrokeWidth(10);
        p.setAntiAlias(true);
        float screenWidth = this.getWidth();
        float screenHeight = this.getHeight();

        float[][] area = getMinMaxValues();
        float[] areaX = area[0];
        float[] areaY = area[1];

        Log.d("AREA", Arrays.deepToString(area));

        float stepX = screenWidth / (areaX[1] - areaX[0]);
        float stepY = screenHeight / (areaY[1] - areaY[0]);


        float step;
        if (stepX < stepY) {
            step = stepX;
        } else {
            step = stepY;
        }
        Log.d("STEPS", "(" + stepX + ";" + stepY + ")");

        float startX = (entries.get(0).getX() - areaX[0]) * step;
        float startY = normalY((entries.get(0).getY() - areaY[0]) * step);
        for(int i = 1; i < entries.size(); i++){
            float stopX = (entries.get(i).getX() - areaX[0]) * step;
            float stopY = normalY((entries.get(i).getY() - areaY[0])* step);
            Log.d("LINE", "(" + startX + ";" + startY + ")");
            canvas.drawLine(startX, startY, stopX, stopY, p);
            startX = stopX;
            startY = stopY;
        }
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

//  < We have to normalize Y-coordinate because zero of Y is in the top >
    private float normalY(float y){
        return getHeight() - y;
    }
}
