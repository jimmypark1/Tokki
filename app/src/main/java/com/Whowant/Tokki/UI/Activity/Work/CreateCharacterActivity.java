package com.Whowant.Tokki.UI.Activity.Work;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Photopicker.PhotoPickerActivity;
import com.Whowant.Tokki.UI.Popup.MediaSelectPopup;
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

public class CreateCharacterActivity extends AppCompatActivity implements ColorPickerDialogListener  {
    public static ArrayList<String> nameList;
    public static CharacterVO characterVO = null;

    private EditText inputNameView;
    private Intent oldIntent;
    private ImageView currentColorView;
    private Button addBtn;
    private Button deleteBtn;
    private int    nLeftRight = 0;
    private boolean bBlackName = true;
    private boolean bBlackText = true;
    private ImageView faceView;
    private String imgUri = null;
    private int nSelectedIndex = -1;
    private String strOriginalName = null;
    private int    nBgColor = 0;
    private String bgColor;
    boolean bColor = false;

    private LinearLayout nameBlackLayout, nameWhiteLayout, leftLayout, rightLayout, textBlackLayout, textWhiteLayout;
    private RadioButton nameBlackCheckbox, nameWhiteCheckbox, leftCheckbox, rightCheckbox, textBlackCheckbox, textWhiteCheckbox;
    private TextView prevTextView;
    private RelativeLayout prevBalloonView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_character);

        oldIntent = getIntent();

        inputNameView = findViewById(R.id.inputNameView);
        currentColorView = findViewById(R.id.currentColorView);
        faceView = findViewById(R.id.faceView);
        addBtn = findViewById(R.id.addBtn);
        deleteBtn = findViewById(R.id.deleteBtn);

        nBgColor = Color.parseColor("#ffffff");

        nameBlackLayout = findViewById(R.id.nameBlackLayout);
        nameWhiteLayout = findViewById(R.id.nameWhiteLayout);
        leftLayout = findViewById(R.id.leftLayout);
        rightLayout = findViewById(R.id.rightLayout);
        textBlackLayout = findViewById(R.id.textBlackLayout);
        textWhiteLayout = findViewById(R.id.textWhiteLayout);

        nameBlackCheckbox = findViewById(R.id.nameBlackCheckbox);
        nameWhiteCheckbox = findViewById(R.id.nameWhiteCheckbox);
        leftCheckbox = findViewById(R.id.leftCheckbox);
        rightCheckbox = findViewById(R.id.rightCheckbox);
        textBlackCheckbox = findViewById(R.id.textBlackCheckbox);
        textWhiteCheckbox = findViewById(R.id.textWhiteCheckbox);
        prevBalloonView = findViewById(R.id.prevBalloonView);
        prevTextView = findViewById(R.id.prevTextView);

        nSelectedIndex = oldIntent.getIntExtra("INDEX", -1);
        if(characterVO != null && nSelectedIndex > -1) {
            initViews();
        }

        nameBlackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameBlackCheckbox.setChecked(true);
                nameWhiteCheckbox.setChecked(false);
                bBlackName = true;
            }
        });

        nameWhiteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameBlackCheckbox.setChecked(false);
                nameWhiteCheckbox.setChecked(true);
                bBlackName = false;
            }
        });

        leftLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leftCheckbox.setChecked(true);
                rightCheckbox.setChecked(false);
                nLeftRight = 0;
            }
        });

        rightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leftCheckbox.setChecked(false);
                rightCheckbox.setChecked(true);
                nLeftRight = 1;
            }
        });

        textBlackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textBlackCheckbox.setChecked(true);
                textWhiteCheckbox.setChecked(false);
                prevTextView.setTextColor(Color.parseColor("#000000"));
                bBlackText = true;
            }
        });

        textWhiteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textBlackCheckbox.setChecked(false);
                textWhiteCheckbox.setChecked(true);
                prevTextView.setTextColor(Color.parseColor("#ffffff"));
                bBlackText = false;
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oldIntent.putExtra("INDEX", nSelectedIndex);
                oldIntent.putExtra("DELETE", true);
                setResult(RESULT_OK, oldIntent);
                finish();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        imgUri = intent.getStringExtra("URI");
        if(imgUri != null) {
            faceView.setBackgroundResource(0);
            Glide.with(this)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(Uri.parse(imgUri))
                    .apply(new RequestOptions().circleCrop())
                    .into(faceView);
        }
    }

    public void onClickTopLeftBtn(View veiw) {
        finish();
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

                currentColorView.setBackgroundColor(nColor);
                PorterDuffColorFilter greyFilter = new PorterDuffColorFilter(nColor, PorterDuff.Mode.MULTIPLY);
                prevBalloonView.getBackground().setColorFilter(greyFilter);
            }
        }

        if(characterVO.getImage() != null && !characterVO.getImage().equals("null")) {
            faceView.setBackgroundResource(0);
            Glide.with(this)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(Uri.parse(imgUri))
                    .apply(new RequestOptions().circleCrop())
                    .into(faceView);
        } else if(characterVO.getStrImgFile() != null && !characterVO.getStrImgFile().equals("null")) {
            faceView.setBackgroundResource(0);
            Glide.with(CreateCharacterActivity.this)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(CommonUtils.strDefaultUrl + "images/" + characterVO.getStrImgFile())
                    .apply(new RequestOptions().circleCrop())
                    .into(faceView);
        }

        if(characterVO.getDirection() == 0) { // 왼쪽
            leftCheckbox.setChecked(true);
            rightCheckbox.setChecked(false);
            nLeftRight = 0;
        } else {
            leftCheckbox.setChecked(false);
            rightCheckbox.setChecked(true);
            nLeftRight = 1;
        }

        if(characterVO.isbBlackText() == true) {
            textBlackCheckbox.setChecked(true);
            textWhiteCheckbox.setChecked(false);
            bBlackText = true;
        } else {
            textBlackCheckbox.setChecked(false);
            textWhiteCheckbox.setChecked(true);
            bBlackText = false;
        }

        if(characterVO.isbBlackName()) {
            nameBlackCheckbox.setChecked(true);
            nameWhiteCheckbox.setChecked(false);
            bBlackName = true;
        } else {
            nameBlackCheckbox.setChecked(false);
            nameWhiteCheckbox.setChecked(true);
            bBlackName = false;
        }

        if(characterVO.isbBlackText()) {
            bBlackText = true;
            prevTextView.setTextColor(Color.parseColor("#000000"));
        } else {
            bBlackText = false;
            prevTextView.setTextColor(Color.parseColor("#ffffff"));
        }
    }

    public void onClickAddBtn(View view) {
        String strName = inputNameView.getText().toString();

        if(strName.length() == 0) {
            Toast.makeText(CreateCharacterActivity.this, "이름을 입력해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        if(nameList.contains(strName) && !strName.equals(strOriginalName)) {
            Toast.makeText(CreateCharacterActivity.this, "이미 등록된 이름입니다.", Toast.LENGTH_LONG).show();
            return;
        }

        oldIntent.putExtra("NAME", strName);
        oldIntent.putExtra("LEFTRIGHT", nLeftRight);
        oldIntent.putExtra("URI", imgUri);
        oldIntent.putExtra("BLACK_TEXT", bBlackText);
        oldIntent.putExtra("BLACK_NAME", bBlackName);

        if(bColor)
            oldIntent.putExtra("BALLOON_COLOR", bgColor);

        if(nSelectedIndex > -1)
            oldIntent.putExtra("INDEX", nSelectedIndex);

        setResult(RESULT_OK, oldIntent);

        finish();
    }

    public void onClickColorSettingView(View view) {
        ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setAllowPresets(false)
                .setDialogId(1010)
                .setColor(nBgColor)
                .setShowAlphaSlider(true)
                .show(CreateCharacterActivity.this);
    }

    public void OnClickPhotoBtn(View view) {
        TedPermission.with(CreateCharacterActivity.this)
                .setPermissionListener(permissionlistener)
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    private PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {             // 모든 권한 획득
            Intent intent = new Intent(CreateCharacterActivity.this, MediaSelectPopup.class);
            intent.putExtra("TYPE", PhotoPickerActivity.TYPE_FACE_IMG);
            startActivity(intent);
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            Toast.makeText(CreateCharacterActivity.this, "권한을 거부하셨습니다.", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onColorSelected(int dialogId, int color) {
        nBgColor = color;
        bgColor = String.format("#%08X", nBgColor);
        int nColor = nBgColor;

        if(bgColor.length() > 7) {
            String strColor = "#" + bgColor.substring(3);
            nColor = Color.parseColor(strColor);
        }

        currentColorView.setBackgroundColor(nColor);
        PorterDuffColorFilter greyFilter = new PorterDuffColorFilter(nColor, PorterDuff.Mode.MULTIPLY);
        prevBalloonView.getBackground().setColorFilter(greyFilter);
        bColor = true;
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }
}