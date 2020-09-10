package com.Whowant.Tokki.UI.Activity.Carrot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.CarrotVO;

import java.util.ArrayList;
import java.util.Timer;

import okhttp3.OkHttpClient;

public class TotalCarrotListActivity extends AppCompatActivity {
    private TextView carrotCountView;
    private RecyclerView recyclerView;
    private ArrayList<CarrotVO> usedCarrotList;
    private UsedCarrotAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_carrot_list);

        carrotCountView = findViewById(R.id.carrotcountView);
        recyclerView = findViewById(R.id.recyclerView);

        getUsedCarrotList();
    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }

    private void getUsedCarrotList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
                usedCarrotList = HttpClient.getTotalCarrotList(new OkHttpClient(), pref.getString("USER_ID", "Guest"));
//                usedCarrotList = HttpClient.getTotalCarrotList(new OkHttpClient(), "paul9045");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(usedCarrotList == null) {
                            Toast.makeText(TotalCarrotListActivity.this, "서버와의 통신에 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(usedCarrotList.size() > 0) {
                            CarrotVO vo = usedCarrotList.get(0);
                            carrotCountView.setText(CommonUtils.comma(vo.getnTotalPoint()));
                        }

                        adapter = new UsedCarrotAdapter(TotalCarrotListActivity.this, usedCarrotList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(TotalCarrotListActivity.this, LinearLayoutManager.VERTICAL, false));
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        }).start();
    }

    public class UsedCarrotAdapter extends RecyclerView.Adapter<UsedCarrotAdapter.UsedCarrotHolder>{
        private ArrayList<CarrotVO> itemsList;
        private Activity mContext;
        private Timer timer;
        private int nCurrentItem = 0;

        public UsedCarrotAdapter(Activity context, ArrayList<CarrotVO> itemsList) {
            this.mContext = context;
            this.itemsList = itemsList;
        }

        @Override
        public UsedCarrotAdapter.UsedCarrotHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.used_carrot_row, viewGroup, false);
            UsedCarrotAdapter.UsedCarrotHolder vh = new UsedCarrotAdapter.UsedCarrotHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull UsedCarrotAdapter.UsedCarrotHolder holder, int position) {
            TextView dateView, usedDescView, carrotCountView, countView;

            CarrotVO vo = itemsList.get(position);
            dateView = holder.itemView.findViewById(R.id.dateView);
            usedDescView = holder.itemView.findViewById(R.id.usedDescView);
            carrotCountView = holder.itemView.findViewById(R.id.carrotCountView);
            countView = holder.itemView.findViewById(R.id.countView);

            String strDesc = "";

            // 0 - 사용,  2 - 후원받음, 3 - 별점 적립, 10 - 추천인 등록, 11 - 추천 받음
            if(vo.getType() == 11) {
                strDesc = String.format("%s 님으로부터 추천인 입력", vo.getDonationFrom());
            } else if(vo.getType() == 2) {
                strDesc = String.format("%s 님으로부터 '%s' 작품 응원", vo.getDonationName(), vo.getDonationWorkTitle());
            } else if(vo.getType() == 20) {
                strDesc = String.format("출석체크");
            } else if(vo.getType() == 3) {
                strDesc = String.format("%s 작품 %d 회차 별점 주기", vo.getDonationWorkTitle(), vo.getDotaionEpisodeOrder());
            } else if(vo.getType() == 4) {
                strDesc = String.format("%s 작품 %d 회차 댓글 달기", vo.getDonationWorkTitle(), vo.getDotaionEpisodeOrder());
            }

            dateView.setText(vo.getUseDate().substring(0, 10));
            usedDescView.setText(strDesc);
            carrotCountView.setText("+" + CommonUtils.comma(vo.getCarrotPoint()));
            carrotCountView.setTextColor(ContextCompat.getColor(TotalCarrotListActivity.this, R.color.colorPrimary));
            countView.setTextColor(ContextCompat.getColor(TotalCarrotListActivity.this, R.color.colorPrimary));
        }

        @Override
        public int getItemCount() {
            return (null != itemsList ? itemsList.size() : 0);
        }

        public class UsedCarrotHolder extends RecyclerView.ViewHolder {
            public UsedCarrotHolder(View view) {
                super(view);
            }
        }
    }
}