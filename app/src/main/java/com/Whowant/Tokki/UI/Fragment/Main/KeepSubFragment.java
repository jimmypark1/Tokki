package com.Whowant.Tokki.UI.Fragment.Main;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Work.WorkMainActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class KeepSubFragment extends Fragment implements AdapterView.OnItemClickListener {
    private CKeepArrayAdapter aa;
    private ArrayList<WorkVO> keepList;
    private ListView listView;
    private SharedPreferences pref;
    private TextView emptyView;

    private int nMode = 0;

    private static final int MODE_KEEP = 0;
    private static final int MODE_READ = 1;
    private String strKeepOrder = "UPDATE";
    private String strReadOrder = "UPDATE";
    private boolean bVisible = false;
    private boolean bScroll = false;

    public KeepSubFragment(int mode) {
        nMode = mode;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.keep_menu, menu);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_more: {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("작품 정렬");

                if (nMode == MODE_KEEP) {
                    builder.setItems(getResources().getStringArray(R.array.KEEP_MENU), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int pos) {
                            if (pos == 0) {
                                strKeepOrder = "NAME";
                            } else if (pos == 1) {
                                strKeepOrder = "UPDATE";
                            }

                            getKeepListData();
                        }
                    });
                } else {
                    builder.setItems(R.array.READ_MENU, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int pos) {
                            if (pos == 0) {
                                strReadOrder = "NAME";
                            } else if (pos == 1) {
                                strReadOrder = "UPDATE";
                            } else if (pos == 2) {
                                strReadOrder = "READ";
                            }

                            getReadListData();
                        }
                    });
                }

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
//                startActivity(new Intent(getActivity(), SearchActivity.class));
            }
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.keep_frg_layout, container, false);

        pref = getActivity().getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);

        keepList = new ArrayList<>();
        listView = inflaterView.findViewById(R.id.listView);
        emptyView = inflaterView.findViewById(R.id.emptyView);
        listView.setOnItemClickListener(this);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    bScroll = false;
                } else {
                    bScroll = true;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        getKeepListData();

//        workTabLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(bScroll)
//                    return;
//
//                nMode = MODE_KEEP;
//                initTab(0);
//                getKeepListData();
//            }
//        });
//
//        commentTabLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(bScroll)
//                    return;
//
//                nMode = MODE_READ;
//                initTab(1);
//                getReadListData();
//            }
//        });

        return inflaterView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(keepList == null)
            return;

        if (isVisibleToUser) {
            FirebaseAnalytics.getInstance(getContext()).setCurrentScreen(getActivity(), "KeepFragment", null);
            bVisible = true;
            if (nMode == MODE_KEEP) {
                getKeepListData();
            } else {
                getReadListData();
            }
        } else {
            bVisible = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (bVisible) {
            if (nMode == MODE_KEEP)
                getKeepListData();
            else
                getReadListData();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        WorkVO vo = keepList.get(i);
        Intent intent = new Intent(getActivity(), WorkMainActivity.class);
        intent.putExtra("WORK_ID", vo.getnWorkID());
        startActivity(intent);
    }

    public void onClickTopLeftBtn(View view) {

    }

    public void showMenus() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("작품 정렬");

        if (nMode == MODE_KEEP) {
            builder.setItems(getResources().getStringArray(R.array.KEEP_MENU), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int pos) {
                    if (pos == 0) {
                        strKeepOrder = "NAME";
                    } else if (pos == 1) {
                        strKeepOrder = "UPDATE";
                    }

                    getKeepListData();
                }
            });
        } else {
            builder.setItems(R.array.READ_MENU, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int pos) {
                    if (pos == 0) {
                        strReadOrder = "NAME";
                    } else if (pos == 1) {
                        strReadOrder = "UPDATE";
                    } else if (pos == 2) {
                        strReadOrder = "READ";
                    }

                    getReadListData();
                }
            });
        }

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void getKeepListData() {
        CommonUtils.showProgressDialog(getActivity(), "서버에서 데이터를 가져오고 있습니다. 잠시만 기다려주세요.");

        keepList.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                keepList = HttpClient.getKeepWorkList(new OkHttpClient(), pref.getString("USER_ID", "Guest"), strKeepOrder);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();
                        
                        if(keepList == null) {
                            Toast.makeText(getActivity(), "서버와의 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        aa = new CKeepArrayAdapter(getActivity(), R.layout.best_row, keepList);
                        listView.setAdapter(aa);

                        if (keepList.size() == 0) {
                            emptyView.setVisibility(View.VISIBLE);
                        } else {
                            emptyView.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        }).start();
    }

    private void getReadListData() {
        CommonUtils.showProgressDialog(getActivity(), "서버에서 데이터를 가져오고 있습니다. 잠시만 기다려주세요.");
        keepList.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                keepList = HttpClient.getReadWorkList(new OkHttpClient(), pref.getString("USER_ID", "Guest"), strReadOrder);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(keepList == null) {
                            Toast.makeText(getActivity(), "서버와의 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        aa = new CKeepArrayAdapter(getActivity(), R.layout.best_row, keepList);
                        listView.setAdapter(aa);

                        if (keepList.size() == 0) {
                            emptyView.setVisibility(View.VISIBLE);
                        } else {
                            emptyView.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        }).start();
    }

    public class CKeepArrayAdapter extends ArrayAdapter<Object> {
        private LayoutInflater mLiInflater;

        CKeepArrayAdapter(Context context, int layout, List titles) {
            super(context, layout, titles);
            mLiInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = mLiInflater.inflate(R.layout.best_row, parent, false);

            LinearLayout rightLayout = convertView.findViewById(R.id.rightLayout);
            rightLayout.setVisibility(View.GONE);

            ImageView menuBtn = convertView.findViewById(R.id.menuBtn);

            if (nMode == MODE_READ) {
                menuBtn.setVisibility(View.VISIBLE);
            } else {
                menuBtn.setVisibility(View.INVISIBLE);
            }

            WorkVO vo = keepList.get(position);
            String strImgUrl = vo.getCoverFile();

            ImageView coverView = convertView.findViewById(R.id.coverImgView);
            coverView.setClipToOutline(true);
            TextView titleView = convertView.findViewById(R.id.titleView);
            TextView synopsisView = convertView.findViewById(R.id.synopsisView);
            TextView starPointView = convertView.findViewById(R.id.startPointView);
            TextView hitsCountView = convertView.findViewById(R.id.hitsCountView);
            TextView commentCountView = convertView.findViewById(R.id.commentCountView);
            TextView subcriptionCountView = convertView.findViewById(R.id.subcriptionCountView);
            TextView bestBGView = convertView.findViewById(R.id.bestBGView);
            TextView writerNameView = convertView.findViewById(R.id.writerNameView);

            if (strImgUrl == null || strImgUrl.equals("null") || strImgUrl.equals("NULL") || strImgUrl.length() == 0) {
                Glide.with(getActivity())
                        .asBitmap() // some .jpeg files are actually gif
                        .load(R.drawable.no_poster)
                        .apply(new RequestOptions().override(800, 800))
                        .into(coverView);
            } else {
                if (!strImgUrl.startsWith("http")) {
                    strImgUrl = CommonUtils.strDefaultUrl + "images/" + strImgUrl;
                }

                Glide.with(getActivity())
                        .asBitmap() // some .jpeg files are actually gif
                        .placeholder(R.drawable.no_poster)
                        .load(strImgUrl)
                        .apply(new RequestOptions().override(800, 800))
                        .into(coverView);
            }

            menuBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(getActivity(), menuBtn);
                    popup.getMenuInflater().inflate(R.menu.comment_admin_menu, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            Intent intent = null;

                            switch (item.getItemId()) {
                                case R.id.delete:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setTitle("읽은 작품 삭제");
                                    builder.setMessage("읽은 작품을 삭제하시겠습니까?");
                                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            requestDeleteRead(vo.getnWorkID());
                                        }
                                    });

                                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                        }
                                    });

                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                    break;
                            }
                            return true;
                        }
                    });

                    popup.show();//showing popup menu
                }
            });

            writerNameView.setText(vo.getStrWriterName());
            titleView.setText(vo.getTitle());
            synopsisView.setText(vo.getSynopsis());
            bestBGView.setVisibility(View.GONE);

            float fStarPoint = vo.getfStarPoint();
            if (fStarPoint == 0)
                starPointView.setText("0");
            else
                starPointView.setText(String.format("%.1f", vo.getfStarPoint()));

            hitsCountView.setText(CommonUtils.getPointCount(vo.getnTapCount()));
            commentCountView.setText(CommonUtils.getPointCount(vo.getnCommentCount()));
            subcriptionCountView.setText(CommonUtils.getPointCount(vo.getnKeepcount()));

            return convertView;
        }
    }

    private void requestDeleteRead(int nEpisodeID) {
        CommonUtils.showProgressDialog(getActivity(), "서버와 통신중입니다 잠시만 기다려 주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestDeleteRead(new OkHttpClient(), nEpisodeID, pref.getString("USER_ID", "Guest"));

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
}
