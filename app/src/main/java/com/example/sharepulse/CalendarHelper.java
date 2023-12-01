package com.example.sharepulse;

import android.app.Activity;
import android.app.DatePickerDialog;

public class CalendarHelper {

    public static void openCalendar(Activity activity, String date, DatePickerDialog.OnDateSetListener listener) {
        String[] bodArr = date.split("/");
        int day = Integer.parseInt(bodArr[2]);
        int month = Integer.parseInt(bodArr[1]) - 1;
        int year = Integer.parseInt(bodArr[0]);

        DatePickerDialog dialog = new DatePickerDialog(activity, listener, year, month, day);
        dialog.show();
    }
}
