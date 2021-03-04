package com.Whowant.Tokki.UI.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.DrawerMenu.CarrotEvent;
import com.Whowant.Tokki.UI.Activity.Work.WorkMainActivity;
import com.Whowant.Tokki.UI.Activity.Work.WorkRegActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.vansuita.gaussianblur.GaussianBlur;

import java.util.ArrayList;
import java.util.Timer;

import static android.content.ContentValues.TAG;

public class MainRecommendAdapter extends RecyclerView.Adapter<MainRecommendAdapter.RecommendCardHolder>{
    private ArrayList<WorkVO> itemsList;
    private Activity mContext;
    private Timer timer;
    private int nCurrentItem = 0;
    private int nType = 0;

    public MainRecommendAdapter(Activity context, ArrayList<WorkVO> itemsList, int type) {
        this.mContext = context;
        this.itemsList = itemsList;
        this.nType = type;
    }

    @Override
    public RecommendCardHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if(nType == 0)
        {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recommand_book_card, viewGroup, false);
            RecommendCardHolder vh = new RecommendCardHolder(v);
            return vh;
        }
        else
        {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recommand_book_card2, viewGroup, false);
            RecommendCardHolder vh = new RecommendCardHolder(v);
            return vh;
        }

    }

    @Override
    public void onBindViewHolder(RecommendCardHolder holder, int position) {
        WorkVO workVO = itemsList.get(position);
        String strImgUrl = workVO.getCoverFile();

        if (workVO.getnWorkID() == -1 && strImgUrl == "-1") {
            Glide.with(mContext)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(R.drawable.carrot_event_banner)
                    .into(holder.coverView);
            return;
        }

        if(strImgUrl == null || strImgUrl.equals("null") || strImgUrl.equals("NULL") || strImgUrl.length() == 0) {
            Glide.with(mContext)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(R.drawable.no_poster_horizontal)
                    .into(holder.coverView);
            return;
        } else if(!strImgUrl.startsWith("http")) {
            strImgUrl = CommonUtils.strDefaultUrl + "images/" + strImgUrl;
        }

        if(nType >0)
        {
          //  String strBlurUrl = workVO.getStrCoverBlurFile();
          //  strBlurUrl = CommonUtils.strDefaultUrl + "images/" + strBlurUrl;



            Glide.with(mContext)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(strImgUrl)
                    //     .into(photoIv);
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            Bitmap blurredBitmap = GaussianBlur.with(mContext).size(300).radius(20).render(resource);
//
                            holder.blurView.setImageBitmap(blurredBitmap);
//                            photoIv.setImageBitmap(blurredBitmap);

                        }
                    });

        }


        Glide.with(mContext)
                .asBitmap() // some .jpeg files are actually gif
                .placeholder(R.drawable.no_poster_horizontal)
                .load(strImgUrl)
                .into(holder.coverView);
    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class RecommendCardHolder extends RecyclerView.ViewHolder {
        protected ImageView coverView;
        protected ImageView blurView;


        public RecommendCardHolder(View view) {
            super(view);
            this.coverView = (ImageView) view.findViewById(R.id.coverImgView);
            if(nType > 0)
            {
                this.blurView = (ImageView) view.findViewById(R.id.blurImgView);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int nPosition = getAdapterPosition();
                    WorkVO vo = itemsList.get(nPosition);

                    if (itemsList.get(nPosition).getnWorkID() == -1) {
                        Intent intent = new Intent(mContext, CarrotEvent.class);
                        mContext.startActivity(intent);
                    }
                    else {
                        Intent intent = new Intent(mContext, WorkMainActivity.class);
                        intent.putExtra("WORK_ID", vo.getnWorkID());
                        intent.putExtra("WORK_TYPE", vo.getnTarget());


                        mContext.startActivity(intent);
                    }
                }
            });
        }
    }
}
