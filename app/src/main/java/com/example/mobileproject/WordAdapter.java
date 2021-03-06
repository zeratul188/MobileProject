package com.example.mobileproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class WordAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Word> wordList;

    private TextView txtEmpty;

    private MyDBHelper myDBHelper;
    private SQLiteDatabase sqlDB;

    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private View view;

    public WordAdapter(Context context, ArrayList<Word> wordList, MyDBHelper myDBHelper, TextView txtEmpty) {
        this.context = context;
        this.wordList = wordList;
        this.myDBHelper = myDBHelper;
        this.txtEmpty = txtEmpty;
    }

    @Override
    public int getCount() {
        return wordList.size();
    }

    @Override
    public Object getItem(int position) {
        return wordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) convertView = View.inflate(context, R.layout.item, null);

        TextView txtEnglish = convertView.findViewById(R.id.txtEnglish);
        TextView txtKorean = convertView.findViewById(R.id.txtKorean);
        ImageView imgDelete = convertView.findViewById(R.id.imgDelete);
        ImageView imgEdit = convertView.findViewById(R.id.imgEdit);

        txtEnglish.setText(wordList.get(position).getEnglish());
        txtKorean.setText(wordList.get(position).getKorean());

        final int index = position;

        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditActivity.class);
                intent.putExtra("index", index);
                intent.putExtra("wordList", wordList);
                context.startActivity(intent);

            }
        });

        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(context);
                builder.setTitle("단어 삭제");
                builder.setMessage(wordList.get(index).getEnglish()+"("+wordList.get(index).getKorean()+") 단어를 삭제하시겠습니까?");

                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        toast(wordList.get(index).getEnglish()+"("+wordList.get(index).getKorean()+") 단어가 삭제되었습니다.", false);

                        deleteItem(wordList.get(index).getEnglish());
                        wordList.remove(index);

                        if (wordList.isEmpty()) txtEmpty.setVisibility(View.VISIBLE);
                        else txtEmpty.setVisibility(View.GONE);

                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("취소", null);

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });

        return convertView;
    }

    public void deleteItem(String delete_word) {
        sqlDB = myDBHelper.getWritableDatabase();
        String sql = "delete from word where english = '"+delete_word+"';";
        sqlDB.execSQL(sql);

        sqlDB.close();
    }

    public void editItem(String english, String changed_english, String changed_korean, String type) {
        sqlDB = myDBHelper.getWritableDatabase();
        String sql = "update word set english = '"+changed_english+"', korean = '"+changed_korean+"', type = '"+type+"' where english = '"+english+"';";
        sqlDB.execSQL(sql);
        sqlDB.close();
    }

    private void toast(String message, boolean longer) {
        int toast_length;
        if (longer) toast_length = Toast.LENGTH_LONG;
        else toast_length = Toast.LENGTH_SHORT;
        Toast.makeText(context, message, toast_length).show();
    }
}
