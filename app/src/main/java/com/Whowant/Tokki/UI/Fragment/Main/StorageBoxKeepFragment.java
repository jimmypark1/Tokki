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
import com.Whowant.Tokki.UI.Activity.Work.ReportSelectActivity;
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

public class StorageBoxKeepFragment extends Fragment {

    RecyclerView recyclerView;
    StorageBoxKeepAdapter adapter;
    ArrayList<WorkVO> mArrayList = new ArrayList<>();
    ArrayList<BookListVo> bookListVos = new ArrayList<>();

    private String strKeepOrder = "UPDATE";

    LinearLayout emptyLl;
    LinearLayout selectedKeepLl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_storage_box_keep, container, false);

        selectedKeepLl = v.findViewById(R.id.ll_storage_box_keep_selected);
        selectedKeepLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ????????? ????????? ?????? ??????

            }
        });

        recyclerView = v.findViewById(R.id.recyclerView);
        adapter = new StorageBoxKeepAdapter(getContext(), mArrayList);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.addItemDecoration(new StorageBoxItemDecoration(getActivity()));
        recyclerView.setAdapter(adapter);
        ItemClickSupport.addTo(recyclerView).setItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                WorkVO item = mArrayList.get(position);

                Intent intent = new Intent(getContext(), WorkMainActivity.class);
                intent.putExtra("WORK_ID", item.getnWorkID());
                intent.putExtra("WORK_TYPE", item.getnTarget());

                startActivity(intent);
            }
        });

        emptyLl = v.findViewById(R.id.ll_storage_box_keep_empty);
        emptyLl.setVisibility(View.GONE);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        getKeepListData();
    }

    public class StorageBoxKeepAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Context context;
        ArrayList<WorkVO> arrayList;

        public StorageBoxKeepAdapter(Context context, ArrayList<WorkVO> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.row_storage_box_contents, parent, false);
            return new StorageBoxKeepViewHolder(v, new TypeViewOnClickListener() {
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
                                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Tokki?????? " + workVO.getTitle() + " ????????? ???????????????!"); // ??????
                                            shareIntent.putExtra(Intent.EXTRA_TEXT, strURL); // ??????
                                            startActivity(Intent.createChooser(shareIntent, "")); // ????????? ??????
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

            if (holder instanceof StorageBoxKeepViewHolder) {
                StorageBoxKeepViewHolder viewHolder = (StorageBoxKeepViewHolder) holder;

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

    public class StorageBoxKeepViewHolder extends RecyclerView.ViewHolder {

        ImageView coverIv;
        TextView titleTv;
        TextView tabCountTv;
        TextView countTv;
        ImageView optionIv;

        public StorageBoxKeepViewHolder(@NonNull View itemView, TypeViewOnClickListener listener) {
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

    private void getKeepListData() {
        CommonUtils.showProgressDialog(getActivity(), "???????????? ???????????? ???????????? ????????????. ????????? ??????????????????.");

        mArrayList.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<WorkVO> responseVo = HttpClient.getKeepWorkList(new OkHttpClient(), SimplePreference.getStringPreference(getContext(), "USER_INFO", "USER_ID", "Guest"), strKeepOrder);

                if (responseVo != null) {
                    mArrayList.addAll(responseVo);
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if (mArrayList == null) {
                            Toast.makeText(getActivity(), "???????????? ????????? ???????????? ????????????.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        adapter.notifyDataSetChanged();

                        if (mArrayList.size() == 0) {
                            emptyLl.setVisibility(View.VISIBLE);
                        } else {
                            emptyLl.setVisibility(View.INVISIBLE);
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
                            Toast.makeText(getActivity(), "????????? ??????????????? ????????????.", Toast.LENGTH_LONG).show();
                        } else {
                            dialogMenu.showMenu(getActivity(), "??????????????? ??????", tmp, new DialogMenu.ItemClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int pos) {
                                    addReadingList(workId, String.valueOf(bookListVos.get(pos).getID()));
                                }
                            });
                        }
                    }
                });
            }
        }).start();
    }

    private void requestUnKeep(int workId) {
        CommonUtils.showProgressDialog(getActivity(), "????????? ?????? ????????????.");

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
                            CommonUtils.makeText(getActivity(), "???????????? ????????? ???????????? ????????????. ????????? ?????? ????????? ?????????.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        CommonUtils.makeText(getActivity(), "????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                        getKeepListData();
                    }
                });
            }
        }).start();
    }

    private void addReadingList(String workId, String readingId) {
//        CommonUtils.showProgressDialog(getActivity(), "????????? ?????? ????????????.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                final int result = HttpClient.addReadingList(new OkHttpClient(), workId, readingId);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(result == 0) {
                            Toast.makeText(getActivity(), "??????????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                        } else if(result == 1) {
                            Toast.makeText(getActivity(), "?????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "??????????????? ???????????? ???????????????. ?????? ??? ?????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    public void refreshData(String order) {
        strKeepOrder = order;
//        getKeepListData();
    }
}
