package com.example.seekingforfriends;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScrollingActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 0;
    private FirebaseUser user;
    FirebaseFirestore firestore;
    Intent intent;
    RecyclerView rv;
    List<Map> users;

    MyViewModel model;
    LiveData<List<Map>> livedata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        rv = findViewById(R.id.list);
        model = ViewModelProviders.of(this).get(MyViewModel.class);
        livedata = model.getUsers();
        firestore = FirebaseFirestore.getInstance();
        users = new ArrayList<>();

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build());

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        }
        else{
            user = FirebaseAuth.getInstance().getCurrentUser();

            firestore.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            //
                        }
                        else{
                            Map<String, Object> currentUser = new HashMap<>();
                            currentUser.put("name", "Нет данных");
                            currentUser.put("birth day", "Нет данных");
                            currentUser.put("city", "Нет данных");
                            currentUser.put("doing", "Нет данных");
                            currentUser.put("interes", "Нет данных");
                            currentUser.put("music", "Нет данных");
                            currentUser.put("film", "Нет данных");
                            currentUser.put("book", "Нет данных");
                            currentUser.put("game", "Нет данных");
                            currentUser.put("me", "Нет данных");
                            currentUser.put("politic", "Нет данных");
                            currentUser.put("think", "Нет данных");
                            currentUser.put("id", user.getUid());

                            firestore.collection("users").document(user.getUid())
                                    .set(currentUser)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast toast = Toast.makeText(getApplicationContext(),
                                                    "Здравствуйте! Заполните анкету о себе", Toast.LENGTH_SHORT);
                                            toast.show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //
                                        }
                                    });
                        }
                    } else {
                        //
                    }
                    Service.setServiceAlarm(ScrollingActivity.this);
                }
            });

            livedata.observe(this, new Observer<List<Map>>() {
                @Override
                public void onChanged(@Nullable List<Map> temp) {
                    users = temp;
                    rv.setLayoutManager(new LinearLayoutManager(ScrollingActivity.this));
                    rv.setAdapter(new RVAdapter(ScrollingActivity.this, users));
                }
            });
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.anketa) {
            intent = new Intent(ScrollingActivity.this, AnketaActivity.class);
            intent.putExtra("userUid", user.getUid());
            startActivity(intent);
        }

        if (id == R.id.messages) {
            intent = new Intent(ScrollingActivity.this, Messages.class);
            intent.putExtra("userUid", user.getUid());
            startActivity(intent);
        }

        if (id == R.id.friends) {
            intent = new Intent(ScrollingActivity.this, ShowFriends.class);
            startActivity(intent);
        }

        if (id == R.id.logOut) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            List<AuthUI.IdpConfig> providers = Arrays.asList(
                                    new AuthUI.IdpConfig.EmailBuilder().build());

                            startActivityForResult(
                                    AuthUI.getInstance()
                                            .createSignInIntentBuilder()
                                            .setAvailableProviders(providers)
                                            .build(),
                                    RC_SIGN_IN);
                        }
                    });
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                user = FirebaseAuth.getInstance().getCurrentUser();

                firestore.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                //
                            }
                            else{
                                Map<String, Object> currentUser = new HashMap<>();
                                currentUser.put("name", "Нет данных");
                                currentUser.put("birth day", "Нет данных");
                                currentUser.put("city", "Нет данных");
                                currentUser.put("doing", "Нет данных");
                                currentUser.put("interes", "Нет данных");
                                currentUser.put("music", "Нет данных");
                                currentUser.put("film", "Нет данных");
                                currentUser.put("book", "Нет данных");
                                currentUser.put("game", "Нет данных");
                                currentUser.put("me", "Нет данных");
                                currentUser.put("politic", "Нет данных");
                                currentUser.put("think", "Нет данных");
                                currentUser.put("id", user.getUid());

                                firestore.collection("users").document(user.getUid())
                                        .set(currentUser)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast toast = Toast.makeText(getApplicationContext(),
                                                        "Здравствуйте! Заполните анкету о себе", Toast.LENGTH_SHORT);
                                                toast.show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //
                                            }
                                        });
                            }
                        } else {
                            //
                        }
                    }
                });

                firestore.collection("users")
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value,
                                                @Nullable FirebaseFirestoreException e) {
                                if (e != null) {
                                    Log.w("Загрузка", "Listen failed.", e);
                                    return;
                                }

                                if(users.size() != 0)
                                    users.clear();

                                for (QueryDocumentSnapshot doc : value) {
                                    if(!doc.getData().get("id").toString().equals(user.getUid()))
                                        users.add(doc.getData());
                                }

                                rv.setLayoutManager(new LinearLayoutManager(ScrollingActivity.this));
                                rv.setAdapter(new RVAdapter(ScrollingActivity.this, users));
                            }
                        });

                Service.setServiceAlarm(ScrollingActivity.this);
            } else {
                //
            }
        }
    }
}