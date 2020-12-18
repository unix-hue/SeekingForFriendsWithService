package com.example.seekingforfriends;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShowFriends extends AppCompatActivity {

    FirebaseFirestore firestore;
    private FirebaseUser user;
    List<Map> friends;
    RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_friends);

        MyViewModel model = ViewModelProviders.of(this).get(MyViewModel.class);
        LiveData<List<Map>> data = model.getFriends();

        user = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        rv = findViewById(R.id.show_friends);
        friends = new ArrayList<>();

        data.observe(this, new Observer<List<Map>>() {
            @Override
            public void onChanged(@Nullable List<Map> temp) {
                friends = temp;
                rv.setLayoutManager(new LinearLayoutManager(ShowFriends.this));
                rv.setAdapter(new RVAdapterFriends(ShowFriends.this, friends));
            }
        });
    }
}