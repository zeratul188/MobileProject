package com.example.mobileproject;

import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class EditActivity extends AppCompatActivity {

    private MyDBHelper myDBHelper;
    private SQLiteDatabase sqlDB;

    private EditText edtEnglish, edtKorean;
    private Button btnEdit;
    private TextView txtEnglish, txtKorean;
    private RadioGroup rgGrade;
    private RadioButton[] rdoGrade = new RadioButton[3];

    private ArrayList<Word> wordList = null;
    private int index;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editlayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("단어 수정");

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

        int temp;
        for (int i = 0; i < rdoGrade.length; i++) {
            temp = getResources().getIdentifier("rdoGrade"+(i+1), "id", getPackageName());
            rdoGrade[i] = findViewById(temp);
        }

        edtEnglish = findViewById(R.id.edtEnglish);
        edtKorean = findViewById(R.id.edtKorean);
        btnEdit = findViewById(R.id.btnEdit);
        txtEnglish = findViewById(R.id.txtEnglish);
        txtKorean = findViewById(R.id.txtKorean);
        rgGrade = findViewById(R.id.rgGrade);

        Intent intent = getIntent();

        wordList = (ArrayList<Word>) intent.getSerializableExtra("wordList");
        index = intent.getIntExtra("index", 0);

        txtEnglish.setText(wordList.get(index).getEnglish());
        txtKorean.setText(wordList.get(index).getKorean());

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (String.valueOf(edtEnglish.getText()).equals("") || String.valueOf(edtKorean.getText()).equals("")) {
                    toast("영어와 한글 모두 입력해주십시오.", false);
                    return;
                }

                toast(wordList.get(index).getEnglish()+"("+wordList.get(index).getKorean()+") 단어가 "+String.valueOf(edtEnglish.getText())+"("+String.valueOf(edtKorean.getText())+")으로 수정되었습니다.", false);

                String type;
                switch (rgGrade.getCheckedRadioButtonId()) {
                    case R.id.rdoGrade1:
                        type = "초급";
                        break;
                    case R.id.rdoGrade2:
                        type = "중급";
                        break;
                    case R.id.rdoGrade3:
                        type = "고급";
                        break;
                    default:
                        type = "초급";
                }

                editItem(wordList.get(index).getEnglish(), String.valueOf(edtEnglish.getText()), String.valueOf(edtKorean.getText()), type);
                wordList.get(index).setEnglish(String.valueOf(edtEnglish.getText()));
                wordList.get(index).setKorean(String.valueOf(edtKorean.getText()));

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

    public void editItem(String english, String changed_english, String changed_korean, String type) {
        sqlDB = myDBHelper.getWritableDatabase();
        String sql = "update word set english = '"+changed_english+"', korean = '"+changed_korean+"', type = '"+type+"' where english = '"+english+"';";
        sqlDB.execSQL(sql);
        sqlDB.close();
    }
}
