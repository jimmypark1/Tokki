package com.Whowant.Tokki.UI.Activity.Market;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.VO.MessageVO;

public class MainCompletePopup extends AppCompatActivity {

    Intent oldIntent;
    MessageVO message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_complete_popup);

        oldIntent = getIntent();

        message = (MessageVO)getIntent().getSerializableExtra("MESSAGE_DATA");
    }

    public void onClickCloseBtn(View view) {

        finish();
    }
    public void onClickOKBtn(View view) {

        oldIntent.putExtra("MESSAGE_END","결제가 완료되었습니다.\n");
        oldIntent.putExtra("CARROT_END",message.getCarrot());
        setResult(RESULT_OK, oldIntent);

        finish();
    }

}