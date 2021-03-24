package com.Whowant.Tokki.UI.Fragment.MyPage;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Market.CarrotBuyFragment;
import com.Whowant.Tokki.UI.Activity.Market.MarketContentsFragment;
import com.Whowant.Tokki.VO.CarrotAquireVO;
import com.Whowant.Tokki.VO.CarrotItemVO;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyPageCarrotAccuireFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyPageCarrotAccuireFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView recyclerView;
    private ArrayList<CarrotAquireVO> datas = new ArrayList<CarrotAquireVO>();

    CarrotAquireAdapter adapter;


    public MyPageCarrotAccuireFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyPageCarrotAccuireFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyPageCarrotAccuireFragment newInstance(String param1, String param2) {
        MyPageCarrotAccuireFragment fragment = new MyPageCarrotAccuireFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    void initData()
    {
        if(datas != null)
        {
            datas.clear();
        }
        CarrotAquireVO data = new CarrotAquireVO();
        data.setNum("5");
        data.setTitle("광고 보기");
        datas.add(data);

        adapter = new CarrotAquireAdapter(getActivity(),datas);
        recyclerView.setAdapter(adapter);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View v = inflater.inflate(R.layout.fragment_carrot_accuire, container, false);
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        initData();

        return v;
    }
    public class CarrotAquireAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
        Context context;
        ArrayList<CarrotAquireVO> arrayList;


        public CarrotAquireAdapter(Context context,ArrayList<CarrotAquireVO> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {



            View v = LayoutInflater.from(context).inflate(R.layout.aquire_item, parent, false);

            //       recyclerView = v.findViewById(R.id.recyclerView);
            //       recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            return new CarrotAquireViewHolder(v);
        }


        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
        {
            CarrotAquireViewHolder viewHolder = (CarrotAquireViewHolder) holder;



            CarrotAquireVO data = arrayList.get(position);



        }



        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }
    public class CarrotAquireViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView desc;
        TextView price;

        MarketContentsFragment.ItemClickListener itemClickListener;


        public CarrotAquireViewHolder(@NonNull View itemView) {

            super(itemView);
            desc = itemView.findViewById(R.id.desc);
            price = itemView.findViewById(R.id.price);


            itemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View v){
            // this.itemClickListener.onItemClickListener(v, getLayoutPosition());
            int pos = getLayoutPosition();

            //initiatePurchase(pos);
        }



    }
}