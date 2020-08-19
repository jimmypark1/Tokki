package com.Whowant.Tokki.UI.Fragment.Main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.DrawerMenu.ContestEventActivity;
import com.Whowant.Tokki.UI.Popup.InteractionSpeakerSelectActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.EventVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class EventPopupFragment extends Fragment {
    private EventVO eventVO;

    public EventPopupFragment(EventVO vo) {
        eventVO = vo;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.event_popup_row, container, false);
        ImageView imgView = inflaterView.findViewById(R.id.eventImgView);
        imgView.setClipToOutline(true);

        String strImg = eventVO.getStrEventPopupFile();
        if(!strImg.startsWith("http"))
            strImg = CommonUtils.strDefaultUrl + "images/" + strImg;

        Glide.with(getActivity())
                .asBitmap() // some .jpeg files are actually gif
                .load(strImg)
                .into(imgView);

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ContestEventActivity.class);
                intent.putExtra("IMG_URL", eventVO.getStrEventContentsFile());
                intent.putExtra("EVENT_TITLE", eventVO.getStrEventTitle());
                getActivity().startActivity(intent);
            }
        });

//        if(strEventID.equals("EVENT_1"))
//            imgView.setImageResource(R.drawable.event2);
//        else
//            imgView.setImageResource(R.drawable.event1);

        return inflaterView;
    }
}