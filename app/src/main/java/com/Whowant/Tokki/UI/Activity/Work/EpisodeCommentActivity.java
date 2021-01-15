package com.Whowant.Tokki.UI.Activity.Work;

import android.annotation.SuppressLint;
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

public class EpisodeCommentActivity extends AppCompatActivity {                                             // 작품 회차별 댓글 페이지.
    private ExpandableListView listView;
    private LinearLayout emptyLayout;
    private EditText inputTextView;

    private int nWorkID;
    private int nEpisodeID;

    private ArrayList<CommentVO> commentList;                                                               // 댓글 목록
    private ArrayList<ArrayList<CommentVO>> subCommentList = new ArrayList<>();                             // 대댓글 목록 - 댓글과 대댓글로 구분하여 ExpandibleListView 로 표현한다
    private CExpandableListviewAdapter expandableAdapter;
    private SharedPreferences pref;

    private String strParentName;
    private int nParentID = -1;
    private int nParentChatID = -1;
    private boolean showLogin = false;
    private int nOrder = 2;                 // 1 : 인기순, 2 : 최신순
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
        titleView.setText("댓글");

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
                    Toast.makeText(EpisodeCommentActivity.this, "로그인이 필요한 기능입니다. 로그인 해주세요.", Toast.LENGTH_SHORT).show();
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

    public void onClickTopRightBtn(View view) {                                         // 우측 상단 메뉴 클릭하여 popup 메뉴 호출 -> 정렬
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
        CommonUtils.showProgressDialog(this, "댓글을 전송하고 있습니다. 잠시만 기다려주세요.");

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
                            Toast.makeText(EpisodeCommentActivity.this, "댓글 등록에 실패하였습니다. 잠시후 다시 이용해 주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            getCommentData();
                            inputTextView.setText("");
                        }
                    }
                });
            }
        }).start();
    }

    private void getCommentData() {
        commentList.clear();
        subCommentList.clear();

        CommonUtils.showProgressDialog(this, "댓글 목록을 가져오고 있습니다. 잠시만 기다려주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject resultObject = null;

                if(nEpisodeID > 0)                          // 에피소드 아이디가 있는 경우 회차별 댓글로 보고, 없는 경우 작품 자체의 댓글로 판단
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

                    reOrderCommentList(resultArray);                                                                    ///  대댓글 순서를 정리하는 펑션
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(result == null) {
                            Toast.makeText(EpisodeCommentActivity.this, "댓글 목록을 가져오는데 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
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

    private void reOrderCommentList(JSONArray array) {                          // 댓글과 대댓글을 리오더링 하는 펑션. 인기순 댓글일때는 적용하지 않는다        c
        ArrayList<CommentVO> subList = new ArrayList<>();

        try {
            for(int i = 0 ; i < array.length() ; i++) {                                     // Parent Group 생성
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
        ArrayList<CommentVO> parentItems; //부모 리스트를 담을 배열
        ArrayList<ArrayList<CommentVO>> childItems; //자식 리스트를 담을 배열

        //각 리스트의 크기 반환
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

        //리스트의 아이템 반환
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

        //리스트 아이템의 id 반환
        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        //동일한 id가 항상 동일한 개체를 참조하는지 여부를 반환
        @Override
        public boolean hasStableIds() {
            return true;
        }

        //리스트 각각의 row에 view를 설정
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            Context context = parent.getContext();

            //convertView가 비어있을 경우 xml파일을 inflate 해줌
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
            TextView episodeNumView = convertView.findViewById(R.id.episodeNumView);
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
                arrowBtn.setVisibility(View.INVISIBLE);
            }

            if(isExpanded) {
                arrowBtn.setBackgroundResource(R.drawable.up_arrow_btn);
            } else {
                arrowBtn.setBackgroundResource(R.drawable.down_arrow_btn);
            }

            if(vo.getUserID().equals(pref.getString("USER_ID", "Guest")) || pref.getString("ADMIN", "N").equals("Y")) {
                reportBtn.setText("삭제");
            } else {
                reportBtn.setText("신고");
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

            likeLayout.setVisibility(View.VISIBLE);
            likeCountView.setText(vo.getLikeCount() + "");

            int nOrder = vo.getnEpisodeOrder();

            if(nOrder == 0)
                episodeNumView.setText(vo.getStrWorkTitle());
            else
                episodeNumView.setText(vo.getStrWorkTitle() + "(" + vo.getnEpisodeOrder() + "화)");

            if(episodeNumView.getText().toString().equals("null"))
                episodeNumView.setText("전체댓글");

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
                        Toast.makeText(context, "내 댓글에는 좋아요를 표시할 수 없습니다.", Toast.LENGTH_SHORT).show();
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

            reportBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(pref.getString("ADMIN", "N").equals("Y") || pref.getString("USER_ID", "Guest").equals(vo.getUserID())) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(EpisodeCommentActivity.this);
                        builder.setTitle("댓글 삭제 알림");
                        builder.setMessage("댓글을 정말 삭제하시겠습니까?");
                        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                deleteComment(vo.getCommentID());
                            }
                        });

                        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(EpisodeCommentActivity.this);
                        builder.setTitle("댓글 신고 알림");
                        builder.setMessage("댓글을 정말 신고하시겠습니까?");
                        builder.setPositiveButton("예", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
//                                        Intent intent = new Intent(ChatActivity.this, ReportPopup.class);
                                Intent intent = new Intent(EpisodeCommentActivity.this, ReportSelectActivity.class);
                                intent.putExtra("COMMENT_ID", vo.getCommentID());
                                startActivity(intent);
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
                }
            });

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

            return convertView;
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
            TextView episodeNumView = convertView.findViewById(R.id.episodeNumView);
            ImageView thumbIconView = convertView.findViewById(R.id.thumbIconView);
            ImageView arrowBtn = convertView.findViewById(R.id.arrowBtn);
            TextView reportBtn = convertView.findViewById(R.id.reportBtn);
            TextView replyBtn = convertView.findViewById(R.id.replyBtn);

            arrowBtn.setVisibility(View.INVISIBLE);
            bgView.setBackgroundResource(R.drawable.round_shadow_gray_bg);

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

            likeLayout.setVisibility(View.VISIBLE);
            likeCountView.setText(vo.getLikeCount() + "");

            int nOrder = vo.getnEpisodeOrder();

            if(nOrder == 0)
                episodeNumView.setText(vo.getStrWorkTitle());
            else
                episodeNumView.setText(vo.getStrWorkTitle() + "(" + vo.getnEpisodeOrder() + "화)");

            if(episodeNumView.getText().toString().equals("null"))
                episodeNumView.setText("전체댓글");

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
                        Toast.makeText(context, "내 댓글에는 좋아요를 표시할 수 없습니다.", Toast.LENGTH_SHORT).show();
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

            if(vo.getUserID().equals(pref.getString("USER_ID", "Guest")) || pref.getString("ADMIN", "N").equals("Y")) {
                reportBtn.setText("삭제");
            } else {
                reportBtn.setText("신고");
            }

            reportBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(pref.getString("ADMIN", "N").equals("Y") || pref.getString("USER_ID", "Guest").equals(vo.getUserID())) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(EpisodeCommentActivity.this);
                        builder.setTitle("댓글 삭제 알림");
                        builder.setMessage("댓글을 정말 삭제하시겠습니까?");
                        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                deleteComment(vo.getCommentID());
                            }
                        });

                        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(EpisodeCommentActivity.this);
                        builder.setTitle("댓글 신고 알림");
                        builder.setMessage("댓글을 정말 신고하시겠습니까?");
                        builder.setPositiveButton("예", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
//                                        Intent intent = new Intent(ChatActivity.this, ReportPopup.class);
                                Intent intent = new Intent(EpisodeCommentActivity.this, ReportSelectActivity.class);
                                intent.putExtra("COMMENT_ID", vo.getCommentID());
                                startActivity(intent);
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
                }
            });

            replyBtn.setVisibility(View.GONE);
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

        //리스트에 새로운 아이템을 추가
        public void addItem(int groupPosition, CommentVO item) {
            childItems.get(groupPosition).add(item);
        }

        //리스트 아이템을 삭제
        public void removeChild(int groupPosition, int childPosition) {
            childItems.get(groupPosition).remove(childPosition);
        }
    }

    private void requestLikeComment(final int nCommentID) {
        CommonUtils.showProgressDialog(EpisodeCommentActivity.this, "좋아요 표시중");

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
                            Toast.makeText(EpisodeCommentActivity.this, "좋아요 표시에 실패하였습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void requestReleaseLikeComment(final int nCommentID) {
        CommonUtils.showProgressDialog(EpisodeCommentActivity.this, "좋아요 표시중");

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
                            Toast.makeText(EpisodeCommentActivity.this, "좋아요 표시에 실패하였습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();
    }

    public void OnClickSendBtn(View view) {
        boolean bLogin = CommonUtils.bLocinCheck(pref);
        if(!bLogin && !showLogin) {
            Toast.makeText(EpisodeCommentActivity.this, "로그인이 필요한 기능입니다. 로그인 해주세요.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(EpisodeCommentActivity.this, PanbookLoginActivity.class));
            showLogin = true;
            inputTextView.setText("");
            return;
        }

        String strComment = inputTextView.getText().toString();

        if(strComment.length() == 0) {
            Toast.makeText(this, "댓글 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(strParentName == null || strParentName.length() == 0 || (strParentName.length() > 0 && !strComment.contains(strParentName))) {
            nParentID = -1;
            nParentChatID = -1;
            strParentName = null;
        } else {
            if(strComment.equals(strParentName) || strComment.equals(strParentName + " ")) {
                Toast.makeText(this, "댓글 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
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
        CommonUtils.showProgressDialog(EpisodeCommentActivity.this, "댓글을 삭제하고 있습니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestDeleteComment(new OkHttpClient(), nCommentID, pref.getString("USER_ID", "Guest"));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(bResult) {
                            Toast.makeText(EpisodeCommentActivity.this, "댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            getCommentData();
                        } else {
                            Toast.makeText(EpisodeCommentActivity.this, "댓글을 삭제하지 못했습니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();
    }
}
