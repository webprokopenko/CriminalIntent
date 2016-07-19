package com.example.ivan.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Ivan on 7/19/2016.
 */
public class DatePickerFragment extends DialogFragment {
    public static final String EXTRA_DATE = "com.example.ivan.criminalintent.date"; //Обратный вызов целевого фрагмента
    private static final String ARG_DATE = "date";
    private DatePicker mDatePicker;

    public static DatePickerFragment newInstance(Date date){
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE,date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public Dialog onCreateDialog(Bundle savedInstanceState){ // Создаем диалоговое окно AlertDialog
        Date date = (Date)getArguments().getSerializable(ARG_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date,null); // Находим вью календаря

        mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_date_picker); // Находим Date Picker
        mDatePicker.init(year,month,day,null); // Устанавливаем текущую дату в Date Picker
        return new AlertDialog.Builder(getActivity())
                    .setView(v) //Устанавливаем вью
                    .setTitle(R.string.date_picker_title) // Устанавливаем титл
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) { //Передача информации
                                    int year = mDatePicker.getYear();
                                    int month = mDatePicker.getMonth();
                                    int day = mDatePicker.getDayOfMonth();
                                    Date date = new GregorianCalendar(year,month,day).getTime();

                                    sendResult(Activity.RESULT_OK,date);
                                }
                            }
                    )// Устанавливаем кнопку
                    .create(); //Создаем диалоговое окно
    }

    private void sendResult(int resultCode,Date date){
        if(getTargetFragment() == null)
            return;

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE,date);

        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent); //Отправка результата целевого фрагмента
    }
}
