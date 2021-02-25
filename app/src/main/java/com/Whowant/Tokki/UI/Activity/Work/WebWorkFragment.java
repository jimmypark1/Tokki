package com.Whowant.Tokki.UI.Activity.Work;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Login.PanbookLoginActivity;
import com.Whowant.Tokki.UI.Popup.CarrotDoneActivity;
import com.Whowant.Tokki.UI.Popup.StarPointPopup;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.WebWorkVO;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WebWorkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WebWorkFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WebWorkVO webWork;

    WebView webview;
    TextView prev;
    TextView next;

    ImageView comment;
    ImageView rate;
    ImageView carrot;

    RelativeLayout bottomMenu;
    Animation translateDown,translateUp;
    private GestureDetector mDetector;

    public String workId;


    RelativeLayout starPointLayout;

    public WebWorkFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WebWorkFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WebWorkFragment newInstance(String param1, String param2) {
        WebWorkFragment fragment = new WebWorkFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void showMenu(Boolean show, Boolean isAnim )
    {
        translateDown = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_bottom_down);
        translateUp = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_bottom_up);

        if(show) {
            if(isAnim)
                bottomMenu.startAnimation(translateUp);
            bottomMenu.setVisibility(View.VISIBLE);
/*
            WebWorkViewerActivity parent = (WebWorkViewerActivity) getActivity();
            parent.showNav(show);
*/
        }
        else {
            if(isAnim)
                bottomMenu.startAnimation(translateDown);

            bottomMenu.setVisibility(View.INVISIBLE);
/*
            WebWorkViewerActivity parent = (WebWorkViewerActivity) getActivity();
            parent.showNav(show);


 */

        }

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    int dpToPx(float dp)
    {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
    }
    void initLayout()
    {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        int offset = ( width -  dpToPx(20 * 2) - 2 * dpToPx(20) - 2 * dpToPx(47) - 3 * dpToPx(36)) / 5;


        ViewGroup.MarginLayoutParams lp0 = (ViewGroup.MarginLayoutParams) comment.getLayoutParams();
        ViewGroup.MarginLayoutParams lp1 = (ViewGroup.MarginLayoutParams) rate.getLayoutParams();
        ViewGroup.MarginLayoutParams lp2 = (ViewGroup.MarginLayoutParams) carrot.getLayoutParams();




        lp0.leftMargin = offset;
        lp1.leftMargin = offset;
        lp2.leftMargin = offset;


    }

    public void onClickPrev(View view) {


        WebWorkViewerActivity parent =  (WebWorkViewerActivity)getActivity();
         parent.onClickPrev(view);

    }
    public void onClickNext(View view) {

        WebWorkViewerActivity parent =  (WebWorkViewerActivity)getActivity();

        parent.onClickNext(view);

    }
    public void onClickComment(View view) {

        WebWorkViewerActivity parent =  (WebWorkViewerActivity)getActivity();

        parent.onClickComment(view);
    }

    public void onClickStar(View view) {

        WebWorkViewerActivity parent =  (WebWorkViewerActivity)getActivity();
        parent.getStarPoint();

    }

    public void onClickCarrotBtn(View view) {

        WebWorkViewerActivity parent =  (WebWorkViewerActivity)getActivity();
        parent.onClickCarrotBtn(view);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    //    webview = findViewById(R.id.webview);
    //    webview.getSettings().setJavaScriptEnabled(true);//자바스크립트 허용
         View v =  inflater.inflate(R.layout.fragment_web_work, container, false);

        webview = v.findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);//자바스크립트 허용
        prev = v.findViewById(R.id.prev);
        next = v.findViewById(R.id.next);
        comment = v.findViewById(R.id.comment);
        rate = v.findViewById(R.id.rate);

        carrot = v.findViewById(R.id.carrot);
        bottomMenu = v.findViewById(R.id.bottom);


            prev.setText("< 이전화");
            next.setText("다음화 >");

        initLayout();
        webview.loadData(webWork.getRaw(), "text/html; charset=utf-8", "UTF-8");


        mDetector = new GestureDetector(getActivity(), new MyGestureListener());
        webview.setLongClickable(false);
        webview.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                // ... Respond to touch events

             //   return mDetector.onTouchEvent(event);
                return webview.onTouchEvent(event) ||  mDetector.onTouchEvent(event);

            }
        });

        return v;
    }
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {

            // don't return false here or else none of the other
            // gestures will work
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            if(bottomMenu.getVisibility() == View.VISIBLE)
            {
                showMenu(false,true);
            }
            else
            {
                showMenu(true,true);

            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            return true;
        }
    }
}