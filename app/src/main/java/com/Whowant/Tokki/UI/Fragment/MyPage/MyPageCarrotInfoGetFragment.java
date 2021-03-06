package com.Whowant.Tokki.UI.Fragment.MyPage;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.SimplePreference;
import com.Whowant.Tokki.VO.CarrotVO;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class MyPageCarrotInfoGetFragment extends Fragment {

    RecyclerView recyclerView;
    MyPageCarrotInfoGetAdapter adapter;
    ArrayList<CarrotVO> mArrayList = new ArrayList<>();

    TextView carrotCountTv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_page_carrot_info_get, container, false);

        carrotCountTv = v.findViewById(R.id.tv_carrot_info_get_carrot_count);

        recyclerView = v.findViewById(R.id.recyclerView);


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getUsedCarrot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //getUsedCarrot();
    }


    public class MyPageCarrotInfoGetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Context context;
        ArrayList<CarrotVO> arrayList;

        public MyPageCarrotInfoGetAdapter(Context context, ArrayList<CarrotVO> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.row_my_page_carrot_info_get, parent, false);
            return new MyPageCarrotInfoGetViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            CarrotVO item = arrayList.get(position);

            if (holder instanceof MyPageCarrotInfoGetViewHolder) {
                MyPageCarrotInfoGetViewHolder viewHolder = (MyPageCarrotInfoGetViewHolder) holder;

                String strDesc = "";
                int type = item.getType();

                // 0 - ??????,  2 - ????????????, 3 - ?????? ??????, 10 - ????????? ??????, 11 - ?????? ??????
                if (type == 11) {
                    strDesc = String.format("%s ??????????????? ????????? ??????", item.getDonationFrom());
                } else if (type == 2) {
                    strDesc = String.format("%s ??????????????? '%s' ?????? ??????", item.getDonationName(), item.getDonationWorkTitle());
                } else if (type == 20) {
                    strDesc = String.format("????????????");
                } else if (type == 3) {
                    strDesc = String.format("%s ?????? %d ?????? ?????? ??????", item.getDonationWorkTitle(), item.getDotaionEpisodeOrder());
                } else if (type == 4) {
                    strDesc = String.format("%s ?????? %d ?????? ?????? ??????", item.getDonationWorkTitle(), item.getDotaionEpisodeOrder());
                }

                viewHolder.dateTv.setText(item.getUseDate().substring(0, 10));
                viewHolder.contentTv.setText(strDesc);
                viewHolder.countTv.setText(CommonUtils.comma(item.getCarrotPoint()));
            }
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }

    public class MyPageCarrotInfoGetViewHolder extends RecyclerView.ViewHolder {

        TextView dateTv;
        TextView contentTv;
        TextView countTv;

        public MyPageCarrotInfoGetViewHolder(@NonNull View itemView) {
            super(itemView);

            dateTv = itemView.findViewById(R.id.tv_row_carrot_info_get_date);
            contentTv = itemView.findViewById(R.id.tv_row_carrot_info_get_content);
            countTv = itemView.findViewById(R.id.tv_row_carrot_info_get_count);
        }
    }

    private void getUsedCarrot() {
        mArrayList.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<CarrotVO> tmp = HttpClient.getTotalCarrotList(new OkHttpClient(), SimplePreference.getStringPreference(getActivity(), "USER_INFO", "USER_ID", "Guest"));

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (tmp == null) {
                            Toast.makeText(getActivity(), "???????????? ????????? ??????????????????. ????????? ?????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        mArrayList.addAll(tmp);

                        if (mArrayList.size() > 0) {
                            CarrotVO vo = mArrayList.get(0);
                            carrotCountTv.setText(CommonUtils.comma(vo.getnTotalPoint()));
                        }
                        adapter = new MyPageCarrotInfoGetAdapter(getContext(), mArrayList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        ;
                    }
                });
            }
        }).start();
    }
}
