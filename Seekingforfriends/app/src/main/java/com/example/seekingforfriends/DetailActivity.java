package com.example.seekingforfriends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {

    ImageView imageView;

    TextView name, year, city, doing, interes, music, film, book, game, me, politic, think;

    FirebaseStorage storage;
    StorageReference reference;
    FirebaseUser user;

    Intent intent;

    Button makeFriend, sendMessage;

    String userId;

    FirebaseFirestore firestore;

    boolean IsFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        user = FirebaseAuth.getInstance().getCurrentUser();

        intent = getIntent();

        storage = FirebaseStorage.getInstance();
        reference = storage.getReference();
        firestore = FirebaseFirestore.getInstance();

        imageView = findViewById(R.id.detailView);

        name = findViewById(R.id.detailName);
        year = findViewById(R.id.detailYear);
        city = findViewById(R.id.detailCity);
        doing = findViewById(R.id.detailDoing);
        interes = findViewById(R.id.detailInteres);
        music = findViewById(R.id.detailMusic);
        film = findViewById(R.id.detailFilm);
        book = findViewById(R.id.detailBook);
        game = findViewById(R.id.detailGame);
        me = findViewById(R.id.detailMe);
        politic = findViewById(R.id.detailPolitic);
        think = findViewById(R.id.detailThink);

        makeFriend = findViewById(R.id.makeFriend);
        sendMessage = findViewById(R.id.makeMessage);

        userId = intent.getStringExtra("id");

        firestore.collection("friends/users/" + user.getUid())
                .document(userId)
                .get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        makeFriend.setText("Удалить из друзей");
                        IsFriend = true;
                    }
                    else{
                        makeFriend.setText("Добавить в друзья");
                        IsFriend = false;
                    }
                } else {
                    //
                }
            }
        });

        reference.child(intent.getStringExtra("id")).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(DetailActivity.this).load(uri).into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //
            }
        });

        name.setText(intent.getStringExtra("name"));
        year.setText(intent.getStringExtra("year"));
        city.setText(intent.getStringExtra("city"));
        doing.setText(intent.getStringExtra("doing"));
        interes.setText(intent.getStringExtra("interes"));
        music.setText(intent.getStringExtra("music"));
        film.setText(intent.getStringExtra("film"));
        book.setText(intent.getStringExtra("book"));
        game.setText(intent.getStringExtra("game"));
        me.setText(intent.getStringExtra("me"));
        politic.setText(intent.getStringExtra("politic"));
        think.setText(intent.getStringExtra("think"));

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, list_messages.class);
                intent.putExtra("id", userId);
                startActivity(intent);
            }
        });

        makeFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(IsFriend)
                {
                    firestore.collection("friends/users/" + user.getUid())
                            .document(userId)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast toast = Toast.makeText(getApplicationContext(),
                                            "Пользователь удален из друзей", Toast.LENGTH_SHORT);
                                    toast.show();
                                    makeFriend.setText("Добавить в друзья");
                                    IsFriend = false;
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //
                                }
                            });
                }
                else
                {
                    Map<String, Object> friend = new HashMap<>();
                    friend.put("name", intent.getStringExtra("name"));
                    friend.put("year", intent.getStringExtra("year"));
                    friend.put("city", intent.getStringExtra("city"));
                    friend.put("doing", intent.getStringExtra("doing"));
                    friend.put("interes", intent.getStringExtra("interes"));
                    friend.put("music", intent.getStringExtra("music"));
                    friend.put("film", intent.getStringExtra("film"));
                    friend.put("book", intent.getStringExtra("book"));
                    friend.put("game", intent.getStringExtra("game"));
                    friend.put("me", intent.getStringExtra("me"));
                    friend.put("politic", intent.getStringExtra("politic"));
                    friend.put("think", intent.getStringExtra("think"));
                    friend.put("id", intent.getStringExtra("id"));

                    firestore.collection("friends/users/" + user.getUid())
                            .document(userId)
                            .set(friend)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast toast = Toast.makeText(getApplicationContext(),
                                            "Пользователь добавлен в друзья", Toast.LENGTH_SHORT);
                                    toast.show();
                                    makeFriend.setText("Удалить из друзей");
                                    IsFriend = true;
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