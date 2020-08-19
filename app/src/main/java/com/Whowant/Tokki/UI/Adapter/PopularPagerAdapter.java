package com.Whowant.Tokki.UI.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Work.WorkMainActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Timer;

public class PopularPagerAdapter extends RecyclerView.Adapter<PopularPagerAdapter.RecommendCardHolder>{
    private ArrayList<WorkVO> itemsList;
    private Activity mContext;

    public PopularPagerAdapter(Activity context, ArrayList<WorkVO> itemsList) {
        this.mContext = context;
        this.itemsList = itemsList;
    }

    @Override
    public RecommendCardHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.popular_card, viewGroup, false);
        RecommendCardHolder vh = new RecommendCardHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecommendCardHolder holder, int position) {
        int nRealPosition = position * 2;
        WorkVO workVO = itemsList.get(nRealPosition);
        String strImgUrl = workVO.getCoverFile();

        if(strImgUrl == null || strImgUrl.equals("null") || strImgUrl.equals("NULL") || strImgUrl.length() == 0) {
            Glide.with(mContext)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(R.drawable.no_poster)
                    .apply(new RequestOptions().override(800, 800))
                    .into(holder.coverView1);
            return;
        } else if(!strImgUrl.startsWith("http")) {
            strImgUrl = CommonUtils.strDefaultUrl + "images/" + strImgUrl;
        }

        Glide.with(mContext)
                .asBitmap() // some .jpeg files are actually gif
                .placeholder(R.drawable.no_poster)
                .load(strImgUrl)
                .apply(new RequestOptions().override(800, 800))
                .into(holder.coverView1);

        holder.titleView1.setText(workVO.getTitle());
        holder.tapCountView1.setText(workVO.getnTapCount() + "");
        holder.commentCountView1.setText(workVO.getnCommentCount() + "");

        if(nRealPosition < itemsList.size() - 1) {
            holder.secondLayout.setVisibility(View.VISIBLE);
            workVO = itemsList.get(nRealPosition+1);

            strImgUrl = workVO.getCoverFile();

            if(strImgUrl == null || strImgUrl.equals("null") || strImgUrl.equals("NULL") || strImgUrl.length() == 0) {
                Glide.with(mContext)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(R.drawable.no_poster)
                        .apply(new RequestOptions().override(800, 800))
                        .into(holder.coverView2);
                return;
            } else if(!strImgUrl.startsWith("http")) {
                strImgUrl = CommonUtils.strDefaultUrl + "images/" + strImgUrl;
            }

            Glide.with(mContext)
                    .asBitmap() // some .jpeg files are actually gif
                    .placeholder(R.drawable.no_poster)
                    .load(strImgUrl)
                    .apply(new RequestOptions().override(800, 800))
                    .into(holder.coverView2);

            holder.titleView2.setText(workVO.getTitle());
            holder.tapCountView2.setText(workVO.getnTapCount() + "");
            holder.commentCountView2.setText(workVO.getnCommentCount() + "");
        } else {
            holder.secondLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if(itemsList == null || itemsList.size() == 0)
            return 0;
        else if(itemsList.size() % 2 == 0)
            return itemsList.size() / 2;
        else
            return (itemsList.size() / 2) + 1;
    }

    public class RecommendCardHolder extends RecyclerView.ViewHolder {
        protected ImageView coverView1;
        protected TextView titleView1;
        protected TextView tapCountView1;
        protected TextView commentCountView1;
        protected ImageView coverView2;
        protected TextView titleView2;
        protected TextView tapCountView2;
        protected TextView commentCountView2;
        protected RelativeLayout firstLayout, secondLayout;

        public RecommendCardHolder(View view) {
            super(view);
            this.coverView1 = (ImageView) view.findViewById(R.id.coverImgView1);
            this.titleView1 = view.findViewById(R.id.titleView1);
            this.tapCountView1 = view.findViewById(R.id.tapCountView1);
            this.commentCountView1 = view.findViewById(R.id.commentCountView1);
            this.coverView2 = (ImageView) view.findViewById(R.id.coverImgView2);
            this.titleView2 = view.findViewById(R.id.titleView2);
            this.tapCountView2 = view.findViewById(R.id.tapCountView2);
            this.commentCountView2 = view.findViewById(R.id.commentCountView2);
            this.firstLayout = view.findViewById(R.id.firstLayout);
            this.secondLayout = view.findViewById(R.id.secondLayout);

            this.firstLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int nPosition = getLayoutPosition() * 2;
                    WorkVO vo = itemsList.get(nPosition);//
                    Intent intent = new Intent(mContext, WorkMainActivity.class);
                    intent.putExtra("WORK_ID", vo.getnWorkID());
                    mContext.startActivity(intent);
                }
            });

            this.secondLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int nPosition = (getLayoutPosition() * 2) + 1;
                    WorkVO vo = itemsList.get(nPosition);//
                    Intent intent = new Intent(mContext, WorkMainActivity.class);
                    intent.putExtra("WORK_ID", vo.getnWorkID());
                    mContext.startActivity(intent);
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    int nPosition = (int)titleView.getTag();
//                    WorkVO vo = itemsList.get(nPosition);
//
//                    Intent intent = new Intent(mContext, WorkMainActivity.class);
//                    intent.putExtra("WORK_ID", vo.getnWorkID());
//                    mContext.startActivity(intent);
                }
            });
        }
    }
}
