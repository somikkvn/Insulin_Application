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

public class MainActivity extends AppCompatActivity {

    Button btnLogOut;
    FirebaseAuth mAuth;
    private EditText editTextHeight, editTextWeight, editTextAge;
    private int weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogOut = findViewById(R.id.btnLogout);
        mAuth = FirebaseAuth.getInstance();
        editTextHeight = findViewById(R.id.editTextHeight);
        editTextWeight = findViewById(R.id.editTextWeight);
        editTextAge = findViewById(R.id.editTextAge);

        Button buttonCalculate = findViewById(R.id.buttonCalculate);
        buttonCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateInsulinDose();
            }
        });

        btnLogOut.setOnClickListener(view ->{
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

    public void calculateInsulinDose() {
        // Отримання даних користувача
        int height = Integer.parseInt(editTextHeight.getText().toString());
        weight = Integer.parseInt(editTextWeight.getText().toString());
        int age = Integer.parseInt(editTextAge.getText().toString());

        // Розрахунок добової норми інсуліну
        double insulinNorm = calculateInsulinNorm(weight);

        // Розрахунок долі інсуліну довгої та короткої дії
        double longInsulinRatio = calculateLongInsulinRatio();
        double shortInsulinRatio = 1 - longInsulinRatio;

        // Розрахунок ранкової та вечірньої дози інсуліну довгої дії
        double morningDose = calculateMorningLongDose(longInsulinRatio);
        double eveningDose = calculateEveningLongDose(longInsulinRatio);

//        // Виведення результатів на екран
//        String resultText = "Добова норма інсуліну: " + insulinNorm + " од.\n"
//                + "Доля інсуліну довгої дії: " + longInsulinRatio + "\n"
//                + "Доля інсуліну короткої дії: " + shortInsulinRatio + "\n"
//                + "Ранкова доза інсуліну довгої дії: " + morningDose + " од.\n"
//                + "Вечірня доза інсуліну довгої дії: " + eveningDose + " од.\n";
//        Toast.makeText(this, resultText, Toast.LENGTH_LONG).show();
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

        // Вперше виявлений ЦД типу 1 (коефіцієнт 0,5)
        switch(view.getId()) {
            case R.id.radioButton:
                if (checked){
                    int height = Integer.parseInt(editTextHeight.getText().toString());
                    weight = Integer.parseInt(editTextWeight.getText().toString());
                    int age = Integer.parseInt(editTextAge.getText().toString());

                    // Розрахунок добової норми інсуліну
                    double insulinNorm = calculateInsulinNorm(weight) * 0.5;

                    // Розрахунок долі інсуліну довгої та короткої дії
                    double longInsulinRatio = calculateLongInsulinRatio() * 0.5;
                    double shortInsulinRatio = (insulinNorm - longInsulinRatio) * 0.5;

                    // Розрахунок ранкової та вечірньої дози інсуліну довгої дії
                    double morningDose = calculateMorningLongDose(longInsulinRatio) * 0.5;
                    double eveningDose = calculateEveningLongDose(longInsulinRatio) * 0.5;

                    // Виведення результатів на екран
                    String resultText = "Добова норма інсуліну: " + insulinNorm + " од.\n"
                            + "Доля інсуліну довгої дії: " + longInsulinRatio + "\n"
                            + "Доля інсуліну короткої дії: " + shortInsulinRatio + "\n"
                            + "Ранкова доза інсуліну довгої дії: " + morningDose + " од.\n"
                            + "Вечірня доза інсуліну довгої дії: " + eveningDose + " од.\n";
                    Toast.makeText(this, resultText, Toast.LENGTH_LONG).show();
                }
                break;

            // ЦД типу 1 тривалістю більше року в хорошій компенсації (k=0,6)
            case R.id.radioButton2:
                if (checked){
                    int height = Integer.parseInt(editTextHeight.getText().toString());
                    weight = Integer.parseInt(editTextWeight.getText().toString());
                    int age = Integer.parseInt(editTextAge.getText().toString());

                    // Розрахунок добової норми інсуліну
                    double insulinNorm = calculateInsulinNorm(weight) * 0.6;

                    // Розрахунок долі інсуліну довгої та короткої дії
                    double longInsulinRatio = calculateLongInsulinRatio() * 0.6;
                    double shortInsulinRatio = (insulinNorm - longInsulinRatio) * 0.6;

                    // Розрахунок ранкової та вечірньої дози інсуліну довгої дії
                    double morningDose = calculateMorningLongDose(longInsulinRatio) * 0.6;
                    double eveningDose = calculateEveningLongDose(longInsulinRatio) * 0.6;

                    // Виведення результатів на екран
                    String resultText = "Добова норма інсуліну: " + insulinNorm + " од.\n"
                            + "Доля інсуліну довгої дії: " + longInsulinRatio + "\n"
                            + "Доля інсуліну короткої дії: " + shortInsulinRatio + "\n"
                            + "Ранкова доза інсуліну довгої дії: " + morningDose + " од.\n"
                            + "Вечірня доза інсуліну довгої дії: " + eveningDose + " од.\n";
                    Toast.makeText(this, resultText, Toast.LENGTH_LONG).show();
                }
                    break;

                // ЦД типу 1 тривалістю більше року при нестійкій компенсації (k=0,7)
            case R.id.radioButton3:
                    if (checked){
                        int height = Integer.parseInt(editTextHeight.getText().toString());
                        weight = Integer.parseInt(editTextWeight.getText().toString());
                        int age = Integer.parseInt(editTextAge.getText().toString());

                        // Розрахунок добової норми інсуліну
                        double insulinNorm = calculateInsulinNorm(weight) * 0.7;

                        // Розрахунок долі інсуліну довгої та короткої дії
                        double longInsulinRatio = calculateLongInsulinRatio() * 0.7;
                        double shortInsulinRatio = (insulinNorm - longInsulinRatio) * 0.7;

                        // Розрахунок ранкової та вечірньої дози інсуліну довгої дії
                        double morningDose = calculateMorningLongDose(longInsulinRatio) * 0.7;
                        double eveningDose = calculateEveningLongDose(longInsulinRatio) * 0.7;

                        // Виведення результатів на екран
                        String resultText = "Добова норма інсуліну: " + insulinNorm + " од.\n"
                                + "Доля інсуліну довгої дії: " + longInsulinRatio + "\n"
                                + "Доля інсуліну короткої дії: " + shortInsulinRatio + "\n"
                                + "Ранкова доза інсуліну довгої дії: " + morningDose + " од.\n"
                                + "Вечірня доза інсуліну довгої дії: " + eveningDose + " од.\n";
                        Toast.makeText(this, resultText, Toast.LENGTH_LONG).show();
                    }
                    break;

            // ЦД типу 1 в ситуації декомпенсації (k=0,8)
            case R.id.radioButton4:
                if (checked){
                    int height = Integer.parseInt(editTextHeight.getText().toString());
                    weight = Integer.parseInt(editTextWeight.getText().toString());
                    int age = Integer.parseInt(editTextAge.getText().toString());

                    // Розрахунок добової норми інсуліну
                    double insulinNorm = calculateInsulinNorm(weight) * 0.8;

                    // Розрахунок долі інсуліну довгої та короткої дії
                    double longInsulinRatio = calculateLongInsulinRatio() * 0.8;
                    double shortInsulinRatio = (insulinNorm - longInsulinRatio) * 0.8;

                    // Розрахунок ранкової та вечірньої дози інсуліну довгої дії
                    double morningDose = calculateMorningLongDose(longInsulinRatio) * 0.8;
                    double eveningDose = calculateEveningLongDose(longInsulinRatio) * 0.8;

                    // Виведення результатів на екран
                    String resultText = "Добова норма інсуліну: " + insulinNorm + " од.\n"
                            + "Доля інсуліну довгої дії: " + longInsulinRatio + "\n"
                            + "Доля інсуліну короткої дії: " + shortInsulinRatio + "\n"
                            + "Ранкова доза інсуліну довгої дії: " + morningDose + " од.\n"
                            + "Вечірня доза інсуліну довгої дії: " + eveningDose + " од.\n";
                    Toast.makeText(this, resultText, Toast.LENGTH_LONG).show();
                }
                break;

            // ЦД типу 1 в стані кетоацидозу (k=0,9)
            case R.id.radioButton5:
                if (checked){
                    int height = Integer.parseInt(editTextHeight.getText().toString());
                    weight = Integer.parseInt(editTextWeight.getText().toString());
                    int age = Integer.parseInt(editTextAge.getText().toString());

                    // Розрахунок добової норми інсуліну
                    double insulinNorm = calculateInsulinNorm(weight) * 0.9;

                    // Розрахунок долі інсуліну довгої та короткої дії
                    double longInsulinRatio = calculateLongInsulinRatio() * 0.9;
                    double shortInsulinRatio = (insulinNorm - longInsulinRatio) * 0.9;

                    // Розрахунок ранкової та вечірньої дози інсуліну довгої дії
                    double morningDose = calculateMorningLongDose(longInsulinRatio) * 0.9;
                    double eveningDose = calculateEveningLongDose(longInsulinRatio) * 0.9;

                    // Виведення результатів на екран
                    String resultText = "Добова норма інсуліну: " + insulinNorm + " од.\n"
                            + "Доля інсуліну довгої дії: " + longInsulinRatio + "\n"
                            + "Доля інсуліну короткої дії: " + shortInsulinRatio + "\n"
                            + "Ранкова доза інсуліну довгої дії: " + morningDose + " од.\n"
                            + "Вечірня доза інсуліну довгої дії: " + eveningDose + " од.\n";
                    Toast.makeText(this, resultText, Toast.LENGTH_LONG).show();
                }
                break;

            // ЦД типу 1 в пубертатному періоді або в III триместрі вагітності (k=1)
            case R.id.radioButton6:
                if (checked){
                    int height = Integer.parseInt(editTextHeight.getText().toString());
                    weight = Integer.parseInt(editTextWeight.getText().toString());
                    int age = Integer.parseInt(editTextAge.getText().toString());

                    // Розрахунок добової норми інсуліну
                    double insulinNorm = calculateInsulinNorm(weight);

                    // Розрахунок долі інсуліну довгої та короткої дії
                    double longInsulinRatio = calculateLongInsulinRatio();
                    double shortInsulinRatio = insulinNorm - longInsulinRatio;

                    // Розрахунок ранкової та вечірньої дози інсуліну довгої дії
                    double morningDose = calculateMorningLongDose(longInsulinRatio);
                    double eveningDose = calculateEveningLongDose(longInsulinRatio);

                    // Виведення результатів на екран
                    String resultText = "Добова норма інсуліну: " + insulinNorm + " од.\n"
                            + "Доля інсуліну довгої дії: " + longInsulinRatio + "\n"
                            + "Доля інсуліну короткої дії: " + shortInsulinRatio + "\n"
                            + "Ранкова доза інсуліну довгої дії: " + morningDose + " од.\n"
                            + "Вечірня доза інсуліну довгої дії: " + eveningDose + " од.\n";
                    Toast.makeText(this, resultText, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}