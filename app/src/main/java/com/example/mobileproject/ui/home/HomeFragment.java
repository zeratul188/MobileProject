package com.example.mobileproject.ui.home;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private ListView listView;
    private Button btnQuiz, btnStudy;

    private AlertDialog alertDialog = null;
    private AlertDialog.Builder builder = null;
    private View dialog_view = null;

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
                        //단어 추가 코드 작성 바람.

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

        return root;
    }

    public void toast(String message, boolean longer) {
        int toast_length;
        if (longer) toast_length = Toast.LENGTH_LONG;
        else toast_length = Toast.LENGTH_SHORT;
        Toast.makeText(getActivity(), message, toast_length).show();
    }
}