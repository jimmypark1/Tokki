package com.Whowant.Tokki.UI.Popup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.Whowant.Tokki.R;

public class DistractorPopup extends AppCompatActivity {
    private EditText dist1;
    private EditText dist2;

    private Intent oldIntent;
    private int nOrder;
    private boolean bEdit;
    private String strOrigianl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distractor_popup);

        dist1 = findViewById(R.id.inputDistractor1);
        dist2 = findViewById(R.id.inputDistractor2);

        oldIntent = getIntent();
        bEdit = oldIntent.getBooleanExtra("EDIT", false);
        nOrder = oldIntent.getIntExtra("ORDER", -1);
        strOrigianl = oldIntent.getStringExtra("TEXT");

        if(bEdit) {
            dist1.setText(strOrigianl.substring(0, strOrigianl.indexOf("╋")));
            dist2.setText(strOrigianl.substring(strOrigianl.indexOf("╋")+1));
        }
    }

    public void onClickCloseBtn(View view) {
        finish();
    }

    public void onClickAddBtn(View view) {
        String strDis1 = dist1.getText().toString();
        String strDis2 = dist2.getText().toString();

        if((strDis1.length() == 0 && strDis2.length() == 0)) {
            Toast.makeText(DistractorPopup.this, "선택지를 입력해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        oldIntent.putExtra("DISTRACTOR_1", dist1.getText().toString());
        oldIntent.putExtra("DISTRACTOR_2", dist2.getText().toString());

        if(bEdit)
            oldIntent.putExtra("ORDER", nOrder);

        setResult(RESULT_OK, oldIntent);
        finish();
    }
}
