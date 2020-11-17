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
import com.Whowant.Tokki.UI.Activity.DrawerMenu.EventDetailActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.EventVO;
import com.bumptech.glide.Glide;

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
                if(eventVO.getnEventType() == 0) {               // 일반 이벤트
                    Intent intent = new Intent(getActivity(), EventDetailActivity.class);
                    intent.putExtra("EVENT_TITLE", eventVO.getStrEventTitle());
                    intent.putExtra("EVENT_FILE", eventVO.getStrEventContentsFile());
                    getActivity().startActivity(intent);
                } else if(eventVO.getnEventType() == 10) {           // 콘테스트 이벤트
                    Intent intent = new Intent(getActivity(), ContestEventActivity.class);
                    intent.putExtra("EVENT_TITLE", eventVO.getStrEventTitle());
                    intent.putExtra("IMG_URL", eventVO.getStrEventContentsFile());
                    getActivity().startActivity(intent);
                }

//                Intent intent = new Intent(getActivity(), ContestEventActivity.class);
//                intent.putExtra("IMG_URL", eventVO.getStrEventContentsFile());
//                intent.putExtra("EVENT_TITLE", eventVO.getStrEventTitle());
//                getActivity().startActivity(intent);
            }
        });

        return inflaterView;
    }
}