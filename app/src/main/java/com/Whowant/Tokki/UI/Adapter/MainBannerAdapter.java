package com.Whowant.Tokki.UI.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.DrawerMenu.CarrotEvent;
import com.Whowant.Tokki.UI.Activity.Work.WorkMainActivity;
import com.Whowant.Tokki.UI.Activity.Work.WorkRegActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.EventVO;
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

public class MainBannerAdapter extends RecyclerView.Adapter<MainBannerAdapter.BannerCardHolder>{
    private ArrayList<EventVO> itemsList;
    private Activity mContext;
    private Timer timer;
    private int nCurrentItem = 0;
    private int nType = 0;

    public MainBannerAdapter(Activity context, ArrayList<EventVO> itemsList, int type) {
        this.mContext = context;
        this.itemsList = itemsList;
        this.nType = type;
    }

    @Override
    public BannerCardHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.banner_book_card, viewGroup, false);
        BannerCardHolder vh = new BannerCardHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(BannerCardHolder holder, int position) {
        EventVO workVO = itemsList.get(position);
        String strImgUrl = workVO.getStrEventContentsFile();


        if(strImgUrl == null || strImgUrl.equals("null") || strImgUrl.equals("NULL") || strImgUrl.length() == 0) {
            Glide.with(mContext)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(R.drawable.no_poster_horizontal)
                    .into(holder.coverView);
            return;
        } else if(!strImgUrl.startsWith("http")) {
            strImgUrl = CommonUtils.strDefaultUrl + "images/" + strImgUrl;
        }

        Display display = this.mContext.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        ViewGroup.LayoutParams params1 = holder.frame.getLayoutParams();
      //  params1.height = width / 2 + 30;

        ViewGroup.LayoutParams params = holder.coverView.getLayoutParams();
        params.height = width / 2;



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

    public class BannerCardHolder extends RecyclerView.ViewHolder {
        protected ImageView coverView;
        protected ImageView blurView;
        protected RelativeLayout frame;


        public BannerCardHolder(View view) {
            super(view);
            this.coverView = (ImageView) view.findViewById(R.id.coverImgView);
            this.frame = view.findViewById(R.id.frame);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int nPosition = getAdapterPosition();
                    EventVO vo = itemsList.get(nPosition);


                }
            });
        }
    }
}
