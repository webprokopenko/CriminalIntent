package com.example.ivan.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
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
    private static final int REQUEST_CONTACT = 1; //Константа для кода запроса. Нужна для того, чтобы вернуть результат

    private Crime mCrime; //Объект модели данных преступления
    private EditText mTitleField; //Поле для ввода названия преступления
    private Button  mDateButton; //Кнопка с датой
    private CheckBox mSolvedCheckBox; //Признак закрытия преступления
    private Button  mReportButton; //Кнопка отправки отчета
    private Button  mSuspectButton; //Кнопка выбора подозреваемого

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

    public void onPause(){
        super.onPause();

        CrimeLab.get(getActivity()).updateCrime(mCrime);
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
                dialog.setTargetFragment(CrimeFragment.this,REQUEST_DATE); //Назначаем целевой фрагмент!!!!!
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

        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() { //Слушатель на кнопку отправки отчета
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND); //Создание неявного интента на отправку текста
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.crime_report_subject));
                i = Intent.createChooser(i,getString(R.string.send_report)); //Добавляем окно выбора
                startActivity(i);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });
        if(mCrime.getSuspect() != null){
            mSuspectButton.setText(mCrime.getSuspect());
        }
        //Защита от отсутствия контактных приложений
        //PackageManager известно все о компонентах, установленных на устройстве, включая все его активности.
        //Вызывае resolveActivity(Intent,int) вы приказываете найти активность, соответствующую переданному интенту
        //Если поиск прошел успешно, возвращается экземпляр ResolveInfo. который сообщает полную информацию о найденной активности.
        //С другой стороны, если поиск вернул null, все кончено - контактного приложения нет, поэтому бесполезная кнопка просто блокируется
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,PackageManager.MATCH_DEFAULT_ONLY) == null){
            mSuspectButton.setEnabled(false);
        }

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
        } else if(requestCode == REQUEST_CONTACT && data != null){ //Обработка возвращаемых данных от окна контактов
            Uri contactUri = data.getData();
            //Определение полей, значения которых должны быть возвращены запросом.
            String[] queryFields = new String[]{ContactsContract.Contacts.DISPLAY_NAME};
            //Выполнение запроса - contactUri здесь выполняет функции устовия where
            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields,null,null,null);

            try {
                //Проверка получения результатов
                if(c.getCount() ==0){
                    return;
                }
                //Извлечение первого столбца данных - имени подозреваемого.
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
            } finally {
                c.close();
            }
        }
    }

    private void updateDate(){ //Обновление даты на кнопке
        mDateButton.setText(mCrime.getDate().toString());
    }

    private String getCrimeReport(){
        String  solvedString = null;
        if(mCrime.isSolved()){
            solvedString = getString(R.string.crime_report_solved);
        } else{
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat,mCrime.getDate()).toString();
        String suspect = mCrime.getSuspect();
        if(suspect == null){
            suspect = getString(R.string.crime_report_no_suspect);
        } else{
            suspect = getString(R.string.crime_report_suspect, suspect);
        }
        String report = getString(R.string.crime_report,mCrime.getTitle(),dateString,solvedString,suspect);

        return report;
    }
}
