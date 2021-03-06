package com.Whowant.Tokki.UI.Activity.Main;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
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

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Login.PanbookLoginActivity;
import com.Whowant.Tokki.UI.Activity.Work.EpisodeCommentActivity;
import com.Whowant.Tokki.UI.Activity.Work.ReportSelectActivity;
import com.Whowant.Tokki.UI.Activity.Writer.WriterMainActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.WriterChatVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.OkHttpClient;

public class ChatActivity extends AppCompatActivity {
    private ExpandableListView listView;
    private LinearLayout emptyLayout;
    private EditText inputTextView;
    private ImageView faceView;

    private String strWriterID;

    private ArrayList<WriterChatVO> commentList;
    private ArrayList<ArrayList<WriterChatVO>> subCommentList = new ArrayList<>();
    private CExpandableListviewAdapter expandableAdapter;
    private SharedPreferences pref;

    private String strParentName;
    private int nParentID = -1;
    private InputMethodManager imm;
    private boolean showLogin = false;
    private Button sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_tokkitalk);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        listView = findViewById(R.id.listView);
        emptyLayout = findViewById(R.id.emptyLayout);
        inputTextView = findViewById(R.id.inputTextView);
        sendBtn = findViewById(R.id.sendBtn);
        faceView = findViewById(R.id.faceView);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean bLogin = CommonUtils.bLocinCheck(pref);

                if(!bLogin && !showLogin) {
                    Toast.makeText(ChatActivity.this, "???????????? ????????? ???????????????. ????????? ????????????.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ChatActivity.this, PanbookLoginActivity.class));
                    showLogin = true;
                    inputTextView.setText("");
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
        strWriterID = getIntent().getStringExtra("WRITER_ID");

        pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

        commentList = new ArrayList<>();
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                return false;
            }
        });


//        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
//                WriterChatVO vo = commentList.get(position);
//
//                PopupMenu popup = new PopupMenu(ChatActivity.this, view);
//                if(pref.getString("ADMIN", "N").equals("Y") || vo.getUserID().equals(pref.getString("USER_ID", "Guest")))
//                    popup.getMenuInflater().inflate(R.menu.comment_admin_menu, popup.getMenu());
//                else
//                    popup.getMenuInflater().inflate(R.menu.comment_menu, popup.getMenu());
//
//                popup.getMenuInflater().inflate(R.menu.comment_menu, popup.getMenu());
//                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    public boolean onMenuItemClick(MenuItem item) {
//                        Intent intent = null;
//
//                        switch(item.getItemId()) {
//                            case R.id.delete: {
//                                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
//                                builder.setTitle("?????? ?????? ??????");
//                                builder.setMessage("????????? ?????? ?????????????????????????");
//                                builder.setPositiveButton("???", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        deleteComment(vo.getCommentID());
//                                    }
//                                });
//
//                                builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int id) {
//                                    }
//                                });
//
//                                AlertDialog alertDialog = builder.create();
//                                alertDialog.show();
//                            }
//                            break;
//                            case R.id.report: {
//                                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
//                                builder.setTitle("?????? ?????? ??????");
//                                builder.setMessage("????????? ?????? ?????????????????????????");
//                                builder.setPositiveButton("???", new DialogInterface.OnClickListener(){
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int id) {
////                                        Intent intent = new Intent(ChatActivity.this, ReportPopup.class);
//                                        Intent intent = new Intent(ChatActivity.this, ReportSelectActivity.class);
//                                        intent.putExtra("COMMENT_ID", vo.getCommentID());
//                                        startActivity(intent);
//                                    }
//                                });
//
//                                builder.setNegativeButton("??????", new DialogInterface.OnClickListener(){
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int id) {
//                                    }
//                                });
//
//                                AlertDialog alertDialog = builder.create();
//                                alertDialog.show();
//                            }
//                            break;
//                        }
//                        return true;
//                    }
//                });
//
//                popup.show();//showing popup menu
//                return true;
//            }
//        });

        getCommentData();
    }

    @Override
    public void onResume() {
        super.onResume();
        showLogin =  false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                imm.hideSoftInputFromWindow(inputTextView.getWindowToken(), 0);
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        finish();
                    }
                }, 200);

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getCommentData() {
        CommonUtils.showProgressDialog(this, "????????? ???????????? ????????????. ????????? ??????????????????.");
        commentList.clear();
        subCommentList.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONArray resultArray = HttpClient.getWriterChatComment(new OkHttpClient(), strWriterID);

                if(resultArray != null) {
                    reOrderCommentList(resultArray);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(resultArray == null) {
                            Toast.makeText(ChatActivity.this, "????????? ??????????????? ??????????????????. ????????? ?????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
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

    private void reOrderCommentList(JSONArray array) {
        ArrayList<WriterChatVO> subList = new ArrayList<>();

        try {
            for(int i = 0 ; i < array.length() ; i++) {                                     // Parent Group ??????
                JSONObject object = array.getJSONObject(i);
                if(object.getInt("COMMENT_ID") == 0)
                    continue;

                WriterChatVO vo = new WriterChatVO();
                vo.setCommentID(object.getInt("COMMENT_ID"));
                vo.setStrWriterID(object.getString("WRITER_ID"));
                vo.setParentID(object.getInt("PARENT_ID"));
                vo.setStrComment(object.getString("COMMENT"));
                vo.setRegisterDate(object.getString("REGISTER_DATE"));
                vo.setUserName(object.getString("USER_NAME"));
                vo.setUserPhoto(object.getString("USER_PHOTO"));
                vo.setUserID(object.getString("USER_ID"));
                vo.setnUserDonationCarrot(object.getInt("DONATION_CARROT"));

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

                WriterChatVO vo = new WriterChatVO();
                vo.setCommentID(object.getInt("COMMENT_ID"));
                vo.setStrWriterID(object.getString("WRITER_ID"));
                vo.setParentID(object.getInt("PARENT_ID"));
                vo.setStrComment(object.getString("COMMENT"));
                vo.setRegisterDate(object.getString("REGISTER_DATE"));
                vo.setUserName(object.getString("USER_NAME"));
                vo.setUserPhoto(object.getString("USER_PHOTO"));
                vo.setUserID(object.getString("USER_ID"));
                vo.setnUserDonationCarrot(object.getInt("DONATION_CARROT"));

                int nParentID = object.getInt("PARENT_ID");
                if(nParentID > -1) {
                    for(int j = 0 ; j < commentList.size() ; j++) {
                        WriterChatVO tempVO = commentList.get(j);
                        subList = subCommentList.get(j);
                        if(subList == null)
                            subList = new ArrayList<>();

                        int nCommentID = tempVO.getCommentID();

                        if(nParentID == nCommentID) {
                            WriterChatVO subVO = new WriterChatVO();
                            subVO.setCommentID(object.getInt("COMMENT_ID"));
                            subVO.setStrWriterID(object.getString("WRITER_ID"));
                            subVO.setParentID(object.getInt("PARENT_ID"));
                            subVO.setStrComment(object.getString("COMMENT"));
                            subVO.setRegisterDate(object.getString("REGISTER_DATE"));
                            subVO.setUserName(object.getString("USER_NAME"));
                            subVO.setUserPhoto(object.getString("USER_PHOTO"));
                            subVO.setUserID(object.getString("USER_ID"));
                            subVO.setnUserDonationCarrot(object.getInt("DONATION_CARROT"));
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
        ArrayList<WriterChatVO> parentItems; //?????? ???????????? ?????? ??????
        ArrayList<ArrayList<WriterChatVO>> childItems; //?????? ???????????? ?????? ??????

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
        public WriterChatVO getGroup(int groupPosition) {
            return parentItems.get(groupPosition);
        }

        @Override
        public WriterChatVO getChild(int groupPosition, int childPosition) {
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
                convertView = inflater.inflate(R.layout.tokki_tok_row, parent, false);
            }

            WriterChatVO vo = commentList.get(groupPosition);

            RelativeLayout bgView = convertView.findViewById(R.id.bgView);
            ImageView faceView = convertView.findViewById(R.id.faceView);
            TextView nameView = convertView.findViewById(R.id.nameView);
            TextView dateView = convertView.findViewById(R.id.dateTimeView);
            TextView commentView = convertView.findViewById(R.id.commentView);
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

            if(vo.getUserID().equals(pref.getString("USER_ID", "Guest")) || pref.getString("ADMIN", "N").equals("Y")) {
                reportBtn.setText("??????");
//                reportBtn.setVisibility(View.INVISIBLE);
//                replyBtn.setVisibility(View.VISIBLE);
            } else {
                reportBtn.setText("??????");
            }

            reportBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(vo.getUserID().equals(pref.getString("USER_ID", "Guest")) || pref.getString("ADMIN", "N").equals("Y")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                        builder.setTitle("??? ?????? ??????");
                        builder.setMessage("?????? ?????? ?????????????????????????");
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                        builder.setTitle("??? ?????? ??????");
                        builder.setMessage("?????? ?????? ?????????????????????????");
                        builder.setPositiveButton("???", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
//                                        Intent intent = new Intent(ChatActivity.this, ReportPopup.class);
                                Intent intent = new Intent(ChatActivity.this, ReportSelectActivity.class);
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

            if(vo.getUserID().equals(pref.getString("USER_ID", "Guest"))) {
                replyBtn.setVisibility(View.GONE);
            } else {
                replyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        WriterChatVO vo = commentList.get(groupPosition);
                        strParentName = vo.getUserName();
                        strParentName = "@" + strParentName;
                        nParentID = vo.getCommentID();

                        setParent();
                    }
                });
            }

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

            faceView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ChatActivity.this, WriterMainActivity.class);
                    intent.putExtra("USER_ID", vo.getUserID());
                    intent.putExtra("WRITER", false);
                    startActivity(intent);
                }
            });

            arrowBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isExpanded) {
                        listView.collapseGroup(groupPosition);
                    } else {
                        listView.expandGroup(groupPosition, true);
                    }
                }
            });

            bgView.setBackgroundResource(R.drawable.round_shadow_btn_white_bg);

            String strPhoto = vo.getUserPhoto();
            if(strPhoto != null && !strPhoto.equals("null") && !strPhoto.equals("NULL") && strPhoto.length() > 0) {
                if(!strPhoto.startsWith("http"))
                    strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

                Glide.with(ChatActivity.this)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(strPhoto)
                        .placeholder(R.drawable.user_icon)
                        .apply(new RequestOptions().circleCrop())
                        .into(faceView);
            } else {
                Glide.with(ChatActivity.this)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(R.drawable.user_icon)
                        .apply(new RequestOptions().circleCrop())
                        .into(faceView);
            }

            nameView.setText(vo.getUserName());
//            String strTime = vo.getRegisterDate();
//            strTime = strTime.substring(0, 10) + "\n" + strTime.substring(11, 16);
            dateView.setText(CommonUtils.strGetTime(vo.getRegisterDate()));
            commentView.setText(vo.getStrComment());

            emptyIconView.setVisibility(View.GONE);
            lv1IconView.setVisibility(View.GONE);
            lv5IconView.setVisibility(View.GONE);
            lv10IconView.setVisibility(View.GONE);
            smallLv10View.setVisibility(View.GONE);

            int nLevel = CommonUtils.getLevel(vo.getnUserDonationCarrot());
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
                convertView = inflater.inflate(R.layout.tokki_tok_row, parent, false);
            }

            WriterChatVO vo = getChild(groupPosition, childPosition);

            if(vo == null)
                return convertView;

            RelativeLayout bgView = convertView.findViewById(R.id.bgView);
            ImageView faceView = convertView.findViewById(R.id.faceView);
            TextView nameView = convertView.findViewById(R.id.nameView);
            TextView dateView = convertView.findViewById(R.id.dateTimeView);
            TextView commentView = convertView.findViewById(R.id.commentView);
            ImageView arrowBtn = convertView.findViewById(R.id.arrowBtn);
            TextView reportBtn = convertView.findViewById(R.id.reportBtn);
//            ImageView menuBtn = convertView.findViewById(R.id.menuBtn);
            TextView replyBtn = convertView.findViewById(R.id.replyBtn);

            arrowBtn.setVisibility(View.INVISIBLE);
            bgView.setBackgroundResource(R.drawable.round_shadow_gray_bg);

            if(vo.getUserID().equals(pref.getString("USER_ID", "Guest")) || pref.getString("ADMIN", "N").equals("Y")) {
                reportBtn.setText("??????");
//                reportBtn.setVisibility(View.INVISIBLE);
//                replyBtn.setVisibility(View.VISIBLE);
            } else {
                reportBtn.setText("??????");
            }

            reportBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(vo.getUserID().equals(pref.getString("USER_ID", "Guest")) || pref.getString("ADMIN", "N").equals("Y")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                        builder.setTitle("??? ?????? ??????");
                        builder.setMessage("?????? ?????? ?????????????????????????");
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                        builder.setTitle("??? ?????? ??????");
                        builder.setMessage("?????? ?????? ?????????????????????????");
                        builder.setPositiveButton("???", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
//                                        Intent intent = new Intent(ChatActivity.this, ReportPopup.class);
                                Intent intent = new Intent(ChatActivity.this, ReportSelectActivity.class);
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

            String strPhoto = vo.getUserPhoto();
            if(strPhoto != null && !strPhoto.equals("null") && !strPhoto.equals("NULL") && strPhoto.length() > 0) {
                if(!strPhoto.startsWith("http"))
                    strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

                Glide.with(ChatActivity.this)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(strPhoto)
                        .apply(new RequestOptions().circleCrop())
                        .placeholder(R.drawable.user_icon)
                        .into(faceView);
            } else {
                Glide.with(ChatActivity.this)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(R.drawable.user_icon)
                        .apply(new RequestOptions().circleCrop())
                        .into(faceView);
            }

            nameView.setText(vo.getUserName());
//            String strTime = vo.getRegisterDate();
//            strTime = strTime.substring(0, 10) + "\n" + strTime.substring(11, 16);
            dateView.setText(CommonUtils.strGetTime(vo.getRegisterDate()));
            commentView.setText(vo.getStrComment());

            replyBtn.setVisibility(View.GONE);

            faceView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ChatActivity.this, WriterMainActivity.class);
                    intent.putExtra("USER_ID", vo.getUserID());
                    intent.putExtra("WRITER", false);
                    startActivity(intent);
                }
            });

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        //???????????? ????????? ???????????? ??????
        public void addItem(int groupPosition, WriterChatVO item) {
            childItems.get(groupPosition).add(item);
        }

        //????????? ???????????? ??????
        public void removeChild(int groupPosition, int childPosition) {
            childItems.get(groupPosition).remove(childPosition);
        }
    }

    public void OnClickSendBtn(View view) {
        String strComment = inputTextView.getText().toString();

        if(strComment.length() == 0) {
            Toast.makeText(this, "????????? ??????????????????.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(strParentName == null || strParentName.length() == 0 || (strParentName.length() > 0 && !strComment.contains(strParentName))) {
            nParentID = -1;
            strParentName = null;
        } else {
            if(strComment.equals(strParentName) || strComment.equals(strParentName + " ")) {
                Toast.makeText(this, "????????? ??????????????????.", Toast.LENGTH_SHORT).show();
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
        CommonUtils.showProgressDialog(ChatActivity.this, "????????? ???????????? ????????????.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestDeleteWriterChat(new OkHttpClient(), nCommentID);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(bResult) {
                            Toast.makeText(ChatActivity.this, "????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                            getCommentData();
                        } else {
                            Toast.makeText(ChatActivity.this, "????????? ???????????? ???????????????. ????????? ?????? ????????? ?????????.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void requestSendComment(final String strComment) {
        CommonUtils.showProgressDialog(this, "????????? ???????????? ????????????. ????????? ??????????????????.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> dataMap = new HashMap<>();
                dataMap.put("USER_ID", pref.getString("USER_ID", "Guest"));
                dataMap.put("COMMENT", strComment);
                dataMap.put("WRITER_ID", strWriterID);

                if(nParentID > -1)
                    dataMap.put("PARENT_ID", "" + nParentID);

                boolean bResult = HttpClient.requestSendWriterChat(new OkHttpClient(), dataMap);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(!bResult) {
                            Toast.makeText(ChatActivity.this, "????????? ?????????????????????. ????????? ?????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
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

    public void onClickTopLeftBtn(View view) {
        finish();
    }
}
