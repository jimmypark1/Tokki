package com.Whowant.Tokki.UI.Fragment.Main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Decoration.StorageBoxItemDecoration;
import com.Whowant.Tokki.UI.Activity.Work.WorkMainActivity;
import com.Whowant.Tokki.UI.TypeViewOnClickListener;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.DialogMenu;
import com.Whowant.Tokki.Utils.ItemClickSupport;
import com.Whowant.Tokki.Utils.SimplePreference;
import com.Whowant.Tokki.VO.BookListVo;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class StorageBoxReadingFragment extends Fragment {

    RecyclerView recyclerView;
    StorageBoxReadingAdapter adapter;
    ArrayList<WorkVO> mArrayList = new ArrayList<>();
    ArrayList<BookListVo> bookListVos = new ArrayList<>();

    LinearLayout emptyLl;

    private final String READ_ORDER_UPDATE = "UPDATE";
    private final String READ_ORDER_WRITER = "WRITER";
    private final String READ_ORDER_RECENTLY = "RECENTLY";
    private final String READ_ORDER_TITLE = "TITLE";

    private String strReadOrder = READ_ORDER_RECENTLY;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_storage_box_reading, container, false);

        recyclerView = v.findViewById(R.id.recyclerView);
        adapter = new StorageBoxReadingAdapter(getContext(), mArrayList);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.addItemDecoration(new StorageBoxItemDecoration(getActivity()));
        recyclerView.setAdapter(adapter);
        ItemClickSupport.addTo(recyclerView).setItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                WorkVO item = mArrayList.get(position);

                Intent intent = new Intent(getContext(), WorkMainActivity.class);
                intent.putExtra("WORK_ID", item.getnWorkID());
                startActivity(intent);
            }
        });

        emptyLl = v.findViewById(R.id.ll_storage_box_reading_empty);
        emptyLl.setVisibility(View.GONE);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        getReadListData();
    }

    public class StorageBoxReadingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Context context;
        ArrayList<WorkVO> arrayList;

        public StorageBoxReadingAdapter(Context context, ArrayList<WorkVO> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.row_storage_box_contents, parent, false);
            return new StorageBoxReadingViewHolder(v, new TypeViewOnClickListener() {
                @Override
                public void onClick(View v, int type, int position) {
                    WorkVO workVO = arrayList.get(position);

                    switch (type) {
                        case 0:
                            PopupMenu popup = new PopupMenu(getContext(), v);
                            popup.getMenuInflater().inflate(R.menu.storage_box_reading_card_menu, popup.getMenu());

                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.add:
                                            getReadingList(SimplePreference.getStringPreference(getContext(), "USER_INFO", "USER_ID", "Guest"), String.valueOf(workVO.getnWorkID()));
                                            break;
                                        case R.id.share:
                                            String strURL = "https://tokki.page.link/1ux2?CMD=workmain&WORK_ID=" + workVO.getnWorkID();
                                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                            shareIntent.setType("text/plain");
                                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Tokki에서 " + workVO.getTitle() + " 작품을 만나보세요!"); // 제목
                                            shareIntent.putExtra(Intent.EXTRA_TEXT, strURL); // 내용
                                            startActivity(Intent.createChooser(shareIntent, "")); // 공유창 제목
                                            break;
                                        case R.id.remove:
                                            requestDeleteRead(workVO.getnWorkID());
                                            break;
                                    }
                                    return true;
                                }
                            });
                            popup.show();
                            break;
                    }
                }
            });
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            WorkVO item = arrayList.get(position);

            if (holder instanceof StorageBoxReadingViewHolder) {
                StorageBoxReadingViewHolder viewHolder = (StorageBoxReadingViewHolder) holder;

                String strImgUrl = item.getCoverFile();

                viewHolder.titleTv.setText(item.getTitle());
                viewHolder.tabCountTv.setText(CommonUtils.getPointCount(item.getnTapCount()));
                viewHolder.countTv.setText(CommonUtils.getPointCount(item.getnCommentCount()));

                if (strImgUrl == null || strImgUrl.equals("null") || strImgUrl.equals("NULL") || strImgUrl.length() == 0) {
                    Glide.with(getActivity())
                            .asBitmap() // some .jpeg files are actually gif
                            .load(R.drawable.no_poster)
//                            .apply(new RequestOptions().override(800, 800))
                            .into(viewHolder.coverIv);
                } else {
                    if (!strImgUrl.startsWith("http")) {
                        strImgUrl = CommonUtils.strDefaultUrl + "images/" + strImgUrl;
                    }

                    Glide.with(getActivity())
                            .asBitmap() // some .jpeg files are actually gif
                            .placeholder(R.drawable.no_poster)
                            .load(strImgUrl)
//                            .apply(new RequestOptions().override(800, 800))
                            .into(viewHolder.coverIv);
                }
            }
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }

    public class StorageBoxReadingViewHolder extends RecyclerView.ViewHolder {

        ImageView coverIv;
        TextView titleTv;
        TextView tabCountTv;
        TextView countTv;
        ImageView optionIv;

        public StorageBoxReadingViewHolder(@NonNull View itemView, TypeViewOnClickListener listener) {
            super(itemView);

            coverIv = itemView.findViewById(R.id.iv_row_storage_box_contents_cover);
            coverIv.setClipToOutline(true);
            titleTv = itemView.findViewById(R.id.tv_row_storage_box_contents_title);
            tabCountTv = itemView.findViewById(R.id.tv_row_storage_box_contents_tab_count);
            countTv = itemView.findViewById(R.id.tv_row_storage_box_contents_count);
            optionIv = itemView.findViewById(R.id.iv_row_storage_box_contents_option);
            optionIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick(v, 0, getAdapterPosition());
                    }
                }
            });
        }
    }

    private void getReadListData() {
        CommonUtils.showProgressDialog(getActivity(), "서버에서 데이터를 가져오고 있습니다. 잠시만 기다려주세요.");
        mArrayList.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<WorkVO> responseVo = HttpClient.getReadWorkList(new OkHttpClient(), SimplePreference.getStringPreference(getContext(), "USER_INFO", "USER_ID", "Guest"), strReadOrder);
                if (responseVo != null) {
                    mArrayList.addAll(responseVo);
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if (mArrayList == null) {
                            Toast.makeText(getActivity(), "서버와의 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        adapter.notifyDataSetChanged();

                        if (mArrayList.size() == 0) {
                            emptyLl.setVisibility(View.VISIBLE);
                        } else {
                            emptyLl.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }).start();
    }

    private void requestDeleteRead(int nEpisodeID) {
        CommonUtils.showProgressDialog(getActivity(), "서버와 통신중입니다 잠시만 기다려 주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestDeleteRead(new OkHttpClient(), nEpisodeID, SimplePreference.getStringPreference(getActivity(), "USER_INFO", "USER_ID", "Guest"));

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if (!bResult) {
                            Toast.makeText(getActivity(), "읽은목록 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        getReadListData();
                    }
                });
            }
        }).start();
    }

    private void addReadingList(String workId, String readingId) {
//        CommonUtils.showProgressDialog(getActivity(), "구독을 취소 중입니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                final int result = HttpClient.addReadingList(new OkHttpClient(), workId, readingId);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(result == 0) {
                            Toast.makeText(getActivity(), "독서목록에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                        } else if(result == 1) {
                            Toast.makeText(getActivity(), "이미 추가된 작품입니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "독서목록에 추가되지 않았습니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void getReadingList(String userId, String workId) {
        bookListVos.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                bookListVos.addAll(HttpClient.getReadingList(new OkHttpClient(), userId));

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String[] tmp = new String[bookListVos.size()];
                        for (int i = 0; i < bookListVos.size(); i++) {
                            tmp[i] = bookListVos.get(i).getREADING_NAME();
                        }

                        DialogMenu dialogMenu = new DialogMenu();
                        if (bookListVos.size() == 0) {
                            Toast.makeText(getActivity(), "추가할 독서목록이 없습니다.", Toast.LENGTH_LONG).show();
                        } else {
                            dialogMenu.showMenu(getActivity(), "독서목록에 추가", tmp, new DialogMenu.ItemClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int pos) {
                                    addReadingList(workId, String.valueOf(bookListVos.get(pos).getID()));
                                }
                            });
                        };
                    }
                });
            }
        }).start();
    }

    public void refreshData(String order) {
        strReadOrder = order;
//        getReadListData();
    }
}
