package binaryblitz.athleteapp.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import binaryblitz.athleteapp.Abstract.BaseActivity;
import binaryblitz.athleteapp.Data.ProfessionalType;
import binaryblitz.athleteapp.Fragments.ProfsFragment;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.GetFitServerRequest;
import binaryblitz.athleteapp.Server.OnRequestPerformedListener;

public class ProfsActivity extends BaseActivity {

    private ViewPager mPager;

    ProfsFragment first;
    ProfsFragment second;
    ProfsFragment third;

    private View fab;

    public static boolean filter = false;
    public static String specFilter = "0";
    public static String orderFilter = "followers_count";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profs_activity);
        init();

        NavigationAdapter mPagerAdapter = new NavigationAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(3);

        first = new ProfsFragment();
        second = new ProfsFragment();
        third = new ProfsFragment();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mPager);
        tabLayout.setBackgroundColor(Color.TRANSPARENT);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        fab = findViewById(R.id.fab12);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfsActivity.this, TrainersFilterActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(filter) {
            filter = false;
            first.filter(specFilter, orderFilter);
            second.filter(specFilter, orderFilter);
            third.filter(specFilter, orderFilter);
        }
    }

    public View getFab() {
        return fab;
    }

    private class NavigationAdapter extends FragmentPagerAdapter {

        String[] TITLES = {"COACHES",
                "DOCTORS",
                "NUTRITIONISTS"};

        public NavigationAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    first.setType(ProfessionalType.COACH);
                    return first;
                case 1:
                    second.setType(ProfessionalType.DOCTOR);
                    return second;
                case 2:
                    third.setType(ProfessionalType.NUTRITIONIST);
                    return third;
                default:
                    return first;
            }
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }
    }

}
