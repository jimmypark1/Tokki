package com.Whowant.Tokki.UI.Fragment.MyPage;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.SimplePreference;
import com.Whowant.Tokki.VO.CarrotVO;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class MyPageCarrotInfoUseFragment extends Fragment {

    RecyclerView recyclerView;
    MyPageCarrotInfoUseAdapter adapter;
    ArrayList<CarrotVO> mArrayList = new ArrayList<>();

    TextView carrotCountTv;
   // LinearLayout container;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_page_carrot_info_use, container, false);

        carrotCountTv = v.findViewById(R.id.tv_carrot_info_use_carrot_count);

        recyclerView = v.findViewById(R.id.recyclerView);
     //   container =  v.findViewById(R.id.container);
        FragmentManager childFragmentManager =  getChildFragmentManager();
        childFragmentManager.beginTransaction()
                .add(0,new MyPageCarrotInfoGetFragment())
                .commit();

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
     //   getUsedCarrot();
    }

    @Override
    public void onResume() {
        super.onResume();
        getUsedCarrot();
    }

    public class MyPageCarrotInfoUseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Context context;
        ArrayList<CarrotVO> arrayList;

        public MyPageCarrotInfoUseAdapter(Context context, ArrayList<CarrotVO> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.row_my_page_carrot_info_use, parent, false);
            return new MyPageCarrotInfoUseViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            CarrotVO item = arrayList.get(position);

            if (holder instanceof MyPageCarrotInfoUseViewHolder) {
                MyPageCarrotInfoUseViewHolder viewHolder = (MyPageCarrotInfoUseViewHolder) holder;

                viewHolder.dateTv.setText(item.getUseDate().substring(0, 10));
                // 0 - ??????,  2 - ????????????, 3 - ?????? ??????, 10 - ????????? ??????, 11 - ?????? ??????
                viewHolder.contentTv.setText(String.format("%s ????????? '%s' ????????? ??????", item.getWriterName(), item.getDonationWorkTitle()));
                viewHolder.countTv.setText("-" + CommonUtils.comma(item.getCarrotPoint()));
            }
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }

    public class MyPageCarrotInfoUseViewHolder extends RecyclerView.ViewHolder {

        TextView dateTv;
        TextView contentTv;
        TextView countTv;

        public MyPageCarrotInfoUseViewHolder(@NonNull View itemView) {
            super(itemView);

            dateTv = itemView.findViewById(R.id.tv_row_carrot_info_use_date);
            contentTv = itemView.findViewById(R.id.tv_row_carrot_info_use_content);
            countTv = itemView.findViewById(R.id.tv_row_carrot_info_use_count);
        }
    }

    private void getUsedCarrot() {
        mArrayList.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<CarrotVO> tmp = HttpClient.getUsedCarrotList(new OkHttpClient(), SimplePreference.getStringPreference(getActivity(), "USER_INFO", "USER_ID", "Guest"));

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
                        adapter = new MyPageCarrotInfoUseAdapter(getContext(), mArrayList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    }
                });
            }
        }).start();
    }
}
