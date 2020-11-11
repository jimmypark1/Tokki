package com.Whowant.Tokki.UI.Activity.Work;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.DrawerMenu.NoticeActivity;
import com.Whowant.Tokki.UI.Activity.Media.VideoPlayerActivity;
import com.Whowant.Tokki.UI.Activity.Photopicker.PhotoPickerActivity;
import com.Whowant.Tokki.UI.Popup.BGImageSelectPopup;
import com.Whowant.Tokki.UI.Popup.ChangeTitlePopup;
import com.Whowant.Tokki.UI.Popup.CommonPopup;
import com.Whowant.Tokki.UI.Popup.DistractorPopup;
import com.Whowant.Tokki.UI.Popup.LegalNoticePopup;
import com.Whowant.Tokki.UI.Popup.MediaSelectPopup;
import com.Whowant.Tokki.UI.Popup.SlangPopup;
import com.Whowant.Tokki.UI.Popup.TextEditPopup;
import com.Whowant.Tokki.UI.Popup.WorkPostPopup;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.CustomUncaughtExceptionHandler;
import com.Whowant.Tokki.Utils.ExcelReader;
import com.Whowant.Tokki.Utils.SoftKeyboard;
import com.Whowant.Tokki.VO.CharacterVO;
import com.Whowant.Tokki.VO.ChatVO;
import com.Whowant.Tokki.VO.EpisodeVO;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LiteratureWriteActivity extends AppCompatActivity implements View.OnClickListener, ColorPickerDialogListener {                             // 작품 작성창
    public static WorkVO workVO;
    private ArrayList<CharacterVO> characterList;                                                                                                       // 작품에 등장하는 캐릭터 리스트
    private ArrayList<ChatVO> chattingList;                                                                                                             // 작품 말풍선 리스트(실제 작품 내용)
    private HashMap<String, Bitmap> thumbBitmapList = new HashMap<>();                                                                                  // 동영상 썸네일 이미지는 새로 로딩하지 않고 저장하고 사용

    private LinearLayout speakerAddLayout;                                                                                                              // 하단에 좌우 스크롤되는 등장인물 목록 화면
    private ArrayList<String> nameList;                                                                                                                 // 등장인물 이름 리스트
    private ArrayList<View> characterViewList;                                                                                                          // 등장인물들 view List
    private int nSelectedCharacterIndex = 0;                                                                                                            // 0 = 나레이
    private boolean bShowMenu = false;                                                                                                                  // 하단 메뉴가 보여지는지 여부
    private ConstraintLayout bottomSettingLayout;                                                                                                       // 하단 메뉴 Layout
    private EditText inputTextView;
    private InputMethodManager imm;
    private ImageButton contentsAddBtn;
    private ListView chattingListView;                                                                                                                  // 대화가 보여지는 ListView
    private CChattingArrayAdapter aa;

    private LinearLayout bgSettingView;                                                                                                                 // 배경 첨부 버튼
    private LinearLayout imgSettingView;                                                                                                                // 이미지 첨부 버튼
    private LinearLayout videoSettingView;                                                                                                              // 동영상 첨부 버튼
    private LinearLayout distractorView;
    private LinearLayout soundSettingView;

    private int    nBgColor;
    private String bgColor;
    private boolean bColorPicker = false;

    private ImageView bgView;

    private int nEpisodeID;
    private int nInteractionIndex;              // 1회차, 2회차 등 회차의 순서
    private int nAddIndex = -1;             // 사이에 추가하기 넘버
    private int nEditIndex = -1;            // 수정할때 넘버
    private int nDeleteIndex = -1;
    private ProgressDialog mProgressDialog;

    private TextView titleView, episodeNumView;
    private ArrayList<String> newFileList;
    private boolean isEdit = false;
    private String strTitle = "";

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Timer timer;
    private int nPlayingIndex = -1;                     // 동영상 재생/일시정지를 위한 현재 재생중인 영상의 순서
    private ImageView oldPlayBtn;                       // 한개만 재생시키기 위해 다른 영상을 클릭하면 전 영상을 멈추기 위해 버튼 객체 저장
    private ProgressBar oldPB;
    private boolean bSubmit = false;                    // 회차가 제출(심사요청) 되었는지 여부
    private Button sendBtn;

    private float fX, fY;                               // 롱클릭 등을 위해 터치 좌표 저장

    private ViewGroup viewGroup;
    private SoftKeyboard softKeyboard;
    private boolean isExcelUploaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_renewal_layout);

        strTitle = getIntent().getStringExtra("EPISODE_TITLE");
        if(getIntent().getStringExtra("SUBMIT").equals("Y")) {
            bSubmit = true;
        } else {
            bSubmit = false;
        }

        isExcelUploaded = getIntent().getBooleanExtra("EXCEL_UPLOADED", false);
        ImageButton submitBtn = findViewById(R.id.submitBtn);

        if(isExcelUploaded || workVO.getnUserStatus() == 10 || workVO.getnUserStatus() == 20) {
            submitBtn.setBackgroundResource(R.drawable.send_button);
        } else {
            submitBtn.setBackgroundResource(R.drawable.post_botton);
        }

        int nKeyboard = getResources().getConfiguration().keyboard;
        Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
        if (!(handler instanceof CustomUncaughtExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
        }

        IntentFilter filter = new IntentFilter();                                                   // 엑셀 업로드 시 업로드 완료 이벤트를 받기위한 리시버 등록
        filter.addAction("ACTION_EXCEL_DONE");
        registerReceiver(mReceiver, filter);

        titleView = findViewById(R.id.episodeTitleView);
        episodeNumView = findViewById(R.id.episodeNumView);
        newFileList = new ArrayList<>();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        nEpisodeID = getIntent().getIntExtra("EPISODE_ID", -1);
        nInteractionIndex = getIntent().getIntExtra("EPISODE_INDEX", -1);
        titleView.setText(strTitle);
        episodeNumView.setText((nInteractionIndex+1) + "화");
        characterList = new ArrayList<>();
        nameList = new ArrayList<>();
        characterViewList = new ArrayList<>();
        characterList.add(null);
        nameList.add("지문");
        LinearLayout speakerAddView = findViewById(R.id.speakerAddView);
        speakerAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateCharacterActivity.nameList = new ArrayList<String>(nameList);
                startActivityForResult(new Intent(LiteratureWriteActivity.this, CreateCharacterActivity.class), 1010);
            }
        });

        nBgColor = ContextCompat.getColor(LiteratureWriteActivity.this, R.color.colorDefaultBG);
        bgColor = String.format("#%06X", (0xFFFFFF & nBgColor));
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        speakerAddLayout = findViewById(R.id.speakerAddLayout);
        bottomSettingLayout = findViewById(R.id.bottomSettingLayout);
        inputTextView = findViewById(R.id.inputTextView);
        contentsAddBtn = findViewById(R.id.contentsAddBtn);
        chattingListView = findViewById(R.id.chattingListView);
        bgView = findViewById(R.id.bgView);
        bgSettingView = findViewById(R.id.bgSettingView);
        imgSettingView = findViewById(R.id.imgSettingView);
        videoSettingView = findViewById(R.id.videoSettingView);
        distractorView = findViewById(R.id.distractorView);
        soundSettingView = findViewById(R.id.soundSettingView);

        inputTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(inputTextView.getText().toString().length() > 0) {
                    sendBtn.setBackgroundResource(R.drawable.common_btn_bg);
                    sendBtn.setEnabled(true);
                } else {
                    sendBtn.setBackgroundResource(R.drawable.common_btn_disable_bg);
                    sendBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        resetCharacterLayout();

        if(nKeyboard != 2)
            setKeyboardEvent();

        chattingList = new ArrayList<>();
        aa = new CChattingArrayAdapter(LiteratureWriteActivity.this, R.layout.right_chatting_row, chattingList);
        chattingListView.setAdapter(aa);
        chattingListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {                       // 롱클릭시 동작
                nEditIndex = position;
                ChatVO vo = chattingList.get(position);
                int nType = vo.getType();
                if(nType == ChatVO.TYPE_TEXT || nType == ChatVO.TYPE_NARRATION) {                                               // 일반 텍스트 채팅 혹은 나레이션 이라면 문구 수정창
                    Intent intent = new Intent(LiteratureWriteActivity.this, TextEditPopup.class);
                    intent.putExtra("TEXT", vo.getContents());
                    intent.putExtra("ORDER", position);
                    startActivityForResult(intent, 1050);
                } else if(nType == ChatVO.TYPE_IMAGE || nType == ChatVO.TYPE_VIDEO) {
                    TedPermission.with(LiteratureWriteActivity.this).setPermissionListener(new PermissionListener() {   // 이미지 혹은 영상이라면 퍼미션 확인 후 이미지/영상 선택 화면으로
                        @Override
                        public void onPermissionGranted() {
                            Intent intent = new Intent(LiteratureWriteActivity.this, MediaSelectPopup.class);
                            if(nType == ChatVO.TYPE_IMAGE)
                                intent.putExtra("TYPE", PhotoPickerActivity.TYPE_CONTENTS_IMG);
                            else
                                intent.putExtra("TYPE", PhotoPickerActivity.TYPE_VIDEO);

                            intent.putExtra("EDIT", true);
                            intent.putExtra("ORDER", position);
                            startActivityForResult(intent, 2000);
                        }

                        @Override
                        public void onPermissionDenied(List<String> deniedPermissions) {
                            Toast.makeText(LiteratureWriteActivity.this, "권한을 허용해주셔야 사진 설정이 가능합니다.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                    .check();
                } else if(nType == ChatVO.TYPE_SOUND) {                                                                             // 음원파일 이라면 퍼미션 확인 후 음원파일 선택 화면으로
                    TedPermission.with(LiteratureWriteActivity.this)
                            .setPermissionListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted() {
                                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                    intent.setType("audio/*");
                                    startActivityForResult(Intent.createChooser(intent, "Music File"), 1035);
                                }

                                @Override
                                public void onPermissionDenied(List<String> deniedPermissions) {
                                    Toast.makeText(LiteratureWriteActivity.this, "권한을 허용해주셔야 사진 설정이 가능합니다.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                            .check();
                } else if(nType == ChatVO.TYPE_CHANGE_BG || nType == ChatVO.TYPE_CHANGE_BG_COLOR) {                                 // 배경화면 변경이라면 해당 화면으로 이동
                    Intent intent = new Intent(LiteratureWriteActivity.this, BGImageSelectPopup.class);
                    intent.putExtra("EDIT", true);
                    intent.putExtra("ORDER", position);
                    startActivityForResult(intent, 1000);
                } else if(nType == ChatVO.TYPE_DISTRACTOR) {                                                                        // 분기 부분이라면 분기 팝업 호출
                    Intent intent = new Intent(LiteratureWriteActivity.this, DistractorPopup.class);
                    intent.putExtra("EDIT", true);
                    intent.putExtra("ORDER", position);
                    intent.putExtra("TEXT", vo.getContents());
                    startActivityForResult(intent, 1025);
                } else if(nType == ChatVO.TYPE_IMAGE_NAR) {                                                                         // 나레이션 이미지 라면 이미지 선택 화면으로 이동
                    Intent intent = new Intent(LiteratureWriteActivity.this, MediaSelectPopup.class);
                    intent.putExtra("TYPE", PhotoPickerActivity.TYPE_CONTENTS_IMG_NAR);
                    intent.putExtra("EDIT", true);
                    intent.putExtra("ORDER", position);
                    startActivity(intent);
                }

                return false;
            }
        });

        bgSettingView.setOnClickListener(this);
        imgSettingView.setOnClickListener(this);
        videoSettingView.setOnClickListener(this);
        distractorView.setOnClickListener(this);
        soundSettingView.setOnClickListener(this);

        if(nInteractionIndex > -1) {                                                        // 이게 있다면 분기 설정된 회차이므로 분기 작성 화면으로 이동
            EpisodeVO episodeVO = workVO.getEpisodeList().get(nInteractionIndex);
            if(episodeVO.getIsDistractor() == true) {
                Intent intent = new Intent(LiteratureWriteActivity.this, InteractionWriteActivity.class);
                InteractionWriteActivity.workVO = workVO;
                intent.putExtra("TITLE", strTitle);
                intent.putExtra("EPISODE_ID", nEpisodeID);
                intent.putExtra("EPISODE_INDEX", nInteractionIndex);
                startActivity(intent);
                finish();
                return;
            }
        }

        if(nEpisodeID > -1) {
            getCharacterData();
        }

        sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strContents = inputTextView.getText().toString();
                CommonUtils.showProgressDialog(LiteratureWriteActivity.this, "서버와 통신중입니다. 잠시만 기다려주세요.");

                String strFobiddenWords = CommonUtils.checkForbiddenWords(strContents);
                if(strFobiddenWords.length() > 0) {
                    CommonUtils.hideProgressDialog();
                    Intent intent = new Intent(LiteratureWriteActivity.this, SlangPopup.class);
                    intent.putExtra("SLANG", strFobiddenWords);
                    startActivity(intent);
                    overridePendingTransition(R.anim.cross_fade_in, R.anim.cross_fade_out);
                    return;
                }

                CommonUtils.hideProgressDialog();

                if(strContents.length() == 0) {
                    Toast.makeText(LiteratureWriteActivity.this, "내용을 입력하세요.", Toast.LENGTH_LONG).show();
                    return;
                }

                ChatVO chatVO = new ChatVO();

                if(nSelectedCharacterIndex == 0) {                   // 나레이션
                    chatVO.setType(ChatVO.TYPE_NARRATION);
                } else {                                             // 사람이 선택돼있을 경우
                    chatVO.setType(ChatVO.TYPE_TEXT);
                    CharacterVO characterVO = characterList.get(nSelectedCharacterIndex);
                    chatVO.setCharacter(characterVO);
                }

                if(nEditIndex > -1) {
                    chatVO = chattingList.get(nEditIndex);
                } else {
                    if(nAddIndex > -1) {
                        ChatVO currentVO = chattingList.get(nAddIndex);
                        int nIndex = 0;

                        if(currentVO.getType() != ChatVO.TYPE_EMPTY) {
                            nIndex = currentVO.getnOrder();
                            chatVO.setnOrder(nIndex+1);
                        }
                    } else {
                        if(chattingList.size() == 0)
                            chatVO.setnOrder(0);
                        else {
                            ChatVO currentVO = chattingList.get(chattingList.size()-1);
                            int nIndex = currentVO.getnOrder();
                            chatVO.setnOrder(nIndex+1);
                        }
                    }
                }

                chatVO.setContents(strContents);
                requestUploadMessage(chatVO, false);
            }
        });

        if(bSubmit) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LiteratureWriteActivity.this);
            builder.setMessage("이미 게시된 작품입니다. 게시된 작품을 수정하시면 게시 취소가 되어 다시 제출하셔야 합니다.");
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int id) {
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        chattingListView.setOnTouchListener(new View.OnTouchListener()
        {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    fX = motionEvent.getX();
                    fY = motionEvent.getY();
                } else if(motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                    float fEndX = motionEvent.getX();
                    float fEndY = motionEvent.getY();

                    if(fX >= fEndX + 10 || fX <= fEndX - 10 || fY >= fEndY + 10 || fY <= fEndY - 10) {              // 10px 이상 움직였다면
                        return false;
                    } else {
                        imm.hideSoftInputFromWindow(inputTextView.getWindowToken(), 0);
                    }
                }
                return false;
            }
        });

        startActivity(new Intent(this, LegalNoticePopup.class));
        overridePendingTransition(R.anim.cross_fade_in, R.anim.cross_fade_out);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {           // BT 키보드 접속됨
            removeKeyboardEvent();
        }
        else if(newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {     // BT 키보드 해제됨
            setKeyboardEvent();
        }
    }

    private void setKeyboardEvent() {
        hideBottomView();
        viewGroup = (ViewGroup) ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0);
        softKeyboard = new SoftKeyboard(viewGroup, imm);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
            @Override
            public void onSoftKeyboardHide() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }

            @Override
            public void onSoftKeyboardShow() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        hideBottomView();
                    }
                });
            }
        });
    }

    private void removeKeyboardEvent() {
        softKeyboard.setSoftKeyboardCallback(null);
    }

    @Override
    public void onBackPressed() {
        if(bShowMenu) {
            bottomSettingLayout.setVisibility(View.GONE);
            contentsAddBtn.setImageResource(R.drawable.selectionplus);
            bShowMenu = false;
            return;
        }

        super.onBackPressed();
    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }

    public void onClickSubmitBtn(View view) {
        if(isExcelUploaded || workVO.getnUserStatus() == 10 || workVO.getnUserStatus() == 20) {
            requestEpisodeSubmit();
        } else {
            requestEpisodePost();
        }
    }

    public void onClickTopRightBtn(View view) {                                                             // 작품을 앱 안에서 작성하지 않고 미리 만들어둔 엑셀 파일로 업로드 하는 기능
        PopupMenu popup = new PopupMenu(LiteratureWriteActivity.this, view);
        popup.getMenuInflater().inflate(R.menu.work_write_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = null;

                switch(item.getItemId()) {
                    case R.id.action_btn2:
                        if(chattingList.size() > 1) {
                            Toast.makeText(LiteratureWriteActivity.this, "입력된 내용이 없어야만 엑셀 파일을 로딩할 수 있습니다.", Toast.LENGTH_LONG).show();
                            return true;
                        } else if(characterList.size() > 1) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LiteratureWriteActivity.this);
                            builder.setMessage("이미 저장된 등장인물이 있습니다. 엑셀 파일을 로딩하면 저장된 등장인물은 모두 삭제됩니다.\n정말 엑셀파일을 로딩하시겠습니까?");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    filePermission();
                                }
                            });

                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                            return true;
                        }

                        filePermission();

                        return true;
                    case R.id.action_btn3:
                        intent = new Intent(LiteratureWriteActivity.this, ViewerActivity.class);
                        ViewerActivity.workVO = workVO;
                        intent.putExtra("EPISODE_INDEX", nInteractionIndex);
                        intent.putExtra("INTERACTION", workVO.getEpisodeList().get(nInteractionIndex).getIsDistractor());
                        intent.putExtra("PREVIEW", true);
                        startActivity(intent);
                        return true;
                    case R.id.action_btn4:
                        AlertDialog.Builder builder = new AlertDialog.Builder(LiteratureWriteActivity.this);
                        builder.setTitle("회차 삭제");
                        builder.setMessage("회차의 모든 내용이 삭제됩니다.\n삭제하시겠습니까?");
                        builder.setPositiveButton("예", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                requestDeleteAllMessage();
                            }
                        });

                        builder.setNegativeButton("취소", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        return true;
                }
                return true;
            }
        });

        popup.show();//showing popup menu
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        boolean bEdit = getIntent().getBooleanExtra("EDIT", false);
        int     nOrder = getIntent().getIntExtra("ORDER", -1);

        String imgUri = intent.getStringExtra("BG_URI");
        if(imgUri != null) {                    // 배경 변경 이라면
            String strFilePath = CommonUtils.getRealPathFromURI(LiteratureWriteActivity.this, Uri.parse(imgUri));
            newFileList.add(strFilePath);

            ChatVO chatVO = null;

            if(bEdit) {
                chatVO = chattingList.get(nEditIndex);
            } else {
                chatVO = new ChatVO();
                if(nAddIndex > -1) {
                    ChatVO currentVO = chattingList.get(nAddIndex);
                    int nIndex = 0;

                    if(currentVO.getType() != ChatVO.TYPE_EMPTY) {
                        nIndex = currentVO.getnOrder();
                        chatVO.setnOrder(nIndex+1);
                    }
                } else {
                    if(chattingList.size() == 0)
                        chatVO.setnOrder(0);
                    else {
                        ChatVO currentVO = chattingList.get(chattingList.size()-1);
                        int nIndex = currentVO.getnOrder();
                        chatVO.setnOrder(nIndex+1);
                    }
                }
            }

            chatVO.setType(ChatVO.TYPE_CHANGE_BG);
            chatVO.setContentsUri(Uri.parse(imgUri));
            chatVO.setStrContentsFile(CommonUtils.getRealPathFromURI(LiteratureWriteActivity.this, Uri.parse(imgUri)));

            Glide.with(LiteratureWriteActivity.this)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(imgUri)
                    .transition(BitmapTransitionOptions.withCrossFade(300))
                    .into(bgView);

            requestUploadMessage(chatVO, bEdit);

            nEditIndex = -1;
            nAddIndex = -1;
            return;
        }

        imgUri = intent.getStringExtra("IMG_URI");
        if(imgUri != null) {                                    // 일반 채팅 이미지 라면
            int nType = intent.getIntExtra("TYPE", 0);
            ChatVO chatVO = null;

            if(bEdit) {
                chatVO = chattingList.get(nEditIndex);
            } else {
                chatVO = new ChatVO();
                if(nAddIndex > -1) {
                    ChatVO currentVO = chattingList.get(nAddIndex);
                    int nIndex = 0;

                    if(currentVO.getType() != ChatVO.TYPE_EMPTY) {
                        nIndex = currentVO.getnOrder();
                        chatVO.setnOrder(nIndex+1);
                    }
                } else {
                    if(chattingList.size() == 0)
                        chatVO.setnOrder(0);
                    else {
                        ChatVO currentVO = chattingList.get(chattingList.size()-1);
                        int nIndex = currentVO.getnOrder();
                        chatVO.setnOrder(nIndex+1);
                    }
                }
            }

            if(nType == PhotoPickerActivity.TYPE_CONTENTS_IMG) {
                CharacterVO characterVO = characterList.get(nSelectedCharacterIndex);
                chatVO.setCharacter(characterVO);
                chatVO.setType(ChatVO.TYPE_IMAGE);
            } else if(nType == PhotoPickerActivity.TYPE_CONTENTS_IMG_NAR) {
                chatVO.setType(ChatVO.TYPE_IMAGE_NAR);
            }

            chatVO.setContentsUri(Uri.parse(imgUri));
            chatVO.setStrContentsFile(CommonUtils.getRealPathFromURI(LiteratureWriteActivity.this, Uri.parse(imgUri)));
            requestUploadMessage(chatVO, bEdit);

            nEditIndex = -1;
            nAddIndex = -1;
            return;
        }

        imgUri = intent.getStringExtra("VIDEO_URI");
        if(imgUri != null) {
            ChatVO chatVO = null;

            if(bEdit) {
                chatVO = chattingList.get(nEditIndex);
            } else {
                chatVO = new ChatVO();
                if(nAddIndex > -1) {
                    ChatVO currentVO = chattingList.get(nAddIndex);
                    int nIndex = 0;

                    if(currentVO.getType() != ChatVO.TYPE_EMPTY) {
                        nIndex = currentVO.getnOrder();
                        chatVO.setnOrder(nIndex+1);
                    }
                } else {
                    if(chattingList.size() == 0)
                        chatVO.setnOrder(0);
                    else {
                        ChatVO currentVO = chattingList.get(chattingList.size()-1);
                        int nIndex = currentVO.getnOrder();
                        chatVO.setnOrder(nIndex+1);
                    }
                }
            }

            CharacterVO characterVO = characterList.get(nSelectedCharacterIndex);
            chatVO.setCharacter(characterVO);

            chatVO.setType(ChatVO.TYPE_VIDEO);
            chatVO.setContentsUri(Uri.parse(imgUri));
            chatVO.setStrContentsFile(CommonUtils.getRealPathFromURI(LiteratureWriteActivity.this, Uri.parse(imgUri)));
            requestUploadMessage(chatVO, bEdit);

            nEditIndex = -1;
            nAddIndex = -1;
            return;
        }

        nEditIndex = -1;
        nAddIndex = -1;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(bColorPicker) {
            bColorPicker = false;
            ColorPickerDialog.newBuilder()
                    .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                    .setAllowPresets(false)
                    .setDialogId(1010)
                    .setColor(nBgColor)
                    .setShowAlphaSlider(true)
                    .show(LiteratureWriteActivity.this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if(mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer = null;
            timer.cancel();
            timer = null;
            oldPlayBtn = null;
            oldPB = null;
            nPlayingIndex = -1;
        }
    }

    @Override
    public void onDestroy() {
//        workVO = null;
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private void filePermission() {
        TedPermission.with(LiteratureWriteActivity.this)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/*");
                        startActivityForResult(Intent.createChooser(intent, "Excel Files"), 1040);
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {

                    }
                })
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    private void requestUploadMessage(final ChatVO chatVO, final boolean bEdit) {
        if(!bEdit)
            nEditIndex = -1;
        if(mediaPlayer != null && mediaPlayer.isPlaying()) {                // 영상 등 재생하는 부분이 있다면 올스톱
            mediaPlayer.stop();
            oldPlayBtn.setImageResource(R.drawable.talk_play1);
            oldPlayBtn = null;
            oldPB.setProgress(0);
            oldPB = null;
            timer.cancel();
            timer = null;
            nPlayingIndex = -1;
        }

        mProgressDialog.setMessage("서버와 통신중입니다. 잠시만 기다려주세요.");
        mProgressDialog.show();

        try {
            String url = CommonUtils.strDefaultUrl + "PanAppUploadChat.jsp";
            RequestBody requestBody = null;

            File sourceFile = null;

            JSONObject sendObject = new JSONObject();

            if(nEpisodeID > -1)
                sendObject.put("EPISODE_ID", nEpisodeID);

            if(nAddIndex > -1) {                                // 사이에 끼워넣기
                sendObject.put("CHAT_INTERCEPT", true);
            } else {
                sendObject.put("CHAT_INTERCEPT", false);
            }

            if(bEdit)                                           // 수정
                sendObject.put("CHAT_MODIFY", true);
            else
                sendObject.put("CHAT_MODIFY", false);

            JSONArray chatArray = new JSONArray();
            JSONObject chatObject = new JSONObject();

            int nType = chatVO.getType();
            chatObject.put("EPISODE_ID", nEpisodeID);
            chatObject.put("CHAT_TYPE", nType);

            if(chatVO.getCharacterVO() != null)
                chatObject.put("CHARACTER_ID", chatVO.getCharacterVO().getnCharacterID());

            chatObject.put("CHAT_ORDER", chatVO.getnOrder());

            if(nType == 1 || nType == 2 || nType == 7) {
                chatObject.put("CHAT_CONTENTS", chatVO.getContents());
            } else if(nType == 3 || nType == 4 || nType == 5 || nType == 8 || nType == 11) {           // 파일일 경우 파일 명만 보내야함
                String strPath = chatVO.getStrContentsFile();

                if(strPath == null || strPath.length() == 0) {
                    if(chatVO.getContentsUri() != null)
                        chatObject.put("CHAT_CONTENTS", chatVO.getContentsUri().toString());
                } else {
                    String filename = strPath.substring(strPath.lastIndexOf("/")+1);
                    chatObject.put("CHAT_CONTENTS", filename);
                }
            } else if(nType == 6) {
                chatObject.put("CHAT_CONTENTS", chatVO.getStrContentsFile());
            }

            chatArray.put(chatObject);

            sendObject.put("CHAT_ARRAY", chatArray);
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM).addFormDataPart("JSON_BODY", sendObject.toString());

            if(nType == 3 || nType == 4 || nType == 5 || nType == 8 || nType == 11) {
                String strPath = chatVO.getStrContentsFile();
                if(strPath != null && strPath.length() > 0) {
                    sourceFile = new File(strPath);
                    String filename = strPath.substring(strPath.lastIndexOf("/")+1);
                    builder.addFormDataPart(filename, filename, RequestBody.create(MultipartBody.FORM, sourceFile));
                }
            }

            requestBody = builder.build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

//            if(bSubmit) {
//                mProgressDialog.dismiss();
//                Toast.makeText(LiteratureWriteActivity.this, "작품 등록을 실패하였습니다.", Toast.LENGTH_LONG).show();
//                return;
//            }

            new OkHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            e.printStackTrace();
                            mProgressDialog.dismiss();
                            Toast.makeText(LiteratureWriteActivity.this, "등록을 실패하였습니다.", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if(response.code() != 200) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProgressDialog.dismiss();
                                CommonUtils.makeText(LiteratureWriteActivity.this, "서버와의 연결이 원활하지 않습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_LONG).show();
                            }
                        });
                        return;
                    }

                    final String strResult = response.body().string();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog.dismiss();
//                            nEditIndex = -1;

                            try {
                                JSONObject resultObject = new JSONObject(strResult);

                                if(resultObject.getString("RESULT").equals("SUCCESS")) {
                                    if(resultObject.has("CHAT_ID")) {
                                        int nChatID = resultObject.getInt("CHAT_ID");
                                        chatVO.setnChatID(nChatID);
                                    }

//                                    if(!bEdit) {
//                                        if(nAddIndex > -1)
//                                            chattingList.add(nAddIndex+1, chatVO);
//                                        else
//                                            chattingList.add(chatVO);
//                                    }

                                    inputTextView.setText("");

                                    if(chatVO.getType() == ChatVO.TYPE_DISTRACTOR) {                // 분기 설정 했음. 페이지 이동
                                        Intent intent = new Intent(LiteratureWriteActivity.this, InteractionWriteActivity.class);
                                        InteractionWriteActivity.workVO = workVO;
                                        intent.putExtra("TITLE", strTitle);
                                        intent.putExtra("EPISODE_ID", nEpisodeID);
                                        intent.putExtra("EPISODE_INDEX", nInteractionIndex);
                                        intent.putExtra("SUBMIT", bSubmit);
                                        intent.putExtra("EXCEL_UPLOADED", isExcelUploaded);
                                        startActivity(intent);
                                        finish();
                                        return;
                                    }

                                    getEpisodeChatData();
                                } else {
                                    Toast.makeText(LiteratureWriteActivity.this, "작품 등록을 실패하였습니다.", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    // response.body().string()
                }
            });
        } catch (Exception e) {
            e.printStackTrace();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressDialog.dismiss();
                    Toast.makeText(LiteratureWriteActivity.this, "작품 생성을 실패하였습니다.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void requestDeleteCharacter(final int nIndex) {                                 // 등장인물 삭제
        mProgressDialog.setMessage("서버와 통신중입니다. 잠시만 기다려주세요.");
        mProgressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                CharacterVO vo = characterList.get(nIndex);
                JSONObject resultObject = HttpClient.requestDeleteCharacter(new OkHttpClient(), vo.getnCharacterID());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mProgressDialog.dismiss();

                            if(resultObject != null && resultObject.getString("RESULT").equals("SUCCESS")) {
                                characterList.remove(nIndex);
                                resetCharacterLayout();
                                Toast.makeText(LiteratureWriteActivity.this, "삭제되었습니다.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LiteratureWriteActivity.this, "등장인물 삭제를 실패하였습니다.", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    private void requestDeleteAllMessage() {                                                // 모든 대화 삭제
        mProgressDialog.setMessage("서버와 통신중입니다. 잠시만 기다려주세요.");
        mProgressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject resultObject = HttpClient.requestDeleteAllMessage(new OkHttpClient(), nEpisodeID);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mProgressDialog.dismiss();

                            if(resultObject != null && resultObject.getString("RESULT").equals("SUCCESS")) {
                                bgView.setBackgroundResource(0);
                                bgView.setImageBitmap(null);
                                bgView.setBackgroundColor(getResources().getColor(R.color.colorDefaultBG));

                                chattingList.clear();
                                aa.notifyDataSetChanged();

                                Toast.makeText(LiteratureWriteActivity.this, "삭제되었습니다.", Toast.LENGTH_LONG).show();

                                getCharacterData();
                            } else {
                                Toast.makeText(LiteratureWriteActivity.this, "삭제에 실패하였습니다.", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    private void requestDeleteMessage(final int nIndex) {                               // 대화 한개 삭제
        mProgressDialog.setMessage("서버와 통신중입니다. 잠시만 기다려주세요.");
        mProgressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                if(nIndex >= chattingList.size()) {
                    return;
                }

                nDeleteIndex = nIndex;
                ChatVO vo = chattingList.get(nIndex);
                JSONObject resultObject = HttpClient.requestDeleteMessage(new OkHttpClient(), nEpisodeID, vo.getnChatID(), vo.getnOrder());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mProgressDialog.dismiss();

                            if(vo.getType() == ChatVO.TYPE_CHANGE_BG || vo.getType() == ChatVO.TYPE_CHANGE_BG_COLOR) {
                                bgView.setBackgroundResource(0);
                                bgView.setImageBitmap(null);
                                bgView.setBackgroundColor(getResources().getColor(R.color.colorDefaultBG));
                            }

                            if(resultObject != null && resultObject.getString("RESULT").equals("SUCCESS")) {
                                chattingList.remove(nIndex);
                                getEpisodeChatData();
                                Toast.makeText(LiteratureWriteActivity.this, "삭제되었습니다.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LiteratureWriteActivity.this, "작품 등록을 실패하였습니다.", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    private void requestEpisodePost() {                                                                   // 심사 요청(제출)
        Intent intent = new Intent(LiteratureWriteActivity.this, WorkPostPopup.class);
        startActivityForResult(intent, 9000);
    }

    private void sendEpisodePost() {
        mProgressDialog.setMessage("작품을 게시 중입니다.");
        mProgressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject resultObject = HttpClient.requestEpisodePost(new OkHttpClient(), nEpisodeID);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mProgressDialog.dismiss();

                            if(resultObject == null) {
                                Toast.makeText(LiteratureWriteActivity.this, "서버와의 통신에 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_LONG).show();
                                return;
                            }

                            if(resultObject.getString("RESULT").equals("SUCCESS")) {
                                Toast.makeText(LiteratureWriteActivity.this, "게시되었습니다.", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Toast.makeText(LiteratureWriteActivity.this, "게시에 실패하였습니다.", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    private void requestEpisodeSubmit() {                                                                   // 심사 요청(제출)
        AlertDialog.Builder builder = new AlertDialog.Builder(LiteratureWriteActivity.this);
        builder.setTitle("회차 제출");
        builder.setMessage("회차를 제출하면 승인 대기 상태가 됩니다. 관리자가 회차를 승인한 이후부터 회차가 독자들에게 게시됩니다.\n회차를 제출하시겠습니까?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id) {
                mProgressDialog.setMessage("작품을 제출 중입니다.");
                mProgressDialog.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject resultObject = HttpClient.requestEpisodeSubmit(new OkHttpClient(), nEpisodeID);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    mProgressDialog.dismiss();

                                    if(resultObject == null) {
                                        Toast.makeText(LiteratureWriteActivity.this, "서버와의 통신에 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                    if(resultObject.getString("RESULT").equals("SUCCESS")) {
                                        Toast.makeText(LiteratureWriteActivity.this, "제출 되었습니다.", Toast.LENGTH_LONG).show();
                                        finish();
                                    } else {
                                        Toast.makeText(LiteratureWriteActivity.this, "제출에 실패하였습니다.", Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }).start();
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void getCharacterData() {                                           // 등장인물 데이터 가져오기
        mProgressDialog.setMessage("서버에서 작품 데이터를 가져오고 있습니다...");
        mProgressDialog.show();
        characterList.clear();
        characterList.add(null);
        nameList.add("지문");

        new Thread(new Runnable() {
            @Override
            public void run() {
                characterList.addAll(HttpClient.getCharacterDataWithEpisodeID(new OkHttpClient(), "" + workVO.getEpisodeList().get(nInteractionIndex).getnEpisodeID()));

                if(characterList == null) {
                    Toast.makeText(LiteratureWriteActivity.this, "서버와의 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                for(int i = 1 ; i < characterList.size() ; i++) {
                    nameList.add(characterList.get(i).getName());
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resetCharacterLayout();
                        getEpisodeChatData();
                    }
                });
            }
        }).start();
    }

    private void getEpisodeChatData() {                                         // 대화 데이터 가져오기
        new Thread(new Runnable() {
            @Override
            public void run() {
                chattingList.clear();
                chattingList.addAll(HttpClient.getChatDataWithEpisodeID(new OkHttpClient(), "" + workVO.getEpisodeList().get(nInteractionIndex).getnEpisodeID()));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();

                        if(chattingList == null) {
                            Toast.makeText(LiteratureWriteActivity.this, "서버와의 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for(int i = 0 ; i < chattingList.size() ; i++) {
                            ChatVO vo = chattingList.get(i);

                            if(vo.getType() == ChatVO.TYPE_DISTRACTOR) {
                                Intent intent = new Intent(LiteratureWriteActivity.this, InteractionWriteActivity.class);
                                InteractionWriteActivity.workVO = workVO;
                                intent.putExtra("TITLE", strTitle);
                                intent.putExtra("EPISODE_ID", nEpisodeID);
                                intent.putExtra("EPISODE_INDEX", nInteractionIndex);
                                startActivity(intent);
                                finish();
                                return;
                            }
                        }

                        if(chattingList.size() > 0) {
                            ChatVO vo = new ChatVO();
                            vo.setType(ChatVO.TYPE_EMPTY);
                            chattingList.add(0, vo);
                        }

                        aa.notifyDataSetChanged();

                        if(nAddIndex == -1 && nEditIndex == -1 && nDeleteIndex == -1)
                            chattingListView.setSelection(aa.getCount() - 1);
                        else if(nAddIndex != -1) {
                            chattingListView.setSelection(nAddIndex);
                        } else if(nEditIndex != -1) {
                            chattingListView.setSelection(nEditIndex);
                        } else if(nDeleteIndex != -1) {
                            if(nDeleteIndex > 0)
                                nDeleteIndex -= 1;

                            chattingListView.setSelection(nDeleteIndex);
                        }

                        nAddIndex = -1;
                        nEditIndex = -1;
                        nDeleteIndex = -1;
                    }
                });
            }
        }).start();
    }

    public void onClickContentsAddBtn(View view) {
        imm.hideSoftInputFromWindow(inputTextView.getWindowToken(), 0);
        bShowMenu = !bShowMenu;

        if(bShowMenu) {
            bottomSettingLayout.setVisibility(View.VISIBLE);
            contentsAddBtn.setImageResource(R.drawable.pop_close);
        } else {
            bottomSettingLayout.setVisibility(View.GONE);
            contentsAddBtn.setImageResource(R.drawable.selectionplus);
        }
    }

    private void hideBottomView() {
        bShowMenu = false;
        bottomSettingLayout.setVisibility(View.GONE);
        contentsAddBtn.setImageResource(R.drawable.selectionplus);
    }

    private void resetCharacterLayout() {
        speakerAddLayout.removeAllViews();
        characterViewList.clear();

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for(int i = 0 ; i < characterList.size() ; i++) {
            final int nIndex = i;
            View view = inflater.inflate(R.layout.speaker_view, null);
            CharacterVO vo = characterList.get(i);

            ImageView faceView = view.findViewById(R.id.faceView);
            TextView nameView = view.findViewById(R.id.nameView);
            ImageView selectedView = view.findViewById(R.id.selectedView);

            int nImg = 0;

            int newi = i % 10;
            switch(newi) {
                case 1:
                    nImg = R.drawable.user_icon_01;
                    break;
                case 2:
                    nImg = R.drawable.user_icon_02;
                    break;
                case 3:
                    nImg = R.drawable.user_icon_03;
                    break;
                case 4:
                    nImg = R.drawable.user_icon_04;
                    break;
                case 5:
                    nImg = R.drawable.user_icon_05;
                    break;
                case 6:
                    nImg = R.drawable.user_icon_06;
                    break;
                case 7:
                    nImg = R.drawable.user_icon_07;
                    break;
                case 8:
                    nImg = R.drawable.user_icon_08;
                    break;
                case 9:
                    nImg = R.drawable.user_icon_09;
                    break;
                case 0:
                    nImg = R.drawable.user_icon_10;
                    break;
            }

            if(i == 0) {
                faceView.setImageResource(R.drawable.narration_button);
                nameView.setText("지문");
                nameView.setTextColor(ContextCompat.getColor(LiteratureWriteActivity.this, R.color.colorBlack));
                selectedView.setVisibility(View.VISIBLE);
                nSelectedCharacterIndex = 0;
            } else {
                if(vo.getImage() != null && !vo.getImage().equals("null")) {
                    Glide.with(this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(vo.getImage())
                            .placeholder(nImg)
                            .apply(new RequestOptions().circleCrop())
                            .into(faceView);
                } else if(vo.getStrImgFile() != null && !vo.getStrImgFile().equals("null")) {
                    String strUrl = vo.getStrImgFile();
//                    strUrl = strUrl.replaceAll(" ", "");

                    if(!strUrl.startsWith("http"))
                        strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;

                    Glide.with(LiteratureWriteActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .placeholder(nImg)
                            .load(strUrl)
                            .apply(new RequestOptions().circleCrop())
                            .into(faceView);
                } else {
                    faceView.setImageResource(nImg);
                }

                nameView.setText(vo.getName());
                nameView.setTextColor(Color.parseColor("#e9e9e9"));
                selectedView.setVisibility(View.INVISIBLE);
            }

            LinearLayout faceLayout = view.findViewById(R.id.faceLayout);
            faceLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(int i = 0 ; i < characterViewList.size() ; i++) {
                        View view = characterViewList.get(i);

                        TextView nameView = view.findViewById(R.id.nameView);
                        ImageView selectedView = view.findViewById(R.id.selectedView);

                        if(i == nIndex) {
                            nameView.setTextColor(ContextCompat.getColor(LiteratureWriteActivity.this, R.color.colorBlack));
                            selectedView.setVisibility(View.VISIBLE);
                        } else {
                            nameView.setTextColor(Color.parseColor("#e9e9e9"));
                            selectedView.setVisibility(View.INVISIBLE);
                        }
                    }

                    nSelectedCharacterIndex = nIndex;
                }
            });

            if(i > 0) {
                faceLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        CreateCharacterActivity.nameList = new ArrayList<String>(nameList);
                        CreateCharacterActivity.characterVO = characterList.get(nIndex);

                        Intent intent = new Intent(LiteratureWriteActivity.this, CreateCharacterActivity.class);
                        intent.putExtra("INDEX", nIndex);
                        startActivityForResult(intent, 1011);

                        return false;
                    }
                });
            }

            speakerAddLayout.addView(view);
            characterViewList.add(view);
        }
    }

    private void requestUpdateCharacter(final CharacterVO characterVO, final int nIndex) {                                  // 등장인물 수정
        mProgressDialog.setMessage("캐릭터를 생성중입니다");
        mProgressDialog.show();

        try {
            String url = CommonUtils.strDefaultUrl + "PanAppCreateCharacter.jsp";
            RequestBody requestBody = null;

            File sourceFile = null;
            String strFilePath = null;

            MultipartBody.Builder builder = new MultipartBody.Builder();

            String strBalloon = characterVO.getStrBalloonColor();
            if(strBalloon == null)
                strBalloon = "null";

            builder.setType(MultipartBody.FORM)
                    .addFormDataPart("EPISODE_ID", "" + nEpisodeID)
                    .addFormDataPart("CHARACTER_ID", "" + characterVO.getnCharacterID())
                    .addFormDataPart("CHARACTER_NAME", characterVO.getName())
                    .addFormDataPart("BALLOON_COLOR", strBalloon)
                    .addFormDataPart("CHARACTER_DIRECTION", "" + characterVO.getDirection())
                    .addFormDataPart("BLACK_TEXT", characterVO.isbBlackText() == true ? "Y" : "N")
                    .addFormDataPart("BLACK_NAME", characterVO.isbBlackName() == true ? "Y" : "N");

            if(characterVO.getImage() != null) {
                strFilePath = CommonUtils.getRealPathFromURI(LiteratureWriteActivity.this, characterVO.getImage());
                sourceFile = new File(strFilePath);

                if(!sourceFile.exists()) {
                    mProgressDialog.dismiss();
                    Toast.makeText(LiteratureWriteActivity.this, "이미지가 잘못되었습니다.", Toast.LENGTH_LONG).show();

                    return;
                }

                String filename = strFilePath.substring(strFilePath.lastIndexOf("/")+1);
                builder.addFormDataPart(filename, filename, RequestBody.create(MultipartBody.FORM, sourceFile));
            }

            requestBody = builder.build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            new OkHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    mProgressDialog.dismiss();
                    Toast.makeText(LiteratureWriteActivity.this, "캐릭터 생성을 실패하였습니다.", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String strResult = response.body().string();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog.dismiss();

                            try {
                                JSONObject resultObject = new JSONObject(strResult);

                                if(resultObject.getString("RESULT").equals("SUCCESS")) {
                                    nameList.set(nIndex, characterVO.getName());
                                    characterList.set(nIndex, characterVO);
//                                    resetCharacterLayout();
                                    getCharacterData();
                                } else {
                                    Toast.makeText(LiteratureWriteActivity.this, "캐릭터 생성을 실패하였습니다.", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    // response.body().string()
                }
            });
        } catch (Exception e) {
            e.printStackTrace();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressDialog.dismiss();
                    Toast.makeText(LiteratureWriteActivity.this, "작품 생성을 실패하였습니다.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void requestCreateCharacter(final CharacterVO characterVO) {                                        // 등장인물 추가
        mProgressDialog.setMessage("캐릭터를 생성중입니다");
        mProgressDialog.show();

        try {
            String url = CommonUtils.strDefaultUrl + "PanAppCreateCharacter.jsp";
            RequestBody requestBody = null;

            File sourceFile = null;
            String strFilePath = null;

            MultipartBody.Builder builder = new MultipartBody.Builder();

            String strBalloon = characterVO.getStrBalloonColor();
            if(strBalloon == null)
                strBalloon = "null";

            builder.setType(MultipartBody.FORM)
                    .addFormDataPart("EPISODE_ID", "" + nEpisodeID)
                    .addFormDataPart("CHARACTER_NAME", characterVO.getName())
                    .addFormDataPart("BALLOON_COLOR", strBalloon)
                    .addFormDataPart("CHARACTER_DIRECTION", "" + characterVO.getDirection())
                    .addFormDataPart("BLACK_TEXT", characterVO.isbBlackText() == true ? "Y" : "N")
                    .addFormDataPart("BLACK_NAME", characterVO.isbBlackName() == true ? "Y" : "N");

            if(characterVO.getImage() != null) {
                strFilePath = CommonUtils.getRealPathFromURI(LiteratureWriteActivity.this, characterVO.getImage());
                sourceFile = new File(strFilePath);

                if(!sourceFile.exists()) {
                    mProgressDialog.dismiss();
                    Toast.makeText(LiteratureWriteActivity.this, "이미지가 잘못되었습니다.", Toast.LENGTH_LONG).show();

                    return;
                }

                String filename = strFilePath.substring(strFilePath.lastIndexOf("/")+1);
                builder.addFormDataPart(filename, filename, RequestBody.create(MultipartBody.FORM, sourceFile));
            }

            requestBody = builder.build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            new OkHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    mProgressDialog.dismiss();
                    Toast.makeText(LiteratureWriteActivity.this, "캐릭터 생성을 실패하였습니다.", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String strResult = response.body().string();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog.dismiss();

                            try {
                                JSONObject resultObject = new JSONObject(strResult);

                                if(resultObject.getString("RESULT").equals("SUCCESS")) {
                                    String strCharacterID = resultObject.getString("CHARACTER_ID");
                                    characterVO.setnCharacterID(Integer.valueOf(strCharacterID));

                                    nameList.add(characterVO.getName());
                                    characterList.add(characterVO);
                                    resetCharacterLayout();
                                } else {
                                    Toast.makeText(LiteratureWriteActivity.this, "캐릭터 생성을 실패하였습니다.", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    // response.body().string()
                }
            });
        } catch (Exception e) {
            e.printStackTrace();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressDialog.dismiss();
                    Toast.makeText(LiteratureWriteActivity.this, "작품 생성을 실패하였습니다.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {                                 // 이미지 설정 등의 데이터가 거쳐감
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if(requestCode == 9000) {                                           // 게시 처리 팝업
                sendEpisodePost();
            } else if (requestCode == 2000) {                                   // 수정된 이미지 갤러리
                Intent intent = new Intent(LiteratureWriteActivity.this, AlbumSelectActivity.class);
                intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 1);
                startActivityForResult(intent, 2100);
            } else if (requestCode == 2100) {
                ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
                if (images.size() > 0) {
                    Image image = images.get(0);
                    String strImgPath = image.path;
                }
            } else if (requestCode == 1010) {                                  //   캐릭터 추가
                CharacterVO characterVO = new CharacterVO();
                characterVO.setName(data.getStringExtra("NAME"));
                characterVO.setDirection(data.getIntExtra("LEFTRIGHT", 0));
                characterVO.setbBlackText(data.getBooleanExtra("BLACK_TEXT", true));
                characterVO.setbBlackName(data.getBooleanExtra("BLACK_NAME", true));

                String strBalloonColor = data.getStringExtra("BALLOON_COLOR");
                if (strBalloonColor != null)
                    characterVO.setStrBalloonColor(data.getStringExtra("BALLOON_COLOR"));
                String strUri = data.getStringExtra("URI");

                if (strUri != null)
                    characterVO.setImage(Uri.parse(strUri));

                requestCreateCharacter(characterVO);
            } else if (requestCode == 1011) {                            // 캐릭터 변경/삭제
                int nIndex = data.getIntExtra("INDEX", -1);
                boolean bDelete = data.getBooleanExtra("DELETE", false);

                if (nIndex == -1)
                    return;

                if (bDelete) {
                    requestDeleteCharacter(nIndex);
                    return;
                }

                CharacterVO characterVO = characterList.get(nIndex);
                characterVO.setName(data.getStringExtra("NAME"));
                characterVO.setDirection(data.getIntExtra("LEFTRIGHT", 0));
                characterVO.setbBlackText(data.getBooleanExtra("BLACK_TEXT", true));
                characterVO.setbBlackName(data.getBooleanExtra("BLACK_NAME", true));

                String strBalloonColor = data.getStringExtra("BALLOON_COLOR");
                if (strBalloonColor != null)
                    characterVO.setStrBalloonColor(data.getStringExtra("BALLOON_COLOR"));

                String strUri = data.getStringExtra("URI");

                if (strUri != null)
                    characterVO.setImage(Uri.parse(strUri));

                requestUpdateCharacter(characterVO, nIndex);
            } else if (requestCode == 1000) {                            // 베경 색 설정
                isEdit = data.getBooleanExtra("EDIT", false);

                if (isEdit)
                    nEditIndex = data.getIntExtra("ORDER", -1);

                bColorPicker = true;
            } else if (requestCode == 1020 || requestCode == 1025) {                            // 선택지 추가
                ChatVO chatVO = null;

                boolean bEdit = false;
                if (requestCode == 1020) {
                    chatVO = new ChatVO();
                    chatVO.setType(ChatVO.TYPE_DISTRACTOR);
                    chatVO.setType(ChatVO.TYPE_DISTRACTOR);

                    if (nAddIndex > -1) {
                        ChatVO currentVO = chattingList.get(nAddIndex);
                        int nIndex = 0;

                        if (currentVO.getType() != ChatVO.TYPE_EMPTY) {
                            nIndex = currentVO.getnOrder();
                            chatVO.setnOrder(nIndex + 1);
                        }
                    } else {
                        if (chattingList.size() == 0)
                            chatVO.setnOrder(0);
                        else {
                            ChatVO currentVO = chattingList.get(chattingList.size() - 1);
                            int nIndex = currentVO.getnOrder();
                            chatVO.setnOrder(nIndex + 1);
                        }
                    }

                    if (nSelectedCharacterIndex > 0) {                   // 나레이션
                        CharacterVO characterVO = characterList.get(nSelectedCharacterIndex);
                        chatVO.setCharacter(characterVO);
                    }
                } else {
                    int nOrder = data.getIntExtra("ORDER", -1);
                    chatVO = chattingList.get(nOrder);
                    bEdit = true;
                }

                String strInteraction1 = data.getStringExtra("DISTRACTOR_1");
                String strInteraction2 = data.getStringExtra("DISTRACTOR_2");
                chatVO.setContents(strInteraction1 + "╋" + strInteraction2);

                requestUploadMessage(chatVO, bEdit);
            } else if (requestCode == 1030 || requestCode == 1035) {                           // 음원 가져오기
                Uri fileUri = data.getData();

                String strFilePath = CommonUtils.getRealPathFromURI(LiteratureWriteActivity.this, fileUri);
                File sourceFile = new File(strFilePath);

                if (!sourceFile.exists()) {
                    mProgressDialog.dismiss();
                    Toast.makeText(LiteratureWriteActivity.this, "음원 파일이 잘못되었습니다.", Toast.LENGTH_LONG).show();

                    return;
                }

                String filename = strFilePath.substring(strFilePath.lastIndexOf("/") + 1);

                ChatVO chatVO = null;
                boolean bEdit = false;

                if (requestCode == 1030) {
                    chatVO = new ChatVO();
                    CharacterVO characterVO = characterList.get(nSelectedCharacterIndex);
                    chatVO.setCharacter(characterVO);

                    if (nAddIndex > -1) {
                        ChatVO currentVO = chattingList.get(nAddIndex);
                        int nIndex = 0;

                        if (currentVO.getType() != ChatVO.TYPE_EMPTY) {
                            nIndex = currentVO.getnOrder();
                            chatVO.setnOrder(nIndex + 1);
                        }
                    } else {
                        if (chattingList.size() == 0) {
                            chatVO.setnOrder(0);
                        } else {
                            ChatVO currentVO = chattingList.get(chattingList.size() - 1);
                            int nIndex = currentVO.getnOrder();
                            chatVO.setnOrder(nIndex + 1);
                        }
                    }
                } else if (requestCode == 1035) {
                    chatVO = chattingList.get(nEditIndex);
                    bEdit = true;
                }

                chatVO.setType(ChatVO.TYPE_SOUND);
                chatVO.setContentsUri(fileUri);
                chatVO.setStrContentsFile(CommonUtils.getRealPathFromURI(LiteratureWriteActivity.this, fileUri));

                requestUploadMessage(chatVO, bEdit);
            } else if (requestCode == 1040) {                    // 엑셀파일 가져오
                mProgressDialog.setMessage("엑셀 파일을 읽고 있습니다. 잠시만 기다려 주세요.");
                mProgressDialog.show();

                Uri fileUri = data.getData();
                File file = null;

                try {
                    file = from(LiteratureWriteActivity.this, fileUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (file != null && file.exists()) {
                    Log.d("asdf", "asdf");
                }

                final String filePath = file.getAbsolutePath();

                new Thread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        try {
                            ExcelReader excelReader = new ExcelReader(LiteratureWriteActivity.this, nEpisodeID);
                            excelReader.readExcel(filePath, false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InvalidFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } else if (requestCode == 1050) {                                       // 일단 대화/나레이션 문구 수정
                String strContents = data.getStringExtra("EDITED_TEXT");
                int nOrder = data.getIntExtra("ORDER", -1);
                nEditIndex = nOrder;
                ChatVO chatVO = chattingList.get(nOrder);
                chatVO.setContents(strContents);

                requestUploadMessage(chatVO, true);
            } else if (requestCode == 1100) {                    // 제목 변경
                strTitle = data.getStringExtra("TITLE");
                titleView.setText(strTitle);
            } else if (requestCode == InteractionWriteActivity.EXCEL) {              // 엑셀파일 로딩
                CommonUtils.showProgressDialog(LiteratureWriteActivity.this, "엑셀파일을 불러오고 있습니다. 잠시만 기다려주세요.");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean bTemp = false;

                        if (characterList.size() <= 1)
                            bTemp = true;
                        else
                            bTemp = HttpClient.requestDeleteAllCharacter(new OkHttpClient(), nEpisodeID);

                        final boolean bResult = bTemp;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!bResult) {
                                    CommonUtils.hideProgressDialog();
                                    Toast.makeText(LiteratureWriteActivity.this, "엑셀파일 로딩에 실패했습니다.", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                Uri fileUri = data.getData();
                                readExcel(fileUri);
                            }
                        });
                    }
                }).start();
            }
        }

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

    private void readExcel(final Uri fileUri) {
        final String filePath = CommonUtils.getRealPathFromURI(LiteratureWriteActivity.this, fileUri);

        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                try {
                    ExcelReader excelReader = new ExcelReader(LiteratureWriteActivity.this, nEpisodeID);
                    excelReader.readExcel(filePath, false);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InvalidFormatException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();
                        getCharacterData();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;

        switch(v.getId()) {
            case R.id.bgSettingView:
                TedPermission.with(LiteratureWriteActivity.this)
                        .setPermissionListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                startActivityForResult(new Intent(LiteratureWriteActivity.this, BGImageSelectPopup.class), 1000);
                            }

                            @Override
                            public void onPermissionDenied(List<String> deniedPermissions) {
                                Toast.makeText(LiteratureWriteActivity.this, "권한을 허용해주셔야 사진 설정이 가능합니다.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .check();
                break;
            case R.id.imgSettingView:
//                if(nSelectedCharacterIndex == 0) {
//                    Toast.makeText(LiteratureWriteActivity.this, "지문은 이미지를 추가할 수 없습니다.", Toast.LENGTH_LONG).show();
//                    return;
//                }

                TedPermission.with(LiteratureWriteActivity.this)
                        .setPermissionListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                Intent intent = new Intent(LiteratureWriteActivity.this, MediaSelectPopup.class);
                                if(nSelectedCharacterIndex == 0)
                                    intent.putExtra("TYPE", PhotoPickerActivity.TYPE_CONTENTS_IMG_NAR);
                                else
                                    intent.putExtra("TYPE", PhotoPickerActivity.TYPE_CONTENTS_IMG);
                                intent.putExtra("ORDER", nAddIndex);
                                if(nEditIndex > -1)
                                    intent.putExtra("EDIT", true);
                                startActivity(intent);
                            }

                            @Override
                            public void onPermissionDenied(List<String> deniedPermissions) {
                                Toast.makeText(LiteratureWriteActivity.this, "권한을 허용해주셔야 사진 설정이 가능합니다.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .check();
                break;

            case R.id.videoSettingView:
                if(nSelectedCharacterIndex == 0) {
                    Toast.makeText(LiteratureWriteActivity.this, "지문은 동영상을 추가할 수 없습니다.", Toast.LENGTH_LONG).show();
                    return;
                }

                TedPermission.with(LiteratureWriteActivity.this)
                        .setPermissionListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                Intent intent = new Intent(LiteratureWriteActivity.this, PhotoPickerActivity.class);
                                intent.putExtra("TYPE", PhotoPickerActivity.TYPE_VIDEO);
                                if(nEditIndex > -1)
                                    intent.putExtra("EDIT", true);
                                intent.putExtra("ORDER", nAddIndex);
                                startActivity(intent);
                            }

                            @Override
                            public void onPermissionDenied(List<String> deniedPermissions) {
                                Toast.makeText(LiteratureWriteActivity.this, "권한을 허용해주셔야 사진 설정이 가능합니다.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .check();

                break;

            case R.id.distractorView:
                if(workVO.getEpisodeList().size() > nInteractionIndex+1) {
                    Toast.makeText(this, "작품의 현재 마지막 화에서만 분기 생성이 가능합니다.", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
                intent = new Intent(LiteratureWriteActivity.this, DistractorPopup.class);
                startActivityForResult(intent, 1020);
                break;

            case R.id.soundSettingView:
                if(nSelectedCharacterIndex == 0) {
                    Toast.makeText(LiteratureWriteActivity.this, "지문은 음원을 추가할 수 없습니다.", Toast.LENGTH_LONG).show();
                    return;
                }

                TedPermission.with(LiteratureWriteActivity.this)
                        .setPermissionListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("audio/*");
                                startActivityForResult(Intent.createChooser(intent, "Music File"), 1030);
                            }

                            @Override
                            public void onPermissionDenied(List<String> deniedPermissions) {
                                Toast.makeText(LiteratureWriteActivity.this, "권한을 허용해주셔야 사진 설정이 가능합니다.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .check();

                break;
        }
    }

    @Override
    public void onColorSelected(int dialogId, int color) {                                      // 컬러 선택 콜백 - 배경 색 혹은 말풍선 색, 글씨 색등을 변경하는데 사용됨
        nBgColor = color;
        bgColor = String.format("#%06X", (0xFFFFFF & nBgColor));
        bgView.setBackgroundColor(nBgColor);

        ChatVO chatVO = new ChatVO();

        chatVO.setType(ChatVO.TYPE_CHANGE_BG_COLOR);

        if(nEditIndex > -1) {
            chatVO = chattingList.get(nEditIndex);
        } else {
            if(nAddIndex > -1) {
                ChatVO currentVO = chattingList.get(nAddIndex);
                int nIndex = 0;

                if(currentVO.getType() != ChatVO.TYPE_EMPTY) {
                    nIndex = currentVO.getnOrder();
                    chatVO.setnOrder(nIndex+1);
                }
            } else {
                if(chattingList.size() == 0)
                    chatVO.setnOrder(0);
                else {
                    ChatVO currentVO = chattingList.get(chattingList.size()-1);
                    int nIndex = currentVO.getnOrder();
                    chatVO.setnOrder(nIndex+1);
                }
            }
        }

        chatVO.setStrContentsFile(bgColor);

        if(nEditIndex > -1)
            requestUploadMessage(chatVO, true);
        else
            requestUploadMessage(chatVO, false);
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }

    public class CChattingArrayAdapter extends ArrayAdapter<Object>
    {
        private int mCellLayout;
        private LayoutInflater mLiInflater;

        CChattingArrayAdapter(Context context, int layout, List titles)
        {
            super(context, layout, titles);
            mCellLayout = layout;
            mLiInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            if(position >= chattingList.size()) {
                convertView = mLiInflater.inflate(R.layout.empty_chatting_row, parent, false);
                return convertView;
            }

            ChatVO chatVO = chattingList.get(position);
            CharacterVO characterVO = chatVO.getCharacterVO();

            int nType = chatVO.getType();
            int nWidth = (int)((float)chattingListView.getWidth() * (float)(1.7f/3.0f));
            int nDirection = 0;

            if(nType == ChatVO.TYPE_TEXT) {                                                                     // type 별로 각 리스트의 row 세팅 - 리펙토링 가장 필요한 부분. 내가할거임.   타입별로 거의 비슷함. 일반 대화만 주석 작성함.
                nDirection = characterVO.getDirection();

                if(nDirection == 0)             // left                                                                     // 좌우 구분하여 layout 할당
                    convertView = mLiInflater.inflate(R.layout.left_chatting_row, parent, false);
                else
                    convertView = mLiInflater.inflate(R.layout.right_chatting_row, parent, false);

                TextView nameView = convertView.findViewById(R.id.nameView);                                                // 이름 설정
                nameView.setText(characterVO.getName());

                if(characterVO.isbBlackName())                                                                              // 캐릭터 이름 검은색인지 흰색인지 구분
                    nameView.setTextColor(Color.parseColor("#000000"));
                else
                    nameView.setTextColor(Color.parseColor("#ffffff"));

                TextView contentsTextView = convertView.findViewById(R.id.contentsTextView);                                // 대화 내용 쓰기
                contentsTextView.setText(chatVO.getContents());

                if(characterVO.isbBlackText()) {                                                                            // 글씨 색 검은색/흰색 설정
                    contentsTextView.setTextColor(getResources().getColor(R.color.colorBlack));
                } else {
                    contentsTextView.setTextColor(getResources().getColor(R.color.colorWhite));
                }

                ImageView imageContentsView = convertView.findViewById(R.id.imageContentsView);                             // 이미지 있는경우 설정

                if(chatVO.getContentsUri() != null) {
                    Glide.with(LiteratureWriteActivity.this)
                            .asBitmap()
                            .load(chatVO.getContentsUri())
                            .apply(new RequestOptions().override(800, 800))
                            .into(imageContentsView);
                } else if(chatVO.getStrContentsFile() != null) {
                    String strUrl = characterVO.getStrImgFile();
//                    strUrl = strUrl.replaceAll(" ", "");

                    if(!strUrl.startsWith("http"))
                        strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;

                    Glide.with(LiteratureWriteActivity.this)
                            .asBitmap()
                            .load(strUrl)
                            .apply(new RequestOptions().override(800, 800))
                            .into(imageContentsView);
                }
            } else if(nType == ChatVO.TYPE_SOUND) {
                nDirection = characterVO.getDirection();

                if(nDirection == 0)             // left
                    convertView = mLiInflater.inflate(R.layout.left_audio_row, parent, false);
                else
                    convertView = mLiInflater.inflate(R.layout.right_audio_row, parent, false);

                TextView nameView = convertView.findViewById(R.id.nameView);
                nameView.setText(characterVO.getName());
                if(characterVO.isbBlackName())
                    nameView.setTextColor(Color.parseColor("#000000"));
                else
                    nameView.setTextColor(Color.parseColor("#ffffff"));

                final ProgressBar pb = convertView.findViewById(R.id.progressBar);

                RelativeLayout playBtnlayout = convertView.findViewById(R.id.playBtnlayout);
                ImageView playBtn = convertView.findViewById(R.id.playBtn);

                if(nPlayingIndex == position) {         // 현재 플레이중이라면
                    if(timer != null) {
                        timer.cancel();
                        timer = null;
                    }

                    int nDuration = mediaPlayer.getDuration();
                    pb.setMax(nDuration);

                    playBtn.setImageResource(R.drawable.pause);
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if(mediaPlayer != null && mediaPlayer.isPlaying())
                                pb.setProgress(mediaPlayer.getCurrentPosition());
                            else {
                                timer.cancel();
                                timer = null;
                            }
                        }
                    }, 0, 1000);
                }

                playBtnlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(mediaPlayer != null && mediaPlayer.isPlaying()) {
                            if(nPlayingIndex == position) {
                                mediaPlayer.pause();
                                playBtn.setImageResource(R.drawable.talk_play1);
                                timer.cancel();
                                timer = null;
                                return;
                            } else {
                                mediaPlayer.stop();
                                oldPlayBtn.setImageResource(R.drawable.talk_play1);
                                oldPlayBtn = null;
                                oldPB.setProgress(0);
                                oldPB = null;
                                timer.cancel();
                                timer = null;
                                nPlayingIndex = -1;
                            }
                        } else if(mediaPlayer != null && !mediaPlayer.isPlaying()) {
                            if(nPlayingIndex == position) {
                                mediaPlayer.start();
                                playBtn.setImageResource(R.drawable.pause);

                                timer = new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        if(mediaPlayer != null && mediaPlayer.isPlaying())
                                            pb.setProgress(mediaPlayer.getCurrentPosition());
                                        else {
                                            if(timer != null) {
                                                timer.cancel();
                                                timer = null;
                                            }
                                        }
                                    }
                                }, 0, 1000);

                                return;
                            }
                        }

                        CommonUtils.showProgressDialog(LiteratureWriteActivity.this, "음원을 재생중입니다.");

                        String strUrl = chatVO.getStrContentsFile();
//                        strUrl = strUrl.replaceAll(" ", "");

                        if(!strUrl.startsWith("http")) {
                            try {
                                strUrl = URLEncoder.encode(strUrl, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                            strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;
                        }

                        strUrl = strUrl.replaceAll("\\+", "%20");
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        try {
                            mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(strUrl));
                            mediaPlayer.setLooping(false);
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                public void onPrepared(MediaPlayer mp) {
                                    CommonUtils.hideProgressDialog();
                                    int nDuration = mediaPlayer.getDuration();
                                    pb.setMax(nDuration);
                                    mp.start();

                                    playBtn.setImageResource(R.drawable.pause);

                                    timer = new Timer();
                                    timer.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            if(mediaPlayer != null && mediaPlayer.isPlaying())
                                                pb.setProgress(mediaPlayer.getCurrentPosition());
                                            else {
                                                timer.cancel();
                                                timer = null;
                                            }
                                        }
                                    }, 0, 1000);
                                }
                            });

                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    mediaPlayer.stop();
                                    mediaPlayer = null;
                                    playBtn.setImageResource(R.drawable.talk_play1);
                                    if(timer != null) {
                                        timer.cancel();
                                        timer = null;
                                    }
                                    oldPlayBtn = null;
                                    oldPB = null;
                                    nPlayingIndex = -1;
                                }
                            });

                            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                                @Override
                                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                                    Log.d("asdf", "asdf);");
                                    return false;
                                }
                            });

                            mediaPlayer.prepareAsync();
                            nPlayingIndex = position;
                            oldPlayBtn = playBtn;
                            oldPB = pb;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
            } else if(nType == ChatVO.TYPE_IMAGE_NAR) {
                convertView = mLiInflater.inflate(R.layout.narration_image_row, parent, false);

                ImageView imageContentsView = convertView.findViewById(R.id.imageContentsView);
                imageContentsView.setClipToOutline(true);

                if(chatVO.getContentsUri() != null) {
                    Glide.with(LiteratureWriteActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(chatVO.getContentsUri())
                            .apply(new RequestOptions().override(nWidth, nWidth))
                            .into(imageContentsView);
                } else if(chatVO.getStrContentsFile() != null) {
                    String strUrl = chatVO.getStrContentsFile();

                    if(!strUrl.startsWith("http"))
                        strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;

                    Glide.with(LiteratureWriteActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(strUrl)
                            .apply(new RequestOptions().override(nWidth, nWidth))
                            .into(imageContentsView);
                }
            } else if(nType == ChatVO.TYPE_IMAGE) {
                nDirection = characterVO.getDirection();

                if(nDirection == 0)             // left
                    convertView = mLiInflater.inflate(R.layout.left_image_row, parent, false);
                else
                    convertView = mLiInflater.inflate(R.layout.right_image_row, parent, false);

                TextView nameView = convertView.findViewById(R.id.nameView);
                nameView.setText(characterVO.getName());
                if(characterVO.isbBlackName())
                    nameView.setTextColor(Color.parseColor("#000000"));
                else
                    nameView.setTextColor(Color.parseColor("#ffffff"));

                ImageView imageContentsView = convertView.findViewById(R.id.imageContentsView);
                imageContentsView.setClipToOutline(true);

                if(chatVO.getContentsUri() != null) {
                    Glide.with(LiteratureWriteActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(chatVO.getContentsUri())
                            .apply(new RequestOptions().override(nWidth, nWidth))
                            .into(imageContentsView);
                } else if(chatVO.getStrContentsFile() != null) {
                    String strUrl = chatVO.getStrContentsFile();
//                    strUrl = strUrl.replaceAll(" ", "");

                    if(!strUrl.startsWith("http"))
                        strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;

                    Glide.with(LiteratureWriteActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(strUrl)
                            .apply(new RequestOptions().override(nWidth, nWidth))
                            .into(imageContentsView);
                }

                imageContentsView.setClipToOutline(true);
            } else if(nType == ChatVO.TYPE_NARRATION) {
                convertView = mLiInflater.inflate(R.layout.narration_chatting_row, parent, false);

                TextView contentsTextView = convertView.findViewById(R.id.contentsTextView);
                contentsTextView.setText(chatVO.getContents());
            } else if(nType == ChatVO.TYPE_VIDEO) {
                nDirection = characterVO.getDirection();

                if(nDirection == 0)             // left
                    convertView = mLiInflater.inflate(R.layout.left_video_row, parent, false);
                else
                    convertView = mLiInflater.inflate(R.layout.right_video_row, parent, false);

                TextView nameView = convertView.findViewById(R.id.nameView);
                nameView.setText(characterVO.getName());
                if(characterVO.isbBlackName())
                    nameView.setTextColor(Color.parseColor("#000000"));
                else
                    nameView.setTextColor(Color.parseColor("#ffffff"));

                ImageView imageContentsView = convertView.findViewById(R.id.videoThumbnailView);
                imageContentsView.setClipToOutline(true);

//                ImageView playBtn = convertView.findViewById(R.id.playBtn);
                imageContentsView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String strUrl = chatVO.getStrContentsFile();
                        if(!strUrl.startsWith("http"))
                            strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;

                        Intent intent = new Intent(LiteratureWriteActivity.this, VideoPlayerActivity.class);
                        intent.putExtra("VIDEO_URL", strUrl);
                        startActivity(intent);
                    }
                });

                if(chatVO.getContentsUri() != null) {
                    if(thumbBitmapList.containsKey(chatVO.getContentsUri().toString())) {
//                        imageContentsView.setImageBitmap(thumbBitmapList.get(chatVO.getContentsUri().toString()));
                        Glide.with(LiteratureWriteActivity.this)
                                .load(thumbBitmapList.get(chatVO.getContentsUri().toString()))
                                .apply(new RequestOptions().override(nWidth-50, nWidth-50))
                                .into(imageContentsView);
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap thumbnailBitmap = CommonUtils.getVideoThumbnail(chatVO.getContentsUri());
                                thumbBitmapList.put(chatVO.getContentsUri().toString(), thumbnailBitmap);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Glide.with(LiteratureWriteActivity.this)
                                                .load(thumbnailBitmap)
                                                .apply(new RequestOptions().override(nWidth-50, nWidth-50))
                                                .into(imageContentsView);
                                    }
                                });
                            }
                        }).start();

                        try {
                            Bitmap thumbnailBitmap = CommonUtils.getVideoThumbnail(chatVO.getContentsUri());
//                            Bitmap thumbnailBitmap = CommonUtils.retriveVideoFrameFromVideo(ViewerActivity.this, chatVO.getContentsUri().getPath());
                            thumbBitmapList.put(chatVO.getContentsUri().toString(), thumbnailBitmap);
                            Glide.with(LiteratureWriteActivity.this)
                                    .load(thumbnailBitmap)
                                    .apply(new RequestOptions().override(nWidth-50, nWidth-50))
                                    .into(imageContentsView);
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                } else if(chatVO.getStrContentsFile() != null) {
                    String strUrl = chatVO.getStrContentsFile();

                    if(!strUrl.startsWith("http"))
                        strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;

                    if(thumbBitmapList.containsKey(strUrl)) {
//                        imageContentsView.setImageBitmap(thumbBitmapList.get(strUrl));
                        Glide.with(LiteratureWriteActivity.this)
                                .load(thumbBitmapList.get(strUrl))
                                .apply(new RequestOptions().override(nWidth-50, nWidth-50))
                                .into(imageContentsView);
                    } else {
                        final String finalUrl = strUrl;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap thumbnailBitmap = CommonUtils.getVideoThumbnail(Uri.parse(finalUrl));
                                thumbBitmapList.put(finalUrl, thumbnailBitmap);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Glide.with(LiteratureWriteActivity.this)
                                                .load(thumbnailBitmap)
                                                .apply(new RequestOptions().override(nWidth-50, nWidth-50))
                                                .into(imageContentsView);
                                    }
                                });
                            }
                        }).start();
                    }
                }
            } else if(nType == ChatVO.TYPE_DISTRACTOR) {
                convertView = mLiInflater.inflate(R.layout.narration_chatting_row, parent, false);
                String strContents = chatVO.getContents();
                strContents = "분기 설정 : " + strContents.substring(0, strContents.indexOf("╋")) + ", " + strContents.substring(strContents.indexOf("╋")+1);
                TextView contentsTextView = convertView.findViewById(R.id.contentsTextView);
                contentsTextView.setText(strContents);
            } else if(nType == ChatVO.TYPE_CHANGE_BG) {
                convertView = mLiInflater.inflate(R.layout.narration_chatting_row, parent, false);
                TextView contentsTextView = convertView.findViewById(R.id.contentsTextView);
                contentsTextView.setText("배경 이미지 변경");

                if(chatVO.getContentsUri() != null) {
                    Glide.with(LiteratureWriteActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(chatVO.getContentsUri())
                            .transition(BitmapTransitionOptions.withCrossFade(300))
                            .into(bgView);
                } else if(chatVO.getStrContentsFile() != null) {
                    String strUrl = chatVO.getStrContentsFile();
//                    strUrl = strUrl.replaceAll(" ", "");

                    if(!strUrl.startsWith("http"))
                        strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;

                    Glide.with(LiteratureWriteActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(strUrl)
                            .transition(BitmapTransitionOptions.withCrossFade(300))
                            .into(bgView);
                }
            } else if(nType == ChatVO.TYPE_CHANGE_BG_COLOR) {
                convertView = mLiInflater.inflate(R.layout.narration_chatting_row, parent, false);

                TextView contentsTextView = convertView.findViewById(R.id.contentsTextView);
                contentsTextView.setText("배경 색 변경");

                String strColor = chatVO.getStrContentsFile();
                int nColor = Color.parseColor(strColor);
                bgView.setBackgroundColor(nColor);
            } else if(nType == ChatVO.TYPE_EMPTY) {
                convertView = mLiInflater.inflate(R.layout.empty_chatting_row, parent, false);
            }

            ImageView deleteBtn = convertView.findViewById(R.id.deleteBtn);
            if(deleteBtn != null) {
                deleteBtn.setClipToOutline(true);
                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                    chattingList.remove(position);
                        requestDeleteMessage(position);
                    }
                });
            }

            if(characterVO != null && (nType == ChatVO.TYPE_TEXT || nType == ChatVO.TYPE_SOUND)) {
                RelativeLayout contentsLayout = convertView.findViewById(R.id.contentsLayout);
                if(contentsLayout != null) {
                    String strBalloonColor = characterVO.getStrBalloonColor();

                    if(strBalloonColor != null && !strBalloonColor.equals("null") && strBalloonColor.length() > 0) {
                        if(contentsLayout != null && contentsLayout.getBackground() != null) {
                            int nColor = Color.parseColor(strBalloonColor);
                            PorterDuffColorFilter greyFilter = new PorterDuffColorFilter(nColor, PorterDuff.Mode.MULTIPLY);
                            contentsLayout.getBackground().setColorFilter(greyFilter);
                        }
                    } else {
                        if(contentsLayout != null && contentsLayout.getBackground() != null)
                            contentsLayout.getBackground().setColorFilter(null);
                    }
                }
            }

            final RelativeLayout addBtn = convertView.findViewById(R.id.addBtn);
            final ImageView leftLine = convertView.findViewById(R.id.leftLine);
            final ImageView rightLine = convertView.findViewById(R.id.rightLine);
            final TextView txtView = convertView.findViewById(R.id.txtView);

            if(position == chattingList.size() - 1)
                addBtn.setVisibility(View.INVISIBLE);
            else
                addBtn.setVisibility(View.VISIBLE);

            ImageView faceView = convertView.findViewById(R.id.faceView);
            if(faceView != null) {
                int nPlaceHolder = 0;
                int faceIndex = getCharacterIndex(characterVO) % 10;
                switch(faceIndex) {
                    case 1:
                        nPlaceHolder = R.drawable.user_icon_01;
                        break;
                    case 2:
                        nPlaceHolder = R.drawable.user_icon_02;
                        break;
                    case 3:
                        nPlaceHolder = R.drawable.user_icon_03;
                        break;
                    case 4:
                        nPlaceHolder = R.drawable.user_icon_04;
                        break;
                    case 5:
                        nPlaceHolder = R.drawable.user_icon_05;
                        break;
                    case 6:
                        nPlaceHolder = R.drawable.user_icon_06;
                        break;
                    case 7:
                        nPlaceHolder = R.drawable.user_icon_07;
                        break;
                    case 8:
                        nPlaceHolder = R.drawable.user_icon_08;
                        break;
                    case 9:
                        nPlaceHolder = R.drawable.user_icon_09;
                        break;
                    case 0:
                        nPlaceHolder = R.drawable.user_icon_10;
                        break;
                }

                if(characterVO.getImage() != null && !characterVO.getImage().equals("null")) {
                    Glide.with(LiteratureWriteActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(characterVO.getImage())
                            .placeholder(nPlaceHolder)
                            .apply(new RequestOptions().circleCrop())
                            .into(faceView);
                } else if(characterVO.getStrImgFile() != null && !characterVO.getStrImgFile().equals("null")) {
                    String strUrl = characterVO.getStrImgFile();
//                    strUrl = strUrl.replaceAll(" ", "");

                    if(!strUrl.startsWith("http"))
                        strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;

                    Glide.with(LiteratureWriteActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .placeholder(nPlaceHolder)
                            .load(strUrl)
                            .apply(new RequestOptions().circleCrop())
                            .into(faceView);
                } else {
                    Glide.with(LiteratureWriteActivity.this)
                            .asBitmap() // some .jpeg files
                            .load(nPlaceHolder)
                            .apply(new RequestOptions().circleCrop())
                            .into(faceView);
                }
            }

            if(nAddIndex == position) {
//                addBtn.setBackgroundResource(R.drawable.common_selected_rounded_btn_bg);
                txtView.setText("취소");
                leftLine.setBackgroundColor(Color.parseColor("#ff0000"));
                rightLine.setBackgroundColor(Color.parseColor("#ff0000"));
                txtView.setTextColor(Color.parseColor("#ff0000"));
            } else {
//                addBtn.setBackgroundResource(R.drawable.common_gray_rounded_btn_bg);
                txtView.setText("사이에 추가");
                txtView.setTextColor(Color.parseColor("#000000"));
                leftLine.setBackgroundColor(Color.parseColor("#000000"));
                rightLine.setBackgroundColor(Color.parseColor("#000000"));
            }

            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(nAddIndex == position) {
                        nAddIndex = -1;
                        aa.notifyDataSetChanged();
                    } else {
                        Toast.makeText(LiteratureWriteActivity.this, "내용을 추가하시면 선택하신 사이에 추가 됩니다.\n취소하시려면 버튼을 다시 한 번 눌러주세요.", Toast.LENGTH_LONG).show();
                        nAddIndex = position;
                        aa.notifyDataSetChanged();
                    }
                }
            });

            TextView commentCountView = convertView.findViewById(R.id.commentCountView);
            if(commentCountView != null) {
                commentCountView.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }
    }

    private int getCharacterIndex(CharacterVO vo) {                                                 // 캐릭터 아이디로 몇번째 캐릭터인지 가져오는 함수
        int characterID = vo.getnCharacterID();

        for(int i = 1 ; i < characterList.size() ; i++) {
            CharacterVO characterVO = characterList.get(i);
            if(characterVO == null)
                return 1;
            if(characterVO.getnCharacterID() == characterID)
                return i;
        }

        return 1;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {                                         // 엑셀 업로드 완료시 호출
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CommonUtils.hideProgressDialog();
                    isExcelUploaded = true;
                    ImageButton submitBtn = findViewById(R.id.submitBtn);
                    submitBtn.setBackgroundResource(R.drawable.send_button);
                    getCharacterData();
                }
            });
        }
    };

    public void clickEditTitleBtn(View view) {
        Intent intent = new Intent(LiteratureWriteActivity.this, ChangeTitlePopup.class);
        intent.putExtra("TITLE", strTitle);
        intent.putExtra("EPISODE_ID", nEpisodeID);
        startActivityForResult(intent, 1100);
    }
}