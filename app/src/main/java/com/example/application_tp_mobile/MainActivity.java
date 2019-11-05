package com.example.application_tp_mobile;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    final static int GALLERY_REQUEST_CODE = 10;
    final static int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Display d = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        d.getSize(size);
        int width = size.x;
        int height = size.y;
        singleton.getInstance().setScreenWidth(width);
        singleton.getInstance().setScreenHeight(height);

        //TODO faire le test selon les SDK

// on verifie qu'on a acces au stockage
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {


            Log.d("IMAGES", "IS NOT GRANTED");
//  si ce n'est pas le cas on demande l'autorisation
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);

            // Permission is not granted

        }

        String selection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " = ? ";
        String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME };
        String selectionArgs[] = {"Camera"};

// on recupere les path
        Cursor mCursor = getContentResolver()
                .query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        columns,
                        selection,
                        selectionArgs,
                        MediaStore.Images.Media.DEFAULT_SORT_ORDER);

        mCursor.moveToFirst();
        while (!mCursor.isAfterLast()) {
            /*Log.d("IMAGES", " - _ID : " + mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media._ID)));
            Log.d("IMAGES", " - File Name : " + mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
            Log.d("IMAGES", " - File Path : " + mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA)));*/
            singleton.getInstance().imagePathList.add(mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA)));
            mCursor.moveToNext();
        }
        mCursor.close();

        TouchExample view = new TouchExample(this);
        setContentView(view);
    }



}
