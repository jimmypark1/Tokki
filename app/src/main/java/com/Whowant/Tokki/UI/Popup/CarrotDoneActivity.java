package com.Whowant.Tokki.UI.Popup;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.OnKeyboardVisibilityListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;

public class CarrotDoneActivity extends AppCompatActivity implements OnKeyboardVisibilityListener {
    private int nCarrotCount = 10;
    private int nCurrentCarrot = 0;
    private int nWorkID;
    private TextView carrotCountView;
    private EditText carrotInputView;
    private ImageButton leftBtn, rightBtn;
    private Timer timer = null;
    private InputMethodManager imm;
    private boolean bInputVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrot_done);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        nWorkID = getIntent().getIntExtra("WORK_ID", 0);
        carrotCountView = findViewById(R.id.carrotCountView);
        carrotInputView = findViewById(R.id.carrotInputView);
        leftBtn = findViewById(R.id.leftBtn);
        rightBtn = findViewById(R.id.rightBtn);

        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nCarrotCount > 0)
                    nCarrotCount -= 1;

                carrotCountView.setText(CommonUtils.comma(nCarrotCount));
            }
        });

        setKeyboardVisibilityListener(this);

        carrotCountView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        carrotInputView.setVisibility(View.VISIBLE);
                        carrotInputView.setText("" + nCarrotCount);
                        carrotCountView.setVisibility(View.INVISIBLE);
                        carrotInputView.requestFocus();
                        carrotInputView.setSelection(carrotInputView.getText().toString().length());
                        imm.showSoftInput(carrotInputView, InputMethodManager.SHOW_FORCED);
                        bInputVisible = true;
                    }
                });
            }
        });

        carrotInputView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_NEXT:
                    case EditorInfo.IME_ACTION_DONE:
                    case EditorInfo.IME_ACTION_GO:
                    case EditorInfo.IME_ACTION_SEND:
                        String strInputCount = carrotInputView.getText().toString();
                        if(strInputCount.length() == 0)
                            nCarrotCount = 0;
                        else
                            nCarrotCount = Integer.valueOf(carrotInputView.getText().toString());

                        carrotCountView.setText("" + nCarrotCount);
                        imm.hideSoftInputFromWindow(carrotInputView.getWindowToken(), 0);
                        carrotInputView.setVisibility(View.INVISIBLE);
                        carrotCountView.setVisibility(View.VISIBLE);
                        bInputVisible = false;
                        break;
                    default:
                        // 기본 엔터키 동작
                        return false;
                }
                return true;
            }
        });

        carrotInputView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String carrot = carrotInputView.getText().toString();
                if(carrot.length() == 0) {
                    carrotInputView.setText("0");
                    carrotInputView.setSelection(1);
                } else {
                    String strCarrotCount = carrotInputView.getText().toString();
                    int nCount = Integer.valueOf(strCarrotCount);

                    if(nCount > nCurrentCarrot) {
                        carrotInputView.setText("" + nCurrentCarrot);
                    } else if(carrot.length() > 1 && carrot.startsWith("0")) {
                        carrotInputView.setText("" + nCount);
                    }

                    carrotInputView.setSelection(carrotInputView.getText().toString().length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

//        carrotInputView.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int i, KeyEvent keyEvent) {
//                switch(keyEvent.getKeyCode()) {
//                    case KeyEvent.KEYCODE_BACK:
//                        String strInputCount = carrotInputView.getText().toString();
//                        if(strInputCount.length() == 0)
//                            nCarrotCount = 0;
//                        else
//                            nCarrotCount = Integer.valueOf(carrotInputView.getText().toString());
//
//                        carrotCountView.setText("" + nCarrotCount);
//                        imm.hideSoftInputFromWindow(carrotInputView.getWindowToken(), 0);
//                        carrotInputView.setVisibility(View.INVISIBLE);
//                        carrotCountView.setVisibility(View.VISIBLE);
//                        bInputVisible = false;
//                        break;
//                }
//                return false;
//            }
//        });

        leftBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if(bInputVisible == true) {
                            String strInputCount = carrotInputView.getText().toString();
                            if(strInputCount.length() == 0)
                                nCarrotCount = 0;
                            else
                                nCarrotCount = Integer.valueOf(carrotInputView.getText().toString());

                            carrotCountView.setText("" + nCarrotCount);
                            imm.hideSoftInputFromWindow(carrotInputView.getWindowToken(), 0);
                            carrotInputView.setVisibility(View.INVISIBLE);
                            carrotCountView.setVisibility(View.VISIBLE);
                            bInputVisible = false;
                            return false;
                        }

                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                        }

                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(nCarrotCount > 10)
                                            nCarrotCount -= 10;
                                        else
                                            nCarrotCount = 0;

                                        carrotCountView.setText(CommonUtils.comma(nCarrotCount));
                                    }
                                });
                            }
                        }, 500, 300);
                    }
                        break;

                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        if(timer != null) {
                            timer.cancel();
                            timer = null;
                        }
                        break;
                }
                return false;
            }
        });

        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nCarrotCount+1 > nCurrentCarrot) {
                    Toast.makeText(CarrotDoneActivity.this, "당근이 모자랍니다.", Toast.LENGTH_SHORT).show();
                } else {
                    nCarrotCount += 1;
                    carrotCountView.setText(CommonUtils.comma(nCarrotCount));
                }
            }
        });

        rightBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if(bInputVisible == true) {
                            String strInputCount = carrotInputView.getText().toString();
                            if(strInputCount.length() == 0)
                                nCarrotCount = 0;
                            else
                                nCarrotCount = Integer.valueOf(carrotInputView.getText().toString());

                            carrotCountView.setText("" + nCarrotCount);
                            imm.hideSoftInputFromWindow(carrotInputView.getWindowToken(), 0);
                            carrotInputView.setVisibility(View.INVISIBLE);
                            carrotCountView.setVisibility(View.VISIBLE);
                            bInputVisible = false;
                            return false;
                        }

                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                        }

                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(nCarrotCount+10 > nCurrentCarrot) {
                                            Toast.makeText(CarrotDoneActivity.this, "당근이 모자랍니다.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            nCarrotCount += 10;
                                            carrotCountView.setText(CommonUtils.comma(nCarrotCount));
                                        }
                                    }
                                });
                            }
                        }, 500, 300);
                    }
                    break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        if(timer != null) {
                            timer.cancel();
                            timer = null;
                        }
                        break;
                }
                return false;
            }
        });

        getMyCarrotInfo();
    }

    private void getMyCarrotInfo() {
        CommonUtils.showProgressDialog(CarrotDoneActivity.this, "서버와 통신중입니다. 잠시만 기다려 주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
                String strMyID = pref.getString("USER_ID", "Guest");

                JSONObject resultObject = HttpClient.getMyCarrotInfo(new OkHttpClient(), strMyID);
                CommonUtils.hideProgressDialog();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        try {
                            if(resultObject == null)
                                return;

                            if(resultObject.getString("RESULT").equals("SUCCESS")) {
                                nCurrentCarrot = resultObject.getInt("CURRENT_CARROT");
                                nCurrentCarrot = 5000;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(nCurrentCarrot < 10)
                            nCarrotCount = nCurrentCarrot;

                        carrotCountView.setText(CommonUtils.comma(nCarrotCount));
                    }
                });
            }
        }).start();
    }

    public void onClickCloseBtn(View view) {
        finish();
    }

    public void onClickDonationBtn(View view) {
        imm.hideSoftInputFromWindow(carrotInputView.getWindowToken(), 0);
        nCarrotCount = Integer.valueOf(carrotInputView.getText().toString());
        if(nCarrotCount == 0) {
            Toast.makeText(CarrotDoneActivity.this, "응원할 당근을 선택해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        } else if(nCarrotCount > nCurrentCarrot) {
            Toast.makeText(CarrotDoneActivity.this, "응원할 당근이 모자랍니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        CommonUtils.showProgressDialog(CarrotDoneActivity.this, "서버와 통신중입니다. 잠시만 기다려 주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
                String strMyID = pref.getString("USER_ID", "Guest");

                JSONObject resultObject = HttpClient.requestDonation(new OkHttpClient(), nWorkID, strMyID, nCarrotCount);
                CommonUtils.hideProgressDialog();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        try {
                            if(resultObject == null) {
                                Toast.makeText(CarrotDoneActivity.this, "응원에 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if(resultObject.getString("RESULT").equals("SUCCESS")) {
                                Toast.makeText(CarrotDoneActivity.this, "응원 하였습니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(CarrotDoneActivity.this, "응원에 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(CarrotDoneActivity.this, "응원에 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void setKeyboardVisibilityListener(final OnKeyboardVisibilityListener onKeyboardVisibilityListener) {
        final View parentView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        parentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private boolean alreadyOpen;
            private final int defaultKeyboardHeightDP = 100;
            private final int EstimatedKeyboardDP = defaultKeyboardHeightDP + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 48 : 0);
            private final Rect rect = new Rect();

            @Override
            public void onGlobalLayout() {
                int estimatedKeyboardHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP, parentView.getResources().getDisplayMetrics());
                parentView.getWindowVisibleDisplayFrame(rect);
                int heightDiff = parentView.getRootView().getHeight() - (rect.bottom - rect.top);
                boolean isShown = heightDiff >= estimatedKeyboardHeight;

                if (isShown == alreadyOpen) {
                    Log.i("Keyboard state", "Ignoring global layout change...");
                    return;
                }
                alreadyOpen = isShown;
                onKeyboardVisibilityListener.onVisibilityChanged(isShown);
            }
        });
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        if(!visible) {
            String strInputCount = carrotInputView.getText().toString();
            if(strInputCount.length() == 0)
                nCarrotCount = 0;
            else
                nCarrotCount = Integer.valueOf(strInputCount);

            carrotCountView.setText("" + nCarrotCount);
            carrotInputView.setText("" + nCarrotCount);
            imm.hideSoftInputFromWindow(carrotInputView.getWindowToken(), 0);
            carrotInputView.setVisibility(View.INVISIBLE);
            carrotCountView.setVisibility(View.VISIBLE);
            bInputVisible = false;
        }
    }
}