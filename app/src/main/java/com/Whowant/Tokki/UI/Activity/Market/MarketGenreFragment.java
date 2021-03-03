package com.Whowant.Tokki.UI.Activity.Market;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.MarketVO;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MarketGenreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MarketGenreFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private MarketGenreAdapter marketGenreAdapter;
    private ArrayList<MarketVO> markets;

    RecyclerView recyclerView;

    public int position = 0;
    public int topPosition = 0;


    public MarketGenreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MarketGenreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MarketGenreFragment newInstance(String param1, String param2) {
        MarketGenreFragment fragment = new MarketGenreFragment();
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

    public void Update(int pos, String content)
    {
        if(pos > 0)
        {

            getMarketDataSort(content);
        }
        else
        {
            getMarketData();
        }


    }
    @Override
    public void onResume() {
        super.onResume();
        if(position == 0)
        {
            getMarketData();
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_market_contents, container, false);
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        return v;//inflater.inflate(R.layout.fragment_market_genre, container, false);
    }

    private void getMarketData() {
//        CommonUtils.showProgressDialog(getActivity(), "서버에서 데이터를 가져오고 있습니다. 잠시만 기다려주세요.");

        if(markets != null)
            markets.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {

                markets = HttpClient.getWorksOnMarket(new OkHttpClient());

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //       CommonUtils.hideProgressDialog();



                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(markets == null) {
                                    Toast.makeText(getActivity(), "서버와의 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                marketGenreAdapter = new MarketGenreAdapter(getActivity(),markets);
                                recyclerView.setAdapter(marketGenreAdapter);

                                //                       marketAdapter.notifyDataSetChanged();
                            }
                        });


                    }
                });
            }
        }).start();
    }
    private void getMarketDataSort(String genre) {
//        CommonUtils.showProgressDialog(getActivity(), "서버에서 데이터를 가져오고 있습니다. 잠시만 기다려주세요.");

        if(markets != null)
            markets.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {

                markets = HttpClient.getWorksSorByGenreOnMarket(new OkHttpClient(),genre);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //       CommonUtils.hideProgressDialog();



                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(markets == null) {
                                    Toast.makeText(getActivity(), "서버와의 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                marketGenreAdapter = new MarketGenreAdapter(getActivity(),markets);
                                recyclerView.setAdapter(marketGenreAdapter);

                                //                       marketAdapter.notifyDataSetChanged();
                            }
                        });


                    }
                });
            }
        }).start();
    }
    public interface ItemClickListener{
        void onItemClickListener(View v, int position);
    }
    public class MarketGenreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
        Context context;
        ArrayList<MarketVO> arrayList;


        public MarketGenreAdapter(Context context,ArrayList<MarketVO> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


            View v = LayoutInflater.from(context).inflate(R.layout.market_row, parent, false);



            return new MarketGenreViewHolder(v);
        }

        private int dpToPx(Context context, int dp) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
        }
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
        {
            MarketGenreViewHolder viewHolder = (MarketGenreViewHolder) holder;



            MarketVO data = arrayList.get(position);


            viewHolder.title.setText(data.getTitle());
            viewHolder.sypnopsis.setText(data.getSypnopsis());

            viewHolder.tag.setText(data.getTag());
            viewHolder.copyright0.setText("저작권 : " + data.getCopyright0());
            if(data.getStrField()!= null && data.getStrField().length() > 0)
                viewHolder.copyright1.setText("판권 :" + data.getStrField());
            else
                viewHolder.copyright1.setText("");

            viewHolder.career.setText(data.getCareer());
            /*
                  carrotNum.text = String(Int( nPrice! / 120)) + "개"
               // let dPrice =  Double(dummy)! * 0.8
                let dNPrice =  Int(Double(dummy)! * 0.8)
             */

            float div = data.getPrice() / 1000000;
            if(div < 1)
            {
                int nCarrot = (int)data.getPrice() / 120;


                viewHolder.carrot.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams params = viewHolder.carrot.getLayoutParams();
                params.width = dpToPx(getActivity(),36);
                params.height = dpToPx(getActivity(),36);

                String strPrice =  String.format("%,d", data.getPrice());
                viewHolder.price.setText(String.valueOf(nCarrot) + "개" +" (" + strPrice +"원)");
            }
            else
            {
                ViewGroup.LayoutParams params = viewHolder.carrot.getLayoutParams();
                params.width = 0;

                viewHolder.carrot.setVisibility(View.INVISIBLE);


                String strPrice =  String.format("%,d", data.getPrice());

                viewHolder.price.setText(strPrice +"원");

            }


            String strCover = CommonUtils.strDefaultUrl + "images/" + data.getCover();

            Glide.with(context)
                    .asBitmap() // some .jpeg files are actually gif
                    .placeholder(R.drawable.no_poster)
                    .load(strCover)
                    .into(viewHolder.cover);
/*
            holder.setItemClickListener(new ItemClickListener() {

                @Override
                public void onItemClickListener(View v, int position) {


                }
            });

 */
        }



        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }


    public class MarketGenreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        TextView sypnopsis;
        TextView tag;
        TextView copyright0;
        TextView copyright1;
        TextView career;
        ImageView cover;
        TextView price;
        ImageView carrot;
        MarketGenreFragment.ItemClickListener itemClickListener;


        public MarketGenreViewHolder(@NonNull View itemView) {

            super(itemView);
            title = itemView.findViewById(R.id.titleView);
            sypnopsis = itemView.findViewById(R.id.synopsisView);
            tag = itemView.findViewById(R.id.tag);
            copyright0 = itemView.findViewById(R.id.copyright0);
            copyright1 = itemView.findViewById(R.id.copyright1);
            career = itemView.findViewById(R.id.career);
            cover= itemView.findViewById(R.id.coverImgView);
            carrot= itemView.findViewById(R.id.carrot);

            price = itemView.findViewById(R.id.price);

            itemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View v){
            // this.itemClickListener.onItemClickListener(v, getLayoutPosition());
            int pos = getLayoutPosition();
            MarketVO market = markets.get(pos);
            Intent intent = new Intent(getContext(), MarketDetailActivity.class);
            intent.putExtra("MARKET_DATA", market);

            //MarketDetailActivity
            getContext().startActivity(intent);
        }



    }
}