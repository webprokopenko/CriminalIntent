package com.example.ivan.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by Ivan on 7/18/2016.
 */
public class CrimeListActivity extends SingleFragmentActivity {
    protected Fragment createFragment(){
        return new CrimeListFragment();
    }
}
