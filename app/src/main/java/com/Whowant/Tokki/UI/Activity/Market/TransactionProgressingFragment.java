package com.Whowant.Tokki.UI.Activity.Market;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Fragment.Friend.FriendSelectActivity;
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
 * Use the {@link TransactionProgressingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransactionProgressingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView recyclerView;

    TextView desc;
    private TransactionProgressingAdapter adapter;
    private ArrayList<MarketMsg> marketMsgs;
    public TransactionProgressingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TransactionProgressingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TransactionProgressingFragment newInstance(String param1, String param2) {
        TransactionProgressingFragment fragment = new TransactionProgressingFragment();
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

        View v = inflater.inflate(R.layout.fragment_transaction_progressing, container, false);
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        desc = v.findViewById(R.id.desc);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getTrading();

    }

    private void getTrading() {
//        CommonUtils.showProgressDialog(getActivity(), "서버에서 데이터를 가져오고 있습니다. 잠시만 기다려주세요.");

        if(marketMsgs != null)
            marketMsgs.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String userId = SimplePreference.getStringPreference(getActivity(), "USER_INFO", "USER_ID", "Guest");

            //    userId = "1511869881";
            //    boolean isWriter =  HttpClient.isWriter(new OkHttpClient(),userId,"");
/*
                if(isWriter)
                {
                    //let userId = UserDefaults.standard.string(forKey: "USER_ID")


                    marketMsgs = HttpClient.getTrading(new OkHttpClient(),userId);

                }
                else
                {
                    marketMsgs = HttpClient.getTrading2(new OkHttpClient(),userId);

                }

 */
                marketMsgs = HttpClient.getTrading(new OkHttpClient(),userId);


                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //       CommonUtils.hideProgressDialog();
                        if(marketMsgs.size() == 0)
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
                                if(marketMsgs == null) {
                                    Toast.makeText(getActivity(), "서버와의 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                adapter = new TransactionProgressingAdapter(getActivity(),marketMsgs);
                                recyclerView.setAdapter(adapter);

                                //                       marketAdapter.notifyDataSetChanged();
                            }
                        });

//                        recyclerView.notifyAll();

                        //  recyclerView.setAdapter(aa);
                        // adapter = new MyPageFeedFragment.MyPageFeedAdapter(getContext(), mArrayList);
                        /*
                        aa = new MarketAdapter(getActivity());

                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                        recyclerView.setAdapter(aa);
                        ItemClickSupport.addTo(recyclerView).setItemClickListener(new ItemClickSupport.OnItemClickListener() {
                            @Override
                            public void onItemClick(RecyclerView parent, View view, int position, long id) {

                            }
                        });

                         */

                    }
                });
            }
        }).start();
    }
    public class TransactionProgressingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
        Context context;
        ArrayList<MarketMsg> arrayList;


        public TransactionProgressingAdapter(Context context,ArrayList<MarketMsg> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


            View v = LayoutInflater.from(context).inflate(R.layout.trans_progressing_row, parent, false);

     //       recyclerView = v.findViewById(R.id.recyclerView);
     //       recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            return new TransactionProgressingViewHolder(v);
        }


        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
        {
            TransactionProgressingViewHolder viewHolder = (TransactionProgressingViewHolder) holder;



            MarketMsg data = arrayList.get(position);


            String date = covertTimeToText(data.getDate());
            viewHolder.title.setText(data.getTitle());
            viewHolder.msg.setText(data.getMsg());
            viewHolder.name.setText(data.getName());
            viewHolder.date.setText(date);


            String strProfile = "";
            if (!data.getProfile().startsWith("http"))
            {
                strProfile = CommonUtils.strDefaultUrl + "images/" + data.getProfile();

            }
            else
            {
                strProfile =  data.getProfile();

            }


            String strCover = CommonUtils.strDefaultUrl + "images/" + data.getCover();

            Glide.with(context)
                    .asBitmap() // some .jpeg files are actually gif
                    .placeholder(R.drawable.no_poster)
                    .load(strCover)
                    .into(viewHolder.cover);

            Glide.with(context)
                    .asBitmap() // some .jpeg files are actually gif
                    .placeholder(R.drawable.user_icon)
                    .load(strProfile)
                    .apply(new RequestOptions().circleCrop())
                    .into(viewHolder.profile);
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

    public class TransactionProgressingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        ImageView profile;
        TextView tag;
        TextView name;
        TextView date;
        TextView msg;
        ImageView cover;
        MarketContentsFragment.ItemClickListener itemClickListener;


        public TransactionProgressingViewHolder(@NonNull View itemView) {

            super(itemView);
            title = itemView.findViewById(R.id.titleView);
            profile = itemView.findViewById(R.id.senderImg);
            tag = itemView.findViewById(R.id.tag);
            name = itemView.findViewById(R.id.name);
            cover= itemView.findViewById(R.id.coverImgView);
            msg= itemView.findViewById(R.id.msgFrame);
            date= itemView.findViewById(R.id.date);

            itemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View v){
            // this.itemClickListener.onItemClickListener(v, getLayoutPosition());
            int pos = getLayoutPosition();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String userId = SimplePreference.getStringPreference(getActivity(), "USER_INFO", "USER_ID", "Guest");

                    //    userId = "1511869881";
                    MarketMsg data = marketMsgs.get(pos);

                    if(data.getWriterId().contains(userId) == true)
                    {
                        Intent intent = new Intent(getActivity(), MessageDetailActivity.class);
                        intent.putExtra("RECEIVER_ID", data.getSenderID());
                        intent.putExtra("RECEIVER_NAME", data.getName());
                        intent.putExtra("WRITER_ID", data.getWriterId());
                        intent.putExtra("WORK_ID", data.getWorkId());
                        intent.putExtra("FIELD", data.getField());

                        intent.putExtra("WORK_TITLE",data.getTitle());
                        intent.putExtra("THREAD_ID", Integer.parseInt( data.getThreadID()));
                        intent.putExtra("MSG_TYPE", 1);

                        //

                        startActivity(intent);
                    }
                    else
                    {
                        Intent intent = new Intent(getActivity(), MessageDetailActivity.class);
                        intent.putExtra("RECEIVER_ID", data.getRecvID());
                        intent.putExtra("RECEIVER_NAME", data.getRecvname());
                        intent.putExtra("WRITER_ID", data.getWriterId());
                        intent.putExtra("WORK_ID", data.getWorkId());
                        intent.putExtra("FIELD", data.getField());

                        intent.putExtra("WORK_TITLE",data.getTitle());
                        intent.putExtra("THREAD_ID", Integer.parseInt( data.getThreadID()));
                        intent.putExtra("MSG_TYPE", 1);

                        startActivity(intent);
                    }



                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                        }
                    });
                }
            }).start();



        }



    }
}