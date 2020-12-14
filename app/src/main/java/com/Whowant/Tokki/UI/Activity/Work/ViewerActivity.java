package com.Whowant.Tokki.UI.Activity.Work;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Login.PanbookLoginActivity;
import com.Whowant.Tokki.UI.Activity.Media.VideoPlayerActivity;
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
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.load.resource.bitmap.VideoBitmapDecoder;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
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

    private BottomSheetBehavior bottomSheetBehavior;
    private ArrayList<TextView> nameViewList = new ArrayList<>();
    private boolean bGetComment = false;

    private Timer autoScrollTimer = null;
    private int   nAutoLevel = -1;
    private LinearLayout autoScrollLayout;
    private RelativeLayout autoScrollLevel1Btn, autoScrollLevel2Btn, autoScrollLevel3Btn;

    // Animation animation, animation2;

    boolean isSlideUp;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_viewer);

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

        if(workVO.getEpisodeList() == null) {
            Toast.makeText(this, "작품을 읽어오는 중 문제가 발생했습니다. 다시 로딩해 주세요.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView titleView = findViewById(R.id.titleView);
        titleView.setText(workVO.getEpisodeList().get(nEpisodeIndex).getStrTitle());

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
                        }
                    }, 500);
//                        autoScrollLayout.setVisibility(View.INVISIBLE);
                    bShowingAutoscroll = false;
                    return false;
                }

                if(autoScrollTimer != null) {
                    autoScrollTimer.cancel();
                    autoScrollTimer = null;
                    Toast.makeText(ViewerActivity.this, "자동 스크롤을 정지합니다.", Toast.LENGTH_SHORT).show();
                    return false;
                }

                bLong = true;

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

        fileObserver.startWatching();

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
                EpisodeVO vo = workVO.getEpisodeList().get(nEpisodeIndex);
                HttpClient.sendTap(new OkHttpClient(), vo.getnEpisodeID());
            }
        }).start();
    }

    private void sendViewing(final int nOrder) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                EpisodeVO vo = workVO.getEpisodeList().get(nEpisodeIndex);
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
                chattingList = HttpClient.getChatDataWithEpisodeID(new OkHttpClient(), "" + workVO.getEpisodeList().get(nEpisodeIndex).getnEpisodeID());
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
                int nEpisodeID = workVO.getEpisodeList().get(nEpisodeIndex).getnEpisodeID();
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
                int nEpisodeID = workVO.getEpisodeList().get(nEpisodeIndex).getnEpisodeID();
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

        if(autoScrollTimer != null) {
            autoScrollTimer.cancel();
            autoScrollTimer = null;
        }
    }

    private void setNextChat() {
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
                    intent.putExtra("EPISODE_ID", workVO.getEpisodeList().get(nEpisodeIndex).getnEpisodeID());
                    startActivity(intent);
                }
            });

            setComment();

            TextView nextEpisodeBtn = findViewById(R.id.nextEpisodeBtn);
            if(nEpisodeIndex >= workVO.getEpisodeList().size()-1) {         // 마지막 이라면
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
                JSONObject resultObject = HttpClient.getEpisodeCommnet(new OkHttpClient(), workVO.getEpisodeList().get(nEpisodeIndex).getnEpisodeID(), 1, pref.getString("USER_ID", "Guest"));

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
                                intent.putExtra("EPISODE_ID", workVO.getEpisodeList().get(nEpisodeIndex).getnEpisodeID());
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
                                if(i >= 3)
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
        intent.putExtra("EPISODE_ID", workVO.getEpisodeList().get(nEpisodeIndex).getnEpisodeID());
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

        Toast.makeText(this, "자동 스크롤을 시작합니다.", Toast.LENGTH_SHORT).show();
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
}
