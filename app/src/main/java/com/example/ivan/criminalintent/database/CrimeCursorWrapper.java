package com.example.ivan.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.ivan.criminalintent.Crime;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Ivan on 7/19/2016.
 */
public class CrimeCursorWrapper extends CursorWrapper {
    public CrimeCursorWrapper(Cursor cursor){
        super(cursor);
    }
    public Crime getCrime(){
        String uuidString = getString(getColumnIndex(CrimeDbSchema.Cols.UUID));
        String title = getString(getColumnIndex(CrimeDbSchema.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeDbSchema.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeDbSchema.Cols.SOLVED));
        String suspect = getString(getColumnIndex(CrimeDbSchema.Cols.SUSPECT));

        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved !=0);
        crime.setSuspect(suspect);
        return crime;
    }
}
