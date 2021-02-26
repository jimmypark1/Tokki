package com.Whowant.Tokki.UI.Activity.Market;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.MarketVO;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class MarketAddActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    private ArrayList<WorkVO> works;

    WorktAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_add);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getWorkstData();

    }
    public void onClickTopLeftBtn(View view) {
        finish();
    }

    private void getWorkstData() {
//        CommonUtils.showProgressDialog(getActivity(), "서버에서 데이터를 가져오고 있습니다. 잠시만 기다려주세요.");

        if(works != null)
            works.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

                works = HttpClient.GetAllWorkListWithID(new OkHttpClient(),pref.getString("USER_ID", "Guest"));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //       CommonUtils.hideProgressDialog();



                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(works == null) {
                                    Toast.makeText(MarketAddActivity.this, "서버와의 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                adapter = new WorktAdapter(MarketAddActivity.this,works);
                                recyclerView.setAdapter(adapter);

                                //                       marketAdapter.notifyDataSetChanged();
                            }
                        });



                    }
                });
            }
        }).start();
    }

    public class WorktAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
        Context context;
        ArrayList<WorkVO> arrayList;


        public WorktAdapter(Context context,ArrayList<WorkVO> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


            View v = LayoutInflater.from(context).inflate(R.layout.row_literature, parent, false);



            return new WorksViewHolder(v);
        }


        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
        {
            WorksViewHolder viewHolder = (WorksViewHolder) holder;



            WorkVO data = arrayList.get(position);

            viewHolder.title.setText(data.getTitle());
            viewHolder.sypnopsis.setText(data.getSynopsis());

            viewHolder.date.setText(data.getStrUpdateDate());


            String strCover = CommonUtils.strDefaultUrl + "images/" + data.getCoverFile();

            Glide.with(context)
                    .asBitmap() // some .jpeg files are actually gif
                    .placeholder(R.drawable.no_poster)
                    .load(strCover)
                    .into(viewHolder.cover);


/*
            viewHolder.title.setText(data.getTitle());
            viewHolder.tag.setText(data.getTag());
            viewHolder.copyright0.setText(data.getCopyright0());
            viewHolder.copyright1.setText(data.getCopyright1());
            viewHolder.career.setText(data.getCareer());

            String strCover = CommonUtils.strDefaultUrl + "images/" + data.getCover();

            Glide.with(context)
                    .asBitmap() // some .jpeg files are actually gif
                    .placeholder(R.drawable.no_poster)
                    .load(strCover)
                    .into(viewHolder.cover);


 */
            /*
            ImageView coverImgView = viewHolder.findViewById(R.id.iv_row_literature_photo);
            TextView titleView = convertView.findViewById(R.id.tv_row_literature_title);
            TextView synopsisView = convertView.findViewById(R.id.tv_row_literature_contents);
//            TextView writerNameView = convertView.findViewById(R.id.writerNameView);
            TextView dateView = convertView.findViewById(R.id.tv_row_literature_date);
            coverImgView.setClipToOutline(true);
//            ImageView coverImgView = convertView.findViewById(R.id.coverImgView);
//            TextView titleView = convertView.findViewById(R.id.titleView);
//            TextView synopsisView = convertView.findViewById(R.id.synopsisView);
//            TextView writerNameView = convertView.findViewById(R.id.writerNameView);
//            TextView dateView = convertView.findViewById(R.id.dateView);

            String strImgUrl = workVO.getCoverFile();
            nTarget = workVO.getnTarget();

            if(strImgUrl == null || strImgUrl.equals("null") || strImgUrl.equals("NULL") || strImgUrl.length() == 0) {
                Glide.with(getActivity())
                        .asBitmap() // some .jpeg files are actually gif
                        .load(R.drawable.no_poster)
//                        .apply(new RequestOptions().override(800, 800))
                        .into(coverImgView);
            } else {
                if(!strImgUrl.startsWith("http")) {
                    strImgUrl = CommonUtils.strDefaultUrl + "images/" + strImgUrl;
                }

                Glide.with(getActivity())
                        .asBitmap() // some .jpeg files are actually gif
                        .placeholder(R.drawable.no_poster)
                        .load(strImgUrl)
                        .apply(new RequestOptions().override(800, 800))
                        .into(coverImgView);
            }

            titleView.setText(workVO.getTitle());
            synopsisView.setText(workVO.getSynopsis());
//            writerNameView.setText("by " + workVO.getStrWriterName());
            dateView.setText(workVO.getCreatedDate().substring(0, 10));

             */
        }



        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }


    public class WorksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        TextView sypnopsis;
        TextView date;
        /*
        TextView copyright0;
        TextView copyright1;
        TextView career;

         */
        ImageView cover;
        MarketContentsFragment.ItemClickListener itemClickListener;


        public WorksViewHolder(@NonNull View itemView) {

            super(itemView);
            title = itemView.findViewById(R.id.tv_row_literature_title);
            sypnopsis = itemView.findViewById(R.id.tv_row_literature_contents);
            date = itemView.findViewById(R.id.tv_row_literature_date);

            cover= itemView.findViewById(R.id.iv_row_literature_photo);

            itemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View v){
            // this.itemClickListener.onItemClickListener(v, getLayoutPosition());

            int pos = getLayoutPosition();
            WorkVO work = works.get(pos);
            Intent intent = new Intent(getApplicationContext() , MarketAddEditActivity.class);
            intent.putExtra("WORK", work);

            //MarketDetailActivity
            startActivity(intent);


        }



    }
}