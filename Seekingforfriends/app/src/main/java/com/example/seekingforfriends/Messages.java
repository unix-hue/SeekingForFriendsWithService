package com.example.seekingforfriends;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Messages extends AppCompatActivity {

    Intent intent;
    FirebaseFirestore firestore;
    RecyclerView recyclerView;
    String user;
    List<Map> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        intent = getIntent();
        firestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.list_humans);
        users = new ArrayList<>();

        user = intent.getStringExtra("userUid");

        MyViewModel model = ViewModelProviders.of(this).get(MyViewModel.class);
        LiveData<List<Map>> data = model.getTalkers();

        data.observe(this, new Observer<List<Map>>() {
            @Override
            public void onChanged(@Nullable List<Map> temp) {
                users = temp;

                recyclerView.setLayoutManager(new LinearLayoutManager(Messages.this));
                recyclerView.setAdapter(new RVAdapterMesUser(Messages.this, users));
            }
        });
    }
}