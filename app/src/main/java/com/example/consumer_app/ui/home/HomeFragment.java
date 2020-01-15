package com.example.consumer_app.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.consumer_app.Model.Action;
import com.example.consumer_app.Model.Firebase_DBManager;
import com.example.consumer_app.Model.User;
import com.example.consumer_app.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        final Button btn_test= root.findViewById(R.id.button_test);
        final User a=new User("dan","cohen","dandan","1234","0501234567",new ArrayList<User>());
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                addUser( a);
            }
        });

        homeViewModel.getText().observe(this, new Observer<String>()
        {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    public void  addUser(User a)
    {

        try {

            Firebase_DBManager.addParcel(a, new Action<String>()
            {
                @Override
                public void onSuccess(String obj) {



                    Toast.makeText(getContext(),obj, Toast.LENGTH_LONG).show();

                }

                @Override
                public void onFailure(Exception exception) {
                    Toast.makeText(getContext(), "Error \n" + exception.getMessage(), Toast.LENGTH_LONG).show();

                }

                @Override
                public void onProgress(String status, double percent) {


                }
            });
        } catch (Exception e) {
            Toast.makeText(getContext(),e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
}