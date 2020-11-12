package com.Whowant.Tokki.UI.Popup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Photopicker.PhotoPickerActivity;
import com.Whowant.Tokki.UI.Activity.Photopicker.TokkiGalleryActivity;
import com.Whowant.Tokki.UI.Fragment.Main.TokkiGalleryFragment;

import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_BG;

public class BGImageSelectPopup extends AppCompatActivity {
    private boolean bEdit = false;
    private Intent oldIntent;
    private int    nOrder = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_select_popup2);

        oldIntent = getIntent();
        bEdit = oldIntent.getBooleanExtra("EDIT", false);

        if(bEdit)
            nOrder = oldIntent.getIntExtra("ORDER", -1);
    }

    public void onClickCloseBtn(View view) {
        finish();
    }

    public void OnClickGalleryBtn(View view) {
//        Intent intent = new Intent(BGImageSelectPopup.this, SeesoGalleryActivity.class);
        Intent intent = new Intent(BGImageSelectPopup.this, TokkiGalleryActivity.class);
        intent.putExtra("TYPE",  TYPE_BG.ordinal());
        intent.putExtra("EDIT", bEdit);
        intent.putExtra("ORDER", nOrder);
        startActivity(intent);
        finish();
    }

    public void OnClickAlbumBtn(View view) {
        Intent intent = new Intent(BGImageSelectPopup.this, PhotoPickerActivity.class);
        intent.putExtra("TYPE",  TYPE_BG.ordinal());
        intent.putExtra("EDIT", bEdit);
        intent.putExtra("ORDER", nOrder);
        startActivity(intent);
        finish();
    }

    public void OnClickColorBtn(View view) {
        oldIntent.putExtra("EDIT", bEdit);
        oldIntent.putExtra("ORDER", nOrder);
        setResult(RESULT_OK, oldIntent);
        finish();
    }
}
