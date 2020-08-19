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

public class WideBookCardListAdapter extends RecyclerView.Adapter<WideBookCardListAdapter.WideBookCardHolder>{
    private ArrayList<WorkVO> itemsList;
    private Context mContext;
    private int nViewType;

    public WideBookCardListAdapter(Context context, ArrayList<WorkVO> itemsList, int nViewType) {
        this.mContext = context;
        this.itemsList = itemsList;
        this.nViewType = nViewType;
    }

    @Override
    public WideBookCardHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_new_row, viewGroup, false);

        if(nViewType == 0)
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recommand_book_card, viewGroup, false);
        else
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.wide_book_card, null);

        WideBookCardHolder mh = new WideBookCardHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(WideBookCardHolder holder, int position) {
        WorkVO workVO = itemsList.get(position);

        holder.titleView.setText(workVO.getTitle());
        holder.titleView.setTag(position);
        holder.writerNameView.setText(workVO.getStrWriterName());
        holder.hitsCountView.setText(CommonUtils.getPointCount(workVO.getnTapCount()));

        String strImgUrl = workVO.getCoverFile();

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
                .apply(new RequestOptions().override(800, 800))
                .into(holder.coverView);
    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class WideBookCardHolder extends RecyclerView.ViewHolder {
        protected TextView titleView;
        protected TextView writerNameView;
        protected ImageView coverView;
        protected TextView hitsCountView;

        public WideBookCardHolder(View view) {
            super(view);
            this.titleView = (TextView) view.findViewById(R.id.titleView);
            this.coverView = (ImageView) view.findViewById(R.id.coverImgView);
            this.writerNameView = (TextView) view.findViewById(R.id.writerNameView);
            this.hitsCountView = (TextView) view.findViewById(R.id.hitsCountView);
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
