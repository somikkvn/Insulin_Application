package com.example.insulinapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class MessageDialogFragment extends DialogFragment {

    private String message_text_1 = "Рекомендаційна доза короткого інсуліну для цього прийому їжі складає:\n";
    private String message_text_2 = "\nНе забудьте додати інформацію про введення інсуліну, після дозування.\nБудьте здорові!";
    private String message;
    public MessageDialogFragment(String value) {
        this.message = message_text_1 + value +message_text_2 ;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }
}