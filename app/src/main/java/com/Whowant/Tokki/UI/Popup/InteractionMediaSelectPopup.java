package com.Whowant.Tokki.UI.Popup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Photopicker.PhotoPickerActivity;
import com.Whowant.Tokki.UI.Activity.Photopicker.TokkiGalleryActivity;

public class InteractionMediaSelectPopup extends AppCompatActivity {
    private int     nType = 0;
    private boolean bEdit = false;
    private int     nOrder = -1;

    private Intent oldIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_select_popup);

        oldIntent = getIntent();
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
//        Toast.makeText(InteractionMediaSelectPopup.this, "준비 중입니다.", Toast.LENGTH_LONG).show();
//        oldIntent.putExtra("SELECTION", "GALLERY");
//        setResult(RESULT_OK, oldIntent);
//        finish();
        Intent intent = new Intent(InteractionMediaSelectPopup.this, TokkiGalleryActivity.class);

        intent.putExtra("TYPE", nType);
        intent.putExtra("EDIT", bEdit);
        intent.putExtra("ORDER", nOrder);

        startActivity(intent);
        finish();
    }

    public void OnClickAlbumBtn(View view) {
//        oldIntent.putExtra("SELECTION", "ALBUM");
//        setResult(RESULT_OK, oldIntent);
//        finish();
        Intent intent = new Intent(InteractionMediaSelectPopup.this, PhotoPickerActivity.class);

        intent.putExtra("TYPE", nType);
        intent.putExtra("EDIT", bEdit);
        intent.putExtra("ORDER", nOrder);

        startActivity(intent);
        finish();
    }
}