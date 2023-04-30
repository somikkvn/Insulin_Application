package com.example.insulinapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class CalendarActivity extends AppCompatActivity {
    DatabaseReference myTableProductDairyRef;
    String data;
    DataSnapshot data_snapshot;
    Integer count = 0;

    public void backMain(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        TextView textView = findViewById(R.id.textView2);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myTableProductDairyRef = database.getReference("meal_diary");
        Query query = myTableProductDairyRef.orderByChild("date").limitToLast(100);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                data = "";
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Integer diary_id = ds.child("id").getValue(Integer.class);
                    DatabaseReference productsRef = database.getReference("product_diary");
                    Query query = productsRef.orderByChild("diary_id").equalTo(diary_id);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String date = ds.child("date").getValue(String.class);
                            String meal = ds.child("meal").getValue(String.class);
                            count++;
                            data += count + ". Дата " + ": " + date + " од.\n"
                                    + "Трапеза: " + meal + "\n"
                                    + "Продукти: ";
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                String name = ds.child("product").getValue(String.class);
                                data += name + " | ";
                            }
                            data+="\n\n";
                            textView.setText(data);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                data+="\n";
                textView.setText(data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("CalendarActivity", "Failed to read value.", error.toException());
            }

        });

    }

}
