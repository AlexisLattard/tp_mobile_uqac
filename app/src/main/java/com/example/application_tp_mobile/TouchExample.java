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
import android.os.HandlerThread;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Handler;


public class TouchExample extends View {

    private static final int MAX_POINTERS = 5;
    private ArrayList<String> images;
    private float mScale = 1f;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;

    private Pointer[] mPointers = new Pointer[MAX_POINTERS];
    private Paint mPaint;
    private float mFontSize;


    private Canvas canvas;
    private HashMap<Integer, BitmapDrawable> imageList = new HashMap<Integer, BitmapDrawable>();
    private int widthScreen = getResources().getDisplayMetrics().widthPixels;
    private int heightScreen = getResources().getDisplayMetrics().heightPixels;

    private int nbImageLigne = 5;
    private float touchPositionY = 0;
    private float mouvement = 0;
    private int index = 0;

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

        preLoadedThread();

    }

    /**
     * charge une image
     *
     * @param numeroPicture
     * @return
     */
    public BitmapDrawable loadImage( Integer numeroPicture, Rect zone) {
        // on verifie que l'image n'est pas déja chargé
        if (!imageList.containsKey(numeroPicture)) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(singleton.getInstance().imagePathList.get(numeroPicture), options);
            options.inSampleSize = calculateInSampleSize(options, zone.right - zone.left, zone.bottom - zone.bottom);
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(singleton.getInstance().imagePathList.get(numeroPicture), options);
            imageList.put(numeroPicture,new BitmapDrawable(getResources(), bitmap));
          //  Log.d("LOAD", "image chargé");
        }else{
           // Log.d("LOAD", "image exist déja, key = "+imageList.get(numeroPicture));
        }

        return imageList.get(numeroPicture);

    }

    public void preLoad(int nbImagePreloaded,int decalage){
        int indexLastImageAffiche = index*nbImageLigne+decalage;

        for (int i = indexLastImageAffiche;i<indexLastImageAffiche+nbImagePreloaded;i++){
            loadImage(i,new Rect(1, 1 * (widthScreen / nbImageLigne), 1 + (widthScreen / nbImageLigne), (widthScreen / nbImageLigne) + 1 * (widthScreen / nbImageLigne)));
            Log.d("LOAD","preLoaded image : "+i);
        }
        //Log.d("LOAD","Images preLoaded");

    }
public void preLoadedThread(){
        ArrayList<Thread> threads = new ArrayList<>();

        for(int i=0;i<3;i++) {
            final int finalI = i;

           threads.add( new Thread(new Runnable() {
                public void run() {
                    Log.d("LOADTREAD", "Thread PRE loaded ID------------------- :"+finalI);
                    preLoad(nbImageLigne*3,3*nbImageLigne+(nbImageLigne* finalI));
                    Log.d("LOADTREAD", "Thread PRE loaded ID____________________ :"+finalI);

                }
            }));
           threads.get(i).start();

        }


}


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    /**
     * affiche une image choisi de imageList ou (imagePathList si elle n'est pas déja chargé) dans une zone de l'écran donnée
     *
     * @param numeroPicture numero de l'image dans imagePathList
     * @param zone          position et taille de l'image
     * @param canvas
     */
    public void drawPicture(final int numeroPicture, final Rect zone, final Canvas canvas) {


        BitmapDrawable image = loadImage(numeroPicture, zone);

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

    /**
     * charge et affiche un nombre d'image par ligne choisi
     *
     * @param nbImageLigne nombre d'image sur une ligne
     */
    public void refrshGallery(int nbImageLigne) {

        int numeroImage = 0+index;
        int positionX = 0;
        int nbLigne =(int) (heightScreen / (widthScreen / nbImageLigne));


        for (int positionY = 0; positionY < nbLigne; positionY++) {
            for (int i = 0; i < nbImageLigne; i++) {
                positionX = i * (widthScreen / nbImageLigne);
                drawPicture(numeroImage, new Rect(positionX, positionY * (widthScreen / nbImageLigne), positionX + (widthScreen / nbImageLigne), (widthScreen / nbImageLigne) + positionY * (widthScreen / nbImageLigne)), canvas);
                numeroImage++;
            }
            Log.d("INITGALLERY", "positionY = " + positionY + "total Ligne = " + nbLigne);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;

        //securité pour ne pas chercher une image qui n'existe pas
        if(index<=0){
            index=0;
        }
        if (index >= singleton.getInstance().imagePathList.size()){
            index = singleton.getInstance().imagePathList.size();
        }
        refrshGallery(nbImageLigne);
        preLoadedThread();

        // canvas.drawBitmap(image.getBitmap(), 0, 0, mPaint);
        Log.d("DRAW", "onDraw(Canvas canvas)");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        mScaleGestureDetector.onTouchEvent(event);

        int pointerCount = Math.min(event.getPointerCount(), MAX_POINTERS);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case (MotionEvent.ACTION_DOWN):
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

        @Override
        public boolean onDown(MotionEvent e){
            if(e.getAction() == MotionEvent.ACTION_DOWN) {
                Log.d("MOVE", "ONDOWN)");
            }
            return true;
        }
//TODO pne recharge pas les anciennes images et saute plus d'une ligne
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){
             mouvement += touchPositionY +distanceY;
             if(mouvement>800){
                 Log.d("MOVE", "MOUVE DOWN ");
                 touchPositionY  = distanceY;
                 index = index+nbImageLigne;
                 mouvement =0;
             }
            if(mouvement<-800){
                Log.d("MOVE", "MOUVE UP ");
                touchPositionY  = distanceY;

                    index = index-nbImageLigne;
                mouvement =0;
            }
           Log.d("MOVE", "Y : "+distanceY +" mouv " +mouvement+"index = "+index);
            touchPositionY  = distanceY;
            return true;
        }

    }

    public class ScaleGesture extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScale *= detector.getScaleFactor();
            mPaint.setTextSize(mScale * mFontSize);
            int valeur = (int) ((Math.abs(mScale)*0.1)%7);
            Log.d("GESTURE", "mScale = "+mScale+"nb ligne : "+valeur);
            if(mScale<0){
                nbImageLigne = 7;
            }else if (mScale>65){
                nbImageLigne = 1;
            }else
            {
                nbImageLigne = 7- valeur;
            }

            invalidate();

            return true;
        }
    }


}
