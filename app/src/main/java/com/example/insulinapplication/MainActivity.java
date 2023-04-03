package com.example.insulinapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button btnLogOut;
    Button buttonNext;
    FirebaseAuth mAuth;
    private EditText editTextHeight, editTextWeight, editTextAge;
    private int weight;

    private double insulinNorm,longInsulinRatio, shortInsulinRatio, morningDose, eveningDose;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogOut = findViewById(R.id.btnLogout);
        buttonNext = findViewById(R.id.buttonNext);
        mAuth = FirebaseAuth.getInstance();
        editTextHeight = findViewById(R.id.editTextHeight);
        editTextWeight = findViewById(R.id.editTextWeight);
        editTextAge = findViewById(R.id.editTextAge);

        Button buttonCalculate = findViewById(R.id.buttonCalculate);
        buttonCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeInsulinDose();
            }
        });

        btnLogOut.setOnClickListener(view ->{
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });

    }
    public void myCalendar(View v){
        Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

    public void writeInsulinDose() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("myTableDose");
        Date currentDate = new Date();
        // Step 3: Create a Map object to hold the data including id and timestamp
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("id", String.valueOf(myRef.push().getKey()));
        dataMap.put("insulinNorm", String.valueOf(this.insulinNorm));
        dataMap.put("longInsulinRatio", String.valueOf(this.longInsulinRatio));
        dataMap.put("shortInsulinRatio", String.valueOf(this.shortInsulinRatio));
        dataMap.put("morningDose", String.valueOf(this.morningDose));
        dataMap.put("eveningDose", String.valueOf(this.eveningDose));
        dataMap.put("time", currentDate.toString());

        // Step 4: Store the data in the Firebase table
        myRef.child(String.valueOf(myRef.push().getKey())).setValue(dataMap);
    }

    private double calculateMorningLongDose(double longInsulinRatio) {
        // Ранкова доза інсуліну довгої дії обчислюється як 2/3 від добової норми інсуліну довгої дії
        return calculateLongInsulinRatio() * 0.67;
    }

    private double calculateEveningLongDose(double longInsulinRatio) {
        // Вечірня доза інсуліну довгої дії обчислюється як 1/3 від добової норми інсуліну довгої дії
        return calculateLongInsulinRatio() * 0.33;
    }

    private double calculateInsulinNorm(int weight) {
        // Формула для розрахунку добової норми інсуліну
        return weight * 0.5;
    }

    private double calculateLongInsulinRatio() {
        // Зазвичай доля інсуліну довгої дії становить 40-50% від загальної добової потреби в інсуліні
        return calculateInsulinNorm(weight) * 0.45;
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        double insulin_norm_value = 1;

        switch(view.getId()) {
            case R.id.radioButton:
                insulin_norm_value =  0.5;
                break;
            case R.id.radioButton2:
                insulin_norm_value =  0.6;
                break;
            case R.id.radioButton3:
                insulin_norm_value =  0.7;
                break;
            case R.id.radioButton4:
                insulin_norm_value =  0.8;
                break;
            case R.id.radioButton5:
                insulin_norm_value =  0.9;
                break;
            default:
                insulin_norm_value =  1;
                break;
        }

        if (checked){
                    int height = Integer.parseInt(editTextHeight.getText().toString());
                    weight = Integer.parseInt(editTextWeight.getText().toString());
                    int age = Integer.parseInt(editTextAge.getText().toString());
                    Date currentDate = new Date();
                    //Отримуємо дані користувача з Firebase
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = mAuth.getCurrentUser();

                    String email = currentUser.getEmail();

                    // Розрахунок добової норми інсуліну
                    this.insulinNorm = calculateInsulinNorm(weight) * insulin_norm_value;

                    // Розрахунок долі інсуліну довгої та короткої дії
                    this.longInsulinRatio = calculateLongInsulinRatio() * insulin_norm_value;
                    this.shortInsulinRatio = (this.insulinNorm - this.longInsulinRatio) * insulin_norm_value;

                    // Розрахунок ранкової та вечірньої дози інсуліну довгої дії
                    this.morningDose = calculateMorningLongDose(longInsulinRatio) * insulin_norm_value;
                    this.eveningDose = calculateEveningLongDose(longInsulinRatio) * insulin_norm_value;

                    // Виведення результатів на екран
                    String resultText = "Email: " + email + "\n"
                            + "Добова норма інсуліну: " + this.insulinNorm + " од.\n"
                            + "Доля інсуліну довгої дії: " + this.longInsulinRatio + "\n"
                            + "Доля інсуліну короткої дії: " + this.shortInsulinRatio + "\n"
                            + "Ранкова доза інсуліну довгої дії: " + this.morningDose + " од.\n"
                            + "Вечірня доза інсуліну довгої дії: " + this.eveningDose + " од.\n"
                            + "Час: " +  currentDate;
                    Toast.makeText(this, resultText, Toast.LENGTH_LONG).show();

                }
    }
}