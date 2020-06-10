package com.example.mobileproject;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SettingActivity extends AppCompatActivity {

    private Button btnAllDelete, btnFavoriteDelete, btnHarderDelete;
    private Button[] btnDiffDelete = new Button[3];

    private MyDBHelper myDBHelper;
    private SQLiteDatabase sqlDB;

    private AlertDialog alertDialog = null;
    private AlertDialog.Builder builder = null;

    private String[] diff = {"초급", "중급", "고급"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settinglayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("환경세팅");

        View view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view != null) {
                // 23 버전 이상일 때 상태바 하얀 색상에 회색 아이콘 색상을 설정
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getWindow().setStatusBarColor(Color.parseColor("#ffffff"));
            }
        }else if (Build.VERSION.SDK_INT >= 21) {
            // 21 버전 이상일 때
            getWindow().setStatusBarColor(Color.BLACK);
        }

        btnAllDelete = findViewById(R.id.btnAllDelete);
        btnFavoriteDelete = findViewById(R.id.btnFavoriteDelete);
        btnHarderDelete = findViewById(R.id.btnHarderDelete);

        int temp;
        for (int i = 0; i < btnDiffDelete.length; i++) {
            temp = getResources().getIdentifier("btnDiffDelete"+(i+1), "id", getPackageName());
            btnDiffDelete[i] = findViewById(temp);

            final int index = i;
            btnDiffDelete[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder = new AlertDialog.Builder(SettingActivity.this);
                    builder.setTitle(diff[index]+" 단어 삭제");
                    builder.setMessage(diff[index]+" 단어를 삭제하시겠습니까?");
                    builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sqlDB = myDBHelper.getWritableDatabase();
                            String sql = "delete from word where type = '"+diff[index]+"';";
                            sqlDB.execSQL(sql);
                            sqlDB.close();
                            toast(diff[index]+" 단어를 삭제하였습니다.", false);
                        }
                    });
                    builder.setNegativeButton("취소", null);

                    alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                }
            });
        }

        myDBHelper = MainActivity.getMyDBHelper();

        btnAllDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("모든 단어 삭제");
                builder.setMessage("모든 단어를 삭제하시겠습니까?");
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sqlDB = myDBHelper.getWritableDatabase();
                        String sql = "delete from word;";
                        sqlDB.execSQL(sql);
                        sqlDB.close();
                        toast("모든 단어가 삭제되었습니다.", false);
                    }
                });
                builder.setNegativeButton("취소", null);

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });

        btnFavoriteDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("즐겨찾기 단어 삭제");
                builder.setMessage("즐겨찾기 단어를 삭제하시겠습니까?");
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sqlDB = myDBHelper.getWritableDatabase();
                        String sql = "delete from word where favorite = 1;";
                        sqlDB.execSQL(sql);
                        sqlDB.close();
                        toast("즐겨찾기 단어를 삭제하였습니다.", false);
                    }
                });
                builder.setNegativeButton("취소", null);

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });

        btnHarderDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("어려운 단어 삭제");
                builder.setMessage("어려운 단어를 삭제하시겠습니까?");
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sqlDB = myDBHelper.getWritableDatabase();
                        String sql = "delete from word where harder = 1;";
                        sqlDB.execSQL(sql);
                        sqlDB.close();
                        toast("어려운 단어를 삭제하였습니다.", false);
                    }
                });
                builder.setNegativeButton("취소", null);

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });

    }

    public void toast(String message, boolean longer) {
        int length;
        if (longer) length = Toast.LENGTH_LONG;
        else length = Toast.LENGTH_SHORT;
        Toast.makeText(getApplicationContext(), message, length).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}