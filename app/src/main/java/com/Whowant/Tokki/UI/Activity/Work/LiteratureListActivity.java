package com.Whowant.Tokki.UI.Activity.Work;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CustomUncaughtExceptionHandler;
import com.Whowant.Tokki.VO.WorkVO;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class LiteratureListActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog;
    private ArrayList<WorkVO> workList;
    private ListView listView;
    private CWorkListAdapter aa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_literature_list);

        Thread.UncaughtExceptionHandler handler = Thread
                .getDefaultUncaughtExceptionHandler();

        if (!(handler instanceof CustomUncaughtExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
        }

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                WorkVO workVO = workList.get(position);

                Intent intent = new Intent(LiteratureListActivity.this, WorkWriteMainActivity.class);
                intent.putExtra("WORK_ID", "" + workVO.getnWorkID());
                startActivity(intent);
            }
        });

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("서버에서 작품 목록을 가져오고 있습니다...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        GetAllWorkList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.work_list_menu, menu);
        return true;
    }

    private void GetAllWorkList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                workList = HttpClient.GetAllWorkListWithID(new OkHttpClient(), pref.getString("USER_ID", "Guest"));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();

                        aa = new CWorkListAdapter(LiteratureListActivity.this, R.layout.work_list_row, workList);
                        listView.setAdapter(aa);
                    }
                });
            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_btn1:              // 작가추가 클릭
                startActivity(new Intent(LiteratureListActivity.this, AddWriterActivity.class));
                return true;
            case R.id.action_btn2:              // 작품추가 클릭
                startActivity(new Intent(LiteratureListActivity.this, CreateWorkActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class CWorkListAdapter extends ArrayAdapter<Object>
    {
        private int mCellLayout;
        private LayoutInflater mLiInflater;
        private SharedPreferences.Editor editor;

        CWorkListAdapter(Context context, int layout, List titles)
        {
            super(context, layout, titles);
            mCellLayout = layout;
            mLiInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            WorkVO workVO = workList.get(position);

            if(convertView == null) {
                convertView = mLiInflater.inflate(R.layout.work_list_row, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.titleView);
            TextView synopsisView = convertView.findViewById(R.id.synopsisView);
            TextView writerNameView = convertView.findViewById(R.id.writerNameView);

            titleView.setText(workVO.getTitle());
            synopsisView.setText(workVO.getSynopsis());
            writerNameView.setText(workVO.getStrWriterName());

            return convertView;
        }
    }
}
