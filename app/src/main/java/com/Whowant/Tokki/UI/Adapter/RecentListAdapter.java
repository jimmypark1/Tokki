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

public class RecentListAdapter extends RecyclerView.Adapter<RecentListAdapter.WideBookCardHolder> {
    private ArrayList<WorkVO> itemsList;
    private Context mContext;
    private int nViewType;

    public RecentListAdapter(Context context, ArrayList<WorkVO> itemsList, int nViewType) {
        this.mContext = context;
        this.itemsList = itemsList;
        this.nViewType = nViewType;
    }

    public void setData(ArrayList<WorkVO> itemsList) {
        this.itemsList = itemsList;
        notifyDataSetChanged();
    }

    @Override
    public WideBookCardHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
//            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.round_squre_stroke_gray_bg.main_new_row,viewGroup,false);
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_main_contents, viewGroup, false);
        WideBookCardHolder mh = new WideBookCardHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(WideBookCardHolder holder, int position) {
        WorkVO workVO = itemsList.get(position);

        holder.titleView.setText(workVO.getTitle());
        holder.tabCountTv.setText(String.valueOf(CommonUtils.getPointCount(workVO.getnHitsCount())));
        holder.commentCountTv.setText(String.valueOf(CommonUtils.getPointCount(workVO.getnCommentCount())));
//        holder.writerNameView.setText("By " + workVO.getStrWriterName());
//        holder.dateView.setText(workVO.getStrUpdateDate().substring(0, 10));
//        holder.descView.setText(workVO.getSynopsis());

        String strImgUrl = workVO.getCoverFile();
        holder.coverView.setClipToOutline(true);

        if (strImgUrl == null || strImgUrl.equals("null") || strImgUrl.equals("NULL") || strImgUrl.length() == 0) {
            Glide.with(mContext)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(R.drawable.no_poster)
                    .apply(new RequestOptions().centerCrop())
                    .into(holder.coverView);
            return;
        } else if (!strImgUrl.startsWith("http")) {
            strImgUrl = CommonUtils.strDefaultUrl + "images/" + strImgUrl;
        }

        Glide.with(mContext)
                .asBitmap() // some .jpeg files are actually gif
                .placeholder(R.drawable.no_poster)
                .load(strImgUrl)
                .apply(new RequestOptions().centerCrop())
                .into(holder.coverView);
    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class WideBookCardHolder extends RecyclerView.ViewHolder {

        protected TextView titleView/*, writerNameView, dateView, descView*/;
        protected ImageView coverView;
        public TextView tabCountTv;
        public TextView commentCountTv;

        public WideBookCardHolder(View view) {
            super(view);
            this.titleView = (TextView) view.findViewById(R.id.titleView);
            this.coverView = (ImageView) view.findViewById(R.id.coverImgView);
            this.tabCountTv = view.findViewById(R.id.tv_row_main_content_tab_count);
            this.commentCountTv = view.findViewById(R.id.tv_row_main_content_comment_count);
//            this.writerNameView = (TextView) view.findViewById(R.id.writerNameView);
//            this.dateView = (TextView) view.findViewById(R.id.dateView);
//            this.descView = view.findViewById(R.id.descView);
            this.coverView.setClipToOutline(true);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int nPosition = getAdapterPosition();
                    WorkVO vo = itemsList.get(nPosition);

                    if (vo.getnWorkID() == 0)
                        return;

                    Intent intent = new Intent(mContext, WorkMainActivity.class);
                    intent.putExtra("WORK_ID", vo.getnWorkID());
                    mContext.startActivity(intent);
                }
            });
        }
    }
}