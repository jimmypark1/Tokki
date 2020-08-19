package com.Whowant.Tokki.UI.Popup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;

import okhttp3.OkHttpClient;

public class StarPointPopup extends AppCompatActivity {
    private TextView ratingPointView;
    private RatingBar ratingBar;
    private float fMyPoint = -1;
    private int nEpisodeID;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star_point_popup);

        nEpisodeID = getIntent().getIntExtra("EPISODE_ID", 0);
        pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

        ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float fValue, boolean b) {
                fMyPoint = fValue;
                ratingPointView.setText(String.format("%.1f", fMyPoint));
            }
        });
        ratingPointView = findViewById(R.id.ratingPointView);

        getMyStarPoint();
    }

    private void getMyStarPoint() {
        CommonUtils.showProgressDialog(StarPointPopup.this, "서버와 통신하고 있습니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                fMyPoint = HttpClient.getMyStarPoint(new OkHttpClient(), nEpisodeID, pref.getString("USER_ID", "Guest"));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();
                        ratingBar.setRating(fMyPoint);
                        ratingPointView.setText(String.format("%.1f", fMyPoint));

                        if(fMyPoint > 0) {
                            ratingBar.setEnabled(false);
                        }
                    }
                });
            }
        }).start();
    }

    private void reqeustSendStarPoint() {
        CommonUtils.showProgressDialog(StarPointPopup.this, "서버와 통신하고 있습니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestSendPoint(new OkHttpClient(), fMyPoint, nEpisodeID, pref.getString("USER_ID", "Guest"));
                
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(bResult) {
                            Toast.makeText(StarPointPopup.this, "별점이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(StarPointPopup.this, "별점 등록에 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    public void onClickOKBtn(View view) {
        reqeustSendStarPoint();
    }

    public void onClickCloseBtn(View view) {
        finish();
    }
}
