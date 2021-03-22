package com.Whowant.Tokki.UI.Activity.Work;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Login.PanbookLoginActivity;
import com.Whowant.Tokki.UI.Activity.Media.VideoPlayerActivity;
import com.Whowant.Tokki.UI.Popup.CarrotDoneActivity;
import com.Whowant.Tokki.UI.Popup.InteractionPopup;
import com.Whowant.Tokki.UI.Popup.StarPointPopup;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.CustomUncaughtExceptionHandler;
import com.Whowant.Tokki.VO.CharacterVO;
import com.Whowant.Tokki.VO.ChatVO;
import com.Whowant.Tokki.VO.CommentVO;
import com.Whowant.Tokki.VO.EpisodeVO;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;

public class ViewerActivity extends AppCompatActivity {                                 // 작품 보기 화면. 작성창과 매우 유사.
    public static WorkVO workVO;
    private int  nEpisodeIndex;
    private ArrayList<ChatVO> chattingList;
    private ArrayList<ChatVO> showingList;
    private int nShoingIndex = -1;
    private int nLastOrder = 0;
    private boolean bShoingPlus = false;

    private ListView chattingListView;
    private LinearLayout weightEmptyView;
    private ImageView bgView;
    private Bitmap    bgBitmap;
    private HashMap<String, Bitmap> thumbBitmapList = new HashMap<>();
    private CChattingArrayAdapter aa;
    private float fX;
    private float fY;
    private boolean bLong = false;
    private boolean bShowingAutoscroll = false;
    private Timer uiTimer = null;

    private int nBGColor;

    private SharedPreferences pref;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Timer timer;
    private boolean bNext = false;
    private float fStarPoint = 0;
    private float fMyPoint = 0;
    private int   nStarCount = 0;
    private ArrayList<CommentVO> commentList;
    private int nInteraction = 0;
    private boolean bInteraction = false;
    private boolean bPreview = false;
    private int nPlayingIndex = -1;
    private ImageView oldPlayBtn;
    private ProgressBar oldPB;
    private boolean bScrolling = false;
    private boolean bFirst = true;
    private ArrayList<View> viewList;
    private String strCurrentBg = "";
    private boolean bLoading = false;

    private BottomSheetBehavior bottomSheetBehavior;
    private ArrayList<TextView> nameViewList = new ArrayList<>();
    private boolean bGetComment = false;

    private Timer autoScrollTimer = null;
    private int   nAutoLevel = -1;
    private LinearLayout autoScrollLayout;
    private RelativeLayout autoScrollLevel1Btn, autoScrollLevel2Btn, autoScrollLevel3Btn;
    private RelativeLayout starPointLayout, navBar;
    private SeekBar seekBar;
    private TextView seekBar_value;
    private int max = 0;
    private int autoScrollSpeed;
    private RecyclerView episodeListView;
    private boolean isClickedList = false;
    private EpisodeListAdapter ea;
    private ArrayList<EpisodeVO> episodeList;
    private LinearLayout fontControlLayout;
    private int fontType = 0;
    private int chatFontSize = 15, nameFontSize = 14, narrFontSize = 15;

    // Animation animation, animation2;
    private ImageView fontCk0,fontCk1,fontCk2,fontCk3;


    boolean isSlideUp;
    Animation translateDown, translateUp;
    Animation translateTopDown,translateTopUp,translateBottomDown,translateBottomUp;

    void initLayout()
    {
        setFont0();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_viewer);

        fontCk0 = findViewById(R.id.font_ck0);
        fontCk1 = findViewById(R.id.font_ck1);
        fontCk2 = findViewById(R.id.font_ck2);
        fontCk3 = findViewById(R.id.font_ck3);

        initLayout();

        fileObserver.startWatching();
        lgFileObserver.startWatching();

        bFirst = true;
        Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();

        if (!(handler instanceof CustomUncaughtExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
        }

        chattingList = new ArrayList<>();
        nBGColor = getResources().getColor(R.color.colorDefaultBG);
        pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

//        nEpidoseID = getIntent().getIntExtra("EPISODE_ID", 0);
        nEpisodeIndex = getIntent().getIntExtra("EPISODE_INDEX", 0);
        bInteraction = getIntent().getBooleanExtra("INTERACTION", false);
        bPreview = getIntent().getBooleanExtra("PREVIEW", false);
        nLastOrder = getIntent().getIntExtra("LAST_ORDER", 0);
        nInteraction = getIntent().getIntExtra("INTERACTION_NUMBER", 0);

        chattingListView = findViewById(R.id.chattingListView);

        episodeList = workVO.getEpisodeList();

        episodeListView = findViewById(R.id.episodeListView);
        ea = new EpisodeListAdapter(this, episodeList);
        episodeListView.setAdapter(ea);
        episodeListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        episodeListView.getLayoutManager().scrollToPosition(episodeList.size() - nEpisodeIndex - 1);

        if(workVO.getSortedEpisodeList() == null) {
            Toast.makeText(this, "작품을 읽어오는 중 문제가 발생했습니다. 다시 로딩해 주세요.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView titleView = findViewById(R.id.titleView);
        titleView.setText(workVO.getSortedEpisodeList().get(nEpisodeIndex).getStrTitle());

        ToggleButton settingBtn = findViewById(R.id.settingBtn);
        TextView previousBtn = findViewById(R.id.previousBtn);
        TextView nextBtn = findViewById(R.id.nextBtn);
        ImageButton scrollBtn = findViewById(R.id.scrollBtn);

        autoScrollSpeed = 0;

        scrollBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (autoScrollSpeed == 0) {
                    autoScrollSpeed = 1;
                    bShowingAutoscroll = true;
                    scrollBtn.setBackgroundResource(R.drawable.ic_i_scroll);
               //     Toast.makeText(ViewerActivity.this, "자동 스크롤을 시작합니다.", Toast.LENGTH_SHORT).show();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ViewerActivity.this, "1배속 자동스크롤을 시작합니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    startAutoScroll(1);
                } else if (autoScrollSpeed == 1) {
                    autoScrollSpeed = 2;
                    bShowingAutoscroll = true;
                    scrollBtn.setBackgroundResource(R.drawable.ic_i_autoscroll_3);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ViewerActivity.this, "2배속 자동스크롤을 시작합니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                   startAutoScroll(2);
                } else if (autoScrollSpeed == 2) {
                    autoScrollSpeed = 3;
                    bShowingAutoscroll = true;
                    scrollBtn.setBackgroundResource(R.drawable.ic_i_autoscroll_stop);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ViewerActivity.this, "3배속 자동스크롤을 시작합니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    startAutoScroll(3);
                } else if (autoScrollSpeed == 3) {
                    autoScrollSpeed = 0;
                    bShowingAutoscroll = false;
                    scrollBtn.setBackgroundResource(R.drawable.ic_i_autoscroll_1);
                    Toast.makeText(ViewerActivity.this, "자동 스크롤을 정지합니다.", Toast.LENGTH_SHORT).show();
                    if (autoScrollTimer != null) {
                        autoScrollTimer.cancel();
                        autoScrollTimer = null;
                    }
                }

            }
        });
//        scrollBtn.setOnTouchListener(onTouchListener);
        navBar = findViewById(R.id.navBar);
        starPointLayout = findViewById(R.id.starPointLayout);
        fontControlLayout = findViewById(R.id.fontControlLayout);

        settingBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if(nEpisodeIndex >= workVO.getSortedEpisodeList().size()-1) {
                    //    nextBtn.setVisibility(View.INVISIBLE);
                        nextBtn.setTextColor(Color.parseColor("#D4D4D8"));

                    } else if (nEpisodeIndex == 0) {
                      //  previousBtn.setVisibility(View.INVISIBLE);
                        previousBtn.setTextColor(Color.parseColor("#D4D4D8"));
                    }
                    settingBtn.setBackgroundResource(R.drawable.ic_i_setting_blue);

                    navBar.startAnimation(translateBottomUp);
                    fontControlLayout.startAnimation(translateTopDown);

                    navBar.setVisibility(View.VISIBLE);
                    fontControlLayout.setVisibility(View.VISIBLE);

                } else {

                    navBar.startAnimation(translateBottomDown);
                    fontControlLayout.startAnimation(translateTopUp);

                    navBar.setVisibility(View.INVISIBLE);
                    fontControlLayout.setVisibility(View.INVISIBLE);
                    starPointLayout.setVisibility(View.INVISIBLE);
                    settingBtn.setBackgroundResource(R.drawable.ic_i_setting);
                }
            }
        });

        ToggleButton starBtn = findViewById(R.id.starBtn);

        starBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    getStarPoint();
                    starPointLayout.setVisibility(View.VISIBLE);
                } else {
                    starPointLayout.setVisibility(View.INVISIBLE);
                }
            }
        });

        seekBar_value = findViewById(R.id.seekBar_value);
        seekBar = findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (max != 0) {
                    seekBar_value.setText(String.valueOf(seekBar.getProgress() * 100 / max));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int showingIndex = seekBar.getProgress();

                if (showingIndex > showingList.size()) {
                    for (int i = showingList.size() ; i < showingIndex ; i++) {
                        setNextChat();
                    }
                } else {
                    chattingListView.setSelection(showingIndex);
                    seekBar_value.setText(String.valueOf(showingIndex * 100 / max));
                }
            }
        });

        LinearLayout episodeListLayout = findViewById(R.id.episodeListLayout);
        ToggleButton episodeListBtn = findViewById(R.id.episodeListBtn);
        translateDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_down);
        translateUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_up);

        translateTopDown = AnimationUtils.loadAnimation(this, R.anim.translate_down);
        translateTopUp = AnimationUtils.loadAnimation(this, R.anim.translate_up);
        translateBottomDown = AnimationUtils.loadAnimation(this, R.anim.translate_bottom_down);
        translateBottomUp = AnimationUtils.loadAnimation(this, R.anim.translate_bottom_up);



        episodeListBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    episodeListLayout.setVisibility(View.VISIBLE);
                    translateDown.setFillAfter(true);
                    episodeListLayout.startAnimation(translateDown);
                } else {
                    episodeListLayout.startAnimation(translateUp);
                    episodeListLayout.setVisibility(View.INVISIBLE);
                }
            }
        });

        autoScrollLevel1Btn = findViewById(R.id.autoScrollLevel1Btn);
        autoScrollLevel2Btn = findViewById(R.id.autoScrollLevel2Btn);
        autoScrollLevel3Btn = findViewById(R.id.autoScrollLevel3Btn);

        autoScrollLayout = findViewById(R.id.autoScrollLayout);
        bgView = findViewById(R.id.bgView);
        chattingListView = findViewById(R.id.listView);
//        weightEmptyView = findViewById(R.id.weightEmptyView);

        chattingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                setNextChat();
            }
        });

        isSlideUp = false;

        chattingListView.setOnTouchListener(onTouchListener);
//        weightEmptyView.setOnTouchListener(onTouchListener);

//        bgView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                if(autoScrollTimer != null) {
//                    autoScrollTimer.cancel();
//                    autoScrollTimer = null;
//                }
//
//                autoScrollLayout.setVisibility(View.VISIBLE);
//
//                if (isSlideUp)
//                {
//                    slide(autoScrollLevel1Btn, 0, 1500);
//                    slide(autoScrollLevel2Btn, 0, 1000);
//                    slide(autoScrollLevel3Btn, 0, 500);
//                }
//                else
//                {
//                    slide(autoScrollLevel1Btn, 1500, 0);
//                    slide(autoScrollLevel2Btn, 1000, 0);
//                    slide(autoScrollLevel3Btn, 500, 0);
//
//                }
//
//                isSlideUp = !isSlideUp;
//
//                return false;
//            }
//        });

        chattingListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    bScrolling = false;
                } else if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    bScrolling = true;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        showingList = new ArrayList<>();
        aa = new CChattingArrayAdapter(this, R.layout.left_chatting_row, showingList);
        chattingListView.setAdapter(aa);

//        if(!bPreview)
//            sendViewing(nLastOrder);

        RelativeLayout photoSelectBottomSheet = findViewById(R.id.comment_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(photoSelectBottomSheet);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size); // size.x size.y
            int height = size.y;
            height *= 0.2;

            ListView listView = findViewById(R.id.listView);
            listView.setPadding(0, 0, 0, height); // 화면 해상도 계산 후 paddingBottom
        }
    }

    FileObserver fileObserver = new FileObserver(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Screenshots") {
        @Override
        public void onEvent(int event, @Nullable String path) {
            if (event == FileObserver.CREATE) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ViewerActivity.this, "캡쳐 후 불법 유포 시 처벌받을 수 있습니다.", Toast.LENGTH_LONG).show();
                    }
                });

            }
        }
    };

    FileObserver lgFileObserver = new FileObserver(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/Screenshots") {
        @Override
        public void onEvent(int event, @Nullable String path) {
            if (event == FileObserver.CREATE) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ViewerActivity.this, "캡쳐 후 불법 유포 시 처벌받을 수 있습니다.", Toast.LENGTH_LONG).show();
                    }
                });

            }
        }
    };

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                fX = motionEvent.getX();
                fY = motionEvent.getY();

                if(bShowingAutoscroll) {
                    slide(autoScrollLevel1Btn, 0, 1500);
                    slide(autoScrollLevel2Btn, 0, 1000);
                    slide(autoScrollLevel3Btn, 0, 500);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            autoScrollLayout.setVisibility(View.INVISIBLE);
                            navBar.setVisibility(View.VISIBLE);
                        }
                    }, 500);
//                        autoScrollLayout.setVisibility(View.INVISIBLE);
                    bShowingAutoscroll = false;
                    return false;
                }

//                if(autoScrollTimer != null) {
//                    autoScrollTimer.cancel();
//                    autoScrollTimer = null;
//                    Toast.makeText(ViewerActivity.this, "자동 스크롤을 정지합니다.", Toast.LENGTH_SHORT).show();
//                    return false;
//                }

//                bLong = true;

                if(uiTimer != null) {
                    uiTimer.cancel();
                    uiTimer = null;
                }

                uiTimer = new Timer();
                uiTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(bLong) {
                                    bLong = false;
                                    bShowingAutoscroll = true;
                                    if(autoScrollTimer != null) {
                                        autoScrollTimer.cancel();
                                        autoScrollTimer = null;
                                    }

                                    autoScrollLayout.setVisibility(View.VISIBLE);

                                    slide(autoScrollLevel1Btn, 1500, 0);
                                    slide(autoScrollLevel2Btn, 1000, 0);
                                    slide(autoScrollLevel3Btn, 500, 0);
                                    isSlideUp = !isSlideUp;
                                }
                            }
                        });
                    }
                }, 400);
            } else if(motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                float fEndX = motionEvent.getX();
                float fEndY = motionEvent.getY();

                bLong = false;
                if(uiTimer != null) {
                    uiTimer.cancel();
                    uiTimer = null;
                }

                if(fX >= fEndX + 10 || fX <= fEndX - 10 || fY >= fEndY + 10 || fY <= fEndY - 10) {              // 10px 이상 움직였다면
                    return false;
                } else {
                    if(!bShowingAutoscroll)
                        setNextChat();
                }
            } else if(motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                float fEndX = motionEvent.getX();
                float fEndY = motionEvent.getY();

                if(fX >= fEndX + 10 || fX <= fEndX - 10 || fY >= fEndY + 10 || fY <= fEndY - 10) {              // 10px 이상 움직였다면
                    bLong = false;
                    if(uiTimer != null) {
                        uiTimer.cancel();
                        uiTimer = null;
                    }
                    return false;
                }
            }
            return false;
        }
    };

    public void slide(View view, int x, int y)
    {
        TranslateAnimation animation = new TranslateAnimation(
                0,
                0,
                x,
                y);
        animation.setDuration(500);
        animation.setFillAfter(true);
        view.startAnimation(animation);
    }

    @Override
    public void onPause() {
        super.onPause();

        fileObserver.stopWatching();
        lgFileObserver.stopWatching();

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

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                if(nShoingIndex >= 0) {
//                    ChatVO vo = chattingList.get(nShoingIndex);
//                    int nOrder = vo.getnOrder();
//                    sendViewing(nOrder);
//                }
//
//                finish();
//                break;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onResume() {
        super.onResume();
        getInteraction();
        getStarPoint();

        fileObserver.startWatching();
        lgFileObserver.startWatching();

//        commentList = new ArrayList<>();
//        getCommentData();

        if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            getCommentData();
        }
    }

    @Override
    public void onBackPressed() {
        if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return;
        }

        if(nShoingIndex >= 0 && !bPreview) {
            sendViewing(nShoingIndex);
            return;
        } else
            finish();

        super.onBackPressed();
    }

    public void onClickBottomBgView(View view) {

    }

    public void clickCloseBtn(View view) {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public void onClickTopLeftBtn(View view) {
        if(nShoingIndex >= 0) {
            sendViewing(nShoingIndex);
        } else
            finish();
//        Intent intent = new Intent(ViewerActivity.this, EpisodeCommentActivity.class);
//        intent.putExtra("EPISODE_ID", workVO.getEpisodeList().get(nEpisodeIndex).getnEpisodeID());
//        startActivity(intent);
    }

    private void sendTap() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                EpisodeVO vo = workVO.getSortedEpisodeList().get(nEpisodeIndex);
                HttpClient.sendTap(new OkHttpClient(), vo.getnEpisodeID());
            }
        }).start();
    }

    private void sendViewing(final int nOrder) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                EpisodeVO vo = workVO.getSortedEpisodeList().get(nEpisodeIndex);
                HttpClient.sendHitsWork(new OkHttpClient(), workVO.getnWorkID(), vo.getnEpisodeID(), pref.getString("USER_ID", "Guest"), nOrder);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            }
        }).start();
    }

    private void getEpisodeChatData() {
        showingList.clear();
        nameViewList.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                chattingList = HttpClient.getChatDataWithEpisodeID(new OkHttpClient(), "" + workVO.getSortedEpisodeList().get(nEpisodeIndex).getnEpisodeID());
                nameViewList = new ArrayList<>();
                ChatVO vo = new ChatVO();

//                if(!bPreview) {
//                    vo.setType(ChatVO.TYPE_END_COMMENT);
//                    chattingList.add(vo);
//                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(chattingList == null) {
                            Toast.makeText(ViewerActivity.this, "서버와의 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        showingList.clear();
                        nameViewList.clear();
                        bLoading = true;

                        if(nLastOrder > 0) {
                            nShoingIndex = nLastOrder;
                            nLastOrder = 0;
                        }

                            for(int i = 0 ; i <= nShoingIndex ; i++) {
                                if(chattingList.get(i).getType() == ChatVO.TYPE_DISTRACTOR)
                                    continue;

                            showingList.add(chattingList.get(i));
                            nameViewList.add(null);
                        }

                        aa.notifyDataSetChanged();

                        if(nShoingIndex <= 0)
                            Toast.makeText(ViewerActivity.this, "화면을 터치하시면 내용이 진행됩니다.", Toast.LENGTH_SHORT).show();

                        chattingListView.setSelection(aa.getCount() - 1);

                        if (chattingList.size() != 0) {
                            max = chattingList.size();
                            seekBar.setMax(max);
                            seekBar.setProgress(nShoingIndex);
                            if(nShoingIndex == chattingList.size() - 1)
                            {
                                seekBar_value.setText("100");
                            }
                            else
                            {
                                seekBar_value.setText(String.valueOf(seekBar.getProgress() * 100 / max));

                            }
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 9090) {
            if (resultCode == RESULT_OK) {
                nInteraction = data.getIntExtra("RESULT", 1);
                bInteraction = true;
                setEpisodeInteraction(nInteraction);
//                getEpisodeChatDataWithInteraction(nInteraction);
            } else {
                bNext = true;
                nShoingIndex--;
                return;
            }
        }

        bNext = false;
    }

    private void setEpisodeInteraction(final int nIteraction) {
        CommonUtils.showProgressDialog(this, "작품정보를 가져오고 있습니다.");
        new Thread(new Runnable() {
            @Override
            public void run() {
                int nEpisodeID = workVO.getSortedEpisodeList().get(nEpisodeIndex).getnEpisodeID();
                boolean bResult = HttpClient.setEpisodeInteraction(new OkHttpClient(), workVO.getnWorkID(), pref.getString("USER_ID", "Guest"), nIteraction);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();
                        getEpisodeChatDataWithInteraction(nIteraction);
                    }
                });
            }
        }).start();
    }

    private void getEpisodeChatDataWithInteraction(final int nInteraction) {
        CommonUtils.showProgressDialog(this, "작품정보를 가져오고 있습니다.");
        new Thread(new Runnable() {
            @Override
            public void run() {
                int nEpisodeID = workVO.getSortedEpisodeList().get(nEpisodeIndex).getnEpisodeID();
                chattingList = HttpClient.getChatDataWithEpisodeIDAndInteraction(new OkHttpClient(), "" + nEpisodeID, nInteraction);

                ChatVO vo = new ChatVO();

//                if(!bPreview) {
//                    vo.setType(ChatVO.TYPE_END_COMMENT);
//                    chattingList.add(vo);
//                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(chattingList == null) {
                            Toast.makeText(ViewerActivity.this, "서버와의 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(nLastOrder > 0 && nShoingIndex == 0) {
                            showingList.clear();
                            nameViewList.clear();

                            nShoingIndex = nLastOrder;
                            nLastOrder = 0;

                            for(int i = 0 ; i <= nShoingIndex ; i++) {
                                if(chattingList.get(i).getType() == ChatVO.TYPE_DISTRACTOR)
                                    continue;

                                showingList.add(chattingList.get(i));
                                nameViewList.add(null);
                            }
                        }

                        if(bShoingPlus) {
                            nShoingIndex++;
                            bShoingPlus = false;
                            setNextChat();
                        } else {
                            aa.notifyDataSetChanged();
                            chattingListView.setSelection(aa.getCount() - 1);
                        }

                    }
                });
            }
        }).start();
    }

    private void colorChangeAnimation(int nNewColor) {
        ValueAnimator anim = new ValueAnimator();
        anim.setIntValues(nBGColor, nNewColor);
        anim.setEvaluator(new ArgbEvaluator());
        bgBitmap = null;
        bgView.setImageResource(0);
//        bgView.setBackgroundResource(0);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                bgView.setBackgroundColor((Integer)valueAnimator.getAnimatedValue());
            }
        });

        anim.setDuration(300);
        anim.start();

        nBGColor = nNewColor;
    }

    @Override
    public void onDestroy() {
//        workVO = null;
        super.onDestroy();

        fileObserver.stopWatching();
        lgFileObserver.stopWatching();

        if(autoScrollTimer != null) {
            autoScrollTimer.cancel();
            autoScrollTimer = null;
        }
    }

    private void setNextChat() {
        if (!bLoading)
            return;

        bFirst = false;
        if(nShoingIndex >= chattingList.size() - 1) {
            if(bPreview)
                return;

            if(autoScrollTimer != null) {
                Log.d("TOUCH", "자동스크롤을 정지합니다");
                autoScrollTimer.cancel();
                autoScrollTimer = null;
                Toast.makeText(ViewerActivity.this, "자동스크롤을 정지합니다.", Toast.LENGTH_SHORT).show();
            }

            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            TextView moreView = findViewById(R.id.commentMoreView);
            moreView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ViewerActivity.this, EpisodeCommentActivity.class);
                    intent.putExtra("EPISODE_ID", workVO.getSortedEpisodeList().get(nEpisodeIndex).getnEpisodeID());
                    startActivity(intent);
                }
            });

            setComment();

            TextView nextEpisodeBtn = findViewById(R.id.nextEpisodeBtn);
            if(nEpisodeIndex >= workVO.getSortedEpisodeList().size()-1) {         // 마지막 이라면
                nextEpisodeBtn.setVisibility(View.INVISIBLE);
            } else {
                nextEpisodeBtn.setVisibility(View.VISIBLE);
                nextEpisodeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(nEpisodeIndex >= 1 && !CommonUtils.bLocinCheck(pref)) {
                            Toast.makeText(ViewerActivity.this, "로그인이 필요한 기능입니다. 로그인해 주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Intent intent = new Intent(ViewerActivity.this, ViewerActivity.class);
                        intent.putExtra("EPISODE_INDEX", nEpisodeIndex+1);
                        intent.putExtra("INTERACTION", bInteraction);
                        startActivity(intent);
                        finish();
                    }
                });

            }

            return;
        }

        if(bNext) {
            bNext = false;
            return;
        }

        bNext = true;
        nShoingIndex ++;

        if(!bPreview)
            sendTap();

        ChatVO vo = chattingList.get(nShoingIndex);

        if(vo.getType() == ChatVO.TYPE_CHANGE_BG) {
            String strImg = vo.getStrContentsFile();
//            strImg = strImg.replaceAll(" ", "");

            if(!strImg.startsWith("http"))
                strImg = CommonUtils.strDefaultUrl + "images/" + strImg;

            String finalStrImg = strImg;

            showingList.add(chattingList.get(nShoingIndex));
            nameViewList.add(null);
            aa.notifyDataSetChanged();

            chattingListView.setSelection(aa.getCount() - 1);
            bNext = false;

//            Glide.with(ViewerActivity.this)
//                    .asBitmap() // some .jpeg files are actually gif
//                    .load(strImg)
//                    .into(new CustomTarget<Bitmap>() {
//                        @Override
//                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                            bgBitmap = resource;
////                            bgBitmapList.put(finalStrImg, bgBitmap);
//
//                            showingList.add(chattingList.get(nShoingIndex));
//                            nameViewList.add(null);
//                            aa.notifyDataSetChanged();
//
//                            chattingListView.setSelection(aa.getCount() - 1);
//                            bNext = false;
//                        }
//
//                        @Override
//                        public void onLoadCleared(@Nullable Drawable placeholder) {
//                        }
//                    });

            Glide.with(ViewerActivity.this)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(strImg)
                    .transition(BitmapTransitionOptions.withCrossFade(300))
                    .into(bgView);

            return;
        } else if(vo.getType() == ChatVO.TYPE_CHANGE_BG_COLOR){
            try {
                strCurrentBg = "";
                String strColor = vo.getStrContentsFile();
                int nColor = Color.parseColor(strColor);
                colorChangeAnimation(nColor);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(vo.getType() == ChatVO.TYPE_DISTRACTOR) {
            CharacterVO characterVO = vo.getCharacterVO();
            String strContents = vo.getContents();
            String[] contents = strContents.split("╋");

            Intent intent = new Intent(ViewerActivity.this, InteractionPopup.class);
            if(characterVO == null || characterVO.getName() == null || characterVO.getName().equals("null"))
                intent.putExtra("TYPE", 3);
            else {
                if(characterVO.getDirection() == 0) {           // left
                    intent.putExtra("TYPE", 1);
                } else {
                    intent.putExtra("TYPE", 2);
                }

                intent.putExtra("NAME", characterVO.getName());
                intent.putExtra("PHOTO", characterVO.getStrImgFile());
            }

            intent.putExtra("CONTENTS_1", contents[0]);
            intent.putExtra("CONTENTS_2", contents[1]);

            startActivityForResult(intent, 9090);
            nShoingIndex --;
            bShoingPlus = true;
            return;
        }

        showingList.add(chattingList.get(nShoingIndex));
        nameViewList.add(null);
        aa.notifyDataSetChanged();

        chattingListView.setSelection(aa.getCount() - 1);
        bNext = false;

        max = chattingList.size();
        seekBar.setProgress(nShoingIndex);
    }

    public void getInteraction() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(bInteraction && nInteraction == 0) {
                    nInteraction = HttpClient.getEpisodeInteraction(new OkHttpClient(), workVO.getnWorkID(), pref.getString("USER_ID", "Guest"));
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(nInteraction == 0)
                            getEpisodeChatData();
                        else
                            getEpisodeChatDataWithInteraction(nInteraction);
                    }
                });
            }
        }).start();
    }

    public class CChattingArrayAdapter extends ArrayAdapter<Object>
    {
        private LayoutInflater mLiInflater;

        CChattingArrayAdapter(Context context, int layout, List titles)
        {
            super(context, layout, titles);
            mLiInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            ChatVO chatVO = showingList.get(position);
            CharacterVO characterVO = chatVO.getCharacterVO();

            int nType = chatVO.getType();
            int nDirection = 0;

            if(nType == ChatVO.TYPE_TEXT) {
                nDirection = characterVO.getDirection();

                if(nDirection == 0)             // left
                    convertView = mLiInflater.inflate(R.layout.left_chatting_row, parent, false);
                else
                    convertView = mLiInflater.inflate(R.layout.right_chatting_row, parent, false);

                TextView nameView = convertView.findViewById(R.id.nameView);
                nameView.setText(characterVO.getName());

                RelativeLayout contentsLayout = convertView.findViewById(R.id.contentsLayout);
                getPaletteColor(nameView);
                nameViewList.add(position, nameView);

                TextView contentsTextView = convertView.findViewById(R.id.contentsTextView);
                contentsTextView.setText(chatVO.getContents());

                if(characterVO.isbBlackText()) {
                    contentsTextView.setTextColor(getResources().getColor(R.color.colorBlack));
                } else {
                    contentsTextView.setTextColor(getResources().getColor(R.color.colorWhite));
                }

                nameView.setTextSize(nameFontSize);
                contentsTextView.setTextSize(chatFontSize);

                if (fontType == 1) {
                    Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.nn_square_font);
                    nameView.setTypeface(typeface);
                    contentsTextView.setTypeface(typeface);
                } else if (fontType == 2) {
                    Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.nnsquare_round_font);
                    nameView.setTypeface(typeface);
                    contentsTextView.setTypeface(typeface);
                } else if (fontType == 3) {
                    Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.nnmyeongjo_font);
                    nameView.setTypeface(typeface);
                    contentsTextView.setTypeface(typeface);
                } else if (fontType == 4) {
                    Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.misaeng_font);
                    nameView.setTypeface(typeface);
                    contentsTextView.setTypeface(typeface);
                    nameView.setTextSize(nameFontSize + 8);
                    contentsTextView.setTextSize(chatFontSize + 8);
                }

                ImageView imageContentsView = convertView.findViewById(R.id.imageContentsView);

                if(chatVO.getContentsUri() != null) {
                    Glide.with(ViewerActivity.this)
                            .asBitmap()
                            .load(chatVO.getContentsUri())
                            .apply(new RequestOptions().override(800, 800))
                            .into(imageContentsView);
                } else if(chatVO.getStrContentsFile() != null) {
                    String strUrl = characterVO.getStrImgFile();
//                    strUrl = strUrl.replaceAll(" ", "");

                    if(!strUrl.startsWith("http"))
                        strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;

                    Glide.with(ViewerActivity.this)
                            .asBitmap()
                            .load(strUrl)
                            .apply(new RequestOptions().override(800, 800))
                            .into(imageContentsView);
                }

//                if(nDirection == 0) {
//                    contentsLayout.setBackgroundResource(R.drawable.left_sub_balloon);
//                } else {
//                    contentsLayout.setBackgroundResource(R.drawable.right_sub_balloon);
//                }
            } else if(nType == ChatVO.TYPE_SOUND) {
                nDirection = characterVO.getDirection();

                if(nDirection == 0)             // left
                    convertView = mLiInflater.inflate(R.layout.left_audio_row, parent, false);
                else
                    convertView = mLiInflater.inflate(R.layout.right_audio_row, parent, false);

                TextView nameView = convertView.findViewById(R.id.nameView);
                nameView.setText(characterVO.getName());

                getPaletteColor(nameView);
                nameViewList.add(position, nameView);

                nameView.setTextSize(nameFontSize);

                if (fontType == 1) {
                    Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.nn_square_font);
                    nameView.setTypeface(typeface);
                } else if (fontType == 2) {
                    Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.nnsquare_round_font);
                    nameView.setTypeface(typeface);
                } else if (fontType == 3) {
                    Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.nnmyeongjo_font);
                    nameView.setTypeface(typeface);
                } else if (fontType == 4) {
                    Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.misaeng_font);
                    nameView.setTypeface(typeface);
                    nameView.setTextSize(nameFontSize + 8);
                }

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

                        CommonUtils.showProgressDialog(ViewerActivity.this, "음원을 재생중입니다.");

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

//                RelativeLayout contentsLayout = convertView.findViewById(R.id.contentsLayout);
//                if(nDirection == 0) {
//                    contentsLayout.setBackgroundResource(R.drawable.left_sub_balloon);
//                } else {
//                    contentsLayout.setBackgroundResource(R.drawable.right_sub_balloon);
//                }
            } else if(nType == ChatVO.TYPE_IMAGE_NAR) {
                convertView = mLiInflater.inflate(R.layout.narration_image_row, parent, false);
                int nWidth = (int)((float)chattingListView.getWidth() * (float)(1.7f/3.0f));

                ImageView imageContentsView = convertView.findViewById(R.id.imageContentsView);
                imageContentsView.setClipToOutline(true);

                if(chatVO.getContentsUri() != null) {
                    Glide.with(ViewerActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(chatVO.getContentsUri())
                            .apply(new RequestOptions().override(nWidth-50, nWidth-50))
                            .into(imageContentsView);
                } else if(chatVO.getStrContentsFile() != null) {
                    String strUrl = chatVO.getStrContentsFile();
//                    strUrl = strUrl.replaceAll(" ", "");

                    if(!strUrl.startsWith("http"))
                        strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;

                    Glide.with(ViewerActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(strUrl)
                            .apply(new RequestOptions().override(nWidth-50, nWidth-50))
                            .into(imageContentsView);
                }
            } else if(nType == ChatVO.TYPE_IMAGE) {
                nDirection = characterVO.getDirection();

                if(nDirection == 0)             // left
                    convertView = mLiInflater.inflate(R.layout.left_image_row, parent, false);
                else
                    convertView = mLiInflater.inflate(R.layout.right_image_row, parent, false);

                int nWidth = (int)((float)chattingListView.getWidth() * (float)(1.7f/3.0f));
                TextView nameView = convertView.findViewById(R.id.nameView);
                nameView.setText(characterVO.getName());

                getPaletteColor(nameView);
                nameViewList.add(position, nameView);

                nameView.setTextSize(nameFontSize);

                if (fontType == 1) {
                    Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.nn_square_font);
                    nameView.setTypeface(typeface);
                } else if (fontType == 2) {
                    Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.nnsquare_round_font);
                    nameView.setTypeface(typeface);
                } else if (fontType == 3) {
                    Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.nnmyeongjo_font);
                    nameView.setTypeface(typeface);
                } else if (fontType == 4) {
                    Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.misaeng_font);
                    nameView.setTypeface(typeface);
                    nameView.setTextSize(nameFontSize + 8);
                }

                ImageView imageContentsView = convertView.findViewById(R.id.imageContentsView);
                imageContentsView.setClipToOutline(true);

                if(chatVO.getContentsUri() != null) {
                    Glide.with(ViewerActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(chatVO.getContentsUri())
                            .apply(new RequestOptions().override(nWidth-50, nWidth-50))
                            .into(imageContentsView);
                } else if(chatVO.getStrContentsFile() != null) {
                    String strUrl = chatVO.getStrContentsFile();
//                    strUrl = strUrl.replaceAll(" ", "");

                    if(!strUrl.startsWith("http"))
                        strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;

                    Glide.with(ViewerActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(strUrl)
                            .apply(new RequestOptions().override(nWidth-50, nWidth-50))
                            .into(imageContentsView);
                }
            } else if(nType == ChatVO.TYPE_NARRATION) {
                convertView = mLiInflater.inflate(R.layout.narration_chatting_row, parent, false);

                TextView contentsTextView = convertView.findViewById(R.id.contentsTextView);
                contentsTextView.setText(chatVO.getContents());

                contentsTextView.setTextSize(narrFontSize);

                if (fontType == 1) {
                    Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.nn_square_font);
                    contentsTextView.setTypeface(typeface);
                } else if (fontType == 2) {
                    Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.nnsquare_round_font);
                    contentsTextView.setTypeface(typeface);
                } else if (fontType == 3) {
                    Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.nnmyeongjo_font);
                    contentsTextView.setTypeface(typeface);
                } else if (fontType == 4) {
                    Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.misaeng_font);
                    contentsTextView.setTypeface(typeface);
                    contentsTextView.setTextSize(narrFontSize + 8);
                }
            } else if(nType == ChatVO.TYPE_VIDEO) {
                nDirection = characterVO.getDirection();

                if(nDirection == 0)             // left
                    convertView = mLiInflater.inflate(R.layout.left_video_row, parent, false);
                else
                    convertView = mLiInflater.inflate(R.layout.right_video_row, parent, false);

                int nWidth = (int)((float)chattingListView.getWidth() * (float)(1.7f/3.0f));
                TextView nameView = convertView.findViewById(R.id.nameView);
                nameView.setText(characterVO.getName());

                getPaletteColor(nameView);
                nameViewList.add(position, nameView);

                nameView.setTextSize(narrFontSize);

                if (fontType == 1) {
                    Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.nn_square_font);
                    nameView.setTypeface(typeface);
                } else if (fontType == 2) {
                    Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.nnsquare_round_font);
                    nameView.setTypeface(typeface);
                } else if (fontType == 3) {
                    Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.nnmyeongjo_font);
                    nameView.setTypeface(typeface);
                } else if (fontType == 4) {
                    Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.misaeng_font);
                    nameView.setTypeface(typeface);
                    nameView.setTextSize(narrFontSize + 8);
                }

                ImageView imageContentsView = convertView.findViewById(R.id.videoThumbnailView);
                imageContentsView.setClipToOutline(true);
                imageContentsView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String strUrl = chatVO.getStrContentsFile();
                        if(!strUrl.startsWith("http"))
                            strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;

                        Intent intent = new Intent(ViewerActivity.this, VideoPlayerActivity.class);
                        intent.putExtra("VIDEO_URL", strUrl);
                        startActivity(intent);
                    }
                });

                if(chatVO.getContentsUri() != null) {
                    if(thumbBitmapList.containsKey(chatVO.getContentsUri().toString())) {
//                        imageContentsView.setImageBitmap(thumbBitmapList.get(chatVO.getContentsUri().toString()));
                        Glide.with(ViewerActivity.this)
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
                                        Glide.with(ViewerActivity.this)
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
                            Glide.with(ViewerActivity.this)
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
                        Glide.with(ViewerActivity.this)
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
                                        Glide.with(ViewerActivity.this)
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
                TextView contentsTextView = convertView.findViewById(R.id.contentsTextView);
                contentsTextView.setText(chatVO.getContents());

                contentsTextView.setTextSize(narrFontSize);

                if (fontType == 1) {
                    Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.nn_square_font);
                    contentsTextView.setTypeface(typeface);
                } else if (fontType == 2) {
                    Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.nnsquare_round_font);
                    contentsTextView.setTypeface(typeface);
                } else if (fontType == 3) {
                    Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.nnmyeongjo_font);
                    contentsTextView.setTypeface(typeface);
                } else if (fontType == 4) {
                    Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.misaeng_font);
                    contentsTextView.setTypeface(typeface);
                    contentsTextView.setTextSize(narrFontSize + 8);
                }

            } else if(nType == ChatVO.TYPE_CHANGE_BG) {
                convertView = mLiInflater.inflate(R.layout.change_bg_row, parent, false);

                if(bScrolling || bFirst) {
                    if(chatVO.getContentsUri() != null) {
                        if(strCurrentBg.equals(chatVO.getContentsUri().toString()))
                            return convertView;

                        strCurrentBg = chatVO.getContentsUri().toString();
                        Glide.with(ViewerActivity.this)
                                .asBitmap() // some .jpeg files are actually gif
                                .load(chatVO.getContentsUri())
                                .transition(BitmapTransitionOptions.withCrossFade(300))
                                .into(bgView);

                        resetNameViewsColor();
                    } else if(chatVO.getStrContentsFile() != null) {
                        String strUrl = chatVO.getStrContentsFile();

                        if(strCurrentBg.equals(strUrl))
                            return convertView;

                        strCurrentBg = strUrl;
//                    strUrl = strUrl.replaceAll(" ", "");
                        if(!strUrl.startsWith("http"))
                            strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;

                        Glide.with(ViewerActivity.this)
                                .asBitmap() // some .jpeg files are actually gif
                                .load(strUrl)
                                .transition(BitmapTransitionOptions.withCrossFade(300))
                                .into(bgView);

                        resetNameViewsColor();
                    }
                }


//                if(bScrolling || bFirst) {
//                    bFirst = false;
//
//                }
            } else if(nType == ChatVO.TYPE_CHANGE_BG_COLOR) {
                convertView = mLiInflater.inflate(R.layout.change_bg_row, parent, false);

                if(bScrolling || bFirst) {
                    bFirst = false;
                    strCurrentBg = "";
                    int nColor = Color.parseColor(chatVO.getStrContentsFile());
                    colorChangeAnimation(nColor);
                }

                resetNameViewsColor();
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

            ImageView faceView = convertView.findViewById(R.id.faceView);
            TextView nameView = convertView.findViewById(R.id.nameView);
            if(faceView != null) {

                if(characterVO.isbFaceShow()) {
                    faceView.setVisibility(View.VISIBLE);
                    nameView.setVisibility(View.VISIBLE);
                } else {
                    faceView.setVisibility(View.INVISIBLE);
                    nameView.setVisibility(View.GONE);
                }

                int nPlaceHolder = 0;
                int faceIndex = characterVO.getIndex() % 10;
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
                    Glide.with(ViewerActivity.this)
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

                    Glide.with(ViewerActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(strUrl)
                            .placeholder(nPlaceHolder)
                            .apply(new RequestOptions().circleCrop())
                            .into(faceView);
                } else {
                    Glide.with(ViewerActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(nPlaceHolder)
                            .apply(new RequestOptions().circleCrop())
                            .into(faceView);
                }
            }

            TextView commentCountView = convertView.findViewById(R.id.commentCountView);
            commentCountView.setVisibility(View.INVISIBLE);
//            commentCountView.setText(CommonUtils.getPointCount(chatVO.getnCommentCount()));
//            if(chatVO.getnCommentCount() == 0) {
//                commentCountView.setVisibility(View.INVISIBLE);
//            } else {
//                commentCountView.setVisibility(View.VISIBLE);
//            }

            ImageView deleteBtn = convertView.findViewById(R.id.deleteBtn);
            deleteBtn.setVisibility(View.INVISIBLE);

            RelativeLayout addBtn = convertView.findViewById(R.id.addBtn);
            if(addBtn != null)
                addBtn.setVisibility(View.GONE);

            return convertView;
        }
    }

    private void setComment() {
        commentList = new ArrayList<>();
        getCommentData();
    }

    private void resetNameViewsColor() {
        for(int i = 0 ; i < nameViewList.size() ; i++) {
            TextView nameView = nameViewList.get(i);
            if(nameView == null)
                continue;

            getPaletteColor(nameView);
        }
    }

    private void getPaletteColor(TextView nameView) {
//        if(bgBitmap == null) {
//            if(CommonUtils.isColorDark(nBGColor)) {
//                nameView.setTextColor(getResources().getColor(R.color.colorWhite));
//            } else {
//                nameView.setTextColor(getResources().getColor(R.color.colorBlack));
//            }
//        } else {
//            final Palette palette = Palette.from(bgBitmap).generate();
//            Palette.Swatch swatch = palette.getVibrantSwatch();
//
//            if(swatch == null) {
//                int nColor = Color.TRANSPARENT;
//                nColor = palette.getDarkVibrantColor(Color.TRANSPARENT);
//                if(nColor == Color.TRANSPARENT) {
//                    nColor = palette.getLightVibrantColor(nColor);
//                    if(nColor != Color.TRANSPARENT){
//                        nameView.setTextColor(Color.BLACK);
//                    } else {
//                        nColor = palette.getLightMutedColor(nColor);
//                        if(nColor != Color.TRANSPARENT){
//                            nameView.setTextColor(Color.BLACK);
//                        } else {
//                            nameView.setTextColor(Color.WHITE);
//                        }
//                    }
//                } else {
//                    nameView.setTextColor(Color.WHITE);
//                }
//            } else {
//                int nColor = swatch.getRgb();
//                if(CommonUtils.isColorDark(nColor)) {
//                    nameView.setTextColor(getResources().getColor(R.color.colorWhite));
//                } else {
//                    nameView.setTextColor(getResources().getColor(R.color.colorBlack));
//                }
//            }
//        }
    }

    private void getStarPoint() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject resultObject = HttpClient.getEpisodeCommnet(new OkHttpClient(), workVO.getSortedEpisodeList().get(nEpisodeIndex).getnEpisodeID(), 1, pref.getString("USER_ID", "Guest"));

                if (resultObject != null) {
                    try {
                        fStarPoint = (float)resultObject.getDouble("STAR_POINT");
                        nStarCount = resultObject.getInt("STAR_COUNT");
                        fMyPoint = (float)resultObject.getDouble("MY_POINT");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RatingBar smallRatingBar = findViewById(R.id.smallRatingBar2);
                        TextView starPointView = findViewById(R.id.starPointView2);
                        TextView starCountView = findViewById(R.id.starCountView2);

                        smallRatingBar.setRating(fStarPoint);
                        starPointView.setText(String.format("%.1f", fStarPoint));
                        starCountView.setText("(" + nStarCount + "명)");

                        TextView rightView = findViewById(R.id.rightView2);
                        rightView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                boolean bLogin = CommonUtils.bLocinCheck(pref);

                                if(!bLogin) {
                                    Toast.makeText(ViewerActivity.this, "로그인이 필요한 기능입니다. 로그인 해주세요.", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ViewerActivity.this, PanbookLoginActivity.class));
                                    return;
                                }

                                if(fMyPoint > 0) {
                                    Toast.makeText(ViewerActivity.this, "이미 평가한 작품입니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                Intent intent = new Intent(ViewerActivity.this, StarPointPopup.class);
                                intent.putExtra("EPISODE_ID", workVO.getSortedEpisodeList().get(nEpisodeIndex).getnEpisodeID());
                                startActivity(intent);
                            }
                        });
                    }
                });
            }
        }).start();
    }

    private void getCommentData() {
        if(bGetComment)
            return;

        bGetComment = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bGetComment = false;
            }
        }, 1000);

        CommonUtils.showProgressDialog(this, "댓글 목록을 가져오고 있습니다. 잠시만 기다려주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                commentList.clear();
                JSONObject resultObject = HttpClient.getEpisodeCommnet(new OkHttpClient(), workVO.getSortedEpisodeList().get(nEpisodeIndex).getnEpisodeID(), 1, pref.getString("USER_ID", "Guest"));

                if(resultObject != null) {
                    try {
                        JSONArray resultArray = resultObject.getJSONArray("COMMENT_LIST");

                        for(int i = 0 ; i < resultArray.length() ; i++) {
                            JSONObject object = resultArray.getJSONObject(i);
                            CommentVO vo = new CommentVO();
                            vo.setCommentID(object.getInt("COMMENT_ID"));
                            vo.setEpisodeID(object.getInt("EPISODE_ID"));
                            vo.setParentID(object.getInt("PARENT_ID"));
                            vo.setStrComment(object.getString("COMMENT"));
                            vo.setRegisterDate(object.getString("REGISTER_DATE"));
                            vo.setUserName(object.getString("USER_NAME"));
                            vo.setUserPhoto(object.getString("USER_PHOTO"));
                            vo.setChatID(object.getInt("CHAT_ID"));
                            vo.setUserID(object.getString("USER_ID"));
                            vo.setLikeCount(object.getInt("LIKE_COUNT"));

                            commentList.add(vo);
                        }

                        fStarPoint = (float)resultObject.getDouble("STAR_POINT");
                        nStarCount = resultObject.getInt("STAR_COUNT");
                        fMyPoint = (float)resultObject.getDouble("MY_POINT");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        RatingBar smallRatingBar = findViewById(R.id.smallRatingBar);
                        TextView starPointView = findViewById(R.id.starPointView);
                        TextView starCountView = findViewById(R.id.starCountView);

                        smallRatingBar.setRating(fStarPoint);
                        starPointView.setText(String.format("%.1f", fStarPoint));
                        starCountView.setText("(" + nStarCount + "명)");

                        TextView rightView = findViewById(R.id.rightView);
                        rightView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                boolean bLogin = CommonUtils.bLocinCheck(pref);

                                if(!bLogin) {
                                    Toast.makeText(ViewerActivity.this, "로그인이 필요한 기능입니다. 로그인 해주세요.", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ViewerActivity.this, PanbookLoginActivity.class));
                                    return;
                                }

                                if(fMyPoint > 0) {
                                    Toast.makeText(ViewerActivity.this, "이미 평가한 작품입니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                Intent intent = new Intent(ViewerActivity.this, StarPointPopup.class);
                                intent.putExtra("EPISODE_ID", workVO.getSortedEpisodeList().get(nEpisodeIndex).getnEpisodeID());
                                startActivity(intent);
                            }
                        });

                        LinearLayout container = findViewById(R.id.commentContainer);
                        TextView noCommentView = findViewById(R.id.noCommentView);
                        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                        if(viewList != null) {
                            for(int i = 0 ; i < viewList.size() ; i++) {
                                container.removeView(viewList.get(i));
                            }
                        }

                        viewList = new ArrayList<>();
                        if(commentList != null && commentList.size() > 0) {
                            noCommentView.setVisibility(View.GONE);

                            for(int i = 0 ; i < commentList.size() ; i ++) {
                                if(i >= 2)
                                    break;

                                CommentVO vo = commentList.get(i);

                                View view = inflater.inflate(R.layout.chat_comment_row, null);
                                RelativeLayout bgView = view.findViewById(R.id.bgView);
                                ImageView faceView = view.findViewById(R.id.faceView);
                                TextView nameView = view.findViewById(R.id.nameView);
                                TextView dateView = view.findViewById(R.id.dateTimeView);
                                TextView commentView = view.findViewById(R.id.commentView);
                                LinearLayout likeLayout = view.findViewById(R.id.likeLayout);
                                TextView likeCountView = view.findViewById(R.id.likeCountView);
                                TextView episodeNumView = view.findViewById(R.id.episodeNumView);
                                ImageView thumbIconView = view.findViewById(R.id.thumbIconView);
                                ImageView arrowBtn = view.findViewById(R.id.arrowBtn);
                                TextView reportBtn = view.findViewById(R.id.reportBtn);
                                TextView replyBtn = view.findViewById(R.id.replyBtn);

                                if(vo.isHasChild()) {
                                    arrowBtn.setVisibility(View.VISIBLE);
                                } else {
                                    arrowBtn.setVisibility(View.INVISIBLE);
                                }

                                if(vo.getUserID().equals(pref.getString("USER_ID", "Guest")) || pref.getString("ADMIN", "N").equals("Y")) {
                                    reportBtn.setText("삭제");
                                } else {
                                    reportBtn.setText("신고");
                                }

                                String strPhoto = vo.getUserPhoto();
                                if(strPhoto != null && !strPhoto.equals("null") && !strPhoto.equals("NULL") && strPhoto.length() > 0) {
                                    if(!strPhoto.startsWith("http"))
                                        strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

                                    Glide.with(ViewerActivity.this)
                                            .asBitmap() // some .jpeg files are actually gif
                                            .load(strPhoto)
                                            .apply(new RequestOptions().circleCrop())
                                            .into(faceView);
                                } else {
                                    Glide.with(ViewerActivity.this)
                                            .asBitmap() // some .jpeg files are actually gif
                                            .load(R.drawable.user_icon)
                                            .apply(new RequestOptions().circleCrop())
                                            .into(faceView);
                                }

                                nameView.setText(vo.getUserName());
                                dateView.setText(CommonUtils.strGetTime(vo.getRegisterDate()));
                                commentView.setText(vo.getStrComment());
                                container.addView(view);
                                viewList.add(view);

                                /*
                                likeCountView.setText(vo.getLikeCount() + "");

                                if(vo.isMyComment()) {
                                    likeLayout.setBackgroundResource(R.drawable.round_blue_btn_bg);
                                    likeCountView.setTextColor(Color.parseColor("#ffffff"));
                                    thumbIconView.setBackgroundResource(R.drawable.white_like_box);
                                } else {
                                    likeLayout.setBackgroundResource(R.drawable.badge_complete);
                                    likeCountView.setTextColor(ContextCompat.getColor(ViewerActivity.this, R.color.colorPrimary));
                                    thumbIconView.setBackgroundResource(R.drawable.like_box);
                                }


                                if(vo.getUserID().equals(pref.getString("USER_ID", "Guest"))) {
                                    replyBtn.setVisibility(View.GONE);
                                } else {
                                    replyBtn.setVisibility(View.VISIBLE);
                                }
                                */


                            }
                        } else {
                            noCommentView.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }).start();
    }

    public void onClickStar(View view) {
        boolean bLogin = CommonUtils.bLocinCheck(pref);

        if(!bLogin) {
            Toast.makeText(ViewerActivity.this, "로그인이 필요한 기능입니다. 로그인 해주세요.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ViewerActivity.this, PanbookLoginActivity.class));
            return;
        }

        if(fMyPoint > 0) {
            Toast.makeText(ViewerActivity.this, "이미 평가한 작품입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(ViewerActivity.this, StarPointPopup.class);
        intent.putExtra("EPISODE_ID", workVO.getSortedEpisodeList().get(nEpisodeIndex).getnEpisodeID());
        startActivity(intent);
    }

    public void onClickAutoScrolLevel1(View view) {


        startAutoScroll(1);
    }

    public void onClickAutoScrolLevel2(View view) {

        startAutoScroll(2);
    }

    public void onClickAutoScrolLevel3(View view) {

        startAutoScroll(3);
    }

    private void startAutoScroll(int nLevel) {
        if (!bShowingAutoscroll)
            return;

        if(autoScrollTimer != null) {
            autoScrollTimer.cancel();
            autoScrollTimer = null;
        }

        nAutoLevel = nLevel;
        int nRepeat = 0;
        if(nAutoLevel == 1) {
            nRepeat = 2000;
        } else if(nAutoLevel == 2) {
            nRepeat = 1000;
        } else if(nAutoLevel == 3) {
            nRepeat = 800;
        }

        autoScrollTimer = new Timer();
        autoScrollTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setNextChat();
                    }
                });
            }
        }, nRepeat, nRepeat);

//        Toast.makeText(this, "자동 스크롤을 시작합니다.", Toast.LENGTH_SHORT).show();
//        autoScrollLayout.setVisibility(View.INVISIBLE);
        slide(autoScrollLevel1Btn, 0, 1500);
        slide(autoScrollLevel2Btn, 0, 1000);
        slide(autoScrollLevel3Btn, 0, 500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                autoScrollLayout.setVisibility(View.INVISIBLE);
            }
        }, 500);
        bShowingAutoscroll = false;
    }

    public void onClickPreviousEpisode(View view) {

        if(nEpisodeIndex == 0)
            return;

        Intent intent = new Intent(ViewerActivity.this, ViewerActivity.class);
        intent.putExtra("EPISODE_INDEX", nEpisodeIndex-1);
        intent.putExtra("INTERACTION", bInteraction);
        startActivity(intent);
        finish();
    }

    public void onClickNextEpisode(View view) {

        if(nEpisodeIndex >= workVO.getSortedEpisodeList().size()-1)
            return;

        EpisodeVO episodeVO = workVO.getSortedEpisodeList().get(nEpisodeIndex + 1);
        if(workVO.getnInteractionEpisodeID() > 0 && episodeVO.getnEpisodeID() > workVO.getnInteractionEpisodeID()) {                // 클릭한 에피소드가 분기보다 위의 에피소드 라면. 즉, 분기 이후의 에피소드 라면
            checkInteractionSelect(nEpisodeIndex + 1);
            return;
        }

        Intent intent = new Intent(ViewerActivity.this, ViewerActivity.class);
        intent.putExtra("EPISODE_INDEX", nEpisodeIndex+1);
        intent.putExtra("INTERACTION", bInteraction);
        startActivity(intent);
        finish();
    }

    public void onClickComment(View view) {
        Intent intent = new Intent(ViewerActivity.this, EpisodeCommentActivity.class);
        intent.putExtra("EPISODE_ID", workVO.getSortedEpisodeList().get(nEpisodeIndex).getnEpisodeID());
        startActivity(intent);
    }

    public void onClickScroll(View view) {
        uiTimer = new Timer();
        uiTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                            bShowingAutoscroll = true;
                            if(autoScrollTimer != null) {
                                autoScrollTimer.cancel();
                                autoScrollTimer = null;
                            }

                            autoScrollLayout.setVisibility(View.VISIBLE);
                            navBar.setVisibility(View.INVISIBLE);

                            slide(autoScrollLevel1Btn, 1500, 0);
                            slide(autoScrollLevel2Btn, 1000, 0);
                            slide(autoScrollLevel3Btn, 500, 0);
                            isSlideUp = !isSlideUp;
                    }
                });
            }
        }, 400);
    }

    public void onClickCarrotBtn(View view) {
        if (workVO.getnWriterID().equals(pref.getString("USER_ID", "Guest"))) {
            CommonUtils.makeText(this, "내 작품에는 후원 하실수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, CarrotDoneActivity.class);
        intent.putExtra("WORK_ID", workVO.getnWorkID());
        startActivity(intent);
    }

    public class EpisodeListAdapter extends RecyclerView.Adapter<EpisodeListAdapter.EpisodeListViewHolder> {
        private ArrayList<EpisodeVO> itemsList;
        private Activity mContext;

        public EpisodeListAdapter(Activity context, ArrayList<EpisodeVO> itemsList) {
            this.mContext = context;
            this.itemsList = itemsList;
        }

        @Override
        public EpisodeListViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.work_main_episode_row, null);

            return new EpisodeListViewHolder(v);
        }

        @Override
        public void onBindViewHolder(EpisodeListViewHolder holder, int position) {
            if (position >= episodeList.size())
                return;

                int nIndex = position;
                EpisodeVO vo = workVO.getEpisodeList().get(nIndex);

                TextView episodeTitleView = holder.itemView.findViewById(R.id.episodeTitleView);
                TextView postAvailableView = holder.itemView.findViewById(R.id.postAvailableView);

                TextView dateTimeView = holder.itemView.findViewById(R.id.dateTimeView);
                TextView startPointView = holder.itemView.findViewById(R.id.startPointView);
                TextView hitsCountView = holder.itemView.findViewById(R.id.hitsCountView);
                TextView commentCountView = holder.itemView.findViewById(R.id.commentCountView);
//                LinearLayout chatCountLayout = holder.itemView.findViewById(R.id.chatCountLayout);
                TextView chatCountView = holder.itemView.findViewById(R.id.chatCountView);
//                TextView tabCountView = holder.itemView.findViewById(R.id.tabCountView);

                episodeTitleView.setText(vo.getStrTitle());
                dateTimeView.setText(vo.getStrDate().substring(0, 10));
                startPointView.setText(String.format("%.1f", vo.getfStarPoint()));
                hitsCountView.setText(CommonUtils.getPointCount(vo.getnTapCount()));
                commentCountView.setText(CommonUtils.getPointCount(vo.getnCommentCount()));

                ImageView menuBtn = holder.itemView.findViewById(R.id.menuBtn);

                postAvailableView.setVisibility(View.GONE);
                menuBtn.setVisibility(View.GONE);

                chatCountView.setText("" + vo.getnChatCount());
//                tabCountView.setText("" + vo.getnTapCount());
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return (null != itemsList ? itemsList.size() : 0);
        }

        public class EpisodeListViewHolder extends RecyclerView.ViewHolder {
            public EpisodeListViewHolder(View view) {
                super(view);

                view.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                if (isClickedList)
                                    return false;
                                fX = motionEvent.getX();
                                fY = motionEvent.getY();
                                break;
                            case MotionEvent.ACTION_MOVE: {
                                float fEndX = motionEvent.getX();
                                float fEndY = motionEvent.getY();

                                if (fX >= fEndX + 10 || fX <= fEndX - 10 || fY >= fEndY + 10 || fY <= fEndY - 10) {              // 10px 이상 움직였다면
                                    return false;
                                }
                                break;
                            }
                            case MotionEvent.ACTION_CANCEL:
                                return false;
                            case MotionEvent.ACTION_UP: {
                                float fEndX = motionEvent.getX();
                                float fEndY = motionEvent.getY();

                                if (fX >= fEndX + 10 || fX <= fEndX - 10 || fY >= fEndY + 10 || fY <= fEndY - 10) {              // 10px 이상 움직였다면
                                    return false;
                                } else {
                                    int nPosition = getAdapterPosition();

                                        EpisodeVO episodeVO = workVO.getEpisodeList().get(nPosition);
                                        if(workVO.getnInteractionEpisodeID() > 0 && episodeVO.getnEpisodeID() > workVO.getnInteractionEpisodeID()) {                // 클릭한 에피소드가 분기보다 위의 에피소드 라면. 즉, 분기 이후의 에피소드 라면
                                            checkInteractionSelect(episodeList.size() - nPosition - 1);
                                            return false;
                                        }

                                    Intent intent = new Intent(ViewerActivity.this, ViewerActivity.class);
                                    intent.putExtra("EPISODE_INDEX", episodeList.size() - nPosition - 1);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                            break;
                        }
                        return true;
                    }
                });
            }
        }
    }

    private void checkInteractionSelect(final int nIndex) {
        CommonUtils.showProgressDialog(this, "서버와 통신중입니다. 잠시만 기다려주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.checkInteractionSelect(new OkHttpClient(), pref.getString("USER_ID", "Guest"), workVO.getnWorkID());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(!bResult) {
                            CommonUtils.makeText(ViewerActivity.this, "분기를 선택하지 않으셨습니다.  작품의 분기를 선택해주세요.", Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            Intent intent = new Intent(ViewerActivity.this, ViewerActivity.class);
                            intent.putExtra("EPISODE_INDEX", nIndex);
                            intent.putExtra("INTERACTION", true);
                            startActivity(intent);
                        }
                    }
                });
            }
        }).start();
    }
    void setFont0()
    {
        fontCk0.setVisibility(View.VISIBLE);
        fontCk1.setVisibility(View.INVISIBLE);
        fontCk2.setVisibility(View.INVISIBLE);
        fontCk3.setVisibility(View.INVISIBLE);

    }
    void setFont1()
    {
        fontCk0.setVisibility(View.INVISIBLE);
        fontCk1.setVisibility(View.VISIBLE);
        fontCk2.setVisibility(View.INVISIBLE);
        fontCk3.setVisibility(View.INVISIBLE);

    }
    void setFont2()
    {
        fontCk0.setVisibility(View.INVISIBLE);
        fontCk1.setVisibility(View.INVISIBLE);
        fontCk2.setVisibility(View.VISIBLE);
        fontCk3.setVisibility(View.INVISIBLE);

    }
    void setFont3()
    {
        fontCk0.setVisibility(View.INVISIBLE);
        fontCk1.setVisibility(View.INVISIBLE);
        fontCk2.setVisibility(View.INVISIBLE);
        fontCk3.setVisibility(View.VISIBLE);

    }
    public void onClickFont1(View view) {
        fontType = 1;
        // check visible
        setFont0();

        aa.notifyDataSetChanged();
    }

    public void onClickFont2(View view) {
        fontType = 2;
        // check visible
        setFont1();
        aa.notifyDataSetChanged();
    }

    public void onClickFont3(View view) {
        fontType = 3;
        // check visible
        setFont2();

        aa.notifyDataSetChanged();
    }

    public void onClickFont4(View view) {
        fontType = 4;
        setFont3();

        aa.notifyDataSetChanged();
    }

    public void onClickIncreaseFontSize(View view) {
        if (chatFontSize > 18 && narrFontSize > 18 && nameFontSize > 17)
            return;

        chatFontSize ++;
        narrFontSize ++;
        nameFontSize ++;

        aa.notifyDataSetChanged();
    }

    public void onClickDecreaseFontSize(View view) {
        if (chatFontSize < 11 && narrFontSize < 11 && nameFontSize < 10)
            return;

        chatFontSize --;
        narrFontSize --;
        nameFontSize --;

        aa.notifyDataSetChanged();
    }
}
