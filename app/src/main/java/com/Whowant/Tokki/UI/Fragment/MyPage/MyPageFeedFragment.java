package com.Whowant.Tokki.UI.Fragment.MyPage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Market.MarketAddActivity;
import com.Whowant.Tokki.UI.Activity.Market.MarketAddEditActivity;
import com.Whowant.Tokki.UI.Activity.Market.MarketContentsFragment;
import com.Whowant.Tokki.UI.Activity.Mypage.MyPageActivity;
import com.Whowant.Tokki.UI.Activity.Work.WorkMainActivity;
import com.Whowant.Tokki.UI.Activity.Writer.WriterPageActivity;
import com.Whowant.Tokki.UI.Fragment.Main.KeepSubFragment;
import com.Whowant.Tokki.UI.TypeOnClickListener;
import com.Whowant.Tokki.UI.ViewHolder.SearchResultViewHolder;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.DeviceUtils;
import com.Whowant.Tokki.Utils.ItemClickSupport;
import com.Whowant.Tokki.Utils.SimplePreference;
import com.Whowant.Tokki.VO.MyPageFeedVo;
import com.Whowant.Tokki.VO.WorkVO;
import com.Whowant.Tokki.VO.WriterVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.Serializable;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import java.io.Serializable;



public class MyPageFeedFragment extends Fragment {

    public class CategoryData implements Serializable {
        private String title;
        private int viewType;

        public void setViewType(int viewType) {
            this.viewType = viewType;
        }

        public int getViewType() {
            return viewType;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }
    RecyclerView recyclerView;
    RecyclerView recyclerView2;
    RecyclerView recyclerView3;

    MyPageWorkCategoryAdapter adapter0;
    MyPageWorkAdapter workAdater;
    MyPageWorkAdapter workAdater2;
    MyPageWorkAdapter workAdater3;


    ArrayList<WorkVO> myArrayList = new ArrayList<>();
    ArrayList<WorkVO> readArrayList = new ArrayList<>();
    ArrayList<WorkVO> keepArrayList = new ArrayList<>();

    ArrayList<WriterVO> followArrayList = new ArrayList<>();

 //   ArrayList<MyPageFeedVo> mArrayList = new ArrayList<>();

 //   ArrayList<WorkVO> writings = new ArrayList<>();


    ArrayList<CategoryData> category = new ArrayList<>();

    public String writerId;
    public int type = 0;
    OkHttpClient keepHttp;
    OkHttpClient readHttp;
    OkHttpClient allHttp;

    LinearLayout empty;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        if(type == 0)
        {
            if (bundle != null) {
                writerId = bundle.getString("writerId");
            } else {
                writerId = SimplePreference.getStringPreference(getContext(), "USER_INFO", "USER_ID", "Guest");
            }
        }
        else
        {
            String userId = SimplePreference.getStringPreference(getContext(), "USER_INFO", "USER_ID", "Guest");
            if(userId.equals(writerId))
            {
                writerId = userId;
            }

        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_my_page_feed, container, false);

        recyclerView = v.findViewById(R.id.recyclerView);

        empty  = v.findViewById(R.id.empty);

        empty.setVisibility(View.INVISIBLE);
        CategoryData data = new CategoryData();

        data.title = "쓰고있는 작품";
        data.setViewType(0);
        category.add(data);

        CategoryData data1 = new CategoryData();

        data1.title = "읽고있는 작품";
        data1.setViewType(1);
        category.add(data1);

        CategoryData data2 = new CategoryData();

        data2.title = "찜한 작품";
        data2.setViewType(2);
        category.add(data2);


        adapter0 = new MyPageWorkCategoryAdapter(getContext(), category);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter0);

//        getAllWorkWithWriterID();



        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(adapter0 != null)
        {
            adapter0.notifyDataSetChanged();
            /*
            adapter0 = new MyPageWorkCategoryAdapter(getContext(), category);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(adapter0);

             */
        }

      //  getAllWorkWithWriterID();
       // getReadListData();
        //getKeepListData();
    }

    @Override
    public void onPause() {
        super.onPause();
        /*
        myArrayList.clear();
        readArrayList.clear();
        keepArrayList.clear();
        if(workAdater != null)
        {
            workAdater.notifyDataSetChanged();
        }
        if(workAdater2 != null)
        {
            workAdater2.notifyDataSetChanged();
        }
        if(workAdater3 != null)
        {
            workAdater3.notifyDataSetChanged();
        }

         */
    }

    public void refresh()
    {
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        ft.detach(this).attach(this).commit();

    }


    public class MyPageWorkCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Context context;
        ArrayList<CategoryData> arrayList;
        String descTmp = "";

        public MyPageWorkCategoryAdapter(Context context, ArrayList<CategoryData> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v;

            v = LayoutInflater.from(context).inflate(R.layout.row_feed, parent, false);

            CategoryViewHolder viewHoder =  new CategoryViewHolder(v);
            viewHoder.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            readArrayList.clear();


            new Thread(new Runnable() {
                @Override
                public void run() {
                 //   readHttp = new OkHttpClient();
                    myArrayList = HttpClient.GetAllWorkListWithWriterID2(new OkHttpClient(), writerId);
                    readArrayList = HttpClient.getReadWorkList2(new OkHttpClient(), writerId, "UPDATE");
                    keepArrayList = HttpClient.getKeepWorkList2(new OkHttpClient(), writerId, "UPDATE");

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(myArrayList.size() == 0 && readArrayList.size() == 0 && keepArrayList.size() == 0)
                            {
                                category.clear();
                                adapter0.notifyDataSetChanged();
                                empty.setVisibility(View.VISIBLE);

                                return;
                            }

                            if(viewType == 0)
                            {
                                 workAdater = new MyPageWorkAdapter(getContext(), myArrayList,0);
                                viewHoder.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                                viewHoder.recyclerView.setAdapter(workAdater);

                            }
                            else if(viewType == 1)
                            {
                                workAdater2 = new MyPageWorkAdapter(getContext(), readArrayList,1);
                                viewHoder.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                                viewHoder.recyclerView.setAdapter(workAdater2);

                            }
                            else
                            {
                                workAdater3 = new MyPageWorkAdapter(getContext(), keepArrayList,2);
                                viewHoder.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                                viewHoder.recyclerView.setAdapter(workAdater3);
                            }
                        }
                    });




                }
            }).start();
            return viewHoder;

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            CategoryData data = arrayList.get(position);
            CategoryViewHolder viewHolder = (CategoryViewHolder) holder;
            viewHolder.title.setText(data.getTitle());
            //   workAdater = new MyPageWorkAdapter(getContext(), myArrayList, 0);



        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return arrayList.get(position).getViewType();
        }


    }
    public class CategoryViewHolder extends RecyclerView.ViewHolder  {

        TextView title;
        TextView sypnopsis;
        TextView date;

        ImageView cover;

        RecyclerView recyclerView;
    //    MarketContentsFragment.ItemClickListener itemClickListener;


        public CategoryViewHolder(@NonNull View itemView) {

            super(itemView);
            title = itemView.findViewById(R.id.title);
            recyclerView = itemView.findViewById(R.id.row_recyclerView);


        }

    }

    public class MyPageWorkAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Context context;
        ArrayList<WorkVO> arrayList;
        String descTmp = "";


        public TextView notiTv;
        public ImageView photoIv;
        public LinearLayout rankLl;
        public TextView rankTv;
        public TextView titleTv;
        public ImageView optionIv;
        public TextView writerTv;
        public TextView heartTv;
        public TextView starTv;
        public TextView tabTv;
        public TextView synopsisTv;
        ImageView cover;
        int viewType = 0;

        public MyPageWorkAdapter(Context context, ArrayList<WorkVO> arrayList, int viewType) {
            this.context = context;
            this.arrayList = arrayList;
            this.viewType = viewType;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v;

            v = LayoutInflater.from(context).inflate(R.layout.row_search_category, parent, false);

/*
            photoIv = v.findViewById(R.id.iv_row_search_category_photo);
            titleTv = v.findViewById(R.id.tv_row_search_category_title);
            writerTv = v.findViewById(R.id.tv_row_search_category_writer);
            synopsisTv = v.findViewById(R.id.tv_row_search_category_synopsis);
            heartTv = v.findViewById(R.id.tv_row_search_category_heart);
            starTv = v.findViewById(R.id.tv_row_search_category_star);
            tabTv = v.findViewById(R.id.tv_row_search_category_tab);
            optionIv = v.findViewById(R.id.iv_row_search_category_option);
*/
            if(viewType == 0)
            {
                WorkViewHolder viewHoder = new WorkViewHolder(v);
                return viewHoder;
            }
            else if(viewType == 1)
            {
                WorkViewHolder2 viewHoder = new WorkViewHolder2(v);
                return viewHoder;
            }
            else
            {
                WorkViewHolder3 viewHoder = new WorkViewHolder3(v);
                return viewHoder;
            }

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            WorkVO item = new WorkVO();

            if(this.viewType == 0)
            {
                item = myArrayList.get(position);

            }
            else if(this.viewType == 1)
            {
                item = readArrayList.get(position);
            }
            else
            {
                item = keepArrayList.get(position);
            }
            photoIv = holder.itemView.findViewById(R.id.iv_row_search_category_photo);
            titleTv = holder.itemView.findViewById(R.id.tv_row_search_category_title);
            writerTv = holder.itemView.findViewById(R.id.tv_row_search_category_writer);
            synopsisTv = holder.itemView.findViewById(R.id.tv_row_search_category_synopsis);
            heartTv = holder.itemView.findViewById(R.id.tv_row_search_category_heart);
            starTv = holder.itemView.findViewById(R.id.tv_row_search_category_star);
            tabTv = holder.itemView.findViewById(R.id.tv_row_search_category_tab);
            optionIv = holder.itemView.findViewById(R.id.iv_row_search_category_option);

            String strImgUrl = item.getCoverFile();

            if (strImgUrl == null || strImgUrl.equals("null") || strImgUrl.equals("NULL") || strImgUrl.length() == 0) {
                Glide.with(context)
                        .asBitmap() // some .jpeg files are actually gif
                        .placeholder(R.drawable.no_poster)
                        .load(R.drawable.no_poster_vertical)
                        .into(photoIv);
            } else {
                if (!strImgUrl.startsWith("http")) {
                    strImgUrl = CommonUtils.strDefaultUrl + "images/" + strImgUrl;
                }

                Glide.with(context)
                        .asBitmap() // some .jpeg files are actually gif
                        .placeholder(R.drawable.no_poster)
                        .load(strImgUrl)
                        .into(photoIv);
            }
            titleTv.setText(item.getTitle());
            optionIv.setVisibility(View.GONE);

            String name = item.getStrWriterName();
            int type = item.getnTarget();
            String strType = "채팅소설";
            if(type == 0)
            {
                strType= "채팅소설";
            }
            else if(type == 1)
            {
                strType= "웹소설";

            }
            else if(type == 3)
            {
                strType= "스토리";

            }
            writerTv.setText("by " + name + " | " + strType);
            synopsisTv.setText(item.getSynopsis());
            heartTv.setText(CommonUtils.getPointCount(item.getnKeepcount()));
            float fStarPoint = item.getfStarPoint();

            if (fStarPoint == 0)
                starTv.setText("0");
            else
                starTv.setText(String.format("%.1f", fStarPoint));
            // viewHolder.title.setText(name);
            tabTv.setText(CommonUtils.getPointCount(item.getnHitsCount()));



        }

        @Override
        public int getItemCount() {
           // return arrayList.size();
            if(this.viewType == 0)
            {
                return myArrayList.size();

            }
            else if(this.viewType == 1)
            {
                return readArrayList.size();

            }
            else
            {
                return keepArrayList.size();

            }
        }
        @Override
        public int getItemViewType(int position) {
            return arrayList.get(position).getViewType();
        }

    }

    public class WorkViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        TextView sypnopsis;
        TextView date;

        ImageView cover;

        RecyclerView recyclerView;
        //    MarketContentsFragment.ItemClickListener itemClickListener;


        public WorkViewHolder(@NonNull View itemView) {

            super(itemView);
            title = itemView.findViewById(R.id.title);
            recyclerView = itemView.findViewById(R.id.row_recyclerView);


            itemView.setOnClickListener(this);




        }
        @Override
        public void onClick(View v){
            // this.itemClickListener.onItemClickListener(v, getLayoutPosition());

            int position = getAdapterPosition();

            WorkVO item = myArrayList.get(position);

            Intent intent = new Intent(getContext(), WorkMainActivity.class);
            intent.putExtra("WORK_ID", item.getnWorkID());
            intent.putExtra("WORK_TYPE", item.getnTarget());

            getContext().startActivity(intent);
        }



    }

    public class WorkViewHolder2 extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        TextView sypnopsis;
        TextView date;

        ImageView cover;

        RecyclerView recyclerView;
        //    MarketContentsFragment.ItemClickListener itemClickListener;


        public WorkViewHolder2(@NonNull View itemView) {

            super(itemView);
            title = itemView.findViewById(R.id.title);
            recyclerView = itemView.findViewById(R.id.row_recyclerView);


            itemView.setOnClickListener(this);




        }
        @Override
        public void onClick(View v){
            // this.itemClickListener.onItemClickListener(v, getLayoutPosition());

            int position = getAdapterPosition();

            WorkVO item = readArrayList.get(position);

            Intent intent = new Intent(getContext(), WorkMainActivity.class);
            intent.putExtra("WORK_ID", item.getnWorkID());
            intent.putExtra("WORK_TYPE", item.getnTarget());

            getContext().startActivity(intent);
        }



    }
    public class WorkViewHolder3 extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        TextView sypnopsis;
        TextView date;

        ImageView cover;

        RecyclerView recyclerView;
        //    MarketContentsFragment.ItemClickListener itemClickListener;


        public WorkViewHolder3(@NonNull View itemView) {

            super(itemView);
            title = itemView.findViewById(R.id.title);
            recyclerView = itemView.findViewById(R.id.row_recyclerView);


            itemView.setOnClickListener(this);




        }
        @Override
        public void onClick(View v){
            // this.itemClickListener.onItemClickListener(v, getLayoutPosition());

            int position = getAdapterPosition();

            WorkVO item = keepArrayList.get(position);

            Intent intent = new Intent(getContext(), WorkMainActivity.class);
            intent.putExtra("WORK_ID", item.getnWorkID());
            intent.putExtra("WORK_TYPE", item.getnTarget());

            getContext().startActivity(intent);
        }



    }

    // 내가 쓴 작품 목록
    private void getAllWorkWithWriterID() {
        if (SimplePreference.getStringPreference(getContext(), "USER_INFO", "USER_ID", "Guest").equals(writerId)) {
            MyPageFeedVo desc = new MyPageFeedVo();
            desc.setUserDesc(SimplePreference.getStringPreference(getContext(), "USER_INFO", "USER_DESC", ""));
     //       desc.setType(0);
       //     mArrayList.add(desc);
        }

/*
        if(allHttp != null)
        {
            for (Call call : allHttp.dispatcher().queuedCalls()) {
                if (call.request().tag().equals("ALL_WORK"))
                    call.cancel();
            }
            for (Call call : allHttp.dispatcher().runningCalls()) {
                if (call.request().tag().equals("ALL_WORK"))
                    call.cancel();
            }
        }
        if(readHttp != null)
        {
            for (Call call : readHttp.dispatcher().queuedCalls()) {
                if (call.request().tag().equals("READ_WORK"))
                    call.cancel();
            }
            for (Call call : readHttp.dispatcher().runningCalls()) {
                if (call.request().tag().equals("READ_WORK"))
                    call.cancel();
            }
        }
        if(keepHttp != null)
        {
            for (Call call : keepHttp.dispatcher().queuedCalls()) {
                if (call.request().tag().equals("KEEP_WORK"))
                    call.cancel();
            }
            for (Call call : keepHttp.dispatcher().runningCalls()) {
                if (call.request().tag().equals("KEEP_WORK"))
                    call.cancel();
            }
        }



 */
        myArrayList.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (getActivity() == null)
                    return;

                allHttp = new OkHttpClient();
                myArrayList = HttpClient.GetAllWorkListWithWriterID2(allHttp, writerId);

                if (myArrayList != null) {
                    if(myArrayList.size() > 0)
                    {
                      //  category.add("쓰고있는 작품");



                    }

                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (myArrayList == null) {
                            Toast.makeText(getActivity(), "서버와의 통신에 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        workAdater = new MyPageWorkAdapter(getContext(), myArrayList, 0);
                        recyclerView.setAdapter(workAdater);
                        workAdater.notifyDataSetChanged();
                      //  adapter0.notifyDataSetChanged();

                     //   getReadListData();

                        //    getMyFollowingList();
                    }
                });
            }
        }).start();
    }

    // 내가 읽은 작품 목록
    private void getReadListData() {
//        CommonUtils.showProgressDialog(getActivity(), "서버에서 데이터를 가져오고 있습니다. 잠시만 기다려주세요.");
        readArrayList.clear();


        new Thread(new Runnable() {
            @Override
            public void run() {
                readHttp = new OkHttpClient();
                readArrayList = HttpClient.getReadWorkList2(readHttp, writerId, "UPDATE");

           //     workAdater = new MyPageWorkAdapter(getContext(), readArrayList,1);
           //     viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

            //    recyclerView.setAdapter(workAdater);

            //    workAdater.notifyDataSetChanged();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        CommonUtils.hideProgressDialog();

                        if (readArrayList == null) {
                            Toast.makeText(getActivity(), "서버와의 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                      //  adapter0.notifyDataSetChanged();

                      //  getKeepListData();


                    }
                });
            }
        }).start();
    }
    private void getKeepListData() {
     //   CommonUtils.showProgressDialog(getActivity(), "서버에서 데이터를 가져오고 있습니다. 잠시만 기다려주세요.");

        keepArrayList.clear();


        new Thread(new Runnable() {
            @Override
            public void run() {


                keepHttp = new OkHttpClient();
                keepArrayList = HttpClient.getKeepWorkList2(keepHttp, writerId, "UPDATE");

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                     //   CommonUtils.hideProgressDialog();

                        if(keepArrayList == null) {
                            Toast.makeText(getActivity(), "서버와의 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (keepArrayList != null) {

                            if(keepArrayList.size() > 0)
                            {
                               // category.add("찜한 작품");


                            }
                            workAdater = new MyPageWorkAdapter(getContext(), keepArrayList,2);
                            recyclerView.setAdapter(workAdater);

                            workAdater.notifyDataSetChanged();
                          //  adapter0.notifyDataSetChanged();

                            /*
                            for (int i = 0; i < keepArrayList.size(); i++) {
                                WorkVO workVO = keepArrayList.get(i);
                                MyPageFeedVo myPageFeedVo = new MyPageFeedVo();
                                myPageFeedVo.setType(4);
                                myPageFeedVo.setWorkVO(workVO);

                                if (i == 0) {
                                    myPageFeedVo.setNoti("찜한 작품");
                                }
                                mArrayList.add(myPageFeedVo);
                            }

                             */
                        }

                    }
                });
            }
        }).start();
    }


    private void requestUpdateUserDesc(String desc) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestSendUserProfile(new OkHttpClient(), SimplePreference.getStringPreference(getContext(), "USER_INFO", "USER_ID", "Guest"), "USER_DESC", desc);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "내 소개글이 변경되었습니다.", Toast.LENGTH_SHORT).show();

                        SimplePreference.setPreference(getContext(), "USER_INFO", "USER_DESC", desc);
                    }
                });
            }
        }).start();
    }
}
