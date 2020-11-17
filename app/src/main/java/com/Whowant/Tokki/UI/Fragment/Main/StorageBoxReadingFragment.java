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

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class StorageBoxReadingFragment extends Fragment {

    RecyclerView recyclerView;
    StorageBoxReadingAdapter adapter;
    ArrayList<WorkVO> mArrayList = new ArrayList<>();
    ArrayList<BookListVo> bookListVos = new ArrayList<>();

    LinearLayout emptyLl;

    private String strReadOrder = "UPDATE";

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
                                            Intent intent = new Intent(Intent.ACTION_SEND);
                                            intent.setType("text/plain");
                                            intent.putExtra(Intent.EXTRA_TEXT, "Tokki공유");
                                            Intent chooser = Intent.createChooser(intent, "~통해 공유");
                                            startActivity(chooser);
                                            break;
                                        case R.id.remove:
                                            requestUnKeep(workVO.getnWorkID());
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

    private void requestUnKeep(int workId) {
        CommonUtils.showProgressDialog(getActivity(), "구독을 취소 중입니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                String userId = SimplePreference.getStringPreference(getActivity(), "USER_INFO", "USER_ID", "Guest");
                JSONObject resultObject = HttpClient.requestUnKeep(new OkHttpClient(), userId, "" + workId);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if (resultObject == null) {
                            CommonUtils.makeText(getActivity(), "서버와의 통신이 원활하지 않습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        CommonUtils.makeText(getActivity(), "구독이 취소되었습니다.", Toast.LENGTH_SHORT).show();
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
                boolean result = HttpClient.addReadingList(new OkHttpClient(), workId, readingId);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if (result) {

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
                        dialogMenu.showMenu(getActivity(), "독서목록에 추가", tmp, new DialogMenu.ItemClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int pos) {
                                addReadingList(workId, String.valueOf(bookListVos.get(pos).getID()));
                            }
                        });
                    }
                });
            }
        }).start();
    }

    public void refreshData(String order) {
        strReadOrder = order;
        getReadListData();
    }
}
