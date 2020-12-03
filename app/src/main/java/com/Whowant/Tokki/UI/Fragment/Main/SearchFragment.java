package com.Whowant.Tokki.UI.Fragment.Main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.ItemClickSupport;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class SearchFragment extends Fragment {

    EditText searchEt;
    LinearLayout searchDeleteLl;
    ImageView searchIv;

    RecyclerView recyclerView;
    SearchAdapter adapter;
    ArrayList<String> mArrayList = new ArrayList<>();

    public static Fragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);

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
                if (TextUtils.isEmpty(searchEt.getText().toString()))
                    Toast.makeText(getActivity(), "검색어를 입력하세요.", Toast.LENGTH_SHORT).show();

                if (actionId == EditorInfo.IME_ACTION_SEARCH || event.getAction() == KeyEvent.ACTION_UP) {
                    Intent intent = new Intent(getActivity(), SearchResultActivity.class);
                    intent.putExtra("search", searchEt.getText().toString());
                    startActivity(intent);

                    searchEt.setText(null);
                    handle = true;
                }
                return handle;
            }
        });
        searchDeleteLl = v.findViewById(R.id.ll_search_delete);
        searchDeleteLl.setOnClickListener((v1) -> searchEt.setText(""));
        searchIv = v.findViewById(R.id.iv_search);
        searchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(searchEt.getText().toString()))
                    Toast.makeText(getActivity(), "검색어를 입력하세요.", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(getActivity(), SearchResultActivity.class);
                    intent.putExtra("search", searchEt.getText().toString());
                    startActivity(intent);

                    searchEt.setText(null);
                }
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
                String item = mArrayList.get(position);

                Intent intent = new Intent(getActivity(), SearchCategoryActivity.class);
                intent.putExtra("genre", item);
                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getGenreList();
    }

    public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Context context;
        ArrayList<String> arrayList;

        public SearchAdapter(Context context, ArrayList<String> arrayList) {
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
            String item = arrayList.get(position);

            if (holder instanceof SearchViewHolder) {
                SearchViewHolder viewHolder = (SearchViewHolder) holder;

                viewHolder.titleTv.setText(item);
            }
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {

        LinearLayout bgLl;
        TextView titleTv;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);

            bgLl = itemView.findViewById(R.id.ll_row_search_bg);
            titleTv = itemView.findViewById(R.id.tv_row_search_title);
        }
    }

    private void getGenreList() {
        mArrayList.clear();

        CommonUtils.showProgressDialog(getContext(), "서버와 통신중입니다. 잠시만 기다려주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> tmp = HttpClient.getAllGenreList(new OkHttpClient());
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
