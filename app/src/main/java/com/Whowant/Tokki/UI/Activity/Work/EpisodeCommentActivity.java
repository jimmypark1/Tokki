package com.Whowant.Tokki.UI.Activity.Work;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Login.LoginSelectActivity;
import com.Whowant.Tokki.UI.Activity.Login.PanbookLoginActivity;
import com.Whowant.Tokki.UI.Activity.Writer.WriterMainActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.CustomUncaughtExceptionHandler;
import com.Whowant.Tokki.VO.CommentVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.OkHttpClient;

public class EpisodeCommentActivity extends AppCompatActivity {                                             // ?????? ????????? ?????? ?????????.
    private ExpandableListView listView;
    private LinearLayout emptyLayout;
    private EditText inputTextView;

    private int nWorkID;
    private int nEpisodeID;

    private ArrayList<CommentVO> commentList;                                                               // ?????? ??????
    private ArrayList<ArrayList<CommentVO>> subCommentList = new ArrayList<>();                             // ????????? ?????? - ????????? ???????????? ???????????? ExpandibleListView ??? ????????????
    private CExpandableListviewAdapter expandableAdapter;
    private SharedPreferences pref;

    private String strParentName;
    private int nParentID = -1;
    private int nParentChatID = -1;
    private boolean showLogin = false;
    private int nOrder = 2;                 // 1 : ?????????, 2 : ?????????
    private Button sendBtn;

    private InputMethodManager imm;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_comment);

        Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();

        if (!(handler instanceof CustomUncaughtExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
        }

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        nWorkID = getIntent().getIntExtra("WORK_ID", 0);
        nEpisodeID = getIntent().getIntExtra("EPISODE_ID", 0);

        TextView titleView = findViewById(R.id.titleView);
        titleView.setText("??????");

        listView = findViewById(R.id.listView);
        emptyLayout = findViewById(R.id.emptyLayout);
        inputTextView = findViewById(R.id.inputTextView);
        sendBtn = findViewById(R.id.sendBtn);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean bLogin = CommonUtils.bLocinCheck(pref);

                if(!bLogin && !showLogin) {
                    showLogin = true;
                    Toast.makeText(EpisodeCommentActivity.this, "???????????? ????????? ???????????????. ????????? ????????????.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EpisodeCommentActivity.this, PanbookLoginActivity.class));
                    return;
                }

                if(inputTextView.getText().toString().length() > 0) {
                    sendBtn.setBackgroundResource(R.drawable.comment_enter_btn_blue);
                } else {
                    sendBtn.setBackgroundResource(R.drawable.comment_enter_btn_gray);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        inputTextView.addTextChangedListener(textWatcher);

        pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

        commentList = new ArrayList<>();
        subCommentList = new ArrayList<>();

        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                return false;
            }
        });

        getCommentData();
    }

    @Override
    public void onResume() {
        super.onResume();
        showLogin =  false;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.keep_menu, menu);
//
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch(item.getItemId()) {
//            case android.R.id.home:
//                finish();
//                break;
//            case R.id.action_more:
//                AlertDialog.Builder builder = new AlertDialog.Builder(EpisodeCommentActivity.this);
//                builder.setItems(getResources().getStringArray(R.array.ORDER_MENU), new DialogInterface.OnClickListener(){
//                    @Override
//                    public void onClick(DialogInterface dialog, int pos) {
//                        if(pos == 0) {
//                            nOrder = 1;
//                        } else if(pos == 1) {
//                            nOrder = 2;
//                        }
//
//                        getCommentData();
//                    }
//                });
//
//                AlertDialog alertDialog = builder.create();
//                alertDialog.show();
//                break;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }

    public void onClickTopRightBtn(View view) {                                         // ?????? ?????? ?????? ???????????? popup ?????? ?????? -> ??????
        PopupMenu popup = new PopupMenu(EpisodeCommentActivity.this, view);
        popup.getMenuInflater().inflate(R.menu.comment_order_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.asc_order: {
                        nOrder = 2;
                    }
                    break;
                    case R.id.popular_order: {
                        nOrder = 1;
                    }
                    break;
                }

                getCommentData();
                return true;
            }
        });

        popup.show();//showing popup menu
    }

    private void requestSendComment(final String strComment) {
        CommonUtils.showProgressDialog(this, "????????? ???????????? ????????????. ????????? ??????????????????.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> dataMap = new HashMap<>();
                dataMap.put("USER_ID", pref.getString("USER_ID", "Guest"));
                dataMap.put("COMMENT", strComment);
                dataMap.put("EPISODE_ID", "" + nEpisodeID);
                dataMap.put("WORK_ID", "" + nWorkID);

                if(nParentID > -1) {
                    dataMap.put("PARENT_ID", "" + nParentID);
                    dataMap.put("CHAT_ID", "" + nParentChatID);
                }

                boolean bResult = HttpClient.requestSendComment(new OkHttpClient(), dataMap);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(!bResult) {
                            Toast.makeText(EpisodeCommentActivity.this, "?????? ????????? ?????????????????????. ????????? ?????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            getCommentData();
                            inputTextView.setText("");
                            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                            //Find the currently focused view, so we can grab the correct window token from it.
                            View view = getCurrentFocus();
                            //If no view currently has focus, create a new one, just so we can grab a window token from it
                            if (view == null) {
                                view = new View(EpisodeCommentActivity.this);
                            }
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                });
            }
        }).start();
    }

    private void getCommentData() {
        commentList.clear();
        subCommentList.clear();

        CommonUtils.showProgressDialog(this, "?????? ????????? ???????????? ????????????. ????????? ??????????????????.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject resultObject = null;

                if(nEpisodeID > 0)                          // ???????????? ???????????? ?????? ?????? ????????? ????????? ??????, ?????? ?????? ?????? ????????? ????????? ??????
                    resultObject = HttpClient.getEpisodeCommnet(new OkHttpClient(), nEpisodeID, nOrder, pref.getString("USER_ID", "Guest"));
                else
                    resultObject = HttpClient.getWorkCommnet(new OkHttpClient(), nWorkID, nOrder, pref.getString("USER_ID", "Guest"));

                final JSONObject result = resultObject;
                JSONArray resultArray = null;
                if(resultObject != null) {
                    try {
                        resultArray = resultObject.getJSONArray("COMMENT_LIST");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    reOrderCommentList(resultArray);                                                                    ///  ????????? ????????? ???????????? ??????
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(result == null) {
                            Toast.makeText(EpisodeCommentActivity.this, "?????? ????????? ??????????????? ??????????????????. ????????? ?????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
                            setNoComment();
                            return;
                        }

                        if(commentList.size() == 0) {
                            setNoComment();
                            return;
                        }

                        listView.setVisibility(View.VISIBLE);
                        emptyLayout.setVisibility(View.INVISIBLE);

                        expandableAdapter = new CExpandableListviewAdapter();
                        expandableAdapter.parentItems = commentList;
                        expandableAdapter.childItems = subCommentList;
                        listView.setAdapter(expandableAdapter);
                    }
                });
            }
        }).start();
    }

    private void reOrderCommentList(JSONArray array) {                          // ????????? ???????????? ???????????? ?????? ??????. ????????? ??????????????? ???????????? ?????????        c
        ArrayList<CommentVO> subList = new ArrayList<>();

        try {
            for(int i = 0 ; i < array.length() ; i++) {                                     // Parent Group ??????
                JSONObject object = array.getJSONObject(i);
                if(object.getInt("COMMENT_ID") == 0)
                continue;

                CommentVO vo = new CommentVO();

                vo.setCommentID(object.getInt("COMMENT_ID"));
                vo.setEpisodeID(object.getInt("EPISODE_ID"));
                vo.setParentID(object.getInt("PARENT_ID"));
                vo.setStrComment(object.getString("COMMENT"));
                vo.setRegisterDate(object.getString("REGISTER_DATE"));
                vo.setUserName(object.getString("USER_NAME"));
                vo.setUserPhoto(object.getString("USER_PHOTO"));
                vo.setUserID(object.getString("USER_ID"));
                vo.setChatID(object.getInt("CHAT_ID"));
                vo.setMyComment(object.getInt("MY_COMMENT") > 0 ? true : false);
                vo.setLikeCount(object.getInt("LIKE_COUNT"));
                vo.setStrWorkTitle(object.getString("WORK_TITLE"));
                vo.setnEpisodeOrder(object.getInt("EPISODE_ORDER"));
                vo.setnDonationCarrot(object.getInt("DONATION_CARROT"));
                vo.setHasChild(false);

                int nParentID = object.getInt("PARENT_ID");
                if(nParentID > -1) {
                    continue;
                }

                commentList.add(vo);
                subCommentList.add(null);
            }

            for(int i = 0 ; i < array.length() ; i++) {
                JSONObject object = array.getJSONObject(i);
                if(object.getInt("COMMENT_ID") == 0)
                    continue;

                CommentVO vo = new CommentVO();

                vo.setCommentID(object.getInt("COMMENT_ID"));
                vo.setEpisodeID(object.getInt("EPISODE_ID"));
                vo.setParentID(object.getInt("PARENT_ID"));
                vo.setStrComment(object.getString("COMMENT"));
                vo.setRegisterDate(object.getString("REGISTER_DATE"));
                vo.setUserName(object.getString("USER_NAME"));
                vo.setUserPhoto(object.getString("USER_PHOTO"));
                vo.setUserID(object.getString("USER_ID"));
                vo.setChatID(object.getInt("CHAT_ID"));
                vo.setMyComment(object.getInt("MY_COMMENT") > 0 ? true : false);
                vo.setLikeCount(object.getInt("LIKE_COUNT"));
                vo.setStrWorkTitle(object.getString("WORK_TITLE"));
                vo.setnEpisodeOrder(object.getInt("EPISODE_ORDER"));
                vo.setnDonationCarrot(object.getInt("DONATION_CARROT"));

                int nParentID = object.getInt("PARENT_ID");
                if(nParentID > -1) {
                    for(int j = 0 ; j < commentList.size() ; j++) {
                        CommentVO tempVO = commentList.get(j);
                        subList = subCommentList.get(j);
                        if(subList == null)
                            subList = new ArrayList<>();

                        int nCommentID = tempVO.getCommentID();

                        if(nParentID == nCommentID) {
                            CommentVO subVO = new CommentVO();
                            subVO.setCommentID(object.getInt("COMMENT_ID"));
                            subVO.setEpisodeID(object.getInt("EPISODE_ID"));
                            subVO.setParentID(object.getInt("PARENT_ID"));
                            subVO.setStrComment(object.getString("COMMENT"));
                            subVO.setRegisterDate(object.getString("REGISTER_DATE"));
                            subVO.setUserName(object.getString("USER_NAME"));
                            subVO.setUserPhoto(object.getString("USER_PHOTO"));
                            subVO.setUserID(object.getString("USER_ID"));
                            subVO.setChatID(object.getInt("CHAT_ID"));
                            subVO.setMyComment(object.getInt("MY_COMMENT") > 0 ? true : false);
                            subVO.setLikeCount(object.getInt("LIKE_COUNT"));
                            subVO.setStrWorkTitle(object.getString("WORK_TITLE"));
                            subVO.setnEpisodeOrder(object.getInt("EPISODE_ORDER"));
                            subVO.setnDonationCarrot(object.getInt("DONATION_CARROT"));
                            subVO.setParentID(nParentID);
                            subList.add(subVO);
                            subCommentList.set(j, subList);

                            tempVO.setHasChild(true);
                            commentList.set(j, tempVO);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setNoComment() {
        listView.setVisibility(View.INVISIBLE);
        emptyLayout.setVisibility(View.VISIBLE);
    }

    public class CExpandableListviewAdapter extends BaseExpandableListAdapter {
        ArrayList<CommentVO> parentItems; //?????? ???????????? ?????? ??????
        ArrayList<ArrayList<CommentVO>> childItems; //?????? ???????????? ?????? ??????

        //??? ???????????? ?????? ??????
        @Override
        public int getGroupCount() {
            return parentItems.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            if(childItems.get(groupPosition) == null)
                return 0;
            else
                return childItems.get(groupPosition).size();
        }

        //???????????? ????????? ??????
        @Override
        public CommentVO getGroup(int groupPosition) {
            return parentItems.get(groupPosition);
        }

        @Override
        public CommentVO getChild(int groupPosition, int childPosition) {
            if(childItems.get(groupPosition) == null)
                return null;
            else
                return childItems.get(groupPosition).get(childPosition);
        }

        //????????? ???????????? id ??????
        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        //????????? id??? ?????? ????????? ????????? ??????????????? ????????? ??????
        @Override
        public boolean hasStableIds() {
            return true;
        }

        //????????? ????????? row??? view??? ??????
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            Context context = parent.getContext();

            //convertView??? ???????????? ?????? xml????????? inflate ??????
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.chat_comment_row, parent, false);
            }

            CommentVO vo = commentList.get(groupPosition);
            RelativeLayout bgView = convertView.findViewById(R.id.bgView);
            ImageView faceView = convertView.findViewById(R.id.faceView);
            TextView nameView = convertView.findViewById(R.id.nameView);
            TextView dateView = convertView.findViewById(R.id.dateTimeView);
            TextView commentView = convertView.findViewById(R.id.commentView);
            LinearLayout likeLayout = convertView.findViewById(R.id.likeLayout);
            TextView likeCountView = convertView.findViewById(R.id.likeCountView);
          //  TextView episodeNumView = convertView.findViewById(R.id.episodeNumView);
            ImageView thumbIconView = convertView.findViewById(R.id.thumbIconView);
            ImageView arrowBtn = convertView.findViewById(R.id.arrowBtn);
            TextView reportBtn = convertView.findViewById(R.id.reportBtn);
            TextView replyBtn = convertView.findViewById(R.id.replyBtn);

            ImageView emptyIconView = convertView.findViewById(R.id.emptyIconView);
            ImageView lv1IconView = convertView.findViewById(R.id.lv1IconView);
            ImageView lv5IconView = convertView.findViewById(R.id.lv5IconView);
            ImageView lv10IconView = convertView.findViewById(R.id.lv10IconView);
            RelativeLayout smallLv10View = convertView.findViewById(R.id.smallLv10View);
            RelativeLayout levelBGView = convertView.findViewById(R.id.levelBGView);
            TextView smallLvView = convertView.findViewById(R.id.smallLvView);

            if(vo.isHasChild()) {
                arrowBtn.setVisibility(View.VISIBLE);
            } else {
                arrowBtn.setVisibility(View.GONE);
            }

            if(isExpanded) {
                arrowBtn.setBackgroundResource(R.drawable.i_up_arrow_gray);
            } else {
                arrowBtn.setBackgroundResource(R.drawable.i_down_arrow_gray);
            }

            if(vo.getUserID().equals(pref.getString("USER_ID", "Guest")) || pref.getString("ADMIN", "N").equals("Y")) {
                reportBtn.setText("??????");
            } else {
                reportBtn.setText("??????");
            }

//            arrowBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if(isExpanded) {
//                        listView.collapseGroup(groupPosition);
//                    } else {
//                        listView.expandGroup(groupPosition, true);
//                    }
//                }
//            });

//            bgView.setBackgroundResource(R.drawable.round_shadow_btn_white_bg);

            String strPhoto = vo.getUserPhoto();
            if(strPhoto != null && !strPhoto.equals("null") && !strPhoto.equals("NULL") && strPhoto.length() > 0) {
                if(!strPhoto.startsWith("http"))
                    strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

                Glide.with(EpisodeCommentActivity.this)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(strPhoto)
                        .placeholder(R.drawable.user_icon)
                        .apply(new RequestOptions().circleCrop())
                        .into(faceView);
            } else {
                Glide.with(EpisodeCommentActivity.this)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(R.drawable.user_icon)
                        .apply(new RequestOptions().circleCrop())
                        .into(faceView);
            }

            faceView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(EpisodeCommentActivity.this, WriterMainActivity.class);
                    intent.putExtra("USER_ID", vo.getUserID());
                    intent.putExtra("WRITER", false);
                    startActivity(intent);
                }
            });

            nameView.setText(vo.getUserName());
//            String strTime = vo.getRegisterDate();
//            strTime = strTime.substring(0, 10) + "\n" + strTime.substring(11, 16);
            dateView.setText(CommonUtils.strGetTime(vo.getRegisterDate()));
            commentView.setText(vo.getStrComment());

//            likeLayout.setVisibility(View.VISIBLE);
//            likeCountView.setText(vo.getLikeCount() + "");

            int nOrder = vo.getnEpisodeOrder();
/*
            if(nOrder == 0)
                episodeNumView.setText(vo.getStrWorkTitle());
            else
                episodeNumView.setText(vo.getStrWorkTitle() + "(" + vo.getnEpisodeOrder() + "???)");

            if(episodeNumView.getText().toString().equals("null"))
                episodeNumView.setText("????????????");

 */

            if(vo.isMyComment()) {
                /*
                likeLayout.setBackgroundResource(R.drawable.round_blue_btn_bg);
                likeCountView.setTextColor(Color.parseColor("#ffffff"));
                thumbIconView.setBackgroundResource(R.drawable.white_like_box);

                 */
//                heartView.setBackgroundResource(R.drawable.top_like_active);
//                heartView.getBackground().setColorFilter(null);
            } else {
                /*
                likeLayout.setBackgroundResource(R.drawable.badge_complete);
                likeCountView.setTextColor(ContextCompat.getColor(EpisodeCommentActivity.this, R.color.colorPrimary));
                thumbIconView.setBackgroundResource(R.drawable.like_box);

                 */
//                heartView.setBackgroundResource(R.drawable.top_like);
//                PorterDuffColorFilter greyFilter = new PorterDuffColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
//                heartView.getBackground().setColorFilter(greyFilter);
            }
/*
            likeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(vo.getUserID().equals(pref.getString("USER_ID", "Guest"))) {
                        Toast.makeText(context, "??? ???????????? ???????????? ????????? ??? ????????????.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(vo.isMyComment()) {
                        vo.setLikeCount(vo.getLikeCount()-1);
                        vo.setMyComment(false);
                        requestReleaseLikeComment(vo.getCommentID());
                    } else {
                        vo.setLikeCount(vo.getLikeCount()+1);
                        vo.setMyComment(true);
                        requestLikeComment(vo.getCommentID());
                    }
                }
            });

 */

            reportBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(pref.getString("ADMIN", "N").equals("Y") || pref.getString("USER_ID", "Guest").equals(vo.getUserID())) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(EpisodeCommentActivity.this);
                        builder.setTitle("?????? ?????? ??????");
                        builder.setMessage("????????? ?????? ?????????????????????????");
                        builder.setPositiveButton("???", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                deleteComment(vo.getCommentID());
                            }
                        });

                        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(EpisodeCommentActivity.this);
                        builder.setTitle("?????? ?????? ??????");
                        builder.setMessage("????????? ?????? ?????????????????????????");
                        builder.setPositiveButton("???", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
//                                        Intent intent = new Intent(ChatActivity.this, ReportPopup.class);
                                Intent intent = new Intent(EpisodeCommentActivity.this, ReportSelectActivity.class);
                                intent.putExtra("COMMENT_ID", vo.getCommentID());
                                startActivity(intent);
                            }
                        });

                        builder.setNegativeButton("??????", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                }
            });
/*
            if(vo.getUserID().equals(pref.getString("USER_ID", "Guest"))) {
                replyBtn.setVisibility(View.GONE);
            } else {
                replyBtn.setVisibility(View.VISIBLE);
                replyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CommentVO vo = commentList.get(groupPosition);
                        strParentName = vo.getUserName();
                        strParentName = "@" + strParentName;
                        nParentID = vo.getCommentID();
                        nParentChatID = vo.getChatID();

                        setParent();
                    }
                });
            }

 */
/*
            emptyIconView.setVisibility(View.GONE);
            lv1IconView.setVisibility(View.GONE);
            lv5IconView.setVisibility(View.GONE);
            lv10IconView.setVisibility(View.GONE);
            smallLv10View.setVisibility(View.GONE);

            int nLevel = CommonUtils.getLevel(vo.getnDonationCarrot());

            switch(nLevel) {
                case 1:
                    levelBGView.setBackgroundResource(R.drawable.lv1_bg);
                    smallLvView.setBackgroundResource(R.drawable.lv1_bg);
                    smallLvView.setText("LV.1");
                    lv1IconView.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    levelBGView.setBackgroundResource(R.drawable.lv2_bg);
                    smallLvView.setBackgroundResource(R.drawable.lv2_bg);
                    smallLvView.setText("LV.2");
                    emptyIconView.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    levelBGView.setBackgroundResource(R.drawable.lv3_bg);
                    smallLvView.setBackgroundResource(R.drawable.lv3_bg);
                    smallLvView.setText("LV.3");
                    emptyIconView.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    levelBGView.setBackgroundResource(R.drawable.lv4_bg);
                    smallLvView.setBackgroundResource(R.drawable.lv4_bg);
                    smallLvView.setText("LV.4");
                    emptyIconView.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    levelBGView.setBackgroundResource(R.drawable.lv5_bg);
                    smallLvView.setBackgroundResource(R.drawable.lv5_bg);
                    smallLvView.setText("LV.5");
                    lv5IconView.setVisibility(View.VISIBLE);
                    break;
                case 6:
                    levelBGView.setBackgroundResource(R.drawable.lv6_bg);
                    smallLvView.setBackgroundResource(R.drawable.lv6_bg);
                    smallLvView.setText("LV.6");
                    emptyIconView.setVisibility(View.VISIBLE);
                    break;
                case 7:
                    levelBGView.setBackgroundResource(R.drawable.lv7_bg);
                    smallLvView.setBackgroundResource(R.drawable.lv7_bg);
                    smallLvView.setText("LV.7");
                    emptyIconView.setVisibility(View.VISIBLE);
                    break;
                case 8:
                    levelBGView.setBackgroundResource(R.drawable.lv8_bg);
                    smallLvView.setBackgroundResource(R.drawable.lv8_bg);
                    smallLvView.setText("LV.8");
                    emptyIconView.setVisibility(View.VISIBLE);
                    break;
                case 9:
                    levelBGView.setBackgroundResource(R.drawable.lv9_bg);
                    smallLvView.setBackgroundResource(R.drawable.lv9_bg);
                    smallLvView.setText("LV.9");
                    emptyIconView.setVisibility(View.VISIBLE);
                    break;
                case 10:
                    levelBGView.setBackgroundResource(R.drawable.lv10_bg);
                    smallLvView.setVisibility(View.GONE);
                    lv10IconView.setVisibility(View.VISIBLE);
                    smallLv10View.setVisibility(View.VISIBLE);
                    break;
            }
            */
            return convertView;
        }
        int dpToPx(float dp)
        {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
        }
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            Context context = parent.getContext();

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.chat_comment_row, parent, false);
            }

            CommentVO vo = getChild(groupPosition, childPosition);

            if(vo == null)
                return convertView;

            RelativeLayout bgView = convertView.findViewById(R.id.bgView);
            ImageView faceView = convertView.findViewById(R.id.faceView);
            TextView nameView = convertView.findViewById(R.id.nameView);
            TextView dateView = convertView.findViewById(R.id.dateTimeView);
            TextView commentView = convertView.findViewById(R.id.commentView);
            LinearLayout likeLayout = convertView.findViewById(R.id.likeLayout);
            TextView likeCountView = convertView.findViewById(R.id.likeCountView);
          //  TextView episodeNumView = convertView.findViewById(R.id.episodeNumView);
            ImageView thumbIconView = convertView.findViewById(R.id.thumbIconView);
            ImageView arrowBtn = convertView.findViewById(R.id.arrowBtn);
            TextView reportBtn = convertView.findViewById(R.id.reportBtn);
            TextView replyBtn = convertView.findViewById(R.id.replyBtn);

            arrowBtn.setVisibility(View.INVISIBLE);

            nameView.setTextColor(Color.parseColor("#ffffff"));
            dateView.setTextColor(Color.parseColor("#b9d4ff"));
            commentView.setTextColor(Color.parseColor("#ffffff"));
            reportBtn                          .setTextColor(Color.parseColor("#ffffff"));

            //

            ViewGroup.MarginLayoutParams lp0 = (ViewGroup.MarginLayoutParams) bgView.getLayoutParams();
            lp0.leftMargin = dpToPx(20);


         //   bgView.setPadding(0,0,0,0);


            bgView.setBackgroundResource(R.drawable.round_shadow_gray_bg_rev);

            String strPhoto = vo.getUserPhoto();
            if(strPhoto != null && !strPhoto.equals("null") && !strPhoto.equals("NULL") && strPhoto.length() > 0) {
                if(!strPhoto.startsWith("http"))
                    strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

                Glide.with(EpisodeCommentActivity.this)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(strPhoto)
                        .apply(new RequestOptions().circleCrop())
                        .into(faceView);
            } else {
                Glide.with(EpisodeCommentActivity.this)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(R.drawable.user_icon)
                        .apply(new RequestOptions().circleCrop())
                        .into(faceView);
            }

            faceView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(EpisodeCommentActivity.this, WriterMainActivity.class);
                    intent.putExtra("USER_ID", vo.getUserID());
                    intent.putExtra("WRITER", false);
                    startActivity(intent);
                }
            });

            nameView.setText(vo.getUserName());
//            String strTime = vo.getRegisterDate();
//            strTime = strTime.substring(0, 10) + "\n" + strTime.substring(11, 16);
            dateView.setText(CommonUtils.strGetTime(vo.getRegisterDate()));
            commentView.setText(vo.getStrComment());

    //        likeLayout.setVisibility(View.VISIBLE);
    //        likeCountView.setText(vo.getLikeCount() + "");

            int nOrder = vo.getnEpisodeOrder();
/*
            if(nOrder == 0)
                episodeNumView.setText(vo.getStrWorkTitle());
            else
                episodeNumView.setText(vo.getStrWorkTitle() + "(" + vo.getnEpisodeOrder() + "???)");

            if(episodeNumView.getText().toString().equals("null"))
                episodeNumView.setText("????????????");

 */
/*
            if(vo.isMyComment()) {
                likeLayout.setBackgroundResource(R.drawable.round_blue_btn_bg);
                likeCountView.setTextColor(Color.parseColor("#ffffff"));
                thumbIconView.setBackgroundResource(R.drawable.white_like_box);
//                heartView.setBackgroundResource(R.drawable.top_like_active);
//                heartView.getBackground().setColorFilter(null);
            } else {
                likeLayout.setBackgroundResource(R.drawable.badge_complete);
                likeCountView.setTextColor(ContextCompat.getColor(EpisodeCommentActivity.this, R.color.colorPrimary));
                thumbIconView.setBackgroundResource(R.drawable.like_box);
//                heartView.setBackgroundResource(R.drawable.top_like);
//                PorterDuffColorFilter greyFilter = new PorterDuffColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
//                heartView.getBackground().setColorFilter(greyFilter);
            }

            likeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(vo.getUserID().equals(pref.getString("USER_ID", "Guest"))) {
                        Toast.makeText(context, "??? ???????????? ???????????? ????????? ??? ????????????.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(vo.isMyComment()) {
                        vo.setLikeCount(vo.getLikeCount()-1);
                        vo.setMyComment(false);
                        requestReleaseLikeComment(vo.getCommentID());
                    } else {
                        vo.setLikeCount(vo.getLikeCount()+1);
                        vo.setMyComment(true);
                        requestLikeComment(vo.getCommentID());
                    }
                }
            });

 */

            if(vo.getUserID().equals(pref.getString("USER_ID", "Guest")) || pref.getString("ADMIN", "N").equals("Y")) {
                reportBtn.setText("??????");
            } else {
                reportBtn.setText("??????");
            }

            reportBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(pref.getString("ADMIN", "N").equals("Y") || pref.getString("USER_ID", "Guest").equals(vo.getUserID())) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(EpisodeCommentActivity.this);
                        builder.setTitle("?????? ?????? ??????");
                        builder.setMessage("????????? ?????? ?????????????????????????");
                        builder.setPositiveButton("???", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                deleteComment(vo.getCommentID());
                            }
                        });

                        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(EpisodeCommentActivity.this);
                        builder.setTitle("?????? ?????? ??????");
                        builder.setMessage("????????? ?????? ?????????????????????????");
                        builder.setPositiveButton("???", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
//                                        Intent intent = new Intent(ChatActivity.this, ReportPopup.class);
                                Intent intent = new Intent(EpisodeCommentActivity.this, ReportSelectActivity.class);
                                intent.putExtra("COMMENT_ID", vo.getCommentID());
                                startActivity(intent);
                            }
                        });

                        builder.setNegativeButton("??????", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                }
            });

        //    replyBtn.setVisibility(View.GONE);
//            replyBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    CommentVO vo = commentList.get(groupPosition);
//                    strParentName = vo.getUserName();
//                    strParentName = "@" + strParentName;
//                    nParentID = vo.getCommentID();
//                    nParentChatID = vo.getChatID();
//
//                    setParent();
//                }
//            });

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        //???????????? ????????? ???????????? ??????
        public void addItem(int groupPosition, CommentVO item) {
            childItems.get(groupPosition).add(item);
        }

        //????????? ???????????? ??????
        public void removeChild(int groupPosition, int childPosition) {
            childItems.get(groupPosition).remove(childPosition);
        }
    }

    private void requestLikeComment(final int nCommentID) {
        CommonUtils.showProgressDialog(EpisodeCommentActivity.this, "????????? ?????????");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestLikeComment(new OkHttpClient(), nCommentID, pref.getString("USER_ID", "Guest"));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(bResult) {
//                            getCommentData();
                            expandableAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(EpisodeCommentActivity.this, "????????? ????????? ?????????????????????. ????????? ?????? ????????? ?????????.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void requestReleaseLikeComment(final int nCommentID) {
        CommonUtils.showProgressDialog(EpisodeCommentActivity.this, "????????? ?????????");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestReleaseLikeComment(new OkHttpClient(), nCommentID, pref.getString("USER_ID", "Guest"));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(bResult) {
                            expandableAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(EpisodeCommentActivity.this, "????????? ????????? ?????????????????????. ????????? ?????? ????????? ?????????.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();
    }

    public void OnClickSendBtn(View view) {
        boolean bLogin = CommonUtils.bLocinCheck(pref);
        if(!bLogin && !showLogin) {
            Toast.makeText(EpisodeCommentActivity.this, "???????????? ????????? ???????????????. ????????? ????????????.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(EpisodeCommentActivity.this, PanbookLoginActivity.class));
            showLogin = true;
            inputTextView.setText("");
            return;
        }

        String strComment = inputTextView.getText().toString();

        if(strComment.length() == 0) {
            Toast.makeText(this, "?????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(strParentName == null || strParentName.length() == 0 || (strParentName.length() > 0 && !strComment.contains(strParentName))) {
            nParentID = -1;
            nParentChatID = -1;
            strParentName = null;
        } else {
            if(strComment.equals(strParentName) || strComment.equals(strParentName + " ")) {
                Toast.makeText(this, "?????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                return;
            }

            strComment = strComment.substring(strParentName.length(), strComment.length());
            if(strComment.startsWith(" "))
                strComment = strComment.substring(1);
        }

        requestSendComment(strComment);
//        setParent(strComment);
    }

    private void setParent() {
        String strCurrent = inputTextView.getText().toString();

        if(strCurrent.startsWith("@")) {
            String[] split = strCurrent.split(" ");

            if(split.length > 1)
                strCurrent = split[1];
            else
                strCurrent = "";
        }

        strCurrent = strParentName + " " + strCurrent;

        int nStart = 0;
        int nEnd = strParentName.length();

        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append(strCurrent);

        BitmapDrawable bd = (BitmapDrawable) convertViewToDrawable(createTokenView(strParentName));
        bd.setBounds(0, 0, bd.getIntrinsicWidth(), bd.getIntrinsicHeight());
        sb.setSpan(new ImageSpan(bd), nStart, nEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        inputTextView.setText("");
        inputTextView.setText(sb);
        inputTextView.setSelection(sb.length());
        imm.showSoftInput(inputTextView, InputMethodManager.SHOW_FORCED);

    }

    public Object convertViewToDrawable(View view) {
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(spec, spec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        Bitmap b = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(b);

        c.translate(-view.getScrollX(), -view.getScrollY());
        view.draw(c);
        view.setDrawingCacheEnabled(true);
        Bitmap cacheBmp = view.getDrawingCache();
        Bitmap viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true);
        view.destroyDrawingCache();
        return new BitmapDrawable(getResources(), viewBmp);
    }

    public View createTokenView(String text) {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setBackgroundResource(R.drawable.bordered_rectangle_rounded_corners);

        TextView tv = new TextView(this);
        l.addView(tv);
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);

        return l;
    }

    private void deleteComment(int nCommentID) {
        CommonUtils.showProgressDialog(EpisodeCommentActivity.this, "????????? ???????????? ????????????.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestDeleteComment(new OkHttpClient(), nCommentID, pref.getString("USER_ID", "Guest"));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(bResult) {
                            Toast.makeText(EpisodeCommentActivity.this, "????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                            getCommentData();
                        } else {
                            Toast.makeText(EpisodeCommentActivity.this, "????????? ???????????? ???????????????. ?????? ??? ?????? ????????? ?????????.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();
    }
}
