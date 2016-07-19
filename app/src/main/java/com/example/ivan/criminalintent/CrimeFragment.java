package com.example.ivan.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Ivan on 7/18/2016.
 */
public class CrimeFragment extends Fragment {
    private static final String ARG_CRIME_ID = "crime_id"; //Значение передаваемое при вызове Fragment
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE =0; //Назначаем целевой фрагмент

    private Crime mCrime; //Объект модели данных преступления
    private EditText mTitleField; //Поле для ввода названия преступления
    private Button  mDateButton; //Кнопка с датой
    private CheckBox mSolvedCheckBox; //Признак закрытия преступления

    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID,crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //Создаем фрагмент. При создании фрагмента - он не отображается
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID)getArguments().getSerializable(ARG_CRIME_ID); //На этапе создания получаем ID преступления
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    //Отображаем фрагмент
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime,container,false);
        //Подключение виджетов в фрагменте
        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
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
        //Подключение виджета кнопки в фрагменте
        mDateButton = (Button)v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener(){ //Добавляем слушателя на кнопку даты
            public void onClick(View v){
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this,REQUEST_DATE); //Назначаем целевой фрагмент
                dialog.show(manager,DIALOG_DATE); //Вызываем диалоговое окно
            }
        });
        //Назначение слушателя на CheckBox и изменение состояния о раскрытии
        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        return v;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){ //Реакция на получение данных от диалогового окна
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        if (requestCode == REQUEST_DATE){
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        }
    }

    private void updateDate(){ //Обновление даты на кнопке
        mDateButton.setText(mCrime.getDate().toString());
    }
}
