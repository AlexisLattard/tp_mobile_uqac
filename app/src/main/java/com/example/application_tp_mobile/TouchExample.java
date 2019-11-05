package com.example.application_tp_mobile;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;


public class TouchExample extends View {

    private float mScale = 1f;
    private float mScroll = 0f;
    private GestureDetector mScrollGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;

    private Paint mPaint;
    private float mFontSize;

    public TouchExample(Context context) {
        super(context);

        mFontSize = 16 * getResources().getDisplayMetrics().density;
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(mFontSize);

        mScrollGestureDetector = new GestureDetector(context, new ScrollGesture());
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGesture());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        ArrayList<String> urlList = singleton.getInstance().imagePathList;
        int swidth = singleton.getInstance().screenWidth;
        int sheight = singleton.getInstance().screenHeight;
        int n = 7;
        int largeur_image = swidth/n;
        int nb_images = sheight / largeur_image * n + n;
        Log.d("SIZE", ""+nb_images);
        Log.d("SIZE", ""+sheight);
        Log.d("SIZE", ""+largeur_image);
        int start = (int) mScroll / largeur_image * n;
        for (int i = start; i - start < nb_images && i < urlList.size(); i++) {
            Bitmap image = BitmapFactory.decodeFile(urlList.get(i));
            image = Bitmap.createScaledBitmap(image,largeur_image,largeur_image,false);
            mPaint.setFilterBitmap(true);
            canvas.drawBitmap(image, (i%n)*largeur_image, (i/n)*largeur_image-mScroll, mPaint);
        }
    }

    @Override
    public void setOnScrollChangeListener(OnScrollChangeListener l) {
        super.setOnScrollChangeListener(l);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScrollGestureDetector.onTouchEvent(event);
        mScaleGestureDetector.onTouchEvent(event);

        return true;
    }

    public class ScrollGesture extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (distanceY < 1 || distanceY > 1) {
                if (mScroll + distanceY < 0) mScroll = 0;
                else mScroll += distanceY;
                Log.d("SCROLL",""+mScroll);
                invalidate();
            }

            return true;
        }
    }

    public class ScaleGesture extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScale *= detector.getScaleFactor();
            mPaint.setTextSize(mScale * mFontSize);
            invalidate();

            return true;
        }
    }
}