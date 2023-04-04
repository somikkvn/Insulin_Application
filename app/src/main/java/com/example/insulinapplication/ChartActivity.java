package com.example.insulinapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class ChartActivity extends AppCompatActivity {

    private LineChart mChart;
    private String userId;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        // Get the userId from the intent
        userId = getIntent().getStringExtra("userId");
        mAuth = FirebaseAuth.getInstance();

        mChart = findViewById(R.id.line_chart);

        // enable description text
        mChart.getDescription().setEnabled(true);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        // enable pinch zoom to avoid scaling x and y axis separately
        mChart.setPinchZoom(true);

        // set an empty chart
        mChart.setData(new LineData());

        // customize x axis
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        // customize y axis
        YAxis yAxis = mChart.getAxisLeft();
        yAxis.setDrawGridLines(false);
        yAxis.setAxisMinimum(0f);

        // remove right y axis
        mChart.getAxisRight().setEnabled(false);

        // add data to chart
        addChartData();

        // update chart
        mChart.invalidate();
    }

    private void addChartData() {
        ArrayList<Entry> entries = new ArrayList<>();

        // Retrieve the data from your database here and add it to the entries list
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("myTableDose");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String user_id = currentUser.getUid();
        Query query = databaseReference.orderByChild("userId").equalTo(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int index = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String insulinNorm = ds.child("insulinNorm").getValue(String.class);
                    if (insulinNorm != null) {
                        Float insulinNormFloat = Float.parseFloat(insulinNorm);
                        entries.add(new Entry(index, insulinNormFloat));
                        index++;
                    }
                }

                // Create the LineDataSet and LineData objects
                LineDataSet dataSet = new LineDataSet(entries, "Insulin Levels");
                dataSet.setColor(Color.RED);
                dataSet.setCircleColor(Color.RED);
                dataSet.setLineWidth(2f);
                dataSet.setCircleRadius(4f);
                dataSet.setDrawValues(false);

                LineData lineData = new LineData(dataSet);
                mChart.setData(lineData);

                // Set x-axis labels
                mChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        return String.valueOf((int) value);
                    }
                });

                // Update chart
                mChart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    public void backCalendar(View v){
        Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);
    }


    public void onChartClick(View view) {
        Toast.makeText(this, "Chart button clicked!", Toast.LENGTH_SHORT).show();
    }
}

