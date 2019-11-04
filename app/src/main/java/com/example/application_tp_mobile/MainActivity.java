package com.example.application_tp_mobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    final static int GALLERY_REQUEST_CODE =10;
    final static int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TouchExample view = new TouchExample(this);
        setContentView(view);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {


            Log.d("IMAGES", "IS NOT GRANTED");

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);

            // Permission is not granted

            }

        Cursor mCursor = getContentResolver()
                .query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        null,
                        null,
                        null,
                        MediaStore.Images.Media.DEFAULT_SORT_ORDER);

        mCursor.moveToFirst();
        while(!mCursor.isAfterLast()) {
            Log.d("IMAGES", " - _ID : " + mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media._ID)));
            Log.d("IMAGES", " - File Name : " + mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
            Log.d("IMAGES", " - File Path : " + mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA)));
            mCursor.moveToNext();
        }
        mCursor.close();


/*
        final String[] columns = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        };
        final String orderBy = MediaStore.Images.Media.DISPLAY_NAME;

        Cursor cursor = this.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy);

       String mExternalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();

        int mCount = cursor.getCount();
// mAbsolutePath is the absolute path (e.g. //emulated/0/folder/picture.png)
       String[] mAbsolutePath = new String[mCount];
        String[] mDisplayName = new String[mCount];
// mFolderIntentPath is what will eventually become the folder path (e.g. //emulated/0/folder/)
        String[] mFolderIntentPath = new String[mCount];
        String[] mBucket = new String[mCount];

        for (int i = 0; i < mCount; i++) {
            cursor.moveToPosition(i);
            int absolutePathColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            int displayNameColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
            int bucketColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            mAbsolutePath[i]= cursor.getString(absolutePathColumnIndex);
            mDisplayName[i] = cursor.getString(displayNameColumnIndex);
            mBucket[i] = cursor.getString(bucketColumnIndex);
            mFolderIntentPath[i] = mAbsolutePath[i].substring(0, mAbsolutePath[i].lastIndexOf('/'));
            Log.d("IMAGES", "absol path : "+mAbsolutePath[i] + "name : "+ mDisplayName[i]);
        }
*/




    }








}
