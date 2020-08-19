package com.Whowant.Tokki.UI.Fragment.Main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class RecommandCardFragment extends Fragment {
    private ImageView coverImgView;
    private WorkVO workVO;
    private Context mContext;

    public RecommandCardFragment(WorkVO workVo, Context context) {
        this.workVO = workVo;
        this.mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View inflaterView = inflater.inflate(R.layout.recommand_book_card, container, false);
        coverImgView = inflaterView.findViewById(R.id.coverImgView);

        String strImgUrl = workVO.getCoverFile();

        if(strImgUrl == null || strImgUrl.equals("null") || strImgUrl.equals("NULL") || strImgUrl.length() == 0) {
            Glide.with(mContext)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(R.drawable.no_poster_vertical)
                    .apply(new RequestOptions().override(800, 800))
                    .into(coverImgView);
            return inflaterView;
        } else if(!strImgUrl.startsWith("http")) {
            strImgUrl = CommonUtils.strDefaultUrl + "images/" + strImgUrl;
        }

        Glide.with(mContext)
                .asBitmap() // some .jpeg files are actually gif
                .load(strImgUrl)
                .apply(new RequestOptions().override(800, 800))
                .into(coverImgView);

        return inflaterView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
