package com.Whowant.Tokki.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class CommonUtils {
    private static ProgressDialog mProgressDialog;

//    public static String strDefaultUrl = "http://220.126.60.144:8080/howmuch_web/";
//    public static String strDefaultUrl = "http://192.168.43.249:8080/howmuch_web/";
//    public static String strDefaultUrl = "http://172.30.1.36:8080/howmuch_web/";
    public static String strDefaultUrl = "http://175.123.253.231:8080/howmuch_web/";
    public static Toast toast = null;

    public static String getRealPathFromURI(final Context context, final Uri uri) {
        String strAuth = uri.getAuthority();
        // ExternalStorageProvider
        if (isExternalStorageDocument(uri) || isDownloadsDocument(uri)) {
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            final String type = split[0];
            if ("primary".equalsIgnoreCase(type)) {
                return Environment.getExternalStorageDirectory() + "/"
                        + split[1];
            } else {
                String SDcardpath = getRemovableSDCardPath(context).split("/Android")[0];
                return SDcardpath +"/"+ split[1];
            }
        }

        // DownloadsProvider
        else if (isDownloadsDocument(uri)) {
            final String id = DocumentsContract.getDocumentId(uri);
            final Uri contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    Long.valueOf(id));

            return getDataColumn(context, contentUri, null, null);
        }

        // MediaProvider
        else if (isMediaDocument(uri)) {
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            final String type = split[0];

            Uri contentUri = null;
            if ("image".equals(type)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if ("video".equals(type)) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            } else if ("audio".equals(type)) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            }

            final String selection = "_id=?";
            final String[] selectionArgs = new String[] { split[1] };

            return getDataColumn(context, contentUri, selection, selectionArgs);
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getPointCount(int nCount) {
        String strCount = "" + nCount;

        if(nCount >= 1000) {
            float fHitsCount = (float)nCount / 1000;
            strCount = String.format("%.1fK", fHitsCount);
        } else {
            strCount = comma(nCount);
        }

        return strCount;
    }

    public static void showProgressDialog(Context context, String strContents) {
        if(context == null)
            return;

        if(mProgressDialog != null) {
            if(mProgressDialog.isShowing())
                return;

            mProgressDialog = null;
        }

        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(strContents);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);

        mProgressDialog.show();
    }

    public static void hideProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {

        }

        mProgressDialog = null;
    }

    public static Toast makeText(Context context, String text, int time) {
        if(toast != null)
            toast.cancel();

        toast = Toast.makeText(context, text, time);
        return toast;
    }


    public static String getRemovableSDCardPath(Context context) {
        File[] storages = ContextCompat.getExternalFilesDirs(context, null);
        if (storages.length > 1 && storages[0] != null && storages[1] != null)
            return storages[1].toString();
        else
            return "";
    }


    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }


    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }


    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }


    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }

    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap( v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }

    public static int getStatusBarSize(Activity activity) {
        Rect rectgle = new Rect();
        Window window = activity.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectgle);
        int StatusBarHeight = rectgle.top;
        int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int TitleBarHeight = contentViewTop - StatusBarHeight;

        return StatusBarHeight + contentViewTop;
    }

    public static void resetUserInfo(SharedPreferences pref) {
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("USER_ID", "Guest");
        editor.putString("USER_NAME", "Guest");
        editor.remove("USER_PW");
        editor.remove("USER_EMAIL");
        editor.remove("USER_PHOTO");
        editor.remove("USER_PHONENUM");
        editor.remove("REGISTER_SNS");
        editor.remove("ADMIN");
        editor.remove("EVENT_1");
        editor.remove("EVENT_2");
        editor.commit();
    }

    public static boolean bLocinCheck(SharedPreferences pref) {
        String strUserID = pref.getString("USER_ID", "Guest");
        String strUserName = pref.getString("USER_NAME", "Guest");

        if(strUserID == null || strUserID.length() == 0 || strUserID.equals("Guest")) {
            return false;
        } else if(strUserName == null || strUserName.length() == 0 || strUserName.equals("Guest")) {
            return false;
        }

        return true;
    }

    public static String strGetTime(String strDate) {
        String strDuration = "";

        Date dateNow = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss.s");
        Date dDate = null;

        try {
            dDate = dateFormat.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long duration = dateNow.getTime() - dDate.getTime();

        if(duration < 120000) {          // 1분보다 적다면, 즉 초단위라면
            strDuration = "방금 전";
        } else if(duration < 60000 * 60) {          // 1시간 보다 적다면, 즉 분단위라면
            strDuration = "" + (duration / 60000) + "분 전";
        } else if(duration < 60000 * 60 * 24) {     // 24시간 보다 적다면, 즉 시간 단위라면
            strDuration = "" + (duration / (60000 * 60)) + "시간 전";
        } else if(duration < 60000 * 60 * 24 * 8) {     // 7일 이내라면
            strDuration = "" + (duration / (60000 * 60 * 24)) + "일 전";
        } else {
            strDuration = strDate.substring(0, 16);
        }

        return strDuration;
    }

    public static Bitmap retriveVideoFrameFromVideo(Context context, String videoPath) throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(context, Uri.parse(videoPath));
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }

    public static boolean isColorDark(int color){
        double darkness = 1-(0.299* Color.red(color) + 0.587*Color.green(color) + 0.114*Color.blue(color))/255;
        if(darkness<0.5){
            return false; // It's a light color
        }else{
            return true; // It's a dark color
        }
    }

    public static Bitmap getImageFromURL(String imageURL){
        Bitmap imgBitmap = null;
        HttpURLConnection conn = null;
        BufferedInputStream bis = null;

        try
        {
            URL url = new URL(imageURL);
            conn = (HttpURLConnection)url.openConnection();
            conn.connect();

            int nSize = conn.getContentLength();
            bis = new BufferedInputStream(conn.getInputStream(), nSize);
            imgBitmap = BitmapFactory.decodeStream(bis);
        }
        catch (Exception e){
            e.printStackTrace();
        } finally{
            if(bis != null) {
                try {bis.close();} catch (IOException e) {}
            }
            if(conn != null ) {
                conn.disconnect();
            }
        }

        return imgBitmap;
    }

    public static String comma(String num) {
        int nNum = Integer.valueOf(num);
        DecimalFormat df = new DecimalFormat("#,##0");
        return df.format(nNum);
    }

    public static String comma(Long num) {
        DecimalFormat df = new DecimalFormat("#,##0");
        return df.format(num);
    }

    public static String comma(int num) {
        DecimalFormat df = new DecimalFormat("#,##0");
        return df.format(num);
    }

    public static int getLevel(int nDonationCarrotCount) {
        /*
        lv1 : 0 ~ 200
        lv2 : 201 ~ 400
        lv3 : 401 ~ 600
        lv4 : 601 ~ 800
        lv5 : 801 ~ 1000
        lv6 : 1001 ~ 2000
        lv7 : 2001 ~ 4000
        lv8 : 4001 ~ 6000
        lv9 : 6001 ~ 8000
        lv10 : 8001 ~
         */
        int nLevel = 1;

        if(nDonationCarrotCount <= 500)
            nLevel = 1;
        else if(nDonationCarrotCount <= 1000)
            nLevel = 2;
        else if(nDonationCarrotCount <= 2000)
            nLevel = 3;
        else if(nDonationCarrotCount <= 4000)
            nLevel = 4;
        else if(nDonationCarrotCount <= 8000)
            nLevel = 5;
        else if(nDonationCarrotCount <= 16000)
            nLevel = 6;
        else if(nDonationCarrotCount <= 32000)
            nLevel = 7;
        else if(nDonationCarrotCount <= 80000)
            nLevel = 8;
        else if(nDonationCarrotCount <= 300000)
            nLevel = 9;
        else
            nLevel = 10;


        return nLevel;
    }

    public static Bitmap getVideoThumbnail(Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try {
            retriever.setDataSource(uri.toString(), new HashMap<String, String>());
            Bitmap thumbnailBitmap = retriever.getFrameAtTime(1000, MediaMetadataRetriever.OPTION_CLOSEST);
            return thumbnailBitmap;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
