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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Main.MainActivity;
import com.Whowant.Tokki.UI.Fragment.Main.CoinPurchaseLogFragment;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.CarrotItemVO;
import com.Whowant.Tokki.VO.CarrotVO;
import com.Whowant.Tokki.VO.MarketMsg;
import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.android.billingclient.api.BillingClient.SkuType.INAPP;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CarrotBuyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CarrotBuyFragment extends Fragment implements PurchasesUpdatedListener {

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
    BillingClient billingClient;

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

        billingClient = BillingClient.newBuilder(getActivity())
                .enablePendingPurchases()
                .setListener(this)
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult){
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK)
                {
                    Purchase.PurchasesResult queryPurchase = billingClient.queryPurchases(INAPP);
                    List<Purchase> queryPurchases = queryPurchase.getPurchasesList();
                    if(queryPurchases!=null && queryPurchases.size()>0){
                        handlePurchases(queryPurchases);
                    }
                    //if purchase list is empty that means item is not purchased
                    //Or purchase is refunded or canceled
                    else{
                       // savePurchaseValueToPref(false);
                    }

                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(getActivity(), "결제서비스 연동 실패", Toast.LENGTH_LONG).show();
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }


        });
        return v;


    }
    private void initiatePurchase() {
        List<String> skuList = new ArrayList<>();
        skuList.add("carrot_10");
   //     skuList.add("carrot_45");
   //     skuList.add("carrot_120");
   //     skuList.add("carrot_500");
   //     skuList.add("carrot_2000");

        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(INAPP);
        billingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult,
                                                     List<SkuDetails> skuDetailsList) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            if (skuDetailsList != null && skuDetailsList.size() > 0) {
                                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                        .setSkuDetails(skuDetailsList.get(0))
                                        .build();
                                billingClient.launchBillingFlow(getActivity(), flowParams);
                            }
                            else{
                                //try to add item/product id "purchase" inside managed product in google play console
                                Toast.makeText(getApplicationContext(),"Purchase Item not Found",Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    " Error "+billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    @Override
    public void onResume() {
        super.onResume();
        initCarrots();

    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
        // To be implemented in a later section.

        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            handlePurchases(purchases);
        }
        //if item already purchased then check and reflect changes
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            Purchase.PurchasesResult queryAlreadyPurchasesResult = billingClient.queryPurchases(INAPP);
            List<Purchase> alreadyPurchases = queryAlreadyPurchasesResult.getPurchasesList();
            if(alreadyPurchases!=null){
                handlePurchases(alreadyPurchases);
            }
        }
        //if purchase cancelled
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(getApplicationContext(),"Purchase Canceled",Toast.LENGTH_SHORT).show();
        }
        // Handle any other error msgs
        else {
            Toast.makeText(getApplicationContext(),"Error "+billingResult.getDebugMessage(),Toast.LENGTH_SHORT).show();
        }

    }
    void handlePurchases(List<Purchase>  purchases) {
        for(Purchase purchase:purchases) {
            //if item is purchased
           // if (PRODUCT_ID.equals(purchase.getSku()) && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED)
            if ( purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED)
            {
                /*
                if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
                    // Invalid purchase
                    // show error to user
                    Toast.makeText(getApplicationContext(), "Error : Invalid Purchase", Toast.LENGTH_SHORT).show();
                    return;
                }

                 */
                // else purchase is valid
                //if item is purchased and not acknowledged
                if (!purchase.isAcknowledged()) {
                    AcknowledgePurchaseParams acknowledgePurchaseParams =
                            AcknowledgePurchaseParams.newBuilder()
                                    .setPurchaseToken(purchase.getPurchaseToken())
                                    .build();
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams, ackPurchase);
                }
                //else item is purchased and also acknowledged
                else {
                    // Grant entitlement to the user on item purchase
                    // restart activity
                    /*
                    if(!getPurchaseValueFromPref()){
                        savePurchaseValueToPref(true);
                        Toast.makeText(getApplicationContext(), "Item Purchased", Toast.LENGTH_SHORT).show();
                        this.recreate();
                    }

                     */
                }
            }
            //if purchase is pending
            else if(  purchase.getPurchaseState() == Purchase.PurchaseState.PENDING)
            {
                Toast.makeText(getApplicationContext(),
                        "Purchase is Pending. Please complete Transaction", Toast.LENGTH_SHORT).show();
            }
            //if purchase is unknown
            else if( purchase.getPurchaseState() == Purchase.PurchaseState.UNSPECIFIED_STATE)
            {
               // savePurchaseValueToPref(false);
              //  purchaseStatus.setText("Purchase Status : Not Purchased");
              //  purchaseButton.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Purchase Status Unknown", Toast.LENGTH_SHORT).show();
            }
        }
    }

    AcknowledgePurchaseResponseListener ackPurchase = new AcknowledgePurchaseResponseListener() {
        @Override
        public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
            if(billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK){
                //if purchase is acknowledged
                // Grant entitlement to the user. and restart activity
               // savePurchaseValueToPref(true);
                Toast.makeText(getApplicationContext(), "Item Purchased", Toast.LENGTH_SHORT).show();
                getActivity().recreate();
            }
        }
    };

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

            initiatePurchase();
        }



    }

}