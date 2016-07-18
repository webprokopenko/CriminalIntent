package com.example.ivan.criminalintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Ivan on 7/18/2016.
 */
public class CrimeLab {

    private static CrimeLab sCrimeLab;
    private List<Crime> mCrimes;

    public static CrimeLab get(Context context){
        if (sCrimeLab == null){
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context){
        mCrimes = new ArrayList<>();
        for (int i=0;i<100;i++){
            Crime crime = new Crime();
            crime.setTitle("Crime #" + i);
            crime.setSolved(i %2 ==0); //Для каждого второго объекта
            mCrimes.add(crime); // Добавляем в список массива объектов Crime одно преступление
        }
    }
    //Возаращаем список всех преступлений
    public List<Crime> getCrimes(){
        return mCrimes;
    }
    //Возвращаем искомое преступление
    public Crime getCrime(UUID id){
        for (Crime crime : mCrimes){
            if (crime.getId().equals(id)){
                return crime;
            }
        }
        return null;
    }
}
