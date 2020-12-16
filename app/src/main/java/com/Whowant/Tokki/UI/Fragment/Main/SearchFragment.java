package com.Whowant.Tokki.UI.Fragment.Main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Decoration.StorageBoxItemDecoration;
import com.Whowant.Tokki.UI.Activity.Search.SearchCategoryActivity;
import com.Whowant.Tokki.UI.Activity.Search.SearchResultActivity;
import com.Whowant.Tokki.UI.Fragment.Friend.FriendListFragment;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.ItemClickSupport;
import com.Whowant.Tokki.VO.FriendVO;
import com.Whowant.Tokki.VO.GenreVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class SearchFragment extends Fragment {

    EditText searchEt;
    LinearLayout searchDeleteLl;
    ImageView searchIv;

    RecyclerView recyclerView;
    SearchAdapter adapter;
    ArrayList<GenreVO> mArrayList = new ArrayList<>();
    private InputMethodManager imm;

    public static Fragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        searchEt.setCursorVisible(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        imm = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
        searchEt = v.findViewById(R.id.et_search);
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 0) {
                    searchDeleteLl.setVisibility(View.VISIBLE);
                } else {
                    searchDeleteLl.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handle = false;

                if (actionId == EditorInfo.IME_ACTION_SEARCH || event.getAction() == KeyEvent.ACTION_UP) {
                    if (TextUtils.isEmpty(searchEt.getText().toString()) || searchEt.getText().toString().trim().equals(""))
                        Toast.makeText(getActivity(), "검색어를 입력하세요.", Toast.LENGTH_SHORT).show();
                    else {
                        Intent intent = new Intent(getActivity(), SearchResultActivity.class);
                        intent.putExtra("search", searchEt.getText().toString().trim());
                        startActivity(intent);

                        searchEt.setText(null);
                        handle = true;
                    }

                    imm.hideSoftInputFromWindow(searchEt.getWindowToken(), 0);
                }
                return handle;
            }
        });

        searchEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    searchEt.setCursorVisible(true);
            }
        });
        searchDeleteLl = v.findViewById(R.id.ll_search_delete);
        searchDeleteLl.setOnClickListener((v1) -> searchEt.setText(""));
        searchIv = v.findViewById(R.id.iv_search);
        searchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(searchEt.getText().toString()) || searchEt.getText().toString().trim().equals(""))
                    Toast.makeText(getActivity(), "검색어를 입력하세요.", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(getActivity(), SearchResultActivity.class);
                    intent.putExtra("search", searchEt.getText().toString().trim());
                    startActivity(intent);

                    searchEt.setText(null);
                }

                imm.hideSoftInputFromWindow(searchEt.getWindowToken(), 0);
            }
        });

        recyclerView = v.findViewById(R.id.recyclerView);
        adapter = new SearchAdapter(getContext(), mArrayList);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.addItemDecoration(new StorageBoxItemDecoration(getContext()));
        recyclerView.setAdapter(adapter);
        ItemClickSupport.addTo(recyclerView).setItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                GenreVO vo = mArrayList.get(position);

                Intent intent = new Intent(getActivity(), SearchCategoryActivity.class);
                intent.putExtra("genre", vo.getGenreName());
                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser) {

        } else {
            if (searchEt != null) {
                searchEt.clearFocus();
                searchEt.setCursorVisible(false);
                searchEt.setText(null);
                imm.hideSoftInputFromWindow(searchEt.getWindowToken(), 0);
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getGenreList();
    }

    public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Context context;
        ArrayList<GenreVO> arrayList;

        public SearchAdapter(Context context, ArrayList<GenreVO> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.row_search_fragment, parent, false);
            return new SearchViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            GenreVO vo = arrayList.get(position);

            if (holder instanceof SearchViewHolder) {
                SearchViewHolder viewHolder = (SearchViewHolder) holder;

                viewHolder.titleTv.setText(vo.getGenreName());
//                viewHolder.titleTv.setShadowLayer(1, 1, 1, Color.parseColor("#a8000000"));

                String strPhoto = vo.getGenreImg();
                viewHolder.bgIm.setClipToOutline(true);
                if(strPhoto != null && !strPhoto.equals("null") && !strPhoto.equals("NULL") && strPhoto.length() > 0) {
                    if(!strPhoto.startsWith("http"))
                        strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

                    Glide.with(getActivity())
                            .asBitmap() // some .jpeg files are actually gif
                            .load(strPhoto)
                            .apply(new RequestOptions().centerCrop())
                            .into(viewHolder.bgIm);
                } else {
                    Glide.with(getActivity())
                            .asBitmap() // some .jpeg files are actually gif
                            .load(R.drawable.round_8_cc222222)
                            .into(viewHolder.bgIm);
                }
            }
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {

//        LinearLayout bgLl;
        ImageView bgIm;
        TextView titleTv;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);

//            bgLl = itemView.findViewById(R.id.ll_row_search_bg);
            bgIm = itemView.findViewById(R.id.im_row_search_bg);
            titleTv = itemView.findViewById(R.id.tv_row_search_title);
        }
    }

    private void getGenreList() {
        mArrayList.clear();

        CommonUtils.showProgressDialog(getContext(), "서버와 통신중입니다. 잠시만 기다려주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<GenreVO> tmp = HttpClient.getGenreInfo(new OkHttpClient());
                if (tmp != null) {
                    mArrayList.addAll(tmp);
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if (tmp == null) {
                            Toast.makeText(getContext(), "장르 목록을 가져오는데 실패했습니다.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }
}
