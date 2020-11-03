package com.Whowant.Tokki.UI.Popup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Photopicker.PhotoPickerActivity;
import com.Whowant.Tokki.UI.Activity.Photopicker.SeesoGalleryActivity;
import com.Whowant.Tokki.UI.Activity.Photopicker.TokkiGalleryActivity;

public class MediaSelectPopup extends AppCompatActivity {
    private int     nType = 0;
    private boolean bEdit = false;
    private int     nOrder = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_select_popup);

        nType = getIntent().getIntExtra("TYPE", 0);
        bEdit = getIntent().getBooleanExtra("EDIT", false);
        nOrder = getIntent().getIntExtra("ORDER", -1);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    public void onClickCloseBtn(View view) {
        finish();
    }

    public void OnClickGalleryBtn(View view) {
//        Intent intent = new Intent(MediaSelectPopup.this, SeesoGalleryActivity.class);
        Intent intent = new Intent(MediaSelectPopup.this, TokkiGalleryActivity.class);
        intent.putExtra("TYPE", nType);
        intent.putExtra("EDIT", bEdit);
        intent.putExtra("ORDER", nOrder);
        startActivity(intent);
        finish();
    }

    public void OnClickAlbumBtn(View view) {
//        Intent oldIntent = getIntent();
//        oldIntent.putExtra("TYPE", nType);
//        oldIntent.putExtra("EDIT", bEdit);
//        oldIntent.putExtra("ORDER", nOrder);
//        setResult(RESULT_OK, oldIntent);
//        finish();
        Intent intent = new Intent(MediaSelectPopup.this, PhotoPickerActivity.class);
        intent.putExtra("TYPE", nType);
        intent.putExtra("EDIT", bEdit);
        intent.putExtra("ORDER", nOrder);
        startActivity(intent);
        finish();
    }
}
