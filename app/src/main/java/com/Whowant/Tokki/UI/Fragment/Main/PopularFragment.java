package com.Whowant.Tokki.UI.Fragment.Main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class PopularFragment extends Fragment {
    private WorkVO vo1;
    private WorkVO vo2;
    private Context mContext;

    public PopularFragment(WorkVO vo1, WorkVO vo2, Context context) {
        this.vo1 = vo1;
        this.vo2 = vo2;
        this.mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View inflaterView = inflater.inflate(R.layout.popular_fragment, container, false);

        ImageView coverImgView1 = inflaterView.findViewById(R.id.coverImgView);
//        TextView titleView1 = inflaterView.findViewById(R.id.titleView1);
//        TextView tapCountView1 = inflaterView.findViewById(R.id.tapCountView1);
//        TextView commentCountView1 = inflaterView.findViewById(R.id.commentCountView1);

//        ImageView coverImgView2 = inflaterView.findViewById(R.id.coverImgView2);
//        TextView titleView2 = inflaterView.findViewById(R.id.titleView2);
//        TextView tapCountView2 = inflaterView.findViewById(R.id.tapCountView2);
//        TextView commentCountView2 = inflaterView.findViewById(R.id.commentCountView2);

        Glide.with(mContext)
                .asBitmap() // some .jpeg files are actually gif
                .load(vo1.getCoverFile())
                .apply(new RequestOptions().override(800, 800))
                .into(coverImgView1);

//        titleView1.setText(vo1.getTitle());
//        tapCountView1.setText(vo1.getnTapCount() + "");
//        commentCountView1.setText(vo1.getnCommentCount() + "");

//        Glide.with(mContext)
//                .asBitmap() // some .jpeg files are actually gif
//                .load(vo2.getCoverFile())
//                .apply(new RequestOptions().override(800, 800))
//                .into(coverImgView2);
//
//        titleView2.setText(vo2.getTitle());
//        tapCountView2.setText(vo2.getnTapCount() + "");
//        commentCountView2.setText(vo2.getnCommentCount() + "");

        return inflaterView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
