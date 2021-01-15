package com.Whowant.Tokki.UI.Fragment.MyPage;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.VO.CarrotVO;

import java.util.ArrayList;

public class MyPageCarrotShopGetFragment extends Fragment {

    RecyclerView recyclerView;
    MyPageCarrotShopGetAdapter adapter;
    ArrayList<CarrotVO> mArrayList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_page_feed, container, false);

        recyclerView = v.findViewById(R.id.recyclerView);
        adapter = new MyPageCarrotShopGetAdapter(getContext(), mArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        return v;
    }

    private void initData() {
        CarrotVO vo = new CarrotVO();
        vo.setWriterName("앱 출석 및 추천하기");
        vo.setCarrotPoint(5);
        mArrayList.add(vo);

        vo = new CarrotVO();
        vo.setWriterName("앱에서 댓글 달기");
        vo.setCarrotPoint(5);
        mArrayList.add(vo);

        vo = new CarrotVO();
        vo.setWriterName("작품에 응원받기");
        vo.setCarrotPoint(5);
        mArrayList.add(vo);

        vo = new CarrotVO();
        vo.setWriterName("광고 보기");
        vo.setCarrotPoint(5);
        mArrayList.add(vo);
    }

    public class MyPageCarrotShopGetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Context context;
        ArrayList<CarrotVO> arrayList;

        public MyPageCarrotShopGetAdapter(Context context, ArrayList<CarrotVO> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.row_my_page_carrot_shop_get, parent, false);
            return new MyPageCarrotShopGetViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            CarrotVO item = arrayList.get(position);

            if (holder instanceof MyPageCarrotShopGetViewHolder) {
                MyPageCarrotShopGetViewHolder viewHolder = (MyPageCarrotShopGetViewHolder) holder;

                viewHolder.titleTv.setText(item.getWriterName());
                viewHolder.countTv.setText(String.valueOf(item.getCarrotPoint()));
            }
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }

    public class MyPageCarrotShopGetViewHolder extends RecyclerView.ViewHolder {

        TextView titleTv;
        TextView countTv;

        public MyPageCarrotShopGetViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTv = itemView.findViewById(R.id.tv_row_my_page_carrot_shop_get_title);
            countTv = itemView.findViewById(R.id.tv_row_my_page_carrot_shop_get_count);
        }
    }
}
