package com.Whowant.Tokki.UI.Popup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.Whowant.Tokki.R;

public class TextEditPopup extends AppCompatActivity {
    private EditText inputTextView;
    private String strOrigianlString;
    private Intent oldIntent;
    private int nOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_edit_popup);

        inputTextView = findViewById(R.id.inputTextView);

        oldIntent = getIntent();
        strOrigianlString = oldIntent.getStringExtra("TEXT");
        nOrder = oldIntent.getIntExtra("ORDER", 0);

        inputTextView.setText(strOrigianlString);
        inputTextView.setSelection(strOrigianlString.length());
    }

    public void onClickCloseBtn(View view) {
        finish();
    }

    public void onClickAddBtn(View view) {
        String strText = inputTextView.getText().toString();

        if(strText.length() == 0) {
            Toast.makeText(TextEditPopup.this, "수정할 문구를 입력하세요.", Toast.LENGTH_LONG).show();
            return;
        } else if(strText.equals(strOrigianlString)) {
            Toast.makeText(TextEditPopup.this, "같은 문구는 사용할 수 없습니다.", Toast.LENGTH_LONG).show();
            return;
        } else {
            oldIntent.putExtra("EDITED_TEXT", strText);
            oldIntent.putExtra("ORDER", nOrder);
            setResult(RESULT_OK, oldIntent);
            finish();
        }
    }
}