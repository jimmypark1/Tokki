package com.Whowant.Tokki.UI.Fragment.Main;

import android.content.Context;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Decoration.StorageBoxItemDecoration;
import com.Whowant.Tokki.UI.Activity.Search.SearchCategoryActivity;
import com.Whowant.Tokki.UI.Activity.Search.SearchResultActivity;
import com.Whowant.Tokki.Utils.ItemClickSupport;
import com.Whowant.Tokki.VO.WorkVO;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    EditText searchEt;
    LinearLayout searchDeleteLl;
    ImageView searchIv;

    RecyclerView recyclerView;
    SearchAdapter adapter;
    ArrayList<WorkVO> mArrayList = new ArrayList<>();

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
        searchDeleteLl = v.findViewById(R.id.ll_search_delete);
        searchDeleteLl.setOnClickListener((v1) -> searchEt.setText(""));
        searchIv = v.findViewById(R.id.iv_search);
        searchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(searchEt.getText().toString())) {
                    Intent intent = new Intent(getActivity(), SearchResultActivity.class);
                    intent.putExtra("search", searchEt.getText().toString());
                    startActivity(intent);
                }
            }
        });

        recyclerView = v.findViewById(R.id.recyclerView);
        adapter = new SearchAdapter(getContext());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.addItemDecoration(new StorageBoxItemDecoration(getContext()));
        recyclerView.setAdapter(adapter);
        ItemClickSupport.addTo(recyclerView).setItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), SearchCategoryActivity.class);
                startActivity(intent);
            }
        });

        return v;
    }

    public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Context context;

        public SearchAdapter(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.row_search_fragment, parent, false);
            return new SearchViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 20;
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
}
