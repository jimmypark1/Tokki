package com.Whowant.Tokki.Utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.Whowant.Tokki.VO.CharacterVO;
import com.Whowant.Tokki.VO.ChatVO;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ExcelReader {                                                                              // 엑셀로 작품 업로드할때 사용하는 클래스
    private FileInputStream fis = null;
    private int nEpisodeID;
    private Activity mActivity;

    public ExcelReader(Activity activity, int nEpisodeID) {
        mActivity = activity;
        this.nEpisodeID = nEpisodeID;
    }

    public void readExcel(String strExcelPath, boolean bInteraction) throws IOException, InvalidFormatException {
        File excelFile = new File(strExcelPath);
        if(!excelFile.exists())
            return;

        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");

        Workbook wb = WorkbookFactory.create(excelFile);

        ArrayList<CharacterVO> characterVOArrayList = new ArrayList<>();
        ArrayList<ChatVO> chatVOArrayList = new ArrayList<>();
        String strEpisodeTitle = "";

        for(int sheetIndex = 0 ; sheetIndex < wb.getNumberOfSheets() ; sheetIndex++) {
            Sheet currentSheet = wb.getSheetAt(sheetIndex);

            for(int rowIndex = 0 ; rowIndex < currentSheet.getPhysicalNumberOfRows() ; rowIndex ++) {
                Row currentRow = currentSheet.getRow(rowIndex);
                if(currentRow == null) {
                    continue;
                }

                if(rowIndex == 0) {
                    Cell currentCell = currentRow.getCell(0);
                    strEpisodeTitle = currentCell.getStringCellValue();
                    continue;
                }

                CharacterVO characterVO = null;
                ChatVO chatVO = null;

                for(int cellIndex = 0 ; cellIndex < 5 ; cellIndex ++) {
                    Cell currentCell = currentRow.getCell(cellIndex);
                    String strContents = "";

                    if(currentCell == null)
                        strContents = "";
                    else if(currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        double nContents = currentCell.getNumericCellValue();
                        strContents = "" + nContents;
                    } else if(currentCell.getCellTypeEnum() == CellType.STRING)
                        strContents = currentCell.getStringCellValue();

                    Log.d("CONTENTS", strContents);
                    if(cellIndex == 0) {                // 첫번째 셀이라면
                        if(strContents != null && strContents.startsWith("인물")) {               // 인물 설정 row
                            characterVO = new CharacterVO();
                        } else {                                                                // 채팅 row
                            chatVO = new ChatVO();

                            if(strContents != null && strContents.length() > 0) {
                                String strName = strContents;

                                for(int i = 0 ; i < characterVOArrayList.size() ; i++) {
                                    CharacterVO vo = characterVOArrayList.get(i);

                                    if(strName.equals(vo.getName())) {
                                        chatVO.setCharacter(vo);
                                        break;
                                    }
                                }
                            }
                        }
                    } else {
                        if(chatVO == null) {                            // 첫번째 행이 아닌데 채팅 정보가 없으면 인물 설정 row 로 판단
                            if(cellIndex == 1) {                // 이름
                                if(strContents != null)
                                    characterVO.setName(strContents);
                            } else if(cellIndex == 2) {
                                if(strContents.equals("왼쪽"))
                                    characterVO.setDirection(0);
                                else
                                    characterVO.setDirection(1);
                            } else if(cellIndex == 3) {
                                if(strContents != null && strContents.length() > 0) {
                                    characterVO.setStrImgFile(strContents);
                                }

                                if(characterVO != null)
                                    characterVOArrayList.add(characterVO);
                                characterVO = null;
                            }
                        } else {                                        // 채팅 row 로 판단
                            if(cellIndex == 1) {
                                if(strContents != null && strContents.length() > 0)
                                    chatVO.setContents(strContents);
                            } else if(cellIndex == 2) {
                                chatVO.setType(1);

                                if(strContents != null && strContents.length() > 0) {
                                    if(strContents.equals("배경이미지전환")) {
                                        chatVO.setType(8);
                                    } else if(strContents.equals("이미지")) {
                                        chatVO.setType(3);
                                    } else if(strContents.equals("동영상")) {
                                        chatVO.setType(4);
                                    } else if(strContents.equals("음원")) {
                                        chatVO.setType(5);
                                    } else if(strContents.equals("배경변경")) {
                                        chatVO.setType(6);
                                    } else if(strContents.equals("분기설정")) {
                                        chatVO.setType(7);

                                        if(bInteraction) {
                                            mActivity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(mActivity, "이미 분기가 설정된 작품 입니다. 분기를 추가할 수 없습니다.", Toast.LENGTH_LONG).show();
                                                }
                                            });

                                            return;
                                        }
                                    }
                                } else if(chatVO.getCharacterVO() == null) {
                                    chatVO.setType(2);
                                }
                            } else if(cellIndex == 3 ) {
                                if(strContents != null && strContents.length() > 0) {
                                    chatVO.setStrContentsFile(strContents);
                                }
                            } else if(cellIndex == 4) {
                                if(strContents != null && strContents.length() > 0) {
                                    if(strContents.contains(".")) {
                                        strContents = strContents.substring(0, strContents.indexOf("."));
                                    }
                                    chatVO.setnInteractionNum(Integer.valueOf(strContents));
//                                    chatVO.setnOrder(chatVOArrayList.size());
                                    chatVOArrayList.add(chatVO);
                                    chatVO = null;
                                }
                            }
                        }
                    }
                }
            }
        }

        requestCreateCharacter(characterVOArrayList, chatVOArrayList, strEpisodeTitle);
    }

    private void requestCreateCharacter(final ArrayList<CharacterVO> characterVOList, final ArrayList<ChatVO> chatVOArrayList, final String strEpisodeTitle) {
        try {
            String url = CommonUtils.strDefaultUrl + "PanAppCreateCharacterWithJson.jsp";
            MultipartBody.Builder builder = new MultipartBody.Builder();

            String strJson = "{\"CHARACTER_ARRAY\":[";

            for(int i = 0 ; i < characterVOList.size() ; i++) {
                if(i > 0)
                    strJson += ", ";

                CharacterVO vo = characterVOList.get(i);
                strJson += "{\"CHARACTER_NAME\":\"" + vo.getName() + "\", \"CHARACTER_DIRECTION\":" + vo.getDirection() + ", \"CHARACTER_IMAGE\":\"" + vo.getStrImgFile() + "\"}";
            }

            strJson += "]}";

            RequestBody requestBody = builder.setType(MultipartBody.FORM)
                    .addFormDataPart("EPISODE_ID", "" + nEpisodeID)
                    .addFormDataPart("UPLOAD_DATA", strJson).build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            new OkHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Toast.makeText(mActivity, "캐릭터 생성을 실패하였습니다.", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String strResult = response.body().string();

                    try {
                        JSONObject resultObject = new JSONObject(strResult);

                        if(resultObject.getString("RESULT").equals("SUCCESS")) {
                            JSONArray chatArray = resultObject.getJSONArray("CHARACTER_LIST");

                            for(int i = 0 ; i < chatArray.length() ; i++) {
                                JSONObject object = chatArray.getJSONObject(i);
                                CharacterVO characterVO = new CharacterVO();

                                characterVO.setnCharacterID(object.getInt("CHARACTER_ID"));
                                characterVO.setName(object.getString("CHARACTER_NAME"));
                                characterVO.setStrImgFile(object.getString("CHARACTER_IMG"));

                                if(object.getString("BALLOON_COLOR") != null && !object.getString("BALLOON_COLOR").equals("null"))
                                    characterVO.setStrBalloonColor(object.getString("BALLOON_COLOR"));

                                characterVO.setDirection(object.getInt("CHARACTER_DIRECTION"));

                                for(int j = 0 ; j < chatVOArrayList.size() ; j++) {
                                    ChatVO vo = chatVOArrayList.get(j);

                                    if(vo.getCharacterVO() == null)
                                        continue;

                                    if(characterVO.getName().equals(vo.getCharacterVO().getName())) {
                                        vo.getCharacterVO().setnCharacterID(characterVO.getnCharacterID());
                                        break;
                                    }
                                }
                            }

                            requestSendEpisode(chatVOArrayList, strEpisodeTitle);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestSendEpisode(ArrayList<ChatVO> chatVOArrayList, String strEpisodeTitle) {                   // 한번에 업로드. 엑셀 업로드 할때 사용해보자꾸나
        try {
            String url = CommonUtils.strDefaultUrl + "PanAppCreateEpisode.jsp";
            RequestBody requestBody = null;

            File sourceFile = null;

            JSONObject sendObject = new JSONObject();

            if(nEpisodeID > -1)
                sendObject.put("EPISODE_ID", nEpisodeID);

            sendObject.put("EPISODE_TITLE", strEpisodeTitle);

            JSONArray chatArray = new JSONArray();

            for(int i = 0 ; i < chatVOArrayList.size() ; i++) {
                JSONObject chatObject = new JSONObject();
                ChatVO vo = chatVOArrayList.get(i);

                int nType = vo.getType();
                chatObject.put("EPISODE_ID", nEpisodeID);
                chatObject.put("CHAT_TYPE", nType);

                if(vo.getCharacterVO() != null)
                    chatObject.put("CHARACTER_ID", vo.getCharacterVO().getnCharacterID());

                chatObject.put("INTERACTION", vo.getnInteractionNum());
//                chatObject.put("CHAT_ORDER", i);

                if(nType == 1 || nType == 2 || nType == 7) {
                    chatObject.put("CHAT_CONTENTS", vo.getContents());
                } else if(nType == 3 || nType == 4 || nType == 5 || nType == 6 || nType == 8) {
                    chatObject.put("CHAT_CONTENTS", vo.getStrContentsFile());
                }

                chatArray.put(chatObject);
            }

            sendObject.put("CHAT_ARRAY", chatArray);

            MultipartBody.Builder builder = new MultipartBody.Builder();

            builder.setType(MultipartBody.FORM)
                    .addFormDataPart("JSON_BODY", sendObject.toString());

            requestBody = builder.build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            new OkHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mActivity, "작품 등록을 실패하였습니다.", Toast.LENGTH_LONG).show();
                            mActivity.sendBroadcast(new Intent("ACTION_EXCEL_DONE"));
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String strResult = response.body().string();

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(strResult.contains("SUCCESS")) {
                                Toast.makeText(mActivity, "작품이 등록 되었습니다..", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(mActivity, "작품 등록을 실패하였습니다.", Toast.LENGTH_LONG).show();
                            }

                            mActivity.sendBroadcast(new Intent("ACTION_EXCEL_DONE"));
                        }
                    });


                    // response.body().string()
                }
            });
        } catch (Exception e) {
            e.printStackTrace();

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mActivity, "작품 등록을 실패하였습니다.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}