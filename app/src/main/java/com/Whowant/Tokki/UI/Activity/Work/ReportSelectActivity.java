package com.Whowant.Tokki.UI.Activity.Work;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class ReportSelectActivity extends AppCompatActivity {                                       // 신고 화면
    private int nSelectedIndex = 1;
    private RadioButton radio1;
    private RadioButton radio2;
    private RadioButton radio3;
    private RadioButton radio4;
    private RadioButton radio5;
    private RadioButton radio6;
    private RadioButton radio7;
    private RadioButton radio8;

    private ArrayList<RadioButton> buttonList;
    private EditText reportReasonInputView;
    private int nCommentID;
    private int nEpisodeID;
    private int nThreadID;
    private int nWorkID;

    private SharedPreferences pref;
    private Button reportBtn;

    String postId = "";
    TextView titleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_select);


        titleView = findViewById(R.id.titleView);

        nCommentID = getIntent().getIntExtra("COMMENT_ID", -1);

        postId = getIntent().getStringExtra("POST_ID");

        nEpisodeID = getIntent().getIntExtra("EPISODE_ID", -1);

        nThreadID = getIntent().getIntExtra("THREAD_ID", -1);
        nWorkID = getIntent().getIntExtra("WORK_ID", -1);

        if(nCommentID != -1)
        {
            titleView.setText("댓글 신고");
        }
        else if(postId != null && postId.length() > 0)
        {
            titleView.setText("게시물 신고");
        }
        else if(nEpisodeID != -1 || nWorkID != -1)
        {
            titleView.setText("작품 신고");
        }
        else
        {
            titleView.setText("신고");

        }
        pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

        radio1 = findViewById(R.id.radio1);
        radio2 = findViewById(R.id.radio2);
        radio3 = findViewById(R.id.radio3);
        radio4 = findViewById(R.id.radio4);
        radio5 = findViewById(R.id.radio5);
        radio6 = findViewById(R.id.radio6);
        radio7 = findViewById(R.id.radio7);
        radio8 = findViewById(R.id.radio8);
        reportBtn = findViewById(R.id.reportBtn);
        reportReasonInputView = findViewById(R.id.reportReasonInputView);
        reportReasonInputView.setEnabled(false);

        buttonList = new ArrayList<>();
        buttonList.add(radio1);
        buttonList.add(radio2);
        buttonList.add(radio3);
        buttonList.add(radio4);
        buttonList.add(radio5);
        buttonList.add(radio6);
        buttonList.add(radio7);
        buttonList.add(radio8);

        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestReport();
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(reportReasonInputView.getText().toString().length() == 0) {
                    reportBtn.setEnabled(false);
                    reportBtn.setBackgroundResource(R.drawable.common_btn_disable_bg);
                } else {
                    reportBtn.setEnabled(true);
                    reportBtn.setBackgroundResource(R.drawable.common_btn_bg);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        reportReasonInputView.addTextChangedListener(textWatcher);
    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.report_menu, menu);
        return true;
    }

    private void initRadioBtn() {
        for(int i = 0 ; i < 8 ; i++) {
            buttonList.get(i).setChecked(false);
        }

        buttonList.get(nSelectedIndex-1).setChecked(true);

        if(nSelectedIndex != 8) {
            reportReasonInputView.setText("");
            reportReasonInputView.setEnabled(false);
            reportBtn.setEnabled(true);
            reportBtn.setBackgroundResource(R.drawable.common_btn_bg);
        } else {
            reportReasonInputView.setEnabled(true);

            if(reportReasonInputView.getText().toString().length() == 0) {
                reportBtn.setEnabled(false);
                reportBtn.setBackgroundResource(R.drawable.common_btn_disable_bg);
            } else {
                reportBtn.setEnabled(true);
                reportBtn.setBackgroundResource(R.drawable.common_btn_bg);
            }
        }
    }

    public void onClickList1(View view) {
        nSelectedIndex = 1;
        initRadioBtn();
    }

    public void onClickList2(View view) {
        nSelectedIndex = 2;
        initRadioBtn();
    }

    public void onClickList3(View view) {
        nSelectedIndex = 3;
        initRadioBtn();
    }

    public void onClickList4(View view) {
        nSelectedIndex = 4;
        initRadioBtn();
    }

    public void onClickList5(View view) {
        nSelectedIndex = 5;
        initRadioBtn();
    }

    public void onClickList6(View view) {
        nSelectedIndex = 6;
        initRadioBtn();
    }

    public void onClickList7(View view) {
        nSelectedIndex = 7;
        initRadioBtn();
    }
    public void onClickList8(View view) {
        nSelectedIndex = 8;
        initRadioBtn();
    }

    public void requestReport() {
        String strReason = "";

        switch (nSelectedIndex) {
            case 1:
                strReason = "영리목적/홍보성";
                break;
            case 2:
                strReason = "욕설/인신공격";
                break;
            case 3:
                strReason = "불법정보";
                break;
            case 4:
                strReason = "개인정보노출";
                break;
            case 5:
                strReason = "음란성/선정성";
                break;
            case 6:
                strReason = "같은 내용 도배";
                break;
            case 7:
                strReason = "저작권";
                break;
            case 8:
                strReason = reportReasonInputView.getText().toString();
                break;
        }

        if(strReason.length() == 0) {
            Toast.makeText(this, "신고 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        final String strSendReason = strReason;
        CommonUtils.showProgressDialog(ReportSelectActivity.this, "신고 내용을 전송 중입니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                //OkHttpClient httpClient, String strUserID, String strAdminID, String strReason
                if(nCommentID > 0)
                {
                    final int nResult = HttpClient.requestCommentReport(new OkHttpClient(), pref.getString("USER_ID", "Guest"), nCommentID, strSendReason);
//                boolean bResult = HttpClient.requestCommentReport(new OkHttpClient(), pref.getString("USER_ID", "Guest"), nCommentID, strReason);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.hideProgressDialog();

                            if(nResult == 0) {
                                Toast.makeText(ReportSelectActivity.this, "신고되었습니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            } else if(nResult == 1) {
                                Toast.makeText(ReportSelectActivity.this, "이미 신고한 댓글입니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ReportSelectActivity.this, "신고에 실패하였습니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else if(postId != null && postId.length() > 0)
                {
                    int nResult = HttpClient.requestSpaceReport(new OkHttpClient(), pref.getString("USER_ID", "Guest"), postId, strSendReason);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.hideProgressDialog();

                            if(nResult == 0) {
                                Toast.makeText(ReportSelectActivity.this, "신고되었습니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            } else if(nResult == 1) {
                                Toast.makeText(ReportSelectActivity.this, "이미 신고한 게시입니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ReportSelectActivity.this, "신고에 실패하였습니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else if(nThreadID > 0)
                {
                    final int nResult = HttpClient.requestMessageReport(new OkHttpClient(), pref.getString("USER_ID", "Guest"), nThreadID, strSendReason);
//                boolean bResult = HttpClient.requestCommentReport(new OkHttpClient(), pref.getString("USER_ID", "Guest"), nCommentID, strReason);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.hideProgressDialog();

                            if(nResult == 0) {
                                Toast.makeText(ReportSelectActivity.this, "신고되었습니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            } else if(nResult == 1) {
                                Toast.makeText(ReportSelectActivity.this, "이미 신고한 대화입니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ReportSelectActivity.this, "신고에 실패하였습니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                //requestWorkReport
                else if(nWorkID > 0)
                {
                    final int nResult = HttpClient.requestWorkReport(new OkHttpClient(), pref.getString("USER_ID", "Guest"), String.valueOf(nWorkID), strSendReason);
//                boolean bResult = HttpClient.requestCommentReport(new OkHttpClient(), pref.getString("USER_ID", "Guest"), nCommentID, strReason);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.hideProgressDialog();

                            if(nResult == 0) {
                                Toast.makeText(ReportSelectActivity.this, "신고되었습니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            } else if(nResult == 1) {
                                Toast.makeText(ReportSelectActivity.this, "이미 신고한 댓글입니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ReportSelectActivity.this, "신고에 실패하였습니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else if(nEpisodeID > 0)
                {
                    final int nResult = HttpClient.requestEpisodeReport(new OkHttpClient(), pref.getString("USER_ID", "Guest"), String.valueOf(nEpisodeID), strSendReason);
//                boolean bResult = HttpClient.requestCommentReport(new OkHttpClient(), pref.getString("USER_ID", "Guest"), nCommentID, strReason);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.hideProgressDialog();

                            if(nResult == 0) {
                                Toast.makeText(ReportSelectActivity.this, "신고되었습니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            } else if(nResult == 1) {
                                Toast.makeText(ReportSelectActivity.this, "이미 신고한 댓글입니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ReportSelectActivity.this, "신고에 실패하였습니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }

        }).start();
    }
}
