package com.Whowant.Tokki.UI.Activity.Work;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MotionEventCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Login.PanbookLoginActivity;
import com.Whowant.Tokki.UI.Activity.Market.MarketContentsFragment;
import com.Whowant.Tokki.UI.Activity.Market.MarketGenreFragment;
import com.Whowant.Tokki.UI.Activity.Market.MarketPagerAdapter;
import com.Whowant.Tokki.UI.Activity.Market.MarketTagFragment;
import com.Whowant.Tokki.UI.Popup.CarrotDoneActivity;
import com.Whowant.Tokki.UI.Popup.StarPointPopup;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.CommentVO;
import com.Whowant.Tokki.VO.EpisodeVO;
import com.Whowant.Tokki.VO.WebWorkVO;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.wajahatkarim3.easyflipviewpager.BookFlipPageTransformer;
import com.wajahatkarim3.easyflipviewpager.BookFlipPageTransformer2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

import static androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_DRAGGING;
import androidx.viewpager2.widget.ViewPager2;

public class WebWorkViewerActivity extends AppCompatActivity{

    WorkVO work;
    int episodeIndex = 0;
    int lastOrder = -1;
    int lastIndex = -1;

    int episodeID = 0;

    ArrayList<WebWorkVO> webs;

    TextView title;


    Boolean show = false;


    WebWorkPagerAdapter pagerAdapter;
    private ViewPager viewPager;

    private RecyclerView episodeListView;
    private ArrayList<EpisodeVO> episodeList;

    private WebEpisodeListAdapter ea;
    Animation translateDown, translateUp;

    RelativeLayout top;
    private float fMyPoint = 0;
    private float fStarPoint = 0;
    private int   nStarCount = 0;

    String workID = "";

    boolean drag = false;
    ToggleButton settingBtn;
    RelativeLayout dimLayerLayout;
    TextView nextEpisodeBtn;
    int selectedPage = 0;
    private SharedPreferences pref;
    private ArrayList<View> viewList;
    private ArrayList<CommentVO> commentList;
    private boolean bGetComment = false;

    int dpToPx(float dp)
    {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
    }
    public void showNav(Boolean show)
    {
        if(show)
        {
            ViewGroup.MarginLayoutParams lp0 = (ViewGroup.MarginLayoutParams) top.getLayoutParams();
            lp0.height = dpToPx(52);

          //  top.startAnimation(translateDown);
            top.setVisibility(View.VISIBLE);


        }
        else
        {
     //       top.startAnimation(translateUp);
            top.setVisibility(View.INVISIBLE);
            ViewGroup.MarginLayoutParams lp0 = (ViewGroup.MarginLayoutParams) top.getLayoutParams();
            lp0.height = 0;


        }
    }
    private void sendViewing(final int nOrder) {
        SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                EpisodeVO vo = episodeList.get(episodeIndex);
                int nWorkId = ViewerActivity.workVO.getnWorkID();
                HttpClient.sendHitsWork(new OkHttpClient(),nWorkId, vo.getnEpisodeID(), pref.getString("USER_ID", "Guest"), lastOrder);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                  //      finish();
                    }
                });
            }
        }).start();
    }
    public void nextEpisode()
    {
      //  episodeIndex = episodeIndex - episodeList.size() -1;
        episodeIndex --;
        if(episodeIndex < 0)
        {
            episodeIndex = 0;
            return;

        }


        EpisodeVO episode = work.getEpisodeList().get(episodeIndex);


        //  EpisodeVO episode = work.getEpisodeList().get(episodeIndex);

        title.setText(episode.getStrTitle());

        episodeID = episode.getnEpisodeID();

        getEpisodeNovelData();

    }
    public void prevEpisode()
    {
      //  episodeIndex = episodeIndex - episodeList.size() -1;
        episodeIndex ++;
        if(episodeIndex > episodeList.size() -1)
        {
            episodeIndex = episodeList.size() -1;
            return;
        }
           

        EpisodeVO episode = work.getEpisodeList().get(episodeIndex);


        //  EpisodeVO episode = work.getEpisodeList().get(episodeIndex);

        title.setText(episode.getStrTitle());

        episodeID = episode.getnEpisodeID();

        getEpisodeNovelData();

    }
    public void onClickComment(View view) {
        Intent intent = new Intent(WebWorkViewerActivity.this, EpisodeCommentActivity.class);
        intent.putExtra("EPISODE_ID", work.getEpisodeList().get(episodeIndex).getnEpisodeID());
        startActivity(intent);
    }
    public void onClickPrev(View view) {

        prevEpisode();

    }
    public void onClickNext(View view) {

       nextEpisode();

    }
    public void getStarPoint() {
        SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject resultObject = HttpClient.getEpisodeCommnet(new OkHttpClient(), work.getEpisodeList().get(episodeIndex).getnEpisodeID(), 1, pref.getString("USER_ID", "Guest"));

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
                                    Toast.makeText(WebWorkViewerActivity.this, "로그인이 필요한 기능입니다. 로그인 해주세요.", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(WebWorkViewerActivity.this, PanbookLoginActivity.class));
                                    return;
                                }

                                if(fMyPoint > 0) {
                                    Toast.makeText(WebWorkViewerActivity.this, "이미 평가한 작품입니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                Intent intent = new Intent(WebWorkViewerActivity.this, StarPointPopup.class);
                                intent.putExtra("EPISODE_ID", work.getEpisodeList().get(episodeIndex).getnEpisodeID());
                                startActivity(intent);
                            }
                        });
                    }
                });
            }
        }).start();
    }

    public void onClickCarrotBtn(View view) {
        SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

        if (work.getnWriterID().equals(pref.getString("USER_ID", "Guest"))) {
            CommonUtils.makeText(this, "내 작품에는 후원 하실수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, CarrotDoneActivity.class);
        intent.putExtra("WORK_ID", work.getnWorkID());
        startActivity(intent);
    }
    public void onClickStar(View view) {

        SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

        boolean bLogin = CommonUtils.bLocinCheck(pref);

        if(!bLogin) {
            Toast.makeText(WebWorkViewerActivity.this, "로그인이 필요한 기능입니다. 로그인 해주세요.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(WebWorkViewerActivity.this, PanbookLoginActivity.class));
            return;
        }

        if(fMyPoint > 0) {
            Toast.makeText(WebWorkViewerActivity.this, "이미 평가한 작품입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(WebWorkViewerActivity.this, StarPointPopup.class);
        intent.putExtra("EPISODE_ID", work.getEpisodeList().get(episodeIndex).getnEpisodeID());
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_work_viewer);

        viewPager = findViewById(R.id.viewPager);

        title = findViewById(R.id.titleView );

        dimLayerLayout= findViewById(R.id.dimLayerLayout );

        dimLayerLayout.setVisibility(View.GONE);
        work = ViewerActivity.workVO;//(WorkVO)getIntent().getSerializableExtra("WORK");
        episodeIndex = getIntent().getIntExtra("EPISODE_INDEX",0);
        lastOrder = getIntent().getIntExtra("LAST_ORDER",-1);

        workID = getIntent().getStringExtra("WORK_ID");

        episodeListView = findViewById(R.id.episodeListView);
        top = findViewById(R.id.topBarLayout);

        episodeList = work.getEpisodeList();

        pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);


        ea = new WebEpisodeListAdapter(WebWorkViewerActivity.this, episodeList);
        episodeListView.setAdapter(ea);
        episodeListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        episodeListView.getLayoutManager().scrollToPosition(episodeList.size() - episodeIndex - 1);


        episodeIndex = work.getEpisodeList().size() - episodeIndex - 1;

        EpisodeVO episode = work.getEpisodeList().get(episodeIndex);

        title.setText(episode.getStrTitle());

        episodeID = episode.getnEpisodeID();

        settingBtn = findViewById(R.id.settingBtn);



        getEpisodeNovelData();
        ToggleButton episodeListBtn = findViewById(R.id.episodeListBtn);
        LinearLayout episodeListLayout = findViewById(R.id.episodeListLayout);
        translateDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_down);
        translateUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_up);

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

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
                selectedPage = position;
                lastOrder = position;
                if(position == 0)
                {
                    show = true;

                    WebWorkFragment fragment = (WebWorkFragment)pagerAdapter.getItem(position);
                    fragment.page = position;
                    fragment.showMenu(true,false,true);
                    settingBtn.setBackgroundResource(R.drawable.ic_i_setting_blue);


                }
                else
                {
                    WebWorkFragment fragment = (WebWorkFragment)pagerAdapter.getItem(position);
                    fragment.page = position;
                    fragment.showMenu(false, false,false);
                    fragment.getHtml();
                    settingBtn.setBackgroundResource(R.drawable.ic_i_setting);

                    if(webs.size() == (position + 1)) {

                    }
                    else
                        dimLayerLayout.setVisibility(View.GONE);


                }
                drag = false;

            }

            @Override
            public void onPageSelected(int position)
            {

            }

            @Override
            public void onPageScrollStateChanged(int state)
            {
                /*
                 * @see ViewPager#SCROLL_STATE_IDLE
                 * @see ViewPager#SCROLL_STATE_DRAGGING
                 * @see ViewPager#SCROLL_STATE_SETTLING
                 */
                if(state == SCROLL_STATE_DRAGGING &&  webs.size() == (selectedPage + 1))
                {
                    dimLayerLayout.setVisibility(View.VISIBLE);
                    if(episodeIndex == 0) {         // 마지막 이라면

                        nextEpisodeBtn.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        nextEpisodeBtn.setVisibility(View.VISIBLE);
                  //      setComment();

                    }

                }

            }
        });


        TextView moreView = findViewById(R.id.commentMoreView);
        moreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WebWorkViewerActivity.this, EpisodeCommentActivity.class);
                intent.putExtra("EPISODE_ID", work.getEpisodeList().get(episodeIndex).getnEpisodeID());
                startActivity(intent);
            }
        });

         nextEpisodeBtn = findViewById(R.id.nextEpisodeBtn);
        if(episodeIndex == 0) {         // 마지막 이라면


            nextEpisodeBtn.setVisibility(View.INVISIBLE);
        } else {
            nextEpisodeBtn.setVisibility(View.VISIBLE);
            nextEpisodeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dimLayerLayout.setVisibility(View.INVISIBLE);

                    nextEpisode();

                }
            });

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        setComment();
    }

    public void onClickBottomBgView(View view) {
     //   dimLayerLayout.setVisibility(View.GONE);

    }
    public void clickCloseBtn(View view) {
          dimLayerLayout.setVisibility(View.GONE);

    }

    public void onClickNextEpisode(View view) {
       // dimLayerLayout.setVisibility(View.GONE);

    }
    private void setComment() {
        commentList = new ArrayList<>();
        getCommentData();
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
                JSONObject resultObject = HttpClient.getEpisodeCommnet(new OkHttpClient(), work.getEpisodeList().get(episodeIndex).getnEpisodeID(), 1, pref.getString("USER_ID", "Guest"));

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


                                if(fMyPoint > 0) {
                                    Toast.makeText(WebWorkViewerActivity.this, "이미 평가한 작품입니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                Intent intent = new Intent(WebWorkViewerActivity.this, StarPointPopup.class);
                                intent.putExtra("EPISODE_ID", work.getEpisodeList().get(episodeIndex).getnEpisodeID());
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

                                    Glide.with(WebWorkViewerActivity.this)
                                            .asBitmap() // some .jpeg files are actually gif
                                            .load(strPhoto)
                                            .apply(new RequestOptions().circleCrop())
                                            .into(faceView);
                                } else {
                                    Glide.with(WebWorkViewerActivity.this)
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


                            }
                        } else {
                            noCommentView.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }).start();
    }
    void getEpisodeNovelData()
    {

        if(webs != null && webs.size() > 0)
            webs.clear();

        CommonUtils.showProgressDialog(WebWorkViewerActivity.this, "서버와 통신중입니다. 잠시만 기다려주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                webs = HttpClient.getEpisodeNovelData(new OkHttpClient(), String.valueOf(episodeID));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*
                        CommonUtils.hideProgressDialog();
                        WebWorkVO data = webs.get(1);
                        webview.loadData(data.getRaw(), "text/html; charset=utf-8", "UTF-8");
                        */
                        CommonUtils.hideProgressDialog();
                        pagerAdapter = new WebWorkPagerAdapter(getSupportFragmentManager());

                        for(int i = 0;i< webs.size();i++)
                        {
                            WebWorkVO data = webs.get(i);
                            WebWorkFragment fragmet =  new WebWorkFragment();
                            fragmet.webWork = data;
                            pagerAdapter.fragments.add(fragmet);

                        }
                        viewPager.setAdapter(pagerAdapter);
                        // Create an object of page transformer
                        BookFlipPageTransformer bookFlipPageTransformer = new BookFlipPageTransformer();

                        bookFlipPageTransformer.setEnableScale(false);
                        bookFlipPageTransformer.setScaleAmountPercent(2f);
                        viewPager.setPageTransformer( true,bookFlipPageTransformer);
                        pagerAdapter.notifyDataSetChanged();
                        viewPager.setCurrentItem(lastOrder);




                    }
                });
            }
        }).start();
    }

    public void onClickTopLeftBtn(View view) {
        initVariables();
        sendViewing(lastOrder);

        finish();
    }
    public void onClickSettingsBtn(View view) {

        int pos = viewPager.getCurrentItem();
        Boolean isCover = false;
        if(pos==0)
        {
            isCover = true;
        }
        WebWorkFragment fragment = (WebWorkFragment)pagerAdapter.getItem(pos);
        if(fragment.bottomMenu.getVisibility() == View.VISIBLE)
        {
            settingBtn.setBackgroundResource(R.drawable.ic_i_setting);

            fragment.showMenu(false, true,isCover);
            show = false;

        }
        else
        {
            settingBtn.setBackgroundResource(R.drawable.ic_i_setting_blue);

            fragment.initSettings();
            fragment.showMenu(true, true,isCover);
            show = true;
        }
        if(pos > 0)
        {
           // fragment.showMenu(show);

        }
    }

    public void initVariables()
    {
        SharedPreferences pref = getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();

        editor.putFloat("FONT_SIZE", 18.0f);
        editor.putInt("BACK_TYPE", 0);
        editor.putInt("FONT_TYPE", 0);

        editor.commit();

    }
    public void onClickBack0(View view) {

        int pos = viewPager.getCurrentItem();
        WebWorkFragment fragment = (WebWorkFragment)pagerAdapter.getItem(pos);
        fragment.setBack0();
/*
        for(int i = 0;i< webs.size();i++)
        {
            WebWorkVO data = webs.get(i);
//            WebWorkFragment fragmet =  new WebWorkFragment();
            WebWorkFragment fragment = (WebWorkFragment)pagerAdapter.getItem(i);

            fragment.setBack0();
        }


 */


    }
    public void onClickBack1(View view) {
        int pos = viewPager.getCurrentItem();
        WebWorkFragment fragment = (WebWorkFragment)pagerAdapter.getItem(pos);
        fragment.setBack1();
        pagerAdapter.notifyDataSetChanged();
        /*
        for(int i = 0;i< webs.size();i++)
        {
            WebWorkVO data = webs.get(i);
//            WebWorkFragment fragmet =  new WebWorkFragment();
            WebWorkFragment fragment0 = (WebWorkFragment)pagerAdapter.getItem(i);

            fragment0.getHtml();
        }

         */

    }
    public void onClickBack2(View view) {
        int pos = viewPager.getCurrentItem();
        WebWorkFragment fragment = (WebWorkFragment)pagerAdapter.getItem(pos);
        fragment.setBack2();
        pagerAdapter.notifyDataSetChanged();

        /*
        for(int i = 0;i< webs.size();i++)
        {
            WebWorkVO data = webs.get(i);
//            WebWorkFragment fragmet =  new WebWorkFragment();
            WebWorkFragment fragment0 = (WebWorkFragment)pagerAdapter.getItem(i);

            fragment0.getHtml();
        }

         */


    }
    public void onClickBack3(View view) {
        int pos = viewPager.getCurrentItem();
        WebWorkFragment fragment = (WebWorkFragment)pagerAdapter.getItem(pos);
        fragment.setBack3();
        pagerAdapter.notifyDataSetChanged();

        /*
        for(int i = 0;i< webs.size();i++)
        {
            WebWorkVO data = webs.get(i);
//            WebWorkFragment fragmet =  new WebWorkFragment();
            WebWorkFragment fragment0 = (WebWorkFragment)pagerAdapter.getItem(i);

            fragment0.getHtml();
        }

         */


    }
    public void onClickBack4(View view) {
        int pos = viewPager.getCurrentItem();
        WebWorkFragment fragment = (WebWorkFragment)pagerAdapter.getItem(pos);
        fragment.setBack4();
        pagerAdapter.notifyDataSetChanged();

        /*
        for(int i = 0;i< webs.size();i++)
        {
            WebWorkVO data = webs.get(i);
//            WebWorkFragment fragmet =  new WebWorkFragment();
            WebWorkFragment fragment0 = (WebWorkFragment)pagerAdapter.getItem(i);

            fragment0.getHtml();
        }

         */


    }
    public void onClickBack5(View view) {
        int pos = viewPager.getCurrentItem();
        WebWorkFragment fragment = (WebWorkFragment)pagerAdapter.getItem(pos);
        fragment.setBack5();
        pagerAdapter.notifyDataSetChanged();

        /*
        for(int i = 0;i< webs.size();i++)
        {
            WebWorkVO data = webs.get(i);
//            WebWorkFragment fragmet =  new WebWorkFragment();
            WebWorkFragment fragment0 = (WebWorkFragment)pagerAdapter.getItem(i);

            fragment0.getHtml();
        }


         */

    }
    public void onClickFont0(View view) {
        int pos = viewPager.getCurrentItem();
        WebWorkFragment fragment = (WebWorkFragment)pagerAdapter.getItem(pos);
        fragment.setFont0();
        pagerAdapter.notifyDataSetChanged();

        /*
        for(int i = 0;i< webs.size();i++)
        {
            WebWorkVO data = webs.get(i);
//            WebWorkFragment fragmet =  new WebWorkFragment();
            WebWorkFragment fragment = (WebWorkFragment)pagerAdapter.getItem(i);

            fragment.setFont0();
        }

         */


    }
    public void onClickFont1(View view) {
        int pos = viewPager.getCurrentItem();
        WebWorkFragment fragment = (WebWorkFragment)pagerAdapter.getItem(pos);
        fragment.setFont1();
        pagerAdapter.notifyDataSetChanged();



    }
    public void onClickFont2(View view) {
        int pos = viewPager.getCurrentItem();
        WebWorkFragment fragment = (WebWorkFragment)pagerAdapter.getItem(pos);
        fragment.setFont2();
        pagerAdapter.notifyDataSetChanged();


    }
    public void onClickFont3(View view) {
        int pos = viewPager.getCurrentItem();
        WebWorkFragment fragment = (WebWorkFragment)pagerAdapter.getItem(pos);
       fragment.setFont3();
        pagerAdapter.notifyDataSetChanged();


    }

    public void onClickDecreaseFont(View view) {

        int pos = viewPager.getCurrentItem();
        WebWorkFragment fragment = (WebWorkFragment)pagerAdapter.getItem(pos);
        fragment.setDecreaseFont();
        pagerAdapter.notifyDataSetChanged();

    }

    public void onClickIncreaseFont(View view) {

        int pos = viewPager.getCurrentItem();
        WebWorkFragment fragment = (WebWorkFragment)pagerAdapter.getItem(pos);
        fragment.setIncreaseFont();
        pagerAdapter.notifyDataSetChanged();

    }

    public class WebWorkPagerAdapter extends FragmentStatePagerAdapter {

        public List<Fragment> fragments=new ArrayList<>();

        public WebWorkPagerAdapter(FragmentManager fm) {
            super(fm);
           // fragments.add(new MarketContentsFragment());
           // fragments.add(new MarketGenreFragment());
           // fragments.add(new MarketTagFragment());
        }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    public class WebEpisodeListAdapter extends RecyclerView.Adapter<WebEpisodeListAdapter.WebEpisodeListViewHolder> {
        private ArrayList<EpisodeVO> itemsList;
        private Activity mContext;

        public WebEpisodeListAdapter(Activity context, ArrayList<EpisodeVO> itemsList) {
            this.mContext = context;
            this.itemsList = itemsList;
        }

        @Override
        public WebEpisodeListViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.work_main_episode_row, null);

            return new WebEpisodeListViewHolder(v);
        }

        @Override
        public void onBindViewHolder(WebEpisodeListViewHolder holder, int position) {
            if (position >= episodeList.size())
                return;

            int nIndex = position;
            EpisodeVO vo = work.getEpisodeList().get(nIndex);

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

        public class WebEpisodeListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
            public WebEpisodeListViewHolder(View view) {
                super(view);

                view.setOnClickListener(this);

            }
            @Override
            public void onClick(View v){
                // this.itemClickListener.onItemClickListener(v, getLayoutPosition());
                int pos = getLayoutPosition();
                EpisodeVO episodeVO = work.getEpisodeList().get(pos);

                episodeIndex = pos;

                EpisodeVO episode = work.getEpisodeList().get(episodeIndex);

                title.setText(episode.getStrTitle());

                episodeID = episode.getnEpisodeID();

                getEpisodeNovelData();



            }
        }


    }

}