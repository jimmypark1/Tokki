package com.Whowant.Tokki.UI.Fragment.Main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.StorageBox.StorageBoxBookListDetailActivity;
import com.Whowant.Tokki.UI.Popup.EditPopup;
import com.Whowant.Tokki.Utils.ItemClickSupport;
import com.Whowant.Tokki.Utils.SimplePreference;
import com.Whowant.Tokki.VO.BookListVo;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class StorageBoxBookListFragment extends Fragment {

    RecyclerView recyclerView;
    StorageBoxBookListAdapter adapter;
    ArrayList<BookListVo> mArrayList = new ArrayList<>();

    LinearLayout emptyLl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_storage_box_reading, container, false);

        emptyLl = v.findViewById(R.id.ll_storage_box_reading_empty);
        v.findViewById(R.id.ll_storage_box_reading_empty_button).setVisibility(View.GONE);
        ((TextView)v.findViewById(R.id.tv_storage_box_reading_empty_title)).setText("독서목록에 아무 목록도 없습니다.");

        recyclerView = v.findViewById(R.id.recyclerView);
        adapter = new StorageBoxBookListAdapter(getContext(), mArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.addItemDecoration(new StorageBoxItemDecoration(getActivity()));
        recyclerView.setAdapter(adapter);
        ItemClickSupport.addTo(recyclerView).setItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                BookListVo item = mArrayList.get(position);

                Intent intent = new Intent(getActivity(), StorageBoxBookListDetailActivity.class);
                intent.putExtra("title", item.getREADING_NAME());
                intent.putExtra("readingId", String.valueOf(item.getID()));
                startActivity(intent);
            }
        });

        return v;
    }

    public class StorageBoxBookListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Context context;
        ArrayList<BookListVo> arrayList;

        public StorageBoxBookListAdapter(Context context, ArrayList<BookListVo> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.row_storage_box_book_list, parent, false);
            return new StorageBoxBookListViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            BookListVo item = arrayList.get(position);

            if (holder instanceof StorageBoxBookListViewHolder) {
                StorageBoxBookListViewHolder viewHolder = (StorageBoxBookListViewHolder) holder;

                viewHolder.titleTv.setText(item.getREADING_NAME());
                viewHolder.countTv.setText(String.valueOf(item.getBOOKS_COUNT()) + " 스토리");
                viewHolder.optionIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popup = new PopupMenu(getActivity(), viewHolder.optionIv);
                        popup.getMenuInflater().inflate(R.menu.storage_box_book_list_card_menu, popup.getMenu());
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item2) {
                                switch (item2.getItemId()) {
                                    case R.id.rename: {
                                        Intent intent = new Intent(getActivity(), EditPopup.class);
                                        intent.putExtra("type", EditPopup.TYPE_RENAME_BOOK_LIST);
                                        intent.putExtra("name", item.getREADING_NAME());
                                        intent.putExtra("readingId", item.getID());
                                        startActivity(intent);
                                    }
                                    break;
                                }
                                return true;
                            }
                        });

                        popup.show();
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }

    public class StorageBoxBookListViewHolder extends RecyclerView.ViewHolder {

        TextView titleTv;
        TextView countTv;
        ImageView optionIv;

        public StorageBoxBookListViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTv = itemView.findViewById(R.id.tv_row_storage_box_book_list_title);
            countTv = itemView.findViewById(R.id.tv_row_storage_box_book_list_count);
            optionIv = itemView.findViewById(R.id.iv_row_storage_box_book_list_option);
        }
    }

    private void getReadingList(String userId) {
        mArrayList.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                mArrayList.addAll(HttpClient.getReadingList(new OkHttpClient(), userId));

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();

                        emptyLl.setVisibility(mArrayList.size() > 0 ? View.GONE : View.VISIBLE);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();

        getReadingList(SimplePreference.getStringPreference(getContext(), "USER_INFO", "USER_ID", "Guest"));
    }
}
