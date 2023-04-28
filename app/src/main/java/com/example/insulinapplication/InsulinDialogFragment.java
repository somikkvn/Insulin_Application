package com.example.insulinapplication;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.insulinapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InsulinDialogFragment extends DialogFragment {

    TextView textViewDateTime;
    int selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.CustomDialog);
        dialog.setContentView(R.layout.dialog_insulin);
        dialog.setTitle("Додати дозування інсуліном");

        Spinner spinner_insulin = dialog.findViewById(R.id.insulinSpinner);

        Button buttonSave = dialog.findViewById(R.id.buttonSave);
        Button buttonCancel = dialog.findViewById(R.id.buttonClose);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference insulinsRef = database.getReference("insulins");
        insulinsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> options = new ArrayList<String>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String name = ds.child("name").getValue(String.class);
                    options.add(name);
                    Log.w("InsulinDialogFragment", name);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, options);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_insulin.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("CalendarActivity", "Failed to read value.", error.toException());
            }

        });

        textViewDateTime = dialog.findViewById(R.id.textViewDateTime);
        Button buttonSelectDateTime = dialog.findViewById(R.id.buttonSelectDateTime);

        buttonSelectDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });


        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference productRef = database.getReference("insulin_diary");

                productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String selectedInsulin = spinner_insulin.getSelectedItem().toString();
                        Calendar calendar = Calendar.getInstance();
                        long timestamp = calendar.getTimeInMillis();
                        long count = dataSnapshot.getChildrenCount();
                        Map<String, Object> dataMap = new HashMap<>();
                        dataMap.put("id", count + 1);
                        dataMap.put("insulin", selectedInsulin);
                        dataMap.put("timestamp", timestamp);
                        productRef.child(String.valueOf(productRef.push().getKey())).setValue(dataMap);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("CalendarActivity", "Failed to read value.", error.toException());
                    }
                });
                dismiss();
                long timerDuration = 1 * 1 * 1000; // 30 хвилин в мілісекундах
                long timerInterval = 1000; // Інтервал виклику методу onTick() (необов'язково)
                MedicineTimer timer = new MedicineTimer(timerDuration, timerInterval, getActivity());
                timer.start();
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

    private void showDateTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedYear = year;
                        selectedMonth = month;
                        selectedDay = dayOfMonth;

                        updateDateTimeText();
                    }
                },
                year,
                month,
                dayOfMonth);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getActivity(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        selectedHour = hourOfDay;
                        selectedMinute = minute;

                        updateDateTimeText();
                    }
                },
                hourOfDay,
                minute,
                DateFormat.is24HourFormat(getActivity()));

        datePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                timePickerDialog.show();
            }
        });

        datePickerDialog.show();
    }

    private void updateDateTimeText() {
        String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
        String selectedTime = selectedHour + ":" + selectedMinute;
        String dateTime = "Час: " + selectedDate + " " + selectedTime;
        textViewDateTime.setText(dateTime);
    }

}
