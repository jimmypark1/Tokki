package com.Whowant.Tokki.UI.Activity.Market;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Carrot.UsedCarrotListActivity;
import com.Whowant.Tokki.UI.Activity.Rank.RankActivity;
import com.Whowant.Tokki.UI.Activity.Work.WorkMainActivity;
import com.Whowant.Tokki.UI.Fragment.Main.KeepSubFragment;
import com.Whowant.Tokki.UI.Fragment.MyPage.MyPageFeedFragment;
import com.Whowant.Tokki.UI.TypeOnClickListener;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.ItemClickSupport;
import com.Whowant.Tokki.VO.CarrotVO;
import com.Whowant.Tokki.VO.MarketVO;
import com.Whowant.Tokki.VO.MyPageFeedVo;
import com.Whowant.Tokki.VO.WorkVO;
import com.Whowant.Tokki.VO.WriterVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MarketContentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MarketContentsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private MarketAdapter marketAdapter;
    private ArrayList<MarketVO> markets;

    RecyclerView recyclerView;

    public int position = 0;
    public int topPosition = 0;

    TextView desc;

    OkHttpClient sortHttp;
    OkHttpClient allHttp;

    Boolean bBlock = false;


    public MarketContentsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MarketContentsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MarketContentsFragment newInstance(String param1, String param2) {
        MarketContentsFragment fragment = new MarketContentsFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_market_contents, container, false);
        recyclerView = v.findViewById(R.id.recyclerView);
        desc = v.findViewById(R.id.desc);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


     //   getMarketData();

        return v;
    }

    public void Update(int pos, String content)
    {
        if(topPosition == 0)
        {
            if(pos > 0)
            {
                bBlock = true;
                getMarketDataSort(content);
            }
            else
            {
                bBlock = true;
                getMarketData();
            }
        }


    }
    @Override
    public void onResume() {
        super.onResume();
        if(topPosition == 0)
        {
            if(position == 0)
            {
                getMarketData();
            }
        }


    }
    private void getMarketDataSort(String field) {
//        CommonUtils.showProgressDialog(getActivity(), "???????????? ???????????? ???????????? ????????????. ????????? ??????????????????.");

        if(markets != null) {
            markets.clear();
            marketAdapter.notifyDataSetChanged();
        }


        if(sortHttp != null)
        {
            for (Call call : sortHttp.dispatcher().queuedCalls()) {
                if (call.request().tag().equals("SORT_CONTENT"))
                    call.cancel();
            }
            for (Call call : sortHttp.dispatcher().runningCalls()) {
                if (call.request().tag().equals("SORT_CONTENT"))
                    call.cancel();
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                //SORT_CONTENT
                sortHttp = new OkHttpClient();
                markets = HttpClient.getWorksSorByContentOnMarket(sortHttp,field);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //       CommonUtils.hideProgressDialog();
                        if(markets.size() == 0)
                        {
                            desc.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            desc.setVisibility(View.INVISIBLE);

                        }


                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(markets == null) {
                                    Toast.makeText(getActivity(), "???????????? ????????? ???????????? ????????????.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                marketAdapter = new MarketAdapter(getActivity(),markets);
                                recyclerView.setAdapter(marketAdapter);

                                //                       marketAdapter.notifyDataSetChanged();
                            }
                        });


                    }
                });
            }
        }).start();
    }
    private void getMarketData() {
//        CommonUtils.showProgressDialog(getActivity(), "???????????? ???????????? ???????????? ????????????. ????????? ??????????????????.");

        if(markets != null)
        {
            markets.clear();
            marketAdapter.notifyDataSetChanged();

        }

        if(allHttp != null)
        {
            for (Call call : allHttp.dispatcher().queuedCalls()) {
                if (call.request().tag().equals("SORT_ALL"))
                    call.cancel();
            }
            for (Call call : allHttp.dispatcher().runningCalls()) {
                if (call.request().tag().equals("SORT_ALL"))
                    call.cancel();
            }
        }


        new Thread(new Runnable() {
            @Override
            public void run() {

                allHttp = new OkHttpClient();
                markets = HttpClient.getWorksOnMarket(allHttp);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                 //       CommonUtils.hideProgressDialog();

                        if(markets.size() == 0)
                        {
                            desc.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            desc.setVisibility(View.INVISIBLE);

                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                  if(markets == null) {
                                    Toast.makeText(getActivity(), "???????????? ????????? ???????????? ????????????.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                marketAdapter = new MarketAdapter(getActivity(),markets);
                                recyclerView.setAdapter(marketAdapter);

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
    public class MarketAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
        Context context;
        ArrayList<MarketVO> arrayList;

        Boolean bBlock = false;

        public MarketAdapter(Context context,ArrayList<MarketVO> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


            View v = LayoutInflater.from(context).inflate(R.layout.market_row, parent, false);

            if(arrayList.size() == 0)
            {

            }
            else
            {

            }

            return new MarketViewHolder(v);
        }

        private int dpToPx(Context context, int dp) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
        }
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
        {
            MarketViewHolder viewHolder = (MarketViewHolder) holder;



            if(bBlock)
            {
                bBlock = false;
                return;
            }
            MarketVO data = arrayList.get(position);


            viewHolder.title.setText(data.getTitle());
            viewHolder.sypnopsis.setText(data.getSypnopsis());

            int type = data.getWorkType();
//            viewHolder.tag.setText(data.getTag());

            String name = data.getName();
            if(type == 0)
            {
                viewHolder.tag.setText(name + " | " + "????????????");

            }
            else if(type == 1)
            {
                viewHolder.tag.setText(name + " | " + "?????????");

            }
            else if(type == 3)
            {
                viewHolder.tag.setText(name + " | " + "?????????");

            }


            viewHolder.copyright0.setText("????????? : " + data.getCopyright0());
            if(data.getStrField()!= null && data.getStrField().length() > 0)
                viewHolder.copyright1.setText("?????? :" + data.getStrField());
            else
                viewHolder.copyright1.setText("");

            viewHolder.career.setText(data.getCareer());
            /*
                  carrotNum.text = String(Int( nPrice! / 120)) + "???"
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
                viewHolder.price.setText(String.valueOf(nCarrot) + "???" +" (" + strPrice +"???)");
            }
            else
            {
                ViewGroup.LayoutParams params = viewHolder.carrot.getLayoutParams();
                params.width = 0;

                viewHolder.carrot.setVisibility(View.INVISIBLE);


                String strPrice =  String.format("%,d", data.getPrice());

                viewHolder.price.setText(strPrice +"???");

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


    public class MarketViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        TextView sypnopsis;
        TextView tag;
        TextView copyright0;
        TextView copyright1;
        TextView career;
        ImageView cover;
        TextView price;
        ImageView carrot;

        ItemClickListener itemClickListener;


        public MarketViewHolder(@NonNull View itemView) {

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