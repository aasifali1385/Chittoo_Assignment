package com.aa.chittooassignment.Fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.aa.chittooassignment.Fragments.FragmentCert;
import com.aa.chittooassignment.Fragments.FragmentChat;
import com.aa.chittooassignment.Fragments.FragmentProfile;
import com.aa.chittooassignment.Fragments.FragmentScore;

public class FragmentAdapter extends FragmentStateAdapter {

    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position)
        {
            case 1 :
                return new FragmentProfile();
            case 2 :
                return new FragmentChat();
            case 3:
                return new FragmentScore();
        }
        return new FragmentCert();
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
