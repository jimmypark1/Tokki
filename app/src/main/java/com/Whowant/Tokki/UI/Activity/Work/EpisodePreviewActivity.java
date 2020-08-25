package com.Whowant.Tokki.UI.Activity.Work;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Media.VideoPlayerActivity;
import com.Whowant.Tokki.UI.Popup.InteractionPopup;
import com.Whowant.Tokki.UI.Popup.StarPointPopup;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.CustomUncaughtExceptionHandler;
import com.Whowant.Tokki.VO.CharacterVO;
import com.Whowant.Tokki.VO.ChatVO;
import com.Whowant.Tokki.VO.CommentVO;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;

public class EpisodePreviewActivity extends AppCompatActivity {
    public static WorkVO workVO;
    private ArrayList<ChatVO> chattingList;
    private ArrayList<ChatVO> showingList;
    private int nShoingIndex = -1;
    private boolean bShoingPlus = false;

    private ListView chattingListView;
    private ImageView bgView;
    private CChattingArrayAdapter aa;
    private boolean bScroll = false;
    private float fX;
    private float fY;

    private int nEpisodeIndex;
    private int nBGColor;

    private SharedPreferences pref;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Timer timer;
    private boolean bNext = false;
    private float fStarPoint = 0;
    private int   nStarCount = 0;
    private ArrayList<CommentVO> commentList;
    private int nInteraction = 0;
    private boolean bInteraction = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Thread.UncaughtExceptionHandler handler = Thread
                .getDefaultUncaughtExceptionHandler();

        if (!(handler instanceof CustomUncaughtExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
        }

        chattingList = new ArrayList<>();
        nBGColor = getResources().getColor(R.color.colorDefaultBG);
        pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

        nEpisodeIndex = getIntent().getIntExtra("EPISODE_INDEX", 0);
        bInteraction = getIntent().getBooleanExtra("INTERACTION", false);

        chattingListView = findViewById(R.id.chattingListView);

        actionBar.setTitle("미리보기");

        bgView = findViewById(R.id.bgView);
        chattingListView = findViewById(R.id.listView);
        chattingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                setNextChat();
            }
        });
        chattingListView.setOnTouchListener(new View.OnTouchListener() {
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
                        setNextChat();
                    }
                }
                return false;
            }
        });

        showingList = new ArrayList<>();
        aa = new CChattingArrayAdapter(this, R.layout.left_chatting_row, showingList);
        chattingListView.setAdapter(aa);

//        sendViewing();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        getInteraction();
    }

    public void getInteraction() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(bInteraction) {
                    nInteraction = HttpClient.getEpisodeInteraction(new OkHttpClient(), workVO.getnWorkID(), pref.getString("USER_ID", "Guest"));
                } else {
                    nInteraction = 0;
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

    private void getEpisodeChatData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                chattingList = HttpClient.getChatDataWithEpisodeID(new OkHttpClient(), "" + workVO.getEpisodeList().get(nEpisodeIndex).getnEpisodeID());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(chattingList == null) {
                            Toast.makeText(EpisodePreviewActivity.this, "서버와의 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        showingList.clear();

                        for(int i = 0 ; i <= nShoingIndex ; i++) {
                            if(chattingList.get(i).getType() == ChatVO.TYPE_CHANGE_BG || chattingList.get(i).getType() == ChatVO.TYPE_CHANGE_BG_COLOR ||
                                    chattingList.get(i).getType() == ChatVO.TYPE_DISTRACTOR)
                                continue;

                            showingList.add(chattingList.get(i));
                        }

                        aa.notifyDataSetChanged();

                        if(nShoingIndex <= 0)
                            Toast.makeText(EpisodePreviewActivity.this, "화면을 터치하시면 내용이 진행됩니다.", Toast.LENGTH_LONG).show();
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
                getEpisodeChatDataWithInteraction(nInteraction);
            }
        }

        bNext = false;
    }

    private void getEpisodeChatDataWithInteraction(final int nInteraction) {
//        chattingList.clear();
        CommonUtils.showProgressDialog(this, "작품정보를 가져오고 있습니다.");
        new Thread(new Runnable() {
            @Override
            public void run() {
                int nEpisodeID = chattingList.get(0).getnEpisodeID();
                chattingList = HttpClient.getChatDataWithEpisodeIDAndInteraction(new OkHttpClient(), "" + nEpisodeID, nInteraction);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(chattingList == null) {
                            Toast.makeText(EpisodePreviewActivity.this, "서버와의 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(bShoingPlus) {
                            nShoingIndex++;
                            bShoingPlus = false;
                            setNextChat();
                        } else {
                            aa.notifyDataSetChanged();
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
        chattingList = null;
        super.onDestroy();
    }

    private void setNextChat() {
        if(nShoingIndex >= chattingList.size() - 1) {
            Toast.makeText(EpisodePreviewActivity.this, "마지막 입니다.", Toast.LENGTH_LONG).show();
            return;
        }

        if(bNext)
            return;

        bNext = true;

        nShoingIndex ++;

        ChatVO vo = chattingList.get(nShoingIndex);

        if(vo.getType() == ChatVO.TYPE_CHANGE_BG) {
            String strImg = vo.getStrContentsFile();
//            strImg = strImg.replaceAll(" ", "");

            if(!strImg.startsWith("http"))
                strImg = CommonUtils.strDefaultUrl + "images/" + strImg;

            Glide.with(EpisodePreviewActivity.this)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(strImg)
                    .transition(BitmapTransitionOptions.withCrossFade())
                    .into(bgView);
            bNext = false;
            return;
        } else if(vo.getType() == ChatVO.TYPE_CHANGE_BG_COLOR){
            try {
                String strColor = vo.getStrContentsFile();
                int nColor = Color.parseColor(strColor);
                colorChangeAnimation(nColor);
            } catch (Exception e) {
                e.printStackTrace();
            }

            bNext = false;
            return;
        } else if(vo.getType() == ChatVO.TYPE_DISTRACTOR) {
            CharacterVO characterVO = vo.getCharacterVO();
            String strContents = vo.getContents();
            String[] contents = strContents.split("╋");

            Intent intent = new Intent(EpisodePreviewActivity.this, InteractionPopup.class);
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
        aa.notifyDataSetChanged();

        chattingListView.setSelection(aa.getCount() - 1);
        bNext = false;
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

            if(nType == ChatVO.TYPE_TEXT) {
                int nDirection = characterVO.getDirection();

                if(nDirection == 0)             // left
                    convertView = mLiInflater.inflate(R.layout.left_chatting_row, parent, false);
                else
                    convertView = mLiInflater.inflate(R.layout.right_chatting_row, parent, false);

                TextView nameView = convertView.findViewById(R.id.nameView);
                nameView.setText(characterVO.getName());

                ImageView faceView = convertView.findViewById(R.id.faceView);

                if(characterVO.getImage() != null && !characterVO.getImage().equals("null")) {
                    Glide.with(EpisodePreviewActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(characterVO.getImage())
                            .apply(new RequestOptions().circleCrop())
                            .into(faceView);
                } else if(characterVO.getStrImgFile() != null && !characterVO.getStrImgFile().equals("null")) {
                    String strUrl = characterVO.getStrImgFile();
//                    strUrl = strUrl.replaceAll(" ", "");

                    if(!strUrl.startsWith("http"))
                        strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;

                    Glide.with(EpisodePreviewActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(strUrl)
                            .apply(new RequestOptions().circleCrop())
                            .into(faceView);
                } else {
                    faceView.setImageResource(0);
                }

                TextView contentsTextView = convertView.findViewById(R.id.contentsTextView);
                contentsTextView.setText(chatVO.getContents());

                if(characterVO.isbBlackText()) {
                    contentsTextView.setTextColor(getResources().getColor(R.color.colorBlack));
                } else {
                    contentsTextView.setTextColor(getResources().getColor(R.color.colorWhite));
                }

                ImageView imageContentsView = convertView.findViewById(R.id.imageContentsView);

                if(chatVO.getContentsUri() != null) {
                    Glide.with(EpisodePreviewActivity.this)
                            .asBitmap()
                            .load(chatVO.getContentsUri())
                            .apply(new RequestOptions().override(800, 800))
                            .into(imageContentsView);
                } else if(chatVO.getStrContentsFile() != null) {
                    String strUrl = characterVO.getStrImgFile();
//                    strUrl = strUrl.replaceAll(" ", "");

                    if(!strUrl.startsWith("http"))
                        strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;

                    Glide.with(EpisodePreviewActivity.this)
                            .asBitmap()
                            .load(strUrl)
                            .apply(new RequestOptions().override(800, 800))
                            .into(imageContentsView);
                }
            } else if(nType == ChatVO.TYPE_SOUND) {
                int nDirection = characterVO.getDirection();

                if(nDirection == 0)             // left
                    convertView = mLiInflater.inflate(R.layout.left_audio_row, parent, false);
                else
                    convertView = mLiInflater.inflate(R.layout.right_audio_row, parent, false);

                TextView nameView = convertView.findViewById(R.id.nameView);
                nameView.setText(characterVO.getName());

                final ProgressBar pb = convertView.findViewById(R.id.progressBar);

                ImageView faceView = convertView.findViewById(R.id.faceView);

                if(characterVO.getImage() != null && !characterVO.getImage().equals("null")) {
                    Glide.with(EpisodePreviewActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(characterVO.getImage())
                            .apply(new RequestOptions().circleCrop())
                            .into(faceView);
                } else if(characterVO.getStrImgFile() != null && !characterVO.getStrImgFile().equals("null")) {
                    String strUrl = characterVO.getStrImgFile();
//                    strUrl = strUrl.replaceAll(" ", "");

                    if(!strUrl.startsWith("http"))
                        strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;

                    Glide.with(EpisodePreviewActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(strUrl)
                            .apply(new RequestOptions().circleCrop())
                            .into(faceView);
                } else {
                    faceView.setImageResource(0);
                }

                ImageView playBtn = convertView.findViewById(R.id.playBtn);
                playBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(mediaPlayer != null && mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                            timer.cancel();
                            timer = null;
                            return;
                        }

                        String strUrl = chatVO.getStrContentsFile();
//                        strUrl = strUrl.replaceAll(" ", "");

                        if(!strUrl.startsWith("http"))
                            strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;

                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        try {
                            mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(strUrl));
                            mediaPlayer.setLooping(false);
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                public void onPrepared(MediaPlayer mp) {
                                    int nDuration = mediaPlayer.getDuration();
                                    pb.setMax(nDuration);
                                    mp.start();

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

                            mediaPlayer.prepareAsync();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
            } else if(nType == ChatVO.TYPE_IMAGE) {
                int nDirection = characterVO.getDirection();

                if(nDirection == 0)             // left
                    convertView = mLiInflater.inflate(R.layout.left_image_row, parent, false);
                else
                    convertView = mLiInflater.inflate(R.layout.right_image_row, parent, false);

                TextView nameView = convertView.findViewById(R.id.nameView);
                nameView.setText(characterVO.getName());

                ImageView imageContentsView = convertView.findViewById(R.id.imageContentsView);
                imageContentsView.setClipToOutline(true);

                ImageView faceView = convertView.findViewById(R.id.faceView);

                if(characterVO.getImage() != null && !characterVO.getImage().equals("null")) {
                    Glide.with(EpisodePreviewActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(characterVO.getImage())
                            .apply(new RequestOptions().circleCrop())
                            .into(faceView);
                } else if(characterVO.getStrImgFile() != null && !characterVO.getStrImgFile().equals("null")) {
                    String strUrl = characterVO.getStrImgFile();
//                    strUrl = strUrl.replaceAll(" ", "");

                    if(!strUrl.startsWith("http"))
                        strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;

                    Glide.with(EpisodePreviewActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(strUrl)
                            .apply(new RequestOptions().circleCrop())
                            .into(faceView);
                } else {
                    faceView.setImageResource(0);
                }

                if(chatVO.getContentsUri() != null) {
                    Glide.with(EpisodePreviewActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(chatVO.getContentsUri())
                            .apply(new RequestOptions().override(800, 800))
                            .into(imageContentsView);
                } else if(chatVO.getStrContentsFile() != null) {
                    String strUrl = chatVO.getStrContentsFile();
//                    strUrl = strUrl.replaceAll(" ", "");

                    if(!strUrl.startsWith("http"))
                        strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;

                    Glide.with(EpisodePreviewActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(strUrl)
                            .apply(new RequestOptions().override(800, 800))
                            .into(imageContentsView);
                }
            } else if(nType == ChatVO.TYPE_NARRATION) {
                convertView = mLiInflater.inflate(R.layout.narration_chatting_row, parent, false);

                TextView contentsTextView = convertView.findViewById(R.id.contentsTextView);
                contentsTextView.setText(chatVO.getContents());
            } else if(nType == ChatVO.TYPE_VIDEO) {
                int nDirection = characterVO.getDirection();

                if(nDirection == 0)             // left
                    convertView = mLiInflater.inflate(R.layout.left_video_row, parent, false);
                else
                    convertView = mLiInflater.inflate(R.layout.right_video_row, parent, false);

                TextView nameView = convertView.findViewById(R.id.nameView);
                nameView.setText(characterVO.getName());

                ImageView imageContentsView = convertView.findViewById(R.id.videoThumbnailView);
                imageContentsView.setClipToOutline(true);

                ImageView playBtn = convertView.findViewById(R.id.playBtn);
                playBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String strUrl = chatVO.getStrContentsFile();
                        if(!strUrl.startsWith("http"))
                            strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;

                        Intent intent = new Intent(EpisodePreviewActivity.this, VideoPlayerActivity.class);
                        intent.putExtra("VIDEO_URL", strUrl);
                        startActivity(intent);
                    }
                });

                ImageView faceView = convertView.findViewById(R.id.faceView);

                if(characterVO.getImage() != null && !characterVO.getImage().equals("null")) {
                    Glide.with(EpisodePreviewActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(characterVO.getImage())
                            .apply(new RequestOptions().circleCrop())
                            .into(faceView);
                } else if(characterVO.getStrImgFile() != null && !characterVO.getStrImgFile().equals("null")) {
                    String strUrl = characterVO.getStrImgFile();
//                    strUrl = strUrl.replaceAll(" ", "");

                    if(!strUrl.startsWith("http"))
                        strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;

                    Glide.with(EpisodePreviewActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(strUrl)
                            .apply(new RequestOptions().circleCrop())
                            .into(faceView);
                } else {
                    faceView.setImageResource(0);
                }

                if(chatVO.getContentsUri() != null) {
                    Glide.with(EpisodePreviewActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(chatVO.getContentsUri())
                            .apply(new RequestOptions().override(800, 800))
                            .into(imageContentsView);
                } else if(chatVO.getStrContentsFile() != null) {
                    String strUrl = chatVO.getStrContentsFile();
//                    strUrl = strUrl.replaceAll(" ", "");

                    if(!strUrl.startsWith("http"))
                        strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;

                    Glide.with(EpisodePreviewActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(strUrl)
                            .apply(new RequestOptions().override(800, 800))
                            .into(imageContentsView);
                }
            } else if(nType == ChatVO.TYPE_DISTRACTOR) {
                convertView = mLiInflater.inflate(R.layout.narration_chatting_row, parent, false);
                TextView contentsTextView = convertView.findViewById(R.id.contentsTextView);
                contentsTextView.setText(chatVO.getContents());
            } else if(nType == ChatVO.TYPE_CHANGE_BG) {
                convertView = mLiInflater.inflate(R.layout.narration_chatting_row, parent, false);
                TextView contentsTextView = convertView.findViewById(R.id.contentsTextView);
                contentsTextView.setText("배경 이미지 변경");

                if(chatVO.getContentsUri() != null) {
                    Glide.with(EpisodePreviewActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(chatVO.getContentsUri())
                            .transition(BitmapTransitionOptions.withCrossFade())
                            .into(bgView);
                } else if(chatVO.getStrContentsFile() != null) {
                    String strUrl = chatVO.getStrContentsFile();
//                    strUrl = strUrl.replaceAll(" ", "");

                    if(!strUrl.startsWith("http"))
                        strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;

                    Glide.with(EpisodePreviewActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(strUrl)
                            .transition(BitmapTransitionOptions.withCrossFade())
                            .into(bgView);
                }
            } else if(nType == ChatVO.TYPE_CHANGE_BG_COLOR) {
                convertView = mLiInflater.inflate(R.layout.narration_chatting_row, parent, false);

                TextView contentsTextView = convertView.findViewById(R.id.contentsTextView);
                contentsTextView.setText("배경 색 변경");

                int nColor = Integer.valueOf(chatVO.getStrContentsFile());
                bgView.setBackgroundColor(nColor);
            }

//            LinearLayout commentCountLayout = convertView.findViewById(R.id.commentCountLayout);
//            commentCountLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(EpisodePreviewActivity.this, ChatCommentActivity.class);
//                    intent.putExtra("CHAT_ID", chatVO.getnChatID());
//                    intent.putExtra("EPISODE_ID", workVO.getEpisodeList().get(nEpisodeIndex).getnEpisodeID());
//                    startActivity(intent);
//                }
//            });

            if(characterVO != null && nType != 3) {
                RelativeLayout contentsLayout = convertView.findViewById(R.id.contentsLayout);

                if(contentsLayout != null) {
                    String strBalloonColor = characterVO.getStrBalloonColor();

                    if(strBalloonColor != null && !strBalloonColor.equals("null") && strBalloonColor.length() > 0) {
                        if(contentsLayout != null && contentsLayout.getBackground() != null) {
                            int nColor = Color.parseColor(strBalloonColor);
                            PorterDuffColorFilter greyFilter = new PorterDuffColorFilter(nColor, PorterDuff.Mode.MULTIPLY);

                            if(characterVO.getDirection() == 0)
                                contentsLayout.setBackgroundResource(R.drawable.left_small_talk_new_white);
                            else
                                contentsLayout.setBackgroundResource(R.drawable.right_small_talk_new_white);
                            contentsLayout.getBackground().setColorFilter(greyFilter);
                        }
                    } else {
                        if(characterVO.getDirection() == 0)
                            contentsLayout.setBackgroundResource(R.drawable.left_small_talk_new);
                        else
                            contentsLayout.setBackgroundResource(R.drawable.right_small_talk_new);

                        if(contentsLayout != null && contentsLayout.getBackground() != null)
                            contentsLayout.getBackground().setColorFilter(null);
                    }
                }
            }

            TextView commentCountView = convertView.findViewById(R.id.commentCountView);
            commentCountView.setText(CommonUtils.getPointCount(chatVO.getnCommentCount()));
            if(chatVO.getnCommentCount() == 0) {
                commentCountView.setVisibility(View.INVISIBLE);
            } else {
                commentCountView.setVisibility(View.VISIBLE);
            }

            ImageView deleteBtn = convertView.findViewById(R.id.deleteBtn);
            deleteBtn.setVisibility(View.INVISIBLE);

            final TextView addBtn = convertView.findViewById(R.id.addBtn);
            addBtn.setVisibility(View.GONE);

            return convertView;
        }
    }

    private void setComment(View convertView) {
        commentList = new ArrayList<>();

        getCommentData(convertView);
    }

    private void getCommentData(final View convertView) {
        CommonUtils.showProgressDialog(this, "댓글 목록을 가져오고 있습니다. 잠시만 기다려주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject resultObject = HttpClient.getEpisodeCommnet(new OkHttpClient(), chattingList.get(0).getnEpisodeID(), 1, pref.getString("USER_ID", "Guest"));

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

                            commentList.add(vo);
                        }

                        fStarPoint = (float)resultObject.getDouble("STAR_POINT");
                        nStarCount = resultObject.getInt("STAR_COUNT");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        RatingBar smallRatingBar = convertView.findViewById(R.id.smallRatingBar);
                        TextView starPointView = convertView.findViewById(R.id.starPointView);
                        TextView starCountView = convertView.findViewById(R.id.starCountView);

                        smallRatingBar.setRating(fStarPoint);
                        starPointView.setText(String.format("%.1f", fStarPoint));
                        starCountView.setText("(" + nStarCount + "명)");

                        TextView rightView = convertView.findViewById(R.id.rightView);
                        rightView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(EpisodePreviewActivity.this, StarPointPopup.class);
                                intent.putExtra("EPISODE_ID", chattingList.get(0).getnEpisodeID());
                                startActivity(intent);
                            }
                        });

                        LinearLayout container = convertView.findViewById(R.id.commentContainer);
                        TextView noCommentView = convertView.findViewById(R.id.noCommentView);
                        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                        if(commentList != null && commentList.size() > 0) {
                            noCommentView.setVisibility(View.GONE);

                            for(int i = 0 ; i < commentList.size() ; i ++) {
                                if(i >= 3)
                                    break;

                                CommentVO vo = commentList.get(i);

                                View view = inflater.inflate(R.layout.chat_comment_row, null);
                                ImageView faceView = view.findViewById(R.id.faceView);
                                TextView nameView = view.findViewById(R.id.nameView);
                                TextView dateView = view.findViewById(R.id.dateTimeView);
                                TextView commentView = view.findViewById(R.id.commentView);

                                String strPhoto = vo.getUserPhoto();
                                if(strPhoto != null && !strPhoto.equals("null") && !strPhoto.equals("NULL") && strPhoto.length() > 0) {
                                    if(!strPhoto.startsWith("http"))
                                        strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

                                    Glide.with(EpisodePreviewActivity.this)
                                            .asBitmap() // some .jpeg files are actually gif
                                            .load(strPhoto)
                                            .apply(new RequestOptions().circleCrop())
                                            .into(faceView);
                                } else {
                                    Glide.with(EpisodePreviewActivity.this)
                                            .asBitmap() // some .jpeg files are actually gif
                                            .load(R.drawable.user_icon)
                                            .apply(new RequestOptions().circleCrop())
                                            .into(faceView);
                                }

                                nameView.setText(vo.getUserName());
                                dateView.setText(CommonUtils.strGetTime(vo.getRegisterDate()));
                                commentView.setText(vo.getStrComment());

                                container.addView(view);
                            }
                        }
                    }
                });
            }
        }).start();
    }
}