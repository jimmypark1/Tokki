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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.UI.Activity.Work.LiteratureWriteActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import okhttp3.OkHttpClient;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class CommonUtils {                                                                  // Const 등을 사용하지 않고 잡다한 static 변수 및 펑션등을 모아놓은 클래스
    private static ProgressDialog mProgressDialog;

//    public static String strDefaultUrl = "http://172.31.48.1:8080/howmuch_web/";                               // 테스트 서버 주소
    public static String strDefaultUrl = "http://175.123.253.231:8080/howmuch_web/";                        // 상용 서버 주소

    public static Toast toast = null;                                                                       // 연속으로 Toast 를 띄울때 이걸 사용하면 딜레이 없이 토스트 사용 가능
    public static ArrayList<String> forbiddenWords = new ArrayList<>();                                     // 비속어 필터
    private static boolean isComplete = false;                                                              // forbiddenWords 를 서버에서 모두 받아왔는지 확인하는 flag. 신경 안써도 됨

    public static String getRealPathFromURI(final Context context, final Uri uri) {                         // 안드로이드의 파일 관련 정책이 자꾸 바뀌변서 추가된 펑션. 촬영한 사진의 URI 를 바탕으로 진짜 파일 경로를 얻어오는 펑션
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
                return SDcardpath + "/" + split[1];
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
            final String[] selectionArgs = new String[]{split[1]};

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

    public static String getPointCount(int nCount) {                                                                            // 1000 이 넘는 숫자는 1k 등으로 표시하기 위해 사용
        String strCount = "" + nCount;

        if (nCount >= 1000) {
            float fHitsCount = (float) nCount / 1000;
            strCount = String.format("%.1fK", fHitsCount);
        } else {
            strCount = comma(nCount);
        }

        return strCount;
    }

    public static void showProgressDialog(Context context, String strContents) {                                                // ProgressDialog 는 이걸 사용합니다. 리팩토링 예정(ProgressDialog 가 Deprecated 되었음;;)
        if (context == null)
            return;

        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing())
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

    public static Toast makeText(Context context, String text, int time) {                                                                      // 토스트 연속으로 띄울때 딜레이 없이 사용
        if (toast != null)
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
        final String[] projection = {column};

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
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }

    public static void resetUserInfo(SharedPreferences pref) {                                                              //  로그인 실패 등의 동작에서 회원정보 삭제
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

    public static boolean bLocinCheck(SharedPreferences pref) {                                                                             // 기존에는 로그인하지 않고 앱을 사용했기 때문에 사용한 펑션. 현재는 무조건 로그인을 해야 하므로 의미없는 펑션
        String strUserID = pref.getString("USER_ID", "Guest");
        String strUserName = pref.getString("USER_NAME", "Guest");

        if (strUserID == null || strUserID.length() == 0 || strUserID.equals("Guest")) {
            return false;
        } else if (strUserName == null || strUserName.length() == 0 || strUserName.equals("Guest")) {
            return false;
        }

        return true;
    }

    public static String strGetTime(String strDate) {                                                                                       // 댓글 등의 시간을 표시할때 ~~전 으로 표기하기 위한 펑션
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

        if (duration < 120000) {          // 1분보다 적다면, 즉 초단위라면
            strDuration = "방금 전";
        } else if (duration < 60000 * 60) {          // 1시간 보다 적다면, 즉 분단위라면
            strDuration = "" + (duration / 60000) + "분 전";
        } else if (duration < 60000 * 60 * 24) {     // 24시간 보다 적다면, 즉 시간 단위라면
            strDuration = "" + (duration / (60000 * 60)) + "시간 전";
        } else if (duration < 60000 * 60 * 24 * 8) {     // 7일 이내라면
            strDuration = "" + (duration / (60000 * 60 * 24)) + "일 전";
        } else {
            strDuration = strDate.substring(0, 16);
        }

        return strDuration;
    }

    public static Bitmap getImageFromURL(String imageURL) {
        Bitmap imgBitmap = null;
        HttpURLConnection conn = null;
        BufferedInputStream bis = null;

        try {
            URL url = new URL(imageURL);
            conn = (HttpURLConnection) url.openConnection();
            conn.connect();

            int nSize = conn.getContentLength();
            bis = new BufferedInputStream(conn.getInputStream(), nSize);
            imgBitmap = BitmapFactory.decodeStream(bis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }

        return imgBitmap;
    }

    public static String comma(String num) {                                                            // String 으로 된 숫자로 콤마 찍기 위한 펑션
        int nNum = Integer.valueOf(num);
        DecimalFormat df = new DecimalFormat("#,##0");
        return df.format(nNum);
    }

    public static String comma(Long num) {                                                              // Long 으로 된 숫자로 콤마 찍기 위한 펑션
        DecimalFormat df = new DecimalFormat("#,##0");
        return df.format(num);
    }

    public static String comma(int num) {                                                               // int 로 된 숫자로 콤마 찍기 위한 펑션. 현재는 이것만 사용중
        DecimalFormat df = new DecimalFormat("#,##0");
        return df.format(num);
    }

    public static int getLevel(int nDonationCarrotCount) {                                              // 당근 갯수로 레벨 가져오기. 레벨 변동이 있을때는 이곳만 수정하면 됨
        /*
        lv1 : 0 ~ 500
        lv2 : 501 ~ 1000
        lv3 : 1001 ~ 2000
        lv4 : 2001 ~ 4000
        lv5 : 4001 ~ 8000
        lv6 : 8001 ~ 16000
        lv7 : 16001 ~ 32000
        lv8 : 32001 ~ 80000
        lv9 : 80001 ~ 300000
        lv10 : 300001 ~
         */
        int nLevel = 1;

        if (nDonationCarrotCount <= 500)
            nLevel = 1;
        else if (nDonationCarrotCount <= 1000)
            nLevel = 2;
        else if (nDonationCarrotCount <= 2000)
            nLevel = 3;
        else if (nDonationCarrotCount <= 4000)
            nLevel = 4;
        else if (nDonationCarrotCount <= 8000)
            nLevel = 5;
        else if (nDonationCarrotCount <= 16000)
            nLevel = 6;
        else if (nDonationCarrotCount <= 32000)
            nLevel = 7;
        else if (nDonationCarrotCount <= 80000)
            nLevel = 8;
        else if (nDonationCarrotCount <= 300000)
            nLevel = 9;
        else
            nLevel = 10;


        return nLevel;
    }

    public static Bitmap getVideoThumbnail(Uri uri) {                                                                       // Video URL 로 동영상의 썸네일 추출. 추출한 이미지는 Viewer 등의 화면에서 따로 배열에 저장하여 캐시 처럼 사용해야 재로딩 없이 사용할 수 있음
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

    public static ArrayList<String> getForbiddenWords() {                                                                   // 서버에서 비속어 필터링할 목록 가져오기. 한번 받아왔으면 그대로 리턴하도록 함
        isComplete = false;

        if (forbiddenWords == null || forbiddenWords.size() == 0) {
            while (true) {
                if (isComplete)
                    break;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String strVersion = HttpClient.getStoreVersion(new OkHttpClient());
                        if (strVersion != null) {

                        }

                        isComplete = HttpClient.getforbiddenWordsFromServer(new OkHttpClient());
                    }
                }).start();

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return forbiddenWords;
            // 통신
        } else {
            return forbiddenWords;
        }
    }

    public static String checkForbiddenWords(String strKeyword) {
        ArrayList<String> forbiddenList = getForbiddenWords();

        for (String strForbiddenWord : forbiddenList) {
            if (strKeyword.contains(strForbiddenWord))
                return strForbiddenWord;
        }

        return "";
    }

    public static boolean getNetworkState(Context context) {
        boolean isAvailableNetwork = true;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        //if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) isAvailableNetwork = true; // WIFI에 연결됨
        //if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) isAvailableNetwork = true; // LTE(이동통신망)에 연결됨
        if (networkInfo == null || !networkInfo.isConnected())
            isAvailableNetwork = false; // 연결되지않음

        return isAvailableNetwork;
    }

    public static File from(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        String fileName = getFileName(context, uri);
        String[] splitName = splitFileName(fileName);
        File tempFile = File.createTempFile(splitName[0], splitName[1]);
        tempFile = rename(tempFile, fileName);
        tempFile.deleteOnExit();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(tempFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (inputStream != null) {
            copy(inputStream, out);
            inputStream.close();
        }

        if (out != null) {
            out.close();
        }
        return tempFile;
    }

    private static String[] splitFileName(String fileName) {
        String name = fileName;
        String extension = "";
        int i = fileName.lastIndexOf(".");
        if (i != -1) {
            name = fileName.substring(0, i);
            extension = fileName.substring(i);
        }

        return new String[]{name, extension};
    }

    private static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf(File.separator);
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private static File rename(File file, String newName) {
        File newFile = new File(file.getParent(), newName);
        if (!newFile.equals(file)) {
            if (newFile.exists() && newFile.delete()) {
                Log.d("FileUtil", "Delete old " + newName + " file");
            }
            if (file.renameTo(newFile)) {
                Log.d("FileUtil", "Rename file to " + newName);
            }
        }
        return newFile;
    }

    private static long copy(InputStream input, OutputStream output) throws IOException {
        long count = 0;
        int n;
        byte[] buffer = new byte[1024 * 4];
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
}
