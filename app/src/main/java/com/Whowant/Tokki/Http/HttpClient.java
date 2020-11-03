package com.Whowant.Tokki.Http;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.Whowant.Tokki.UI.Popup.CommonPopup;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.AlarmVO;
import com.Whowant.Tokki.VO.BillingLogVO;
import com.Whowant.Tokki.VO.CarrotVO;
import com.Whowant.Tokki.VO.CharacterVO;
import com.Whowant.Tokki.VO.ChatVO;
import com.Whowant.Tokki.VO.CommentVO;
import com.Whowant.Tokki.VO.ContestVO;
import com.Whowant.Tokki.VO.EpisodeVO;
import com.Whowant.Tokki.VO.EventVO;
import com.Whowant.Tokki.VO.MainCardVO;
import com.Whowant.Tokki.VO.NoticeVO;
import com.Whowant.Tokki.VO.UserInfoVO;
import com.Whowant.Tokki.VO.WaitingVO;
import com.Whowant.Tokki.VO.WorkVO;
import com.Whowant.Tokki.VO.WriterVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpClient {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static ArrayList<WorkVO> GetAllWorkList(OkHttpClient httpClient) {                              // 모든 작품 목록 가져오기
        ArrayList<WorkVO> resultList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetAllWorkList")
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);
            JSONArray resultArray = resultObject.getJSONArray("WORK_LIST");

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject object = resultArray.getJSONObject(i);

                WorkVO workVO = new WorkVO();
                workVO.setWorkID(object.getInt("WORK_ID"));
                workVO.setCreatedDate(object.getString("CREATED_DATE"));
                workVO.setStrSynopsis(object.getString("WORK_SYNOPSIS"));
                workVO.setWriteID(object.getString("WRITER_ID"));
                workVO.setStrWriterName(object.getString("WRITER_NAME"));
                workVO.setTitle(object.getString("WORK_TITLE"));
                workVO.setCoverFile(object.getString("WORK_COVER_IMG"));
                workVO.setbDistractor(object.getString("DISTRACTOR").equals("Y") ? true : false);
                workVO.setnTarget(object.getInt("TARGET"));

                resultList.add(workVO);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    public static ArrayList<CarrotVO> getUsedCarrotList(OkHttpClient httpClient, String strUserID) {
        ArrayList<CarrotVO> resultList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetUsedCarrot&USER_ID=" + strUserID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            JSONArray resultArray = resultObject.getJSONArray("USED_CARROT");
            int nTotalPoint = 0;

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject object = resultArray.getJSONObject(i);

                int nType = object.getInt("TYPE");
                if (nType != 0)
                    continue;

                CarrotVO vo = new CarrotVO();
                vo.setId(object.getLong("ID"));
                vo.setUserId(object.getString("USER_ID"));
                nTotalPoint += object.getInt("CARROT_POINT");
                vo.setCarrotPoint(object.getInt("CARROT_POINT"));
                vo.setUseDate(object.getString("USE_DATE"));
                vo.setType(object.getInt("TYPE"));
                vo.setDonationFrom(object.getString("DONATION_WORK_ID"));
                vo.setDonationFrom(object.getString("DONATION_FROM"));
                vo.setDonationWorkTitle(object.getString("WORK_TITLE"));
                vo.setWriterName(object.getString("WRITER_NAME"));
                vo.setDonationName(object.getString("DONATION_NAME"));

                resultList.add(vo);
            }

            if (resultList.size() > 0) {
                CarrotVO vo = resultList.get(0);
                vo.setnTotalPoint(nTotalPoint);
                resultList.set(0, vo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    public static ArrayList<CarrotVO> getTotalCarrotList(OkHttpClient httpClient, String strUserID) {
        ArrayList<CarrotVO> resultList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetUsedCarrot&USER_ID=" + strUserID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            JSONArray resultArray = resultObject.getJSONArray("USED_CARROT");
            int nTotalPoint = 0;

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject object = resultArray.getJSONObject(i);

                int nType = object.getInt("TYPE");
                if (nType == 0)
                    continue;

                CarrotVO vo = new CarrotVO();
                vo.setId(object.getLong("ID"));
                vo.setUserId(object.getString("USER_ID"));
                nTotalPoint += object.getInt("CARROT_POINT");
                vo.setCarrotPoint(object.getInt("CARROT_POINT"));
                vo.setUseDate(object.getString("USE_DATE"));
                vo.setType(object.getInt("TYPE"));
                vo.setDonationWorkId(object.getLong("DONATION_WORK_ID"));
                vo.setDonationFrom(object.getString("DONATION_FROM"));
                vo.setDonationWorkTitle(object.getString("WORK_TITLE"));
                vo.setWriterName(object.getString("WRITER_NAME"));
                vo.setDonationName(object.getString("DONATION_NAME"));
                vo.setDotaionEpisodeOrder(object.getInt("DONATION_EPISODE_ORDER"));

                resultList.add(vo);
            }

            if (resultList.size() > 0) {
                CarrotVO vo = resultList.get(0);
                vo.setnTotalPoint(nTotalPoint);
                resultList.set(0, vo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    public static boolean requestAttendance(OkHttpClient httpClient, String strUserID) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=RequestCheckAttendance&USER_ID=" + strUserID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return false;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            if (resultObject.getString("RESULT").equals("SUCCESS"))
                return true;
            else
                return false;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static ArrayList<WorkVO> GetAllWorkListWithWriterID(OkHttpClient httpClient, String strUserID) {                              // 모든 작품 목록 가져오기
        ArrayList<WorkVO> resultList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetAllWorkWithWriterID&USER_ID=" + strUserID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            JSONArray resultArray = resultObject.getJSONArray("WORK_LIST");

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject object = resultArray.getJSONObject(i);

                WorkVO workVO = new WorkVO();
                workVO.setWorkID(object.getInt("WORK_ID"));
                workVO.setCreatedDate(object.getString("CREATED_DATE"));
                workVO.setStrSynopsis(object.getString("WORK_SYNOPSIS"));
                workVO.setWriteID(object.getString("WRITER_ID"));
                workVO.setStrWriterName(object.getString("WRITER_NAME"));
                workVO.setTitle(object.getString("WORK_TITLE"));
                workVO.setCoverFile(object.getString("WORK_COVER_IMG"));
                workVO.setbDistractor(object.getString("DISTRACTOR").equals("Y") ? true : false);
                workVO.setnTarget(object.getInt("TARGET"));

                resultList.add(workVO);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    public static ArrayList<WorkVO> GetAllWorkListWithID(OkHttpClient httpClient, String strUserID) {                              // 모든 작품 목록 가져오기
        ArrayList<WorkVO> resultList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetAllWorkWithID&USER_ID=" + strUserID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            JSONArray resultArray = resultObject.getJSONArray("WORK_LIST");

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject object = resultArray.getJSONObject(i);

                WorkVO workVO = new WorkVO();
                workVO.setWorkID(object.getInt("WORK_ID"));
                workVO.setCreatedDate(object.getString("CREATED_DATE"));
                workVO.setStrSynopsis(object.getString("WORK_SYNOPSIS"));
                workVO.setWriteID(object.getString("WRITER_ID"));
                workVO.setStrWriterName(object.getString("WRITER_NAME"));
                workVO.setTitle(object.getString("WORK_TITLE"));
                workVO.setCoverFile(object.getString("WORK_COVER_IMG"));
                workVO.setbDistractor(object.getString("DISTRACTOR").equals("Y") ? true : false);
                workVO.setnTarget(object.getInt("TARGET"));

                resultList.add(workVO);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    public static ArrayList<WorkVO> GetAllWorkListWithIDForContest(OkHttpClient httpClient, String strUserID) {                              // 모든 작품 목록 가져오기
        ArrayList<WorkVO> resultList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetAllWorkWithIDForContest&USER_ID=" + strUserID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            JSONArray resultArray = resultObject.getJSONArray("WORK_LIST");

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject object = resultArray.getJSONObject(i);

                WorkVO workVO = new WorkVO();
                workVO.setWorkID(object.getInt("WORK_ID"));
                workVO.setCreatedDate(object.getString("CREATED_DATE"));
                workVO.setStrSynopsis(object.getString("WORK_SYNOPSIS"));
                workVO.setWriteID(object.getString("WRITER_ID"));
                workVO.setStrWriterName(object.getString("WRITER_NAME"));
                workVO.setTitle(object.getString("WORK_TITLE"));
                workVO.setCoverFile(object.getString("WORK_COVER_IMG"));
                workVO.setbDistractor(object.getString("DISTRACTOR").equals("Y") ? true : false);
                workVO.setnTarget(object.getInt("TARGET"));

                resultList.add(workVO);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    public static ArrayList<ContestVO> getContestList(OkHttpClient httpClient) {
        ArrayList<ContestVO> resultList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetAllContestList")
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            JSONArray resultArray = resultObject.getJSONArray("CONTEST_LIST");

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject object = resultArray.getJSONObject(i);
                ContestVO vo = new ContestVO();
                vo.setContestID(object.getInt("ID"));
                vo.setWorkID(object.getInt("WORK_ID"));
                vo.setStrTitle(object.getString("TITLE"));
                vo.setUserName(object.getString("USER_NAME"));
                vo.setUserID(object.getString("USER_ID"));
                vo.setUserBirthday(object.getString("USER_BIRTHDAY"));
                vo.setEmail(object.getString("EMAIL"));
                vo.setCareer(object.getString("CAREER").equals("Y") ? true : false);
                vo.setCharacterInfo(object.getString("CHARACTER_INFO"));
                vo.setSynopsis(object.getString("SYNOPSIS"));
                vo.setContestCode(object.getString("CONTEST_CODE"));
                vo.setRegisterDate(object.getString("REGISTER_DATE"));
                vo.setStrPhoneNum(object.getString("PHONE_NUMBER"));
                resultList.add(vo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    public static boolean requestUploadContest(OkHttpClient httpClient, HashMap<String, String> contestInfo) {
        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("USER_ID", contestInfo.get("USER_ID"));
            jsonBody.put("USER_NAME", contestInfo.get("USER_NAME"));
            jsonBody.put("USER_BIRTHDAY", contestInfo.get("USER_BIRTHDAY"));
            jsonBody.put("USER_PHONENUM", contestInfo.get("USER_PHONENUM"));
            jsonBody.put("EMAIL", contestInfo.get("EMAIL"));
            jsonBody.put("CAREER", contestInfo.get("CAREER"));
            jsonBody.put("WORK_TITLE", contestInfo.get("WORK_TITLE"));
            jsonBody.put("CHARACTER_INFO", contestInfo.get("CHARACTER_INFO"));
            jsonBody.put("SYNOPSIS", contestInfo.get("SYNOPSIS"));
            jsonBody.put("WORK_ID", contestInfo.get("WORK_ID"));
            String jsonString = jsonBody.toString();

            RequestBody requestBody = RequestBody.create(JSON, jsonString);

            Request request = new Request.Builder()
                    .url(CommonUtils.strDefaultUrl + "ContestUpload.jsp")
                    .post(requestBody)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.code() != 200)
                    return false;

                String strResult = response.body().string();
                JSONObject resultJsonObject = new JSONObject(strResult);

                if (resultJsonObject.getString("RESULT").equals("SUCCESS"))
                    return true;
                else
                    return false;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static ArrayList<WorkVO> getSearchWorkList(OkHttpClient httpClient, String strKeyword, int nMode) {                              // 모든 작품 목록 가져오기
        ArrayList<WorkVO> resultList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetSearchWorkList&SEARCH_KEYWORD=" + strKeyword + "&MODE=" + nMode)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            JSONArray resultArray = resultObject.getJSONArray("WORK_LIST");

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject object = resultArray.getJSONObject(i);

                WorkVO workVO = new WorkVO();
                workVO.setWorkID(object.getInt("WORK_ID"));
                workVO.setCreatedDate(object.getString("CREATED_DATE"));
                workVO.setStrSynopsis(object.getString("WORK_SYNOPSIS"));
                workVO.setWriteID(object.getString("WRITER_ID"));
                workVO.setStrWriterName(object.getString("WRITER_NAME"));
                workVO.setTitle(object.getString("WORK_TITLE"));
                workVO.setCoverFile(object.getString("WORK_COVER_IMG"));
                workVO.setfStarPoint((float) object.getDouble("STAR_POINT"));
                workVO.setbDistractor(object.getString("DISTRACTOR").equals("Y") ? true : false);
                workVO.setnTarget(object.getInt("TARGET"));
                workVO.setnTapCount(object.getInt("TAB_COUNT"));
                workVO.setnHitsCount(object.getInt("HITS_COUNT"));
                workVO.setnCommentCount(object.getInt("COMMENT_COUNT"));

                resultList.add(workVO);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    public static int getInteractionEpisodeID(OkHttpClient httpClient, int nWorkID) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetInteractionEpisodeID&WORK_ID=" + nWorkID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return 0;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);

            if (resultJsonObject.getString("RESULT").equals("SUCCESS"))
                return resultJsonObject.getInt("EPISODE_ID");
            else
                return 0;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static ArrayList<WorkVO> getKeepWorkList(OkHttpClient httpClient, String strUserID) {                              // 모든 작품 목록 가져오기
        ArrayList<WorkVO> resultList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetKeepWorkList&USER_ID=" + strUserID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            JSONArray resultArray = resultObject.getJSONArray("WORK_LIST");

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject object = resultArray.getJSONObject(i);

                WorkVO workVO = new WorkVO();
                workVO.setWorkID(object.getInt("WORK_ID"));
                workVO.setCreatedDate(object.getString("CREATED_DATE"));
                workVO.setStrSynopsis(object.getString("WORK_SYNOPSIS"));
                workVO.setWriteID(object.getString("WRITER_ID"));
                workVO.setStrWriterName(object.getString("WRITER_NAME"));
                workVO.setTitle(object.getString("WORK_TITLE"));
                workVO.setCoverFile(object.getString("WORK_COVER_IMG"));
                workVO.setbDistractor(object.getString("DISTRACTOR").equals("Y") ? true : false);
                workVO.setnTarget(object.getInt("TARGET"));
                workVO.setnHitsCount(object.getInt("HITS_COUNT"));
                workVO.setnTapCount(object.getInt("TAB_COUNT"));
                workVO.setfStarPoint((float) object.getDouble("STAR_POINT"));
                workVO.setnKeepcount(object.getInt("KEEP_COUNT"));
                workVO.setnCommentCount(object.getInt("COMMENT_COUNT"));
                resultList.add(workVO);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    public static ArrayList<WorkVO> getKeepWorkList(OkHttpClient httpClient, String strUserID, String strOrder) {                              // 모든 작품 목록 가져오기
        ArrayList<WorkVO> resultList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetKeepWorkList&USER_ID=" + strUserID + "&ORDER=" + strOrder)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            JSONArray resultArray = resultObject.getJSONArray("WORK_LIST");

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject object = resultArray.getJSONObject(i);

                WorkVO workVO = new WorkVO();
                workVO.setWorkID(object.getInt("WORK_ID"));
                workVO.setCreatedDate(object.getString("CREATED_DATE"));
                workVO.setStrSynopsis(object.getString("WORK_SYNOPSIS"));
                workVO.setWriteID(object.getString("WRITER_ID"));
                workVO.setStrWriterName(object.getString("WRITER_NAME"));
                workVO.setTitle(object.getString("WORK_TITLE"));
                workVO.setCoverFile(object.getString("WORK_COVER_IMG"));
                workVO.setbDistractor(object.getString("DISTRACTOR").equals("Y") ? true : false);
                workVO.setnTarget(object.getInt("TARGET"));
                workVO.setnHitsCount(object.getInt("HITS_COUNT"));
                workVO.setnTapCount(object.getInt("TAB_COUNT"));
                workVO.setfStarPoint((float) object.getDouble("STAR_POINT"));
                workVO.setnKeepcount(object.getInt("KEEP_COUNT"));
                workVO.setnCommentCount(object.getInt("COMMENT_COUNT"));
                resultList.add(workVO);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    public static ArrayList<WorkVO> getReadWorkList(OkHttpClient httpClient, String strUserID, String strOrder) {                              // 모든 작품 목록 가져오기
        ArrayList<WorkVO> resultList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetMyReadWorkList&USER_ID=" + strUserID + "&ORDER=" + strOrder)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            JSONArray resultArray = resultObject.getJSONArray("WORK_LIST");

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject object = resultArray.getJSONObject(i);

                WorkVO workVO = new WorkVO();

                if (object.getString("WORK_ID") == null || object.getInt("WORK_ID") == 0)
                    continue;

                workVO.setWorkID(object.getInt("WORK_ID"));
                workVO.setCreatedDate(object.getString("CREATED_DATE"));
                workVO.setStrSynopsis(object.getString("WORK_SYNOPSIS"));
                workVO.setWriteID(object.getString("WRITER_ID"));
                workVO.setStrWriterName(object.getString("WRITER_NAME"));
                workVO.setTitle(object.getString("WORK_TITLE"));
                workVO.setCoverFile(object.getString("WORK_COVER_IMG"));
                workVO.setbDistractor(object.getString("DISTRACTOR").equals("Y") ? true : false);
                workVO.setnTarget(object.getInt("TARGET"));
                workVO.setnHitsCount(object.getInt("HITS_COUNT"));
                workVO.setnTapCount(object.getInt("TAB_COUNT"));
                workVO.setfStarPoint((float) object.getDouble("STAR_POINT"));
                workVO.setnKeepcount(object.getInt("KEEP_COUNT"));
                workVO.setnCommentCount(object.getInt("COMMENT_COUNT"));
                resultList.add(workVO);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    public static void sendTap(OkHttpClient httpClient, int nEpisodeID) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=SendTap&EPISODE_ID=" + nEpisodeID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendHitsWork(OkHttpClient httpClient, int strWorkID, int strEpisodeID, String strUserID, int nLastOrder) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=SendHitsWork&WORK_ID=" + strWorkID + "&EPISODE_ID=" + strEpisodeID + "&USER_ID=" + strUserID + "&LAST_ORDER=" + nLastOrder)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getAllGenreList(OkHttpClient httpClient) {
        ArrayList<String> resultList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetGenreList")
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            JSONArray resultArray = resultObject.getJSONArray("GENRE_LIST");

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject object = resultArray.getJSONObject(i);

                resultList.add(object.getString("GENRE_NAME"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            resultList = null;
        } catch (JSONException e) {
            e.printStackTrace();
            resultList = null;
        }

        return resultList;
    }

    public static ArrayList<String> getAllTagList(OkHttpClient httpClient) {
        ArrayList<String> resultList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetTagList")
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            JSONArray resultArray = resultObject.getJSONArray("TAG_LIST");

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject object = resultArray.getJSONObject(i);

                resultList.add(object.getString("TAG_TITLE"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            resultList = null;
        } catch (JSONException e) {
            e.printStackTrace();
            resultList = null;
        }

        return resultList;
    }

    public static ArrayList<String> getGalleryData(OkHttpClient httpClient) {
        ArrayList<String> resultList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "SeesoGallery.jsp")
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            JSONArray resultArray = resultObject.getJSONArray("FILE_LIST");

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject object = resultArray.getJSONObject(i);
                resultList.add(object.getString("FILE_NAME"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            resultList = null;
        } catch (JSONException e) {
            e.printStackTrace();
            resultList = null;
        }

        return resultList;
    }

    public static ArrayList<EventVO> getEventList(OkHttpClient httpClient, Context context) {
        ArrayList<EventVO> resultList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanApp.jsp?CMD=GetEventList")
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            JSONArray resultArray = resultObject.getJSONArray("EVENT_LIST");

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject object = resultArray.getJSONObject(i);

                EventVO vo = new EventVO();
                vo.setnEventID(object.getInt("EVENT_ID"));
                vo.setStrUserID(object.getString("USER_ID"));
                vo.setStrUserName(object.getString("USER_NAME"));
                vo.setStrEventTitle(object.getString("EVENT_TITLE"));
                vo.setStrEventContentsFile(object.getString("EVENT_CONTENTS_FILE"));
                vo.setStrEventPopupFile(object.getString("EVENT_POPUP_FILE"));
                vo.setStrRegisterDate(object.getString("REGISTER_DATE"));

                if(object.getInt("EVENT_TYPE") == 20)
                    continue;

                vo.setnEventType(object.getInt("EVENT_TYPE"));

                SharedPreferences pref = context.getSharedPreferences("EVENT_INFO", Activity.MODE_PRIVATE);
                vo.setbRead(pref.getBoolean("" + vo.getnEventID(), false));

                resultList.add(vo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    public static ArrayList<NoticeVO> getNoticeList(OkHttpClient httpClient, Context context) {
        ArrayList<NoticeVO> resultList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanApp.jsp?CMD=GetNoticeList")
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            JSONArray resultArray = resultObject.getJSONArray("NOTICE_LIST");

            SharedPreferences pref = context.getSharedPreferences("NOTICE_INFO", Activity.MODE_PRIVATE);

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject object = resultArray.getJSONObject(i);

                NoticeVO vo = new NoticeVO();
                vo.setnNoticeID(object.getInt("NOTICE_ID"));
                vo.setStrUserID(object.getString("USER_ID"));
                vo.setStrUserName(object.getString("USER_NAME"));
                vo.setStrNoticeTitle(object.getString("NOTICE_TITLE"));
                vo.setStrNoticeContents(object.getString("NOTICE_CONTENTS"));
                vo.setStrRegisterDate(object.getString("REGISTER_DATE"));
                vo.setbExpand(false);

                if (pref.getBoolean("" + vo.getnNoticeID(), false))
                    vo.setbRead(true);
                else
                    vo.setbRead(false);

                resultList.add(vo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    public static ArrayList<CommentVO> getReportsList(OkHttpClient httpClient) {
        ArrayList<CommentVO> resultList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetReportsList")
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            JSONArray resultArray = resultObject.getJSONArray("REPORTS_LIST");

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject object = resultArray.getJSONObject(i);

                CommentVO vo = new CommentVO();

                vo.setCommentID(object.getInt("COMMENT_ID"));
                vo.setStrComment(object.getString("COMMENT"));
                vo.setRegisterDate(object.getString("COMMENT_DATE"));
                vo.setUserName(object.getString("COMMENT_USER_NAME"));
                vo.setUserPhoto(object.getString("COMMENT_USER_PHOTO"));
                vo.setLikeCount(object.getInt("LIKE_COUNT"));
                vo.setnReportID(object.getInt("REPORT_ID"));
                vo.setStrRepotUserName(object.getString("REPORT_USER_NAME"));
                vo.setStrReportReson(object.getString("REPORT_REASON"));
                vo.setStrReportDate(object.getString("REPORT_DATE"));
                vo.setnCount(object.getInt("REPORT_COUNT"));

                if (vo.getStrComment() == null || vo.getStrComment().equals("null"))
                    continue;

                resultList.add(vo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    public static ArrayList<CommentVO> getReportsListByCommentID(OkHttpClient httpClient, int nCommentID) {
        ArrayList<CommentVO> resultList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetReportsListByCommentID&COMMENT_ID=" + nCommentID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            JSONArray resultArray = resultObject.getJSONArray("REPORTS_LIST");

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject object = resultArray.getJSONObject(i);

                CommentVO vo = new CommentVO();

                vo.setCommentID(object.getInt("COMMENT_ID"));
                vo.setStrComment(object.getString("COMMENT"));
                vo.setRegisterDate(object.getString("COMMENT_DATE"));
                vo.setUserName(object.getString("COMMENT_USER_NAME"));
                vo.setUserPhoto(object.getString("COMMENT_USER_PHOTO"));
                vo.setLikeCount(object.getInt("LIKE_COUNT"));
                vo.setnReportID(object.getInt("REPORT_ID"));
                vo.setStrRepotUserName(object.getString("REPORT_USER_NAME"));
                vo.setStrReportReson(object.getString("REPORT_REASON"));
                vo.setStrReportDate(object.getString("REPORT_DATE"));

                if (vo.getStrComment() == null || vo.getStrComment().equals("null"))
                    continue;

                resultList.add(vo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    public static ArrayList<UserInfoVO> getAllUserListByAdmin(OkHttpClient httpClient) {
        ArrayList<UserInfoVO> resultList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanBookAdmin.jsp?CMD=GetAllUserList")
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            JSONArray resultArray = resultObject.getJSONArray("USER_LIST");

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject object = resultArray.getJSONObject(i);

                UserInfoVO vo = new UserInfoVO();
                vo.setStrUserID(object.getString("USER_ID"));
                vo.setStrUserName(object.getString("USER_NAME"));
                vo.setStrUserEmail(object.getString("USER_EMAIL"));
                vo.setStrUserPhoto(object.getString("USER_PHOTO"));
                vo.setnWorkCount(object.getInt("WORK_COUNT"));
                vo.setnCommentCount(object.getInt("COMMENT_COUNT"));
                vo.setnFollowerCount(object.getInt("FOLLOW_COUNT"));
                vo.setnAccumCarrot(object.getInt("CARROT_ACCUMULATION"));
                vo.setnDonationCarrot(object.getInt("CARROT_DONATION"));
                vo.setnPurchaseCarrot(object.getInt("CARROT_PURCHASE"));

                resultList.add(vo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultList;
    }

//    public static ArrayList<WaitingVO> getSearchWaitingListByAdmin(OkHttpClient httpClient, String strKeyword, String strAdminID) {
//        ArrayList<WaitingVO> resultList = new ArrayList<>();
//
//        Request request = new Request.Builder()
//                .url(CommonUtils.strDefaultUrl + "PanBookAdmin.jsp?CMD=GetSearchWaitingWorkList&KEY_WORD=" + strKeyword + "&USER_ID=" + strAdminID)
//                .get()
//                .build();
//
//        try (Response response = httpClient.newCall(request).execute()) {
//            if(response.code() != 200)
//                return null;
//
//            String strResult = response.body().string();
//            JSONObject resultObject = new JSONObject(strResult);
//
//            JSONArray waitingArr = resultObject.getJSONArray("WAITING_LIST");
//
//            for(int i = 0 ; i < waitingArr.length() ; i++) {
//                JSONObject object = waitingArr.getJSONObject(i);
//                WaitingVO vo = new WaitingVO();
//                vo.setnWorkID(object.getInt("WORK_ID"));
//                vo.setStrWorkTitle(object.getString("WORK_TITLE"));
//                vo.setWriterName(object.getString("WRITER_NAME"));
//                vo.setnEpisodeID(object.getInt("EPISODE_ID"));
//                vo.setStrEpisodeTitle(object.getString("EPISODE_TITLE"));
//                vo.setStrCratedDate(object.getString("CREATED_DATE"));
//                vo.setStrCoverImg(object.getString("COVER_IMG"));
//
//                resultList.add(vo);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            resultList = null;
//        } catch (JSONException e) {
//            e.printStackTrace();
//            resultList = null;
//        }
//
//        return resultList;
//    }

    public static ArrayList<WaitingVO> getSearchWaitingListByAdmin(OkHttpClient httpClient, String strKeyword, String strUserID) {
        ArrayList<WaitingVO> resultList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanBookAdmin.jsp?CMD=GetSearchWaitingWorkList&KEY_WORD=" + strKeyword + "&USER_ID=" + strUserID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            JSONArray resultArray = resultObject.getJSONArray("WAITING_LIST");

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject object = resultArray.getJSONObject(i);
                WaitingVO vo = new WaitingVO();
                vo.setnWorkID(object.getInt("WORK_ID"));
                vo.setStrWorkTitle(object.getString("WORK_TITLE"));
                vo.setWriterName(object.getString("WRITER_NAME"));
                vo.setnEpisodeID(object.getInt("EPISODE_ID"));
                vo.setStrEpisodeTitle(object.getString("EPISODE_TITLE"));
                vo.setStrCratedDate(object.getString("CREATED_DATE"));
                vo.setStrCoverImg(object.getString("COVER_IMG"));
                vo.setnEpisodeOrder(object.getInt("EPISODE_ORDER"));
                vo.setStrSynopsis(object.getString("WORK_SYNOPSIS"));

                resultList.add(vo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    public static ArrayList<UserInfoVO> getSearchUserListByAdmin(OkHttpClient httpClient, String strKeyword) {
        ArrayList<UserInfoVO> resultList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanBookAdmin.jsp?CMD=GetSearchUserList&KEYWORD=" + strKeyword)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            JSONArray resultArray = resultObject.getJSONArray("USER_LIST");

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject object = resultArray.getJSONObject(i);

                UserInfoVO vo = new UserInfoVO();
                vo.setStrUserID(object.getString("USER_ID"));
                vo.setStrUserName(object.getString("USER_NAME"));
                vo.setStrUserEmail(object.getString("USER_EMAIL"));
                vo.setStrUserPhoto(object.getString("USER_PHOTO"));
                vo.setnWorkCount(object.getInt("WORK_COUNT"));
                vo.setnCommentCount(object.getInt("COMMENT_COUNT"));
                vo.setnFollowerCount(object.getInt("FOLLOW_COUNT"));
                vo.setnAccumCarrot(object.getInt("CARROT_ACCUMULATION"));
                vo.setnDonationCarrot(object.getInt("CARROT_DONATION"));
                vo.setnPurchaseCarrot(object.getInt("CARROT_PURCHASE"));

                resultList.add(vo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    public static ArrayList<WorkVO> getBestRankingList(OkHttpClient httpClient) {                              // 모든 작품 목록 가져오기
        ArrayList<WorkVO> resultList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanbookGetRanking.jsp?CMD=GetBestRanking")
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            JSONArray resultArray = resultObject.getJSONArray("WORK_LIST");

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject object = resultArray.getJSONObject(i);

                WorkVO workVO = new WorkVO();
                workVO.setWorkID(object.getInt("WORK_ID"));
                workVO.setCreatedDate(object.getString("CREATED_DATE"));
                workVO.setStrSynopsis(object.getString("WORK_SYNOPSIS"));
                workVO.setWriteID(object.getString("WRITER_ID"));
                workVO.setStrWriterName(object.getString("WRITER_NAME"));
                workVO.setTitle(object.getString("WORK_TITLE"));
                workVO.setCoverFile(object.getString("COVER_IMG"));
                workVO.setnHitsCount(object.getInt("HITS_COUNT"));
                workVO.setnTapCount(object.getInt("TAB_COUNT"));
                workVO.setfStarPoint((float) object.getDouble("STAR_POINT"));
                workVO.setnKeepcount(object.getInt("KEEP_COUNT"));
                workVO.setnCommentCount(object.getInt("COMMENT_COUNT"));
                workVO.setStrThumbFile(object.getString("WORK_COVER_THUMBNAIL"));
                workVO.setbPosterThumbnail(object.getString("POSTER_THUMB_YN").equals("Y") ? true : false);
                workVO.setbDistractor(object.getString("DISTRACTOR").equals("Y") ? true : false);
                workVO.setnTarget(object.getInt("TARGET"));

                resultList.add(workVO);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    public static ArrayList<WorkVO> getGenreRankingList(OkHttpClient httpClient, String strGenreID) {                              // 모든 작품 목록 가져오기
        ArrayList<WorkVO> resultList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanbookGetRanking.jsp?CMD=GetGenreRanking&GENRE_ID=" + strGenreID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            JSONArray resultArray = resultObject.getJSONArray("WORK_LIST");

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject object = resultArray.getJSONObject(i);

                WorkVO workVO = new WorkVO();
                workVO.setWorkID(object.getInt("WORK_ID"));
                workVO.setCreatedDate(object.getString("CREATED_DATE"));
                workVO.setStrSynopsis(object.getString("WORK_SYNOPSIS"));
                workVO.setWriteID(object.getString("WRITER_ID"));
                workVO.setStrWriterName(object.getString("WRITER_NAME"));
                workVO.setTitle(object.getString("WORK_TITLE"));
                workVO.setCoverFile(object.getString("COVER_IMG"));
                workVO.setnHitsCount(object.getInt("HITS_COUNT"));
                workVO.setnTapCount(object.getInt("TAB_COUNT"));
                workVO.setfStarPoint((float) object.getDouble("STAR_POINT"));
                workVO.setnKeepcount(object.getInt("KEEP_COUNT"));
                workVO.setnCommentCount(object.getInt("COMMENT_COUNT"));
                workVO.setStrThumbFile(object.getString("WORK_COVER_THUMBNAIL"));
                workVO.setbPosterThumbnail(object.getString("POSTER_THUMB_YN").equals("Y") ? true : false);
                workVO.setbDistractor(object.getString("DISTRACTOR").equals("Y") ? true : false);
                workVO.setnTarget(object.getInt("TARGET"));

                resultList.add(workVO);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    public static ArrayList<WorkVO> getNewRankingList(OkHttpClient httpClient) {                              // 모든 작품 목록 가져오기
        ArrayList<WorkVO> resultList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanbookGetRanking.jsp?CMD=GetNewRanking")
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            JSONArray resultArray = resultObject.getJSONArray("WORK_LIST");

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject object = resultArray.getJSONObject(i);

                WorkVO workVO = new WorkVO();
                workVO.setWorkID(object.getInt("WORK_ID"));
                workVO.setCreatedDate(object.getString("CREATED_DATE"));
                workVO.setStrSynopsis(object.getString("WORK_SYNOPSIS"));
                workVO.setWriteID(object.getString("WRITER_ID"));
                workVO.setStrWriterName(object.getString("WRITER_NAME"));
                workVO.setTitle(object.getString("WORK_TITLE"));
                workVO.setCoverFile(object.getString("COVER_IMG"));
                workVO.setnHitsCount(object.getInt("HITS_COUNT"));
                workVO.setnTapCount(object.getInt("TAB_COUNT"));
                workVO.setnCommentCount(object.getInt("COMMENT_COUNT"));
                workVO.setfStarPoint((float) object.getDouble("STAR_POINT"));
                workVO.setnKeepcount(object.getInt("KEEP_COUNT"));
//                workVO.setStrThumbFile(object.getString("WORK_COVER_THUMBNAIL"));
//                workVO.setbPosterThumbnail(object.getString("POSTER_THUMB_YN").equals("Y") ? true : false);
                workVO.setbDistractor(object.getString("DISTRACTOR").equals("Y") ? true : false);
                workVO.setnTarget(object.getInt("TARGET"));

                resultList.add(workVO);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    public static ArrayList<WorkVO> getRecommandList(OkHttpClient httpClient) {                              // 모든 작품 목록 가져오기
        ArrayList<WorkVO> resultList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanbookGetRanking.jsp?CMD=GetRecommandRanking")
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            JSONArray resultArray = resultObject.getJSONArray("WORK_LIST");

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject object = resultArray.getJSONObject(i);

                WorkVO workVO = new WorkVO();
                workVO.setWorkID(object.getInt("WORK_ID"));
                workVO.setCreatedDate(object.getString("CREATED_DATE"));
                workVO.setStrUpdateDate(object.getString("UPDATE_DATE"));
                workVO.setStrSynopsis(object.getString("WORK_SYNOPSIS"));
                workVO.setWriteID(object.getString("WRITER_ID"));
                workVO.setStrWriterName(object.getString("WRITER_NAME"));
                workVO.setTitle(object.getString("WORK_TITLE"));
                workVO.setCoverFile(object.getString("COVER_IMG"));
                workVO.setnCommentCount(object.getInt("COMMENT_COUNT"));
                workVO.setnHitsCount(object.getInt("HITS_COUNT"));
                workVO.setnTapCount(object.getInt("TAB_COUNT"));
                workVO.setfStarPoint((float) object.getDouble("STAR_POINT"));
                workVO.setnKeepcount(object.getInt("KEEP_COUNT"));
                workVO.setStrThumbFile(object.getString("WORK_COVER_THUMBNAIL"));
                workVO.setbPosterThumbnail(object.getString("POSTER_THUMB_YN").equals("Y") ? true : false);
                workVO.setbDistractor(object.getString("DISTRACTOR").equals("Y") ? true : false);
                workVO.setnTarget(object.getInt("TARGET"));

                resultList.add(workVO);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    public static ArrayList<HashMap<String, String>> getGenreList(OkHttpClient httpClient) {
        //GetGenreList
        ArrayList<HashMap<String, String>> genreList = new ArrayList<>();

        HashMap<String, String> map = new HashMap<>();
        map.put("GENRE_ID", "0");
        map.put("GENRE_NAME", "전체");
        genreList.add(map);

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanApp.jsp?CMD=GetGenreList")
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            JSONArray genreJsonArray = resultObject.getJSONArray("GENRE_LIST");

            for (int i = 0; i < genreJsonArray.length(); i++) {
                JSONObject object = genreJsonArray.getJSONObject(i);
                map = new HashMap<>();
                map.put("GENRE_ID", "" + object.getInt("GENRE_ID"));
                map.put("GENRE_NAME", object.getString("GENRE_NAME"));
                genreList.add(map);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return genreList;
    }

    public static ArrayList<MainCardVO> getAllRankingList(OkHttpClient httpClient) {                              // 모든 작품 목록 가져오기
        ArrayList<MainCardVO> mainCardList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanbookGetRanking.jsp?CMD=GetAllRankingList")
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            JSONArray recommandsonArray = resultObject.getJSONArray("RECOMMAND");
            MainCardVO recommandVO = new MainCardVO();
            ArrayList<WorkVO> recommandWorkList = new ArrayList<>();
            recommandVO.setStrHeaderTitle("추천");
            recommandVO.setViewType(0);

            for (int i = 0; i < recommandsonArray.length(); i++) {
                JSONObject object = recommandsonArray.getJSONObject(i);

                WorkVO workVO = new WorkVO();
                workVO.setWorkID(object.getInt("WORK_ID"));
                workVO.setCreatedDate(object.getString("CREATED_DATE"));
                workVO.setStrSynopsis(object.getString("WORK_SYNOPSIS"));
                workVO.setWriteID(object.getString("WRITER_ID"));
                workVO.setStrWriterName(object.getString("WRITER_NAME"));
                workVO.setTitle(object.getString("WORK_TITLE"));
                workVO.setCoverFile(object.getString("COVER_IMG"));

                if (object.has("RECOMMEND_IMG") && object.getString("RECOMMEND_IMG").length() > 0 && !object.getString("RECOMMEND_IMG").equals("null"))
                    workVO.setCoverFile(object.getString("RECOMMEND_IMG"));

                workVO.setnHitsCount(object.getInt("HITS_COUNT"));
                workVO.setnTapCount(object.getInt("TAB_COUNT"));
                workVO.setfStarPoint((float) object.getDouble("STAR_POINT"));
                workVO.setnKeepcount(object.getInt("KEEP_COUNT"));
                workVO.setnCommentCount(object.getInt("COMMENT_COUNT"));
                workVO.setStrThumbFile(object.getString("WORK_COVER_THUMBNAIL"));
                workVO.setbPosterThumbnail(object.getString("POSTER_THUMB_YN").equals("Y") ? true : false);
                workVO.setbDistractor(object.getString("DISTRACTOR").equals("Y") ? true : false);
                workVO.setnTarget(object.getInt("TARGET"));

                recommandWorkList.add(workVO);
            }

            recommandVO.setAllItemInCard(recommandWorkList);
            mainCardList.add(recommandVO);

            // 2020.09.24 modified by sjy   //////////////////////////////////////
            String strEndDay = "20201001235959";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            Date endDate = null;

            try {
                endDate = sdf.parse(strEndDay);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Date toDay = new Date();
            Log.d("Date", "Today = " + toDay.getTime() + ", EndDay = " + endDate.getTime());

            if (toDay.getTime() <= endDate.getTime()) {
                WorkVO carrotEvent = new WorkVO();
                carrotEvent.setWorkID(-1);
                carrotEvent.setCoverFile("-1");
                recommandWorkList.add(0, carrotEvent);
                ////////  Modify End //////////////////////////////////////
            }
//            Calendar endDay = Calendar.getInstance();
//            endDay.setTime(endDate);
//            Calendar todayCal = Calendar.getInstance();
//            todayCal.setTime(toDay);


            JSONArray bestRankingJsonArray = resultObject.getJSONArray("BEST_RANKING");
            MainCardVO bestRankingVO = new MainCardVO();
            ArrayList<WorkVO> bestWorkList = new ArrayList<>();
            bestRankingVO.setStrHeaderTitle("인기작");
            bestRankingVO.setViewType(1);

            for (int i = 0; i < bestRankingJsonArray.length(); i++) {
                JSONObject object = bestRankingJsonArray.getJSONObject(i);

                WorkVO workVO = new WorkVO();
                workVO.setWorkID(object.getInt("WORK_ID"));
                workVO.setCreatedDate(object.getString("CREATED_DATE"));
                workVO.setStrSynopsis(object.getString("WORK_SYNOPSIS"));
                workVO.setWriteID(object.getString("WRITER_ID"));
                workVO.setStrWriterName(object.getString("WRITER_NAME"));
                workVO.setTitle(object.getString("WORK_TITLE"));
                workVO.setCoverFile(object.getString("COVER_IMG"));
                workVO.setnHitsCount(object.getInt("HITS_COUNT"));
                workVO.setnTapCount(object.getInt("TAB_COUNT"));
                workVO.setfStarPoint((float) object.getDouble("STAR_POINT"));
                workVO.setnKeepcount(object.getInt("KEEP_COUNT"));
                workVO.setnCommentCount(object.getInt("COMMENT_COUNT"));
                workVO.setStrThumbFile(object.getString("WORK_COVER_THUMBNAIL"));
                workVO.setbPosterThumbnail(object.getString("POSTER_THUMB_YN").equals("Y") ? true : false);
                workVO.setbDistractor(object.getString("DISTRACTOR").equals("Y") ? true : false);
                workVO.setnTarget(object.getInt("TARGET"));

                bestWorkList.add(workVO);
            }

            bestRankingVO.setAllItemInCard(bestWorkList);
            mainCardList.add(bestRankingVO);

//            JSONArray genreRankingJsonArray = resultObject.getJSONArray("GENRE_RANKING");
//            MainCardVO genreRankingVO = new MainCardVO();
//            ArrayList<WorkVO> genroWorkList = new ArrayList<>();
//            genreRankingVO.setStrHeaderTitle("장르별 순위");
//            genreRankingVO.setViewType(1);
//
//            for(int i = 0 ; i < genreRankingJsonArray.length() ; i++) {
//                JSONObject object = genreRankingJsonArray.getJSONObject(i);
//
//                WorkVO workVO = new WorkVO();
//                workVO.setWorkID(object.getInt("WORK_ID"));
//                workVO.setCreatedDate(object.getString("CREATED_DATE"));
//                workVO.setStrSynopsis(object.getString("WORK_SYNOPSIS"));
//                workVO.setWriteID(object.getString("WRITER_ID"));
//                workVO.setStrWriterName(object.getString("WRITER_NAME"));
//                workVO.setTitle(object.getString("WORK_TITLE"));
//                workVO.setCoverFile(object.getString("COVER_IMG"));
//                workVO.setnHitsCount(object.getInt("HITS_COUNT"));
//                workVO.setfStarPoint((float)object.getDouble("STAR_POINT"));
//                workVO.setnKeepcount(object.getInt("KEEP_COUNT"));
//                workVO.setbDistractor(object.getString("DISTRACTOR").equals("Y") ? true : false);
//                workVO.setnTarget(object.getInt("TARGET"));
//
//                genroWorkList.add(workVO);
//            }
//
//            genreRankingVO.setAllItemInCard(genroWorkList);
//            mainCardList.add(genreRankingVO);

            /*
            {"NEW_WORK":["value", "value", "value"]}
             */

            JSONArray newJsonArray = resultObject.getJSONArray("NEW_WORK");
            MainCardVO newRankingVO = new MainCardVO();
            ArrayList<WorkVO> newWorkList = new ArrayList<>();
            newRankingVO.setStrHeaderTitle("최신작");
            newRankingVO.setViewType(2);

            for (int i = 0; i < newJsonArray.length(); i++) {
                JSONObject object = newJsonArray.getJSONObject(i);

                WorkVO workVO = new WorkVO();
                workVO.setWorkID(object.getInt("WORK_ID"));
                workVO.setCreatedDate(object.getString("CREATED_DATE"));
                workVO.setStrUpdateDate(object.getString("UPDATE_DATE"));
                workVO.setStrSynopsis(object.getString("WORK_SYNOPSIS"));
                workVO.setWriteID(object.getString("WRITER_ID"));
                workVO.setStrWriterName(object.getString("WRITER_NAME"));
                workVO.setTitle(object.getString("WORK_TITLE"));
                workVO.setCoverFile(object.getString("COVER_IMG"));
                workVO.setnHitsCount(object.getInt("HITS_COUNT"));
                workVO.setnTapCount(object.getInt("TAB_COUNT"));
                workVO.setfStarPoint((float) object.getDouble("STAR_POINT"));
                workVO.setnKeepcount(object.getInt("KEEP_COUNT"));
                workVO.setnCommentCount(object.getInt("COMMENT_COUNT"));
                workVO.setStrThumbFile(object.getString("WORK_COVER_THUMBNAIL"));
                workVO.setbPosterThumbnail(object.getString("POSTER_THUMB_YN").equals("Y") ? true : false);
                workVO.setbDistractor(object.getString("DISTRACTOR").equals("Y") ? true : false);
                workVO.setnTarget(object.getInt("TARGET"));

                newWorkList.add(workVO);
            }

            newRankingVO.setAllItemInCard(newWorkList);
            mainCardList.add(newRankingVO);

//            JSONArray recommandsonArray = resultObject.getJSONArray("RECOMMAND");
//            MainCardVO recommandVO = new MainCardVO();
//            ArrayList<WorkVO> recommandWorkList = new ArrayList<>();
//            recommandVO.setStrHeaderTitle("추천");
//            recommandVO.setViewType(3);
//
//            for(int i = 0 ; i < recommandsonArray.length() ; i++) {
//                JSONObject object = recommandsonArray.getJSONObject(i);
//
//                WorkVO workVO = new WorkVO();
//                workVO.setWorkID(object.getInt("WORK_ID"));
//                workVO.setCreatedDate(object.getString("CREATED_DATE"));
//                workVO.setStrSynopsis(object.getString("WORK_SYNOPSIS"));
//                workVO.setWriteID(object.getString("WRITER_ID"));
//                workVO.setStrWriterName(object.getString("WRITER_NAME"));
//                workVO.setTitle(object.getString("WORK_TITLE"));
//                workVO.setCoverFile(object.getString("COVER_IMG"));
//                workVO.setnHitsCount(object.getInt("HITS_COUNT"));
//                workVO.setfStarPoint((float)object.getDouble("STAR_POINT"));
//                workVO.setnKeepcount(object.getInt("KEEP_COUNT"));
//                workVO.setbDistractor(object.getString("DISTRACTOR").equals("Y") ? true : false);
//                workVO.setnTarget(object.getInt("TARGET"));
//
//                recommandWorkList.add(workVO);
//            }
//
//            recommandVO.setAllItemInCard(recommandWorkList);
//            mainCardList.add(recommandVO);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mainCardList;
    }

    public static ArrayList<CharacterVO> getCharacterDataWithEpisodeID(OkHttpClient httpClient, String strEpisodeID) {
        ArrayList<CharacterVO> characterVOList = new ArrayList<CharacterVO>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetEpisodeChatData&EPISODE_ID=" + strEpisodeID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            JSONArray chatArray = resultObject.getJSONArray("CHARACTER_LIST");

            for (int i = 0; i < chatArray.length(); i++) {
                JSONObject object = chatArray.getJSONObject(i);
                CharacterVO characterVO = new CharacterVO();

                characterVO.setnCharacterID(object.getInt("CHARACTER_ID"));
                characterVO.setName(object.getString("CHARACTER_NAME"));
                characterVO.setStrImgFile(object.getString("CHARACTER_IMG"));
                characterVO.setbBlackText(object.getString("BLACK_TEXT").equals("Y") ? true : false);
                characterVO.setbBlackName(object.getString("BLACK_NAME").equals("Y") ? true : false);
                characterVO.setIndex(i + 1);

                if (object.getString("BALLOON_COLOR") != null && !object.getString("BALLOON_COLOR").equals("null"))
                    characterVO.setStrBalloonColor(object.getString("BALLOON_COLOR"));

                characterVO.setDirection(object.getInt("CHARACTER_DIRECTION"));
                characterVOList.add(characterVO);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return characterVOList;
    }

    public static boolean requestChangeEpisodeTitle(OkHttpClient httpClient, String strTitle, int nEpisodeID) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=ChangeEpisodeTitle&EPISODE_ID=" + nEpisodeID + "&TITLE=" + strTitle)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return false;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            if (resultObject.getString("RESULT").equals("SUCCESS"))
                return true;
            else
                return false;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static ArrayList<ChatVO> getChatDataWithEpisodeID(OkHttpClient httpClient, String strEpisodeID) {
        ArrayList<ChatVO> chatVOList = new ArrayList<ChatVO>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetEpisodeChatData&EPISODE_ID=" + strEpisodeID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            JSONArray chatArray = resultObject.getJSONArray("CHAT_LIST");

            boolean bNoDelbtn = false;
            int nLastCharacterID = -1;

            ArrayList<CharacterVO> characterArray = new ArrayList<>();
            ArrayList<String> characterIDList = new ArrayList<>();

            for (int i = 0; i < chatArray.length(); i++) {
                JSONObject object = chatArray.getJSONObject(i);
                CharacterVO characterVO = new CharacterVO();

                characterVO.setName(object.getString("CHARACTER_NAME"));
                characterVO.setStrImgFile(object.getString("CHARACTER_IMAGE"));
                characterVO.setnCharacterID(object.getInt("CHARACTER_ID"));
                characterVO.setbBlackText(object.getString("BLACK_TEXT").equals("Y") ? true : false);
                characterVO.setbBlackName(object.getString("BLACK_NAME").equals("Y") ? true : false);

                if (object.getString("BALLOON_COLOR") != null && !object.getString("BALLOON_COLOR").equals("null"))
                    characterVO.setStrBalloonColor(object.getString("BALLOON_COLOR"));
                characterVO.setDirection(object.getInt("CHARACTER_DIRECTION"));

                if (characterVO.getnCharacterID() == 0)
                    continue;
                else if (characterIDList.contains("" + characterVO.getnCharacterID()))
                    continue;

                characterVO.setIndex(characterIDList.size() + 1);
                characterIDList.add("" + characterVO.getnCharacterID());
            }

            for (int i = 0; i < chatArray.length(); i++) {
                JSONObject object = chatArray.getJSONObject(i);
                ChatVO vo = new ChatVO();
                CharacterVO characterVO = new CharacterVO();

                characterVO.setName(object.getString("CHARACTER_NAME"));
                characterVO.setStrImgFile(object.getString("CHARACTER_IMAGE"));
                characterVO.setnCharacterID(object.getInt("CHARACTER_ID"));
                characterVO.setbBlackText(object.getString("BLACK_TEXT").equals("Y") ? true : false);
                characterVO.setbBlackName(object.getString("BLACK_NAME").equals("Y") ? true : false);

                if (object.getString("BALLOON_COLOR") != null && !object.getString("BALLOON_COLOR").equals("null"))
                    characterVO.setStrBalloonColor(object.getString("BALLOON_COLOR"));

                characterVO.setIndex(characterIDList.indexOf("" + characterVO.getnCharacterID()) + 1);
                characterVO.setDirection(object.getInt("CHARACTER_DIRECTION"));

                vo.setnEpisodeID(object.getInt("EPISODE_ID"));
                vo.setnChatID(object.getInt("CHAT_ID"));
                vo.setCharacter(characterVO);
                vo.setnOrder(object.getInt("CHAT_ORDER"));
                vo.setType(object.getInt("CHAT_TYPE"));
                vo.setnCommentCount(object.getInt("COMMENT_COUNT"));

                if (bNoDelbtn) {
                    vo.setbNoDelbtn(true);
                    bNoDelbtn = false;
                }

                switch (object.getInt("CHAT_TYPE")) {
                    case ChatVO.TYPE_TEXT:
                        vo.setContents(object.getString("CHAT_CONTENTS"));
                        if (nLastCharacterID == characterVO.getnCharacterID()) {
                            characterVO.setbFaceShow(false);
                        } else {
                            characterVO.setbFaceShow(true);
                            nLastCharacterID = characterVO.getnCharacterID();
                        }
                        break;
                    case ChatVO.TYPE_NARRATION:
                        vo.setContents(object.getString("CHAT_CONTENTS"));
                        nLastCharacterID = -1;
                        break;
                    case ChatVO.TYPE_IMAGE:
                        vo.setStrContentsFile(object.getString("CHAT_CONTENTS"));
                        if (nLastCharacterID == characterVO.getnCharacterID()) {
                            characterVO.setbFaceShow(false);
                        } else {
                            characterVO.setbFaceShow(true);
                            nLastCharacterID = characterVO.getnCharacterID();
                        }
                        break;
                    case ChatVO.TYPE_VIDEO:
                        vo.setStrContentsFile(object.getString("CHAT_CONTENTS"));
                        if (nLastCharacterID == characterVO.getnCharacterID()) {
                            characterVO.setbFaceShow(false);
                        } else {
                            characterVO.setbFaceShow(true);
                            nLastCharacterID = characterVO.getnCharacterID();
                        }
                        break;
                    case ChatVO.TYPE_SOUND:
                        vo.setStrContentsFile(object.getString("CHAT_CONTENTS"));
                        if (nLastCharacterID == characterVO.getnCharacterID()) {
                            characterVO.setbFaceShow(false);
                        } else {
                            characterVO.setbFaceShow(true);
                            nLastCharacterID = characterVO.getnCharacterID();
                        }
                        break;
                    case ChatVO.TYPE_CHANGE_BG_COLOR:
                        vo.setStrContentsFile(object.getString("CHAT_CONTENTS"));
                        break;
                    case ChatVO.TYPE_CHANGE_BG:
                        vo.setStrContentsFile(object.getString("CHAT_CONTENTS"));
                        break;
                    case ChatVO.TYPE_DISTRACTOR:
                        vo.setContents(object.getString("CHAT_CONTENTS"));
                        bNoDelbtn = true;
//                        if(characterVO.getnCharacterID() != 0) {
//                            if(nLastCharacterID == characterVO.getnCharacterID()) {
//                                characterVO.setbFaceShow(false);
//                            } else {
//                                characterVO.setbFaceShow(true);
//                                nLastCharacterID = characterVO.getnCharacterID();
//                            }
//                        } else {
//                            nLastCharacterID = -1;
//                        }

                        nLastCharacterID = -1;
                        break;
                    case ChatVO.TYPE_IMAGE_NAR:
                        vo.setStrContentsFile(object.getString("CHAT_CONTENTS"));
                        if (nLastCharacterID == characterVO.getnCharacterID()) {
                            characterVO.setbFaceShow(false);
                        } else {
                            characterVO.setbFaceShow(true);
                            nLastCharacterID = characterVO.getnCharacterID();
                        }
                        break;
                }


                chatVOList.add(vo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return chatVOList;
    }

    public static int getEpisodeInteraction(OkHttpClient httpClient, int nWorkID, String strUserID) {
        int nResult = 0;

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetEpisodeInteraction&WORK_ID=" + nWorkID + "&USER_ID=" + strUserID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return nResult;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            if (resultObject.getString("RESULT").equals("SUCCESS")) {
                nResult = resultObject.getInt("INTERACTION");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return nResult;
    }

    public static boolean setEpisodeInteraction(OkHttpClient httpClient, int nWorkID, String strUserID, int nInteraction) {
        ArrayList<ChatVO> chatVOList = new ArrayList<ChatVO>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=SetEpisodeInteraction&WORK_ID=" + nWorkID + "&USER_ID=" + strUserID + "&INTERACTION=" + nInteraction)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return false;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            if (resultObject.getString("RESULT").equals("SUCCESS"))
                return true;
            else
                return false;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static ArrayList<ChatVO> getChatDataWithEpisodeIDAndInteraction(OkHttpClient httpClient, String strEpisodeID, int nInteractionNum) {
        ArrayList<ChatVO> chatVOList = new ArrayList<ChatVO>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetEpisodeChatDataWithInteraction&EPISODE_ID=" + strEpisodeID + "&INTERACTION=" + nInteractionNum)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            JSONArray chatArray = resultObject.getJSONArray("CHAT_LIST");
            boolean bNoDelbtn = false;
            int nLastCharacterID = -1;

            for (int i = 0; i < chatArray.length(); i++) {
                JSONObject object = chatArray.getJSONObject(i);
                ChatVO vo = new ChatVO();
                CharacterVO characterVO = new CharacterVO();

                characterVO.setName(object.getString("CHARACTER_NAME"));
                characterVO.setStrImgFile(object.getString("CHARACTER_IMAGE"));
                characterVO.setnCharacterID(object.getInt("CHARACTER_ID"));
                characterVO.setbBlackText(object.getString("BLACK_TEXT").equals("Y") ? true : false);
                characterVO.setbBlackName(object.getString("BLACK_NAME").equals("Y") ? true : false);

                if (object.getString("BALLOON_COLOR") != null && !object.getString("BALLOON_COLOR").equals("null"))
                    characterVO.setStrBalloonColor(object.getString("BALLOON_COLOR"));

                characterVO.setDirection(object.getInt("CHARACTER_DIRECTION"));

                vo.setnEpisodeID(object.getInt("EPISODE_ID"));
                vo.setnChatID(object.getInt("CHAT_ID"));
                vo.setCharacter(characterVO);
                vo.setnOrder(object.getInt("CHAT_ORDER"));
                vo.setType(object.getInt("CHAT_TYPE"));
                vo.setnCommentCount(object.getInt("COMMENT_COUNT"));
                vo.setnInteractionNum(object.getInt("CHAT_DISTRACTOR"));

                if (bNoDelbtn) {
                    bNoDelbtn = false;
                    vo.setbNoDelbtn(true);
                }

                switch (object.getInt("CHAT_TYPE")) {
                    case ChatVO.TYPE_TEXT:
                        vo.setContents(object.getString("CHAT_CONTENTS"));
                        if (nLastCharacterID == characterVO.getnCharacterID()) {
                            characterVO.setbFaceShow(false);
                        } else {
                            characterVO.setbFaceShow(true);
                            nLastCharacterID = characterVO.getnCharacterID();
                        }
                        break;
                    case ChatVO.TYPE_NARRATION:
                        vo.setContents(object.getString("CHAT_CONTENTS"));
                        nLastCharacterID = -1;
                        break;
                    case ChatVO.TYPE_IMAGE:
                        vo.setStrContentsFile(object.getString("CHAT_CONTENTS"));
                        if (nLastCharacterID == characterVO.getnCharacterID()) {
                            characterVO.setbFaceShow(false);
                        } else {
                            characterVO.setbFaceShow(true);
                            nLastCharacterID = characterVO.getnCharacterID();
                        }
                        break;
                    case ChatVO.TYPE_VIDEO:
                        vo.setStrContentsFile(object.getString("CHAT_CONTENTS"));
                        if (nLastCharacterID == characterVO.getnCharacterID()) {
                            characterVO.setbFaceShow(false);
                        } else {
                            characterVO.setbFaceShow(true);
                            nLastCharacterID = characterVO.getnCharacterID();
                        }
                        break;
                    case ChatVO.TYPE_SOUND:
                        vo.setStrContentsFile(object.getString("CHAT_CONTENTS"));
                        if (nLastCharacterID == characterVO.getnCharacterID()) {
                            characterVO.setbFaceShow(false);
                        } else {
                            characterVO.setbFaceShow(true);
                            nLastCharacterID = characterVO.getnCharacterID();
                        }
                        break;
                    case ChatVO.TYPE_CHANGE_BG_COLOR:
                        vo.setStrContentsFile(object.getString("CHAT_CONTENTS"));
                        break;
                    case ChatVO.TYPE_CHANGE_BG:
                        vo.setStrContentsFile(object.getString("CHAT_CONTENTS"));
                        break;
                    case ChatVO.TYPE_DISTRACTOR:
                        vo.setContents(object.getString("CHAT_CONTENTS"));
                        bNoDelbtn = true;
//                        if(characterVO.getnCharacterID() != 0) {
//                            if(nLastCharacterID == characterVO.getnCharacterID()) {
//                                characterVO.setbFaceShow(false);
//                            } else {
//                                characterVO.setbFaceShow(true);
//                                nLastCharacterID = characterVO.getnCharacterID();
//                            }
//                        } else {
//                            nLastCharacterID = -1;
//                        }

                        nLastCharacterID = -1;
                        break;
                    case ChatVO.TYPE_IMAGE_NAR:
                        vo.setStrContentsFile(object.getString("CHAT_CONTENTS"));
                        if (nLastCharacterID == characterVO.getnCharacterID()) {
                            characterVO.setbFaceShow(false);
                        } else {
                            characterVO.setbFaceShow(true);
                            nLastCharacterID = characterVO.getnCharacterID();
                        }
                        break;
                }


                chatVOList.add(vo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return chatVOList;
    }

    public static boolean requestSendPoint(OkHttpClient httpClient, float fPoint, int nEpisodeID, String strUserID) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=RequestSendStarPoint&EPISODE_ID=" + nEpisodeID + "&USER_ID=" + strUserID + "&STAR_POINT=" + fPoint)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return false;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            if (resultObject.getString("RESULT").equals("SUCCESS"))
                return true;
            else
                return false;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static float getMyStarPoint(OkHttpClient httpClient, int nEpisodeId, String strUserID) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetMyStarPoint&EPISODE_ID=" + nEpisodeId + "&USER_ID=" + strUserID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return 0;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            return (float) resultObject.getDouble("STAR_POINT");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static int requestCreateEpisode(OkHttpClient httpClient, String strWorkID) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=RequestCreateEpisode&WORK_ID=" + strWorkID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return 0;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            if (resultObject.getString("RESULT").equals("SUCCESS")) {
                return resultObject.getInt("EPISODE_ID");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static JSONObject getWorkTagWithID(OkHttpClient httpClient, String strWorkID) {
        JSONObject resultObject = null;

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetTagsWithID&WORK_ID=" + strWorkID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            resultObject = new JSONObject(strResult);
        } catch (JSONException e) {
            e.printStackTrace();
            resultObject = null;
        } catch (IOException e) {
            e.printStackTrace();
            resultObject = null;
        }

        return resultObject;
    }

    public static boolean requestFollow(OkHttpClient httpClient, String strID, String strWriterID, boolean bUnfollow) {
        int nUnfollow = 0;
        if (bUnfollow == true)
            nUnfollow = 1;
        else
            nUnfollow = 0;

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=RequestFollow&USER_ID=" + strID + "&WRITER_ID=" + strWriterID + "&UNFOLLOW=" + nUnfollow)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return false;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);
            if (resultObject.getString("RESULT").equals("SUCCESS"))
                return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    //GetMyFollowInfo

    public static ArrayList<WriterVO> getMyFollowerList(OkHttpClient httpClient, String strMyID) {
        ArrayList<WriterVO> writerList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetMyFollowerList&USER_ID=" + strMyID)
                .get()
                .build();

        JSONObject resultObject = null;

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            resultObject = new JSONObject(strResult);
            JSONArray writerArray = resultObject.getJSONArray("WRITER_LIST");

            for (int i = 0; i < writerArray.length(); i++) {
                JSONObject object = writerArray.getJSONObject(i);

                WriterVO vo = new WriterVO();
                vo.setStrWriterID(object.getString("USER_ID"));
                vo.setStrWriterName(object.getString("USER_NAME"));
                vo.setStrWriterPhoto(object.getString("USER_PHOTO"));
                vo.setStrWriterComment(object.getString("USER_COMMENT"));
                vo.setnFollowcount(object.getInt("FOLLOW_COUNT"));
                vo.setnFollowingCount(object.getInt("FOLLOWING_COUNT"));
                vo.setnDonationCarrot(object.getInt("DONATION_CARROT"));

                writerList.add(vo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            resultObject = null;
        } catch (IOException e) {
            e.printStackTrace();
            resultObject = null;
        }

        return writerList;
    }

    public static ArrayList<WriterVO> getMyFollowingList(OkHttpClient httpClient, String strMyID) {
        ArrayList<WriterVO> writerList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetMyFollowingList&USER_ID=" + strMyID)
                .get()
                .build();

        JSONObject resultObject = null;

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            resultObject = new JSONObject(strResult);
            JSONArray writerArray = resultObject.getJSONArray("WRITER_LIST");

            for (int i = 0; i < writerArray.length(); i++) {
                JSONObject object = writerArray.getJSONObject(i);

                WriterVO vo = new WriterVO();
                vo.setStrWriterID(object.getString("USER_ID"));
                vo.setStrWriterName(object.getString("USER_NAME"));
                vo.setStrWriterPhoto(object.getString("USER_PHOTO"));
                vo.setStrWriterComment(object.getString("USER_COMMENT"));
                vo.setnFollowcount(object.getInt("FOLLOW_COUNT"));
                vo.setnFollowingCount(object.getInt("FOLLOWING_COUNT"));
                vo.setnDonationCarrot(object.getInt("DONATION_CARROT"));

                writerList.add(vo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            resultObject = null;
        } catch (IOException e) {
            e.printStackTrace();
            resultObject = null;
        }

        return writerList;
    }

    public static JSONObject getMyCarrotInfo(OkHttpClient httpClient, String strMyID) {
        JSONObject resultObject = null;

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetMyCarrotInfo&USER_ID=" + strMyID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            resultObject = new JSONObject(strResult);
        } catch (JSONException e) {
            e.printStackTrace();
            resultObject = null;
        } catch (IOException e) {
            e.printStackTrace();
            resultObject = null;
        }

        return resultObject;
    }

    public static JSONObject getMyFollowInfo(OkHttpClient httpClient, String strMyID) {
        JSONObject resultObject = null;

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetMyFollowInfo&USER_ID=" + strMyID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            resultObject = new JSONObject(strResult);
        } catch (JSONException e) {
            e.printStackTrace();
            resultObject = null;
        } catch (IOException e) {
            e.printStackTrace();
            resultObject = null;
        }

        return resultObject;
    }

    public static JSONObject getWriterInfo(OkHttpClient httpClient, String strID, String myID) {
        JSONObject resultObject = null;

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetWriterInfo&USER_ID=" + myID + "&WRITER_ID=" + strID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            resultObject = new JSONObject(strResult);
        } catch (JSONException e) {
            e.printStackTrace();
            resultObject = null;
        } catch (IOException e) {
            e.printStackTrace();
            resultObject = null;
        }

        return resultObject;
    }

    public static ArrayList<WaitingVO> getWaitingList(OkHttpClient httpClient, String strUserID) {
        ArrayList<WaitingVO> resultList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanBookAdmin.jsp?CMD=GetWaitingWorkList&USER_ID=" + strUserID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);
            JSONArray waitingArr = resultObject.getJSONArray("WAITING_LIST");

            for (int i = 0; i < waitingArr.length(); i++) {
                JSONObject object = waitingArr.getJSONObject(i);
                WaitingVO vo = new WaitingVO();
                vo.setnWorkID(object.getInt("WORK_ID"));
                vo.setStrWorkTitle(object.getString("WORK_TITLE"));
                vo.setWriterName(object.getString("WRITER_NAME"));
                vo.setnEpisodeID(object.getInt("EPISODE_ID"));
                vo.setStrEpisodeTitle(object.getString("EPISODE_TITLE"));
                vo.setStrCratedDate(object.getString("CREATED_DATE"));
                vo.setStrUpdateDate(object.getString("UPDATE_DATE"));
                vo.setStrCoverImg(object.getString("COVER_IMG"));
                vo.setnEpisodeOrder(object.getInt("EPISODE_ORDER"));
                vo.setStrSynopsis(object.getString("WORK_SYNOPSIS"));
                resultList.add(vo);
            }
        } catch (IOException e) {
            e.printStackTrace();
            resultList = null;
        } catch (JSONException e) {
            e.printStackTrace();
            resultList = null;
        }

        return resultList;
    }

    public static ArrayList<WaitingVO> getCompleteList(OkHttpClient httpClient, String strUserID) {
        ArrayList<WaitingVO> resultList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanBookAdmin.jsp?CMD=GetCompleteWorkList&USER_ID=" + strUserID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            JSONArray waitingArr = resultObject.getJSONArray("WAITING_LIST");

            for (int i = 0; i < waitingArr.length(); i++) {
                JSONObject object = waitingArr.getJSONObject(i);
                WaitingVO vo = new WaitingVO();
                vo.setnWorkID(object.getInt("WORK_ID"));
                vo.setStrWorkTitle(object.getString("WORK_TITLE"));
                vo.setWriterName(object.getString("WRITER_NAME"));
                vo.setnEpisodeID(object.getInt("EPISODE_ID"));
                vo.setStrEpisodeTitle(object.getString("EPISODE_TITLE"));
                vo.setStrCratedDate(object.getString("CREATED_DATE"));
                vo.setStrUpdateDate(object.getString("UPDATE_DATE"));
                vo.setStrCoverImg(object.getString("COVER_IMG"));
                vo.setnEpisodeOrder(object.getInt("EPISODE_ORDER"));
                vo.setStrSynopsis(object.getString("WORK_SYNOPSIS"));

                resultList.add(vo);
            }
        } catch (IOException e) {
            e.printStackTrace();
            resultList = null;
        } catch (JSONException e) {
            e.printStackTrace();
            resultList = null;
        }

        return resultList;
    }

    public static JSONObject requestDonation(OkHttpClient httpClient, int nWorkID, String strUserID, int nCarrot) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=ReqeustDonation&WORK_ID=" + nWorkID + "&USER_ID=" + strUserID + "&CARROT=" + nCarrot)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);
            return resultObject;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static WorkVO getWorkWithID(OkHttpClient httpClient, String strWorkID, String strUserID, boolean bDesc) {                              // 모든 작품 목록 가져오기
        WorkVO workVO = null;

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetWorkWithID&WORK_ID=" + strWorkID + "&USER_ID=" + strUserID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            workVO = new WorkVO();
            workVO.setWorkID(resultObject.getInt("WORK_ID"));
            workVO.setCreatedDate(resultObject.getString("CREATED_DATE"));
            workVO.setStrSynopsis(resultObject.getString("WORK_SYNOPSIS"));
            workVO.setWriteID(resultObject.getString("WRITER_ID"));
            workVO.setStrWriterName(resultObject.getString("WRITER_NAME"));
            workVO.setStrWriterPhoto(resultObject.getString("WRITER_PHOTO"));
            workVO.setTitle(resultObject.getString("WORK_TITLE"));
            workVO.setCoverFile(resultObject.getString("WORK_COVER_IMG"));
            workVO.setnHitsCount(resultObject.getInt("HITS_COUNT"));
            workVO.setnTapCount(resultObject.getInt("TAB_COUNT"));
            workVO.setfStarPoint((float) resultObject.getDouble("STAR_POINT"));
            workVO.setnKeepcount(resultObject.getInt("KEEP_COUNT"));
            workVO.setnCommentCount(resultObject.getInt("COMMENT_COUNT"));
            workVO.setbDistractor(resultObject.getString("DISTRACTOR").equals("Y") ? true : false);
            workVO.setnTarget(resultObject.getInt("TARGET"));
            workVO.setnDonationCarrot(resultObject.getInt("CARROT_DONATION"));
            workVO.setbComplete(resultObject.getString("COMPLETE").equals("Y") ? true : false);

            ArrayList<EpisodeVO> episodeList = new ArrayList<>();

            if (resultObject.has("EPISODE_LIST")) {
                JSONArray episodeArray = resultObject.getJSONArray("EPISODE_LIST");

                if (!bDesc) {
                    for (int i = 0; i < episodeArray.length(); i++) {
                        JSONObject object = episodeArray.getJSONObject(i);
                        EpisodeVO vo = new EpisodeVO();

                        vo.setnEpisodeID(object.getInt("EPISODE_ID"));
                        vo.setStrTitle(object.getString("EPISODE_TITLE"));
                        vo.setStrDate(object.getString("CREATED_DATE"));
                        vo.setnOrder(object.getInt("EPISODE_ORDER"));
                        vo.setnHitsCount(object.getInt("HITS_COUNT"));
                        vo.setnTapCount(object.getInt("TAB_COUNT"));
                        vo.setfStarPoint((float) object.getDouble("STAR_POINT"));
                        vo.setnCommentCount(object.getInt("COMMENT_COUNT"));
                        vo.setDistractor(object.getString("DISTRACTOR").equals("Y") ? true : false);
                        vo.setStrSubmit(object.getString("WORK_SUBMIT"));
                        if(object.has("TRASH_ID")) {
                            vo.setnTrashID(object.getInt("TRASH_ID"));
                            vo.setStrIsolatedDate(object.getString("ISOLATED_DATE"));
                        }
                        if (object.has("CHAT_COUNT"))
                            vo.setnChatCount(object.getInt("CHAT_COUNT"));

                        episodeList.add(vo);
                    }
                } else {
                    for (int i = episodeArray.length() - 1; i >= 0; i--) {
                        JSONObject object = episodeArray.getJSONObject(i);
                        EpisodeVO vo = new EpisodeVO();

                        vo.setnEpisodeID(object.getInt("EPISODE_ID"));
                        vo.setStrTitle(object.getString("EPISODE_TITLE"));
                        vo.setDistractor(object.getString("DISTRACTOR").equals("N") ? false : true);
                        vo.setStrDate(object.getString("CREATED_DATE"));
                        vo.setnOrder(object.getInt("EPISODE_ORDER"));
                        vo.setnHitsCount(object.getInt("HITS_COUNT"));
                        vo.setnTapCount(object.getInt("TAB_COUNT"));
                        vo.setfStarPoint((float) object.getDouble("STAR_POINT"));
                        vo.setnCommentCount(object.getInt("COMMENT_COUNT"));
                        vo.setDistractor(object.getString("DISTRACTOR").equals("Y") ? true : false);
                        vo.setStrSubmit(object.getString("WORK_SUBMIT"));
                        if(object.has("TRASH_ID")) {
                            vo.setnTrashID(object.getInt("TRASH_ID"));
                            vo.setStrIsolatedDate(object.getString("ISOLATED_DATE"));
                        }
                        if (object.has("CHAT_COUNT"))
                            vo.setnChatCount(object.getInt("CHAT_COUNT"));


                        episodeList.add(vo);
                    }
                }

                workVO.setEpisodeList(episodeList);
            } else {
                workVO.setEpisodeList(episodeList);
            }
        } catch (IOException e) {
            e.printStackTrace();
            workVO = null;
        } catch (JSONException e) {
            e.printStackTrace();
            workVO = null;
        }

        return workVO;
    }

    public static WorkVO getWriterWorkWithID(OkHttpClient httpClient, String strWorkID, String strUserID, boolean bDesc) {                              // 특정 작가가 작성한 특정 작품의 모든 회차목록 가져오기(게시중인 작품 포함)
        WorkVO workVO = null;

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetWriterWorkWithID&WORK_ID=" + strWorkID + "&USER_ID=" + strUserID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            workVO = new WorkVO();
            workVO.setWorkID(resultObject.getInt("WORK_ID"));
            workVO.setCreatedDate(resultObject.getString("CREATED_DATE"));
            workVO.setStrSynopsis(resultObject.getString("WORK_SYNOPSIS"));
            workVO.setWriteID(resultObject.getString("WRITER_ID"));
            workVO.setStrWriterName(resultObject.getString("WRITER_NAME"));
            workVO.setStrWriterPhoto(resultObject.getString("WRITER_PHOTO"));
            workVO.setTitle(resultObject.getString("WORK_TITLE"));
            workVO.setCoverFile(resultObject.getString("WORK_COVER_IMG"));
            workVO.setStrThumbFile(resultObject.getString("WORK_COVER_THUMBNAIL"));
            workVO.setnHitsCount(resultObject.getInt("HITS_COUNT"));
            workVO.setnTapCount(resultObject.getInt("TAB_COUNT"));
            workVO.setfStarPoint((float) resultObject.getDouble("STAR_POINT"));
            workVO.setnKeepcount(resultObject.getInt("KEEP_COUNT"));
            workVO.setnCommentCount(resultObject.getInt("COMMENT_COUNT"));
            workVO.setbDistractor(resultObject.getString("DISTRACTOR").equals("Y") ? true : false);
            workVO.setnTarget(resultObject.getInt("TARGET"));
            workVO.setnDonationCarrot(resultObject.getInt("CARROT_DONATION"));
            workVO.setbPosterThumbnail(resultObject.getString("POSTER_THUMB_YN").equals("Y") ? true : false);
            workVO.setbComplete(resultObject.getString("COMPLETE").equals("Y") ? true : false);
            workVO.setnUserStatus(resultObject.getInt("USER_STATUS"));
            workVO.setnUserAuthority(resultObject.getInt("USER_AUTHORITY"));
            workVO.setnEditAuthority(resultObject.getInt("EDIT_AUTHORITY"));

            ArrayList<EpisodeVO> episodeList = new ArrayList<>();

            if (resultObject.has("EPISODE_LIST")) {
                JSONArray episodeArray = resultObject.getJSONArray("EPISODE_LIST");

                if (!bDesc) {
                    for (int i = 0; i < episodeArray.length(); i++) {
                        JSONObject object = episodeArray.getJSONObject(i);
                        EpisodeVO vo = new EpisodeVO();

                        vo.setnEpisodeID(object.getInt("EPISODE_ID"));
                        vo.setStrTitle(object.getString("EPISODE_TITLE"));
                        vo.setDistractor(object.getString("DISTRACTOR").equals("N") ? false : true);
                        vo.setStrDate(object.getString("CREATED_DATE"));
                        vo.setnOrder(object.getInt("EPISODE_ORDER"));
                        vo.setnHitsCount(object.getInt("HITS_COUNT"));
                        vo.setnTapCount(object.getInt("TAB_COUNT"));
                        vo.setfStarPoint((float) object.getDouble("STAR_POINT"));
                        vo.setnCommentCount(object.getInt("COMMENT_COUNT"));
                        vo.setDistractor(object.getString("DISTRACTOR").equals("Y") ? true : false);
                        vo.setStrSubmit(object.getString("WORK_SUBMIT"));
                        vo.setExcelUploaded(object.getString("EXCEL_UPLOADED").equals("Y") ? true : false);
                        vo.setnEditAuthority(object.getInt("EDIT_AUTHORITY"));
                        if (object.has("CHAT_COUNT"))
                            vo.setnChatCount(object.getInt("CHAT_COUNT"));

                        episodeList.add(vo);
                    }
                } else {
                    for (int i = episodeArray.length() - 1; i >= 0; i--) {
                        JSONObject object = episodeArray.getJSONObject(i);
                        EpisodeVO vo = new EpisodeVO();

                        vo.setnEpisodeID(object.getInt("EPISODE_ID"));
                        vo.setStrTitle(object.getString("EPISODE_TITLE"));
                        vo.setDistractor(object.getString("DISTRACTOR").equals("N") ? false : true);
                        vo.setStrDate(object.getString("CREATED_DATE"));
                        vo.setnOrder(object.getInt("EPISODE_ORDER"));
                        vo.setnHitsCount(object.getInt("HITS_COUNT"));
                        vo.setnTapCount(object.getInt("TAB_COUNT"));
                        vo.setfStarPoint((float) object.getDouble("STAR_POINT"));
                        vo.setnCommentCount(object.getInt("COMMENT_COUNT"));
                        vo.setDistractor(object.getString("DISTRACTOR").equals("Y") ? true : false);
                        vo.setStrSubmit(object.getString("WORK_SUBMIT"));
                        vo.setExcelUploaded(object.getString("EXCEL_UPLOADED").equals("Y") ? true : false);
                        vo.setnEditAuthority(object.getInt("EDIT_AUTHORITY"));
                        if (object.has("CHAT_COUNT"))
                            vo.setnChatCount(object.getInt("CHAT_COUNT"));

                        episodeList.add(vo);
                    }
                }

                workVO.setEpisodeList(episodeList);
            } else {
                workVO.setEpisodeList(episodeList);
            }
        } catch (IOException e) {
            e.printStackTrace();
            workVO = null;
        } catch (JSONException e) {
            e.printStackTrace();
            workVO = null;
        }

        return workVO;
    }


    public static WorkVO getWorkWithID(OkHttpClient httpClient, String strWorkID, String strUserID) {                              // 모든 작품 목록 가져오기
        WorkVO workVO = null;

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetWorkWithID&WORK_ID=" + strWorkID + "&USER_ID=" + strUserID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            workVO = new WorkVO();
            workVO.setWorkID(resultObject.getInt("WORK_ID"));
            workVO.setCreatedDate(resultObject.getString("CREATED_DATE"));
            workVO.setStrSynopsis(resultObject.getString("WORK_SYNOPSIS"));
            workVO.setWriteID(resultObject.getString("WRITER_ID"));
            workVO.setStrWriterName(resultObject.getString("WRITER_NAME"));
            workVO.setStrWriterPhoto(resultObject.getString("WRITER_PHOTO"));
            workVO.setTitle(resultObject.getString("WORK_TITLE"));
            workVO.setCoverFile(resultObject.getString("WORK_COVER_IMG"));
            workVO.setnHitsCount(resultObject.getInt("HITS_COUNT"));
            workVO.setnTapCount(resultObject.getInt("TAB_COUNT"));
            workVO.setfStarPoint((float) resultObject.getDouble("STAR_POINT"));
            workVO.setnKeepcount(resultObject.getInt("KEEP_COUNT"));
            workVO.setnCommentCount(resultObject.getInt("COMMENT_COUNT"));
            workVO.setbDistractor(resultObject.getString("DISTRACTOR").equals("Y") ? true : false);
            workVO.setnTarget(resultObject.getInt("TARGET"));

            if (resultObject.has("INTERACTION_EPISODE"))
                workVO.setnInteractionEpisodeID(resultObject.getInt("INTERACTION_EPISODE"));

            ArrayList<EpisodeVO> episodeList = new ArrayList<>();

            if (resultObject.has("EPISODE_LIST")) {
                JSONArray episodeArray = resultObject.getJSONArray("EPISODE_LIST");
                for (int i = 0; i < episodeArray.length(); i++) {
                    JSONObject object = episodeArray.getJSONObject(i);
                    EpisodeVO vo = new EpisodeVO();

                    vo.setnEpisodeID(object.getInt("EPISODE_ID"));
                    vo.setStrTitle(object.getString("EPISODE_TITLE"));
                    vo.setDistractor(object.getString("DISTRACTOR").equals("N") ? false : true);
                    vo.setStrDate(object.getString("CREATED_DATE"));
                    vo.setnOrder(object.getInt("EPISODE_ORDER"));
                    vo.setnHitsCount(object.getInt("HITS_COUNT"));
                    vo.setnTapCount(object.getInt("TAB_COUNT"));
                    vo.setfStarPoint((float) object.getDouble("STAR_POINT"));
                    vo.setnCommentCount(object.getInt("COMMENT_COUNT"));
                    vo.setDistractor(object.getString("DISTRACTOR").equals("Y") ? true : false);
                    vo.setStrSubmit(object.getString("WORK_SUBMIT"));

                    episodeList.add(vo);
                }

                workVO.setEpisodeList(episodeList);
            } else {
                workVO.setEpisodeList(episodeList);
            }
        } catch (IOException e) {
            e.printStackTrace();
            workVO = null;
        } catch (JSONException e) {
            e.printStackTrace();
            workVO = null;
        }

        return workVO;
    }

    public static boolean resetPassword(OkHttpClient httpClient, HashMap<String, String> userMap) {
        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("USER_ID", userMap.get("USER_ID"));
            jsonBody.put("USER_PW", userMap.get("USER_PW"));
            String jsonString = jsonBody.toString();

            RequestBody requestBody = RequestBody.create(JSON, jsonString);

            Request request = new Request.Builder()
                    .url(CommonUtils.strDefaultUrl + "PanBookResetPW.jsp")
                    .post(requestBody)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.code() != 200)
                    return false;

                String strResult = response.body().string();
                JSONObject resultJsonObject = new JSONObject(strResult);

                if (resultJsonObject.getString("RESULT").equals("SUCCESS"))
                    return true;
                else
                    return false;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean findPassword(OkHttpClient httpClient, HashMap<String, String> userMap) {
        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("USER_ID", userMap.get("USER_ID"));
            jsonBody.put("USER_EMAIL", userMap.get("USER_EMAIL"));
            jsonBody.put("FIND_PASSWORD", true);
            String jsonString = jsonBody.toString();

            RequestBody requestBody = RequestBody.create(JSON, jsonString);

            Request request = new Request.Builder()
                    .url(CommonUtils.strDefaultUrl + "PanbookFindAccount.jsp")
                    .post(requestBody)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.code() != 200)
                    return false;

                String strResult = response.body().string();
                JSONObject resultJsonObject = new JSONObject(strResult);

                if (resultJsonObject.getString("RESULT").equals("SUCCESS"))
                    return true;
                else
                    return false;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static JSONObject findAccount(OkHttpClient httpClient, HashMap<String, String> userMap) {
        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("USER_EMAIL", userMap.get("USER_EMAIL"));
            jsonBody.put("FIND_PASSWORD", false);
            String jsonString = jsonBody.toString();

            RequestBody requestBody = RequestBody.create(JSON, jsonString);

            Request request = new Request.Builder()
                    .url(CommonUtils.strDefaultUrl + "PanbookFindAccount.jsp")
                    .post(requestBody)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.code() != 200)
                    return null;

                String strResult = response.body().string();
                JSONObject resultJsonObject = new JSONObject(strResult);
                return resultJsonObject;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ArrayList<HashMap<String, String>> getAllWriterList(OkHttpClient httpClient) {                              // 모든 작품 목록 가져오기
        ArrayList<HashMap<String, String>> resultList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetWriterList")
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            JSONArray resultArray = resultObject.getJSONArray("WRITER_LIST");

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject object = resultArray.getJSONObject(i);
                HashMap<String, String> writerMap = new HashMap<>();

                writerMap.put("WRITER_ID", "" + object.getInt("WRITER_ID"));
                writerMap.put("WRITER_NAME", object.getString("WRITER_NAME"));
                resultList.add(writerMap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    public static JSONObject requestPanbookLogin(OkHttpClient httpClient, String strUserID, String strUserPW, String strFCMToken) throws JSONException {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("USER_ID", strUserID);
        jsonBody.put("USER_PW", strUserPW);
        jsonBody.put("FCM_TOKEN", strFCMToken);
        String jsonString = jsonBody.toString();

        RequestBody requestBody = RequestBody.create(JSON, jsonString);

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanbookLogin.jsp")
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);
            return resultJsonObject;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONObject requestSocialLogin(OkHttpClient httpClient, String strSNSID, String strFCMToken) throws JSONException {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("SNS_ID", strSNSID);
        jsonBody.put("FCM_TOKEN", strFCMToken);
        String jsonString = jsonBody.toString();

        RequestBody requestBody = RequestBody.create(JSON, jsonString);

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanbookSocialLogin.jsp")
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);
            return resultJsonObject;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONObject requestRegister(OkHttpClient httpClient, HashMap<String, String> userInfoMap) throws JSONException {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("USER_ID", userInfoMap.get("USER_ID"));
        jsonBody.put("USER_PW", userInfoMap.get("USER_PW"));
        jsonBody.put("USER_NAME", userInfoMap.get("USER_NAME"));
        jsonBody.put("USER_EMAIL", userInfoMap.get("USER_EMAIL"));
        jsonBody.put("USER_PHONENUM", userInfoMap.get("USER_PHONENUM"));
        jsonBody.put("USER_BIRTHDAY", userInfoMap.get("USER_BIRTHDAY"));
        jsonBody.put("USER_GENDER", userInfoMap.get("USER_GENDER"));

        if (userInfoMap.get("USER_PHOTO") != null)
            jsonBody.put("USER_PHOTO", userInfoMap.get("USER_PHOTO"));

        jsonBody.put("REGISTER_SNS", Integer.valueOf(userInfoMap.get("REGISTER_SNS")));
        jsonBody.put("SNS_ID", userInfoMap.get("SNS_ID"));

        String jsonString = jsonBody.toString();

        RequestBody requestBody = RequestBody.create(JSON, jsonString);

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppUserRegister.jsp")
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);
            return resultJsonObject;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONObject requestAddWriter(OkHttpClient httpClient, String strWriterName) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("USER_NAME", strWriterName)
                .build();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWriter.jsp")
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);
            return resultJsonObject;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean requestDeleteRead(OkHttpClient httpClient, int nWorkID, String strUserID) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=RequestDeleteRead&WORK_ID=" + nWorkID + "&USER_ID=" + strUserID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return false;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);

            if (resultJsonObject.getString("RESULT").equals("SUCCESS"))
                return true;
            else
                return false;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean requestDeleteWork(OkHttpClient httpClient, int nWorkID) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=RequestDeleteWork&WORK_ID=" + nWorkID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return false;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);

            if (resultJsonObject.getString("RESULT").equals("SUCCESS"))
                return true;
            else
                return false;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean requestWorkAprove(OkHttpClient httpClient, String strAdminID, int nEpisodeID) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanBookAdmin.jsp?CMD=RequestWorkAprove&ADMIN_ID=" + strAdminID + "&EPISODE_ID=" + nEpisodeID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return false;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);

            if (resultJsonObject.getString("RESULT").equals("SUCCESS"))
                return true;
            else
                return false;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static String requestDeleteEpisode(OkHttpClient httpClient, int nEpisodeID) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=RequestDeleteEpisode&EPISODE_ID=" + nEpisodeID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return "FAIL";

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);

            return resultJsonObject.getString("RESULT");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "FAIL";
    }

    public static JSONObject requestDeleteMessage(OkHttpClient httpClient, int nEpisodeID, int nChatID, int nOrder) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=RequestDeleteMessage&EPISODE_ID=" + nEpisodeID + "&CHAT_ID=" + nChatID + "&ORDER_NO=" + nOrder)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);
            return resultJsonObject;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONObject requestDeleteAllMessage(OkHttpClient httpClient, int nEpisodeID) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=RequestDeleteAllMessage&EPISODE_ID=" + nEpisodeID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);
            return resultJsonObject;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONObject requestDeleteInteraction(OkHttpClient httpClient, int nEpisodeID, int nChatID, int nOrder) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=RequestDeleteInteraction&EPISODE_ID=" + nEpisodeID + "&CHAT_ID=" + nChatID + "&ORDER_NO=" + nOrder)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);
            return resultJsonObject;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean sendRecommendCode(OkHttpClient httpClient, String strUserID, String strRecommendCode) {
        boolean bResult = false;

        JSONObject jsonBody = new JSONObject();

        try {
            String jsonString = jsonBody.toString();
            RequestBody requestBody = RequestBody.create(JSON, jsonString);

            Request request = new Request.Builder()
                    .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=SendRecommendCode&USER_ID=" + strUserID + "&RECOMMEND_CODE=" + strRecommendCode)
                    .get()
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.code() != 200)
                    return false;

                String strResult = response.body().string();
                JSONObject resultJsonObject = new JSONObject(strResult);

                if (resultJsonObject.getString("RESULT").equals("SUCCESS"))
                    return true;
                else
                    return false;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bResult;
    }

    public static boolean requestSendComment(OkHttpClient httpClient, HashMap<String, String> dataMap) {
        boolean bResult = false;

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("USER_ID", dataMap.get("USER_ID"));
            jsonBody.put("COMMENT", dataMap.get("COMMENT"));
            jsonBody.put("EPISODE_ID", dataMap.get("EPISODE_ID"));
            jsonBody.put("CHAT_ID", dataMap.get("CHAT_ID"));

            if (dataMap.get("WORK_ID") != null)
                jsonBody.put("WORK_ID", dataMap.get("WORK_ID"));

            if (dataMap.get("PARENT_ID") != null)
                jsonBody.put("PARENT_ID", dataMap.get("PARENT_ID"));

            String jsonString = jsonBody.toString();
            RequestBody requestBody = RequestBody.create(JSON, jsonString);

            Request request = new Request.Builder()
                    .url(CommonUtils.strDefaultUrl + "PanBookComment.jsp")
                    .post(requestBody)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.code() != 200)
                    return false;

                String strResult = response.body().string();
                JSONObject resultJsonObject = new JSONObject(strResult);

                if (resultJsonObject.getString("RESULT").equals("SUCCESS"))
                    return true;
                else
                    return false;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return bResult;
    }

    public static ArrayList<BillingLogVO> getPurchaseLog(OkHttpClient httpClient, String strUserID) {
        ArrayList<BillingLogVO> resultList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetPurchaseLog&USER_ID=" + strUserID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);
            JSONArray jsonArray = resultJsonObject.getJSONArray("BILLING_LIST");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);

                BillingLogVO vo = new BillingLogVO();
                vo.setnBillID(object.getInt("BILL_ID"));
                vo.setnCoinPrice(object.getInt("COIN_PRICE"));
                vo.setStrPurchaseDate(object.getString("REGISTER_DATE"));
                vo.setStrOrderID(object.getString("ORDER_ID"));

                resultList.add(vo);
            }
            return resultList;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean requestSendWriterChat(OkHttpClient httpClient, HashMap<String, String> dataMap) {
        boolean bResult = false;

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("USER_ID", dataMap.get("USER_ID"));
            jsonBody.put("COMMENT", dataMap.get("COMMENT"));
            jsonBody.put("WRITER_ID", dataMap.get("WRITER_ID"));

            if (dataMap.get("PARENT_ID") != null)
                jsonBody.put("PARENT_ID", dataMap.get("PARENT_ID"));

            String jsonString = jsonBody.toString();
            RequestBody requestBody = RequestBody.create(JSON, jsonString);

            Request request = new Request.Builder()
                    .url(CommonUtils.strDefaultUrl + "PanbookWriterChat.jsp")
                    .post(requestBody)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.code() != 200)
                    return false;

                String strResult = response.body().string();
                JSONObject resultJsonObject = new JSONObject(strResult);

                if (resultJsonObject.getString("RESULT").equals("SUCCESS"))
                    return true;
                else
                    return false;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return bResult;
    }

    public static boolean requestSendEmail(OkHttpClient httpClient, String strUserEmail) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "EmailAuth.jsp?EMAIL=" + strUserEmail)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return false;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);

            if (resultJsonObject.getString("RESULT").equals("SUCCESS"))
                return true;

            return false;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean requestAuthNum(OkHttpClient httpClient, String strUserEmail, String strAuthNum) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "AuthNum.jsp?EMAIL=" + strUserEmail + "&AUTH_NUM=" + strAuthNum)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return false;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);

            if (resultJsonObject.getString("RESULT").equals("SUCCESS"))
                return true;

            return false;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static JSONArray getChatCommentByID(OkHttpClient httpClient, String strUserID) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetChatCommentByID&USER_ID=" + strUserID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);
            JSONArray resultArray = resultJsonObject.getJSONArray("COMMENT_LIST");

            return resultArray;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONObject getWorkCommnet(OkHttpClient httpClient, int nWorkID, int nOrder, String strUserID) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetWorkEpisodeComment&WORK_ID=" + nWorkID + "&ORDER=" + nOrder + "&USER_ID=" + strUserID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            Log.d("Asdf", strResult);
            JSONObject resultJsonObject = new JSONObject(strResult);

            return resultJsonObject;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONObject getEpisodeCommnet(OkHttpClient httpClient, int nEpisodeID, int nOrder, String strUserID) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetEpisodeComment&EPISODE_ID=" + nEpisodeID + "&ORDER=" + nOrder + "&USER_ID=" + strUserID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);

            return resultJsonObject;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONArray getWriterChatComment(OkHttpClient httpClient, String strWriterID) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetWriterChat&WRITER_ID=" + strWriterID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);
            JSONArray resultArray = resultJsonObject.getJSONArray("COMMENT_LIST");

            return resultArray;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONArray getChatComment(OkHttpClient httpClient, int nChatID) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetChatComment&CHAT_ID=" + nChatID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);
            JSONArray resultArray = resultJsonObject.getJSONArray("COMMENT_LIST");

            return resultArray;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    //{"data":
    //  {"title":"새로운 작품의 게시 신청이 도착했습니다","body":"성대석 작가님이 작품 추가요 작품에 새로운 화를 게시 신청 하였습니다."},
    //  "to":[
    //  ["f8SFjnPTMio:APA91bFTq65rp5W1PzdVZ8vw185QTM1oBjKNTeLCZPHRHDMJd4YcQsWyQC8-0GNO7kI8jVqlEviC_E1ll9fwf_uw6d9v-RtG352iuDlp22UakGCXCuzenPWXKrvy5jbFmZE7WqHyQLzb","cABUQZ-72Q4:APA91bF-L3vhhwDkSbq8GMz4E8Q4_A1QZo-rzFkFpw2HWjzkPMT7NLOGf7uZMY_UJuirs4SPlm__1S1_Oe9JacGV5x5hlGwYG4dq6vuakNk9OFrj3XYxFjdKGrnnrHFMy442ycvAklwJ"]]}

    public static boolean setAlarmRead(OkHttpClient httpClient, String strUserID, int nAlarmID) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=SetAlarmRead&USER_ID=" + strUserID + "&ALARM_ID=" + nAlarmID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return false;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);

            if (resultJsonObject.getString("RESULT").equals("SUCCESS"))
                return true;
            else
                return false;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static ArrayList<AlarmVO> getAlarmList(OkHttpClient httpClient, String strUserID) {
        ArrayList<AlarmVO> resultList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetAlarmList&USER_ID=" + strUserID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);
            JSONArray jsonArray = resultJsonObject.getJSONArray("ALARM_LIST");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);

                AlarmVO vo = new AlarmVO();
                vo.setAlarmTitle(object.getString("ALARM_TITLE"));
                vo.setAlarmContents(object.getString("ALARM_CONTENTS"));
                vo.setnAlarmID(object.getInt("ALARM_ID"));
                vo.setnAlarmType(object.getInt("ALARM_TYPE"));
                vo.setStrObjectID(object.getString("OBJECT_ID"));
                vo.setStrDateTime(object.getString("CREATED_DATE"));
                vo.setbRead(object.getString("READ_YN").equals("Y") ? true : false);

                resultList.add(vo);
            }
            return resultList;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONObject requestEpisodeSubmit(OkHttpClient httpClient, int nEpisodeID) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=RequestEpisodeSubmit&EPISODE_ID=" + nEpisodeID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);
            return resultJsonObject;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONObject requestEpisodePost(OkHttpClient httpClient, int nEpisodeID) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=RequestEpisodePost&EPISODE_ID=" + nEpisodeID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);
            return resultJsonObject;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean requestDeleteWriterChat(OkHttpClient httpClient, int nCommentID) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=DeleteWriterChat&COMMENT_ID=" + nCommentID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return false;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);
            if (resultJsonObject.getString("RESULT").equals("SUCCESS"))
                return true;
            else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean requestLikeComment(OkHttpClient httpClient, int nCommentID, String strUserID) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=RequestLikeComment&COMMENT_ID=" + nCommentID + "&USER_ID=" + strUserID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return false;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);
            if (resultJsonObject.getString("RESULT").equals("SUCCESS"))
                return true;
            else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean requestReleaseLikeComment(OkHttpClient httpClient, int nCommentID, String strUserID) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=RequestReleseLikeComment&COMMENT_ID=" + nCommentID + "&USER_ID=" + strUserID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return false;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);
            if (resultJsonObject.getString("RESULT").equals("SUCCESS"))
                return true;
            else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean requestDeleteComment(OkHttpClient httpClient, int nCommentID, String strAdminID) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanBookAdmin.jsp?CMD=RequestDeleteComment&COMMENT_ID=" + nCommentID + "&ADMIN_ID=" + strAdminID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return false;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);
            if (resultJsonObject.getString("RESULT").equals("SUCCESS"))
                return true;
            else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean requestRemoveReport(OkHttpClient httpClient, int nCommentID) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=RemoveReport&COMMENT_ID=" + nCommentID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return false;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);
            if (resultJsonObject.getString("RESULT").equals("SUCCESS"))
                return true;
            else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static JSONObject requestDeleteCharacter(OkHttpClient httpClient, int nCharacterID) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=RequestDeleteCharacter&CHARACTER_ID=" + nCharacterID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);
            return resultJsonObject;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean requestDeleteAllCharacter(OkHttpClient httpClient, int nEpisodeID) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=RequestDeleteAllCharacter&EPISODE_ID=" + nEpisodeID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return false;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);

            if (resultJsonObject.getString("RESULT").equals("SUCCESS"))
                return true;
            else
                return false;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static JSONObject isKeepWork(OkHttpClient httpClient, String strUserID, String strWorkID) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=CheckKeepWork&USER_ID=" + strUserID + "&WORK_ID=" + strWorkID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);

            return resultJsonObject;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean checkInteractionSelect(OkHttpClient httpClient, String strUserID, int nWorkID) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=CheckInteraction&USER_ID=" + strUserID + "&WORK_ID=" + nWorkID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return false;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);

            if (resultJsonObject.getString("RESULT").equals("SUCCESS"))
                return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static int requestCommentReport(OkHttpClient httpClient, String strUserID, int nCommentID, String strReason) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=RequestCommentReport&USER_ID=" + strUserID + "&COMMENT_ID=" + nCommentID + "&REASON=" + strReason)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return -1;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);

            if (resultJsonObject.getString("RESULT").equals("SUCCESS"))
                return 0;
            else if (resultJsonObject.getString("RESULT").equals("DUPLICATE"))
                return 1;
            else
                return -1;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static int requestEpisodeReport(OkHttpClient httpClient, String strUserID, int nEpisodeID, String strReason) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=RequestEpisodeReport&USER_ID=" + strUserID + "&EPISODE_ID=" + nEpisodeID + "&REASON=" + strReason)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return -1;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);

            if (resultJsonObject.getString("RESULT").equals("SUCCESS"))
                return 0;
            else if (resultJsonObject.getString("RESULT").equals("DUPLICATE"))
                return 1;
            else
                return -1;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static boolean requestAproveCancel(OkHttpClient httpClient, String strAdminID, int nEpisodeID, String strReason) {
        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("ADMIN_ID", strAdminID);
            jsonBody.put("EPISODE_ID", "" + nEpisodeID);
            jsonBody.put("REASON", strReason);
            String jsonString = jsonBody.toString();

            RequestBody requestBody = RequestBody.create(JSON, jsonString);

            Request request = new Request.Builder()
                    .url(CommonUtils.strDefaultUrl + "TokkiAdminApproveCancel.jsp")
                    .post(requestBody)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.code() != 200)
                    return false;

                String strResult = response.body().string();
                JSONObject resultJsonObject = new JSONObject(strResult);

                if (resultJsonObject.getString("RESULT").equals("SUCCESS"))
                    return true;
                else
                    return false;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        Request request = new Request.Builder()
//                .url(CommonUtils.strDefaultUrl + "PanBookAdmin.jsp?CMD=RequestWorkAproveCancel&ADMIN_ID=" + strAdminID + "&EPISODE_ID=" + nEpisodeID + "&REASON=" + strReason)
//                .get()
//                .build();
//
//        try (Response response = httpClient.newCall(request).execute()) {
//            if (response.code() != 200)
//                return false;
//
//            String strResult = response.body().string();
//            JSONObject resultJsonObject = new JSONObject(strResult);
//
//            if (resultJsonObject.getString("RESULT").equals("SUCCESS"))
//                return true;
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        return false;
    }

    public static boolean requestWithdrawUser(OkHttpClient httpClient, String strUserID, String strAdminID, String strReason) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanBookAdmin.jsp?CMD=RequestWithdrawUser&ADMIN_ID=" + strAdminID + "&USER_ID=" + strUserID + "&REASON=" + strReason)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return false;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);

            if (resultJsonObject.getString("RESULT").equals("SUCCESS"))
                return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static JSONObject requestSetKeep(OkHttpClient httpClient, String strUserID, String strWorkID) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=SetKeep&USER_ID=" + strUserID + "&WORK_ID=" + strWorkID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);
            return resultJsonObject;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONObject requestUnKeep(OkHttpClient httpClient, String strUserID, String strWorkID) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=UnKeep&USER_ID=" + strUserID + "&WORK_ID=" + strWorkID)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);
            return resultJsonObject;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONObject requestNaverME(OkHttpClient httpClient, String strToken, String strTokenType) {
        Request request = new Request.Builder()
                .url("https://openapi.naver.com/v1/nid/me")
                .header("Authorization", strTokenType + " " + strToken)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);
            return resultJsonObject;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void checkSocialLogin(Activity activity, String strSNSID, String strFCMToken) {
        CommonUtils.showProgressDialog(activity, "서버와 접속중입니다. 잠시만 기다려 주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject resultObject = HttpClient.requestSocialLogin(new OkHttpClient(), strSNSID, strFCMToken);

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.hideProgressDialog();

                            if (resultObject == null) {
                                Toast.makeText(activity, "서버와의 연결이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            try {
                                if (resultObject.getString("RESULT").equals("SUCCESS")) {            // 이미 소셜 가입이 되어있는 경우
                                    String strUserID = resultObject.getString("USER_ID");
                                    String strUserName = resultObject.getString("USER_NAME");
                                    String strUserEmail = resultObject.getString("USER_EMAIL");
                                    String strUserPhoto = resultObject.getString("USER_PHOTO");
                                    String strUserPhoneNum = resultObject.getString("USER_PHONENUM");
                                    int nRegisterSNS = resultObject.getInt("REGISTER_SNS");
                                    String strSNSID = resultObject.getString("SNS_ID");
                                    String strUserDesc = resultObject.getString("USER_DESC");
                                    String strUserAdmin = resultObject.getString("USER_ADMIN");
                                    String strBirthday = resultObject.getString("USER_BIRTHDAY");
                                    int nGender = resultObject.getInt("USER_GENDER");
                                    int nCoinCount = 0;

                                    SharedPreferences pref = activity.getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = pref.edit();

                                    editor.putString("USER_ID", strUserID);
                                    editor.putString("USER_NAME", strUserName);
                                    editor.putString("USER_EMAIL", strUserEmail);
                                    editor.putString("USER_PHOTO", strUserPhoto);
                                    editor.putString("USER_PHONENUM", strUserPhoneNum);
                                    editor.putInt("REGISTER_SNS", nRegisterSNS);
                                    editor.putString("SNS_ID", strSNSID);
                                    editor.putString("ADMIN", strUserAdmin);
                                    editor.putString("USER_DESC", strUserDesc);
                                    editor.putString("USER_BIRTHDAY", strBirthday);
                                    editor.putInt("USER_GENDER", nGender);
                                    editor.putInt("COIN_COUNT", nCoinCount);

                                    editor.commit();

                                    activity.setResult(Activity.RESULT_OK);
                                    activity.finish();
//                                    Intent intent = new Intent(activity, MainActivity.class);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    activity.startActivity(intent);
                                } else {                                                                    // 신규 가입 해야할 때
                                    Intent intent = new Intent(activity, CommonPopup.class);
                                    intent.putExtra("TITLE", "회원 가입 안내");
                                    intent.putExtra("CONTENTS", "회원가입 되지 않은 소셜 계정입니다.\n간편 회원가입 하시겠습니까?");
                                    intent.putExtra("TWOBTN", true);
                                    activity.startActivityForResult(intent, 1010);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static boolean requestSendUserPhoto(OkHttpClient httpClient, String strUserID, String strFilePath) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM)
                .addFormDataPart("USER_ID", strUserID);

        File file = new File(strFilePath);
        String filename = strFilePath.substring(strFilePath.lastIndexOf("/") + 1);
        builder.addFormDataPart(filename, filename, RequestBody.create(MultipartBody.FORM, file));

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanbookUserProfile.jsp")
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return false;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);

            if (resultJsonObject.getString("RESULT").equals("SUCCESS"))
                return true;
            else
                return false;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean requestSendUserProfile(OkHttpClient httpClient, String strUserID, String strKey, String strValue) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM)
                .addFormDataPart("USER_ID", strUserID)
                .addFormDataPart(strKey, strValue);

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanbookUserProfile.jsp")
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return false;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);

            if (resultJsonObject.getString("RESULT").equals("SUCCESS"))
                return true;
            else
                return false;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean requestSendUserProfileImageDefault(OkHttpClient httpClient, String strUserID) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM)
                .addFormDataPart("USER_ID", strUserID)
                .addFormDataPart("USER_PHOTO", "null");

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "PanbookUserProfile.jsp")
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return false;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);

            if (resultJsonObject.getString("RESULT").equals("SUCCESS"))
                return true;
            else
                return false;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean getforbiddenWordsFromServer(OkHttpClient httpClient) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "ForbiddenWords.jsp")
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return true;

            CommonUtils.forbiddenWords = new ArrayList<>();

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);
            JSONArray resultArray = resultJsonObject.getJSONArray("SLANG_LIST");
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject slangObject = (JSONObject) resultArray.get(i);
                CommonUtils.forbiddenWords.add(slangObject.getString("SLANG"));
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static String getStoreVersion(OkHttpClient httpClient) {
        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "VersionCheck.jsp")
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultJsonObject = new JSONObject(strResult);
            String strVersion = resultJsonObject.getString("VERSION");

            return strVersion;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<String> getFolderName(OkHttpClient httpClient) {
        ArrayList<String> resultList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "TokkiGallery.jsp?CMD=FOLDERS")
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            JSONArray resultArray = resultObject.getJSONArray("FOLDER_LIST");

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject object = resultArray.getJSONObject(i);
                resultList.add(object.getString("FOLDER_NAME"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            resultList = null;
        } catch (JSONException e) {
            e.printStackTrace();
            resultList = null;
        }

        return resultList;
    }

    public static ArrayList<String> getTokkiGalleryData(OkHttpClient httpClient, String folderName) {
        ArrayList<String> resultList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CommonUtils.strDefaultUrl + "TokkiGallery.jsp?CMD=IMAGES&FOLDER=" + folderName)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200)
                return null;

            String strResult = response.body().string();
            JSONObject resultObject = new JSONObject(strResult);

            JSONArray resultArray = resultObject.getJSONArray("FILE_LIST");

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject object = resultArray.getJSONObject(i);
                resultList.add(object.getString("FILE_NAME"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            resultList = null;
        } catch (JSONException e) {
            e.printStackTrace();
            resultList = null;
        }

        return resultList;
    }
}
