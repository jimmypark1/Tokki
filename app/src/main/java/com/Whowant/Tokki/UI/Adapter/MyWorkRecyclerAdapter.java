package com.Whowant.Tokki.UI.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Work.WorkMainActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class MyWorkRecyclerAdapter extends RecyclerView.Adapter<MyWorkRecyclerAdapter.ViewHolder> {
    private ArrayList<WorkVO> itemsList;
    private Context mContext;

    public MyWorkRecyclerAdapter(Context context, ArrayList<WorkVO> itemsList) {
        this.mContext = context;
        this.itemsList = itemsList;
    }

    @Override
    public MyWorkRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_work_card, null);
        MyWorkRecyclerAdapter.ViewHolder mh = new MyWorkRecyclerAdapter.ViewHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(MyWorkRecyclerAdapter.ViewHolder holder, int i) {

        WorkVO workVO = itemsList.get(i);

        holder.titleView.setText(workVO.getTitle());
        holder.titleView.setTag(i);

        String strImgUrl = workVO.getCoverFile();
        holder.coverView.setClipToOutline(true);
        if(strImgUrl == null || strImgUrl.equals("null") || strImgUrl.equals("NULL") || strImgUrl.length() == 0) {
            Glide.with(mContext)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(R.drawable.no_poster_horizontal)
                    .apply(new RequestOptions().override(800, 800))
                    .into(holder.coverView);
            return;
        } else if(!strImgUrl.startsWith("http")) {
            strImgUrl = CommonUtils.strDefaultUrl + "images/" + strImgUrl;
        }

        Glide.with(mContext)
                .asBitmap() // some .jpeg files are actually gif
                .load(strImgUrl)
                .placeholder(R.drawable.no_poster)
                .apply(new RequestOptions().override(800, 800))
                .into(holder.coverView);
    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView titleView;
        protected ImageView coverView;


        public ViewHolder(View view) {
            super(view);

            this.titleView = (TextView) view.findViewById(R.id.titleView);
            this.coverView = (ImageView) view.findViewById(R.id.coverView);
            this.coverView.setClipToOutline(true);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int nPosition = (int)titleView.getTag();
//                    Toast.makeText(v.getContext(), titleView.getText(), Toast.LENGTH_SHORT).show();
                    WorkVO vo = itemsList.get(nPosition);

                    Intent intent = new Intent(mContext, WorkMainActivity.class);
                    intent.putExtra("WORK_ID", vo.getnWorkID());
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
