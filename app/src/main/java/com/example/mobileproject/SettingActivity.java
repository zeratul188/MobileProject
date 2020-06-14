package com.example.mobileproject;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class SettingActivity extends AppCompatActivity{

    private Button btnAllDelete, btnFavoriteDelete, btnHarderDelete;
    private Button[] btnDiffDelete = new Button[3];
    private TextView txtAll, txtFavorite, txtHarder;
    private TextView[] txtDiff = new TextView[3];
    private Spinner spinnerTime;

    private MyDBHelper myDBHelper;
    private SQLiteDatabase sqlDB;
    private Cursor cursor;

    private AlertDialog alertDialog = null;
    private AlertDialog.Builder builder = null;

    private String[] diff = {"초급", "중급", "고급"};
    private String[] items = {"10초", "20초", "30초", "40초", "50초", "1분"};
    private int[] times = {10, 20, 30, 40, 50, 60};

    private int all, favorite, harder;
    private int[] diff_count = new int[3];

    boolean first_selected = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settinglayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("환경설정");

        myDBHelper = MainActivity.getMyDBHelper();

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
        spinnerTime = findViewById(R.id.spinnerTime);
        txtAll = findViewById(R.id.txtAll);
        txtFavorite = findViewById(R.id.txtFavorite);
        txtHarder = findViewById(R.id.txtHarder);

        int temp;
        for (int i = 0; i < txtDiff.length; i++) {
            temp = getResources().getIdentifier("txtDiff"+(i+1), "id", getPackageName());
            txtDiff[i] = findViewById(temp);
        }

        refreshCount();

        spinnerTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadSetting(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, items);
        spinnerTime.setAdapter(adapter);

        int position = Arrays.binarySearch(times, Integer.parseInt(getItemIndex()));
        spinnerTime.setSelection(position);

        for (int i = 0; i < btnDiffDelete.length; i++) {
            temp = getResources().getIdentifier("btnDiffDelete"+(i+1), "id", getPackageName());
            btnDiffDelete[i] = findViewById(temp);

            final int index = i;
            btnDiffDelete[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDialog(diff[index]+" 단어 삭제", diff[index]+" 단어를 삭제하시겠습니까?", "delete from word where type = '"+diff[index]+"';", diff[index]+" 단어를 삭제하였습니다.");
                }
            });
        }

        btnAllDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog("모든 단어 삭제", "모든 단어를 삭제하시겠습니까?", "delete from word;", "모든 단어가 삭제되었습니다.");
            }
        });

        btnFavoriteDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog("즐겨찾기 단어 삭제", "즐겨찾기 단어를 삭제하시겠습니까?", "delete from word where favorite = 1;", "즐겨찾기 단어를 삭제하였습니다.");
            }
        });

        btnHarderDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog("어려운 단어 삭제", "어려운 단어를 삭제하시겠습니까?", "delete from word where harder = 1;", "어려운 단어를 삭제하였습니다.");
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

    public void refreshCount() {
        sqlDB = myDBHelper.getWritableDatabase();

        cursor = sqlDB.rawQuery("select count(*) from word;", null);
        while (cursor.moveToNext()) {
            all = Integer.parseInt(cursor.getString(0));
        }
        txtAll.setText(Integer.toString(all));

        cursor = sqlDB.rawQuery("select count(*) from word where favorite = 1;", null);
        while (cursor.moveToNext()) {
            favorite = Integer.parseInt(cursor.getString(0));
        }
        txtFavorite.setText(Integer.toString(favorite));

        cursor = sqlDB.rawQuery("select count(*) from word where harder = 1;", null);
        while (cursor.moveToNext()) {
            harder = Integer.parseInt(cursor.getString(0));
        }
        txtHarder.setText(Integer.toString(harder));

        for (int i = 0; i < txtDiff.length; i++) {
            cursor = sqlDB.rawQuery("select count(*) from word where type = '"+diff[i]+"';", null);
            while (cursor.moveToNext()) {
                diff_count[i] = Integer.parseInt(cursor.getString(0));
            }
            txtDiff[i].setText(Integer.toString(diff_count[i]));
        }
    }

    public void deleteDialog(String title, String message, String sql, String toast) {
        builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setTitle(title);
        builder.setMessage(message);
        final String final_sql = sql, final_toast = toast;
        builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sqlDB = myDBHelper.getWritableDatabase();
                sqlDB.execSQL(final_sql);
                sqlDB.close();
                toast(final_toast, false);
                refreshCount();
            }
        });
        builder.setNegativeButton("취소", null);

        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public void loadSetting(int position) {
        FileOutputStream fos = null;
        String memoData = Integer.toString(times[position]);
        System.out.println(times[position]);
        if (!first_selected) {
            try {
                fos = openFileOutput("settings.txt", MODE_PRIVATE);
                fos.write(memoData.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                toast(String.valueOf(e), false);
            } finally {
                try {
                    if (fos != null) fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            first_selected = false;
        }
    }

    public String getItemIndex() {
        String result;
        FileInputStream fis = null;

        try {
            fis = openFileInput("settings.txt");
            byte[] memoData = new byte[fis.available()];

            while(fis.read(memoData) != -1) {}
            result = new String(memoData);
        } catch (IOException e) {
            e.printStackTrace();
            result = "30";
        } catch (Exception e) {
            e.printStackTrace();
            result = "30";
        } finally {
            try {
                if (fis != null) fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
