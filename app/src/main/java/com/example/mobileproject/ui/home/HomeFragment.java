package com.example.mobileproject.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.mobileproject.R;
import com.example.mobileproject.Word;
import com.example.mobileproject.WordAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private ListView listView;
    private Button btnQuiz, btnStudy;

    private AlertDialog alertDialog = null;
    private AlertDialog.Builder builder = null;
    private View dialog_view = null;

    private WordAdapter adapter;
    private ArrayList<Word> wordList;
    private Word word;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        /*final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //toast("Opened FAB", false);
                dialog_view = getLayoutInflater().inflate(R.layout.adddialog, null);

                final Button btnCancel = dialog_view.findViewById(R.id.btnCancel);
                final Button btnAdd = dialog_view.findViewById(R.id.btnAdd);
                final EditText edtEnglish = dialog_view.findViewById(R.id.edtEnglish);
                final EditText edtKorean = dialog_view.findViewById(R.id.edtKorean);

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FileOutputStream fos = null;
                        ObjectOutputStream oos = null;

                        String korean = String.valueOf(edtKorean.getText());
                        String english = String.valueOf(edtEnglish.getText());

                        Word temp_word = new Word(korean, english);
                        wordList.add(temp_word);

                        try {
                            fos = getActivity().openFileOutput("word.obj", Context.MODE_PRIVATE);
                            oos = new ObjectOutputStream(fos);
                            for (int i = 0; i < wordList.size(); i++) oos.writeObject(wordList.get(i));
                            oos.flush();
                            toast(english+"("+korean+") 단어를 추가하였습니다.", true);
                        } catch (IOException e) {
                            e.printStackTrace();
                            toast("단어 추가를 하지 못하였습니다.", false);
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
                        }

                        onStart();
                        alertDialog.dismiss();
                    }
                });

                builder = new AlertDialog.Builder(getActivity());
                builder.setView(dialog_view);

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });

        listView = root.findViewById(R.id.listView);
        btnQuiz = root.findViewById(R.id.btnQuiz);
        btnStudy = root.findViewById(R.id.btnStudy);

        wordList = new ArrayList<Word>();
        adapter = new WordAdapter(getActivity(), wordList);
        listView.setAdapter(adapter);



        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        wordList.clear();
        loadItem();
        adapter.notifyDataSetChanged();
    }

    private void toast(String message, boolean longer) {
        int toast_length;
        if (longer) toast_length = Toast.LENGTH_LONG;
        else toast_length = Toast.LENGTH_SHORT;
        Toast.makeText(getActivity(), message, toast_length).show();
    }

    public void loadItem() {
        FileInputStream fis = null;
        ObjectInputStream ois = null;

        try {
            fis = getActivity().openFileInput("word.obj");
            ois = new ObjectInputStream(fis);
            while (wordList.add((Word)ois.readObject()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            toast(String.valueOf(e), false);
        } finally {
            try {
                if (fis != null) fis.close();
                if (ois != null) ois.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}