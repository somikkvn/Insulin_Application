package com.example.insulinapplication;

import android.app.NotificationManager;
import android.content.Context;
import android.os.CountDownTimer;

import androidx.core.app.NotificationCompat;

public class MedicineTimer extends CountDownTimer {
    private Context context;

    public MedicineTimer(long millisInFuture, long countDownInterval, Context context) {
        super(millisInFuture, countDownInterval);
        this.context = context;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        // Код, який виконується під час відліку (необов'язково)
    }

    @Override
    public void onFinish() {
        showNotification();
    }

    private void showNotification() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notification_channel_id")
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle("Час приймати інсулін")
                    .setContentText("Зараз час поїсти і прийняти інсулін")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true);

            notificationManager.notify(1, builder.build());
        }
    }
}
