package com.Whowant.Tokki.UI.Activity.Work;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CustomUncaughtExceptionHandler;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;

public class AddWriterActivity extends AppCompatActivity {
    private EditText inputNameView;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_writer);

        Thread.UncaughtExceptionHandler handler = Thread
                .getDefaultUncaughtExceptionHandler();

        if (!(handler instanceof CustomUncaughtExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
        }

        inputNameView = findViewById(R.id.inputNameView);
    }

    public void OnClickOkBtn(View view) {
        String strName = inputNameView.getText().toString();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("서버에서 작품 목록을 가져오고 있습니다...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        if (strName.length() == 0) {
            Toast.makeText(AddWriterActivity.this, "이름을 입력해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject object = HttpClient.requestAddWriter(new OkHttpClient(), strName);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();

                        try {
                            if(object.getString("RESULT").equals("SUCCESS")) {
                                Toast.makeText(AddWriterActivity.this, "작가가 생성되었습니다.", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Toast.makeText(AddWriterActivity.this, "작가 생성에 실패하였습니다. 다른 이름을 시도해 보세요.", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }
}