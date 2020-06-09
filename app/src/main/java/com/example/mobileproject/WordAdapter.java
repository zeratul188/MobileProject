package com.example.mobileproject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class WordAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Word> wordList;

    private TextView txtEmpty;

    private MyDBHelper myDBHelper;
    private SQLiteDatabase sqlDB;

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

        txtEnglish.setText(wordList.get(position).getEnglish());
        txtKorean.setText(wordList.get(position).getKorean());

        final int index = position;

        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast(wordList.get(index).getEnglish()+"("+wordList.get(index).getKorean()+") 단어가 삭제되었습니다.", false);

                deleteItem(wordList.get(index).getEnglish());
                wordList.remove(index);

                if (wordList.isEmpty()) txtEmpty.setVisibility(View.VISIBLE);
                else txtEmpty.setVisibility(View.GONE);

                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    public void deleteItem(String delete_word) {
        /*FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        try {
            fos = context.openFileOutput("word.obj", Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            for (int i = 0; i < wordList.size(); i++) oos.writeObject(wordList.get(i));
            oos.flush();
        } catch (Exception e) {
            e.printStackTrace();
            toast(String.valueOf(e), false);
        } finally {
            try {
                if (fos != null) fos.close();
                if (oos != null) oos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/

        sqlDB = myDBHelper.getWritableDatabase();
        String sql = "delete from word where english = '"+delete_word+"';";
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
