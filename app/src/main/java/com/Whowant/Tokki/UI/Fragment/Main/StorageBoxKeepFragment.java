package com.Whowant.Tokki.UI.Fragment.Main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Decoration.StorageBoxItemDecoration;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.SimplePreference;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class StorageBoxKeepFragment extends Fragment {

    RecyclerView recyclerView;
    StorageBoxKeepAdapter adapter;
    ArrayList<WorkVO> mArrayList = new ArrayList<>();

    private String strKeepOrder = "UPDATE";

    LinearLayout emptyLl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_storage_box_keep, container, false);

        recyclerView = v.findViewById(R.id.recyclerView);
        adapter = new StorageBoxKeepAdapter(getContext(), mArrayList);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.addItemDecoration(new StorageBoxItemDecoration(getActivity()));
        recyclerView.setAdapter(adapter);

        emptyLl = v.findViewById(R.id.ll_storage_box_keep_empty);
        emptyLl.setVisibility(View.GONE);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getKeepListData();
    }

    public class StorageBoxKeepAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Context context;
        ArrayList<WorkVO> arrayList;

        public StorageBoxKeepAdapter(Context context, ArrayList<WorkVO> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.row_storage_box_contents, parent, false);
            return new StorageBoxKeepViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            WorkVO item = arrayList.get(position);

            if (holder instanceof StorageBoxKeepViewHolder) {
                StorageBoxKeepViewHolder viewHolder = (StorageBoxKeepViewHolder) holder;

                String strImgUrl = item.getCoverFile();

                viewHolder.titleTv.setText(item.getTitle());
                viewHolder.tabCountTv.setText(CommonUtils.getPointCount(item.getnTapCount()));
                viewHolder.countTv.setText(CommonUtils.getPointCount(item.getnCommentCount()));

                if (strImgUrl == null || strImgUrl.equals("null") || strImgUrl.equals("NULL") || strImgUrl.length() == 0) {
                    Glide.with(getActivity())
                            .asBitmap() // some .jpeg files are actually gif
                            .load(R.drawable.no_poster)
//                            .apply(new RequestOptions().override(800, 800))
                            .into(viewHolder.coverIv);
                } else {
                    if (!strImgUrl.startsWith("http")) {
                        strImgUrl = CommonUtils.strDefaultUrl + "images/" + strImgUrl;
                    }

                    Glide.with(getActivity())
                            .asBitmap() // some .jpeg files are actually gif
                            .placeholder(R.drawable.no_poster)
                            .load(strImgUrl)
//                            .apply(new RequestOptions().override(800, 800))
                            .into(viewHolder.coverIv);
                }
            }
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }

    public class StorageBoxKeepViewHolder extends RecyclerView.ViewHolder {

        ImageView coverIv;
        TextView titleTv;
        TextView tabCountTv;
        TextView countTv;
        ImageView optionIv;

        public StorageBoxKeepViewHolder(@NonNull View itemView) {
            super(itemView);

            coverIv = itemView.findViewById(R.id.iv_row_storage_box_contents_cover);
            coverIv.setClipToOutline(true);
            titleTv = itemView.findViewById(R.id.tv_row_storage_box_contents_title);
            tabCountTv = itemView.findViewById(R.id.tv_row_storage_box_contents_tab_count);
            countTv = itemView.findViewById(R.id.tv_row_storage_box_contents_count);
            optionIv = itemView.findViewById(R.id.iv_row_storage_box_contents_option);
            optionIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(itemView.getContext(), optionIv);
                    popup.getMenuInflater().inflate(R.menu.storage_box_reading_card_menu, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                            }
                            return true;
                        }
                    });

                    popup.show();
                }
            });
        }
    }

    private void getKeepListData() {
        CommonUtils.showProgressDialog(getActivity(), "서버에서 데이터를 가져오고 있습니다. 잠시만 기다려주세요.");

        mArrayList.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<WorkVO> responseVo = HttpClient.getKeepWorkList(new OkHttpClient(), SimplePreference.getStringPreference(getContext(), "USER_INFO", "USER_ID", "Guest"), strKeepOrder);

                if (responseVo != null) {
                    mArrayList.addAll(responseVo);
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if (mArrayList == null) {
                            Toast.makeText(getActivity(), "서버와의 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        adapter.notifyDataSetChanged();

                        if (mArrayList.size() == 0) {
                            emptyLl.setVisibility(View.VISIBLE);
                        } else {
                            emptyLl.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        }).start();
    }

    // 보관된 이야기 선택 버튼
    public void btnKeepSelected(View v) {

    }
}
