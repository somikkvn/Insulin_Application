package com.example.insulinapplication;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.insulinapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.CustomDialog);
        dialog.setContentView(R.layout.dialog_food);
        dialog.setTitle("Add Food");

        Button buttonSave = dialog.findViewById(R.id.buttonSave);
        Button buttonCancel = dialog.findViewById(R.id.buttonClose);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference productRef = database.getReference("products");
                String name = ((EditText) dialog.findViewById(R.id.editTextName)).getText().toString();
                double kilocal = Double.parseDouble(((EditText) dialog.findViewById(R.id.editTextKilocal)).getText().toString());
                double proteins = Double.parseDouble(((EditText) dialog.findViewById(R.id.editTextProteins)).getText().toString());
                double fats = Double.parseDouble(((EditText) dialog.findViewById(R.id.editTextFats)).getText().toString());
                double carbohydrates = Double.parseDouble(((EditText) dialog.findViewById(R.id.editTextCarbohydrates)).getText().toString());
                int glycemicIndex = Integer.parseInt(((EditText) dialog.findViewById(R.id.editTextGlycemicIndex)).getText().toString());

                productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.w("FoodDialogFragment", name);
                        long count = dataSnapshot.getChildrenCount();
                        Map<String, Object> dataMap = new HashMap<>();
                        dataMap.put("id", count + 1);
                        dataMap.put("name", name);
                        dataMap.put("proteins", proteins);
                        dataMap.put("fats", fats);
                        dataMap.put("carbohydrates", carbohydrates);
                        dataMap.put("kilocal", kilocal);
                        dataMap.put("glycemic_index", glycemicIndex);
                        productRef.child(String.valueOf(productRef.push().getKey())).setValue(dataMap);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("CalendarActivity", "Failed to read value.", error.toException());
                    }
                });
                dismiss();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return dialog;
    }
}
