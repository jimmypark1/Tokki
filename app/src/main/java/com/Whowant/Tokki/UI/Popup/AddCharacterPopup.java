package com.Whowant.Tokki.UI.Popup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Photopicker.PhotoPickerActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.CharacterVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import java.util.ArrayList;
import java.util.List;

import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_FACE_IMG;

public class AddCharacterPopup extends AppCompatActivity implements ColorPickerDialogListener {
    public static ArrayList<String> nameList;
    public static CharacterVO characterVO = null;

    private EditText inputNameView;
    private TextView leftBtn;
    private TextView rightBtn;
    private TextView whiteBtn;
    private TextView blackBtn;
    private Intent oldIntent;
    private RelativeLayout colorStrokeLayout;
    private ImageView colorSettingView;
    private ImageView strokeView;
    private Button addBtn;
    private Button deleteBtn;
    private int    nLeftRight = 1;
    private boolean bBlackText = true;
    private ImageView faceView;
    private String imgUri = null;
    private int nSelectedIndex = -1;
    private String strOriginalName = null;
    private int    nBgColor = 0;
    private String bgColor;
    boolean bColor = false;

    //left : 231, 237, 239
    // right : 250, 208, 109

    // left default =

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_character);

        oldIntent = getIntent();

        inputNameView = findViewById(R.id.inputNameView);
        colorStrokeLayout = findViewById(R.id.colorStrokeLayout);
        strokeView = findViewById(R.id.strokeView);
        colorSettingView = findViewById(R.id.colorSettingView);
        leftBtn = findViewById(R.id.leftBtn);
        rightBtn = findViewById(R.id.rightBtn);
        whiteBtn = findViewById(R.id.whiteBtn);
        blackBtn = findViewById(R.id.blackBtn);
        faceView = findViewById(R.id.faceView);
        addBtn = findViewById(R.id.addBtn);
        deleteBtn = findViewById(R.id.deleteBtn);

        nSelectedIndex = oldIntent.getIntExtra("INDEX", -1);
        if(characterVO != null && nSelectedIndex > -1) {
            initViews();
        }
    }

    private void initViews() {
        TextView titleView = findViewById(R.id.titleView);
        titleView.setText("등장인물 수정");

        deleteBtn.setVisibility(View.VISIBLE);
        addBtn.setText("수정하기");
        strOriginalName = characterVO.getName();
        inputNameView.setText(strOriginalName);

        if(characterVO.getStrBalloonColor() != null && !characterVO.getStrBalloonColor().equals("null") && characterVO.getStrBalloonColor().length() > 0) {
            bgColor = characterVO.getStrBalloonColor();

            if(bgColor != null && !bgColor.equals("null") && bgColor.length() > 0) {
                nBgColor = Color.parseColor(bgColor);

                int nColor = nBgColor;

                if(bgColor.length() > 7) {
                    String strColor = "#" + bgColor.substring(3);
                    nColor = Color.parseColor(strColor);
                }

                colorStrokeLayout.setBackgroundColor(nColor);
                colorSettingView.setBackgroundColor(nBgColor);
            }
        }

        if(characterVO.getImage() != null && !characterVO.getImage().equals("null")) {
            Glide.with(this)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(Uri.parse(imgUri))
                    .apply(new RequestOptions().circleCrop())
                    .into(faceView);
        } else if(characterVO.getStrImgFile() != null && !characterVO.getStrImgFile().equals("null")) {
            Glide.with(AddCharacterPopup.this)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(CommonUtils.strDefaultUrl + "images/" + characterVO.getStrImgFile())
                    .apply(new RequestOptions().circleCrop())
                    .into(faceView);
        }

        if(characterVO.getDirection() == 0) { // 왼쪽
            leftBtn.setBackgroundResource(R.drawable.common_selected_rounded_btn_bg);
            leftBtn.setTextColor(ContextCompat.getColor(AddCharacterPopup.this, R.color.colorWhite));
            rightBtn.setBackgroundResource(R.drawable.common_gray_rounded_btn_bg);
            rightBtn.setTextColor(ContextCompat.getColor(AddCharacterPopup.this, R.color.colorDefaultText));
            nLeftRight = 0;
        } else {
            rightBtn.setBackgroundResource(R.drawable.common_selected_rounded_btn_bg);
            rightBtn.setTextColor(ContextCompat.getColor(AddCharacterPopup.this, R.color.colorWhite));
            leftBtn.setBackgroundResource(R.drawable.common_gray_rounded_btn_bg);
            leftBtn.setTextColor(ContextCompat.getColor(AddCharacterPopup.this, R.color.colorDefaultText));

            nLeftRight = 1;
        }

        if(characterVO.isbBlackText() == true) {
            blackBtn.setBackgroundResource(R.drawable.common_selected_rounded_btn_bg);
            blackBtn.setTextColor(ContextCompat.getColor(AddCharacterPopup.this, R.color.colorWhite));
            whiteBtn.setBackgroundResource(R.drawable.common_gray_rounded_btn_bg);
            whiteBtn.setTextColor(ContextCompat.getColor(AddCharacterPopup.this, R.color.colorDefaultText));
            bBlackText = true;
        } else {
            whiteBtn.setBackgroundResource(R.drawable.common_selected_rounded_btn_bg);
            whiteBtn.setTextColor(ContextCompat.getColor(AddCharacterPopup.this, R.color.colorWhite));
            blackBtn.setBackgroundResource(R.drawable.common_gray_rounded_btn_bg);
            blackBtn.setTextColor(ContextCompat.getColor(AddCharacterPopup.this, R.color.colorDefaultText));
            bBlackText = false;
        }

        if(characterVO.isbBlackName()) {

        } else {

        }
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        nBgColor = color;
        bgColor = String.format("#%08X", nBgColor);
        int nColor = nBgColor;

        if(bgColor.length() > 7) {
            String strColor = "#" + bgColor.substring(3);
            nColor = Color.parseColor(strColor);
        }

        colorStrokeLayout.setBackgroundColor(nColor);
        colorSettingView.setBackgroundColor(nBgColor);
        bColor = true;
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }

    @Override
    public void onDestroy() {
        nameList = null;
        characterVO = null;
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        imgUri = intent.getStringExtra("URI");
        if(imgUri != null) {
            Glide.with(this)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(Uri.parse(imgUri))
                    .apply(new RequestOptions().circleCrop())
                    .into(faceView);
        }

    }

    public void OnClickWhiteBtn(View view) {
        whiteBtn.setBackgroundResource(R.drawable.common_selected_rounded_btn_bg);
        whiteBtn.setTextColor(ContextCompat.getColor(AddCharacterPopup.this, R.color.colorWhite));
        blackBtn.setBackgroundResource(R.drawable.common_gray_rounded_btn_bg);
        blackBtn.setTextColor(ContextCompat.getColor(AddCharacterPopup.this, R.color.colorDefaultText));
        bBlackText = false;
    }

    public void OnClickBlackBtn(View view) {
        blackBtn.setBackgroundResource(R.drawable.common_selected_rounded_btn_bg);
        blackBtn.setTextColor(ContextCompat.getColor(AddCharacterPopup.this, R.color.colorWhite));
        whiteBtn.setBackgroundResource(R.drawable.common_gray_rounded_btn_bg);
        whiteBtn.setTextColor(ContextCompat.getColor(AddCharacterPopup.this, R.color.colorDefaultText));
        bBlackText = true;
    }

    public void onClickColorSettingView(View view) {
        ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setAllowPresets(false)
                .setDialogId(1010)
                .setColor(nBgColor)
                .setShowAlphaSlider(true)
                .show(AddCharacterPopup.this);
    }

    public void onClickCloseBtn(View view) {
        finish();
    }

    public void onClickAddBtn(View view) {
        String strName = inputNameView.getText().toString();

        if(strName.length() == 0) {
            Toast.makeText(AddCharacterPopup.this, "이름을 입력해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        if(nameList.contains(strName) && !strName.equals(strOriginalName)) {
            Toast.makeText(AddCharacterPopup.this, "이미 등록된 이름입니다.", Toast.LENGTH_LONG).show();
            return;
        }

        oldIntent.putExtra("NAME", strName);
        oldIntent.putExtra("LEFTRIGHT", nLeftRight);
        oldIntent.putExtra("URI", imgUri);
        oldIntent.putExtra("BLACK_TEXT", bBlackText);

        if(bColor)
            oldIntent.putExtra("BALLOON_COLOR", bgColor);

        if(nSelectedIndex > -1)
            oldIntent.putExtra("INDEX", nSelectedIndex);

        setResult(RESULT_OK, oldIntent);

        finish();
    }

    public void OnClickLeftBtn(View view) {
        leftBtn.setBackgroundResource(R.drawable.common_selected_rounded_btn_bg);
        leftBtn.setTextColor(ContextCompat.getColor(AddCharacterPopup.this, R.color.colorWhite));
        rightBtn.setBackgroundResource(R.drawable.common_gray_rounded_btn_bg);
        rightBtn.setTextColor(ContextCompat.getColor(AddCharacterPopup.this, R.color.colorDefaultText));

        nLeftRight = 0;
    }

    public void OnClickRightBtn(View view) {
        rightBtn.setBackgroundResource(R.drawable.common_selected_rounded_btn_bg);
        rightBtn.setTextColor(ContextCompat.getColor(AddCharacterPopup.this, R.color.colorWhite));
        leftBtn.setBackgroundResource(R.drawable.common_gray_rounded_btn_bg);
        leftBtn.setTextColor(ContextCompat.getColor(AddCharacterPopup.this, R.color.colorDefaultText));

        nLeftRight = 1;
    }

    public void OnClickPhotoBtn(View view) {
        TedPermission.with(AddCharacterPopup.this)
                .setPermissionListener(permissionlistener)
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    public void onClickDeleteBtn(View view) {
        oldIntent.putExtra("DELETE", true);

        if(nSelectedIndex > -1)
            oldIntent.putExtra("INDEX", nSelectedIndex);

        setResult(RESULT_OK, oldIntent);

        finish();
    }

    private PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {             // 모든 권한 획득
            Intent intent = new Intent(AddCharacterPopup.this, MediaSelectPopup.class);
            intent.putExtra("TYPE", TYPE_FACE_IMG.ordinal());
            startActivity(intent);
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            Toast.makeText(AddCharacterPopup.this, "권한을 거부하셨습니다.", Toast.LENGTH_LONG).show();
        }
    };
}
