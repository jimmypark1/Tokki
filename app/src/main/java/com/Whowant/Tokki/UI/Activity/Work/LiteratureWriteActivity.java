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
import androidx.core.content.ContextCompat;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Media.VideoPlayerActivity;
import com.Whowant.Tokki.UI.Activity.Photopicker.PhotoPickerActivity;
import com.Whowant.Tokki.UI.Popup.BGImageSelectPopup;
import com.Whowant.Tokki.UI.Popup.ChangeTitlePopup;
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

import static com.Whowant.Tokki.Utils.CommonUtils.from;
import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_CONTENTS_IMG;
import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_CONTENTS_IMG_NAR;
import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_VIDEO;

public class LiteratureWriteActivity extends AppCompatActivity implements View.OnClickListener, ColorPickerDialogListener {                             // ?????? ?????????
    public static WorkVO workVO;
    private ArrayList<CharacterVO> characterList;                                                                                                       // ????????? ???????????? ????????? ?????????
    private ArrayList<ChatVO> chattingList;                                                                                                             // ?????? ????????? ?????????(?????? ?????? ??????)
    private HashMap<String, Bitmap> thumbBitmapList = new HashMap<>();                                                                                  // ????????? ????????? ???????????? ?????? ???????????? ?????? ???????????? ??????

    private LinearLayout speakerAddLayout;                                                                                                              // ????????? ?????? ??????????????? ???????????? ?????? ??????
    private ArrayList<String> nameList;                                                                                                                 // ???????????? ?????? ?????????
    private ArrayList<View> characterViewList;                                                                                                          // ??????????????? view List
    private int nSelectedCharacterIndex = 0;                                                                                                            // 0 = ?????????
    private boolean bShowMenu = false;                                                                                                                  // ?????? ????????? ??????????????? ??????
    private ConstraintLayout bottomSettingLayout;                                                                                                       // ?????? ?????? Layout
    private EditText inputTextView;
    private InputMethodManager imm;
    // winhmoon
    private LinearLayout contentsAddBtn;
    private ListView chattingListView;                                                                                                                  // ????????? ???????????? ListView
    private CChattingArrayAdapter aa;

    private LinearLayout bgSettingView;                                                                                                                 // ?????? ?????? ??????
    private LinearLayout imgSettingView;                                                                                                                // ????????? ?????? ??????
    private LinearLayout videoSettingView;                                                                                                              // ????????? ?????? ??????
    private LinearLayout distractorView;
    private LinearLayout soundSettingView;

    private int nBgColor;
    private String bgColor;
    private boolean bColorPicker = false;

    private ImageView bgView;

    private int nEpisodeID;
    private int nInteractionIndex;              // 1??????, 2?????? ??? ????????? ??????
    private int nEpisodeOrder;
    private int nAddIndex = -1;             // ????????? ???????????? ??????
    private int nEditIndex = -1;            // ???????????? ??????
    private int nDeleteIndex = -1;
    private ProgressDialog mProgressDialog;

    private TextView titleView, episodeNumView;
    private ArrayList<String> newFileList;
    private boolean isEdit = false;
    private String strTitle = "";

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Timer timer;
    private int nPlayingIndex = -1;                     // ????????? ??????/??????????????? ?????? ?????? ???????????? ????????? ??????
    private ImageView oldPlayBtn;                       // ????????? ??????????????? ?????? ?????? ????????? ???????????? ??? ????????? ????????? ?????? ?????? ?????? ??????
    private ProgressBar oldPB;
    private boolean bSubmit = false;                    // ????????? ??????(????????????) ???????????? ??????
    private TextView sendBtn;
    private float fX, fY;                               // ????????? ?????? ?????? ?????? ?????? ??????

    private ViewGroup viewGroup;
    private SoftKeyboard softKeyboard;
    private boolean isExcelUploaded = false;

    // [S] winhmoon
    final int TYPE_TEXT = 0;
    final int TYPE_CHARACTER = 1;
    final int TYPE_MEDIA = 2;
    int type = TYPE_TEXT;

    ImageView textAddBtn;
    ImageView characterAddBtn;
    LinearLayout bottomCharacterLayout;
    ImageView contentsAddImg;

    int nBeforeCharacterIndex = 0;
    // [E] winhmoon

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_renewal_layout);

        strTitle = getIntent().getStringExtra("EPISODE_TITLE");
        if (getIntent().getStringExtra("SUBMIT").equals("Y")) {
            bSubmit = true;
        } else {
            bSubmit = false;
        }

        isExcelUploaded = getIntent().getBooleanExtra("EXCEL_UPLOADED", false);
        ImageButton submitBtn = findViewById(R.id.submitBtn);

        if (isExcelUploaded || workVO.getnUserStatus() == 10 || workVO.getnUserStatus() == 20) {
            submitBtn.setBackgroundResource(R.drawable.send_button);
        } else {
            submitBtn.setBackgroundResource(R.drawable.post_button);
        }

        int nKeyboard = getResources().getConfiguration().keyboard;
        Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
        if (!(handler instanceof CustomUncaughtExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
        }

        IntentFilter filter = new IntentFilter();                                                   // ?????? ????????? ??? ????????? ?????? ???????????? ???????????? ????????? ??????
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
        nEpisodeOrder = getIntent().getIntExtra("EPISODE_ORDER", 0);
        episodeNumView.setText(nEpisodeOrder + "???");
        characterList = new ArrayList<>();
        nameList = new ArrayList<>();
        characterViewList = new ArrayList<>();
        characterList.add(null);
        nameList.add("??????");
        LinearLayout speakerAddView = findViewById(R.id.speakerAddView);
        speakerAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharacterRegActivity.nameList = new ArrayList<String>(nameList);
                startActivityForResult(new Intent(LiteratureWriteActivity.this, CharacterRegActivity.class), 1010);
//                CreateCharacterActivity.nameList = new ArrayList<String>(nameList);
//                startActivityForResult(new Intent(LiteratureWriteActivity.this, CreateCharacterActivity.class), 1010);
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
                if (inputTextView.getText().toString().length() > 0) {
//                    sendBtn.setBackgroundResource(R.drawable.common_btn_bg);
                    sendBtn.setTextColor(Color.parseColor("#5a9aff"));
                    sendBtn.setEnabled(true);
                } else {
//                    sendBtn.setBackgroundResource(R.drawable.common_btn_disable_bg);
                    sendBtn.setTextColor(Color.parseColor("#cccccc"));
                    sendBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        resetCharacterLayout();

        if (nKeyboard != 2)
            setKeyboardEvent();

        chattingList = new ArrayList<>();
        aa = new CChattingArrayAdapter(LiteratureWriteActivity.this, R.layout.right_chatting_row, chattingList);
        chattingListView.setAdapter(aa);
        chattingListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {                       // ???????????? ??????
                nEditIndex = position;
                ChatVO vo = chattingList.get(position);
                int nType = vo.getType();
                if (nType == ChatVO.TYPE_TEXT || nType == ChatVO.TYPE_NARRATION) {                                               // ?????? ????????? ?????? ?????? ???????????? ????????? ?????? ?????????
                    Intent intent = new Intent(LiteratureWriteActivity.this, TextEditPopup.class);
                    intent.putExtra("TEXT", vo.getContents());
                    intent.putExtra("ORDER", position);
                    startActivityForResult(intent, 1050);
                } else if (nType == ChatVO.TYPE_IMAGE || nType == ChatVO.TYPE_VIDEO) {
                    TedPermission.with(LiteratureWriteActivity.this).setPermissionListener(new PermissionListener() {   // ????????? ?????? ??????????????? ????????? ?????? ??? ?????????/?????? ?????? ????????????
                        @Override
                        public void onPermissionGranted() {
                            Intent intent = new Intent(LiteratureWriteActivity.this, MediaSelectPopup.class);
                            if (nType == ChatVO.TYPE_IMAGE)
                                intent.putExtra("TYPE", TYPE_CONTENTS_IMG.ordinal());
                            else
                                intent.putExtra("TYPE", TYPE_VIDEO.ordinal());

                            intent.putExtra("EDIT", true);
                            intent.putExtra("ORDER", position);
                            startActivityForResult(intent, 2000);
                        }

                        @Override
                        public void onPermissionDenied(List<String> deniedPermissions) {
                            Toast.makeText(LiteratureWriteActivity.this, "????????? ?????????????????? ?????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
                        }
                    })
                            .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                            .check();
                } else if (nType == ChatVO.TYPE_SOUND) {                                                                             // ???????????? ????????? ????????? ?????? ??? ???????????? ?????? ????????????
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
                                    Toast.makeText(LiteratureWriteActivity.this, "????????? ?????????????????? ?????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                            .check();
                } else if (nType == ChatVO.TYPE_CHANGE_BG || nType == ChatVO.TYPE_CHANGE_BG_COLOR) {                                 // ???????????? ??????????????? ?????? ???????????? ??????
                    Intent intent = new Intent(LiteratureWriteActivity.this, BGImageSelectPopup.class);
                    intent.putExtra("EDIT", true);
                    intent.putExtra("ORDER", position);
                    startActivityForResult(intent, 1000);
                } else if (nType == ChatVO.TYPE_DISTRACTOR) {                                                                        // ?????? ??????????????? ?????? ?????? ??????
                    Intent intent = new Intent(LiteratureWriteActivity.this, DistractorPopup.class);
                    intent.putExtra("EDIT", true);
                    intent.putExtra("ORDER", position);
                    intent.putExtra("TEXT", vo.getContents());
                    startActivityForResult(intent, 1025);
                } else if (nType == ChatVO.TYPE_IMAGE_NAR) {                                                                         // ???????????? ????????? ?????? ????????? ?????? ???????????? ??????
                    Intent intent = new Intent(LiteratureWriteActivity.this, MediaSelectPopup.class);
                    intent.putExtra("TYPE", TYPE_CONTENTS_IMG_NAR.ordinal());
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

        if (nInteractionIndex > -1) {                                                        // ?????? ????????? ?????? ????????? ??????????????? ?????? ?????? ???????????? ??????
            EpisodeVO episodeVO = workVO.getEpisodeList().get(nInteractionIndex);
            if (episodeVO.getIsDistractor() == true) {
                Intent intent = new Intent(LiteratureWriteActivity.this, InteractionWriteActivity.class);
                InteractionWriteActivity.workVO = workVO;
                intent.putExtra("TITLE", strTitle);
                intent.putExtra("EPISODE_ID", nEpisodeID);
                intent.putExtra("EPISODE_ORDER", nEpisodeOrder);
                intent.putExtra("EPISODE_INDEX", nInteractionIndex);
                startActivity(intent);
                finish();
                return;
            }
        }

        if (nEpisodeID > -1) {
            getCharacterData();
        }

        sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strContents = inputTextView.getText().toString();
                CommonUtils.showProgressDialog(LiteratureWriteActivity.this, "????????? ??????????????????. ????????? ??????????????????.");

                String strFobiddenWords = CommonUtils.checkForbiddenWords(strContents);
                if (strFobiddenWords.length() > 0) {
                    CommonUtils.hideProgressDialog();
                    Intent intent = new Intent(LiteratureWriteActivity.this, SlangPopup.class);
                    intent.putExtra("SLANG", strFobiddenWords);
                    startActivity(intent);
                    overridePendingTransition(R.anim.cross_fade_in, R.anim.cross_fade_out);
                    return;
                }

                CommonUtils.hideProgressDialog();

                if (strContents.length() == 0) {
                    Toast.makeText(LiteratureWriteActivity.this, "????????? ???????????????.", Toast.LENGTH_LONG).show();
                    return;
                }

                ChatVO chatVO = new ChatVO();

                if (nSelectedCharacterIndex == 0) {                   // ????????????
                    chatVO.setType(ChatVO.TYPE_NARRATION);
                } else {                                             // ????????? ??????????????? ??????
                    chatVO.setType(ChatVO.TYPE_TEXT);
                    CharacterVO characterVO = characterList.get(nSelectedCharacterIndex);
                    chatVO.setCharacter(characterVO);
                }

                if (nEditIndex > -1) {
                    chatVO = chattingList.get(nEditIndex);
                } else {
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
                }

                chatVO.setContents(strContents);
                requestUploadMessage(chatVO, false);
            }
        });

        if (bSubmit) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LiteratureWriteActivity.this);
            builder.setMessage("?????? ????????? ???????????????. ????????? ????????? ??????????????? ?????? ????????? ?????? ?????? ??????????????? ?????????.");
            builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        chattingListView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    fX = motionEvent.getX();
                    fY = motionEvent.getY();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                    float fEndX = motionEvent.getX();
                    float fEndY = motionEvent.getY();

                    if (fX >= fEndX + 10 || fX <= fEndX - 10 || fY >= fEndY + 10 || fY <= fEndY - 10) {              // 10px ?????? ???????????????
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

        initView();
    }

    // [S] winhmoon
    private void initView() {
        inputTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                boolean isTextShow = textAddBtn.isShown();
                boolean isCharacterShow = characterAddBtn.isShown();
                boolean isContentsShow = contentsAddBtn.isShown();

                if (hasFocus && (isTextShow && isCharacterShow && isContentsShow) || (nBeforeCharacterIndex == 0 && type != TYPE_TEXT)) {
                    nSelectedCharacterIndex = 0;
                    setSelectBottomLayout(TYPE_TEXT);
                } else if (isCharacterShow && isContentsShow) {
                    setSelectBottomLayout(TYPE_CHARACTER);
                }
            }
        });

        inputTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isTextShow = textAddBtn.isShown();
                boolean isCharacterShow = characterAddBtn.isShown();
                boolean isContentsShow = contentsAddBtn.isShown();

                if ((isTextShow && isCharacterShow && isContentsShow) || (nBeforeCharacterIndex == 0 && type != TYPE_TEXT)) {
                    nSelectedCharacterIndex = 0;
                    setSelectBottomLayout(TYPE_TEXT);
                } else if (isCharacterShow && isContentsShow) {
                    setSelectBottomLayout(TYPE_CHARACTER);
                }
            }
        });

        textAddBtn = findViewById(R.id.textAddBtn);
        textAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nSelectedCharacterIndex = 0;
                setSelectBottomLayout(TYPE_TEXT);
            }
        });
        characterAddBtn = findViewById(R.id.characterAddBtn);
        characterAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nSelectedCharacterIndex = nBeforeCharacterIndex;
                setSelectBottomLayout(TYPE_CHARACTER);
            }
        });

        bottomCharacterLayout = findViewById(R.id.bottomCharacterLayout);
        bottomCharacterLayout.setVisibility(View.GONE);
        contentsAddImg = findViewById(R.id.contentsAddImg);
    }

    // ??????, ????????????, + ?????? ??????
    private void resetBottomLayout() {
        textAddBtn.setVisibility(View.VISIBLE);
        textAddBtn.setImageResource(R.drawable.ic_i_text);
        textAddBtn.setSelected(false);
        characterAddBtn.setVisibility(View.VISIBLE);
        characterAddBtn.setSelected(false);
        contentsAddBtn.setVisibility(View.VISIBLE);
        contentsAddBtn.setSelected(false);
        bottomSettingLayout.setVisibility(View.GONE);
        bottomCharacterLayout.setVisibility(View.GONE);
        contentsAddBtn.setBackgroundResource(R.drawable.circle_cccccc);
        contentsAddImg.setImageResource(R.drawable.ic_i_plus);
    }

    private void setSelectBottomLayout(int type) {
        boolean isTextSelected = !textAddBtn.isSelected();
        boolean isTextShow = textAddBtn.isShown();
        boolean isCharacterSelected = !characterAddBtn.isSelected();
        boolean isContentsSelected = !contentsAddBtn.isSelected();

        resetBottomLayout();

        if (this.type != type) {
            switch (type) {
                case TYPE_TEXT:
                    showBottomText(true);
                    showBottomCharacter(false, false, false);
                    showBottomContents(true, false, false);
                    break;
                case TYPE_CHARACTER:
                    showBottomText(false);
                    showBottomCharacter(true, true, true);
                    showBottomContents(true, false, false);
                    break;
                case TYPE_MEDIA:
                    switch (this.type) {
                        case TYPE_CHARACTER:
                            showBottomText(false);
                            showBottomCharacter(true, false, false);
                            showBottomContents(true, true, true);
                            break;
                        default:
                            showBottomText(true);
                            showBottomCharacter(false, false, false);
                            showBottomContents(true, true, true);
                            break;
                    }
                    break;
            }
        } else {
            switch (type) {
                case TYPE_TEXT:
                    if (isTextSelected) {
                        showBottomText(true);
                        showBottomCharacter(false, false, false);
                        showBottomContents(true, false, false);
                    }
                    break;
                case TYPE_CHARACTER:
                    if (isCharacterSelected) {
                        if (isTextShow) {
                            showBottomText(false);
                            showBottomCharacter(true, true, true);
                            showBottomContents(true, false, false);
                        }
                    } else {
                        showBottomText(false);
                        showBottomCharacter(true, false, false);
                        showBottomContents(true, false, false);
                    }
                    break;
            }
        }

        this.type = type;
    }

    private void showBottomText(boolean isSelected) {
        textAddBtn.setVisibility(isSelected ? View.VISIBLE : View.GONE);
        textAddBtn.setSelected(isSelected);
        textAddBtn.setImageResource(isSelected ? R.drawable.i_text_black : R.drawable.ic_i_text);
    }

    private void showBottomCharacter(boolean isShow, boolean isSelected, boolean isLayout) {
        characterAddBtn.setVisibility(isShow ? View.VISIBLE : View.GONE);
        characterAddBtn.setSelected(isSelected);
        bottomCharacterLayout.setVisibility(isLayout ? View.VISIBLE : View.GONE);
    }

    private void showBottomContents(boolean isShow, boolean isSelected, boolean isLayout) {
        contentsAddBtn.setVisibility(isShow ? View.VISIBLE : View.GONE);
        contentsAddBtn.setSelected(isSelected);
        contentsAddBtn.setBackgroundResource(isSelected ? R.drawable.circle_222222 : R.drawable.circle_cccccc);
        contentsAddImg.setImageResource(isSelected ? R.drawable.ic_i_plus_white : R.drawable.ic_i_plus);
        bottomSettingLayout.setVisibility(isLayout ? View.VISIBLE : View.GONE);
    }
    // [E] winhmoon

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {           // BT ????????? ?????????
            removeKeyboardEvent();
        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {     // BT ????????? ?????????
            setKeyboardEvent();
        }
    }

    private void setKeyboardEvent() {
        hideBottomView();
        viewGroup = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
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
        if (bShowMenu) {
            bottomSettingLayout.setVisibility(View.GONE);
//            contentsAddBtn.setImageResource(R.drawable.selectionplus);
            bShowMenu = false;
            return;
        }

        super.onBackPressed();
    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }

    public void onClickSubmitBtn(View view) {
        if (isExcelUploaded || workVO.getnUserStatus() == 10 || workVO.getnUserStatus() == 20) {
            requestEpisodeSubmit();
        } else {
            requestEpisodePost();
        }
    }

    public void onClickTopRightBtn(View view) {                                                             // ????????? ??? ????????? ???????????? ?????? ?????? ???????????? ?????? ????????? ????????? ?????? ??????
        PopupMenu popup = new PopupMenu(LiteratureWriteActivity.this, view);
        popup.getMenuInflater().inflate(R.menu.work_write_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = null;

                switch (item.getItemId()) {
                    case R.id.action_btn2:
                        if (chattingList.size() > 1) {
                            Toast.makeText(LiteratureWriteActivity.this, "????????? ????????? ???????????? ?????? ????????? ????????? ??? ????????????.", Toast.LENGTH_LONG).show();
                            return true;
                        } else if (characterList.size() > 1) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LiteratureWriteActivity.this);
                            builder.setMessage("?????? ????????? ??????????????? ????????????. ?????? ????????? ???????????? ????????? ??????????????? ?????? ???????????????.\n?????? ??????????????? ?????????????????????????");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    filePermission();
                                }
                            });

                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
                        workVO.setSortedEpisodeList(workVO.getEpisodeList());
                        intent.putExtra("INTERACTION", workVO.getEpisodeList().get(nInteractionIndex).getIsDistractor());
                        intent.putExtra("PREVIEW", true);
                        startActivity(intent);
                        return true;
                    case R.id.action_btn4:
                        if (!chattingList.isEmpty()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LiteratureWriteActivity.this);
                            builder.setTitle("?????? ??????");
                            builder.setMessage("????????? ?????? ????????? ???????????????.\n?????????????????????????");
                            builder.setPositiveButton("???", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    requestDeleteAllMessage();
                                }
                            });

                            builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                            return true;
                        } else {
                            Toast.makeText(LiteratureWriteActivity.this, "????????? ????????? ????????????.", Toast.LENGTH_LONG).show();
                        }
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
        int nOrder = getIntent().getIntExtra("ORDER", -1);

        String imgUri = intent.getStringExtra("BG_URI");
        if (imgUri != null) {                    // ?????? ?????? ?????????
            String strFilePath = CommonUtils.getRealPathFromURI(LiteratureWriteActivity.this, Uri.parse(imgUri));
            newFileList.add(strFilePath);

            ChatVO chatVO = null;

            if (bEdit) {
                chatVO = chattingList.get(nEditIndex);
            } else {
                chatVO = new ChatVO();
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

//            nEditIndex = -1;
//            nAddIndex = -1;
            return;
        }

        imgUri = intent.getStringExtra("IMG_URI");
        if (imgUri != null) {                                    // ?????? ?????? ????????? ??????
            int nType = intent.getIntExtra("TYPE", 0);
            ChatVO chatVO = null;

            if (bEdit) {
                chatVO = chattingList.get(nEditIndex);
            } else {
                chatVO = new ChatVO();
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
            }

            if (nType == TYPE_CONTENTS_IMG.ordinal()) {
                CharacterVO characterVO = characterList.get(nSelectedCharacterIndex);
                chatVO.setCharacter(characterVO);
                chatVO.setType(ChatVO.TYPE_IMAGE);
            } else if (nType == TYPE_CONTENTS_IMG_NAR.ordinal()) {
                chatVO.setType(ChatVO.TYPE_IMAGE_NAR);
            }

            chatVO.setContentsUri(Uri.parse(imgUri));
            chatVO.setStrContentsFile(CommonUtils.getRealPathFromURI(LiteratureWriteActivity.this, Uri.parse(imgUri)));
            requestUploadMessage(chatVO, bEdit);

//            nEditIndex = -1;
//            nAddIndex = -1;
            return;
        }

        imgUri = intent.getStringExtra("VIDEO_URI");
        if (imgUri != null) {
            ChatVO chatVO = null;

            if (bEdit) {
                chatVO = chattingList.get(nEditIndex);
            } else {
                chatVO = new ChatVO();
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
            }

            CharacterVO characterVO = characterList.get(nSelectedCharacterIndex);
            chatVO.setCharacter(characterVO);

            chatVO.setType(ChatVO.TYPE_VIDEO);
            chatVO.setContentsUri(Uri.parse(imgUri));
            chatVO.setStrContentsFile(CommonUtils.getRealPathFromURI(LiteratureWriteActivity.this, Uri.parse(imgUri)));
            requestUploadMessage(chatVO, bEdit);

//            nEditIndex = -1;
//            nAddIndex = -1;
            return;
        }

//        nEditIndex = -1;
//        nAddIndex = -1;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (bColorPicker) {
            bColorPicker = false;
            ColorPickerDialog.newBuilder()
                    .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                    .setAllowPresets(false)
                    .setDialogTitle(R.string.title_color_palette)
                    .setDialogId(1010)
                    .setDialogTitle(R.string.title_color_palette)
                    .setColor(nBgColor)
                    .setShowAlphaSlider(true)
                    .show(LiteratureWriteActivity.this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
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
                        String[] mimeTypes = {"application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet","application/x-msexcel"};
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
                            if (mimeTypes.length > 0) {
                                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                            }
                        } else {
                            String mimeTypesStr = "";
                            for (String mimeType : mimeTypes) {
                                mimeTypesStr += mimeType + "|";
                            }
                            intent.setType(mimeTypesStr.substring(0,mimeTypesStr.length() - 1));
                        }
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
        if (!bEdit)
            nEditIndex = -1;
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {                // ?????? ??? ???????????? ????????? ????????? ?????????
            mediaPlayer.stop();
            oldPlayBtn.setImageResource(R.drawable.talk_play1);
            oldPlayBtn = null;
            oldPB.setProgress(0);
            oldPB = null;
            timer.cancel();
            timer = null;
            nPlayingIndex = -1;
        }

        mProgressDialog.setMessage("????????? ??????????????????. ????????? ??????????????????.");
        mProgressDialog.show();

        try {
            String url = CommonUtils.strDefaultUrl + "PanAppUploadChat.jsp";
            RequestBody requestBody = null;

            File sourceFile = null;

            JSONObject sendObject = new JSONObject();

            if (nEpisodeID > -1)
                sendObject.put("EPISODE_ID", nEpisodeID);

            if (nAddIndex > -1) {                                // ????????? ????????????
                sendObject.put("CHAT_INTERCEPT", true);
            } else {
                sendObject.put("CHAT_INTERCEPT", false);
            }

            if (bEdit)                                           // ??????
                sendObject.put("CHAT_MODIFY", true);
            else
                sendObject.put("CHAT_MODIFY", false);

            JSONArray chatArray = new JSONArray();
            JSONObject chatObject = new JSONObject();

            int nType = chatVO.getType();
            chatObject.put("EPISODE_ID", nEpisodeID);
            chatObject.put("CHAT_TYPE", nType);

            if (chatVO.getCharacterVO() != null)
                chatObject.put("CHARACTER_ID", chatVO.getCharacterVO().getnCharacterID());

            chatObject.put("CHAT_ORDER", chatVO.getnOrder());

            if (nType == 1 || nType == 2 || nType == 7) {
                chatObject.put("CHAT_CONTENTS", chatVO.getContents());
            } else if (nType == 3 || nType == 4 || nType == 5 || nType == 8 || nType == 11) {           // ????????? ?????? ?????? ?????? ????????????
                String strPath = chatVO.getStrContentsFile();

                if (strPath == null || strPath.length() == 0) {
                    if (chatVO.getContentsUri() != null)
                        chatObject.put("CHAT_CONTENTS", chatVO.getContentsUri().toString());
                } else {
                    String filename = strPath.substring(strPath.lastIndexOf("/") + 1);
                    chatObject.put("CHAT_CONTENTS", filename);
                }
            } else if (nType == 6) {
                chatObject.put("CHAT_CONTENTS", chatVO.getStrContentsFile());
            }

            chatArray.put(chatObject);

            sendObject.put("CHAT_ARRAY", chatArray);
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM).addFormDataPart("JSON_BODY", sendObject.toString());

            if (nType == 3 || nType == 4 || nType == 5 || nType == 8 || nType == 11) {
                String strPath = chatVO.getStrContentsFile();
                if (strPath != null && strPath.length() > 0) {
                    sourceFile = new File(strPath);
                    String filename = strPath.substring(strPath.lastIndexOf("/") + 1);
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
//                Toast.makeText(LiteratureWriteActivity.this, "?????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(LiteratureWriteActivity.this, "????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.code() != 200) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProgressDialog.dismiss();
                                CommonUtils.makeText(LiteratureWriteActivity.this, "???????????? ????????? ???????????? ????????????. ????????? ?????? ????????? ?????????.", Toast.LENGTH_LONG).show();
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

                                if (resultObject.getString("RESULT").equals("SUCCESS")) {
                                    if (resultObject.has("CHAT_ID")) {
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

                                    if (chatVO.getType() == ChatVO.TYPE_DISTRACTOR) {                // ?????? ?????? ??????. ????????? ??????
                                        Intent intent = new Intent(LiteratureWriteActivity.this, InteractionWriteActivity.class);
                                        InteractionWriteActivity.workVO = workVO;
                                        intent.putExtra("TITLE", strTitle);
                                        intent.putExtra("EPISODE_ID", nEpisodeID);
                                        intent.putExtra("EPISODE_INDEX", nInteractionIndex);
                                        intent.putExtra("EPISODE_ORDER", nEpisodeOrder);
                                        intent.putExtra("SUBMIT", bSubmit);
                                        intent.putExtra("EXCEL_UPLOADED", isExcelUploaded);
                                        startActivity(intent);
                                        finish();
                                        return;
                                    }

                                    getEpisodeChatData();
                                } else {
                                    Toast.makeText(LiteratureWriteActivity.this, "?????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(LiteratureWriteActivity.this, "?????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void requestDeleteCharacter(final int nIndex) {                                 // ???????????? ??????
        mProgressDialog.setMessage("????????? ??????????????????. ????????? ??????????????????.");
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

                            if (resultObject != null && resultObject.getString("RESULT").equals("SUCCESS")) {
                                characterList.remove(nIndex);
//                                resetCharacterLayout();
                                nSelectedCharacterIndex = 0;
                                getCharacterData();
                                Toast.makeText(LiteratureWriteActivity.this, "?????????????????????.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LiteratureWriteActivity.this, "???????????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    private void requestDeleteAllMessage() {                                                // ?????? ?????? ??????
        mProgressDialog.setMessage("????????? ??????????????????. ????????? ??????????????????.");
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

                            if (resultObject != null && resultObject.getString("RESULT").equals("SUCCESS")) {
                                bgView.setBackgroundResource(0);
                                bgView.setImageBitmap(null);
                                bgView.setBackgroundColor(getResources().getColor(R.color.colorDefaultBG));

                                chattingList.clear();
                                aa.notifyDataSetChanged();

                                Toast.makeText(LiteratureWriteActivity.this, "?????????????????????.", Toast.LENGTH_LONG).show();

                                getCharacterData();
                            } else {
                                Toast.makeText(LiteratureWriteActivity.this, "????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    private void requestDeleteMessage(final int nIndex) {                               // ?????? ?????? ??????
        mProgressDialog.setMessage("????????? ??????????????????. ????????? ??????????????????.");
        mProgressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (nIndex >= chattingList.size()) {
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

                            if (vo.getType() == ChatVO.TYPE_CHANGE_BG || vo.getType() == ChatVO.TYPE_CHANGE_BG_COLOR) {
                                bgView.setBackgroundResource(0);
                                bgView.setImageBitmap(null);
                                bgView.setBackgroundColor(getResources().getColor(R.color.colorDefaultBG));
                            }

                            if (resultObject != null && resultObject.getString("RESULT").equals("SUCCESS")) {
                                chattingList.remove(nIndex);
                                getEpisodeChatData();
                                Toast.makeText(LiteratureWriteActivity.this, "?????????????????????.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LiteratureWriteActivity.this, "?????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    private void requestEpisodePost() {                                                                   // ?????? ??????(??????)
        Intent intent = new Intent(LiteratureWriteActivity.this, WorkPostPopup.class);
        startActivityForResult(intent, 9000);
    }

    private void sendEpisodePost() {
        mProgressDialog.setMessage("????????? ?????? ????????????.");
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

                            if (resultObject == null) {
                                Toast.makeText(LiteratureWriteActivity.this, "???????????? ????????? ??????????????????. ????????? ?????? ????????? ?????????.", Toast.LENGTH_LONG).show();
                                return;
                            }

                            if (resultObject.getString("RESULT").equals("SUCCESS")) {
                                Toast.makeText(LiteratureWriteActivity.this, "?????????????????????.", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Toast.makeText(LiteratureWriteActivity.this, "????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    private void requestEpisodeSubmit() {                                                                   // ?????? ??????(??????)
        AlertDialog.Builder builder = new AlertDialog.Builder(LiteratureWriteActivity.this);
        builder.setTitle("?????? ??????");
        builder.setMessage("????????? ???????????? ?????? ?????? ????????? ?????????. ???????????? ????????? ????????? ???????????? ????????? ??????????????? ???????????????.\n????????? ?????????????????????????");
        builder.setPositiveButton("???", (dialog, id) -> {
            mProgressDialog.setMessage("????????? ?????? ????????????.");
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

                                if (resultObject == null) {
                                    Toast.makeText(LiteratureWriteActivity.this, "???????????? ????????? ??????????????????. ????????? ?????? ????????? ?????????.", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                if (resultObject.getString("RESULT").equals("SUCCESS")) {
                                    Toast.makeText(LiteratureWriteActivity.this, "?????????????????????.", Toast.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    Toast.makeText(LiteratureWriteActivity.this, "????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }).start();
        });

        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void getCharacterData() {                                           // ???????????? ????????? ????????????
        mProgressDialog.setMessage("???????????? ?????? ???????????? ???????????? ????????????.");
        mProgressDialog.show();
        characterList.clear();
        characterList.add(null);
        nameList.add("??????");

        new Thread(new Runnable() {
            @Override
            public void run() {
                characterList.addAll(HttpClient.getCharacterDataWithEpisodeID(new OkHttpClient(), "" + workVO.getEpisodeList().get(nInteractionIndex).getnEpisodeID()));

                if (characterList == null) {
                    Toast.makeText(LiteratureWriteActivity.this, "???????????? ????????? ???????????? ????????????.", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (int i = 1; i < characterList.size(); i++) {
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

    private void getEpisodeChatData() {                                         // ?????? ????????? ????????????
        new Thread(new Runnable() {
            @Override
            public void run() {
                chattingList.clear();
                chattingList.addAll(HttpClient.getChatDataWithEpisodeID(new OkHttpClient(), "" + workVO.getEpisodeList().get(nInteractionIndex).getnEpisodeID()));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();

                        if (chattingList == null) {
                            Toast.makeText(LiteratureWriteActivity.this, "???????????? ????????? ???????????? ????????????.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (int i = 0; i < chattingList.size(); i++) {
                            ChatVO vo = chattingList.get(i);

                            if (vo.getType() == ChatVO.TYPE_DISTRACTOR) {
                                Intent intent = new Intent(LiteratureWriteActivity.this, InteractionWriteActivity.class);
                                InteractionWriteActivity.workVO = workVO;
                                intent.putExtra("TITLE", strTitle);
                                intent.putExtra("EPISODE_ID", nEpisodeID);
                                intent.putExtra("EPISODE_INDEX", nInteractionIndex);
                                intent.putExtra("EPISODE_ORDER", nEpisodeOrder);
                                startActivity(intent);
                                finish();
                                return;
                            }
                        }

                        if (chattingList.size() > 0) {
                            ChatVO vo = new ChatVO();
                            vo.setType(ChatVO.TYPE_EMPTY);
                            chattingList.add(0, vo);
                        }

                        aa.notifyDataSetChanged();

                        if (nAddIndex == -1 && nEditIndex == -1 && nDeleteIndex == -1)
                            chattingListView.setSelection(aa.getCount() - 1);
//                        else if(nAddIndex != -1) {
//                            chattingListView.setSelection(nAddIndex);
//                        } else if(nEditIndex != -1) {
//                            chattingListView.setSelection(nEditIndex);
//                        } else if(nDeleteIndex != -1) {
//                            if(nDeleteIndex > 0)
//                                nDeleteIndex -= 1;
//
//                            chattingListView.setSelection(nDeleteIndex);
//                        }

                        nAddIndex = -1;
                        nEditIndex = -1;
                        nDeleteIndex = -1;
                    }
                });
            }
        }).start();
    }

    // winhmoon
    public void onClickContentsAddBtn(View view) {
        imm.hideSoftInputFromWindow(inputTextView.getWindowToken(), 0);

        setSelectBottomLayout(TYPE_MEDIA);
    }

    private void hideBottomView() {
        bShowMenu = false;
        bottomSettingLayout.setVisibility(View.GONE);
//        contentsAddBtn.setImageResource(R.drawable.selectionplus);
    }

    private void resetCharacterLayout() {
        speakerAddLayout.removeAllViews();
        characterViewList.clear();

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < characterList.size(); i++) {
            final int nIndex = i;
            View view = inflater.inflate(R.layout.speaker_view, null);
            CharacterVO vo = characterList.get(i);

            ImageView faceView = view.findViewById(R.id.faceView);
            TextView nameView = view.findViewById(R.id.nameView);
            ImageView selectedView = view.findViewById(R.id.selectedView);

            int nImg = 0;

            int newi = i % 10;
            switch (newi) {
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

            if (i == 0) {
                // [S] winhmoon
                faceView.setImageResource(R.drawable.caracter_plus_botton);
                nameView.setText("?????? ??????");
                nameView.setTextColor(ContextCompat.getColor(LiteratureWriteActivity.this, R.color.colorBlack));
                selectedView.setVisibility(View.VISIBLE);
                // [E] winhmoon
            } else {
                if (vo.getImage() != null && !vo.getImage().equals("null")) {
                    Glide.with(this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(vo.getImage())
                            .placeholder(nImg)
                            .apply(new RequestOptions().circleCrop())
                            .into(faceView);
                } else if (vo.getStrImgFile() != null && !vo.getStrImgFile().equals("null")) {
                    String strUrl = vo.getStrImgFile();
//                    strUrl = strUrl.replaceAll(" ", "");

                    if (!strUrl.startsWith("http"))
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
            // [S] winhmoon
            faceLayout.setTag(nImg);

            if (i == 0) {
                faceLayout.setOnClickListener((v) -> {
                    CharacterRegActivity.nameList = new ArrayList<String>(nameList);
                    startActivityForResult(new Intent(LiteratureWriteActivity.this, CharacterRegActivity.class), 1010);
//                    CreateCharacterActivity.nameList = new ArrayList<String>(nameList);
//                    startActivityForResult(new Intent(LiteratureWriteActivity.this, CreateCharacterActivity.class), 1010);
                });
            } else {
                faceLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 1; i < characterViewList.size(); i++) {
                            View view = characterViewList.get(i);

                            TextView nameView = view.findViewById(R.id.nameView);
                            ImageView selectedView = view.findViewById(R.id.selectedView);

                            if (i == nIndex) {
                                nameView.setTextColor(ContextCompat.getColor(LiteratureWriteActivity.this, R.color.colorBlack));
                                selectedView.setVisibility(View.VISIBLE);
                            } else {
                                nameView.setTextColor(Color.parseColor("#e9e9e9"));
                                selectedView.setVisibility(View.INVISIBLE);
                            }
                        }

                        CharacterVO characterVO = characterList.get(nIndex);
                        if (characterVO.getImage() != null && !characterVO.getImage().equals("null")) {
                            Glide.with(LiteratureWriteActivity.this)
                                    .asBitmap() // some .jpeg files are actually gif
                                    .load(characterVO.getImage())
                                    .placeholder((int) v.getTag())
                                    .apply(new RequestOptions().circleCrop())
                                    .into(characterAddBtn);
                        } else if (characterVO.getStrImgFile() != null && !characterVO.getStrImgFile().equals("null")) {
                            String strUrl = characterVO.getStrImgFile();

                            if (!strUrl.startsWith("http"))
                                strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;

                            Glide.with(LiteratureWriteActivity.this)
                                    .asBitmap() // some .jpeg files are actually gif
                                    .placeholder((int) v.getTag())
                                    .load(strUrl)
                                    .apply(new RequestOptions().circleCrop())
                                    .into(characterAddBtn);
                        } else {
                            Glide.with(LiteratureWriteActivity.this)
                                    .asBitmap() // some .jpeg files
                                    .load((int) v.getTag())
                                    .apply(new RequestOptions().circleCrop())
                                    .into(characterAddBtn);
                        }

                        nSelectedCharacterIndex = nBeforeCharacterIndex = nIndex;

                    }
                });
            }
            // [E] winhmoon

            if (i > 0) {
                faceLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        CharacterRegActivity.nameList = new ArrayList<String>(nameList);
                        CharacterRegActivity.characterVO = characterList.get(nIndex);

                        Intent intent = new Intent(LiteratureWriteActivity.this, CharacterRegActivity.class);
                        intent.putExtra("INDEX", nIndex);
                        startActivityForResult(intent, 1011);

//                        CreateCharacterActivity.nameList = new ArrayList<String>(nameList);
//                        CreateCharacterActivity.characterVO = characterList.get(nIndex);
//
//                        Intent intent = new Intent(LiteratureWriteActivity.this, CreateCharacterActivity.class);
//                        intent.putExtra("INDEX", nIndex);
//                        startActivityForResult(intent, 1011);

                        return false;
                    }
                });
            }

            speakerAddLayout.addView(view);
            characterViewList.add(view);
        }

        if(nSelectedCharacterIndex == 0 && characterAddBtn != null) {
            characterAddBtn.setImageResource(R.drawable.ic_i_chracter);
        }
    }

    private void requestUpdateCharacter(final CharacterVO characterVO, final int nIndex) {                                  // ???????????? ??????
        mProgressDialog.setMessage("???????????? ??????????????????");
        mProgressDialog.show();

        try {
            String url = CommonUtils.strDefaultUrl + "PanAppCreateCharacter.jsp";
            RequestBody requestBody = null;

            File sourceFile = null;
            String strFilePath = null;

            MultipartBody.Builder builder = new MultipartBody.Builder();

            String strBalloon = characterVO.getStrBalloonColor();
            if (strBalloon == null)
                strBalloon = "null";

            builder.setType(MultipartBody.FORM)
                    .addFormDataPart("EPISODE_ID", "" + nEpisodeID)
                    .addFormDataPart("CHARACTER_ID", "" + characterVO.getnCharacterID())
                    .addFormDataPart("CHARACTER_NAME", characterVO.getName())
                    .addFormDataPart("BALLOON_COLOR", strBalloon)
                    .addFormDataPart("CHARACTER_DIRECTION", "" + characterVO.getDirection())
                    .addFormDataPart("BLACK_TEXT", characterVO.isbBlackText() == true ? "Y" : "N")
                    .addFormDataPart("BLACK_NAME", characterVO.isbBlackName() == true ? "Y" : "N");

            if (characterVO.getImage() != null) {
                strFilePath = CommonUtils.getRealPathFromURI(LiteratureWriteActivity.this, characterVO.getImage());
                sourceFile = new File(strFilePath);

                if (!sourceFile.exists()) {
                    mProgressDialog.dismiss();
                    Toast.makeText(LiteratureWriteActivity.this, "???????????? ?????????????????????.", Toast.LENGTH_LONG).show();

                    return;
                }

                String filename = strFilePath.substring(strFilePath.lastIndexOf("/") + 1);
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
                    Toast.makeText(LiteratureWriteActivity.this, "????????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
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

                                if (resultObject.getString("RESULT").equals("SUCCESS")) {
                                    nameList.set(nIndex, characterVO.getName());
                                    characterList.set(nIndex, characterVO);
//                                    resetCharacterLayout();
                                    getCharacterData();
                                } else {
                                    Toast.makeText(LiteratureWriteActivity.this, "????????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(LiteratureWriteActivity.this, "?????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void requestCreateCharacter(final CharacterVO characterVO) {                                        // ???????????? ??????
        mProgressDialog.setMessage("???????????? ??????????????????");
        mProgressDialog.show();

        try {
            String url = CommonUtils.strDefaultUrl + "PanAppCreateCharacter.jsp";
            RequestBody requestBody = null;

            File sourceFile = null;
            String strFilePath = null;

            MultipartBody.Builder builder = new MultipartBody.Builder();

            String strBalloon = characterVO.getStrBalloonColor();
            if (strBalloon == null)
                strBalloon = "null";

            builder.setType(MultipartBody.FORM)
                    .addFormDataPart("EPISODE_ID", "" + nEpisodeID)
                    .addFormDataPart("CHARACTER_NAME", characterVO.getName())
                    .addFormDataPart("BALLOON_COLOR", strBalloon)
                    .addFormDataPart("CHARACTER_DIRECTION", "" + characterVO.getDirection())
                    .addFormDataPart("BLACK_TEXT", characterVO.isbBlackText() == true ? "Y" : "N")
                    .addFormDataPart("BLACK_NAME", characterVO.isbBlackName() == true ? "Y" : "N");

            if (characterVO.getImage() != null) {
                strFilePath = CommonUtils.getRealPathFromURI(LiteratureWriteActivity.this, characterVO.getImage());
                sourceFile = new File(strFilePath);

                if (!sourceFile.exists()) {
                    mProgressDialog.dismiss();
                    Toast.makeText(LiteratureWriteActivity.this, "???????????? ?????????????????????.", Toast.LENGTH_LONG).show();

                    return;
                }

                String filename = strFilePath.substring(strFilePath.lastIndexOf("/") + 1);
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
                    Toast.makeText(LiteratureWriteActivity.this, "????????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
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

                                if (resultObject.getString("RESULT").equals("SUCCESS")) {
                                    String strCharacterID = resultObject.getString("CHARACTER_ID");
                                    characterVO.setnCharacterID(Integer.valueOf(strCharacterID));

                                    nameList.add(characterVO.getName());
                                    characterList.add(characterVO);
                                    resetCharacterLayout();
                                } else {
                                    Toast.makeText(LiteratureWriteActivity.this, "????????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(LiteratureWriteActivity.this, "?????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {                                 // ????????? ?????? ?????? ???????????? ?????????
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 9000) {                                           // ?????? ?????? ??????
                sendEpisodePost();
            } else if (requestCode == 2000) {                                   // ????????? ????????? ?????????
                Intent intent = new Intent(LiteratureWriteActivity.this, AlbumSelectActivity.class);
                intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 1);
                startActivityForResult(intent, 2100);
            } else if (requestCode == 2100) {
                ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
                if (images.size() > 0) {
                    Image image = images.get(0);
                    String strImgPath = image.path;
                }
            } else if (requestCode == 1010) {                                  //   ????????? ??????
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
            } else if (requestCode == 1011) {                            // ????????? ??????/??????
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
            } else if (requestCode == 1000) {                            // ?????? ??? ??????
                isEdit = data.getBooleanExtra("EDIT", false);

                if (isEdit)
                    nEditIndex = data.getIntExtra("ORDER", -1);

                bColorPicker = true;
            } else if (requestCode == 1020 || requestCode == 1025) {                            // ????????? ??????
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

                    if (nSelectedCharacterIndex > 0) {                   // ????????????
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
                chatVO.setContents(strInteraction1 + "???" + strInteraction2);

                requestUploadMessage(chatVO, bEdit);
            } else if (requestCode == 1030 || requestCode == 1035) {                           // ?????? ????????????
                Uri fileUri = data.getData();

                String strFilePath = CommonUtils.getRealPathFromURI(LiteratureWriteActivity.this, fileUri);
                File sourceFile = new File(strFilePath);

                if (!sourceFile.exists()) {
                    mProgressDialog.dismiss();
                    Toast.makeText(LiteratureWriteActivity.this, "?????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();

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
            } else if (requestCode == 1040) {                    // ???????????? ????????????
                CommonUtils.showProgressDialog(LiteratureWriteActivity.this, "?????? ????????? ?????? ????????????. ????????? ????????? ?????????.");
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

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CommonUtils.hideProgressDialog();
                                    Toast.makeText(LiteratureWriteActivity.this, "????????? ????????? ?????? ????????? ????????????.", Toast.LENGTH_LONG).show();
                                }
                            });
                        } catch (InvalidFormatException e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CommonUtils.hideProgressDialog();
                                    Toast.makeText(LiteratureWriteActivity.this, "????????? ????????? ?????? ????????? ????????????.", Toast.LENGTH_LONG).show();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CommonUtils.hideProgressDialog();
                                    Toast.makeText(LiteratureWriteActivity.this, "????????? ????????? ?????? ????????? ????????????.", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }).start();
            } else if (requestCode == 1050) {                                       // ?????? ??????/???????????? ?????? ??????
                String strContents = data.getStringExtra("EDITED_TEXT");
                int nOrder = data.getIntExtra("ORDER", -1);
                nEditIndex = nOrder;
                ChatVO chatVO = chattingList.get(nOrder);
                chatVO.setContents(strContents);

                requestUploadMessage(chatVO, true);
            } else if (requestCode == 1100) {                    // ?????? ??????
                strTitle = data.getStringExtra("TITLE");
                titleView.setText(strTitle);
            } else if (requestCode == InteractionWriteActivity.EXCEL) {              // ???????????? ??????
                CommonUtils.showProgressDialog(LiteratureWriteActivity.this, "??????????????? ???????????? ????????????. ????????? ??????????????????.");

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
                                    Toast.makeText(LiteratureWriteActivity.this, "???????????? ????????? ??????????????????.", Toast.LENGTH_LONG).show();
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

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.hideProgressDialog();
                            Toast.makeText(LiteratureWriteActivity.this, "????????? ????????? ?????? ????????? ????????????.", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (InvalidFormatException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.hideProgressDialog();
                            Toast.makeText(LiteratureWriteActivity.this, "????????? ????????? ?????? ????????? ????????????.", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.hideProgressDialog();
                            Toast.makeText(LiteratureWriteActivity.this, "????????? ????????? ?????? ????????? ????????????.", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getCharacterData();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;

        switch (v.getId()) {
            case R.id.bgSettingView:
                TedPermission.with(LiteratureWriteActivity.this)
                        .setPermissionListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                startActivityForResult(new Intent(LiteratureWriteActivity.this, BGImageSelectPopup.class), 1000);
                            }

                            @Override
                            public void onPermissionDenied(List<String> deniedPermissions) {
                                Toast.makeText(LiteratureWriteActivity.this, "????????? ?????????????????? ?????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .check();
                break;
            case R.id.imgSettingView:
//                if(nSelectedCharacterIndex == 0) {
//                    Toast.makeText(LiteratureWriteActivity.this, "????????? ???????????? ????????? ??? ????????????.", Toast.LENGTH_LONG).show();
//                    return;
//                }

                TedPermission.with(LiteratureWriteActivity.this)
                        .setPermissionListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                Intent intent = new Intent(LiteratureWriteActivity.this, MediaSelectPopup.class);
                                if (nSelectedCharacterIndex == 0)
                                    intent.putExtra("TYPE", TYPE_CONTENTS_IMG_NAR.ordinal());
                                else
                                    intent.putExtra("TYPE", TYPE_CONTENTS_IMG.ordinal());
                                intent.putExtra("ORDER", nAddIndex);
                                if (nEditIndex > -1)
                                    intent.putExtra("EDIT", true);
                                startActivity(intent);
                            }

                            @Override
                            public void onPermissionDenied(List<String> deniedPermissions) {
                                Toast.makeText(LiteratureWriteActivity.this, "????????? ?????????????????? ?????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .check();
                break;

            case R.id.videoSettingView:
                if (nSelectedCharacterIndex == 0) {
                    Toast.makeText(LiteratureWriteActivity.this, "????????? ???????????? ????????? ??? ????????????.", Toast.LENGTH_LONG).show();
                    return;
                }

                TedPermission.with(LiteratureWriteActivity.this)
                        .setPermissionListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                Intent intent = new Intent(LiteratureWriteActivity.this, PhotoPickerActivity.class);
                                intent.putExtra("TYPE", TYPE_VIDEO.ordinal());
                                if (nEditIndex > -1)
                                    intent.putExtra("EDIT", true);
                                intent.putExtra("ORDER", nAddIndex);
                                startActivity(intent);
                            }

                            @Override
                            public void onPermissionDenied(List<String> deniedPermissions) {
                                Toast.makeText(LiteratureWriteActivity.this, "????????? ?????????????????? ?????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .check();

                break;

            case R.id.distractorView:
                if (workVO.getEpisodeList().size() > nInteractionIndex + 1) {
                    Toast.makeText(this, "????????? ?????? ????????? ???????????? ?????? ????????? ???????????????.", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
                intent = new Intent(LiteratureWriteActivity.this, DistractorPopup.class);
                startActivityForResult(intent, 1020);
                break;

            case R.id.soundSettingView:
                if (nSelectedCharacterIndex == 0) {
                    Toast.makeText(LiteratureWriteActivity.this, "????????? ????????? ????????? ??? ????????????.", Toast.LENGTH_LONG).show();
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
                                Toast.makeText(LiteratureWriteActivity.this, "????????? ?????????????????? ?????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .check();

                break;
        }
    }

    @Override
    public void onColorSelected(int dialogId, int color) {                                      // ?????? ?????? ?????? - ?????? ??? ?????? ????????? ???, ?????? ????????? ??????????????? ?????????
        nBgColor = color;
        bgColor = String.format("#%06X", (0xFFFFFF & nBgColor));
        bgView.setBackgroundColor(nBgColor);

        ChatVO chatVO = new ChatVO();

        chatVO.setType(ChatVO.TYPE_CHANGE_BG_COLOR);

        if (nEditIndex > -1) {
            chatVO = chattingList.get(nEditIndex);
        } else {
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
        }

        chatVO.setStrContentsFile(bgColor);

        if (nEditIndex > -1)
            requestUploadMessage(chatVO, true);
        else
            requestUploadMessage(chatVO, false);
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }

    public class CChattingArrayAdapter extends ArrayAdapter<Object> {
        private int mCellLayout;
        private LayoutInflater mLiInflater;

        CChattingArrayAdapter(Context context, int layout, List titles) {
            super(context, layout, titles);
            mCellLayout = layout;
            mLiInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (position >= chattingList.size()) {
                convertView = mLiInflater.inflate(R.layout.empty_chatting_row, parent, false);
                return convertView;
            }

            ChatVO chatVO = chattingList.get(position);
            CharacterVO characterVO = chatVO.getCharacterVO();

            int nType = chatVO.getType();
            int nWidth = (int) ((float) chattingListView.getWidth() * (float) (1.7f / 3.0f));
            int nDirection = 0;

            if (nType == ChatVO.TYPE_TEXT) {                                                                     // type ?????? ??? ???????????? row ?????? - ???????????? ?????? ????????? ??????. ???????????????.   ???????????? ?????? ?????????. ?????? ????????? ?????? ?????????.
                nDirection = characterVO.getDirection();

                if (nDirection == 0)             // left                                                                     // ?????? ???????????? round_squre_stroke_gray_bg ??????
                    convertView = mLiInflater.inflate(R.layout.left_chatting_row, parent, false);
                else
                    convertView = mLiInflater.inflate(R.layout.right_chatting_row, parent, false);

                TextView nameView = convertView.findViewById(R.id.nameView);                                                // ?????? ??????
                nameView.setText(characterVO.getName());

                if (characterVO.isbBlackName())                                                                              // ????????? ?????? ??????????????? ???????????? ??????
                    nameView.setTextColor(Color.parseColor("#000000"));
                else
                    nameView.setTextColor(Color.parseColor("#ffffff"));

                TextView contentsTextView = convertView.findViewById(R.id.contentsTextView);                                // ?????? ?????? ??????
                contentsTextView.setText(chatVO.getContents());

                if (characterVO.isbBlackText()) {                                                                            // ?????? ??? ?????????/?????? ??????
                    contentsTextView.setTextColor(getResources().getColor(R.color.colorBlack));
                } else {
                    contentsTextView.setTextColor(getResources().getColor(R.color.colorWhite));
                }

                ImageView imageContentsView = convertView.findViewById(R.id.imageContentsView);                             // ????????? ???????????? ??????

                if (chatVO.getContentsUri() != null) {
                    Glide.with(LiteratureWriteActivity.this)
                            .asBitmap()
                            .load(chatVO.getContentsUri())
                            .apply(new RequestOptions().override(800, 800))
                            .into(imageContentsView);
                } else if (chatVO.getStrContentsFile() != null) {
                    String strUrl = characterVO.getStrImgFile();
//                    strUrl = strUrl.replaceAll(" ", "");

                    if (!strUrl.startsWith("http"))
                        strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;

                    Glide.with(LiteratureWriteActivity.this)
                            .asBitmap()
                            .load(strUrl)
                            .apply(new RequestOptions().override(800, 800))
                            .into(imageContentsView);
                }
            } else if (nType == ChatVO.TYPE_SOUND) {
                nDirection = characterVO.getDirection();

                if (nDirection == 0)             // left
                    convertView = mLiInflater.inflate(R.layout.left_audio_row, parent, false);
                else
                    convertView = mLiInflater.inflate(R.layout.right_audio_row, parent, false);

                TextView nameView = convertView.findViewById(R.id.nameView);
                nameView.setText(characterVO.getName());
                if (characterVO.isbBlackName())
                    nameView.setTextColor(Color.parseColor("#000000"));
                else
                    nameView.setTextColor(Color.parseColor("#ffffff"));

                final ProgressBar pb = convertView.findViewById(R.id.progressBar);

                RelativeLayout playBtnlayout = convertView.findViewById(R.id.playBtnlayout);
                ImageView playBtn = convertView.findViewById(R.id.playBtn);

                if (nPlayingIndex == position) {         // ?????? ?????????????????????
                    if (timer != null) {
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
                            if (mediaPlayer != null && mediaPlayer.isPlaying())
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
                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            if (nPlayingIndex == position) {
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
                        } else if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                            if (nPlayingIndex == position) {
                                mediaPlayer.start();
                                playBtn.setImageResource(R.drawable.pause);

                                timer = new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        if (mediaPlayer != null && mediaPlayer.isPlaying())
                                            pb.setProgress(mediaPlayer.getCurrentPosition());
                                        else {
                                            if (timer != null) {
                                                timer.cancel();
                                                timer = null;
                                            }
                                        }
                                    }
                                }, 0, 1000);

                                return;
                            }
                        }

                        CommonUtils.showProgressDialog(LiteratureWriteActivity.this, "????????? ??????????????????.");

                        String strUrl = chatVO.getStrContentsFile();
//                        strUrl = strUrl.replaceAll(" ", "");

                        if (!strUrl.startsWith("http")) {
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
                                            if (mediaPlayer != null && mediaPlayer.isPlaying())
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
                                    if (timer != null) {
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
            } else if (nType == ChatVO.TYPE_IMAGE_NAR) {
                convertView = mLiInflater.inflate(R.layout.narration_image_row, parent, false);

                ImageView imageContentsView = convertView.findViewById(R.id.imageContentsView);
                imageContentsView.setClipToOutline(true);

                if (chatVO.getContentsUri() != null) {
                    Glide.with(LiteratureWriteActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(chatVO.getContentsUri())
                            .apply(new RequestOptions().override(nWidth, nWidth))
                            .into(imageContentsView);
                } else if (chatVO.getStrContentsFile() != null) {
                    String strUrl = chatVO.getStrContentsFile();

                    if (!strUrl.startsWith("http"))
                        strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;

                    Glide.with(LiteratureWriteActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(strUrl)
                            .apply(new RequestOptions().override(nWidth, nWidth))
                            .into(imageContentsView);
                }
            } else if (nType == ChatVO.TYPE_IMAGE) {
                nDirection = characterVO.getDirection();

                if (nDirection == 0)             // left
                    convertView = mLiInflater.inflate(R.layout.left_image_row, parent, false);
                else
                    convertView = mLiInflater.inflate(R.layout.right_image_row, parent, false);

                TextView nameView = convertView.findViewById(R.id.nameView);
                nameView.setText(characterVO.getName());
                if (characterVO.isbBlackName())
                    nameView.setTextColor(Color.parseColor("#000000"));
                else
                    nameView.setTextColor(Color.parseColor("#ffffff"));

                ImageView imageContentsView = convertView.findViewById(R.id.imageContentsView);
                imageContentsView.setClipToOutline(true);

                if (chatVO.getContentsUri() != null) {
                    Glide.with(LiteratureWriteActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(chatVO.getContentsUri())
                            .apply(new RequestOptions().override(nWidth, nWidth))
                            .into(imageContentsView);
                } else if (chatVO.getStrContentsFile() != null) {
                    String strUrl = chatVO.getStrContentsFile();
//                    strUrl = strUrl.replaceAll(" ", "");

                    if (!strUrl.startsWith("http"))
                        strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;

                    Glide.with(LiteratureWriteActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(strUrl)
                            .apply(new RequestOptions().override(nWidth, nWidth))
                            .into(imageContentsView);
                }

                imageContentsView.setClipToOutline(true);
            } else if (nType == ChatVO.TYPE_NARRATION) {
                convertView = mLiInflater.inflate(R.layout.narration_chatting_row, parent, false);

                TextView contentsTextView = convertView.findViewById(R.id.contentsTextView);
                contentsTextView.setText(chatVO.getContents());
            } else if (nType == ChatVO.TYPE_VIDEO) {
                nDirection = characterVO.getDirection();

                if (nDirection == 0)             // left
                    convertView = mLiInflater.inflate(R.layout.left_video_row, parent, false);
                else
                    convertView = mLiInflater.inflate(R.layout.right_video_row, parent, false);

                TextView nameView = convertView.findViewById(R.id.nameView);
                nameView.setText(characterVO.getName());
                if (characterVO.isbBlackName())
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
                        if (!strUrl.startsWith("http"))
                            strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;

                        Intent intent = new Intent(LiteratureWriteActivity.this, VideoPlayerActivity.class);
                        intent.putExtra("VIDEO_URL", strUrl);
                        startActivity(intent);
                    }
                });

                if (chatVO.getContentsUri() != null) {
                    if (thumbBitmapList.containsKey(chatVO.getContentsUri().toString())) {
//                        imageContentsView.setImageBitmap(thumbBitmapList.get(chatVO.getContentsUri().toString()));
                        Glide.with(LiteratureWriteActivity.this)
                                .load(thumbBitmapList.get(chatVO.getContentsUri().toString()))
                                .apply(new RequestOptions().override(nWidth - 50, nWidth - 50))
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
                                                .apply(new RequestOptions().override(nWidth - 50, nWidth - 50))
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
                                    .apply(new RequestOptions().override(nWidth - 50, nWidth - 50))
                                    .into(imageContentsView);
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                } else if (chatVO.getStrContentsFile() != null) {
                    String strUrl = chatVO.getStrContentsFile();

                    if (!strUrl.startsWith("http"))
                        strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;

                    if (thumbBitmapList.containsKey(strUrl)) {
//                        imageContentsView.setImageBitmap(thumbBitmapList.get(strUrl));
                        Glide.with(LiteratureWriteActivity.this)
                                .load(thumbBitmapList.get(strUrl))
                                .apply(new RequestOptions().override(nWidth - 50, nWidth - 50))
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
                                                .apply(new RequestOptions().override(nWidth - 50, nWidth - 50))
                                                .into(imageContentsView);
                                    }
                                });
                            }
                        }).start();
                    }
                }
            } else if (nType == ChatVO.TYPE_DISTRACTOR) {
                convertView = mLiInflater.inflate(R.layout.narration_chatting_row, parent, false);
                String strContents = chatVO.getContents();
                strContents = "?????? ?????? : " + strContents.substring(0, strContents.indexOf("???")) + ", " + strContents.substring(strContents.indexOf("???") + 1);
                TextView contentsTextView = convertView.findViewById(R.id.contentsTextView);
                contentsTextView.setText(strContents);
            } else if (nType == ChatVO.TYPE_CHANGE_BG) {
                convertView = mLiInflater.inflate(R.layout.narration_chatting_row, parent, false);
                TextView contentsTextView = convertView.findViewById(R.id.contentsTextView);
                contentsTextView.setText("?????? ????????? ??????");

                if (chatVO.getContentsUri() != null) {
                    Glide.with(LiteratureWriteActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(chatVO.getContentsUri())
                            .transition(BitmapTransitionOptions.withCrossFade(300))
                            .into(bgView);
                } else if (chatVO.getStrContentsFile() != null) {
                    String strUrl = chatVO.getStrContentsFile();
//                    strUrl = strUrl.replaceAll(" ", "");

                    if (!strUrl.startsWith("http"))
                        strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;

                    Glide.with(LiteratureWriteActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(strUrl)
                            .transition(BitmapTransitionOptions.withCrossFade(300))
                            .into(bgView);
                }
            } else if (nType == ChatVO.TYPE_CHANGE_BG_COLOR) {
                convertView = mLiInflater.inflate(R.layout.narration_chatting_row, parent, false);

                TextView contentsTextView = convertView.findViewById(R.id.contentsTextView);
                contentsTextView.setText("?????? ??? ??????");

                String strColor = chatVO.getStrContentsFile();
                int nColor = Color.parseColor(strColor);
                bgView.setBackgroundColor(nColor);
            } else if (nType == ChatVO.TYPE_EMPTY) {
                convertView = mLiInflater.inflate(R.layout.empty_chatting_row, parent, false);
            }

            ImageView deleteBtn = convertView.findViewById(R.id.deleteBtn);
            if (deleteBtn != null) {
                deleteBtn.setClipToOutline(true);
                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                    chattingList.remove(position);
                        requestDeleteMessage(position);
                    }
                });
            }

            if (characterVO != null && (nType == ChatVO.TYPE_TEXT || nType == ChatVO.TYPE_SOUND)) {
                RelativeLayout contentsLayout = convertView.findViewById(R.id.contentsLayout);
                if (contentsLayout != null) {
                    String strBalloonColor = characterVO.getStrBalloonColor();

                    if (strBalloonColor != null && !strBalloonColor.equals("null") && strBalloonColor.length() > 0) {
                        if (contentsLayout != null && contentsLayout.getBackground() != null) {
                            int nColor = Color.parseColor(strBalloonColor);
                            PorterDuffColorFilter greyFilter = new PorterDuffColorFilter(nColor, PorterDuff.Mode.MULTIPLY);
                            contentsLayout.getBackground().setColorFilter(greyFilter);
                        }
                    } else {
                        if (contentsLayout != null && contentsLayout.getBackground() != null)
                            contentsLayout.getBackground().setColorFilter(null);
                    }
                }
            }

            final RelativeLayout addBtn = convertView.findViewById(R.id.addBtn);
            final ImageView leftLine = convertView.findViewById(R.id.leftLine);
            final ImageView rightLine = convertView.findViewById(R.id.rightLine);
            final TextView txtView = convertView.findViewById(R.id.txtView);

            if (position == chattingList.size() - 1)
                addBtn.setVisibility(View.INVISIBLE);
            else
                addBtn.setVisibility(View.VISIBLE);

            ImageView faceView = convertView.findViewById(R.id.faceView);
            if (faceView != null) {
                int nPlaceHolder = 0;
                int faceIndex = getCharacterIndex(characterVO) % 10;
                switch (faceIndex) {
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

                if (characterVO.getImage() != null && !characterVO.getImage().equals("null")) {
                    Glide.with(LiteratureWriteActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(characterVO.getImage())
                            .placeholder(nPlaceHolder)
                            .apply(new RequestOptions().circleCrop())
                            .into(faceView);
                } else if (characterVO.getStrImgFile() != null && !characterVO.getStrImgFile().equals("null")) {
                    String strUrl = characterVO.getStrImgFile();
//                    strUrl = strUrl.replaceAll(" ", "");

                    if (!strUrl.startsWith("http"))
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

            if (nAddIndex == position) {
//                addBtn.setBackgroundResource(R.drawable.common_selected_rounded_btn_bg);
                txtView.setText("??????");
                leftLine.setBackgroundColor(Color.parseColor("#ff0000"));
                rightLine.setBackgroundColor(Color.parseColor("#ff0000"));
                txtView.setTextColor(Color.parseColor("#ff0000"));
            } else {
//                addBtn.setBackgroundResource(R.drawable.common_gray_rounded_btn_bg);
                txtView.setText("????????? ??????");
                txtView.setTextColor(Color.parseColor("#000000"));
                leftLine.setBackgroundColor(Color.parseColor("#000000"));
                rightLine.setBackgroundColor(Color.parseColor("#000000"));
            }

            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (nAddIndex == position) {
                        nAddIndex = -1;
                        aa.notifyDataSetChanged();
                    } else {
                        Toast.makeText(LiteratureWriteActivity.this, "????????? ??????????????? ???????????? ????????? ?????? ?????????.\n?????????????????? ????????? ?????? ??? ??? ???????????????.", Toast.LENGTH_LONG).show();
                        nAddIndex = position;
                        aa.notifyDataSetChanged();
                    }
                }
            });

            TextView commentCountView = convertView.findViewById(R.id.commentCountView);
            if (commentCountView != null) {
                commentCountView.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }
    }

    private int getCharacterIndex(CharacterVO vo) {                                                 // ????????? ???????????? ????????? ??????????????? ???????????? ??????
        int characterID = vo.getnCharacterID();

        for (int i = 1; i < characterList.size(); i++) {
            CharacterVO characterVO = characterList.get(i);
            if (characterVO == null)
                return 1;
            if (characterVO.getnCharacterID() == characterID)
                return i;
        }

        return 1;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {                                         // ?????? ????????? ????????? ??????
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