package com.Whowant.Tokki.UI.Activity.Work;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Login.LoginSelectActivity;
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
import java.util.List;

import okhttp3.OkHttpClient;

public class ChatCommentActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView listView;
    private LinearLayout emptyLayout;
    private EditText inputTextView;

    private int nChatID;
    private int nEpisodeID;

    private ArrayList<CommentVO> commentList;
    private CCommentArrayAdapter aa;
    private SharedPreferences pref;

    private String strParentName;
    private int nParentID = -1;
    private boolean showLogin = false;
    private Button sendBtn;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_comment);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        Thread.UncaughtExceptionHandler handler = Thread
                .getDefaultUncaughtExceptionHandler();

        if (!(handler instanceof CustomUncaughtExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
        }

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setTitle("댓글");

        TextView titleView = findViewById(R.id.titleView);
        titleView.setText("댓글");

        nChatID = getIntent().getIntExtra("CHAT_ID", 0);
        nEpisodeID = getIntent().getIntExtra("EPISODE_ID", 0);

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
                    Toast.makeText(ChatCommentActivity.this, "로그인이 필요한 기능입니다. 로그인 해주세요.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ChatCommentActivity.this, LoginSelectActivity.class));
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
        pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

        commentList = new ArrayList<>();
        aa = new CCommentArrayAdapter(this, R.layout.chat_comment_row, commentList);
        listView.setAdapter(aa);
        listView.setOnItemClickListener(this);

//        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
//                PopupMenu popup = new PopupMenu(ChatCommentActivity.this, view);
//                final CommentVO vo = commentList.get(position);
//
//                if(pref.getString("ADMIN", "N").equals("Y") || vo.getUserID().equals(pref.getString("USER_ID", "Guest")))
//                    popup.getMenuInflater().inflate(R.menu.comment_admin_menu, popup.getMenu());
//                else
//                    popup.getMenuInflater().inflate(R.menu.comment_menu, popup.getMenu());
//
//                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    public boolean onMenuItemClick(MenuItem item) {
//                        Intent intent = null;
//
//                        switch(item.getItemId()) {
//                            case R.id.delete: {
//                                AlertDialog.Builder builder = new AlertDialog.Builder(ChatCommentActivity.this);
//                                builder.setTitle("댓글 삭제 알림");
//                                builder.setMessage("댓글을 정말 삭제하시겠습니까?");
//                                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        deleteComment(vo.getCommentID());
//                                    }
//                                });
//
//                                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int id) {
//                                    }
//                                });
//
//                                AlertDialog alertDialog = builder.create();
//                                alertDialog.show();
//                            }
//                                break;
//                            case R.id.report: {
//                                AlertDialog.Builder builder = new AlertDialog.Builder(ChatCommentActivity.this);
//                                builder.setTitle("댓글 신고 알림");
//                                builder.setMessage("댓글을 정말 신고하시겠습니까?");
//                                builder.setPositiveButton("예", new DialogInterface.OnClickListener(){
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int id) {
////                                                deleteComment(vo.getCommentID());
////                                        Toast.makeText(ChatCommentActivity.this, "댓글을 신고하였습니다.", Toast.LENGTH_SHORT).show();
////                                        Intent intent = new Intent(ChatCommentActivity.this, ReportPopup.class);
//                                        Intent intent = new Intent(ChatCommentActivity.this, ReportSelectActivity.class);
//                                        intent.putExtra("COMMENT_ID", vo.getCommentID());
//                                        startActivity(intent);
//                                    }
//                                });
//
//                                builder.setNegativeButton("취소", new DialogInterface.OnClickListener(){
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int id) {
//                                    }
//                                });
//
//                                AlertDialog alertDialog = builder.create();
//                                alertDialog.show();
//                            }
//                                break;
//                        }
//                        return true;
//                    }
//                });
//
//                popup.show();//showing popup menu
//                return false;
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
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
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
                dataMap.put("CHAT_ID", "" + nChatID);

                if(nParentID > -1)
                    dataMap.put("PARENT_ID", "" + nParentID);

                boolean bResult = HttpClient.requestSendComment(new OkHttpClient(), dataMap);
                
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(!bResult) {
                            Toast.makeText(ChatCommentActivity.this, "댓글 등록에 실패하였습니다. 잠시후 다시 이용해 주세요.", Toast.LENGTH_SHORT).show();
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
        CommonUtils.showProgressDialog(this, "댓글 목록을 가져오고 있습니다. 잠시만 기다려주세요.");
        commentList.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONArray resultArray = HttpClient.getChatComment(new OkHttpClient(), nChatID);

                if(resultArray != null) {
                    reOrderCommentList(resultArray);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(resultArray == null) {
                            Toast.makeText(ChatCommentActivity.this, "댓글 목록을 가져오는데 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            setNoComment();
                            return;
                        }

                        if(commentList.size() == 0) {
                            setNoComment();
                            return;
                        }

                        listView.setVisibility(View.VISIBLE);
                        emptyLayout.setVisibility(View.INVISIBLE);
                        aa.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private void reOrderCommentList(JSONArray array) {
        commentList.clear();
        ArrayList<CommentVO> subCommentList = new ArrayList<>();
        ArrayList<Integer>   parentIDList = new ArrayList<>();

        try {
            for(int i = 0 ; i < array.length() ; i++) {
                JSONObject object = array.getJSONObject(i);

                CommentVO vo = new CommentVO();

                vo.setCommentID(object.getInt("COMMENT_ID"));
                vo.setEpisodeID(object.getInt("EPISODE_ID"));
                vo.setParentID(object.getInt("PARENT_ID"));
                vo.setStrComment(object.getString("COMMENT"));
                vo.setRegisterDate(object.getString("REGISTER_DATE"));
                vo.setUserName(object.getString("USER_NAME"));
                vo.setUserPhoto(object.getString("USER_PHOTO"));
                vo.setUserID(object.getString("USER_ID"));

                int nParentID = object.getInt("PARENT_ID");

                if(nParentID == -1) {
                    commentList.add(vo);
                    parentIDList.add(vo.getCommentID());
                } else {
                    subCommentList.add(vo);
                }
            }

            for(int i = 0 ; i < subCommentList.size() ; i++) {
                CommentVO vo = subCommentList.get(i);
                int nIndex = parentIDList.indexOf(vo.getParentID()) + 1;
                int nPlus = 0;

                while(true) {
                    if(nIndex + nPlus == commentList.size())
                        break;
                    else if(commentList.get(nIndex + nPlus).getParentID() > -1 && commentList.get(nIndex + nPlus).getParentID() == vo.getParentID()) {
                        nPlus ++;
                        continue;
                    } else
                        break;
                }

                commentList.add(nIndex + nPlus, vo);
                parentIDList.add(nIndex + nPlus, vo.getCommentID());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setNoComment() {
        listView.setVisibility(View.INVISIBLE);
        emptyLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        CommentVO vo = commentList.get(position);
        strParentName = vo.getUserName();
        strParentName = "@" + strParentName;
        nParentID = vo.getCommentID();

        setParent();
    }

    public class CCommentArrayAdapter extends ArrayAdapter<Object>
    {
        private LayoutInflater mLiInflater;

        CCommentArrayAdapter(Context context, int layout, List titles)
        {
            super(context, layout, titles);
            mLiInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            if(convertView == null)
                convertView = mLiInflater.inflate(R.layout.chat_comment_row, parent, false);

            CommentVO vo = commentList.get(position);

            RelativeLayout bgView = convertView.findViewById(R.id.bgView);
            ImageView faceView = convertView.findViewById(R.id.faceView);
            TextView nameView = convertView.findViewById(R.id.nameView);
            TextView dateView = convertView.findViewById(R.id.dateTimeView);
            TextView commentView = convertView.findViewById(R.id.commentView);
            TextView reportBtn = convertView.findViewById(R.id.reportBtn);

            if(vo.getParentID() > -1) {
                bgView.setBackgroundColor(getResources().getColor(R.color.colorCommonGray));
            } else {
                bgView.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            }

            String strPhoto = vo.getUserPhoto();
            if(strPhoto != null && !strPhoto.equals("null") && !strPhoto.equals("NULL") && strPhoto.length() > 0) {
                if(!strPhoto.startsWith("http"))
                    strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

                Glide.with(ChatCommentActivity.this)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(strPhoto)
                        .apply(new RequestOptions().circleCrop())
                        .into(faceView);
            } else {
                Glide.with(ChatCommentActivity.this)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(R.drawable.user_icon)
                        .apply(new RequestOptions().circleCrop())
                        .into(faceView);
            }

            nameView.setText(vo.getUserName());
            dateView.setText(CommonUtils.strGetTime(vo.getRegisterDate()));
            commentView.setText(vo.getStrComment());

            if(pref.getString("ADMIN", "N").equals("Y") || vo.getUserID().equals(pref.getString("USER_ID", "Guest"))) {
                reportBtn.setText("삭제");
            } else {
                reportBtn.setText("신고");
            }

            reportBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(pref.getString("ADMIN", "N").equals("Y") || vo.getUserID().equals(pref.getString("USER_ID", "Guest"))) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ChatCommentActivity.this);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(ChatCommentActivity.this);
                        builder.setTitle("댓글 신고 알림");
                        builder.setMessage("댓글을 정말 신고하시겠습니까?");
                        builder.setPositiveButton("예", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
//                                        Intent intent = new Intent(ChatActivity.this, ReportPopup.class);
                                Intent intent = new Intent(ChatCommentActivity.this, ReportSelectActivity.class);
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

            return convertView;
        }
    }

    public void OnClickSendBtn(View view) {
        boolean bLogin = CommonUtils.bLocinCheck(pref);

        if(!bLogin && !showLogin) {
            Toast.makeText(ChatCommentActivity.this, "로그인이 필요한 기능입니다. 로그인 해주세요.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ChatCommentActivity.this, LoginSelectActivity.class));
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

        if(strCurrent.startsWith(strParentName)) {
            strCurrent = strCurrent.substring(strParentName.length() + 1);
        }

        strCurrent = strParentName + " " + strCurrent;

        int nStart = 0;
        int nEnd = strParentName.length();

        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append(strCurrent);

        BitmapDrawable bd = (BitmapDrawable) convertViewToDrawable(createTokenView(strParentName));
        bd.setBounds(0, 0, bd.getIntrinsicWidth(), bd.getIntrinsicHeight());
        sb.setSpan(new ImageSpan(bd), nStart, nEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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
        CommonUtils.showProgressDialog(ChatCommentActivity.this, "댓글을 삭제하고 있습니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestDeleteComment(new OkHttpClient(), nCommentID, pref.getString("USER_ID", "Guest"));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(bResult) {
                            Toast.makeText(ChatCommentActivity.this, "댓글이 삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                            getCommentData();
                        } else {
                            Toast.makeText(ChatCommentActivity.this, "댓글을 삭제하지 못했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_LONG).show();
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