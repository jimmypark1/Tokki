package com.Whowant.Tokki.UI.Fragment.Main;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.PurchaseVO;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CoinPurchaseFragment extends Fragment {//implements PurchasesUpdatedListener {
    /*
    private BillingClient billingClient;
    private ArrayList<HashMap<String, String>> productList;
    private List<SkuDetails> skuDetailList;
    private List<PurchaseVO> purchaseList;
    private ArrayList<String> purchasedTokenList;
    private ArrayList<String> noRentTokenList;
    private ListView listView;
    private CPurchaseArrayAdapter aa;

    public static Fragment newInstance() {
        CoinPurchaseFragment fragment = new CoinPurchaseFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View inflaterView = inflater.inflate(R.layout.coin_list_fragment, container, false);

        listView = inflaterView.findViewById(R.id.listView);
        productList = new ArrayList<>();
        skuDetailList = new ArrayList<>();
        purchasedTokenList = new ArrayList<>();
        noRentTokenList = new ArrayList<>();

        billingClient = BillingClient.newBuilder(getActivity()).setListener(this).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    getSKUList();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(getActivity(), "결제서비스 연동 실패", Toast.LENGTH_LONG).show();
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });


        return inflaterView;
    }

    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
        Log.d("responce", "reponseCode=" + responseCode);

        if (responseCode == 0) {             // 결제 성공
            for (Purchase purchase : purchases) {
                billingClient.consumeAsync(purchase.getPurchaseToken(), new ConsumeResponseListener() {
                    @Override
                    public void onConsumeResponse(int responseCode, String purchaseToken) {

                    }
                });

//                sendPurchaseResult(purchase.getPurchaseToken());
            }
        } else {
            Toast.makeText(getActivity(), "결제가 실패했습니다. 잠시 후 다시 이용해 주세요.", Toast.LENGTH_LONG).show();
//            sendPurchaseResult("test_token");
        }
    }

    private void getSKUList() {
        List<String> skuList = new ArrayList<>();
        skuList.add("coin_10");
        skuList.add("coin_30");
        skuList.add("coin_50");
        skuList.add("coin_100");
        skuList.add("coin_300");
        skuList.add("coin_500");
        skuList.add("coin_1000");

        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        billingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
                skuDetailList = new ArrayList<>(skuDetailsList);
                CommonUtils.hideProgressDialog();

                if(skuDetailList.size() == 0) {
                    purchaseList = new ArrayList<>();

                    for(int i = 0 ; i < 7 ; i++) {
                        PurchaseVO vo = new PurchaseVO();

                        switch(i) {
                            case 0:
                                vo.setnPrice(1000);
                                vo.setStrPurchaseName("10 코인");
                                break;
                            case 1:
                                vo.setnPrice(3000);
                                vo.setStrPurchaseName("30 코인");
                                break;
                            case 2:
                                vo.setnPrice(5000);
                                vo.setStrPurchaseName("50 코인");
                                break;
                            case 3:
                                vo.setnPrice(10000);
                                vo.setStrPurchaseName("100 코인");
                                break;
                            case 4:
                                vo.setnPrice(30000);
                                vo.setStrPurchaseName("300 코인");
                                break;
                            case 5:
                                vo.setnPrice(50000);
                                vo.setStrPurchaseName("500 코인");
                                break;
                            case 6:
                                vo.setnPrice(10000);
                                vo.setStrPurchaseName("1000 코인");
                                break;
                        }

                        purchaseList.add(vo);
                    }

                    aa = new CPurchaseArrayAdapter(getActivity(), R.layout.purchase_row, purchaseList);
                    listView.setAdapter(aa);
                } else {
                    aa = new CPurchaseArrayAdapter(getActivity(), R.layout.purchase_row, skuDetailList);
                    listView.setAdapter(aa);
                }

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Toast.makeText(getActivity(), "구글에서 결제 승인 절차 중입니다.", Toast.LENGTH_SHORT).show();
                    }
                });

                billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP,
                        new PurchaseHistoryResponseListener() {
                            @Override
                            public void onPurchaseHistoryResponse(@BillingClient.BillingResponse int responseCode, List<Purchase> purchasesList) {
                                if (responseCode == BillingClient.BillingResponse.OK && purchasesList != null) {
                                    for (Purchase purchase : purchasesList) {
                                        purchasedTokenList.add(purchase.getPurchaseToken());

                                        billingClient.consumeAsync(purchase.getPurchaseToken(), new ConsumeResponseListener() {
                                            @Override
                                            public void onConsumeResponse(int responseCode, String purchaseToken) {

                                            }
                                        });
                                    }
                                }
                            }
                        });
            }
        });
    }

    private void ParseTokens(InputStream is) throws XmlPullParserException, IOException {
        noRentTokenList.clear();
        String parserName = null;
        String value;

        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(new InputStreamReader(is));

        for (int e = parser.getEventType(); e != XmlPullParser.END_DOCUMENT; e = parser.next()) {
            switch (e) {
                case XmlPullParser.START_TAG:
                    parserName = parser.getName();
                    break;
                case XmlPullParser.TEXT:
                    value = parser.getText();

                    value = value.replaceAll("\\\n", "");
                    value = value.replaceAll("\\\r", "");
                    value = value.replaceAll("\\\t", "");

                    if (value == null || value.length() == 0 || value.equalsIgnoreCase("null"))
                        break;

                    if(parserName.equals("PURCHASE_TOKEN"))
                        noRentTokenList.add(value);

                    break;
                case XmlPullParser.END_TAG: {
                    parserName = parser.getName();
                }
                break;
            }
        }
    }

    public class CPurchaseArrayAdapter extends ArrayAdapter<Object>
    {
        private LayoutInflater mLiInflater;

        CPurchaseArrayAdapter(Context context, int layout, List titles)
        {
            super(context, layout, titles);
            mLiInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            if(convertView == null)
                convertView = mLiInflater.inflate(R.layout.purchase_row, parent, false);

            TextView coinAmmountView = convertView.findViewById(R.id.coinAmmountView);
            TextView purchasePriceView = convertView.findViewById(R.id.purchasePriceView);

            if(skuDetailList.size() == 0) {
                PurchaseVO vo = purchaseList.get(position);

                int nCoin = vo.getnPrice();

                if(nCoin == 1000) {
                    coinAmmountView.setText("10 코인");
                    purchasePriceView.setText("1,000원");
                } else if(nCoin == 3000) {
                    coinAmmountView.setText("30 코인");
                    purchasePriceView.setText("3,000원");
                } else if(nCoin == 5000) {
                    coinAmmountView.setText("50 코인");
                    purchasePriceView.setText("5,000원");
                } else if(nCoin == 10000) {
                    coinAmmountView.setText("100 코인");
                    purchasePriceView.setText("10,000원");
                } else if(nCoin == 30000) {
                    coinAmmountView.setText("300 코인");
                    purchasePriceView.setText("30,000원");
                } else if(nCoin == 50000) {
                    coinAmmountView.setText("500 코인");
                    purchasePriceView.setText("50,000원");
                } else if(nCoin == 100000) {
                    coinAmmountView.setText("1000 코인");
                    purchasePriceView.setText("100,000원");
                }
            } else {
                SkuDetails skuDetails = skuDetailList.get(position);
                String strSKU = skuDetails.getSku();

                if(strSKU.equals("coin_10")) {
                    coinAmmountView.setText("10 코인");
                    purchasePriceView.setText("1,000원");
                } else if(strSKU.equals("coin_30")) {
                    coinAmmountView.setText("30 코인");
                    purchasePriceView.setText("3,000원");
                } else if(strSKU.equals("coin_50")) {
                    coinAmmountView.setText("50 코인");
                    purchasePriceView.setText("5,000원");
                } else if(strSKU.equals("coin_100")) {
                    coinAmmountView.setText("100 코인");
                    purchasePriceView.setText("10,000원");
                } else if(strSKU.equals("coin_300")) {
                    coinAmmountView.setText("300 코인");
                    purchasePriceView.setText("30,000원");
                } else if(strSKU.equals("coin_500")) {
                    coinAmmountView.setText("500 코인");
                    purchasePriceView.setText("50,000원");
                } else if(strSKU.equals("coin_1000")) {
                    coinAmmountView.setText("1000 코인");
                    purchasePriceView.setText("100,000원");
                }
            }

            return convertView;
        }
    }

     */
}
