package com.example.insulinapplication;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("products");
        Button buttonAddInsulin = findViewById(R.id.buttonAddInsulin);
        buttonAddInsulin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InsulinDialogFragment dialogFragmentIns = new InsulinDialogFragment();
                dialogFragmentIns.show(getSupportFragmentManager(), "InsulinDialogFragment");
            }
        });

        Button buttonAddMeal = findViewById(R.id.buttonAddMeal);
        buttonAddMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductDialogFragment dialogFragmentProduct = new ProductDialogFragment();
                dialogFragmentProduct.show(getSupportFragmentManager(), "ProductDialogFragment");
            }
        });
    }

    public void myCalendar(View v){
        Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}