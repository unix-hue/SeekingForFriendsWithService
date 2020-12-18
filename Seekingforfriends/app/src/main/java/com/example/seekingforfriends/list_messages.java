package com.example.seekingforfriends;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class list_messages extends AppCompatActivity {

    FirebaseUser user;
    Intent intent;
    String FriendId;
    RecyclerView recyclerView;
    FirebaseFirestore firestore;
    List<Map> messages;
    Button send;
    EditText text;
    String MyName, HisName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_messages);

        intent = getIntent();
        FriendId = intent.getStringExtra("id");
        user = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = findViewById(R.id.list_of_messages);
        messages = new ArrayList<>();
        firestore = FirebaseFirestore.getInstance();
        send = findViewById(R.id.send);
        text = findViewById(R.id.text_message);

        MyViewModel model = ViewModelProviders.of(this).get(MyViewModel.class);
        LiveData<String> myName = model.getMyName();

        myName.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String temp) {
                MyName = temp;
            }
        });

        firestore.collection("users").document(FriendId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    HisName = snapshot.getData().get("name").toString();
                } else {
                    //
                }
            }
        });

        firestore.collection("message/" + user.getUid() + "/" + FriendId).orderBy("time", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("Загрузка", "Listen failed.", e);
                            return;
                        }

                        if(messages.size() != 0)
                            messages.clear();

                        for (QueryDocumentSnapshot doc : value) {
                            messages.add(doc.getData());
                        }

                        recyclerView.setLayoutManager(new LinearLayoutManager(list_messages.this));
                        recyclerView.setAdapter(new RVAdapterMessages(list_messages.this, messages));
                    }
                });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(text.getText().length() != 0)
                {
                    Date date = new Date();
                    Map<String, Object> messageObj = new HashMap<>();
                    messageObj.put("name", MyName);
                    messageObj.put("text", text.getText().toString());
                    messageObj.put("time", date.getTime());

                    firestore.collection("recievers").document(FriendId)
                            .set(messageObj)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //
                                }
                            });

                    text.setText("");

                    firestore.collection("message/" + user.getUid() + "/" + FriendId).add(messageObj)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    //
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //
                                }
                            });

                    firestore.collection("message/" + FriendId + "/" + user.getUid()).add(messageObj)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    //
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //
                                }
                            });

                    Map<String, Object> Temp1 = new HashMap<>();
                    Temp1.put("id", FriendId);
                    Temp1.put("name", HisName);
                    firestore.collection("mesusers/users/" + user.getUid()).document(FriendId)
                            .set(Temp1)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //
                                }
                            });

                    Temp1.put("id", user.getUid());
                    Temp1.put("name", MyName);
                    firestore.collection("mesusers/users/" + FriendId).document(user.getUid())
                            .set(Temp1)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //
                                }
                            });
                }
            }
        });
    }
}