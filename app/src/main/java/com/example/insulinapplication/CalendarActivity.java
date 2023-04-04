package com.example.insulinapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CalendarActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    DatabaseReference myTableDoseRef;


    public void myChart(View v){
        Intent intent = new Intent(this, ChartActivity.class);
        startActivity(intent);
    }


    private void generateTxt() {
        try {
            // Show a toast message indicating that the file was saved
            Toast.makeText(this, "Файл збережено в Storage", Toast.LENGTH_LONG).show();

            // Create a new file in the Downloads folder
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "InsulinApplication.txt");

            // Write the data to the file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(((TextView)findViewById(R.id.textView2)).getText().toString().getBytes());
            fos.close();



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void backMain(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        mAuth = FirebaseAuth.getInstance();
        TextView textView = findViewById(R.id.textView2);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myTableDoseRef = database.getReference("myTableDose");
        Button buttonPdf = findViewById(R.id.button3);
        buttonPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateTxt();
            }
        });

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();
        myTableDoseRef.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String data = "";
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String insulinNorm = ds.child("insulinNorm").getValue(String.class);
                    String longInsulinRatio = ds.child("longInsulinRatio").getValue(String.class);
                    String shortInsulinRatio = ds.child("shortInsulinRatio").getValue(String.class);
                    String morningDose = ds.child("morningDose").getValue(String.class);
                    String eveningDose = ds.child("eveningDose").getValue(String.class);
                    String time = ds.child("time").getValue(String.class);
                    data += "Добова норма інсуліну: " + insulinNorm + " од.\n"
                            + "Доля інсуліну довгої дії: " + longInsulinRatio + "\n"
                            + "Доля інсуліну короткої дії: " + shortInsulinRatio + "\n"
                            + "Ранкова доза інсуліну довгої дії: " + morningDose + " од.\n"
                            + "Вечірня доза інсуліну довгої дії: " + eveningDose + " од.\n"
                            + "Час: " +  time + "\n";
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
