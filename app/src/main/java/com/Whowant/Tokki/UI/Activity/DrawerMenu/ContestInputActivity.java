package com.Whowant.Tokki.UI.Activity.DrawerMenu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Login.LoginSelectActivity;
import com.Whowant.Tokki.UI.Activity.Work.EpisodeCommentActivity;
import com.Whowant.Tokki.UI.Activity.Work.LiteratureListActivity;
import com.Whowant.Tokki.UI.Custom.MyDatePickerDialogFragment;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.WorkVO;
import com.ycuwq.datepicker.date.DatePickerDialogFragment;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.OkHttpClient;

public class ContestInputActivity extends AppCompatActivity {
    private EditText inputNameView, inputPhoneNumView, inputEmailView, inputCharacterView, inputSynopsisView;
    private TextView birthdayView, checkText1, checkText2, workTitleView;
    private ImageView checkBox1, checkBox2, termsCheckBox;
    private Button okBtn;

    private boolean bCheck = false;                 // true : 있음, false : 없음
    private boolean bTerms = false;
    private String strBirthday = "";
    private String strWorkTitle = "";
    private int    nSelectedWorkID = 0;
    private ArrayList<WorkVO> workList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_input);

        inputNameView = findViewById(R.id.inputNameView);
        inputPhoneNumView = findViewById(R.id.inputPhoneNumView);
        inputEmailView = findViewById(R.id.inputEmailView);
        inputCharacterView = findViewById(R.id.inputCharacterView);
        inputSynopsisView = findViewById(R.id.inputSynopsisView);
        birthdayView = findViewById(R.id.birthdayView);
        checkText1 = findViewById(R.id.checkText1);
        checkText2 = findViewById(R.id.checkText2);
        workTitleView = findViewById(R.id.workTitleView);
        checkBox1 = findViewById(R.id.checkBox1);
        checkBox2 = findViewById(R.id.checkBox2);
        termsCheckBox = findViewById(R.id.termsCheckBox);
        okBtn = findViewById(R.id.okBtn);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkAlldatas();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        inputNameView.addTextChangedListener(textWatcher);
        inputPhoneNumView.addTextChangedListener(textWatcher);
        inputEmailView.addTextChangedListener(textWatcher);
        inputCharacterView.addTextChangedListener(textWatcher);
        inputSynopsisView.addTextChangedListener(textWatcher);

        GetAllWorkList();
    }

    public void onClickTermsCheck(View view) {
        bTerms = !bTerms;

        if(bTerms) {
            termsCheckBox.setImageResource(R.drawable.check_box_on);
        } else {
            termsCheckBox.setImageResource(R.drawable.check_box_non);
        }

        checkAlldatas();
    }

    public void onClickCharacterView(View view) {
        inputCharacterView.requestFocus();
        inputCharacterView.setSelection(inputCharacterView.getText().length());
    }

    public void onClickSynopsisView(View view) {
        inputSynopsisView.requestFocus();
        inputSynopsisView.setSelection(inputSynopsisView.getText().length());
    }

    public void onClickBirthdayLayout(View view) {
        int nYear = 1999;
        int nMonth = 1;
        int nDay = 1;

        if(strBirthday != null && strBirthday.length() > 0) {
            nYear = Integer.valueOf(strBirthday.substring(0, 4));
            nMonth = Integer.valueOf(strBirthday.substring(4, 6));
            nDay = Integer.valueOf(strBirthday.substring(6, 8));
        }

        MyDatePickerDialogFragment datePickerDialogFragment = new MyDatePickerDialogFragment();
        datePickerDialogFragment.setSelectedDate(nYear, nMonth, nDay);
        datePickerDialogFragment.setOnDateChooseListener(new DatePickerDialogFragment.OnDateChooseListener() {
            @Override
            public void onDateChoose(int year, int month, int day) {
                strBirthday = String.format("%d%02d%02d", year, month, day);
                birthdayView.setText(strBirthday);
                birthdayView.setTextColor(Color.parseColor("#000000"));
                checkAlldatas();
//                inputBirthView.setText(strBirthday);
            }
        });
        datePickerDialogFragment.show(getFragmentManager(), "DatePickerDialogFragment");
    }
    private void checkAlldatas() {
        if(inputNameView.getText().toString().length() == 0) {
//            Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
            setBtn(false);
            return;
        }

        if(inputPhoneNumView.getText().toString().length() == 0) {
//            Toast.makeText(this, "휴대폰 번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            setBtn(false);
            return;
        }

        if(inputEmailView.getText().toString().length() == 0) {
//            Toast.makeText(this, "이메일 주소를 입력해주세요.", Toast.LENGTH_SHORT).show();
            setBtn(false);
            return;
        }

        if(inputCharacterView.getText().toString().length() == 0) {
//            Toast.makeText(this, "캐릭터 소개를 입력해주세요.", Toast.LENGTH_SHORT).show();
            setBtn(false);
            return;
        }

        if(inputSynopsisView.getText().toString().length() == 0) {
//            Toast.makeText(this, "시놉시스를 입력해주세요.", Toast.LENGTH_SHORT).show();
            setBtn(false);
            return;
        }

        if(strBirthday == null || strBirthday.length() == 0) {
//            Toast.makeText(this, "생년월일을 입력해주세요.", Toast.LENGTH_SHORT).show();
            setBtn(false);
            return;
        }

        if(strWorkTitle == null || strWorkTitle.length() == 0) {
//            Toast.makeText(this, "작품을 선택해주세요.", Toast.LENGTH_SHORT).show();
            setBtn(false);
            return;
        }

        if(bTerms == false) {
//            Toast.makeText(this, "개인정보 활용에 동의해주세요.", Toast.LENGTH_SHORT).show();
            setBtn(false);
            return;
        }

        setBtn(true);
    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }

    private void setBtn(boolean bEnable) {
        okBtn.setEnabled(bEnable);

        if(bEnable) {
            okBtn.setBackgroundResource(R.drawable.common_btn_bg);
        } else {
            okBtn.setBackgroundResource(R.drawable.common_btn_disable_bg);
        }
    }

    private void GetAllWorkList() {
        CommonUtils.showProgressDialog(this, "작품목록을 가져오고있습니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                workList = HttpClient.GetAllWorkListWithIDForContest(new OkHttpClient(), pref.getString("USER_ID", "Guest"));
//                workList = HttpClient.GetAllWorkListWithIDForContest(new OkHttpClient(), "skyup");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();
                    }
                });
            }
        }).start();
    }

    public void onClickCheck1(View view) {
        bCheck = true;
        checkBox1.setImageResource(R.drawable.check_box_on);
        checkBox2.setImageResource(0);
        checkText1.setTextColor(Color.parseColor("#000000"));
        checkText2.setTextColor(Color.parseColor("#d1d1d1"));
    }

    public void onClickCheck2(View view) {
        bCheck = false;
        checkBox1.setImageResource(0);
        checkBox2.setImageResource(R.drawable.check_box_on);
        checkText1.setTextColor(Color.parseColor("#d1d1d1"));
        checkText2.setTextColor(Color.parseColor("#000000"));
    }

    public void onClickWorkList(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("작품을 선택하세요.");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item);

        for(WorkVO vo : workList) {
            adapter.add(vo.getTitle());
        }

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                checkAlldatas();
            }
        });

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position) {
                WorkVO vo = workList.get(position);
                strWorkTitle = vo.getTitle();
                nSelectedWorkID = vo.getnWorkID();
                workTitleView.setText(strWorkTitle);
                workTitleView.setTextColor(Color.parseColor("#000000"));
                checkAlldatas();
            }
        });

        builder.show();
    }

    public void onClickOKBtn(View view) {
        SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        HashMap<String, String> contestInfo = new HashMap<>();
        contestInfo.put("USER_ID", pref.getString("USER_ID", "Guest"));
        contestInfo.put("USER_NAME", inputNameView.getText().toString());
        contestInfo.put("USER_BIRTHDAY", strBirthday);
        contestInfo.put("USER_PHONENUM", inputPhoneNumView.getText().toString());
        contestInfo.put("EMAIL", inputEmailView.getText().toString());
        contestInfo.put("CAREER", bCheck == true ? "Y" : "N");
        contestInfo.put("WORK_TITLE", strWorkTitle);
        contestInfo.put("CHARACTER_INFO", inputCharacterView.getText().toString());
        contestInfo.put("SYNOPSIS", inputSynopsisView.getText().toString());
        contestInfo.put("WORK_ID", "" + nSelectedWorkID);
        /*
        jsonBody.put("USER_ID", contestInfo.get("USER_ID"));
        jsonBody.put("USER_NAME", contestInfo.get("USER_NAME"));
        jsonBody.put("USER_BIRTHDAY", contestInfo.get("USER_BIRTHDAY"));
        jsonBody.put("USER_PHONENUM", contestInfo.get("USER_PHONENUM"));
        jsonBody.put("EMAIL", contestInfo.get("EMAIL"));
        jsonBody.put("CAREER", contestInfo.get("CAREER"));
        jsonBody.put("WORK_TITLE", contestInfo.get("WORK_TITLE"));
        jsonBody.put("CHARACTER_INFO", contestInfo.get("CHARACTER_INFO"));
        jsonBody.put("SYNOPSIS", contestInfo.get("SYNOPSIS"));
         */

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestUploadContest(new OkHttpClient(), contestInfo);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (bResult) {
                            Toast.makeText(ContestInputActivity.this, "공모전에 등록 되었습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ContestInputActivity.this, "공모전 등록을 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();

    }
}