package com.Whowant.Tokki.UI.Popup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.Whowant.Tokki.R;

public class ProfilePhotoPopup extends AppCompatActivity {
    private Intent oldIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_photo_popup);

        oldIntent = getIntent();
    }

    public void onClickCloseBtn(View view) {
        finish();
    }

    public void OnClickCameraBtn(View view) {
        oldIntent.putExtra("CAMERA", true);
        setResult(RESULT_OK, oldIntent);
        finish();
    }

    public void OnClickAlbumBtn(View view) {
        oldIntent.putExtra("CAMERA", false);
        setResult(RESULT_OK, oldIntent);
        finish();
    }

    public void OnClickDefaultBtn(View view) {
        oldIntent.putExtra("CAMERA", false);
        oldIntent.putExtra("DEFAULT", true);
        setResult(RESULT_OK, oldIntent);
        finish();
    }
}
