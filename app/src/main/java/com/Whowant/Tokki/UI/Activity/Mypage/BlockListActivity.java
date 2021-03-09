package com.Whowant.Tokki.UI.Activity.Mypage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Admin.CommentManagementActivity;
import com.Whowant.Tokki.UI.Activity.Market.MarketAddActivity;
import com.Whowant.Tokki.UI.Activity.Market.MarketAddEditActivity;
import com.Whowant.Tokki.UI.Activity.Market.MarketContentsFragment;
import com.Whowant.Tokki.UI.Activity.Market.MarketMainActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.BlockUserVO;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class BlockListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    BlockAdapter adapter;

    private ArrayList<BlockUserVO> blocks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBlockUser();
    }

    void getBlockUser()
    {

        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

                String userId = pref.getString("USER_ID", "Guest");

                if(blocks != null)
                {
                    blocks.clear();
                }
                blocks = HttpClient.getSpaceBlockUser(new OkHttpClient(),userId);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (blocks == null) {
                            Toast.makeText(BlockListActivity.this, "서버와의 통신에 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        adapter = new BlockAdapter(BlockListActivity.this,blocks);
                        recyclerView.setAdapter(adapter);

                    }
                });
            }
        }).start();
    }
    void deleteBlockUser(String blockId)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

                String userId = pref.getString("USER_ID", "Guest");

                Boolean ret = HttpClient.deleteSpaceBlockUser(new OkHttpClient(),userId, blockId);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (ret == false) {
                            Toast.makeText(BlockListActivity.this, "차단 해제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(BlockListActivity.this, "차단 해제에 성공했습니다.", Toast.LENGTH_SHORT).show();

                        getBlockUser();
                    }
                });
            }
        }).start();
    }
    public class BlockAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
        Context context;
        ArrayList<BlockUserVO> arrayList;


        public BlockAdapter(Context context,ArrayList<BlockUserVO> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


            View v = LayoutInflater.from(context).inflate(R.layout.row_block, parent, false);


            //block_bt
            return new BlockViewHolder(v);
        }


        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
        {
            BlockViewHolder viewHolder = (BlockViewHolder) holder;

            BlockUserVO data = arrayList.get(position);


            viewHolder.name.setText(data.getBlockName());

            String strPhoto = data.getBlockPhoto();

            if (!strPhoto.startsWith("http"))
                strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

            Glide.with(BlockListActivity.this)
                    .asBitmap()
                    .placeholder(R.drawable.user_icon)
                    .load(strPhoto)
                    .apply(new RequestOptions().circleCrop())
                    .into(viewHolder.photo);
           //
        }



        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }

    public interface ItemClickListener{
        void onItemClickListener(View v, int position);
    }
    public class BlockViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;

        ImageView photo;
        ItemClickListener itemClickListener;


        public BlockViewHolder(@NonNull View itemView) {

            super(itemView);
            name = itemView.findViewById(R.id.name);
            photo = itemView.findViewById(R.id.photo);
            Button bt = itemView.findViewById(R.id.block_bt);

            itemView.setOnClickListener(this);

            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getLayoutPosition();
                    BlockUserVO block = blocks.get(pos);
                    deleteBlockUser(block.getBlockId());

                }
            });
        }
        @Override
        public void onClick(View v){
            // this.itemClickListener.onItemClickListener(v, getLayoutPosition());

            int pos = getLayoutPosition();

            BlockUserVO block = blocks.get(pos);
            deleteBlockUser(block.getBlockId());



        }



    }
}