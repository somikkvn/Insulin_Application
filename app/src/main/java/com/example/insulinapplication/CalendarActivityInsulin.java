package com.example.insulinapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CalendarActivityInsulin extends AppCompatActivity {
    DatabaseReference myTableInsulinDairyRef;
    String data;
    int count;

    public void myChart(View v){
        Intent intent = new Intent(this, ChartActivity.class);
        startActivity(intent);
    }

    public void backMain(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_insulin);
        TextView textView = findViewById(R.id.textView2);
        textView.setText("hjsdksdjksdf");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myTableInsulinDairyRef = database.getReference("insulin_diary");
        Query query = myTableInsulinDairyRef.orderByChild("timestamp").limitToLast(100);
        count = 0;
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                data = "";
                for (DataSnapshot ds : snapshot.getChildren()) {
                    count++;
                    String insulin = ds.child("insulin").getValue(String.class);
                    Long timestamp = ds.child("timestamp").getValue(Long.class);
                    Date dateTime = new Date(timestamp);
                    SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss");
                    String date = formatter.format(dateTime);

                    data += count + ". Дата дозування" + ": " + date + " од.\n"
                            + "Тип інсуліну: " + insulin + "\n\n";
                }
                textView.setText(data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("CalendarActivity", "Failed to read value.", error.toException());
            }

        });
    }

}
