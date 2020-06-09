package com.example.mobileproject.ui.send;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.mobileproject.MainActivity;
import com.example.mobileproject.MyDBHelper;
import com.example.mobileproject.R;
import com.example.mobileproject.Word;
import com.example.mobileproject.WordAdapter;
import com.example.mobileproject.ui.home.HomeFragment;
import com.example.mobileproject.ui.send.TimeThread;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class SendFragment extends Fragment {

    private SendViewModel sendViewModel;

    private TimeThread tt = null;

    private ListView listView;
    private Button btnQuiz, btnStudy;
    private TextView txtEmpty;
    private boolean end;

    private AlertDialog alertDialog = null;
    private AlertDialog.Builder builder = null;
    private View dialog_view = null;

    private WordAdapter adapter;
    private ArrayList<Word> wordList;
    private int questions;
    private TextView txtQuestion, txtExam;

    private int ok = 0, worse = 0;
    private ArrayList<Word> ok_list;
    private ArrayList<Word> worse_list;

    private ArrayList<Word> tempList;

    private Handler handler = null;

    private MyDBHelper myDBHelper;
    private SQLiteDatabase sqlDB;
    private Cursor cursor;

    private int index;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sendViewModel =
                ViewModelProviders.of(this).get(SendViewModel.class);
        View root = inflater.inflate(R.layout.fragment_send, container, false);

        myDBHelper = MainActivity.getMyDBHelper();

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
                final RadioGroup rgGrade = dialog_view.findViewById(R.id.rgGrade);
                final RadioButton[] rdoGrade = new RadioButton[3];

                int temp;
                for (int i = 0; i < rdoGrade.length; i++) {
                    temp = dialog_view.getResources().getIdentifier("rdoGrade"+(i+1), "id", getActivity().getPackageName());
                    rdoGrade[i] = dialog_view.findViewById(temp);
                }

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String korean = String.valueOf(edtKorean.getText());
                        String english = String.valueOf(edtEnglish.getText());
                        String grade;

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
                        wordList.add(temp_word);

                        saveItem(english, korean, grade);

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

        handler = new Handler();

        listView = root.findViewById(R.id.listView);
        btnQuiz = root.findViewById(R.id.btnQuiz);
        btnStudy = root.findViewById(R.id.btnStudy);

        txtEmpty = root.findViewById(R.id.txtEmpty);

        wordList = new ArrayList<Word>();
        adapter = new WordAdapter(getActivity(), wordList, myDBHelper, txtEmpty);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog_view = getLayoutInflater().inflate(R.layout.showdialog, null);

                final TextView txtEnglish = dialog_view.findViewById(R.id.txtEnglish);
                final TextView txtGrade = dialog_view.findViewById(R.id.txtGrade);
                final TextView txtKorean = dialog_view.findViewById(R.id.txtKorean);
                final Button btnExit = dialog_view.findViewById(R.id.btnExit);
                final Button btnHard = dialog_view.findViewById(R.id.btnHard);
                final Button btnFavorite = dialog_view.findViewById(R.id.btnFavorite);

                sqlDB = myDBHelper.getWritableDatabase();
                cursor = sqlDB.rawQuery("select type from word where english = '"+wordList.get(position).getEnglish()+"';", null);

                String temp = "초급";
                while (cursor.moveToNext()) {
                    temp = cursor.getString(0);
                }

                txtEnglish.setText(wordList.get(position).getEnglish());
                txtKorean.setText(wordList.get(position).getKorean());

                txtGrade.setText(temp);

                btnExit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                final int index = position;
                btnHard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean harder = false;

                        sqlDB = myDBHelper.getWritableDatabase();
                        cursor = sqlDB.rawQuery("select harder from word where english = '"+wordList.get(index).getEnglish()+"';", null);

                        Word temp;
                        while (cursor.moveToNext()) {
                            if (cursor.getInt(0) == 0) harder = false;
                            else harder = true;
                        }

                        String sql;
                        if (harder) {
                            sql = "update word set harder = 0 where english = '"+wordList.get(index).getEnglish()+"';";
                            toast("어려운 단어를 해제하였습니다.", false);
                        } else {
                            sql = "update word set harder = 1 where english = '"+wordList.get(index).getEnglish()+"';";
                            toast("어려운 단어를 추가하였습니다.", false);
                        }
                        sqlDB.execSQL(sql);

                        sqlDB.close();
                    }
                });

                btnFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean favorite = false;

                        sqlDB = myDBHelper.getWritableDatabase();
                        cursor = sqlDB.rawQuery("select favorite from word where english = '"+wordList.get(index).getEnglish()+"';", null);

                        Word temp;
                        while (cursor.moveToNext()) {
                            if (cursor.getInt(0) == 0) favorite = false;
                            else favorite = true;
                        }

                        String sql;
                        if (favorite) {
                            sql = "update word set favorite = 0 where english = '"+wordList.get(index).getEnglish()+"';";
                            toast("즐겨찾기를 해제하였습니다.", false);
                        } else {
                            sql = "update word set favorite = 1 where english = '"+wordList.get(index).getEnglish()+"';";
                            toast("즐겨찾기를 추가하였습니다.", false);
                        }
                        sqlDB.execSQL(sql);

                        sqlDB.close();
                    }
                });

                builder = new AlertDialog.Builder(getActivity());
                builder.setView(dialog_view);

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });

        btnQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wordList.isEmpty()) {
                    toast("단어가 비어있습니다.", false);
                    return;
                }
                dialog_view = getLayoutInflater().inflate(R.layout.quizdialog, null);

                final RadioGroup rgLanguage = dialog_view.findViewById(R.id.rgLanguage);
                final RadioButton rdoKorean = dialog_view.findViewById(R.id.rdoKorean);
                final RadioButton rdoEnglish = dialog_view.findViewById(R.id.rdoEnglish);
                final Button btnExit = dialog_view.findViewById(R.id.btnExit);
                final Button btnStart = dialog_view.findViewById(R.id.btnStart);

                btnExit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                btnStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        end = false;

                        alertDialog.dismiss();
                        questions = wordList.size();

                        worse = 0;
                        ok = 0;

                        ok_list = new ArrayList<Word>();
                        worse_list = new ArrayList<Word>();

                        tempList = new ArrayList<Word>();
                        for (int i = 0; i < wordList.size(); i++) tempList.add(wordList.get(i));

                        index = (int)(Math.random()*123456789)%tempList.size();
                        if (rdoEnglish.isChecked()) {
                            dialog_view = getLayoutInflater().inflate(R.layout.quizdialog_korean, null);

                            txtQuestion = dialog_view.findViewById(R.id.txtQuestion);
                            final TextView txtTime = dialog_view.findViewById(R.id.txtTime);
                            txtExam = dialog_view.findViewById(R.id.txtExam);
                            final EditText edtResult = dialog_view.findViewById(R.id.edtResult);
                            final Button btnExit = dialog_view.findViewById(R.id.btnExit);
                            final Button btnPass = dialog_view.findViewById(R.id.btnPass);
                            final Button btnNext = dialog_view.findViewById(R.id.btnNext);

                            tt = new TimeThread(handler, 30, txtTime, SendFragment.this);
                            tt.setKoreaned(false);
                            tt.start();

                            txtQuestion.setText(tempList.get(index).getKorean());
                            txtExam.setText(Integer.toString(questions));

                            btnExit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                    if (tt != null)  tt.stopThread();
                                    toast("퀴즈를 종료하였습니다.", false);
                                }
                            });

                            btnPass.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (questions == 1) end = true;
                                    if (!end) {
                                        worse++;
                                        worse_list.add(tempList.get(index));
                                        nextWord(tempList, true, txtQuestion);
                                        txtExam.setText(Integer.toString(questions));
                                        tt.reset(30);
                                    } else {
                                        worse++;
                                        worse_list.add(tempList.get(index));

                                        tt.stopThread();
                                        alertDialog.dismiss();
                                        dialog_view = getLayoutInflater().inflate(R.layout.resultdialog, null);

                                        final TextView txtOK = dialog_view.findViewById(R.id.txtOK);
                                        final TextView txtWorse = dialog_view.findViewById(R.id.txtWorse);
                                        final LinearLayout layoutOK = dialog_view.findViewById(R.id.layoutOK);
                                        final LinearLayout layoutWorse = dialog_view.findViewById(R.id.layoutWorse);

                                        txtOK.setText(Integer.toString(ok));
                                        txtWorse.setText(Integer.toString(worse));

                                        String result;
                                        for (int i = 0; i < ok_list.size(); i++) {
                                            result = ok_list.get(i).getEnglish()+"("+ok_list.get(i).getKorean()+")";
                                            addTextView(result, layoutOK, true);
                                        }
                                        for (int i = 0; i < worse_list.size(); i++) {
                                            result = worse_list.get(i).getEnglish()+"("+worse_list.get(i).getKorean()+")";
                                            addTextView(result, layoutWorse, false);
                                        }

                                        builder = new AlertDialog.Builder(getActivity());
                                        builder.setView(dialog_view);

                                        builder.setPositiveButton("확인", null);

                                        alertDialog = builder.create();
                                        alertDialog.setCancelable(false);
                                        alertDialog.show();
                                    }
                                    edtResult.setText("");
                                }
                            });

                            btnNext.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (questions == 1) end = true;
                                    if (!end) {
                                        if (String.valueOf(edtResult.getText()).equals(tempList.get(index).getEnglish())) {
                                            ok++;
                                            ok_list.add(tempList.get(index));
                                        } else {
                                            worse++;
                                            worse_list.add(tempList.get(index));
                                        }
                                        nextWord(tempList, false, txtQuestion);
                                        txtExam.setText(Integer.toString(questions));
                                        tt.reset(30);
                                        txtTime.setText("30");
                                    } else {
                                        if (String.valueOf(edtResult.getText()).equals(tempList.get(index).getEnglish())) {
                                            ok++;
                                            ok_list.add(tempList.get(index));
                                        } else {
                                            worse++;
                                            worse_list.add(tempList.get(index));
                                        }
                                        tt.stopThread();
                                        alertDialog.dismiss();
                                        dialog_view = getLayoutInflater().inflate(R.layout.resultdialog, null);

                                        final TextView txtOK = dialog_view.findViewById(R.id.txtOK);
                                        final TextView txtWorse = dialog_view.findViewById(R.id.txtWorse);
                                        final LinearLayout layoutOK = dialog_view.findViewById(R.id.layoutOK);
                                        final LinearLayout layoutWorse = dialog_view.findViewById(R.id.layoutWorse);

                                        txtOK.setText(Integer.toString(ok));
                                        txtWorse.setText(Integer.toString(worse));

                                        String result;
                                        for (int i = 0; i < ok_list.size(); i++) {
                                            result = ok_list.get(i).getEnglish()+"("+ok_list.get(i).getKorean()+")";
                                            addTextView(result, layoutOK, true);
                                        }
                                        for (int i = 0; i < worse_list.size(); i++) {
                                            result = worse_list.get(i).getEnglish()+"("+worse_list.get(i).getKorean()+")";
                                            addTextView(result, layoutWorse, false);
                                        }

                                        builder = new AlertDialog.Builder(getActivity());
                                        builder.setView(dialog_view);

                                        builder.setPositiveButton("확인", null);

                                        alertDialog = builder.create();
                                        alertDialog.setCancelable(false);
                                        alertDialog.show();
                                    }
                                    edtResult.setText("");
                                }
                            });

                            builder = new AlertDialog.Builder(getActivity());
                            builder.setView(dialog_view);

                            alertDialog = builder.create();
                            alertDialog.setCancelable(false);
                            alertDialog.show();
                        } else {
                            dialog_view = getLayoutInflater().inflate(R.layout.quizdialog_english, null);

                            txtQuestion = dialog_view.findViewById(R.id.txtQuestion);
                            final TextView txtTime = dialog_view.findViewById(R.id.txtTime);
                            txtExam = dialog_view.findViewById(R.id.txtExam);
                            final EditText edtResult = dialog_view.findViewById(R.id.edtResult);
                            final Button btnExit = dialog_view.findViewById(R.id.btnExit);
                            final Button btnPass = dialog_view.findViewById(R.id.btnPass);
                            final Button btnNext = dialog_view.findViewById(R.id.btnNext);

                            tt = new TimeThread(handler, 30, txtTime, SendFragment.this);
                            tt.setKoreaned(true);
                            tt.start();

                            txtQuestion.setText(tempList.get(index).getEnglish());
                            txtExam.setText(Integer.toString(questions));

                            btnExit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                    if (tt != null)  tt.stopThread();
                                    toast("퀴즈를 종료하였습니다.", false);
                                }
                            });

                            btnPass.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (questions == 1) end = true;
                                    if (!end) {
                                        worse++;
                                        worse_list.add(tempList.get(index));
                                        nextWord(tempList, true, txtQuestion);
                                        txtExam.setText(Integer.toString(questions));
                                        tt.reset(30);
                                    } else {
                                        worse++;
                                        worse_list.add(tempList.get(index));

                                        tt.stopThread();
                                        alertDialog.dismiss();
                                        dialog_view = getLayoutInflater().inflate(R.layout.resultdialog, null);

                                        final TextView txtOK = dialog_view.findViewById(R.id.txtOK);
                                        final TextView txtWorse = dialog_view.findViewById(R.id.txtWorse);
                                        final LinearLayout layoutOK = dialog_view.findViewById(R.id.layoutOK);
                                        final LinearLayout layoutWorse = dialog_view.findViewById(R.id.layoutWorse);

                                        txtOK.setText(Integer.toString(ok));
                                        txtWorse.setText(Integer.toString(worse));

                                        String result;
                                        for (int i = 0; i < ok_list.size(); i++) {
                                            result = ok_list.get(i).getEnglish()+"("+ok_list.get(i).getKorean()+")";
                                            addTextView(result, layoutOK, true);
                                        }
                                        for (int i = 0; i < worse_list.size(); i++) {
                                            result = worse_list.get(i).getEnglish()+"("+worse_list.get(i).getKorean()+")";
                                            addTextView(result, layoutWorse, false);
                                        }

                                        builder = new AlertDialog.Builder(getActivity());
                                        builder.setView(dialog_view);

                                        builder.setPositiveButton("확인", null);

                                        alertDialog = builder.create();
                                        alertDialog.setCancelable(false);
                                        alertDialog.show();
                                    }
                                    edtResult.setText("");
                                }
                            });

                            btnNext.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (questions == 1) end = true;
                                    if (!end) {
                                        if (String.valueOf(edtResult.getText()).equals(tempList.get(index).getKorean())) {
                                            ok++;
                                            ok_list.add(tempList.get(index));
                                        } else {
                                            worse++;
                                            worse_list.add(tempList.get(index));
                                        }
                                        nextWord(tempList, true, txtQuestion);
                                        txtExam.setText(Integer.toString(questions));
                                        tt.reset(30);
                                        txtTime.setText("30");
                                    } else {
                                        if (String.valueOf(edtResult.getText()).equals(tempList.get(index).getKorean())) {
                                            ok++;
                                            ok_list.add(tempList.get(index));
                                        } else {
                                            worse++;
                                            worse_list.add(tempList.get(index));
                                        }
                                        tt.stopThread();
                                        alertDialog.dismiss();
                                        dialog_view = getLayoutInflater().inflate(R.layout.resultdialog, null);

                                        final TextView txtOK = dialog_view.findViewById(R.id.txtOK);
                                        final TextView txtWorse = dialog_view.findViewById(R.id.txtWorse);
                                        final LinearLayout layoutOK = dialog_view.findViewById(R.id.layoutOK);
                                        final LinearLayout layoutWorse = dialog_view.findViewById(R.id.layoutWorse);

                                        txtOK.setText(Integer.toString(ok));
                                        txtWorse.setText(Integer.toString(worse));

                                        String result;
                                        for (int i = 0; i < ok_list.size(); i++) {
                                            result = ok_list.get(i).getEnglish()+"("+ok_list.get(i).getKorean()+")";
                                            addTextView(result, layoutOK, true);
                                        }
                                        for (int i = 0; i < worse_list.size(); i++) {
                                            result = worse_list.get(i).getEnglish()+"("+worse_list.get(i).getKorean()+")";
                                            addTextView(result, layoutWorse, false);
                                        }

                                        builder = new AlertDialog.Builder(getActivity());
                                        builder.setView(dialog_view);

                                        builder.setPositiveButton("확인", null);

                                        alertDialog = builder.create();
                                        alertDialog.setCancelable(false);
                                        alertDialog.show();
                                    }
                                    edtResult.setText("");
                                }
                            });

                            builder = new AlertDialog.Builder(getActivity());
                            builder.setView(dialog_view);

                            alertDialog = builder.create();
                            alertDialog.setCancelable(false);
                            alertDialog.show();
                        }
                    }
                });

                builder = new AlertDialog.Builder(getActivity());
                builder.setView(dialog_view);

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();

            }
        });

        btnStudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wordList.isEmpty()) {
                    toast("단어가 비어있습니다.", false);
                    return;
                }
                index = 0;
                dialog_view = getLayoutInflater().inflate(R.layout.studydialog, null);

                final TextView txtEnglish = dialog_view.findViewById(R.id.txtEnglish);
                final TextView txtKorean = dialog_view.findViewById(R.id.txtKorean);
                final Button btnExit = dialog_view.findViewById(R.id.btnExit);
                final Button btnPervous = dialog_view.findViewById(R.id.btnPervous);
                final Button btnNext = dialog_view.findViewById(R.id.btnNext);
                final TextView txtNow = dialog_view.findViewById(R.id.txtNow);
                final TextView txtMax = dialog_view.findViewById(R.id.txtMax);

                txtEnglish.setText(wordList.get(index).getEnglish());
                txtKorean.setText(wordList.get(index).getKorean());

                txtNow.setText(Integer.toString(index+1));
                txtMax.setText(Integer.toString(wordList.size()));

                btnExit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                btnPervous.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        index--;
                        txtNow.setText(Integer.toString(index+1));
                        txtEnglish.setText(wordList.get(index).getEnglish());
                        txtKorean.setText(wordList.get(index).getKorean());
                        if (index == 0) {
                            btnPervous.setEnabled(false);
                            btnPervous.setTextColor(Color.parseColor("#666666"));
                        }
                        if (index < wordList.size()-1) btnNext.setEnabled(true);
                    }
                });

                btnNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        index++;
                        txtNow.setText(Integer.toString(index+1));
                        txtEnglish.setText(wordList.get(index).getEnglish());
                        txtKorean.setText(wordList.get(index).getKorean());
                        if (index == wordList.size()-1) btnNext.setEnabled(false);
                        if (index > 0) {
                            btnPervous.setEnabled(true);
                            btnPervous.setTextColor(Color.parseColor("#9b3232"));
                        }
                    }
                });

                builder = new AlertDialog.Builder(getActivity());
                builder.setView(dialog_view);

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        wordList.clear();
        loadItem();
        adapter.notifyDataSetChanged();
        if (wordList.isEmpty()) txtEmpty.setVisibility(View.VISIBLE);
        else txtEmpty.setVisibility(View.GONE);
    }

    private void toast(String message, boolean longer) {
        int toast_length;
        if (longer) toast_length = Toast.LENGTH_LONG;
        else toast_length = Toast.LENGTH_SHORT;
        Toast.makeText(getActivity(), message, toast_length).show();
    }

    public void saveItem(String english, String korean, String grade) {
        /*FileOutputStream fos = null;
        ObjectOutputStream oos = null;
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
        }*/

        sqlDB = myDBHelper.getWritableDatabase();

        String sql = "insert into word values (null, ?, ?, ?, ?, ?);";
        Object[] args = {english, korean, 0, 0, grade};
        sqlDB.execSQL(sql, args);
        sqlDB.close();
    }

    public void addTextView(String message, LinearLayout layout, boolean oked) {
        TextView view = new TextView(getActivity());
        view.setText(message);
        view.setTextSize(20);
        if (oked) view.setTextColor(Color.parseColor("#1A721A"));
        else view.setTextColor(Color.parseColor("#C51313"));
        layout.addView(view);
    }

    public void timeout(boolean koreaned) {
        worse++;
        worse_list.add(tempList.get(index));
        nextWord(tempList, koreaned, txtQuestion);
        txtExam.setText(Integer.toString(questions));
        toast("시간이 초과되었습니다. 오답으로 처리됩니다.", false);
    }

    public void nextWord(ArrayList<Word> list, boolean koreaned, TextView view) {
        list.remove(index);
        if (!list.isEmpty()) {
            index = (int)(Math.random()*123456789)%list.size();
            if (koreaned) {
                view.setText(list.get(index).getEnglish());
            } else {
                view.setText(list.get(index).getKorean());
            }
        }
        questions--;
    }

    @Override
    public void onStop() {
        super.onStop();
        cursor.close();
    }

    public void loadItem() {
        /*FileInputStream fis = null;
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
        }*/

        sqlDB = myDBHelper.getWritableDatabase();
        cursor = sqlDB.rawQuery("select * from word where type = '고급';", null);

        Word temp;
        while (cursor.moveToNext()) {
            temp = new Word(cursor.getString(2), cursor.getString(1));
            wordList.add(temp);
        }

        sqlDB.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        wordList.clear();
        loadItem();
        onStart();
    }
}