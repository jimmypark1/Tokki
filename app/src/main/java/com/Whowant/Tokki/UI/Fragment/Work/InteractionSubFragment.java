package com.Whowant.Tokki.UI.Fragment.Work;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Photopicker.SeesoGalleryInteractionActivity;
import com.Whowant.Tokki.UI.Activity.Photopicker.TokkiGalleryActivity;
import com.Whowant.Tokki.UI.Activity.Work.CreateCharacterActivity;
import com.Whowant.Tokki.UI.Activity.Media.VideoPlayerActivity;
import com.Whowant.Tokki.UI.Activity.Photopicker.InteractionPhotoPickerActivity;
import com.Whowant.Tokki.UI.Activity.Photopicker.PhotoPickerActivity;
import com.Whowant.Tokki.UI.Activity.Work.InteractionWriteActivity;
import com.Whowant.Tokki.UI.Activity.Work.LiteratureWriteActivity;
import com.Whowant.Tokki.UI.Activity.Work.ViewerActivity;
import com.Whowant.Tokki.UI.Popup.BGImageSelectPopup;
import com.Whowant.Tokki.UI.Popup.DistractorPopup;
import com.Whowant.Tokki.UI.Popup.InteractionBGSelectPopup;
import com.Whowant.Tokki.UI.Popup.InteractionMediaSelectPopup;
import com.Whowant.Tokki.UI.Popup.MediaSelectPopup;
import com.Whowant.Tokki.UI.Popup.SlangPopup;
import com.Whowant.Tokki.UI.Popup.TextEditPopup;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.ExcelReader;
import com.Whowant.Tokki.Utils.SoftKeyboard;
import com.Whowant.Tokki.VO.CharacterVO;
import com.Whowant.Tokki.VO.ChatVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
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

import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_CONTENTS_IMG;
import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_VIDEO;

public class InteractionSubFragment extends Fragment implements View.OnClickListener {                                  // 작성창 중 '분기' 가 설정 되어있을때 화면을 두개로 나눔. 그 중에 오른쪽 화면으로 기본적으로 작성창과 동일
    private ArrayList<CharacterVO> characterList;
    private ArrayList<ChatVO> chattingList;

    private LinearLayout speakerAddLayout;
    private ArrayList<String> nameList;
    private ArrayList<View> characterViewList;
    private int nSelectedCharacterIndex = 0;                // 0 = 나레이
    private boolean bShowMenu = false;
    private ConstraintLayout bottomSettingLayout;
    private EditText inputTextView;
    private InputMethodManager imm;
    private HashMap<String, Bitmap> thumbBitmapList = new HashMap<>();
    private float fX, fY;

    private ImageButton contentsAddBtn;

    private ListView chattingListView;
    private CChattingArrayAdapter aa;

    private LinearLayout bgSettingView;
    private LinearLayout imgSettingView;
    private LinearLayout videoSettingView;
    private LinearLayout distractorView;
    private LinearLayout soundSettingView;

    private CoordinatorLayout dimLayerLayout;
    private ImageView addedImageView;

    private int    nBgColor;
    private String bgColor;
    private boolean bColorPicker = false;

    private ImageView bgView;

    private String strTitle;
    private int nEpisodeID;
    private int nEpisodeIndex;
    private int nAddIndex = -1;          // 사이에 추가하기 넘버
    private int nEditIndex = -1;            // 수정할때 넘버
    private int nDeleteIndex = -1;

    private ArrayList<String> newFileList;
    private boolean isEdit = false;

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Timer timer;

    private InteractionWriteActivity interactionWriteActivity;

    private boolean bEdit = false;
    private int     nOrder = -1;
    private int     nInteracionOrder = -1;
    private boolean bVideo = false;
    private int nPlayingIndex = -1;
    private ImageView oldPlayBtn;
    private ProgressBar oldPB;
    private Button sendBtn;
    private ViewGroup viewGroup;
    private SoftKeyboard softKeyboard;

    public static Fragment newInstance() {
        InteractionSubFragment fragment = new InteractionSubFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.interaction_fragment_layout, container, false);

        newFileList = new ArrayList<>();
        interactionWriteActivity = (InteractionWriteActivity)getActivity();
        strTitle = interactionWriteActivity.strTitle;
        nEpisodeID = interactionWriteActivity.nEpisodeID;
        nEpisodeIndex = interactionWriteActivity.nEpisodeIndex;

        characterList = new ArrayList<>();
        nameList = new ArrayList<>();
        characterViewList = new ArrayList<>();

        characterList.add(null);
        nameList.add("지문");

        dimLayerLayout = inflaterView.findViewById(R.id.dimLayerLayout);
        LinearLayout speakerAddView = inflaterView.findViewById(R.id.speakerAddView);
        speakerAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateCharacterActivity.nameList = new ArrayList<String>(nameList);
                startActivityForResult(new Intent(getActivity(), CreateCharacterActivity.class), 1010);
            }
        });

        nBgColor = ContextCompat.getColor(getActivity(), R.color.colorDefaultBG);
        bgColor = String.format("#%06X", (0xFFFFFF & nBgColor));

        imm = (InputMethodManager)interactionWriteActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        speakerAddLayout = inflaterView.findViewById(R.id.speakerAddLayout);
        bottomSettingLayout = inflaterView.findViewById(R.id.bottomSettingLayout);
        inputTextView = inflaterView.findViewById(R.id.inputTextView);
        contentsAddBtn = inflaterView.findViewById(R.id.contentsAddBtn);
        chattingListView = inflaterView.findViewById(R.id.chattingListView);
        bgView = inflaterView.findViewById(R.id.bgView);

        bgSettingView = inflaterView.findViewById(R.id.bgSettingView);
        imgSettingView = inflaterView.findViewById(R.id.imgSettingView);
        videoSettingView = inflaterView.findViewById(R.id.videoSettingView);
        distractorView = inflaterView.findViewById(R.id.distractorView);
        soundSettingView = inflaterView.findViewById(R.id.soundSettingView);

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

        contentsAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });

        resetCharacterLayout();

        chattingList = new ArrayList<>();
        aa = new CChattingArrayAdapter(interactionWriteActivity, R.layout.right_chatting_row, chattingList);
        chattingListView.setAdapter(aa);

        chattingListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(nInteracionOrder > -1 && position == nInteracionOrder+1) {
                    position -= 1;
                }

                ChatVO vo = chattingList.get(position);
                int nType = vo.getType();

                if(nType == ChatVO.TYPE_TEXT || nType == ChatVO.TYPE_NARRATION) {
                    nEditIndex = position;
                    Intent intent = new Intent(getActivity(), TextEditPopup.class);
                    intent.putExtra("TEXT", vo.getContents());
                    intent.putExtra("ORDER", position);
                    startActivityForResult(intent, 1050);
                } else if(nType == ChatVO.TYPE_IMAGE || nType == ChatVO.TYPE_VIDEO) {
                    int finalPosition = position;
                    TedPermission.with(getActivity())
                            .setPermissionListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted() {
                                    Intent intent = new Intent(getActivity(), MediaSelectPopup.class);
                                    if(nType == ChatVO.TYPE_IMAGE) {
                                        intent.putExtra("TYPE", TYPE_CONTENTS_IMG.ordinal());
                                    } else {
                                        intent.putExtra("TYPE", TYPE_VIDEO.ordinal());
                                    }

                                    intent.putExtra("EDIT", true);
                                    intent.putExtra("ORDER", finalPosition);
                                    startActivityForResult(intent, 1000);
                                }

                                @Override
                                public void onPermissionDenied(List<String> deniedPermissions) {
                                    Toast.makeText(getActivity(), "권한을 허용해주셔야 사진 설정이 가능합니다.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                            .check();
                } else if(nType == ChatVO.TYPE_SOUND) {
                    int finalPosition1 = position;
                    TedPermission.with(getActivity())
                            .setPermissionListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted() {
                                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                    nEditIndex = finalPosition1;
                                    intent.setType("audio/*");
                                    startActivityForResult(Intent.createChooser(intent, "Music File"), 1035);
                                }

                                @Override
                                public void onPermissionDenied(List<String> deniedPermissions) {
                                    Toast.makeText(getActivity(), "권한을 허용해주셔야 사진 설정이 가능합니다.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                            .check();
                } else if(nType == ChatVO.TYPE_CHANGE_BG || nType == ChatVO.TYPE_CHANGE_BG_COLOR) {
                    int finalPosition2 = position;
                    TedPermission.with(getActivity())
                            .setPermissionListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted() {
                                    Intent intent = new Intent(getActivity(), BGImageSelectPopup.class);
                                    intent.putExtra("EDIT", true);
                                    intent.putExtra("ORDER", finalPosition2);
                                    startActivityForResult(intent, 1000);
                                }

                                @Override
                                public void onPermissionDenied(List<String> deniedPermissions) {
                                    Toast.makeText(getActivity(), "권한을 허용해주셔야 사진 설정이 가능합니다.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                            .check();
                } else if(nType == ChatVO.TYPE_DISTRACTOR) {
                    int finalPosition3 = position;
                    TedPermission.with(getActivity())
                            .setPermissionListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted() {
                                    Intent intent = new Intent(getActivity(), DistractorPopup.class);
                                    intent.putExtra("EDIT", true);
                                    intent.putExtra("ORDER", finalPosition3);
                                    intent.putExtra("TEXT", vo.getContents());
                                    startActivityForResult(intent, 1025);
                                }

                                @Override
                                public void onPermissionDenied(List<String> deniedPermissions) {
                                    Toast.makeText(getActivity(), "권한을 허용해주셔야 사진 설정이 가능합니다.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                            .check();
                }

                return false;
            }
        });

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

        bgSettingView.setOnClickListener(bottomBtnClickListener);
        imgSettingView.setOnClickListener(bottomBtnClickListener);
        videoSettingView.setOnClickListener(bottomBtnClickListener);
        distractorView.setOnClickListener(bottomBtnClickListener);
        soundSettingView.setOnClickListener(bottomBtnClickListener);

        if(nEpisodeID > -1) {
            getCharacterData(true);
        }

        sendBtn = inflaterView.findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strContents = inputTextView.getText().toString();

                if(strContents.length() == 0) {
                    Toast.makeText(getActivity(), "내용을 입력하세요.", Toast.LENGTH_LONG).show();
                    return;
                }

                CommonUtils.showProgressDialog(getActivity(), "서버와 통신중입니다. 잠시만 기다려주세요.");

                String strFobiddenWords = CommonUtils.checkForbiddenWords(strContents);
                if(strFobiddenWords.length() > 0) {
                    CommonUtils.hideProgressDialog();
                    Intent intent = new Intent(getActivity(), SlangPopup.class);
                    intent.putExtra("SLANG", strFobiddenWords);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.cross_fade_in, R.anim.cross_fade_out);
                    return;
                }

                CommonUtils.hideProgressDialog();

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
                    if(nAddIndex > -1) {            // 사이에 추가
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

        int nKeyboard = getResources().getConfiguration().keyboard;
        if(nKeyboard != 2) {
            setKeyboardEvent();
        }

        return inflaterView;
    }

    public void setKeyboardEvent() {
        hideBottomView();
        viewGroup = (ViewGroup) ((ViewGroup)getActivity().findViewById(android.R.id.content)).getChildAt(0);
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

    public void removeKeyboardEvent() {
        softKeyboard.setSoftKeyboardCallback(null);
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
//        actionBar.setDisplayShowCustomEnabled(true);
//
//        LayoutInflater viewinflater = (LayoutInflater)getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
//        View customActionbar = viewinflater.inflate(R.round_squre_stroke_gray_bg.custom_actionbar, null);
//
//        actionBar.setCustomView(customActionbar);
//        actionBar.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
//        actionBar.setDisplayShowHomeEnabled(false);
//
//        ImageButton editButton = customActionbar.findViewById(R.id.changeTitleBtn);
//        editButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), ChangeTitlePopup.class);
//                intent.putExtra("TITLE", strTitle);
//                intent.putExtra("EPISODE_ID", nEpisodeID);
//                startActivityForResult(intent, 1100);
//            }
//        });
//
//        Toolbar parent = (Toolbar)customActionbar.getParent();
//        parent.setContentInsetsAbsolute(0, 0);
//        inflater.inflate(R.menu.work_write_menu, menu);
////        getMenuInflater().inflate(R.menu.work_write_menu, menu);
//    }

    public void selectMenu(int nPosition) {
        Intent intent = null;

        switch (nPosition) {
            case 0:
                if(chattingList.size() > 1) {
                    Toast.makeText(getActivity(), "입력된 내용이 없어야만 엑셀 파일을 로딩할 수 있습니다.", Toast.LENGTH_LONG).show();
                    return;
                } else if(characterList.size() > 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                    return;
                }

                filePermission();

                return;
            case 1:
                intent = new Intent(getActivity(), ViewerActivity.class);
                ViewerActivity.workVO = InteractionWriteActivity.workVO;
                intent.putExtra("EPISODE_INDEX", nEpisodeIndex);
                intent.putExtra("INTERACTION", InteractionWriteActivity.workVO.getEpisodeList().get(nEpisodeIndex).getIsDistractor());
                intent.putExtra("PREVIEW", true);
                intent.putExtra("INTERACTION_NUMBER", 2);
                startActivity(intent);

//                intent = new Intent(getActivity(), EpisodePreviewActivity.class);
//                intent.putExtra("EPISODE_ID", nEpisodeID);
//                intent.putExtra("EPISODE_INDEX", nEpisodeIndex);
//                intent.putExtra("INTERACTION", InteractionWriteActivity.workVO.getEpisodeList().get(nEpisodeIndex).getIsDistractor());
//                EpisodePreviewActivity.workVO = InteractionWriteActivity.workVO;
//                startActivity(intent);
                return;

            case 2:
                if (!chattingList.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("회차 삭제");
                    builder.setMessage("회차의 모든 내용이 삭제됩니다.\n삭제하시겠습니까?");
                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            requestDeleteAllMessage();
                        }
                    });

                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    return;
                } else {
                    Toast.makeText(getActivity(), "삭제할 내용이 없습니다.", Toast.LENGTH_LONG).show();
                }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        Intent intent = null;

        switch (item.getItemId()) {
            case R.id.action_btn1:              // 제출하기 클릭
                requestEpisodeSubmit();
                return true;

            case R.id.action_btn2:
                if(chattingList.size() > 1) {
                    Toast.makeText(getActivity(), "입력된 내용이 없어야만 엑셀 파일을 로딩할 수 있습니다.", Toast.LENGTH_LONG).show();
                    return true;
                } else if(characterList.size() > 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                intent = new Intent(getActivity(), ViewerActivity.class);
                ViewerActivity.workVO = InteractionWriteActivity.workVO;
                intent.putExtra("EPISODE_INDEX", nEpisodeIndex);
                intent.putExtra("INTERACTION", InteractionWriteActivity.workVO.getEpisodeList().get(nEpisodeIndex).getIsDistractor());
                intent.putExtra("PREVIEW", true);
                intent.putExtra("INTERACTION_NUMBER", 2);
                startActivity(intent);

//                intent = new Intent(getActivity(), EpisodePreviewActivity.class);
//                intent.putExtra("EPISODE_ID", nEpisodeID);
//                intent.putExtra("EPISODE_INDEX", nEpisodeIndex);
//                intent.putExtra("INTERACTION", InteractionWriteActivity.workVO.getEpisodeList().get(nEpisodeIndex).getIsDistractor());
//                EpisodePreviewActivity.workVO = InteractionWriteActivity.workVO;
//                startActivity(intent);
                return true;

            case R.id.action_btn4:
                if (!chattingList.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("회차 삭제");
                    builder.setMessage("회차의 모든 내용이 삭제됩니다.\n삭제하시겠습니까?");
                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            requestDeleteAllMessage();
                        }
                    });

                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    return true;
                } else {
                    Toast.makeText(getActivity(), "삭제할 내용이 없습니다.", Toast.LENGTH_LONG).show();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void requestDeleteAllMessage() {
        CommonUtils.showProgressDialog(getActivity(), "서버와 통신중입니다. 잠시만 기다려주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject resultObject = HttpClient.requestDeleteAllMessage(new OkHttpClient(), nEpisodeID);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            CommonUtils.hideProgressDialog();

                            if(resultObject != null && resultObject.getString("RESULT").equals("SUCCESS")) {
                                bgView.setBackgroundResource(0);
                                bgView.setImageBitmap(null);
                                bgView.setBackgroundColor(getResources().getColor(R.color.colorDefaultBG));

                                chattingList.clear();
                                aa.notifyDataSetChanged();
                                Toast.makeText(getActivity(), "삭제되었습니다.", Toast.LENGTH_LONG).show();

                                getCharacterData(true);
                            } else {
                                Toast.makeText(getActivity(), "삭제에 실패하였습니다.", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    private void requestEpisodeSubmit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("회차 제출");
        builder.setMessage("회차를 제출하면 승인 대기 상태가 됩니다. 관리자가 회차를 승인한 이후부터 회차가 독자들에게 게시됩니다.\n회차를 제출하시겠습니까?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id) {
                CommonUtils.showProgressDialog(getActivity(), "작품을 제출하고 있습니다.");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject resultObject = HttpClient.requestEpisodeSubmit(new OkHttpClient(), nEpisodeID);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    CommonUtils.hideProgressDialog();

                                    if(resultObject == null) {
                                        Toast.makeText(getActivity(), "제출에 실패하였습니다.", Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                    if(resultObject.getString("RESULT").equals("SUCCESS")) {
                                        Toast.makeText(getActivity(), "제출되었습니다.", Toast.LENGTH_LONG).show();
                                        getActivity().finish();
                                    } else {
                                        Toast.makeText(getActivity(), "제출에 실패하였습니다.", Toast.LENGTH_LONG).show();
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

    private void filePermission() {
        TedPermission.with(getActivity())
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/*");
                        startActivityForResult(Intent.createChooser(intent, "Excel Files"), InteractionWriteActivity.EXCEL);
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {

                    }
                })
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(bColorPicker) {
            bColorPicker = false;
            ColorPickerDialog.newBuilder()
                    .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                    .setAllowPresets(false)
                    .setDialogId(1200)
                    .setColor(nBgColor)
                    .setShowAlphaSlider(true)
                    .show(getActivity());
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

    public void onBackpress() {
        if(bShowMenu) {
            bottomSettingLayout.setVisibility(View.GONE);
            contentsAddBtn.setImageResource(R.drawable.selectionplus);
            bShowMenu = false;
        } else {
            getActivity().finish();
        }
    }

    public void onColorSelected(int dialogId, int color) {
        nBgColor = color;
        bgColor = String.format("#%06X", (0xFFFFFF & nBgColor));
        bgView.setImageResource(0);
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

        nEditIndex = -1;
    }

    View.OnClickListener bottomBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = null;

            switch(v.getId()) {
                case R.id.bgSettingView:
                    TedPermission.with(getActivity())
                            .setPermissionListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted() {
                                    startActivityForResult(new Intent(getActivity(), InteractionBGSelectPopup.class), InteractionWriteActivity.BG_SELECT_POPUP);
                                }

                                @Override
                                public void onPermissionDenied(List<String> deniedPermissions) {
                                    Toast.makeText(getActivity(), "권한을 허용해주셔야 사진 설정이 가능합니다.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                            .check();
                    break;
                case R.id.imgSettingView:
//                    if(nSelectedCharacterIndex == 0) {
//                        Toast.makeText(getActivity(), "지문은 이미지를 추가할 수 없습니다.", Toast.LENGTH_LONG).show();
//                        return;
//                    }

                    TedPermission.with(getActivity())
                            .setPermissionListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted() {
                                    Intent intent = new Intent(getActivity(), InteractionMediaSelectPopup.class);
                                    bVideo = false;
                                    startActivityForResult(intent, InteractionWriteActivity.MEDIA_SELECT_POPUP);
                                }

                                @Override
                                public void onPermissionDenied(List<String> deniedPermissions) {
                                    Toast.makeText(getActivity(), "권한을 허용해주셔야 사진 설정이 가능합니다.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                            .check();
                    break;

//                    intent = new Intent(getActivity(), InteractionMediaSelectPopup.class);
////                    intent.putExtra("TYPE", PhotoPickerActivity.TYPE_CONTENTS_IMG);
//                    startActivityForResult(intent, InteractionWriteActivity.MEDIA_SELECT_POPUP);
//                    break;

                case R.id.videoSettingView:
                    if(nSelectedCharacterIndex == 0) {
                        Toast.makeText(getActivity(), "지문은 동영상을 추가할 수 없습니다.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    TedPermission.with(getActivity())
                            .setPermissionListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted() {
                                    Intent intent = new Intent(getActivity(), InteractionPhotoPickerActivity.class);
                                    intent.putExtra("TYPE", 3);
                                    bVideo = true;
                                    startActivityForResult(intent, InteractionWriteActivity.PHOTOPICKER_CONTENTS_VIDEO);
                                }

                                @Override
                                public void onPermissionDenied(List<String> deniedPermissions) {
                                    Toast.makeText(getActivity(), "권한을 허용해주셔야 사진 설정이 가능합니다.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                            .check();

//                    intent = new Intent(getActivity(), InteractionMediaSelectPopup.class);
//                    startActivityForResult(intent, InteractionWriteActivity.MEDIA_SELECT_POPUP_VIDEO);
                    break;

                case R.id.distractorView:
                    Toast.makeText(getActivity(), "이미 분기가 설정되었습니다.", Toast.LENGTH_LONG).show();
                    break;

                case R.id.soundSettingView:
                    if(nSelectedCharacterIndex == 0) {
                        Toast.makeText(getActivity(), "지문은 음원을 추가할 수 없습니다.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    TedPermission.with(getActivity())
                            .setPermissionListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted() {
                                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                    intent.setType("audio/*");
                                    startActivityForResult(Intent.createChooser(intent, "Music File"), 1030);
//                                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                                    intent.setType("audio/*");
//                                    startActivityForResult(Intent.createChooser(intent, "Music File"), 1030);
                                }

                                @Override
                                public void onPermissionDenied(List<String> deniedPermissions) {
                                    Toast.makeText(getActivity(), "권한을 허용해주셔야 사진 설정이 가능합니다.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                            .check();

                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == InteractionWriteActivity.MEDIA_SELECT_POPUP) {                // 이미지 선택
                String strSelection = data.getStringExtra("SELECTION");

                if(strSelection.equals("GALLERY")) {
//                    Intent intent = new Intent(getActivity(), SeesoGalleryInteractionActivity.class);
                    Intent intent = new Intent(getActivity(), TokkiGalleryActivity.class);
                    intent.putExtra("Interaction", 100);
                    startActivityForResult(intent, InteractionWriteActivity.PHOTOPICKER_CONTENTS_IMAGE);
                } else if(strSelection.equals("ALBUM")) {
                    Intent intent = new Intent(getActivity(), InteractionPhotoPickerActivity.class);
                    startActivityForResult(intent, InteractionWriteActivity.PHOTOPICKER_CONTENTS_IMAGE);
                }
            } else if(requestCode == InteractionWriteActivity.BG_SELECT_POPUP) {
                String strSelection = data.getStringExtra("SELECTION");

                if(strSelection.equals("GALLERY")) {
                    Intent intent = new Intent(getActivity(), TokkiGalleryActivity.class);
                    intent.putExtra("Interaction", 100);
                    startActivityForResult(intent, InteractionWriteActivity.PHOTOPICKER_BG_IMAGE);
                } else if(strSelection.equals("ALBUM")) {
                    Intent intent = new Intent(getActivity(), InteractionPhotoPickerActivity.class);
                    startActivityForResult(intent, InteractionWriteActivity.PHOTOPICKER_BG_IMAGE);
                } else if(strSelection.equals("COLOR")) {
                    bColorPicker = true;
                }
            } else if(requestCode == InteractionWriteActivity.MEDIA_SELECT_POPUP_VIDEO) {
                String strSelection = data.getStringExtra("SELECTION");

                if(strSelection.equals("GALLERY")) {
                    Toast.makeText(getActivity(), "준비중입니다.", Toast.LENGTH_LONG).show();
                } else if(strSelection.equals("ALBUM")) {
                    Intent intent = new Intent(getActivity(), InteractionPhotoPickerActivity.class);
                    intent.putExtra("TYPE", 3);
                    startActivityForResult(intent, InteractionWriteActivity.PHOTOPICKER_CONTENTS_VIDEO);
                }
            } else if(requestCode == InteractionWriteActivity.PHOTOPICKER_BG_IMAGE) {
                String imgUri = data.getStringExtra("URI");

                Glide.with(this)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(Uri.parse(imgUri))
                        .into(bgView);

                String strFilePath = CommonUtils.getRealPathFromURI(getActivity(), Uri.parse(imgUri));
                newFileList.add(strFilePath);

                ChatVO chatVO = null;

                if(bEdit) {                     // 수정 이라면
                    chatVO = chattingList.get(nEditIndex);
                } else {                        // 신규 일때만 오더를 정해준다
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
                chatVO.setStrContentsFile(CommonUtils.getRealPathFromURI(getActivity(), Uri.parse(imgUri)));

                Glide.with(getActivity())
                        .asBitmap() // some .jpeg files are actually gif
                        .load(imgUri)
                        .apply(new RequestOptions().override(800, 800))
                        .into(bgView);

                requestUploadMessage(chatVO, bEdit);

                bEdit = false;
                nOrder = -1;
                return;
            } else if(requestCode == InteractionWriteActivity.PHOTOPICKER_CONTENTS_IMAGE) {
                String imgUri = data.getStringExtra("URI");
                if(imgUri != null) {                                    // 일반 채팅 이미지 라면
                    ChatVO chatVO = null;

                    if(bEdit) {                     // 수정 이라면
                        chatVO = chattingList.get(nEditIndex);
                    } else {                        // 신규 일때만 오더를 정해준다
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

                        if(nSelectedCharacterIndex > 0) {
                            CharacterVO characterVO = characterList.get(nSelectedCharacterIndex);
                            chatVO.setCharacter(characterVO);
                            chatVO.setType(ChatVO.TYPE_IMAGE);
                        } else {
                            chatVO.setType(ChatVO.TYPE_IMAGE_NAR);
                        }

                    }

                    chatVO.setContentsUri(Uri.parse(imgUri));
                    chatVO.setStrContentsFile(CommonUtils.getRealPathFromURI(getActivity(), Uri.parse(imgUri)));
                    requestUploadMessage(chatVO, bEdit);
                    return;
                }
            } else if(requestCode == InteractionWriteActivity.PHOTOPICKER_CONTENTS_VIDEO) {
                String imgUri = data.getStringExtra("URI");
                if(imgUri != null) {
                    ChatVO chatVO = null;

                    if(bEdit) {                     // 수정 이라면
                        chatVO = chattingList.get(nEditIndex);
                    } else {                        // 신규 일때만 오더를 정해준다
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

                        CharacterVO characterVO = characterList.get(nSelectedCharacterIndex);
                        chatVO.setCharacter(characterVO);
                    }

                    chatVO.setType(ChatVO.TYPE_VIDEO);
                    chatVO.setContentsUri(Uri.parse(imgUri));
                    chatVO.setStrContentsFile(CommonUtils.getRealPathFromURI(getActivity(), Uri.parse(imgUri)));
                    requestUploadMessage(chatVO, bEdit);

                    return;
                }
            } else if (requestCode == 1010) {                                      //   캐릭터 추가
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
            } else if(requestCode == 1011) {                            // 캐릭터 변경/삭제
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
            } else if(requestCode == 1000) {                            // 베경 색 설정
                isEdit = data.getBooleanExtra("EDIT", false);

                if(isEdit)
                    nEditIndex = data.getIntExtra("ORDER", -1);

                bColorPicker = true;
            } else if(requestCode == 1025) {                            // 선택지 추가
                ChatVO chatVO = chattingList.get(nOrder);
                int nOrder = data.getIntExtra("ORDER", -1);

                String strInteraction1 = data.getStringExtra("DISTRACTOR_1");
                String strInteraction2 = data.getStringExtra("DISTRACTOR_2");
                chatVO.setContents(strInteraction1 + "╋" + strInteraction2);

                requestUploadMessage(chatVO, true);
            } else if (requestCode == 1030 || requestCode == 1035) {                           // 음원 가져오기
                Uri fileUri = data.getData();

                String strFilePath = CommonUtils.getRealPathFromURI(getActivity(), fileUri);
                File sourceFile = new File(strFilePath);

                if(!sourceFile.exists()) {
                    CommonUtils.hideProgressDialog();
                    Toast.makeText(getActivity(), "음원 파일이 잘못되었습니다.", Toast.LENGTH_LONG).show();

                    return;
                }

                String filename = strFilePath.substring(strFilePath.lastIndexOf("/")+1);

                ChatVO chatVO = null;

                boolean bEdit = false;

                if(requestCode == 1030) {
                    chatVO = new ChatVO();
                    CharacterVO characterVO = characterList.get(nSelectedCharacterIndex);
                    chatVO.setCharacter(characterVO);

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
                } else if(requestCode == 1035) {
                    chatVO = chattingList.get(nEditIndex);
                    bEdit = true;
                }

                chatVO.setType(ChatVO.TYPE_SOUND);
                chatVO.setContentsUri(fileUri);
                chatVO.setStrContentsFile(CommonUtils.getRealPathFromURI(getActivity(), fileUri));

                requestUploadMessage(chatVO, bEdit);
            } else if(requestCode == 1050) {
                String strContents = data.getStringExtra("EDITED_TEXT");
                int nOrder = data.getIntExtra("ORDER", -1);

                ChatVO chatVO = chattingList.get(nOrder);
                chatVO.setContents(strContents);

                requestUploadMessage(chatVO, true);
            } else if(requestCode == 1100) {                    // 제목 변경
                strTitle = data.getStringExtra("TITLE");
                interactionWriteActivity.setTitle(strTitle);
            } else if(requestCode == InteractionWriteActivity.EXCEL) {              // 엑셀파일 로딩
                CommonUtils.showProgressDialog(getActivity(), "엑셀파일을 불러오고 있습니다. 잠시만 기다려주세요.");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean bTemp = false;

                        if(characterList.size() <= 1)
                            bTemp = true;
                        else
                            bTemp = HttpClient.requestDeleteAllCharacter(new OkHttpClient(), nEpisodeID);

                        final boolean bResult = bTemp;

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!bResult) {
                                    CommonUtils.hideProgressDialog();
                                    Toast.makeText(getActivity(), "엑셀파일 로딩에 실패했습니다.", Toast.LENGTH_LONG).show();
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

    public void excelDoen() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CommonUtils.hideProgressDialog();
                getCharacterData(true);
            }
        });
    }

    private void readExcel(final Uri fileUri) {
        final String filePath = CommonUtils.getRealPathFromURI(getActivity(), fileUri);

        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                try {
                    ExcelReader excelReader = new ExcelReader(getActivity(), nEpisodeID);
                    excelReader.readExcel(filePath, true);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InvalidFormatException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser) {
            if(characterList == null || characterList.size() == 0)
                return;

            strTitle = interactionWriteActivity.strTitle;
            getCharacterData(true);
        }
    }

    private void requestDeleteCharacter(final int nIndex) {
        CommonUtils.showProgressDialog(getActivity(), "등장인물을 삭제하고 있습니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                CharacterVO vo = characterList.get(nIndex);

                JSONObject resultObject = HttpClient.requestDeleteCharacter(new OkHttpClient(), vo.getnCharacterID());

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            CommonUtils.hideProgressDialog();

                            if(resultObject.getString("RESULT").equals("SUCCESS")) {
                                characterList.remove(nIndex);
                                resetCharacterLayout();
                                Toast.makeText(getActivity(), "삭제되었습니다.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), "등장인물 삭제를 실패하였습니다.", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    private void getCharacterData(boolean bReload) {
        CommonUtils.showProgressDialog(getActivity(), "서버에서 작품 데이터를 가져오고 있습니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                characterList.clear();
                nameList.clear();
                characterList.add(null);
                nameList.add("지문");
                characterList.addAll(HttpClient.getCharacterDataWithEpisodeID(new OkHttpClient(), "" + InteractionWriteActivity.workVO.getEpisodeList().get(nEpisodeIndex).getnEpisodeID()));

                if(characterList == null) {
                    Toast.makeText(getActivity(), "서버와의 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                for(int i = 1 ; i < characterList.size() ; i++) {
                    nameList.add(characterList.get(i).getName());
                }

                interactionWriteActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resetCharacterLayout();

                        if(bReload)
                            getEpisodeChatData();
                        else
                            CommonUtils.hideProgressDialog();
                    }
                });
            }
        }).start();
    }

    private int getCharacterIndex(CharacterVO vo) {
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

    private void getEpisodeChatData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                chattingList.clear();
                chattingList.addAll(HttpClient.getChatDataWithEpisodeIDAndInteraction(new OkHttpClient(), "" + InteractionWriteActivity.workVO.getEpisodeList().get(nEpisodeIndex).getnEpisodeID(), 2));

                interactionWriteActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();
                        if(chattingList == null) {
                            Toast.makeText(getActivity(), "서버와의 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(chattingList.size() > 0) {
                            ChatVO vo = new ChatVO();
                            vo.setType(ChatVO.TYPE_EMPTY);
                            chattingList.add(0, vo);
                        }

                        aa.notifyDataSetChanged();

                        if(nAddIndex == -1 && nEditIndex == -1)
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

    private void hideBottomView() {
        bShowMenu = false;
        bottomSettingLayout.setVisibility(View.GONE);
        contentsAddBtn.setImageResource(R.drawable.selectionplus);
    }

    private void resetCharacterLayout() {
        speakerAddLayout.removeAllViews();
        characterViewList.clear();

        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
                nameView.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorBlack));
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

                    Glide.with(getActivity())
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
                            nameView.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorBlack));
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

                        Intent intent = new Intent(getActivity(), CreateCharacterActivity.class);
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

    public void OnClickSendBtn(View view) {
        String strContents = inputTextView.getText().toString();

        if(strContents.length() == 0) {
            Toast.makeText(interactionWriteActivity, "내용을 입력하세요.", Toast.LENGTH_LONG).show();
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

        if(nAddIndex > -1)
            chatVO.setnOrder(nAddIndex+1);
        else
            chatVO.setnOrder(chattingList.size());

        chatVO.setContents(strContents);
//        chattingList.add(chatVO);

        requestUploadMessage(chatVO, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void requestUploadMessage(final ChatVO chatVO, final boolean bEdit) {
        if(!bEdit)
            nEditIndex = -1;

        if(mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            oldPlayBtn.setImageResource(R.drawable.talk_play1);
            oldPlayBtn = null;
            oldPB.setProgress(0);
            oldPB = null;
            timer.cancel();
            timer = null;
            nPlayingIndex = -1;
        }

        CommonUtils.showProgressDialog(getActivity(), "서버와 통신중입니다. 잠시만 기다려주세요.");

        try {
            String url = CommonUtils.strDefaultUrl + "PanAppUploadChat.jsp";
            RequestBody requestBody = null;

            File sourceFile = null;

            JSONObject sendObject = new JSONObject();

            if(nEpisodeID > -1)
                sendObject.put("EPISODE_ID", nEpisodeID);

            if(nAddIndex > -1) {
                sendObject.put("CHAT_INTERCEPT", true);
            } else {
                sendObject.put("CHAT_INTERCEPT", false);
            }

            if(bEdit)
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

            if(nAddIndex > -1) {                                // 사이에 추가의 경우, 인터렉션 이전인지 이후인지 구분이 필요하다. 그래야 값을 가져오는데 문제가 없다.
                ChatVO preVO = chattingList.get(nAddIndex);

                if(preVO.getType() == ChatVO.TYPE_EMPTY) {
                    preVO = chattingList.get(nAddIndex+1);
                }

                Log.d("prevo", preVO.getnChatID() + "");
                chatObject.put("CHAT_DISTRACTOR", preVO.getnInteractionNum());
            } else if(nEditIndex > -1) {                        // 수정의 경우에도 마찬가지
                ChatVO preVO = chattingList.get(nEditIndex);
                chatObject.put("CHAT_DISTRACTOR", preVO.getnInteractionNum());
                chatVO.setnInteractionNum(preVO.getnInteractionNum());
            } else {                                                                    // 2번 선택지 이므로그냥 작성할 경우엔 무조건 2을 넣어주면 된다.
                chatObject.put("CHAT_DISTRACTOR", 2);
                chatVO.setnInteractionNum(2);
            }

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
            } else {
                chatObject.put("CHAT_CONTENTS", chatVO.getStrContentsFile());
            }

            chatArray.put(chatObject);

            sendObject.put("CHAT_ARRAY", chatArray);
            MultipartBody.Builder builder = new MultipartBody.Builder();

            builder.setType(MultipartBody.FORM)
                    .addFormDataPart("JSON_BODY", sendObject.toString());

            if(nType == 3 || nType == 4 || nType == 5 || nType == 8) {
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

            if(InteractionWriteActivity.workVO.getEpisodeList().get(nEpisodeIndex).getStrSubmit().equals("Y")) {
                CommonUtils.hideProgressDialog();
                Toast.makeText(interactionWriteActivity, "작품 등록을 실패하였습니다.", Toast.LENGTH_LONG).show();
                return;
            }

            new OkHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e9) {
                    e9.printStackTrace();

                    interactionWriteActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.hideProgressDialog();
                            Toast.makeText(interactionWriteActivity, "작품 등록을 실패하였습니다.", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String strResult = response.body().string();

                    interactionWriteActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.hideProgressDialog();
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
//
//                                    boolean bScroll = true;
//                                    if(nAddIndex > -1 || nEditIndex > -1)
//                                        bScroll = false;
//
//                                    nAddIndex = -1;
//                                    aa.notifyDataSetChanged();
//
//                                    if(bScroll)
//                                        chattingListView.setSelection(aa.getCount() - 1);

                                    inputTextView.setText("");
                                    getEpisodeChatData();
                                } else {
                                    Toast.makeText(interactionWriteActivity, "작품 등록을 실패하였습니다.", Toast.LENGTH_LONG).show();
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

            interactionWriteActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CommonUtils.hideProgressDialog();
                    Toast.makeText(interactionWriteActivity, "작품 생성을 실패하였습니다.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void requestUpdateCharacter(final CharacterVO characterVO, final int nIndex) {
        CommonUtils.showProgressDialog(getActivity(), "캐릭터를 생성중입니다.");

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
                strFilePath = CommonUtils.getRealPathFromURI(interactionWriteActivity, characterVO.getImage());
                sourceFile = new File(strFilePath);

                if(!sourceFile.exists()) {
                    CommonUtils.hideProgressDialog();
                    Toast.makeText(interactionWriteActivity, "이미지가 잘못되었습니다.", Toast.LENGTH_LONG).show();

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
                    CommonUtils.hideProgressDialog();
                    Toast.makeText(interactionWriteActivity, "캐릭터 생성을 실패하였습니다.", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String strResult = response.body().string();

                    interactionWriteActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.hideProgressDialog();

                            try {
                                JSONObject resultObject = new JSONObject(strResult);

                                if(resultObject.getString("RESULT").equals("SUCCESS")) {
                                    nameList.set(nIndex, characterVO.getName());
                                    characterList.set(nIndex, characterVO);
                                    getCharacterData(true);
                                } else {
                                    Toast.makeText(interactionWriteActivity, "캐릭터 생성을 실패하였습니다.", Toast.LENGTH_LONG).show();
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

            interactionWriteActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CommonUtils.hideProgressDialog();
                    Toast.makeText(interactionWriteActivity, "작품 생성을 실패하였습니다.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void requestCreateCharacter(final CharacterVO characterVO) {
        CommonUtils.showProgressDialog(getActivity(), "캐릭터를 생성중입니다.");

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
                strFilePath = CommonUtils.getRealPathFromURI(interactionWriteActivity, characterVO.getImage());
                sourceFile = new File(strFilePath);

                if(!sourceFile.exists()) {
                    CommonUtils.hideProgressDialog();
                    Toast.makeText(interactionWriteActivity, "이미지가 잘못되었습니다.", Toast.LENGTH_LONG).show();

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
                    CommonUtils.hideProgressDialog();
                    Toast.makeText(interactionWriteActivity, "캐릭터 생성을 실패하였습니다.", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String strResult = response.body().string();

                    interactionWriteActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.hideProgressDialog();

                            try {
                                JSONObject resultObject = new JSONObject(strResult);

                                if(resultObject.getString("RESULT").equals("SUCCESS")) {
                                    String strCharacterID = resultObject.getString("CHARACTER_ID");
                                    characterVO.setnCharacterID(Integer.valueOf(strCharacterID));

                                    nameList.add(characterVO.getName());
                                    characterList.add(characterVO);
                                    resetCharacterLayout();
                                } else {
                                    Toast.makeText(interactionWriteActivity, "캐릭터 생성을 실패하였습니다.", Toast.LENGTH_LONG).show();
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

            interactionWriteActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CommonUtils.hideProgressDialog();
                    Toast.makeText(interactionWriteActivity, "작품 생성을 실패하였습니다.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onClick(View view) {

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

            if(nType == ChatVO.TYPE_TEXT) {
                nDirection = characterVO.getDirection();

                if(nDirection == 0)             // left
                    convertView = mLiInflater.inflate(R.layout.left_chatting_row, parent, false);
                else
                    convertView = mLiInflater.inflate(R.layout.right_chatting_row, parent, false);

                TextView nameView = convertView.findViewById(R.id.nameView);
                nameView.setText(characterVO.getName());

                if(characterVO.isbBlackName())
                    nameView.setTextColor(Color.parseColor("#000000"));
                else
                    nameView.setTextColor(Color.parseColor("#ffffff"));

                TextView contentsTextView = convertView.findViewById(R.id.contentsTextView);
                contentsTextView.setText(chatVO.getContents());

                if(characterVO.isbBlackText()) {
                    contentsTextView.setTextColor(getResources().getColor(R.color.colorBlack));
                } else {
                    contentsTextView.setTextColor(getResources().getColor(R.color.colorWhite));
                }

                ImageView imageContentsView = convertView.findViewById(R.id.imageContentsView);

                if(chatVO.getContentsUri() != null) {
                    Glide.with(getActivity())
                            .asBitmap()
                            .load(chatVO.getContentsUri())
                            .apply(new RequestOptions().override(800, 800))
                            .into(imageContentsView);
                } else if(chatVO.getStrContentsFile() != null) {
                    String strUrl = characterVO.getStrImgFile();
//                    strUrl = strUrl.replaceAll(" ", "");

                    if(!strUrl.startsWith("http"))
                        strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;

                    Glide.with(getActivity())
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

                        CommonUtils.showProgressDialog(getActivity(), "음원을 재생중입니다.");

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
                            mediaPlayer.setDataSource(getActivity().getApplicationContext(), Uri.parse(strUrl));
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
                    Glide.with(getActivity())
                            .asBitmap() // some .jpeg files are actually gif
                            .load(chatVO.getContentsUri())
                            .apply(new RequestOptions().override(nWidth, nWidth))
                            .into(imageContentsView);
                } else if(chatVO.getStrContentsFile() != null) {
                    String strUrl = chatVO.getStrContentsFile();

                    if(!strUrl.startsWith("http"))
                        strUrl = CommonUtils.strDefaultUrl + "images" + strUrl;

                    Glide.with(getActivity())
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
                    Glide.with(getActivity())
                            .asBitmap() // some .jpeg files are actually gif
                            .load(chatVO.getContentsUri())
                            .apply(new RequestOptions().override(nWidth, nWidth))
                            .into(imageContentsView);
                } else if(chatVO.getStrContentsFile() != null) {
                    String strUrl = chatVO.getStrContentsFile();
//                    strUrl = strUrl.replaceAll(" ", "");

                    if(!strUrl.startsWith("http"))
                        strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;

                    Glide.with(getActivity())
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

                ImageView playBtn = convertView.findViewById(R.id.playBtn);
                imageContentsView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String strUrl = chatVO.getStrContentsFile();
                        if(!strUrl.startsWith("http"))
                            strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;

                        Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
                        intent.putExtra("VIDEO_URL", strUrl);
                        startActivity(intent);
                    }
                });

                if(chatVO.getContentsUri() != null) {
                    if(thumbBitmapList.containsKey(chatVO.getContentsUri().toString())) {
//                        imageContentsView.setImageBitmap(thumbBitmapList.get(chatVO.getContentsUri().toString()));
                        Glide.with(getActivity())
                                .load(thumbBitmapList.get(chatVO.getContentsUri().toString()))
                                .apply(new RequestOptions().override(nWidth-50, nWidth-50))
                                .into(imageContentsView);
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap thumbnailBitmap = CommonUtils.getVideoThumbnail(chatVO.getContentsUri());
                                thumbBitmapList.put(chatVO.getContentsUri().toString(), thumbnailBitmap);

                                if(getActivity() != null) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Glide.with(getActivity())
                                                    .load(thumbnailBitmap)
                                                    .apply(new RequestOptions().override(nWidth-50, nWidth-50))
                                                    .into(imageContentsView);
                                        }
                                    });
                                }
                            }
                        }).start();

                        try {
                            Bitmap thumbnailBitmap = CommonUtils.getVideoThumbnail(chatVO.getContentsUri());
//                            Bitmap thumbnailBitmap = CommonUtils.retriveVideoFrameFromVideo(ViewerActivity.this, chatVO.getContentsUri().getPath());
                            thumbBitmapList.put(chatVO.getContentsUri().toString(), thumbnailBitmap);
                            Glide.with(getActivity())
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
                        Glide.with(getActivity())
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

                                if(getActivity() != null) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Glide.with(getActivity())
                                                    .load(thumbnailBitmap)
                                                    .apply(new RequestOptions().override(nWidth-50, nWidth-50))
                                                    .into(imageContentsView);
                                        }
                                    });
                                }
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
                    Glide.with(getActivity())
                            .asBitmap() // some .jpeg files are actually gif
                            .load(chatVO.getContentsUri())
                            .transition(BitmapTransitionOptions.withCrossFade())
                            .into(bgView);
                } else if(chatVO.getStrContentsFile() != null) {
                    String strUrl = chatVO.getStrContentsFile();
//                    strUrl = strUrl.replaceAll(" ", "");

                    if(!strUrl.startsWith("http"))
                        strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;

                    Glide.with(getActivity())
                            .asBitmap() // some .jpeg files are actually gif
                            .load(strUrl)
                            .transition(BitmapTransitionOptions.withCrossFade())
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
                        if(nType == ChatVO.TYPE_DISTRACTOR) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("분기 삭제");
                            builder.setMessage("분기를 삭제하시면 분기 이후의 모든 회차 및 채팅 정보가 삭제됩니다.\n정말로 삭제하시겠습니까?");
                            builder.setPositiveButton("예", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    requestDeleteInteraction(position);
                                }
                            });

                            builder.setNegativeButton("취소", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                            builder.show();
                        } else
                            requestDeleteMessage(position);
                    }
                });

                if(position > 1) {
                    ChatVO preVo = chattingList.get(position-1);
                    if(preVo.getType() == ChatVO.TYPE_DISTRACTOR)
                        deleteBtn.setVisibility(View.INVISIBLE);
                    else
                        deleteBtn.setVisibility(View.VISIBLE);
                } else {
                    deleteBtn.setVisibility(View.VISIBLE);
                }
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

            if(position == chattingList.size() - 1 || nType == ChatVO.TYPE_DISTRACTOR)
                addBtn.setVisibility(View.GONE);
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
                    Glide.with(getActivity())
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

                    Glide.with(getActivity())
                            .asBitmap() // some .jpeg files are actually gif
                            .placeholder(nPlaceHolder)
                            .load(strUrl)
                            .apply(new RequestOptions().circleCrop())
                            .into(faceView);
                } else {
                    Glide.with(getActivity())
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
                        Toast.makeText(getActivity(), "내용을 추가하시면 선택하신 사이에 추가 됩니다.\n취소하시려면 버튼을 다시 한 번 눌러주세요.", Toast.LENGTH_LONG).show();
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

    private void requestDeleteInteraction(final int nIndex) {
        CommonUtils.showProgressDialog(getActivity(), "서버와 통신중입니다. 잠시만 기다려주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                ChatVO vo = chattingList.get(nIndex);

                JSONObject resultObject = HttpClient.requestDeleteInteraction(new OkHttpClient(), nEpisodeID, vo.getnChatID(), vo.getnOrder());

                interactionWriteActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            CommonUtils.hideProgressDialog();

                            if(resultObject.getString("RESULT").equals("SUCCESS")) {
                                Toast.makeText(interactionWriteActivity, "삭제되었습니다.", Toast.LENGTH_LONG).show();
                                getActivity().finish();
                            } else {
                                Toast.makeText(interactionWriteActivity, "작품 등록을 실패하였습니다.", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    private void requestDeleteMessage(final int nIndex) {
        CommonUtils.showProgressDialog(getActivity(), "서버와 통신중입니다. 잠시만 기다려주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                ChatVO vo = chattingList.get(nIndex);
                nDeleteIndex = nIndex;
                JSONObject resultObject = HttpClient.requestDeleteMessage(new OkHttpClient(), nEpisodeID, vo.getnChatID(), vo.getnOrder());

                interactionWriteActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            CommonUtils.hideProgressDialog();

                            if(resultObject.getString("RESULT").equals("SUCCESS")) {
                                if(vo.getType() == ChatVO.TYPE_CHANGE_BG || vo.getType() == ChatVO.TYPE_CHANGE_BG_COLOR) {
                                    bgView.setBackgroundResource(0);
                                    bgView.setImageBitmap(null);
                                    bgView.setBackgroundColor(getResources().getColor(R.color.colorDefaultBG));
                                }

                                chattingList.remove(nIndex);
                                getEpisodeChatData();
                                Toast.makeText(interactionWriteActivity, "삭제되었습니다.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(interactionWriteActivity, "작품 등록을 실패하였습니다.", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }
}
