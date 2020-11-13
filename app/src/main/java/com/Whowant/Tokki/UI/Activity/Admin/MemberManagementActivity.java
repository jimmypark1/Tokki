package com.Whowant.Tokki.UI.Activity.Admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Writer.WriterMainActivity;
import com.Whowant.Tokki.UI.Popup.MemberWithdrawActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.UserInfoVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class MemberManagementActivity extends AppCompatActivity {
    private ListView listView;
    private EditText inputSearchTextView;
    private TextView emptyView;
    private ArrayList<UserInfoVO> userInfoList;
    private CUserInfoArrayAdapter aa;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_management);

        initView();

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        listView = findViewById(R.id.listView);
        emptyView = findViewById(R.id.emptyView);
        inputSearchTextView = findViewById(R.id.inputSearchTextView);
        inputSearchTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        requestSearch();
                        break;
                    case 0:
                        requestSearch();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });

        userInfoList = new ArrayList<>();
        aa = new CUserInfoArrayAdapter(this, R.layout.admin_member_row, userInfoList);
        listView.setAdapter(aa);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                UserInfoVO vo = userInfoList.get(position);
                Intent intent = new Intent(MemberManagementActivity.this, WriterMainActivity.class);
                intent.putExtra("USER_ID", vo.getStrUserID());
                intent.putExtra("WRITER", false);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_top_layout_title)).setText("회원관리");

        findViewById(R.id.ib_top_layout_back).setVisibility(View.VISIBLE);
        findViewById(R.id.ib_top_layout_back).setOnClickListener((v) -> finish());
    }

    @Override
    public void onResume() {
        super.onResume();
        getMemberData();
    }

    public void onClickBackBtn(View view) {
        finish();
    }

    private void getMemberData() {
        userInfoList.clear();
        CommonUtils.showProgressDialog(this, "서버와 통신중입니다. 잠시만 기다려주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<UserInfoVO> list = HttpClient.getAllUserListByAdmin(new OkHttpClient());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (list != null) {
                            userInfoList.addAll(list);
                            aa.notifyDataSetChanged();

                            if (list.size() == 0) {
                                emptyView.setVisibility(View.VISIBLE);
                            }
                            CommonUtils.hideProgressDialog();
                        } else {
                            CommonUtils.hideProgressDialog();
                            Toast.makeText(MemberManagementActivity.this, "서버와의 통신에 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    public void onClickSearchBtn(View view) {
        requestSearch();
    }

    private void requestSearch() {
        imm.hideSoftInputFromWindow(inputSearchTextView.getWindowToken(), 0);
        String strKeyword = inputSearchTextView.getText().toString();

        if (strKeyword.length() == 0) {
            Toast.makeText(this, "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        userInfoList.clear();
        CommonUtils.showProgressDialog(this, "서버와 통신중입니다. 잠시만 기다려주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<UserInfoVO> list = HttpClient.getSearchUserListByAdmin(new OkHttpClient(), strKeyword);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (list != null) {
                            userInfoList.addAll(list);
                            aa.notifyDataSetChanged();

                            if (list.size() == 0) {
                                emptyView.setVisibility(View.VISIBLE);
                            }
                            CommonUtils.hideProgressDialog();
                        } else {
                            CommonUtils.hideProgressDialog();
                            Toast.makeText(MemberManagementActivity.this, "서버와의 통신에 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    public class CUserInfoArrayAdapter extends ArrayAdapter<Object> {
        private LayoutInflater mLiInflater;

        CUserInfoArrayAdapter(Context context, int layout, List titles) {
            super(context, layout, titles);
            mLiInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = mLiInflater.inflate(R.layout.admin_member_row, parent, false);

            UserInfoVO vo = userInfoList.get(position);

            ImageView faceView = convertView.findViewById(R.id.faceView);
            TextView nameView = convertView.findViewById(R.id.nameView);
            TextView emailView = convertView.findViewById(R.id.emailView);
            TextView workCountView = convertView.findViewById(R.id.workCountView);
            TextView commentCountView = convertView.findViewById(R.id.commentCountView);
            TextView followerCountView = convertView.findViewById(R.id.followerCountView);
            LinearLayout menuBtn = convertView.findViewById(R.id.menuBtn);

            String strPhoto = vo.getStrUserPhoto();

            if (!strPhoto.startsWith("http"))
                strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

            Glide.with(MemberManagementActivity.this)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(strPhoto)
                    .apply(new RequestOptions().circleCrop())
                    .into(faceView);

            nameView.setText(vo.getStrUserName());
            emailView.setText(vo.getStrUserEmail());
            workCountView.setText(CommonUtils.getPointCount(vo.getnWorkCount()));
            commentCountView.setText(CommonUtils.getPointCount(vo.getnCommentCount()));
            followerCountView.setText(CommonUtils.getPointCount(vo.getnFollowerCount()));

            menuBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(MemberManagementActivity.this, view);
                    popup.getMenuInflater().inflate(R.menu.writer_admin_menu, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.action_btn3:
                                    requestMemberWithdraw(vo.getStrUserID());
                                    break;
                            }
                            return true;
                        }
                    });

                    popup.show();//showing popup menu
                }
            });

            return convertView;
        }
    }

    private void requestMemberWithdraw(String strUserID) {
        Intent intent = new Intent(MemberManagementActivity.this, MemberWithdrawActivity.class);
        intent.putExtra("USER_ID", strUserID);
        startActivityForResult(intent, 1000);
    }
}
