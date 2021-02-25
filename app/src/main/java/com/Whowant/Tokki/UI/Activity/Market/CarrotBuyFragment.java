package com.Whowant.Tokki.UI.Activity.Market;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.CarrotItemVO;
import com.Whowant.Tokki.VO.CarrotVO;
import com.Whowant.Tokki.VO.MarketMsg;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CarrotBuyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CarrotBuyFragment extends Fragment  {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView recyclerView;
    private ArrayList<CarrotItemVO> caroots = new ArrayList<CarrotItemVO>();


    CarrotAdapter adapter;


    void initCarrots()
    {
        caroots.clear();
        CarrotItemVO data0 = new CarrotItemVO();
        data0.setDesc("당근 10개");
        data0.setPrice(1250);
        data0.setProductId("carrot_10");
        caroots.add(data0);

        CarrotItemVO data1 = new CarrotItemVO();
        data1.setDesc("당근 45개");
        data1.setPrice(5000);
        data1.setProductId("carrot_45");
        caroots.add(data1);

        CarrotItemVO data2 = new CarrotItemVO();
        data2.setDesc("당근 120개");
        data2.setPrice(12500);
        data2.setProductId("carrot_120");
        caroots.add(data2);


        CarrotItemVO data3 = new CarrotItemVO();
        data3.setDesc("당근 500개");
        data3.setPrice(50000);
        data3.setProductId("carrot_500");
        caroots.add(data3);

        CarrotItemVO data4 = new CarrotItemVO();
        data4.setDesc("당근 2000개");
        data4.setPrice(125000);
        data4.setProductId("carrot_2000");
        caroots.add(data4);

        adapter = new CarrotAdapter(getActivity(),caroots);
        recyclerView.setAdapter(adapter);

    }

    /*
    private PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            // To be implemented in a later section.
        }
    };

    private BillingClient billingClient = BillingClient.newBuilder(getActivity())
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build();


     */
    public CarrotBuyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CarrotBuyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CarrotBuyFragment newInstance(String param1, String param2) {
        CarrotBuyFragment fragment = new CarrotBuyFragment();
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
        View v = inflater.inflate(R.layout.fragment_carrot_buy, container, false);
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return v;


    }
    @Override
    public void onResume() {
        super.onResume();
        initCarrots();

    }


    void handlePurchase(Purchase purchase) {
        // Purchase retrieved from BillingClient#queryPurchases or your PurchasesUpdatedListener.
        /*
        Purchase purchase = ...;

        // Verify the purchase.
        // Ensure entitlement was not already granted for this purchaseToken.
        // Grant entitlement to the user.

        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

        ConsumeResponseListener listener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // Handle the success of the consume operation.
                }
            }
        };

        billingClient.consumeAsync(consumeParams, listener);

         */
    }

    public class CarrotAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
        Context context;
        ArrayList<CarrotItemVO> arrayList;


        public CarrotAdapter(Context context,ArrayList<CarrotItemVO> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {



            View v = LayoutInflater.from(context).inflate(R.layout.iap_item, parent, false);

            //       recyclerView = v.findViewById(R.id.recyclerView);
            //       recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            return new CarrotViewHolder(v);
        }


        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
        {
            CarrotViewHolder viewHolder = (CarrotViewHolder) holder;



            CarrotItemVO data = arrayList.get(position);
            int nPrice = data.getPrice();

            viewHolder.desc.setText(data.getDesc());

            viewHolder.price.setText(String.valueOf(data.getPrice()) +"원");


        }



        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }
    public class CarrotViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView desc;
        TextView price;

        MarketContentsFragment.ItemClickListener itemClickListener;


        public CarrotViewHolder(@NonNull View itemView) {

            super(itemView);
            desc = itemView.findViewById(R.id.desc);
            price = itemView.findViewById(R.id.price);


            itemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View v){
            // this.itemClickListener.onItemClickListener(v, getLayoutPosition());
            int pos = getLayoutPosition();

        }



    }

}