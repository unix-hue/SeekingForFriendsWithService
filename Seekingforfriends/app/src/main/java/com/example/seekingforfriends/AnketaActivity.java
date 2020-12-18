package com.example.seekingforfriends;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AnketaActivity extends AppCompatActivity {

    Intent intent;
    String userUID;

    ImageView imageView;
    Button choose, upload;
    EditText name, year, city, doing, interes, music, film, book, game, me, politic, think;

    private Uri filepath;

    private final int PICK_IMAGE_REQUEST = 71;

    FirebaseStorage storage;
    StorageReference storageReference;

    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anketa);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        intent = getIntent();
        userUID = intent.getStringExtra("userUid");

        imageView = findViewById(R.id.anketaView);

        choose = findViewById(R.id.choosepic);
        upload = findViewById(R.id.uploadData);

        name = findViewById(R.id.nameAdd);
        year = findViewById(R.id.birthAdd);
        city = findViewById(R.id.cityAdd);
        doing = findViewById(R.id.doingAdd);
        interes = findViewById(R.id.interesAdd);
        music = findViewById(R.id.musicAdd);
        film = findViewById(R.id.filmAdd);
        book = findViewById(R.id.bookAdd);
        game = findViewById(R.id.gameAdd);
        me = findViewById(R.id.myself);
        politic = findViewById(R.id.politic);
        think = findViewById(R.id.thinking);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl("gs://seekingforfriends-95ed4.appspot.com");
        firestore = FirebaseFirestore.getInstance();

        storageReference.child(userUID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(AnketaActivity.this).load(uri).into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //
            }
        });

        MyViewModel model = ViewModelProviders.of(this).get(MyViewModel.class);
        LiveData<Map<String, Object>> data = model.getAnketa();

        data.observe(this, new Observer<Map<String, Object>>() {
            @Override
            public void onChanged(@Nullable Map<String, Object> temp) {
                name.setText(temp.get("name").toString());
                city.setText(temp.get("city").toString());
                doing.setText(temp.get("doing").toString());
                year.setText(temp.get("birth day").toString());
                interes.setText(temp.get("interes").toString());
                music.setText(temp.get("music").toString());
                film.setText(temp.get("film").toString());
                book.setText(temp.get("book").toString());
                game.setText(temp.get("game").toString());
                me.setText(temp.get("me").toString());
                politic.setText(temp.get("politic").toString());
                think.setText(temp.get("think").toString());
            }
        });

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select picture"), PICK_IMAGE_REQUEST);
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(filepath != null){
                    StorageReference ref = storageReference.child(userUID);
                    ref.putFile(filepath)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    //
                                }
                            });
                }

                Map<String, Object> currentUser = new HashMap<>();
                currentUser.put("name", name.getText().toString());
                currentUser.put("birth day", year.getText().toString());
                currentUser.put("city", city.getText().toString());
                currentUser.put("doing", doing.getText().toString());
                currentUser.put("interes", interes.getText().toString());
                currentUser.put("music", music.getText().toString());
                currentUser.put("film", film.getText().toString());
                currentUser.put("book", book.getText().toString());
                currentUser.put("game", game.getText().toString());
                currentUser.put("me", me.getText().toString());
                currentUser.put("politic", politic.getText().toString());
                currentUser.put("think", think.getText().toString());
                currentUser.put("id", userUID);

                firestore.collection("users").document(userUID)
                        .set(currentUser)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Профиль обновлен!", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Не получилось обновить профиль", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            filepath = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}