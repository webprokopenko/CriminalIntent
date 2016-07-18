package com.example.ivan.criminalintent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by Ivan on 7/18/2016.
 */
public class CrimeFragment extends Fragment {
    private Crime mCrime; //Объект модели данных преступления
    private EditText mTitleField; //Поле для ввода названия преступления

    //Создаем фрагмент. При создании фрагмента - он не отображается
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCrime = new Crime();
    }

    //Отображаем фрагмент
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime,container,false);
        //Подключение виджетов в фрагменте
        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Здесь намеренно оставлено пустое место
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //При изменении поля виджета EditText в модели Crime сохраняем title
                mCrime.setTitle(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Здесь намеренно оставлено пустое место
            }
        });

        return v;
    }
}
