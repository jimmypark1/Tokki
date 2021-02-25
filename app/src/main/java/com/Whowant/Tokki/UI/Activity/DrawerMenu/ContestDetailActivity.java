package com.Whowant.Tokki.UI.Activity.DrawerMenu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Work.WorkMainActivity;
import com.Whowant.Tokki.VO.ContestVO;

public class ContestDetailActivity extends AppCompatActivity {
    public static ContestVO contestVO;
    private TextView nameView, birthdayView, phoneNumView, emailView, careearView,workTitleView, characterView, synopsisView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_detail);

        nameView = findViewById(R.id.nameView);
        birthdayView = findViewById(R.id.birthdayView);
        phoneNumView = findViewById(R.id.phoneNumView);
        emailView = findViewById(R.id.emailView);
        careearView = findViewById(R.id.careearView);
        workTitleView = findViewById(R.id.workTitleView);
        characterView = findViewById(R.id.characterView);
        synopsisView = findViewById(R.id.synopsisView);

        nameView.setText(contestVO.getUserName());
        birthdayView.setText(contestVO.getUserBirthday());
        phoneNumView.setText(contestVO.getStrPhoneNum());
        emailView.setText(contestVO.getEmail());
        careearView.setText(contestVO.isCareer() == true ? "있음" : "없음");
        workTitleView.setText(contestVO.getStrTitle());
        characterView.setText(contestVO.getCharacterInfo());
        synopsisView.setText(contestVO.getSynopsis());
    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }

    public void onClickWorkView(View view) {
        Intent intent = new Intent(ContestDetailActivity.this, WorkMainActivity.class);
        intent.putExtra("WORK_ID", contestVO.getWorkID());
       // intent.putExtra("WORK_TYPE", contestVO.getnTarget());

        startActivity(intent);
    }
}