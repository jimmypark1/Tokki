package com.Whowant.Tokki.UI.Popup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.Whowant.Tokki.R;

public class InteractionBGSelectPopup extends AppCompatActivity {
    private Intent oldIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_select_popup2);
        oldIntent = getIntent();
    }

    public void onClickCloseBtn(View view) {
        finish();
    }

    public void OnClickGalleryBtn(View view) {
//        Toast.makeText(InteractionBGSelectPopup.this, "준비 중입니다.", Toast.LENGTH_LONG).show();
        oldIntent.putExtra("SELECTION", "GALLERY");
        setResult(RESULT_OK, oldIntent);
        finish();
    }

    public void OnClickAlbumBtn(View view) {
//        Intent intent = new Intent(BGImageSelectPopup.this, PhotoPickerActivity.class);
//        intent.putExtra("TYPE",  PhotoPickerActivity.TYPE_BG);
//        intent.putExtra("EDIT", bEdit);
//        intent.putExtra("ORDER", nOrder);
//        startActivity(intent);
//        finish();
        oldIntent.putExtra("SELECTION", "ALBUM");
        setResult(RESULT_OK, oldIntent);
        finish();
    }

    public void OnClickColorBtn(View view) {
//        oldIntent.putExtra("EDIT", bEdit);
//        oldIntent.putExtra("ORDER", nOrder);
        oldIntent.putExtra("SELECTION", "COLOR");
        setResult(RESULT_OK, oldIntent);
        finish();
    }
}
