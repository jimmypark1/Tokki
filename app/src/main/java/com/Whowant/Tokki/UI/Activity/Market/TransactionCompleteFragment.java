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
import com.Whowant.Tokki.UI.Fragment.Friend.MessageDetailActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.SimplePreference;
import com.Whowant.Tokki.VO.MarketMsg;
import com.Whowant.Tokki.VO.MarketVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TransactionCompleteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransactionCompleteFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RecyclerView recyclerView;

    private TransactionCompleteAdapter adapter;
    private ArrayList<MarketVO> markets;


    public TransactionCompleteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TransactionCompleteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TransactionCompleteFragment newInstance(String param1, String param2) {
        TransactionCompleteFragment fragment = new TransactionCompleteFragment();
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

    @Override
    public void onResume() {
        super.onResume();
        getMarketData();
    }

    private void getMarketData() {
//        CommonUtils.showProgressDialog(getActivity(), "서버에서 데이터를 가져오고 있습니다. 잠시만 기다려주세요.");

        if(markets != null)
            markets.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String userId = SimplePreference.getStringPreference(getActivity(), "USER_INFO", "USER_ID", "Guest");

                markets = HttpClient.getWriterTransactionCompleted(new OkHttpClient(),userId);
                ArrayList<MarketVO> markets2 = HttpClient.getBuyerTransactionCompleted(new OkHttpClient(),userId);
                markets.addAll(markets2);

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
                                adapter = new TransactionCompleteAdapter(getActivity(),markets);
                                recyclerView.setAdapter(adapter);

                                //                       marketAdapter.notifyDataSetChanged();
                            }
                        });


                    }
                });
            }
        }).start();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.market_row, container, false);
        View v = inflater.inflate(R.layout.fragment_market_contents, container, false);
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return v;
    }
    public class TransactionCompleteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
        Context context;
        ArrayList<MarketVO> arrayList;


        public TransactionCompleteAdapter(Context context,ArrayList<MarketVO> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


            View v = LayoutInflater.from(context).inflate(R.layout.market_row, parent, false);

            //       recyclerView = v.findViewById(R.id.recyclerView);
            //       recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            return new TransactionCompleteViewHolder(v);
        }
        private int dpToPx(Context context, int dp) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
        {
            TransactionCompleteViewHolder viewHolder = (TransactionCompleteViewHolder) holder;



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

            int nCarrot = data.getTransactionPrice();//int)data.getPrice() / 120;


            viewHolder.carrot.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams params = viewHolder.carrot.getLayoutParams();
            params.width = dpToPx(getActivity(),36);
            params.height = dpToPx(getActivity(),36);

            String strPrice =  String.format("%,d", 120 * data.getTransactionPrice());
            viewHolder.price.setText(String.valueOf(nCarrot) + "개" +" (" + strPrice +"원)");


            String strCover = CommonUtils.strDefaultUrl + "images/" + data.getCover();

            Glide.with(context)
                    .asBitmap() // some .jpeg files are actually gif
                    .placeholder(R.drawable.no_poster)
                    .load(strCover)
                    .into(viewHolder.cover);
        }



        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }
    public String covertTimeToText(String dataDate) {

        String convTime = null;

        String prefix = "";
        String suffix = "전";

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date pasTime = dateFormat.parse(dataDate);

            Date nowTime = new Date();

            long dateDiff = nowTime.getTime() - pasTime.getTime();

            long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
            long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
            long hour   = TimeUnit.MILLISECONDS.toHours(dateDiff);
            long day  = TimeUnit.MILLISECONDS.toDays(dateDiff);

            if (second < 60) {
                convTime = second + "초 " + suffix;
            } else if (minute < 60) {
                convTime = minute + "분 "+suffix;
            } else if (hour < 24) {
                convTime = hour + " Hours "+suffix;
            } else if (day >= 7) {
                if (day > 360) {
                    convTime = (day / 360) + "년 " + suffix;
                } else if (day > 30) {
                    convTime = (day / 30) + "달 " + suffix;
                } else {
                    convTime = (day / 7) + "주 " + suffix;
                }
            } else if (day < 7) {
                convTime = day+"일 "+suffix;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            // Log.e("ConvTimeE", e.getMessage());
        }

        return convTime;
    }

    public class TransactionCompleteViewHolder extends RecyclerView.ViewHolder  {

        TextView title;
        TextView sypnopsis;
        TextView tag;
        TextView copyright0;
        TextView copyright1;
        TextView career;
        ImageView cover;
        TextView price;
        ImageView carrot;


        public TransactionCompleteViewHolder(@NonNull View itemView) {

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



        }



    }
}