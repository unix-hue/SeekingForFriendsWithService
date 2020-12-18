package com.example.seekingforfriends;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class Service extends IntentService {
    private static final String TAG = "Service";
    private static final long INTERVAL_MS = TimeUnit.SECONDS.toMillis(10);

    private static final int NOTIFY_ID = 101;
    private static String CHANNEL_ID = "Messages";

    public Service() {
        super(TAG);
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, Service.class);
    }

    public static void setServiceAlarm(Context context) {
        Intent intent = Service.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), INTERVAL_MS, pi);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!isNetworkAvailableAndConnected()) {
            Log.i(TAG, "Нет соединения: " + intent);
            return;
        }

        final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null)
        {
            firestore.collection("recievers").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.i(TAG, "Есть сообщения");

                            Intent notificationIntent = new Intent(Service.this, ScrollingActivity.class);
                            PendingIntent contentIntent = PendingIntent.getActivity(Service.this,
                                    0, notificationIntent,
                                    PendingIntent.FLAG_CANCEL_CURRENT);

                            long pattern[] = {100, 200, 300};

                            NotificationCompat.Builder builder =
                                    new NotificationCompat.Builder(Service.this, CHANNEL_ID)
                                            .setSmallIcon(R.drawable.fui_ic_mail_white_24dp)
                                            .setContentTitle("Поиск единомышленников")
                                            .setContentText("У вас новые сообщения")
                                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                            .setContentIntent(contentIntent)
                                            .setAutoCancel(true)
                                            .setVibrate(pattern);

                            NotificationManagerCompat notificationManager =
                                    NotificationManagerCompat.from(Service.this);
                            notificationManager.notify(NOTIFY_ID, builder.build());

                            firestore.collection("recievers").document(user.getUid())
                                    .delete()
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
                        } else {
                            Log.i(TAG, "Сообщений нет");
                        }
                    } else {
                        Log.i(TAG, "Не получилось проверить наличие сообщений");
                    }
                }
            });
        }
    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo nwInfo = cm.getActiveNetworkInfo();
        return nwInfo != null && nwInfo.isConnected();
    }
}
