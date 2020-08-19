package com.Whowant.Tokki.UI.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Work.WorkMainActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Timer;

public class MainRecommendAdapter extends RecyclerView.Adapter<MainRecommendAdapter.RecommendCardHolder>{
    private ArrayList<WorkVO> itemsList;
    private Activity mContext;
    private Timer timer;
    private int nCurrentItem = 0;

    public MainRecommendAdapter(Activity context, ArrayList<WorkVO> itemsList) {
        this.mContext = context;
        this.itemsList = itemsList;
    }

    @Override
    public RecommendCardHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recommand_book_card, viewGroup, false);
        RecommendCardHolder vh = new RecommendCardHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecommendCardHolder holder, int position) {
        WorkVO workVO = itemsList.get(position);
        String strImgUrl = workVO.getCoverFile();

        if(strImgUrl == null || strImgUrl.equals("null") || strImgUrl.equals("NULL") || strImgUrl.length() == 0) {
            Glide.with(mContext)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(R.drawable.no_poster)
                    .apply(new RequestOptions().override(800, 800))
                    .into(holder.coverView);
            return;
        } else if(!strImgUrl.startsWith("http")) {
            strImgUrl = CommonUtils.strDefaultUrl + "images/" + strImgUrl;
        }

        Glide.with(mContext)
                .asBitmap() // some .jpeg files are actually gif
                .placeholder(R.drawable.no_poster)
                .load(strImgUrl)
                .into(holder.coverView);
    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class RecommendCardHolder extends RecyclerView.ViewHolder {
        protected ImageView coverView;


        public RecommendCardHolder(View view) {
            super(view);
            this.coverView = (ImageView) view.findViewById(R.id.coverImgView);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int nPosition = getAdapterPosition();
                    WorkVO vo = itemsList.get(nPosition);
                    Intent intent = new Intent(mContext, WorkMainActivity.class);
                    intent.putExtra("WORK_ID", vo.getnWorkID());
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
