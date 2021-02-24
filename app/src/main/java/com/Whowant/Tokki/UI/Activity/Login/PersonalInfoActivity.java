package com.Whowant.Tokki.UI.Activity.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Custom.MyDatePickerDialogFragment;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.CustomUncaughtExceptionHandler;
import com.ycuwq.datepicker.date.DatePickerDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.OkHttpClient;

public class PersonalInfoActivity extends AppCompatActivity {
    private int nSNS;

    private EditText inputNameView;
    private EditText inputEmailView;
    private EditText inputPhonenumView;
    private TextView inputBirthView;
    private LinearLayout maleCheckLayout;
    private LinearLayout femaleCheckLayout;
    private ImageView maleCheck;
    private ImageView femaleCheck;

    private LinearLayout inputPWLayout;
    private LinearLayout inputPWCheckLayout;

    private String strID;
    private String strPW;
    private String strName;
    private String strEmail;
    private String strSNSID;
    private String strProfileImageUrl;
    private String strPhoneNum;
    private String strBirthday;
    private String strBirth;
    private final String emailValidation = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private final int GENDER_MALE = 0;
    private final int GENDER_FEMALE = 1;
    private int    nGender = GENDER_FEMALE;

    private int    nType = 0;  // 0:독자 1:작가 2 제작자


    Button menBt;
    Button womanBt;

    Button readerBt;
    Button writerBt;
    Button producerBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info_renewal);

        Thread.UncaughtExceptionHandler handler = Thread
                .getDefaultUncaughtExceptionHandler();

        if (!(handler instanceof CustomUncaughtExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
        }

        strID = getIntent().getStringExtra("ID");
        strPW = getIntent().getStringExtra("PASSWORD");
        nSNS = getIntent().getIntExtra("SNS", 0);
        strName = getIntent().getStringExtra("USER_NAME");
        strEmail = getIntent().getStringExtra("USER_EMAIL");
        strProfileImageUrl = getIntent().getStringExtra("USER_PHOTO");
        strPhoneNum = getIntent().getStringExtra("USER_PHONENUM");
        strSNSID = getIntent().getStringExtra("SNS_ID");

        inputNameView = findViewById(R.id.inputNameView);
        inputEmailView = findViewById(R.id.inputEmailView);
        inputPhonenumView = findViewById(R.id.inputPhoneNumView);
        inputBirthView = findViewById(R.id.inputBirthView);

        menBt = findViewById(R.id.men);
        womanBt = findViewById(R.id.woman);

        readerBt = findViewById(R.id.reader);
        writerBt = findViewById(R.id.writer);
        producerBt = findViewById(R.id.producer);

        /*
        maleCheckLayout = findViewById(R.id.maleCheckLayout);
        femaleCheckLayout = findViewById(R.id.femaleCheckLayout);

        maleCheck = findViewById(R.id.maleCheck);
        femaleCheck = findViewById(R.id.femaleCheck);

        maleCheck.setImageResource(0);
        femaleCheck.setImageResource(R.drawable.check_box_on);

        maleCheckLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nGender = GENDER_MALE;
                initGenderViews();
            }
        });

        femaleCheckLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nGender = GENDER_FEMALE;
                initGenderViews();
            }
        });
        */
        inputBirthView.setFocusable(false);
        inputBirthView.setClickable(false);
        inputBirthView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                        strBirth = String.format("%d년 %02d월 %02d일",  year, month, day);
                        inputBirthView.setText(strBirth);
                    }
                });
                datePickerDialogFragment.show(getFragmentManager(), "DatePickerDialogFragment");
            }
        });

        initViews();
        initLayout();
    }


    public void onClickMen(View view)
    {
        menBt.setBackgroundResource(R.drawable.common_btn_bg_enable);
        womanBt.setBackgroundResource(R.drawable.common_btn_bg_disable);
        menBt.setTextColor(Color.parseColor("#ffffff"));
        womanBt.setTextColor(Color.parseColor("#767676"));

        nGender = GENDER_MALE;
    }
    public void onClickWoman(View view)
    {
        menBt.setBackgroundResource(R.drawable.common_btn_bg_disable);
        womanBt.setBackgroundResource(R.drawable.common_btn_bg_enable);

        womanBt.setTextColor(Color.parseColor("#ffffff"));
        menBt.setTextColor(Color.parseColor("#767676"));

        nGender = GENDER_FEMALE;


    }
    public void onClickReader(View view)
    {
        readerBt.setBackgroundResource(R.drawable.common_btn_bg_enable);
        writerBt.setBackgroundResource(R.drawable.common_btn_bg_disable);
        producerBt.setBackgroundResource(R.drawable.common_btn_bg_disable);


        readerBt.setTextColor(Color.parseColor("#ffffff"));
        writerBt.setTextColor(Color.parseColor("#767676"));
        producerBt.setTextColor(Color.parseColor("#767676"));

        nType = 0;
    }
    public void onClickWriter(View view)
    {
        readerBt.setBackgroundResource(R.drawable.common_btn_bg_disable);
        writerBt.setBackgroundResource(R.drawable.common_btn_bg_enable);
        producerBt.setBackgroundResource(R.drawable.common_btn_bg_disable);


        readerBt.setTextColor(Color.parseColor("#767676"));
        writerBt.setTextColor(Color.parseColor("#ffffff"));
        producerBt.setTextColor(Color.parseColor("#767676"));

        nType = 1;

    }
    public void onClickProducer(View view)
    {
        readerBt.setBackgroundResource(R.drawable.common_btn_bg_disable);
        writerBt.setBackgroundResource(R.drawable.common_btn_bg_disable);
        producerBt.setBackgroundResource(R.drawable.common_btn_bg_enable);


        readerBt.setTextColor(Color.parseColor("#767676"));
        writerBt.setTextColor(Color.parseColor("#767676"));
        producerBt.setTextColor(Color.parseColor("#ffffff"));

        nType = 2;

    }

    int dpToPx(float dp)
    {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
    }
    int pxToDp(float dp)
    {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
    }


    void initLayout()
    {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        int offsetSex = ( width -  dpToPx(30 * 2) - dpToPx(10) ) / 2;
        int offsetType = ( (width) -  dpToPx(30 * 2) - dpToPx(10*2) ) / 3;
/*
        menBt.setWidth(offsetSex);
        womanBt.setWidth(offsetSex);

        readerBt.setWidth(offsetType);
        writerBt.setWidth(offsetType);
        producerBt.setWidth(offsetType);


 */

        ViewGroup.LayoutParams params = menBt.getLayoutParams();
        params.width = offsetSex;

        ViewGroup.LayoutParams params1 = womanBt.getLayoutParams();
        params1.width = offsetSex;

        ViewGroup.LayoutParams params3 = readerBt.getLayoutParams();
        params3.width = offsetType;
        ViewGroup.LayoutParams params4 = writerBt.getLayoutParams();
        params4.width = offsetType;
        ViewGroup.LayoutParams params5 = producerBt.getLayoutParams();
        params5.width = offsetType;

 /*
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(pxToDp(offsetSex), 40);
        menBt.setLayoutParams(layoutParams);
        womanBt.setLayoutParams(layoutParams);

        ViewGroup.LayoutParams layoutParams1 = new ViewGroup.LayoutParams(pxToDp(offsetType), 40);
        readerBt.setLayoutParams(layoutParams);
        writerBt.setLayoutParams(layoutParams);
        producerBt.setLayoutParams(layoutParams);
*/
        //  button.setLayoutParams(params);


    }

    public void onClickBackBtn(View view) {
        finish();
    }

    public void OnClickBirthBtn(View view) {
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
                strBirth = String.format("%d년 %02d월 %02d일",  year, month, day);
                inputBirthView.setText(strBirth);
//                inputBirthView.setText(strBirthday);
            }
        });
        datePickerDialogFragment.show(getFragmentManager(), "DatePickerDialogFragment");
    }

    private void initGenderViews() {
        if(nGender == GENDER_MALE) {
            maleCheck.setImageResource(R.drawable.check_box_on);
            femaleCheck.setImageResource(0);
        } else {
            maleCheck.setImageResource(0);
            femaleCheck.setImageResource(R.drawable.check_box_on);
        }
    }

    private void initViews() {
        if(strName != null)
            inputNameView.setText(strName);

        if(strEmail != null) {
            inputEmailView.setText(strEmail);
            inputEmailView.setEnabled(false);
        }

        if(strID != null) {
            strPhoneNum = strID;
       //     inputPhonenumView.setText(strPhoneNum);
       //     inputPhonenumView.setEnabled(false);
        }

        if(strPhoneNum != null) {
         //   inputPhonenumView.setText(strPhoneNum);
         //   inputPhonenumView.setEnabled(false);
        }
    }

    public void onClickRegisterBtn(View view) {
        strName = inputNameView.getText().toString();
        strEmail = inputEmailView.getText().toString();
       // strPhoneNum = inputPhonenumView.getText().toString();

        if(strName.length() == 0) {
            Toast.makeText(PersonalInfoActivity.this, "닉네임을 입력해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        if(strEmail.length() == 0) {
            Toast.makeText(PersonalInfoActivity.this, "이메일을 입력해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        if (!strEmail.matches(emailValidation)) {
            Toast.makeText(PersonalInfoActivity.this, "올바른 이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        /*
        if(strPhoneNum.length() == 0) {
            Toast.makeText(PersonalInfoActivity.this, "전화번호를 입력해주세요.", Toast.LENGTH_LONG).show();
            return;
        }
        */
        if(strBirth == null) {
            Toast.makeText(PersonalInfoActivity.this, "생년월일을 선택해주세요", Toast.LENGTH_LONG).show();
            return;
        }

        final HashMap<String, String> userInfoMap = new HashMap<>();
        if(strID == null)
            strID = strEmail;

        userInfoMap.put("USER_ID", strID);

        if(strPW != null)
            userInfoMap.put("USER_PW", strPW);

        userInfoMap.put("USER_NAME", strName);
        userInfoMap.put("USER_EMAIL", strEmail);
    //    userInfoMap.put("USER_PHONENUM", strPhoneNum);
        userInfoMap.put("USER_PHOTO", strProfileImageUrl);
        userInfoMap.put("REGISTER_SNS", "" + nSNS);
        userInfoMap.put("SNS_ID", strSNSID);
        userInfoMap.put("USER_BIRTHDAY", strBirthday);
        userInfoMap.put("USER_GENDER", "" + nGender);
        userInfoMap.put("TYPE", "" + nType);

        CommonUtils.showProgressDialog(PersonalInfoActivity.this, "회원 가입 중입니다. 잠시만 기다려주세요.");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject resultObject = HttpClient.requestRegister(new OkHttpClient(), userInfoMap);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.hideProgressDialog();

                            if(resultObject == null) {
                                Toast.makeText(PersonalInfoActivity.this, "회원가입에 실패했습니다.", Toast.LENGTH_LONG).show();
                                return;
                            }

                            try {
                                if(resultObject.getString("RESULT").equals("DELETED_ID")) {
                                    Toast.makeText(PersonalInfoActivity.this, "탈퇴된 회원입니다. 관리자에게 문의하세요.", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                if(resultObject.getString("RESULT").equals("DUPLICATE_ID")) {
                                    Toast.makeText(PersonalInfoActivity.this, "이미 가입된 아이디입니다. 다른 아이디를 사용해 주세요.", Toast.LENGTH_LONG).show();
                                    return;
                                } else if(resultObject.getString("RESULT").equals("DUPLICATE_EMAIL")) {
                                    Toast.makeText(PersonalInfoActivity.this, "이미 가입된 이메일 주소입니다. 다른 이메일 주소를 사용해 주세요.", Toast.LENGTH_LONG).show();
                                    return;
                                } else if(resultObject.getString("RESULT").equals("DUPLICATE_PHONENUM")) {
                                    Toast.makeText(PersonalInfoActivity.this, "이미 가입된 전화번호입니다. 다른 전화번호를 사용해 주세요.", Toast.LENGTH_LONG).show();
                                    return;
                                } else if(resultObject.getString("RESULT").equals("SUCCESS")) {
                                    SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = pref.edit();

                                    editor.putString("USER_ID", strID);
                                    editor.putString("USER_NAME", strName);
                                    editor.putString("USER_EMAIL", strEmail);
                                    editor.putString("USER_PHONENUM", strPhoneNum);
                                    editor.putString("USER_PHOTO", strProfileImageUrl);
                                    editor.putInt("REGISTER_SNS", nSNS);
                                    editor.putString("SNS_ID", strSNSID);
                                    editor.putString("ADMIN", "N");
                                    editor.putString("USER_BIRTHDAY", strBirthday);
                                    editor.putInt("USER_GENDER", nGender);
                                    editor.putInt("USER_TYPE", nType);

                                    editor.putInt("COIN_COUNT", 0);
                                    editor.commit();

                                    Intent intent = new Intent(PersonalInfoActivity.this, InputRecommendCodeActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(PersonalInfoActivity.this, "회원가입에 실패했습니다.", Toast.LENGTH_LONG).show();
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


}
