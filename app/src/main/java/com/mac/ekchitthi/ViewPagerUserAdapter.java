package com.mac.ekchitthi;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.mac.ekchitthi.Fragments.PostFragment;
import com.mac.ekchitthi.Fragments.StampsFragment;
import com.mac.ekchitthi.Fragments.StatusFragment;


public class ViewPagerUserAdapter extends FragmentPagerAdapter {

    private String[] titles = new String[]{ "Letter box","Track","Stamps box"};


    public ViewPagerUserAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new PostFragment();
            case 1:
                return new StatusFragment();
            case 2:
                return new StampsFragment();

        }
        return new PostFragment();
    }

    @Override
    public int getCount() {
        return titles.length;
    }
}
