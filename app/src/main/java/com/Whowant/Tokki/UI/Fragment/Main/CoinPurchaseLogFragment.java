package com.Whowant.Tokki.UI.Fragment.Main;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.BillingLogVO;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class CoinPurchaseLogFragment extends Fragment {
    private ArrayList<BillingLogVO> billingList;
    private ListView listView;
    private TextView emptyView;
    private SharedPreferences pref;
    private CPurchaseArrayAdapter aa;

    public static Fragment newInstance() {
        CoinPurchaseLogFragment fragment = new CoinPurchaseLogFragment();
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
        pref = getActivity().getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
        listView = inflaterView.findViewById(R.id.listView);
        emptyView = inflaterView.findViewById(R.id.emptyView);

        billingList = new ArrayList<>();
        getPurchaseLogData();

        return inflaterView;
    }

    private void getPurchaseLogData() {
        CommonUtils.showProgressDialog(getActivity(), "결 정보를 가져오고 있습니다.");
        billingList.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<BillingLogVO> list = HttpClient.getPurchaseLog(new OkHttpClient(), pref.getString("USER_ID", "Guest"));

                if(list != null)
                    billingList.addAll(list);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(billingList == null) {
                            Toast.makeText(getActivity(), "서버와의 연결이 실패하였습니다.", Toast.LENGTH_SHORT).show();
                            emptyView.setText("결제 내역이 없습니다.");
                            return;
                        }

                        aa = new CPurchaseArrayAdapter(getActivity(), R.layout.purchase_log_row, billingList);
                        listView.setAdapter(aa);

                        if(billingList.size() == 0) {
                            emptyView.setText("결제 내역이 없습니다.");
                        } else {
                            emptyView.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        }).start();
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
                convertView = mLiInflater.inflate(R.layout.purchase_log_row, parent, false);

            BillingLogVO vo = billingList.get(position);

            TextView purchasePriceView = convertView.findViewById(R.id.purchasePriceView);
            TextView dateView = convertView.findViewById(R.id.dateView);
            TextView coinAmmountView = convertView.findViewById(R.id.coinAmmountView);

            int nCoinPrice = vo.getnCoinPrice();
            int nPrice = nCoinPrice * 100;
            String strDate = vo.getStrPurchaseDate();

            DecimalFormat format = new DecimalFormat("###,###");//콤마
            purchasePriceView.setText(format.format(nPrice) + "원 결제");
            dateView.setText(strDate.substring(0, 10));
            coinAmmountView.setText("+ " + format.format(nCoinPrice));
            return convertView;
        }
    }
}
