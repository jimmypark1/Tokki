package com.Whowant.Tokki.UI.Activity.Work;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Photopicker.PhotoPickerActivity;
import com.Whowant.Tokki.UI.Popup.MediaSelectPopup;
import com.Whowant.Tokki.VO.CharacterRegVo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import java.util.List;

import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_FACE_IMG;

public class CharacterRegActivity extends AppCompatActivity implements View.OnClickListener, ColorPickerDialogListener {
    EditText nameEt;
    ImageView photoIv;
    EditText personalityEt;
    EditText roleEt;

    ImageView colorIv;

    TextView locLeftTv;
    ImageView locLeftIv;
    TextView locRightTv;
    ImageView locRightIv;
    TextView colorBlackTv;
    ImageView colorBlackIv;
    TextView colorWhiteTv;
    ImageView colorWhiteIv;
    TextView colorBoxBlackTv;
    ImageView colorBoxBlackIv;
    TextView colorBoxWhiteTv;
    ImageView colorBoxWhiteIv;

    String imgUri;
    String strColor;

    Activity mActivity;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        imgUri = intent.getStringExtra("URI");
        if (imgUri != null) {
            photoIv.setBackgroundResource(0);
            Glide.with(this)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(Uri.parse(imgUri))
                    .apply(new RequestOptions().circleCrop())
                    .into(photoIv);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_reg);

        mActivity = this;

        initView();
    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_top_layout_title)).setText("등장인물");

        findViewById(R.id.ib_top_layout_back).setVisibility(View.VISIBLE);
        findViewById(R.id.ib_top_layout_back).setOnClickListener((v) -> finish());

        nameEt = findViewById(R.id.et_character_name);
        photoIv = findViewById(R.id.iv_character_photo);
        personalityEt = findViewById(R.id.et_character_personality);
        roleEt = findViewById(R.id.et_character_role);

        colorIv = findViewById(R.id.iv_character_color);
        colorIv.setClipToOutline(true);

        colorIv.setColorFilter(Color.parseColor(strColor = "#ffffff"));

        locLeftTv = findViewById(R.id.tv_character_loc_left);
        locLeftTv.setOnClickListener(this);
        locLeftIv = findViewById(R.id.iv_character_loc_left);
        locRightTv = findViewById(R.id.tv_character_loc_right);
        locRightTv.setOnClickListener(this);
        locRightIv = findViewById(R.id.iv_character_loc_right);
        colorBlackTv = findViewById(R.id.tv_character_color_black);
        colorBlackTv.setOnClickListener(this);
        colorBlackIv = findViewById(R.id.iv_character_color_black);
        colorWhiteTv = findViewById(R.id.tv_character_color_white);
        colorWhiteTv.setOnClickListener(this);
        colorWhiteIv = findViewById(R.id.iv_character_color_white);
        colorBoxBlackTv = findViewById(R.id.tv_character_color_box_black);
        colorBoxBlackTv.setOnClickListener(this);
        colorBoxBlackIv = findViewById(R.id.iv_character_color_box_black);
        colorBoxWhiteTv = findViewById(R.id.tv_character_color_box_white);
        colorBoxWhiteTv.setOnClickListener(this);
        colorBoxWhiteIv = findViewById(R.id.iv_character_color_box_white);
    }

    private PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {             // 모든 권한 획득
            Intent intent = new Intent(mActivity, MediaSelectPopup.class);
            intent.putExtra("TYPE", TYPE_FACE_IMG.ordinal());
            startActivity(intent);
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            Toast.makeText(mActivity, "권한을 거부하셨습니다.", Toast.LENGTH_LONG).show();
        }
    };

    // 프로필 이미지 버튼
    public void btnProfilePhoto(View v) {
        TedPermission.with(mActivity)
                .setPermissionListener(permissionlistener)
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    // 삭제 버튼
    public void btnRemove(View v) {

    }

    // 저장 버튼
    public void btnSave(View v) {
        CharacterRegVo vo = new CharacterRegVo();
        vo.setName(nameEt.getText().toString());
        vo.setPersonality(personalityEt.getText().toString());
        vo.setRole(roleEt.getText().toString());
        vo.setLocation(locLeftIv.isShown() ? 0 : 1);
        vo.setColor(colorWhiteIv.isShown() ? 0 : 1);
        vo.setTxtColorBox(colorBoxWhiteIv.isShown() ? 0 : 1);
        vo.setColorBg(strColor);
        vo.setImgUri(imgUri);

        Intent intent = new Intent();
        intent.putExtra("data", new Gson().toJson(vo));
        setResult(RESULT_OK, intent);
        finish();
    }

    // 다른 컬러 선택하기
    public void btnColorChange(View v) {

        ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setAllowPresets(false)
                .setDialogId(1010)
                .setColor(Color.parseColor("#ffffff"))
                .setShowAlphaSlider(true)
                .show(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_character_loc_left:
                locLeftTv.setTextColor(getResources().getColor(R.color.character_selected_t, getTheme()));
                locLeftIv.setVisibility(View.VISIBLE);
                locRightTv.setTextColor(getResources().getColor(R.color.character_selected_f, getTheme()));
                locRightIv.setVisibility(View.INVISIBLE);
                break;
            case R.id.tv_character_loc_right:
                locLeftTv.setTextColor(getResources().getColor(R.color.character_selected_f, getTheme()));
                locLeftIv.setVisibility(View.INVISIBLE);
                locRightTv.setTextColor(getResources().getColor(R.color.character_selected_t, getTheme()));
                locRightIv.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_character_color_black:
                colorBlackTv.setTextColor(getResources().getColor(R.color.character_selected_t, getTheme()));
                colorBlackIv.setVisibility(View.VISIBLE);
                colorWhiteTv.setTextColor(getResources().getColor(R.color.character_selected_f, getTheme()));
                colorWhiteIv.setVisibility(View.INVISIBLE);
                break;
            case R.id.tv_character_color_white:
                colorBlackTv.setTextColor(getResources().getColor(R.color.character_selected_f, getTheme()));
                colorBlackIv.setVisibility(View.INVISIBLE);
                colorWhiteTv.setTextColor(getResources().getColor(R.color.character_selected_t, getTheme()));
                colorWhiteIv.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_character_color_box_black:
                colorBoxBlackTv.setTextColor(getResources().getColor(R.color.character_selected_t, getTheme()));
                colorBoxBlackIv.setVisibility(View.VISIBLE);
                colorBoxWhiteTv.setTextColor(getResources().getColor(R.color.character_selected_f, getTheme()));
                colorBoxWhiteIv.setVisibility(View.INVISIBLE);
                break;
            case R.id.tv_character_color_box_white:
                colorBoxBlackTv.setTextColor(getResources().getColor(R.color.character_selected_f, getTheme()));
                colorBoxBlackIv.setVisibility(View.INVISIBLE);
                colorBoxWhiteTv.setTextColor(getResources().getColor(R.color.character_selected_t, getTheme()));
                colorBoxWhiteIv.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        int nBgColor = color;
        String bgColor = String.format("#%08X", color);
        int nColor = nBgColor;

        if (bgColor.length() > 7) {
            strColor = "#" + bgColor.substring(3);
            nColor = Color.parseColor(strColor);
        }

        colorIv.setColorFilter(nColor);
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }
}