package com.example.application_tp_mobile;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.HashMap;

/**
 * sert d'interface utilisateur avec chargement et affichages des images, gestion des inputs utilisateurs.
 */

public class ImageGallery extends View {

    private static final int MAX_POINTERS = 5;
    private static final int initialMaxImgPerLine = 4; //nombre d'image au démarrage de l'application
    private float mScale = 1f;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private Pointer[] mPointers = new Pointer[MAX_POINTERS];
    private Paint mPaint;
    private float mFontSize;
    private Canvas canvas;
    private HashMap<Integer, BitmapDrawable> imageList = new HashMap<>();
    private int screenWidth = getResources().getDisplayMetrics().widthPixels;
    private int screenHeight = getResources().getDisplayMetrics().heightPixels;
    private int maxImgPerLine; // nombre d'images sur une ligne
    private float touchPositionY; // dernière vitesse du doigt connue dans la direction Y (pour le drag)
    private float delta; // détermine la distance du mouvement (pour le drag)
    private int index; // correspond à l'index de l'image visible en haut à gauche dans imagePathList
    private Thread thread; // utlisé pour précharger les images en arrière-plan

    /**
     * Constructeur qui précharge  des images et initialise certaines variables
     *
     * @param context le contexte de l'application
     */
    public ImageGallery(Context context) {
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
        maxImgPerLine = initialMaxImgPerLine;
        preLoadThread();

        this.touchPositionY = 0;
        this.delta = 0;
        this.index = 0;

    }

    /**
     * Permet de déterminer le format à charger
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */

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
     * Charge une image selon les dimensions voulues
     *
     * @param pictureNumber index de l'image dans imagePathList
     * @return l'image chargée
     */
    public BitmapDrawable loadImage(Integer pictureNumber, Rect zone) {
        // On vérifie que l'image n'est pas déjà chargée
        if (!imageList.containsKey(pictureNumber)) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(Singleton.getInstance().imagePathList.get(pictureNumber), options);
            options.inSampleSize = calculateInSampleSize(options, zone.right - zone.left, zone.bottom - zone.bottom);
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(Singleton.getInstance().imagePathList.get(pictureNumber), options);
            imageList.put(pictureNumber, new BitmapDrawable(getResources(), bitmap));
        } else {
        }

        return imageList.get(pictureNumber);

    }

    /**
     * Permet de charger des images dans la liste d'images
     *
     * @param nbOfPreloadedImages nombre d'images à charger
     * @param shift               + index = indice de la 1ère image à charger
     */
    public void preLoad(int nbOfPreloadedImages, int shift) {
        int lastPicShownIndex = index * maxImgPerLine + shift;

        for (int i = lastPicShownIndex; i < lastPicShownIndex + nbOfPreloadedImages; i++) {
            loadImage(i, new Rect(1, 1 * (screenWidth / maxImgPerLine),
                    1 + (screenWidth / maxImgPerLine),
                    (screenWidth / maxImgPerLine) + 1 * (screenWidth / maxImgPerLine)));
            // TODO Supprimer le log
            Log.d("LOAD", "preLoaded image : " + i);
        }

    }

    /**
     * Charge des images dans des threads séparés
     */
    public void preLoadThread() {

        final int nbOfLines = screenHeight / (screenWidth / maxImgPerLine);

        thread = new Thread(new Runnable() {
            public void run() {
                preLoad((maxImgPerLine * nbOfLines) / 2, index + (maxImgPerLine * nbOfLines));

            }
        });
        thread.start();

    }

    /**
     * Affiche une image choisie de imageList (ou imagePathList si elle n'est pas déja chargée)
     * dans une zone de l'écran donnée
     *
     * @param pictureID numero de l'image dans imagePathList
     * @param zone      position et taille de l'image
     * @param canvas
     */
    public void drawPicture(final int pictureID, final Rect zone, final Canvas canvas) {
        BitmapDrawable image = loadImage(pictureID, zone);

        image.setBounds(zone);

        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setFilterBitmap(true);

        image.draw(canvas);
    }

    /**
     * Charge et affiche un nombre d'image par ligne choisi
     *
     * @param nbOfPicPerLine nombre d'image sur une ligne
     */
    public void refreshGallery(int nbOfPicPerLine) {

        int pictureID = 0 + index;
        int positionX;
        int nbOfLines = screenHeight / (screenWidth / nbOfPicPerLine);


        for (int positionY = 0; positionY < nbOfLines; positionY++) {
            for (int i = 0; i < nbOfPicPerLine; i++) {
                positionX = i * (screenWidth / nbOfPicPerLine);
                drawPicture(pictureID, new Rect(positionX, positionY * (screenWidth / nbOfPicPerLine),
                        positionX + (screenWidth / nbOfPicPerLine),
                        (screenWidth / nbOfPicPerLine) + positionY * (screenWidth / nbOfPicPerLine)), canvas);
                pictureID++;

            }
        }
    }


    @Override
    protected void onDraw(Canvas canvas) { // fonction qui redessine l'écran
        super.onDraw(canvas);
        this.canvas = canvas;
        //securité pour ne pas chercher une image qui n'existe pas
        if (index <= 0) {
            index = 0;
        }
        if (index >= Singleton.getInstance().imagePathList.size()) {
            index = Singleton.getInstance().imagePathList.size();
        }
        preLoadThread();
        refreshGallery(maxImgPerLine);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        mScaleGestureDetector.onTouchEvent(event);


        invalidate();


        return true;
    }

    class Pointer {
        float x = 0;
        float y = 0;
        int index = -1;
        int id = -1;
    }

    public class ZoomGesture extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                // TODO Supprimer le log (et la méthode complète avec en fait)
                Log.d("MOVE", "ONDOWN)");
            }
            return true;
        }

        /*
        Permet d'effectuer le scrolling a l'écran ( drag )
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            delta += touchPositionY + distanceY;
            if (delta > 800) {
                touchPositionY = distanceY;
                index = index + maxImgPerLine;
                delta = 0;
            }
            if (delta < -800) {
                touchPositionY = distanceY;

                index = index - maxImgPerLine;
                delta = 0;
            }
            touchPositionY = distanceY;
            return true;
        }

    }

    /*
    classe utilisée pour gérer le nombre d'images sur une ligne à partir d'un geste de zoom/dézoom de l'utilisateur
     */
    public class ScaleGesture extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScale *= detector.getScaleFactor();
            thread.interrupt(); // on stoppe le préchargement des images car on change de résolution
            maxImgPerLine = (int) (initialMaxImgPerLine / mScale);

            // par sécurité, on limite le nombre d'images
            if (maxImgPerLine > 7) {
                maxImgPerLine = 7;
            } else if (maxImgPerLine < 1) {
                maxImgPerLine = 1;
            }

            invalidate();

            return true;
        }
    }
}


