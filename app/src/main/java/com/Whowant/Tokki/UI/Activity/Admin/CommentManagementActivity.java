package com.Whowant.Tokki.UI.Activity.Admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Work.ChatCommentActivity;
import com.Whowant.Tokki.UI.Activity.Work.EpisodeCommentActivity;
import com.Whowant.Tokki.UI.Popup.ReportPopup;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.CommentVO;
import com.Whowant.Tokki.VO.UserInfoVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class CommentManagementActivity extends AppCompatActivity {
    private ListView listView;
    private TextView emptyView;
    private ArrayList<CommentVO> reportsList;
    private CReportsArrayAdapter aa;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_management);

        pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

        listView = findViewById(R.id.listView);
        emptyView = findViewById(R.id.emptyView);

        reportsList = new ArrayList<>();
        aa = new CReportsArrayAdapter(this, R.layout.admin_member_row, reportsList);
        listView.setAdapter(aa);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                CommentVO vo = reportsList.get(position);
                Intent intent = new Intent(CommentManagementActivity.this, ReportDetailActivity.class);
                intent.putExtra("COMMENT_ID", vo.getCommentID());
                startActivity(intent);
            }
        });

        getReportsData();
    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }

    private void getReportsData() {
        reportsList.clear();
        CommonUtils.showProgressDialog(this, "서버와 통신중입니다. 잠시만 기다려주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<CommentVO> list = HttpClient.getReportsList(new OkHttpClient());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(list != null) {
                            reportsList.addAll(list);
                            aa.notifyDataSetChanged();

                            if(list.size() == 0) {
                                emptyView.setVisibility(View.VISIBLE);
                            }
                            CommonUtils.hideProgressDialog();
                        } else {
                            CommonUtils.hideProgressDialog();
                            Toast.makeText(CommentManagementActivity.this, "서버와의 통신에 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    public class CReportsArrayAdapter extends ArrayAdapter<Object>
    {
        private LayoutInflater mLiInflater;

        CReportsArrayAdapter(Context context, int layout, List titles)
        {
            super(context, layout, titles);
            mLiInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            if(convertView == null)
                convertView = mLiInflater.inflate(R.layout.comment_report_row, parent, false);

            CommentVO vo = reportsList.get(position);

            RelativeLayout bgView = convertView.findViewById(R.id.bgView);
            ImageView faceView = convertView.findViewById(R.id.faceView);
            TextView nameView = convertView.findViewById(R.id.nameView);
            TextView dateView = convertView.findViewById(R.id.dateTimeView);
            TextView commentView = convertView.findViewById(R.id.commentView);
            TextView reportView = convertView.findViewById(R.id.reportView);
            ImageView menuBtn = convertView.findViewById(R.id.menuBtn);

            String strPhoto = vo.getUserPhoto();
            if(strPhoto != null && !strPhoto.equals("null") && !strPhoto.equals("NULL") && strPhoto.length() > 0) {
                if(!strPhoto.startsWith("http"))
                    strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

                Glide.with(CommentManagementActivity.this)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(strPhoto)
                        .apply(new RequestOptions().circleCrop())
                        .into(faceView);
            } else {
                Glide.with(CommentManagementActivity.this)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(R.drawable.user_icon)
                        .apply(new RequestOptions().circleCrop())
                        .into(faceView);
            }

            nameView.setText(vo.getUserName());
            dateView.setText(CommonUtils.strGetTime(vo.getRegisterDate()));
            commentView.setText(vo.getStrComment());
            reportView.setText("신고개수  : " + vo.getnCount());
            menuBtn.setVisibility(View.VISIBLE);

            menuBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(CommentManagementActivity.this, view);
                    CommentVO vo = reportsList.get(position);

                    popup.getMenuInflater().inflate(R.menu.comment_manage_menu, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            Intent intent = null;

                            switch(item.getItemId()) {
                                case R.id.delete: {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(CommentManagementActivity.this);
                                    builder.setTitle("댓글 삭제 알림");
                                    builder.setMessage("댓글을 정말 삭제하시겠습니까?");
                                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            deleteComment(vo.getCommentID());
                                        }
                                    });

                                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {

                                        }
                                    });

                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                }
                                break;
                                case R.id.remove: {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(CommentManagementActivity.this);
                                    builder.setTitle("신고 철회 알림");
                                    builder.setMessage("댓글 신고를 철회하시겠습니까?");
                                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            removeReport(vo.getCommentID());
                                        }
                                    });

                                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                        }
                                    });

                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                }
                                break;
                            }
                            return true;
                        }
                    });

                    popup.show();//showing popup menu
                }
            });

            return convertView;
        }
    }

    private void deleteComment(int nCommentID) {
        CommonUtils.showProgressDialog(CommentManagementActivity.this, "댓글을 삭제하고 있습니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestDeleteComment(new OkHttpClient(), nCommentID, pref.getString("USER_ID", "Guest"));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(bResult) {
                            Toast.makeText(CommentManagementActivity.this, "댓글이 삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                            getReportsData();
                        } else {
                            Toast.makeText(CommentManagementActivity.this, "댓글을 삭제하지 못했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void removeReport(int nCommentID) {
        CommonUtils.showProgressDialog(CommentManagementActivity.this, "신고를 철회하고 있습니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestRemoveReport(new OkHttpClient(), nCommentID);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(bResult) {
                            Toast.makeText(CommentManagementActivity.this, "신고가 철회 되었습니다.", Toast.LENGTH_SHORT).show();
                            getReportsData();
                        } else {
                            Toast.makeText(CommentManagementActivity.this, "신고를 철회하지 못했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();
    }
}
