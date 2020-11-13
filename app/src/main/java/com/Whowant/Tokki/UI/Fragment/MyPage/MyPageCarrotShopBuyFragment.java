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

public class MyPageCarrotShopBuyFragment extends Fragment {

    RecyclerView recyclerView;
    MyPageCarrotShopBuyAdapter adapter;
    ArrayList<CarrotVO> mArrayList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
    }

    private void initData() {
        CarrotVO vo = new CarrotVO();
        vo.setWriterName("당근 10개 구입");
        vo.setCarrotPoint(1500);
        mArrayList.add(vo);

        vo = new CarrotVO();
        vo.setWriterName("당근 30개 구입");
        vo.setCarrotPoint(2000);
        mArrayList.add(vo);

        vo = new CarrotVO();
        vo.setWriterName("당근 40개 구입");
        vo.setCarrotPoint(1500);
        mArrayList.add(vo);

        vo = new CarrotVO();
        vo.setWriterName("당근 100개 구입");
        vo.setCarrotPoint(5000);
        mArrayList.add(vo);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_page_feed, container, false);

        recyclerView = v.findViewById(R.id.recyclerView);
        adapter = new MyPageCarrotShopBuyAdapter(getContext(), mArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        return v;
    }

    public class MyPageCarrotShopBuyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Context context;
        ArrayList<CarrotVO> arrayList;

        public MyPageCarrotShopBuyAdapter(Context context, ArrayList<CarrotVO> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.row_my_page_carrot_shop_buy, parent, false);
            return new MyPageCarrotShopBuyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            CarrotVO item = arrayList.get(position);

            if (holder instanceof MyPageCarrotShopBuyViewHolder) {
                MyPageCarrotShopBuyViewHolder viewHolder = (MyPageCarrotShopBuyViewHolder) holder;

                viewHolder.titleTv.setText(item.getWriterName());
                viewHolder.cashTv.setText(item.getCarrotPoint() + "원");
            }
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }

    public class MyPageCarrotShopBuyViewHolder extends RecyclerView.ViewHolder {

        TextView titleTv;
        TextView cashTv;

        public MyPageCarrotShopBuyViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTv = itemView.findViewById(R.id.tv_row_my_page_carrot_shop_buy_title);
            cashTv = itemView.findViewById(R.id.tv_row_my_page_carrot_shop_buy_cash);
        }
    }
}
