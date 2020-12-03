package com.Whowant.Tokki.UI.Activity.StorageBox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Popup.EditPopup;
import com.Whowant.Tokki.UI.Popup.MessagePopup;
import com.Whowant.Tokki.UI.TypeViewOnClickListener;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.ItemTouchHelperCallback;
import com.Whowant.Tokki.Utils.SimplePreference;
import com.Whowant.Tokki.VO.BookListVo;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class StorageBoxBookListModifyActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    StorageBoxBookListModifyAdapter adapter;
    ArrayList<BookListVo> mArrayList = new ArrayList<>();

    Activity mActivity;

    String userId;

    ItemTouchHelper itemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_box_book_list_modify);

        mActivity = this;

        getData();
        initView();

        getReadingList(userId);
    }

    private void getData() {
        userId = SimplePreference.getStringPreference(mActivity, "USER_INFO", "USER_ID", "Guest");
    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_top_layout_title)).setText("독서목록 편집");

        findViewById(R.id.ib_top_layout_back).setVisibility(View.VISIBLE);
        findViewById(R.id.ib_top_layout_back).setOnClickListener((v) -> finish());

        findViewById(R.id.ib_top_layout_plus).setVisibility(View.VISIBLE);
        findViewById(R.id.ib_top_layout_plus).setOnClickListener((v) -> {
            Intent intent = new Intent(mActivity, EditPopup.class);
            intent.putExtra("type", EditPopup.TYPE_ADD_BOOK_LIST);
            startActivityForResult(intent, 919);
        });

        recyclerView = findViewById(R.id.recyclerView);
        adapter = new StorageBoxBookListModifyAdapter(mActivity, mArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(adapter);
    }

    public class StorageBoxBookListModifyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperCallback.ItemTouchHelperListener {

        Context context;
        ArrayList<BookListVo> arrayList;

        public StorageBoxBookListModifyAdapter(Context context, ArrayList<BookListVo> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_storage_box_book_list, parent, false);
            return new StorageBoxBookListModifyViewHolder(v, new TypeViewOnClickListener() {
                @Override
                public void onClick(View v, int type, int position) {
                    BookListVo vo = arrayList.get(position);
                    PopupMenu popup = new PopupMenu(context, v);
                    popup.getMenuInflater().inflate(R.menu.storage_box_book_list_modify_card_menu, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.delete: {
                                    Intent intent = new Intent(mActivity, MessagePopup.class);
                                    intent.putExtra("message", "독서 목록을 삭제하시겠어요?");
                                    intent.putExtra("readingId", String.valueOf(vo.getID()));
                                    startActivityForResult(intent, 901);
                                }
                                break;
                                case R.id.rename: {
                                    Intent intent = new Intent(mActivity, EditPopup.class);
                                    intent.putExtra("type", EditPopup.TYPE_RENAME_BOOK_LIST);
                                    intent.putExtra("readingId", vo.getID());
                                    intent.putExtra("name", vo.getREADING_NAME());
                                    startActivityForResult(intent, 921);
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

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            BookListVo item = arrayList.get(position);

            if (holder instanceof StorageBoxBookListModifyViewHolder) {
                StorageBoxBookListModifyViewHolder viewHolder = (StorageBoxBookListModifyViewHolder) holder;

                viewHolder.titleTv.setText(item.getREADING_NAME());
                viewHolder.countTv.setText(String.valueOf(item.getBOOKS_COUNT()) + " 스토리");
            }
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        @Override
        public boolean onItemMove(int from_position, int to_position) {

            // 이동할 객체 저장
            BookListVo bookListVo = arrayList.get(from_position);
            // 이동할 객체 삭제
            arrayList.remove(from_position);
            // 이동하고 싶은 position에 추가
            arrayList.add(to_position, bookListVo);

            notifyItemMoved(from_position, to_position);

            return true;
        }

        @Override
        public void onItemSwipe(int position) {

        }
    }

    public class StorageBoxBookListModifyViewHolder extends RecyclerView.ViewHolder {
        TextView titleTv;
        TextView countTv;
        ImageView optionIv;

        public StorageBoxBookListModifyViewHolder(@NonNull View itemView, TypeViewOnClickListener listener) {
            super(itemView);

            titleTv = itemView.findViewById(R.id.tv_row_storage_box_book_list_title);
            countTv = itemView.findViewById(R.id.tv_row_storage_box_book_list_count);
            optionIv = itemView.findViewById(R.id.iv_row_storage_box_book_list_option);
            optionIv.setOnClickListener((v) -> {
                if (listener != null) {
                    listener.onClick(v, 0, getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(v -> {
                itemTouchHelper.startDrag(StorageBoxBookListModifyViewHolder.this);
                return true;
            });
        }
    }

    private void getReadingList(String userId) {
        mArrayList.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                mArrayList.addAll(HttpClient.getReadingList(new OkHttpClient(), userId));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 919 && resultCode == RESULT_OK) {
            getReadingList(userId);
        } else if (requestCode == 921 && resultCode == RESULT_OK) {
            getReadingList(userId);
        } else if (requestCode == 901 && resultCode == RESULT_OK) {
            if (data != null) {
                String readingId = data.getStringExtra("readingId");
                dropReadingList(userId, readingId);
            }
        }
    }

    private void dropReadingList(String userId, String readingId) {
        CommonUtils.showProgressDialog(mActivity, "서버와 통신중입니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.dropReadingList(new OkHttpClient(), userId, readingId);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();
                        if (bResult) {
                            getReadingList(userId);
                        }
                    }
                });
            }
        }).start();
    }
}