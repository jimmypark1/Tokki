package com.Whowant.Tokki.UI.Activity.Work;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.webkit.WebViewClient;
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

    public RelativeLayout topMenu;
    Animation translateDown,translateUp;
    Animation translateTopDown,translateTopUp;

    private GestureDetector mDetector;

    public String workId;


    RelativeLayout starPointLayout;


    ImageView backCk0,backCk1,backCk2,backCk3,backCk4,backCk5;
    ImageView fontCk0,fontCk1,fontCk2,fontCk3;

    int nBack = 0;
    int nFont = 0;

    int fontSizeStep = 0;
    float defaultFontSize = 18.0f;

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

    public void showMenu(Boolean show, Boolean isAnim, Boolean isCover )
    {
        translateDown = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_bottom_down);
        translateUp = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_bottom_up);

        translateTopDown = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_down);
        translateTopUp = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_up);

        if(isCover)
        {
            topMenu.setVisibility(View.INVISIBLE);
        }

        if(show) {
            if(isAnim) {
                bottomMenu.startAnimation(translateUp);
                if(isCover == false)
                    topMenu.startAnimation(translateTopDown);

            }
            bottomMenu.setVisibility(View.VISIBLE);
            if(isCover == false)
                topMenu.setVisibility(View.VISIBLE);


/*
            WebWorkViewerActivity parent = (WebWorkViewerActivity) getActivity();
            parent.showNav(show);
*/
        }
        else {
            if(isAnim)
            {
                bottomMenu.startAnimation(translateDown);
                if(isCover == false)
                    topMenu.startAnimation(translateTopUp);

            }

            bottomMenu.setVisibility(View.INVISIBLE);
            if(isCover == false)
                topMenu.setVisibility(View.INVISIBLE);

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
    private int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    void setBack0()
    {
        backCk0.setVisibility(View.VISIBLE);
        backCk1.setVisibility(View.INVISIBLE);
        backCk2.setVisibility(View.INVISIBLE);
        backCk3.setVisibility(View.INVISIBLE);
        backCk4.setVisibility(View.INVISIBLE);
        backCk5.setVisibility(View.INVISIBLE);

        nBack = 0;
        String html =  webWork.getRaw();
        String fontSize = "body{ font-size:" + String.valueOf((int)defaultFontSize) + "px";

        String font= "  @font-face {\n" +
                "        font-family: \"NanumSquareOTFR\";\n" +
                "        src: url(\"file:///android_res/font/nn_square_font.ttf\")"  +
                "    }" +
                "  @font-face {\n" +
                "        font-family: \"NanumMyeongjoOTF\";\n" +
                "        src: url(\"file:///android_res/font/nnmyeongjo_font.ttf\")"  +
                "    }"+
                "  @font-face {\n" +
                "        font-family: \"NanumSquareRoundOTFR\";\n" +
                "        src: url(\"file:///android_res/font/nnsquare_round_font.ttf\")"  +
                "    }"+
                "  @font-face {\n" +
                "        font-family: \"SDMiSaeng\";\n" +
                "        src: url(\"file:///android_res/font/misaeng_font.ttf\")"  +
                "    }";

        String fontFamily = "";
        if(nFont == 0)
        {
            fontFamily =  "body{ font-family:NanumSquareOTFR }";

        }
        else if(nFont == 1)
        {
            fontFamily =  "body{ font-family:NanumMyeongjoOTF }";

        }
        else if(nFont == 2)
        {
            fontFamily =  "body{ font-family:NanumSquareRoundOTFR }";

        }
        else if(nFont == 3)
        {
            fontFamily =  "body{ font-family:SDMiSaeng }";

        }

        String text = "<html><head>"
                + "<style type=\"text/css\">body{ background-color:#ffffff; color: black }"
                + font
                + fontFamily
                + fontSize
                + "</style></head>"
                + "<body>"
                + webWork.getRaw()
                + "</body></html>";

        //   webview.setBackgroundColor(Color.parseColor("#404040"));

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        //    webview.loadData(webWork.getRaw(), "text/html; charset=utf-8", "UTF-8");
        webview.loadDataWithBaseURL("file:///android_asset/",text,"text/html","utf-8",null);

    }
    void setBack1()
    {
        backCk0.setVisibility(View.INVISIBLE);
        backCk1.setVisibility(View.VISIBLE);
        backCk2.setVisibility(View.INVISIBLE);
        backCk3.setVisibility(View.INVISIBLE);
        backCk4.setVisibility(View.INVISIBLE);
        backCk5.setVisibility(View.INVISIBLE);

        String fontSize = "body{ font-size:" + String.valueOf((int)defaultFontSize) + "px";

        nBack = 1;

        String font= "  @font-face {\n" +
                "        font-family: \"NanumSquareOTFR\";\n" +
                "        src: url(\"file:///android_res/font/nn_square_font.ttf\")"  +
                "    }" +
                "  @font-face {\n" +
                "        font-family: \"NanumMyeongjoOTF\";\n" +
                "        src: url(\"file:///android_res/font/nnmyeongjo_font.ttf\")"  +
                "    }"+
                "  @font-face {\n" +
                "        font-family: \"NanumSquareRoundOTFR\";\n" +
                "        src: url(\"file:///android_res/font/nnsquare_round_font.ttf\")"  +
                "    }"+
                "  @font-face {\n" +
                "        font-family: \"SDMiSaeng\";\n" +
                "        src: url(\"file:///android_res/font/misaeng_font.ttf\")"  +
                "    }";

        String fontFamily = "";
        if(nFont == 0)
        {
            fontFamily =  "body{ font-family:NanumSquareOTFR }";

        }
        else if(nFont == 1)
        {
            fontFamily =  "body{ font-family:NanumMyeongjoOTF }";

        }
        else if(nFont == 2)
        {
            fontFamily =  "body{ font-family:NanumSquareRoundOTFR }";

        }
        else if(nFont == 3)
        {
            fontFamily =  "body{ font-family:SDMiSaeng }";

        }
        String text = "<html><head>"
                + "<style type=\"text/css\">body{ background-color:#efeff4; color: black }"
                + font+ fontFamily
                + fontSize
                + "</style></head>"
                + "<body>"
                + webWork.getRaw()
                + "</body></html>";

        //   webview.setBackgroundColor(Color.parseColor("#404040"));

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        //    webview.loadData(webWork.getRaw(), "text/html; charset=utf-8", "UTF-8");
        webview.loadDataWithBaseURL("file:///android_asset/",text,"text/html","utf-8",null);


    }
    void setBack2()
    {
        backCk0.setVisibility(View.INVISIBLE);
        backCk1.setVisibility(View.INVISIBLE);
        backCk2.setVisibility(View.VISIBLE);
        backCk3.setVisibility(View.INVISIBLE);
        backCk4.setVisibility(View.INVISIBLE);
        backCk5.setVisibility(View.INVISIBLE);
        String fontSize = "body{ font-size:" + String.valueOf((int)defaultFontSize) + "px";

        nBack = 2;
        String font= "  @font-face {\n" +
                "        font-family: \"NanumSquareOTFR\";\n" +
                "        src: url(\"file:///android_res/font/nn_square_font.ttf\")"  +
                "    }" +
                "  @font-face {\n" +
                "        font-family: \"NanumMyeongjoOTF\";\n" +
                "        src: url(\"file:///android_res/font/nnmyeongjo_font.ttf\")"  +
                "    }"+
                "  @font-face {\n" +
                "        font-family: \"NanumSquareRoundOTFR\";\n" +
                "        src: url(\"file:///android_res/font/nnsquare_round_font.ttf\")"  +
                "    }"+
                "  @font-face {\n" +
                "        font-family: \"SDMiSaeng\";\n" +
                "        src: url(\"file:///android_res/font/misaeng_font.ttf\")"  +
                "    }";

        String fontFamily = "";
        if(nFont == 0)
        {
            fontFamily =  "body{ font-family:NanumSquareOTFR }";

        }
        else if(nFont == 1)
        {
            fontFamily =  "body{ font-family:NanumMyeongjoOTF }";

        }
        else if(nFont == 2)
        {
            fontFamily =  "body{ font-family:NanumSquareRoundOTFR }";

        }
        else if(nFont == 3)
        {
            fontFamily =  "body{ font-family:SDMiSaeng }";

        }
        String text = "<html><head>"
                + "<style type=\"text/css\">body{ background-color:#fcf3de; color: black }"
                + font
                + fontFamily
                + fontSize
                + "</style></head>"
                + "<body>"
                + webWork.getRaw()
                + "</body></html>";

        //   webview.setBackgroundColor(Color.parseColor("#404040"));

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        //    webview.loadData(webWork.getRaw(), "text/html; charset=utf-8", "UTF-8");
        webview.loadDataWithBaseURL("file:///android_asset/",text,"text/html","utf-8",null);

    }
    void setBack3()
    {
        backCk0.setVisibility(View.INVISIBLE);
        backCk1.setVisibility(View.INVISIBLE);
        backCk2.setVisibility(View.INVISIBLE);
        backCk3.setVisibility(View.VISIBLE);
        backCk4.setVisibility(View.INVISIBLE);
        backCk5.setVisibility(View.INVISIBLE);

        nBack = 3;
        String fontSize = "body{ font-size:" + String.valueOf((int)defaultFontSize) + "px";

        String font= "  @font-face {\n" +
                "        font-family: \"NanumSquareOTFR\";\n" +
                "        src: url(\"file:///android_res/font/nn_square_font.ttf\")"  +
                "    }" +
                "  @font-face {\n" +
                "        font-family: \"NanumMyeongjoOTF\";\n" +
                "        src: url(\"file:///android_res/font/nnmyeongjo_font.ttf\")"  +
                "    }"+
                "  @font-face {\n" +
                "        font-family: \"NanumSquareRoundOTFR\";\n" +
                "        src: url(\"file:///android_res/font/nnsquare_round_font.ttf\")"  +
                "    }"+
                "  @font-face {\n" +
                "        font-family: \"SDMiSaeng\";\n" +
                "        src: url(\"file:///android_res/font/misaeng_font.ttf\")"  +
                "    }";

        String fontFamily = "";
        if(nFont == 0)
        {
            fontFamily =  "body{ font-family:NanumSquareOTFR }";

        }
        else if(nFont == 1)
        {
            fontFamily =  "body{ font-family:NanumMyeongjoOTF }";

        }
        else if(nFont == 2)
        {
            fontFamily =  "body{ font-family:NanumSquareRoundOTFR }";

        }
        else if(nFont == 3)
        {
            fontFamily =  "body{ font-family:SDMiSaeng }";

        }
        String text = "<html><head>"
                + "<style type=\"text/css\">body{ background-color:#edfdee; color: black }"
                + font
                + fontFamily
                + fontSize
                + "</style></head>"
                + "<body>"
                + webWork.getRaw()
                + "</body></html>";

        //   webview.setBackgroundColor(Color.parseColor("#404040"));

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        //    webview.loadData(webWork.getRaw(), "text/html; charset=utf-8", "UTF-8");
        webview.loadDataWithBaseURL("file:///android_asset/",text,"text/html","utf-8",null);

    }
    void setBack4()
    {
        backCk0.setVisibility(View.INVISIBLE);
        backCk1.setVisibility(View.INVISIBLE);
        backCk2.setVisibility(View.INVISIBLE);
        backCk3.setVisibility(View.INVISIBLE);
        backCk4.setVisibility(View.VISIBLE);
        backCk5.setVisibility(View.INVISIBLE);

        nBack = 4;
        String fontSize = "body{ font-size:" + String.valueOf((int)defaultFontSize) + "px";

        String font= "  @font-face {\n" +
                "        font-family: \"NanumSquareOTFR\";\n" +
                "        src: url(\"file:///android_res/font/nn_square_font.ttf\")"  +
                "    }" +
                "  @font-face {\n" +
                "        font-family: \"NanumMyeongjoOTF\";\n" +
                "        src: url(\"file:///android_res/font/nnmyeongjo_font.ttf\")"  +
                "    }"+
                "  @font-face {\n" +
                "        font-family: \"NanumSquareRoundOTFR\";\n" +
                "        src: url(\"file:///android_res/font/nnsquare_round_font.ttf\")"  +
                "    }"+
                "  @font-face {\n" +
                "        font-family: \"SDMiSaeng\";\n" +
                "        src: url(\"file:///android_res/font/misaeng_font.ttf\")"  +
                "    }";

        String fontFamily = "";
        if(nFont == 0)
        {
            fontFamily =  "body{ font-family:NanumSquareOTFR }";

        }
        else if(nFont == 1)
        {
            fontFamily =  "body{ font-family:NanumMyeongjoOTF }";

        }
        else if(nFont == 2)
        {
            fontFamily =  "body{ font-family:NanumSquareRoundOTFR }";

        }
        else if(nFont == 3)
        {
            fontFamily =  "body{ font-family:SDMiSaeng }";

        }
        String text = "<html><head>"
                + "<style type=\"text/css\">body{ background-color:#404040; color: white }"
                + font
                + fontFamily
                + fontSize
                + "</style></head>"
                + "<body>"
                + webWork.getRaw()
                + "</body></html>";

     //   webview.setBackgroundColor(Color.parseColor("#404040"));

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
    //    webview.loadData(webWork.getRaw(), "text/html; charset=utf-8", "UTF-8");
        webview.loadDataWithBaseURL("file:///android_asset/",text,"text/html","utf-8",null);
        /*
        webview.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                view.loadUrl(
                        "javascript:document.body.style.setProperty(\"color\", \"white\");"
                );
            }
        });


         */

    }
    void setBack5()
    {
        backCk0.setVisibility(View.INVISIBLE);
        backCk1.setVisibility(View.INVISIBLE);
        backCk2.setVisibility(View.INVISIBLE);
        backCk3.setVisibility(View.INVISIBLE);
        backCk4.setVisibility(View.INVISIBLE);
        backCk5.setVisibility(View.VISIBLE);

        String fontSize = "body{ font-size:" + String.valueOf((int)defaultFontSize) + "px";

        nBack = 5;
        String font= "  @font-face {\n" +
                "        font-family: \"NanumSquareOTFR\";\n" +
                "        src: url(\"file:///android_res/font/nn_square_font.ttf\")"  +
                "    }" +
                "  @font-face {\n" +
                "        font-family: \"NanumMyeongjoOTF\";\n" +
                "        src: url(\"file:///android_res/font/nnmyeongjo_font.ttf\")"  +
                "    }"+
                "  @font-face {\n" +
                "        font-family: \"NanumSquareRoundOTFR\";\n" +
                "        src: url(\"file:///android_res/font/nnsquare_round_font.ttf\")"  +
                "    }"+
                "  @font-face {\n" +
                "        font-family: \"SDMiSaeng\";\n" +
                "        src: url(\"file:///android_res/font/misaeng_font.ttf\")"  +
                "    }";

        String fontFamily = "";
        if(nFont == 0)
        {
            fontFamily =  "body{ font-family:NanumSquareOTFR }";

        }
        else if(nFont == 1)
        {
            fontFamily =  "body{ font-family:NanumMyeongjoOTF }";

        }
        else if(nFont == 2)
        {
            fontFamily =  "body{ font-family:NanumSquareRoundOTFR }";

        }
        else if(nFont == 3)
        {
            fontFamily =  "body{ font-family:SDMiSaeng }";

        }
        String text = "<html><head>"
                + "<style type=\"text/css\">body{ background-color:#191919; color: white }"
                + font
                + fontFamily
                + fontSize
                + "</style></head>"
                + "<body>"
                + webWork.getRaw()
                + "</body></html>";

        //   webview.setBackgroundColor(Color.parseColor("#404040"));

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        //    webview.loadData(webWork.getRaw(), "text/html; charset=utf-8", "UTF-8");
        webview.loadDataWithBaseURL("file:///android_asset/",text,"text/html","utf-8",null);

    }
    void setFont0()
    {
        fontCk0.setVisibility(View.VISIBLE);
        fontCk1.setVisibility(View.INVISIBLE);
        fontCk2.setVisibility(View.INVISIBLE);
        fontCk3.setVisibility(View.INVISIBLE);
        nFont = 0;
/*
        @font-face
        {
            font-family: 'SDMiSaeng';
            src: local('SDMiSaeng'),url('SDMiSaeng.ttf') format('truetype');
        }
        @font-face
        {
            font-family: 'NanumSquareRoundR';
            src: local('NanumSquareRoundR'),url('NanumSquareRoundR.ttf') format('truetype');
        }
        @font-face
        {
            font-family: 'AppleMyungjo';
            src: local('AppleMyungjo'),url('AppleMyungjo.ttf') format('truetype');
        }
        @font-face
        {
            font-family: 'AppleSDGothicNeoL00';
            src: local('AppleSDGothicNeoL00'),url('AppleSDGothicNeoL.ttf') format('truetype');
        }

        body
        {
            font-size: 56px;
            font-weight: normal;

        }

 */
        String fontSize = "body{ font-size:" + String.valueOf((int)defaultFontSize) + "px";

        String font= "  @font-face {\n" +
                "        font-family: \"NanumSquareOTFR\";\n" +
                "        src: url(\"file:///android_res/font/nn_square_font.ttf\")"  +
                "    }" +
                "  @font-face {\n" +
                "        font-family: \"NanumMyeongjoOTF\";\n" +
                "        src: url(\"file:///android_res/font/nnmyeongjo_font.ttf\")"  +
                "    }"+
                "  @font-face {\n" +
                "        font-family: \"NanumSquareRoundOTFR\";\n" +
                "        src: url(\"file:///android_res/font/nnsquare_round_font.ttf\")"  +
                "    }"+
                "  @font-face {\n" +
                "        font-family: \"SDMiSaeng\";\n" +
                "        src: url(\"file:///android_res/font/misaeng_font.ttf\")"  +
                "    }";

        String back = "";
        if(nBack == 0)
        {
            back = "body{ background-color:#ffffff ;color:black}";

        }
        else if(nBack == 1)
        {
            back = "body{ background-color:#efeff4 ;color:black}";

        }
        else if(nBack == 2)
        {
            back = "body{ background-color:#fcf3de ;color:black}";

        }
        else if(nBack == 3)
        {
            back = "body{ background-color:#edfdee ;color:black}";

        }
        else if(nBack == 4)
        {
            back = "body{ background-color:#404040 ;color:white}";

        }
        else if(nBack == 5)
        {
            back = "body{ background-color:#191919 ;color:white}";

        }

        String text = "<html><head>"
                + "<style type=\"text/css\">"
                + font
                + "body{ font-family:NanumSquareOTFR }"
                + back
                + fontSize
                + "</style></head>"
                + "<body>"
                + webWork.getRaw()
                + "</body></html>";

        //   webview.setBackgroundColor(Color.parseColor("#404040"));

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        //    webview.loadData(webWork.getRaw(), "text/html; charset=utf-8", "UTF-8");
        webview.loadDataWithBaseURL("file:///android_asset/",text,"text/html","utf-8",null);


    }
    void setFont1()
    {
        fontCk0.setVisibility(View.INVISIBLE);
        fontCk1.setVisibility(View.VISIBLE);
        fontCk2.setVisibility(View.INVISIBLE);
        fontCk3.setVisibility(View.INVISIBLE);

        nFont = 1;

        String fontSize = "body{ font-size:" + String.valueOf((int)defaultFontSize) + "px";

        String font= "  @font-face {\n" +
                "        font-family: \"NanumSquareOTFR\";\n" +
                "        src: url(\"file:///android_res/font/nn_square_font.ttf\")"  +
                "    }" +
                "  @font-face {\n" +
                "        font-family: \"NanumMyeongjoOTF\";\n" +
                "        src: url(\"file:///android_res/font/nnmyeongjo_font.ttf\")"  +
                "    }"+
                "  @font-face {\n" +
                "        font-family: \"NanumSquareRoundOTFR\";\n" +
                "        src: url(\"file:///android_res/font/nnsquare_round_font.ttf\")"  +
                "    }"+
                "  @font-face {\n" +
                "        font-family: \"SDMiSaeng\";\n" +
                "        src: url(\"file:///android_res/font/misaeng_font.ttf\")"  +
                "    }";
        String back = "";
        if(nBack == 0)
        {
            back = "body{ background-color:#ffffff ;color:black}";

        }
        else if(nBack == 1)
        {
            back = "body{ background-color:#efeff4 ;color:black}";

        }
        else if(nBack == 2)
        {
            back = "body{ background-color:#fcf3de ;color:black}";

        }
        else if(nBack == 3)
        {
            back = "body{ background-color:#edfdee ;color:black}";

        }
        else if(nBack == 4)
        {
            back = "body{ background-color:#404040 ;color:white}";

        }
        else if(nBack == 5)
        {
            back = "body{ background-color:#191919 ;color:white}";

        }

        String text = "<html><head>"
                + "<style type=\"text/css\">"
                + font
                + "body{ font-family:NanumMyeongjoOTF }"
                + back
                + fontSize
                + "</style></head>"
                + "<body>"
                + webWork.getRaw()
                + "</body></html>";

        //   webview.setBackgroundColor(Color.parseColor("#404040"));

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        //    webview.loadData(webWork.getRaw(), "text/html; charset=utf-8", "UTF-8");
        webview.loadDataWithBaseURL("file:///android_asset/",text,"text/html","utf-8",null);


    }
    void setFont2()
    {
        fontCk0.setVisibility(View.INVISIBLE);
        fontCk1.setVisibility(View.INVISIBLE);
        fontCk2.setVisibility(View.VISIBLE);
        fontCk3.setVisibility(View.INVISIBLE);
        nFont = 2;
        String fontSize = "body{ font-size:" + String.valueOf((int)defaultFontSize) + "px";

        String font= "  @font-face {\n" +
                "        font-family: \"NanumSquareOTFR\";\n" +
                "        src: url(\"file:///android_res/font/nn_square_font.ttf\")"  +
                "    }" +
                "  @font-face {\n" +
                "        font-family: \"NanumMyeongjoOTF\";\n" +
                "        src: url(\"file:///android_res/font/nnmyeongjo_font.ttf\")"  +
                "    }"+
                "  @font-face {\n" +
                "        font-family: \"NanumSquareRoundOTFR\";\n" +
                "        src: url(\"file:///android_res/font/nnsquare_round_font.ttf\")"  +
                "    }"+
                "  @font-face {\n" +
                "        font-family: \"SDMiSaeng\";\n" +
                "        src: url(\"file:///android_res/font/misaeng_font.ttf\")"  +
                "    }";

        String back = "";
        if(nBack == 0)
        {
            back = "body{ background-color:#ffffff ;color:black}";

        }
        else if(nBack == 1)
        {
            back = "body{ background-color:#efeff4 ;color:black}";

        }
        else if(nBack == 2)
        {
            back = "body{ background-color:#fcf3de ;color:black}";

        }
        else if(nBack == 3)
        {
            back = "body{ background-color:#edfdee ;color:black}";

        }
        else if(nBack == 4)
        {
            back = "body{ background-color:#404040 ;color:white}";

        }
        else if(nBack == 5)
        {
            back = "body{ background-color:#191919 ;color:white}";

        }

        String text = "<html><head>"
                + "<style type=\"text/css\">"
                + font
                + back
                + fontSize
                + "body{ font-family:NanumSquareRoundOTFR }"
                + "</style></head>"
                + "<body>"
                + webWork.getRaw()
                + "</body></html>";

        //   webview.setBackgroundColor(Color.parseColor("#404040"));

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        //    webview.loadData(webWork.getRaw(), "text/html; charset=utf-8", "UTF-8");
        webview.loadDataWithBaseURL("file:///android_asset/",text,"text/html","utf-8",null);

    }
    void setFont3()
    {
        fontCk0.setVisibility(View.INVISIBLE);
        fontCk1.setVisibility(View.INVISIBLE);
        fontCk2.setVisibility(View.INVISIBLE);
        fontCk3.setVisibility(View.VISIBLE);

        nFont = 3;

        String fontSize = "body{ font-size:" + String.valueOf((int)defaultFontSize) + "px";

        String font= "  @font-face {\n" +
                "        font-family: \"NanumSquareOTFR\";\n" +
                "        src: url(\"file:///android_res/font/nn_square_font.ttf\")"  +
                "    }" +
                "  @font-face {\n" +
                "        font-family: \"NanumMyeongjoOTF\";\n" +
                "        src: url(\"file:///android_res/font/nnmyeongjo_font.ttf\")"  +
                "    }"+
                "  @font-face {\n" +
                "        font-family: \"NanumSquareRoundOTFR\";\n" +
                "        src: url(\"file:///android_res/font/nnsquare_round_font.ttf\")"  +
                "    }"+
                "  @font-face {\n" +
                "        font-family: \"SDMiSaeng\";\n" +
                "        src: url(\"file:///android_res/font/misaeng_font.ttf\")"  +
                "    }";

        String back = "";
        if(nBack == 0)
        {
            back = "body{ background-color:#ffffff ;color:black}";

        }
        else if(nBack == 1)
        {
            back = "body{ background-color:#efeff4 ;color:black}";

        }
        else if(nBack == 2)
        {
            back = "body{ background-color:#fcf3de ;color:black}";

        }
        else if(nBack == 3)
        {
            back = "body{ background-color:#edfdee ;color:black}";

        }
        else if(nBack == 4)
        {
            back = "body{ background-color:#404040 ;color:white}";

        }
        else if(nBack == 5)
        {
            back = "body{ background-color:#191919 ;color:white}";

        }

        String text = "<html><head>"
                + "<style type=\"text/css\">"
                + font
                + "body{ font-family:SDMiSaeng }"
                + back
                + fontSize
                + "</style></head>"
                + "<body>"
                + webWork.getRaw()
                + "</body></html>";

        //   webview.setBackgroundColor(Color.parseColor("#404040"));

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        //    webview.loadData(webWork.getRaw(), "text/html; charset=utf-8", "UTF-8");
        webview.loadDataWithBaseURL("file:///android_asset/",text,"text/html","utf-8",null);

    }

    void setIncreaseFont()
    {

        fontSizeStep ++;
        if(fontSizeStep > 4)
        {
            fontSizeStep = 4;
        }
        else
        {
            defaultFontSize =   (1+ 0.2f * fontSizeStep) * defaultFontSize;

        }



         String fontSize = "body{ font-size:" + String.valueOf((int)defaultFontSize) + "px";

        String font= "  @font-face {\n" +
                "        font-family: \"NanumSquareOTFR\";\n" +
                "        src: url(\"file:///android_res/font/nn_square_font.ttf\")"  +
                "    }" +
                "  @font-face {\n" +
                "        font-family: \"NanumMyeongjoOTF\";\n" +
                "        src: url(\"file:///android_res/font/nnmyeongjo_font.ttf\")"  +
                "    }"+
                "  @font-face {\n" +
                "        font-family: \"NanumSquareRoundOTFR\";\n" +
                "        src: url(\"file:///android_res/font/nnsquare_round_font.ttf\")"  +
                "    }"+
                "  @font-face {\n" +
                "        font-family: \"SDMiSaeng\";\n" +
                "        src: url(\"file:///android_res/font/misaeng_font.ttf\")"  +
                "    }";

        String fontFamily = "";
        if(nFont == 0)
        {
            fontFamily =  "body{ font-family:NanumSquareOTFR }";

        }
        else if(nFont == 1)
        {
            fontFamily =  "body{ font-family:NanumMyeongjoOTF }";

        }
        else if(nFont == 2)
        {
            fontFamily =  "body{ font-family:NanumSquareRoundOTFR }";

        }
        else if(nFont == 3)
        {
            fontFamily =  "body{ font-family:SDMiSaeng }";

        }
        String back = "";
        if(nBack == 0)
        {
            back = "body{ background-color:#ffffff ;color:black}";

        }
        else if(nBack == 1)
        {
            back = "body{ background-color:#efeff4 ;color:black}";

        }
        else if(nBack == 2)
        {
            back = "body{ background-color:#fcf3de ;color:black}";

        }
        else if(nBack == 3)
        {
            back = "body{ background-color:#edfdee ;color:black}";

        }
        else if(nBack == 4)
        {
            back = "body{ background-color:#404040 ;color:white}";

        }
        else if(nBack == 5)
        {
            back = "body{ background-color:#191919 ;color:white}";

        }

        String text = "<html><head>"
                + "<style type=\"text/css\">"
                + font
                + fontFamily
                + back
                + fontSize
                + "</style></head>"
                + "<body>"
                + webWork.getRaw()
                + "</body></html>";

        //   webview.setBackgroundColor(Color.parseColor("#404040"));

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        //    webview.loadData(webWork.getRaw(), "text/html; charset=utf-8", "UTF-8");
        webview.loadDataWithBaseURL("file:///android_asset/",text,"text/html","utf-8",null);

    }
    void setDecreaseFont()
    {
        fontSizeStep --;
        if(fontSizeStep < 0)
        {
            fontSizeStep = 0;
        }
        defaultFontSize =   (1- 0.2f * fontSizeStep) * defaultFontSize;

        //  defaultFontSize =  defaultFontSize + (0.02f * (float)fontSizeStep) * defaultFontSize;
        String fontSize = "body{ font-size:" + String.valueOf((int)defaultFontSize) + "px";

        String font= "  @font-face {\n" +
                "        font-family: \"NanumSquareOTFR\";\n" +
                "        src: url(\"file:///android_res/font/nn_square_font.ttf\")"  +
                "    }" +
                "  @font-face {\n" +
                "        font-family: \"NanumMyeongjoOTF\";\n" +
                "        src: url(\"file:///android_res/font/nnmyeongjo_font.ttf\")"  +
                "    }"+
                "  @font-face {\n" +
                "        font-family: \"NanumSquareRoundOTFR\";\n" +
                "        src: url(\"file:///android_res/font/nnsquare_round_font.ttf\")"  +
                "    }"+
                "  @font-face {\n" +
                "        font-family: \"SDMiSaeng\";\n" +
                "        src: url(\"file:///android_res/font/misaeng_font.ttf\")"  +
                "    }";

        String fontFamily = "";
        if(nFont == 0)
        {
            fontFamily =  "body{ font-family:NanumSquareOTFR }";

        }
        else if(nFont == 1)
        {
            fontFamily =  "body{ font-family:NanumMyeongjoOTF }";

        }
        else if(nFont == 2)
        {
            fontFamily =  "body{ font-family:NanumSquareRoundOTFR }";

        }
        else if(nFont == 3)
        {
            fontFamily =  "body{ font-family:SDMiSaeng }";

        }
        String back = "";
        if(nBack == 0)
        {
            back = "body{ background-color:#ffffff ;color:black}";

        }
        else if(nBack == 1)
        {
            back = "body{ background-color:#efeff4 ;color:black}";

        }
        else if(nBack == 2)
        {
            back = "body{ background-color:#fcf3de ;color:black}";

        }
        else if(nBack == 3)
        {
            back = "body{ background-color:#edfdee ;color:black}";

        }
        else if(nBack == 4)
        {
            back = "body{ background-color:#404040 ;color:white}";

        }
        else if(nBack == 5)
        {
            back = "body{ background-color:#191919 ;color:white}";

        }

        String text = "<html><head>"
                + "<style type=\"text/css\">"
                + font
                + fontFamily
                + back
                + fontSize
                + "</style></head>"
                + "<body>"
                + webWork.getRaw()
                + "</body></html>";

        //   webview.setBackgroundColor(Color.parseColor("#404040"));

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        //    webview.loadData(webWork.getRaw(), "text/html; charset=utf-8", "UTF-8");
        webview.loadDataWithBaseURL("file:///android_asset/",text,"text/html","utf-8",null);

    }
    public void onClickDecreaseFont(View view) {

        WebWorkViewerActivity parent =  (WebWorkViewerActivity)getActivity();
        parent.onClickDecreaseFont(view);


    }
    public void onClickIncreaseFont(View view) {

        WebWorkViewerActivity parent =  (WebWorkViewerActivity)getActivity();
        parent.onClickIncreaseFont(view);


    }
    public void onClickBack0(View view) {

        WebWorkViewerActivity parent =  (WebWorkViewerActivity)getActivity();
        parent.onClickBack0(view);


    }
    public void onClickBack1(View view) {

        WebWorkViewerActivity parent =  (WebWorkViewerActivity)getActivity();
        parent.onClickBack1(view);

    }
    public void onClickBack2(View view) {

        WebWorkViewerActivity parent =  (WebWorkViewerActivity)getActivity();
        parent.onClickBack2(view);

    }
    public void onClickBack3(View view) {

        WebWorkViewerActivity parent =  (WebWorkViewerActivity)getActivity();
        parent.onClickBack3(view);

    }
    public void onClickBack4(View view) {

        WebWorkViewerActivity parent =  (WebWorkViewerActivity)getActivity();
        parent.onClickBack4(view);

    }
    public void onClickBack5(View view) {

        WebWorkViewerActivity parent =  (WebWorkViewerActivity)getActivity();
        parent.onClickBack5(view);

    }

    public void onClickFont0(View view) {

        WebWorkViewerActivity parent =  (WebWorkViewerActivity)getActivity();
        parent.onClickFont0(view);

    }
    public void onClickFont1(View view) {

        WebWorkViewerActivity parent =  (WebWorkViewerActivity)getActivity();
        parent.onClickFont1(view);

    }
    public void onClickFont2(View view) {
        WebWorkViewerActivity parent =  (WebWorkViewerActivity)getActivity();
        parent.onClickFont2(view);


    }
    public void onClickFont3(View view) {

        WebWorkViewerActivity parent =  (WebWorkViewerActivity)getActivity();
        parent.onClickFont3(view);

    }
    void initTopUI()
    {

        backCk0.setVisibility(View.VISIBLE);
        backCk1.setVisibility(View.INVISIBLE);
        backCk1.setVisibility(View.INVISIBLE);
        backCk2.setVisibility(View.INVISIBLE);
        backCk3.setVisibility(View.INVISIBLE);
        backCk4.setVisibility(View.INVISIBLE);
        backCk5.setVisibility(View.INVISIBLE);

        fontCk0.setVisibility(View.VISIBLE);
        fontCk1.setVisibility(View.INVISIBLE);
        fontCk2.setVisibility(View.INVISIBLE);
        fontCk3.setVisibility(View.INVISIBLE);

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

        topMenu = v.findViewById(R.id.topMenu);

        carrot = v.findViewById(R.id.carrot);
        bottomMenu = v.findViewById(R.id.bottom);

        backCk0 =  v.findViewById(R.id.back_ck0);
        backCk1 =  v.findViewById(R.id.back_ck1);
        backCk2 =  v.findViewById(R.id.back_ck2);
        backCk3 =  v.findViewById(R.id.back_ck3);
        backCk4 =  v.findViewById(R.id.back_ck4);
        backCk5 =  v.findViewById(R.id.back_ck5);

        fontCk0 =  v.findViewById(R.id.font_ck0);
        fontCk1 =  v.findViewById(R.id.font_ck1);
        fontCk2 =  v.findViewById(R.id.font_ck2);
        fontCk3 =  v.findViewById(R.id.font_ck3);


        initTopUI();
        prev.setText("< 이전화");
            next.setText("다음화 >");

        initLayout();
        webview.loadData(webWork.getRaw(), "text/html; charset=utf-8", "UTF-8");

        webview.getSettings().setDomStorageEnabled(true);

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
          //      showMenu(false,true);
            }
            else
            {
              //  showMenu(true,true);

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