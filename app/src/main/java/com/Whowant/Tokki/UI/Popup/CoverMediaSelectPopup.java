package com.Whowant.Tokki.UI.Popup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.Whowant.Tokki.R;

public class CoverMediaSelectPopup extends AppCompatActivity {
    Intent oldIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover_media_select_popup);

        oldIntent = getIntent();
    }

    public void onClickCloseBtn(View view) {
        finish();
    }

    public void OnClickGalleryBtn(View view) {
//        Toast.makeText(this, "준비 중입니다.", Toast.LENGTH_LONG).show();
        oldIntent.putExtra("TYPE", 1);
        setResult(RESULT_OK, oldIntent);
        finish();
    }

    public void OnClickAlbumBtn(View view) {
        oldIntent.putExtra("TYPE", 2);
        setResult(RESULT_OK, oldIntent);
        finish();
    }

    public void OnClickDeleteBtn(View view) {
        oldIntent.putExtra("TYPE", 3);
        setResult(RESULT_OK, oldIntent);
        finish();
    }
}
