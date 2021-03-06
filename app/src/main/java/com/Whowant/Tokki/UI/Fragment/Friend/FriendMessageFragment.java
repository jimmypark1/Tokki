package com.Whowant.Tokki.UI.Fragment.Friend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.MessageThreadVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class FriendMessageFragment extends Fragment {
    private RecyclerView recyclerView;
    private FriendMessageAdapter adapter;
    private ArrayList<MessageThreadVO> messageList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friend_message, container, false);

        recyclerView = v.findViewById(R.id.recyclerView);
        adapter = new FriendMessageAdapter(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        RelativeLayout floatingBtn = v.findViewById(R.id.floatingBtn);
        floatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), FriendSelectActivity.class));
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getMessageData();
    }

    private void getMessageData() {
        SharedPreferences pref = getActivity().getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
        messageList.clear();

       // CommonUtils.showProgressDialog(getActivity(), "????????? ??????????????????.");
        new Thread(new Runnable() {
            @Override
            public void run() {
                messageList = HttpClient.getMessageThreadList(new OkHttpClient(), pref.getString("USER_ID", "Guest"));

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    //    CommonUtils.hideProgressDialog();
                        if(messageList == null) {
                            Toast.makeText(getActivity(), "???????????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Collections.sort(messageList, new Comparator<MessageThreadVO>() {
                            public int compare(MessageThreadVO o1, MessageThreadVO o2) {
                                return o1.getCreatedDate().compareTo(o2.getCreatedDate())*(-1);
                            }
                        });
                        adapter.setData(messageList);
                    }
                });
            }
        }).start();
    }
    public String covertTimeToText(String dataDate) {

        String convTime = null;

        String prefix = "";
        String suffix = "???";

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date pasTime = dateFormat.parse(dataDate);

            Date nowTime = new Date();

            long dateDiff = nowTime.getTime() - pasTime.getTime();

            long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
            long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
            long hour   = TimeUnit.MILLISECONDS.toHours(dateDiff);
            long day  = TimeUnit.MILLISECONDS.toDays(dateDiff);

            /*
            if (second < 60) {
                convTime = second + "??? " + suffix;
            } else if (minute < 60) {
                convTime = minute + "??? "+suffix;
            } else if (hour < 24) {
                convTime = hour + "?????? "+suffix;
            } else if (day == 7) {
                convTime = "1??? ???";


            }
            */
            if (day == 0) {

                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                convTime = sdf.format(pasTime);

            }
            if (day >= 1 && day<=6 ) {

              //  SimpleDateFormat sdf = new SimpleDateFormat("M??? d???");
                convTime =  String.valueOf(day) + "??? ???";

            }
            else if (day >=7) {
                if(day <= 360)
                {
                    SimpleDateFormat sdf = new SimpleDateFormat("M??? d???");
                    convTime = sdf.format(pasTime);

                }
                else
                {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy??? M??? d???");
                    convTime = sdf.format(pasTime);

                }

                /*
                if (day > 360) {
                    convTime = (day / 360) + "??? " + suffix;
                } else if (day > 30) {
                    convTime = (day / 30) + "??? " + suffix;
                } else {
                    convTime = (day / 7) + "??? " + suffix;
                }

                 */
            }


        } catch (ParseException e) {
            e.printStackTrace();
            // Log.e("ConvTimeE", e.getMessage());
        }

        return convTime;
    }
    public class FriendMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;
        private ArrayList<MessageThreadVO> dataList = new ArrayList<>();

        public FriendMessageAdapter(Context context) {
            this.context = context;
        }

        public void setData(ArrayList<MessageThreadVO> list) {
            this.dataList = list;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.row_friend_message_fragment, parent, false);
            return new FriendMessageViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if(dataList.size() <= position)
                return;

            MessageThreadVO vo = dataList.get(position);
            FriendMessageViewHolder viewHolder = (FriendMessageViewHolder)holder;

            String strPhoto = vo.getUserPhoto();
            viewHolder.photoIv.setClipToOutline(true);
            if(strPhoto != null && !strPhoto.equals("null") && !strPhoto.equals("NULL") && strPhoto.length() > 0) {
                if(!strPhoto.startsWith("http"))
                    strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

                Glide.with(getActivity())
                        .asBitmap() // some .jpeg files are actually gif
                        .load(strPhoto)
                        .apply(new RequestOptions().circleCrop())
                        .into(viewHolder.photoIv);
            } else {
                Glide.with(getActivity())
                        .asBitmap() // some .jpeg files are actually gif
                        .load(R.drawable.user_icon)
                        .apply(new RequestOptions().circleCrop())
                        .into(viewHolder.photoIv);
            }

            viewHolder.nameTv.setText(vo.getUserName());

            if(vo.getLastMsg() != null && vo.getLastMsg().length() > 0 && !vo.getLastMsg().equals("null"))
                viewHolder.commentTv.setText(vo.getLastMsg());
//            else
//                viewHolder.itemView.setVisibility(View.GONE);
//                viewHolder.commentTv.setText("");

            String strDate = vo.getCreatedDate();
            String convDate = covertTimeToText(strDate);
            viewHolder.dateTv.setText(convDate);

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences pref = getActivity().getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
                    String strMyID = pref.getString("USER_ID", "Guest");
                    int nThreadID = vo.getThreadID();
                    Intent intent = new Intent(getActivity(), MessageDetailActivity.class);
                    intent.putExtra("THREAD_ID", nThreadID);
                    intent.putExtra("RECEIVER_NAME", vo.getUserName());

                    if(strMyID.equals(vo.getUserID()))
                        intent.putExtra("RECEIVER_ID", vo.getPartnerID());
                    else
                        intent.putExtra("RECEIVER_ID", vo.getUserID());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }

    public class FriendMessageViewHolder extends RecyclerView.ViewHolder {

        ImageView photoIv;
        TextView nameTv;
        TextView commentTv;
        TextView dateTv;

        public FriendMessageViewHolder(@NonNull View itemView) {
            super(itemView);

            photoIv = itemView.findViewById(R.id.iv_row_friend_message_photo);
            nameTv = itemView.findViewById(R.id.tv_row_friend_message_name);
            commentTv = itemView.findViewById(R.id.tv_row_friend_message_comment);
            dateTv = itemView.findViewById(R.id.tv_row_friend_message_date);
        }
    }
}
