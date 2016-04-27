package binaryblitz.athleteapp.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import binaryblitz.athleteapp.Abstract.BaseActivity;
import binaryblitz.athleteapp.Data.Professional;
import binaryblitz.athleteapp.Data.ProfessionalType;
import binaryblitz.athleteapp.Fragments.NewsFragment;
import binaryblitz.athleteapp.Fragments.ProgramsFragment;
import binaryblitz.athleteapp.R;

public class ProfProfileActivity extends BaseActivity {

    private ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prof_profile_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        NavigationAdapter mPagerAdapter = new NavigationAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(3);

        findViewById(R.id.drawer_indicator).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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

        final View vBgLike;
        final ImageView ivLike;

        vBgLike = findViewById(R.id.follow_btn);
        ivLike = (ImageView) findViewById(R.id.imageView6);

        final Professional prof = new Professional("1", "photo", null, R.drawable.test4, R.drawable.test10,
                "Mike Silvestri", "Who has taken an extended leave of absence from training?",
                ProfessionalType.COACH, false, 10, 43, 4.9);

        vBgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!prof.isFollowing()) {
                    vBgLike.setBackgroundResource(R.drawable.blue_btn);
                    ivLike.setImageResource(R.drawable.done_ic);
                    ((TextView) findViewById(R.id.textView6)).setTextColor(Color.WHITE);
                } else {
                    vBgLike.setBackgroundResource(R.drawable.blue_border);
                    ivLike.setImageResource(R.drawable.plus_ic);
                    ((TextView) findViewById(R.id.textView6)).setTextColor(Color.parseColor("#3695ed"));
                }

                prof.setFollowing(!prof.isFollowing());
            }
        });
    }


    private class NavigationAdapter extends FragmentPagerAdapter {

        String[] TITLES = {"PROGRAMS",
                "NEWS"};

        public NavigationAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0) {
                return new ProgramsFragment();
            } else {
                return new NewsFragment();
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
