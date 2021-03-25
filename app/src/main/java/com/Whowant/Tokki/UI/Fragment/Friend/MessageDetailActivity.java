package com.Whowant.Tokki.UI.Fragment.Friend;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Admin.AproveWaitingEpisodeListActivity;
import com.Whowant.Tokki.UI.Activity.Admin.CommentManagementActivity;
import com.Whowant.Tokki.UI.Activity.Admin.MemberManagementActivity;
import com.Whowant.Tokki.UI.Activity.Market.MainCompletePopup;
import com.Whowant.Tokki.UI.Activity.Market.MarketDealPopup;
import com.Whowant.Tokki.UI.Activity.Mypage.MyPageActivity;
import com.Whowant.Tokki.UI.Activity.Report.ReportActivity;
import com.Whowant.Tokki.UI.Activity.Work.ReportSelectActivity;
import com.Whowant.Tokki.UI.Activity.Work.ViewerActivity;
import com.Whowant.Tokki.UI.Activity.Work.WorkMainActivity;
import com.Whowant.Tokki.UI.Popup.EpisodeAproveCancelPopup;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.SimplePreference;
import com.Whowant.Tokki.VO.ContestVO;
import com.Whowant.Tokki.VO.MarketVO;
import com.Whowant.Tokki.VO.MessageVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

import static android.view.View.TEXT_ALIGNMENT_TEXT_START;

public class MessageDetailActivity extends AppCompatActivity {
    private int nThreadID;
    private String strReceiverID, strReceiverName;
    private ArrayList<MessageVO> messageList = new ArrayList<>();
    private CMessageArrayAdapter adapter;
    private ListView messageListView;
    private EditText inputTextView;

    private TextView titleView;

    TextView dealBt;
    ImageButton moreBtn;
    private InputMethodManager imm;
    Button buyBt;
    private float fX, fY;                               // 롱클릭 등을 위해 터치 좌표 저장

    int type = 0;

    String writeId = "";
    String workTitle = "";
    String workId = "";
    String field = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);

        inputTextView = findViewById(R.id.inputTextView);
        messageListView = findViewById(R.id.chattingListView);
        titleView = findViewById(R.id.titleView);
        dealBt = findViewById(R.id.dealBtn);
        moreBtn= findViewById(R.id.moreBtn);

        nThreadID = getIntent().getIntExtra("THREAD_ID", 0);
        field = getIntent().getStringExtra("FIELD");

        type = getIntent().getIntExtra("MSG_TYPE", 0);
        if(type == 0)
        {
            dealBt.setVisibility(View.INVISIBLE);
            moreBtn.setVisibility(View.VISIBLE);
        }
        else
        {
            dealBt.setVisibility(View.VISIBLE);
            moreBtn.setVisibility(View.INVISIBLE);

        }

        workId = getIntent().getStringExtra("WORK_ID");
        writeId = getIntent().getStringExtra("WRITER_ID");
        workTitle = getIntent().getStringExtra("WORK_TITLE");

        strReceiverID = getIntent().getStringExtra("RECEIVER_ID");
        strReceiverName = getIntent().getStringExtra("RECEIVER_NAME");
        titleView.setText(strReceiverName);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        adapter = new CMessageArrayAdapter(this, R.layout.left_message_row, messageList);
        messageListView.setAdapter(adapter);

        messageListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    fX = motionEvent.getX();
                    fY = motionEvent.getY();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                    float fEndX = motionEvent.getX();
                    float fEndY = motionEvent.getY();

                    if (fX >= fEndX + 10 || fX <= fEndX - 10 || fY >= fEndY + 10 || fY <= fEndY - 10) {              // 10px 이상 움직였다면
                        return false;
                    } else {
                        imm.hideSoftInputFromWindow(inputTextView.getWindowToken(), 0);
                    }
                }
                return false;
            }
        });

        dealBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageDetailActivity.this, MarketDealPopup.class);
                startActivityForResult(intent,1000);
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        getMessageList();
    }
    public  String convertUTF8ToString(String s) {
        String out = null;
        try {
            out = new String(s.getBytes("ISO-8859-1"), "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return out;
    }
    public  String convertStringToUTF8(String s) {
        String out = null;
        try {
            out = new String(s.getBytes("UTF-8"), "ISO-8859-1");
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return out;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK)
        {
            //intent.putExtra("CARROT",carrot.getText().toString());
            if(requestCode == 1000)
            {
                String strCarrot = data.getStringExtra("CARROT");
                int nCarrot = Integer.parseInt(strCarrot);


                if(nCarrot > 0)
                {
                    String msg = "당근 " + strCarrot + "개로 거래를 제안합니다. 만족하시면 거래수락 버튼을 눌러주세요.";

                    SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                    String strUserID = pref.getString("USER_ID", "Guest");
                    if(strUserID.contains(writeId))
                    {
                        msg = "당근 " + strCarrot + "개로 거래를 제안합니다. 만족하시면 결제하기 버튼을 눌러주세요";
                        requestSendMessage(msg.toString(),nCarrot,"Y");
                    }
                    else
                    {
                        requestSendMessage(msg.toString(),nCarrot,"N");

                    }

                }

            }
            else if(requestCode == 2000 )
            {
//                let msg =  "결제가 완료되었습니다.\n" + "작품명 : " + parentCon.trading.title + "\n가격 : 당근 " + String(messageData.carrot) + " 개"

                String strEnd = data.getStringExtra("MESSAGE_END");
                int nCarrot = data.getIntExtra("CARROT_END",0);
                String msg = strEnd + "작품명 : " + workTitle + "\n가격 : 당근 " + String.valueOf(nCarrot) + " 개";

                requestSendMessage(msg.toString(),nCarrot,"B");

                sendMailToWriter(nCarrot);

            }

        }

    }
    private void sendMailToWriter(int nCarrot) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                JSONObject resultObject = HttpClient.getUserInfo(new OkHttpClient(), writeId);
                // JSONObject resultObject = HttpClient.getMyInfo(new OkHttpClient(), userId);
                try {
                    if (resultObject == null) {
                        return;
                    }

                    String email = resultObject.getString("EMAIL");

                    boolean ret = HttpClient.requestMarketSendMailToWriter(new OkHttpClient(),email);
                    SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                    String strUserID = pref.getString("USER_ID", "Guest");


                    HttpClient.transactionComplete(new OkHttpClient(),strUserID,writeId,workId,String.valueOf(nCarrot), String.valueOf(nThreadID),field);
                   // transactionComplete(OkHttpClient httpClient, String userID,String writerID,String workID, String carrot)

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public void onClickSendBtn(View view) {
        String strText = inputTextView.getText().toString();

        if(strText.length() == 0) {
            Toast.makeText(this, "채팅 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        requestSendMessage(strText,0,"N");
    }

    private void requestSendMessage(String strText, int nCarrot ,String complete) {
        SharedPreferences pref = getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
        CommonUtils.showProgressDialog(MessageDetailActivity.this, "서버와 통신중입니다.");
        new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean bResult = HttpClient.requestSendMessage(new OkHttpClient(), pref.getString("USER_ID", "Guest"), strReceiverID, strText, nThreadID,nCarrot,complete);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();
                        if(bResult == false) {
                            Toast.makeText(MessageDetailActivity.this, "서버와의 통신에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        requestSendFCMMessage(strText);
                        getMessageList();
                        inputTextView.setText("");

                    }
                });
            }
        }).start();
    }
    private void requestSendFCMMessage(String strText) {
        SharedPreferences pref = getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean bResult = HttpClient.sendFCMNChat(new OkHttpClient(), pref.getString("USER_ID", "Guest"), strReceiverID, strText, nThreadID);


            }
        }).start();
    }

    public void onClickTopLeftBtn(View view) {

        finish();
    }

    void deleteMessage()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int ret = HttpClient.deleteMessage(new OkHttpClient(), String.valueOf(nThreadID));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        getMessageList();
                    }
                });
            }
        }).start();
    }

    void showAlert()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MessageDetailActivity.this);
        builder.setTitle("대화 삭제");
        builder.setMessage("대화내용을 모두 삭제하시겠습니까?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                deleteMessage();
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public void onClickTopMoreBtn(View view) {
        PopupMenu popup = new PopupMenu(MessageDetailActivity.this, moreBtn);

        popup.getMenuInflater().inflate(R.menu.message_menu, popup.getMenu());


        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MessageDetailActivity.this);
                AlertDialog alertDialog = null;

                switch(item.getItemId()) {
                    case R.id.profile: {
                        //    builder.setTitle("작품 게시 승인");
                        Intent intent = new Intent(MessageDetailActivity.this, MyPageActivity.class);

                        intent.putExtra("WRITER_ID", strReceiverID);

                        startActivity(intent);
                        break;
                    }
                    case R.id.delete:

                        showAlert();
                        break;
                    case R.id.report: {
                        Intent intent = new Intent(MessageDetailActivity.this, ReportSelectActivity.class);
                        intent.putExtra("THREAD_ID", nThreadID);


                        startActivity(intent);
                        break;
                    }
                }
                return true;
            }
        });

        popup.show();//showing popup menu

    }

    private void getMessageList() {
        SharedPreferences pref = getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
        messageList.clear();

        CommonUtils.showProgressDialog(MessageDetailActivity.this, "서버와 통신중입니다.");
        new Thread(new Runnable() {
            @Override
            public void run() {
                messageList = HttpClient.getMessageList(new OkHttpClient(), nThreadID);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();
                        if(messageList == null) {
                            Toast.makeText(MessageDetailActivity.this, "서버와의 통신에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        adapter.setData(messageList);
                        messageListView.setSelection(adapter.getCount() - 1);
                    }
                });
            }
        }).start();
    }

    public class CMessageArrayAdapter extends ArrayAdapter<Object>
    {
        private LayoutInflater mLiInflater;
        private ArrayList<MessageVO> dataList = new ArrayList<>();

        CMessageArrayAdapter(Context context, int layout, List titles)
        {
            super(context, layout, titles);
            mLiInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.dataList = (ArrayList<MessageVO>) titles;
        }

        public void setData(ArrayList<MessageVO> list) {
            this.dataList = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            MessageVO vo = dataList.get(position);
            SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
            String strUserID = pref.getString("USER_ID", "Guest");

            if(strUserID.equals(vo.getSenderID())) {
                convertView = mLiInflater.inflate(R.layout.right_message_row, parent, false);
                TextView contentsView = convertView.findViewById(R.id.contentsTextView);
                contentsView.setText(vo.getMsgContents());
            } else {
                convertView = mLiInflater.inflate(R.layout.left_message_row, parent, false);
                ImageView faceView = convertView.findViewById(R.id.faceView);

                Button buyBt = convertView.findViewById(R.id.buy);
                if(type == 0)
                {
                    TextView contentsView = convertView.findViewById(R.id.contentsTextView);


                    ViewGroup.LayoutParams params = buyBt.getLayoutParams();

                    params.height = 0;
                    params.width = 0;

                    ViewGroup.MarginLayoutParams lp0 = (ViewGroup.MarginLayoutParams) buyBt.getLayoutParams();
                    ViewGroup.MarginLayoutParams lp1 = (ViewGroup.MarginLayoutParams) contentsView.getLayoutParams();

                    lp0.topMargin = 0;
                    lp0.bottomMargin = 0;
                    lp0.leftMargin = 0;
                    lp0.rightMargin = 0;

                    lp1.topMargin = 0;
                    lp1.leftMargin = 0;
                    lp1.rightMargin = 0;

                    convertView.setTextAlignment(TEXT_ALIGNMENT_TEXT_START);



                    //

                }
                else
                {


                    TextView msgView = convertView.findViewById(R.id.contentsTextView);

                    ViewGroup.MarginLayoutParams lp1 = (ViewGroup.MarginLayoutParams) msgView.getLayoutParams();
                    lp1.topMargin = 10;

                    String userId = SimplePreference.getStringPreference(MessageDetailActivity.this, "USER_INFO", "USER_ID", "Guest");


                    if(writeId.contains(userId))
                    {
                        if(vo.getContract_complete().contains("B"))
                        {
                            ViewGroup.LayoutParams params = buyBt.getLayoutParams();

                            params.height = 0;

                            ViewGroup.MarginLayoutParams lp0 = (ViewGroup.MarginLayoutParams) buyBt.getLayoutParams();
                            lp0.topMargin = 0;
                            lp0.bottomMargin = 0;


                        }
                        else
                        {
                            //거래수락
                            buyBt.setText("거래수락");
                        }

                    }
                    else
                    {
                        if(vo.getContract_complete().contains("Y"))
                        {
                            buyBt.setText("결재하기");
                        }
                        else
                        {
                            ViewGroup.LayoutParams params = buyBt.getLayoutParams();

                            params.height = 0;

                            ViewGroup.MarginLayoutParams lp0 = (ViewGroup.MarginLayoutParams) buyBt.getLayoutParams();
                            lp0.topMargin = 0;
                            lp0.bottomMargin = 0;


                        }

                    }
                    buyBt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //MainCompletePopup
                            if(writeId.contains(userId)== false)
                            {
                                // 결제하기
                                Intent intent = new Intent(MessageDetailActivity.this, MainCompletePopup.class);
                                intent.putExtra("MESSAGE_DATA", vo);

                                startActivityForResult(intent,2000);

                            }
                            else
                            {
                                // 거래수락

                                SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                                String strUserID = pref.getString("USER_ID", "Guest");
                                String strCarrot = String.valueOf(vo.getCarrot());
                                if(strUserID.contains(writeId))
                                {
                                    String msg = "당근 " + strCarrot + "개로 거래를 체결합니다. 결제하기 버튼을 눌러주세요.";
                                    requestSendMessage(msg.toString(),vo.getCarrot(),"Y");
                                }

                            }



                        }
                    });
                }

                faceView.setClipToOutline(true);
                String strPhoto = vo.getSenderPhoto();
                if(strPhoto != null && !strPhoto.equals("null") && !strPhoto.equals("NULL") && strPhoto.length() > 0) {
                    if(!strPhoto.startsWith("http"))
                        strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

                    Glide.with(MessageDetailActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(strPhoto)
                            .apply(new RequestOptions().circleCrop())
                            .into(faceView);
                } else {
                    Glide.with(MessageDetailActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(R.drawable.user_icon)
                            .apply(new RequestOptions().circleCrop())
                            .into(faceView);
                }

                TextView nameView = convertView.findViewById(R.id.nameView);
                nameView.setText(vo.getSenderName());
                TextView contentsView = convertView.findViewById(R.id.contentsTextView);
                contentsView.setText(vo.getMsgContents());
            }

            return convertView;
        }
    }
}