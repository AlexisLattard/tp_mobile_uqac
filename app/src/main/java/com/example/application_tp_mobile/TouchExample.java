package com.example.application_tp_mobile;


import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.io.InputStream;
import java.util.ArrayList;


public class TouchExample extends View {

    private static final int MAX_POINTERS = 5;
    private ArrayList<String> images;
    private float mScale = 1f;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;

    private Pointer[] mPointers = new Pointer[MAX_POINTERS];
    private Paint mPaint;
    private float mFontSize;
    Canvas canvas;
    public TouchExample(Context context) {
        super(context);
        for (int i = 0; i < MAX_POINTERS; i++) {
            mPointers[i] = new Pointer();
        }

        mFontSize = 16 * getResources().getDisplayMetrics().density;
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(mFontSize);

        mGestureDetector = new GestureDetector(context, new ZoomGesture());
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGesture());


    }

    /**
     * affiche une image choisi de imagePathList dans une zone de l'écran donnée
     *
     * @param numberPicture numero de l'image dans imagePathList
     * @param zone          position et taille de l'image
     * @param canvas
     */
    public void drawPicture(int numberPicture, Rect zone, Canvas canvas) {


        BitmapDrawable image = new BitmapDrawable(getResources(), singleton.getInstance().imagePathList.get(numberPicture)); // on recupere l'image



/*
        Rect (The X coordinate of the left side of the rectangle
        // , int: The Y coordinate of the top of the rectangle
        //, int: The X coordinate of the right side of the rectangle,
        //int: The Y coordinate of the bottom of the rectangle)
*/

        image.setBounds(zone);

        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setFilterBitmap(true);

        image.draw(canvas);

    }

    public void initGalleryLigne(int positionY) {

        int widthScreen = getResources().getDisplayMetrics().widthPixels;
        int heightScreen = getResources().getDisplayMetrics().heightPixels;
        int positionX = 0;


            for (int i = 0; i < 7; i++) {
                positionX = i * (widthScreen / 7);
                drawPicture(i, new Rect(positionX, positionY * (widthScreen / 7), positionX + (widthScreen / 7), (widthScreen / 7) + positionY * (widthScreen / 7)), canvas);

            }
            Log.d("INITGALLERY", "positionY = " + positionY);
        }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
this.canvas = canvas;

        initGalleryLigne(0);

        // canvas.drawBitmap(image.getBitmap(), 0, 0, mPaint);
        Log.d("DRAW", "onDraw(Canvas canvas)");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        mScaleGestureDetector.onTouchEvent(event);

        int pointerCount = Math.min(event.getPointerCount(), MAX_POINTERS);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_MOVE:
                // clear previous pointers
                for (int id = 0; id < MAX_POINTERS; id++)
                    mPointers[id].index = -1;

                // Now fill in the current pointers
                for (int i = 0; i < pointerCount; i++) {
                    int id = event.getPointerId(i);
                    Pointer pointer = mPointers[id];
                    pointer.index = i;
                    pointer.id = id;
                    pointer.x = event.getX(i);
                    pointer.y = event.getY(i);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                for (int i = 0; i < pointerCount; i++) {
                    int id = event.getPointerId(i);
                    mPointers[id].index = -1;
                }
                invalidate();
                break;
        }
        return true;
    }

    class Pointer {
        float x = 0;
        float y = 0;
        int index = -1;
        int id = -1;
    }

    public class ZoomGesture extends GestureDetector.SimpleOnGestureListener {
        private boolean normal = true;

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            mScale = normal ? 3f : 1f;
            mPaint.setTextSize(mScale * mFontSize);
            normal = !normal;
            invalidate();
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
