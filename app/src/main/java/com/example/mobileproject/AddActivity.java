package com.example.mobileproject;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddActivity extends AppCompatActivity {
    private EditText edtEnglish, edtKorean;
    private RadioGroup rgGrade;
    private RadioButton[] rdoGrade = new RadioButton[3];
    private Button btnAdd;

    private MyDBHelper myDBHelper;
    private SQLiteDatabase sqlDB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addlayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("단어 추가");

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

        edtEnglish = findViewById(R.id.edtEnglish);
        edtKorean = findViewById(R.id.edtKorean);
        rgGrade = findViewById(R.id.rgGrade);
        btnAdd = findViewById(R.id.btnAdd);

        int temp;
        for (int i = 0; i < rdoGrade.length; i++) {
            temp = getResources().getIdentifier("rdoGrade"+(i+1), "id", getPackageName());
            rdoGrade[i] = findViewById(temp);
        }

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String korean = String.valueOf(edtKorean.getText());
                String english = String.valueOf(edtEnglish.getText());
                String grade;

                if (korean.equals("") || english.equals("")) {
                    toast("영어와 한글을 입력해주세요.", false);
                    return;
                }

                switch (rgGrade.getCheckedRadioButtonId()) {
                    case R.id.rdoGrade1:
                        grade = String.valueOf(rdoGrade[0].getText());
                        break;
                    case R.id.rdoGrade2:
                        grade = String.valueOf(rdoGrade[1].getText());
                        break;
                    case R.id.rdoGrade3:
                        grade = String.valueOf(rdoGrade[2].getText());
                        break;
                    default:
                        grade = String.valueOf(rdoGrade[0].getText());
                }

                Word temp_word = new Word(korean, english);

                saveItem(english, korean, grade);
                toast(english+"("+korean+") 단어가 추가되었습니다.", false);

                finish();
            }
        });

    }

    public void toast(String message, boolean longer) {
        int length;
        if (longer) length = Toast.LENGTH_LONG;
        else length = Toast.LENGTH_SHORT;
        Toast.makeText(getApplicationContext(), message, length).show();
    }

    public void saveItem(String english, String korean, String grade) {
        sqlDB = myDBHelper.getWritableDatabase();

        String sql = "insert into word values (null, ?, ?, ?, ?, ?);";
        Object[] args = {english, korean, 0, 0, grade};
        sqlDB.execSQL(sql, args);
        sqlDB.close();
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
