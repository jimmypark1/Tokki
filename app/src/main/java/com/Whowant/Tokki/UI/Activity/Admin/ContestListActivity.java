package com.Whowant.Tokki.UI.Activity.Admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.DrawerMenu.ContestDetailActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.ContestVO;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class ContestListActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<ContestVO> contestList;
    private CContestArrayAdapter aa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_list);

        listView = findViewById(R.id.listView);
        getContestData();
    }

    private void getContestData() {
        CommonUtils.showProgressDialog(this, "공모전목록을 가져오고있습니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
//                workList = HttpClient.GetAllWorkListWithID(new OkHttpClient(), pref.getString("USER_ID", "Guest"));
                contestList = HttpClient.getContestList(new OkHttpClient());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        aa = new CContestArrayAdapter(ContestListActivity.this, R.layout.contest_list_row, contestList);
                        listView.setAdapter(aa);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                ContestDetailActivity.contestVO = contestList.get(i);
                                Intent intent = new Intent(ContestListActivity.this, ContestDetailActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                });
            }
        }).start();
    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }

    public class CContestArrayAdapter extends ArrayAdapter<Object>
    {
        private LayoutInflater mLiInflater;
        CContestArrayAdapter(Context context, int layout, List titles)
        {
            super(context, layout, titles);
            mLiInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            if(convertView == null)
                convertView = mLiInflater.inflate(R.layout.contest_list_row, parent, false);

            ContestVO vo = contestList.get(position);

            TextView userNameView = convertView.findViewById(R.id.userNameView);
            TextView workTitleView = convertView.findViewById(R.id.workTitleView);
            TextView contestCodeView = convertView.findViewById(R.id.contestCodeView);
            TextView dateView = convertView.findViewById(R.id.dateView);

            userNameView.setText((position+1) + ". " + vo.getUserName());
            workTitleView.setText("작품 : " + vo.getStrTitle());
            contestCodeView.setText("접수 번호 : " + vo.getContestCode());
            dateView.setText("접수 날짜 : " + vo.getRegisterDate().substring(0, 10));

            return convertView;
        }
    }
}