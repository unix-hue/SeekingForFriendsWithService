package com.example.seekingforfriends;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyViewModel extends ViewModel {
    MutableLiveData<Map<String, Object>> myAnketa;
    MutableLiveData<String> myName;
    MutableLiveData<List<Map>> users, friends, talkers;
    FirebaseUser user;
    FirebaseFirestore db;
    List<Map> tempTalkers;

    public MutableLiveData<Map<String, Object>> getAnketa(){
        if(myAnketa == null){
            myAnketa = new MutableLiveData<>();

            db = FirebaseFirestore.getInstance();
            db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        myAnketa.setValue(snapshot.getData());
                    } else {
                        //
                    }
                }
            });
        }
        return myAnketa;
    }

    public MutableLiveData<String> getMyName(){
        if(myName == null){
            myName = new MutableLiveData<>();

            db = FirebaseFirestore.getInstance();
            db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        myName.setValue(snapshot.getData().get("name").toString());
                    } else {
                        //
                    }
                }
            });
        }
        return myName;
    }

    public MutableLiveData<List<Map>> getUsers(){
        if(users == null){
            users = new MutableLiveData<>();
            user = FirebaseAuth.getInstance().getCurrentUser();

            db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w("Загрузка", "Listen failed.", e);
                                return;
                            }

                            List<Map> temp = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : value) {
                                if(!doc.getData().get("id").toString().equals(user.getUid()))
                                    temp.add(doc.getData());
                            }

                            users.setValue(temp);
                        }
                    });
        }
        return users;
    }

    public MutableLiveData<List<Map>> getFriends(){
        if(friends == null){
            friends = new MutableLiveData<>();
            user = FirebaseAuth.getInstance().getCurrentUser();

            db = FirebaseFirestore.getInstance();
            db.collection("friends/users/" + user.getUid())
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w("Загрузка", "Listen failed.", e);
                                return;
                            }

                            List<Map> temp = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : value) {
                                temp.add(doc.getData());
                            }

                            friends.setValue(temp);
                        }
                    });
        }
        return friends;
    }

    public MutableLiveData<List<Map>> getTalkers(){
        if(talkers == null){
            talkers = new MutableLiveData<>();
            user = FirebaseAuth.getInstance().getCurrentUser();
            tempTalkers = new ArrayList<>();

            db = FirebaseFirestore.getInstance();
            db.collection("mesusers/users/" + user.getUid())
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w("Загрузка", "Listen failed.", e);
                                return;
                            }

                            for (QueryDocumentSnapshot doc : value) {
                                tempTalkers.add(doc.getData());
                            }

                            talkers.setValue(tempTalkers);
                        }
                    });
        }
        return talkers;
    }
}
