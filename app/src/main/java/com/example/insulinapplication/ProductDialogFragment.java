package com.example.insulinapplication;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDialogFragment extends DialogFragment {

    int rowCount = 1;
    private LinearLayout parentLayout;
    private ArrayAdapter<String> adapter;
    ArrayList<EditText> editTextList;
    ArrayList<Spinner> spinnerList;
    Map<String, Map<String, Object>> productsMap;

    int count_meal = 0, count_product = 0;
    double sum_ins;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.CustomDialog);
        dialog.setContentView(R.layout.dialog_meals);
        dialog.setTitle("Add Meals");
        editTextList = new ArrayList<>();
        spinnerList = new ArrayList<>();
        parentLayout = dialog.findViewById(R.id.parentLayout);
        productsMap = new HashMap<>();
        Spinner spinnerMeal = dialog.findViewById(R.id.productMeals);
        String[] options  = getResources().getStringArray(R.array.my_options);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMeal.setAdapter(adapter);

        Spinner spinner = dialog.findViewById(R.id.productSpinner1);
        EditText editView = dialog.findViewById(R.id.productEditText1);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("products");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> options = new ArrayList<String>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String name = ds.child("name").getValue(String.class);
                    Map<String, Object> productMap = new HashMap<>();
                    productMap.put("carbohydrates", ds.child("carbohydrates").getValue(Double.class));
                    productMap.put("proteins", ds.child("proteins").getValue(Double.class));
                    productMap.put("fats", ds.child("fats").getValue(Double.class));
                    productMap.put("glycemic_index", ds.child("glycemic_index").getValue(Double.class));
                    productsMap.put(name, productMap);
                    options.add(name);
                    Log.w("MainActivity", name);

                }
                adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, options);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("CalendarActivity", "Failed to read value.", error.toException());
            }

        });

        Button buttonSave = dialog.findViewById(R.id.buttonSave);
        Button buttonCancel = dialog.findViewById(R.id.buttonClose);

        spinnerList.add(spinner);
        editTextList.add(editView);

        DatabaseReference mealDiaryRef = database.getReference("meal_diary");
        Map<String, Object> mealDiaryMap = new HashMap<>();
        mealDiaryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                count_meal = (int) dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("CalendarActivity", "Failed to read value.", error.toException());
            }
        });
        DatabaseReference productDiaryRef = database.getReference("product_diary");

        productDiaryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                count_product = (int) dataSnapshot.getChildrenCount();
                Log.w("getChildrenCount", String.valueOf((int) dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("CalendarActivity", "Failed to read value.", error.toException());
            }
        });

        double recommended_dose = 1;

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Отримаємо ті дані які вибрав користувач ( снідаток, обід чи вечеря)
                sum_ins = 0;
                Spinner spinnerMeal = dialog.findViewById(R.id.productMeals);
                String mealValue = spinnerMeal.getSelectedItem().toString();
                // Обробляємо ті продукти і вагу які вибрав користувач
                for (int i = 0; i < rowCount; i++) {
                    double weight = Double.parseDouble(((EditText) editTextList.get(i)).getText().toString());
                    String spinnerText = ((Spinner) spinnerList.get(i)).getSelectedItem().toString();
                    Map<String, Object> productDiaryMap = new HashMap<>();
                    productDiaryMap.put("id", count_product + i + 1);
                    productDiaryMap.put("product", spinnerText);
                    productDiaryMap.put("diary_id", count_meal + 1);
                    productDiaryMap.put("weight", weight);
                    //вносимо обрані користувачем продукти в журнал щоденника продукта
                    productDiaryRef.child(String.valueOf(productDiaryRef.push().getKey())).setValue(productDiaryMap);
                    Map<String, Object> product_data = productsMap.get(spinnerText);
                    double carbohydrates = (double) product_data.get("carbohydrates");
                    double proteins = (double) product_data.get("proteins");
                    double fats = (double) product_data.get("fats");
                    double glycemicIndex = (double) product_data.get("glycemic_index");
                    double k1;
                    if (mealValue.equals("Сніданок")) {
                        k1 = 2;
                    } else if ((mealValue.equals("Обід")) ) {
                        k1 = 1.5;
                    } else {
                        k1 = 1.2;
                    }
                    double k2 = k1 - 1;
                    double xe = carbohydrates * weight / 100;
                    double first_part = carbohydrates / 100 * glycemicIndex / 100 / xe * k1;
                    double second_part = carbohydrates / 100 * (100 - glycemicIndex) / 100 / xe * k1;
                    double third_part = proteins / 100 * 4.1 / 100 * k2;
                    double fourth_part = fats / 100 * 9.3 / 100 * k2;
                    sum_ins = sum_ins + (first_part + second_part + third_part + fourth_part) * weight;
                }
                sum_ins = Math.round(sum_ins * 100.0) / 100.0;
                mealDiaryMap.put("id", count_meal + 1);
                mealDiaryMap.put("recommended_dose", sum_ins);
                mealDiaryMap.put("meal", mealValue);
                mealDiaryMap.put("date", (new Date()).toString());
                mealDiaryRef.child(String.valueOf(mealDiaryRef.push().getKey())).setValue(mealDiaryMap);
                dismiss();
                showMessageDialogFragment(String.valueOf(sum_ins));
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button btnAddProduct = dialog.findViewById(R.id.btnAddProduct);
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRow();
            }
        });

        Button btnPlus = dialog.findViewById(R.id.btnPlus);
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FoodDialogFragment dialogFragment = new FoodDialogFragment();
                dialogFragment.show(requireActivity().getSupportFragmentManager(), "FoodDialogFragment");
            }
        });

        return dialog;
    }
    private void addRow() {
        rowCount++;
        Dialog dialog = getDialog();
        if (dialog != null) {
            LinearLayout layout = dialog.findViewById(R.id.row1);

            LinearLayout newRow = new LinearLayout(requireContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            newRow.setLayoutParams(params);
            newRow.setOrientation(LinearLayout.HORIZONTAL);

            TextView textView = new TextView(requireContext());
            textView.setText("Продукт " + (rowCount) + ":  ");
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            textView.setTextColor(Color.WHITE);
            textView.setTag("productTextView" + String.valueOf(rowCount));

            Spinner spinner = new Spinner(requireContext());

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1

            );
            layoutParams.setMarginEnd(20);
//            layoutParams.setMarginEnd(20);
            spinner.setLayoutParams(layoutParams);
            spinner.setAdapter(adapter);
            spinner.setPadding(10, 10, 10, 10);
            spinner.setBackgroundDrawable(getResources().getDrawable(R.color.white));
            spinner.setTag("productSpinner" + String.valueOf(rowCount));


            EditText editView = new EditText(requireContext()); // Створюємо новий TextView
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams2.setMarginEnd(20);
            editView.setLayoutParams(layoutParams2);
            editView.setHint("Вага (г)");
            editView.setBackgroundDrawable(getResources().getDrawable(R.color.white));
            editView.setTag("productEditText" + String.valueOf(rowCount));
            newRow.addView(textView);
            newRow.addView(spinner);
            newRow.addView(editView);
            spinnerList.add(spinner);
            editTextList.add(editView);
            parentLayout.addView(newRow);
        }
    }

    private void showMessageDialogFragment(String s) {
        MessageDialogFragment quickModalFragment = new MessageDialogFragment(s);
        quickModalFragment.show(getFragmentManager(), "MessageDialogFragment");
    }

}
