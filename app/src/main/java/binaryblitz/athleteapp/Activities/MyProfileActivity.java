package binaryblitz.athleteapp.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import binaryblitz.athleteapp.Abstract.BaseActivity;
import binaryblitz.athleteapp.Data.User;
import binaryblitz.athleteapp.Fragments.MyProgramsFragment;
import binaryblitz.athleteapp.Fragments.NewsFragment;
import binaryblitz.athleteapp.Fragments.ProgramsFragment;
import binaryblitz.athleteapp.Fragments.StatisticsFragment;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.DeviceInfoStore;
import binaryblitz.athleteapp.Server.GetFitServerRequest;
import binaryblitz.athleteapp.Server.OnRequestPerformedListener;

public class MyProfileActivity extends BaseActivity {

    private ViewPager mPager;

    StatisticsFragment first;
    MyProgramsFragment second;

    public static String firstName;
    public static String lastName;
    public static String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile_layout);
        init();

        first = new StatisticsFragment();
        second = new MyProgramsFragment();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        final NavigationAdapter mPagerAdapter = new NavigationAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(3);

        findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyProfileActivity.this, SettingsActivity.class);
                startActivity(intent);
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

        GetFitServerRequest.with(this)
                .authorize()
                .listener(new OnRequestPerformedListener() {
                    @Override
                    public void onRequestPerformedListener(Object... objects) {
                        Log.e("qwerty", objects[0].toString());
                        try {

                            JSONObject object = (JSONObject) objects[0];

                            Picasso.with(MyProfileActivity.this)
                                    .load(GetFitServerRequest.imagesUrl + object.getString("banner_url"))
                                    .into((ImageView) findViewById(R.id.imageView2));

                            Picasso.with(MyProfileActivity.this)
                                    .load(GetFitServerRequest.imagesUrl + object.getString("avatar_url"))
                                    .into((ImageView) findViewById(R.id.profile_image));

                            firstName = object.getString("first_name");
                            lastName = object.getString("last_name");
                            description = object.getString("description");

                            ((TextView) findViewById(R.id.textView)).setText(object.getString("first_name") + " " + object.getString("last_name"));
                            ((TextView) findViewById(R.id.textView2)).setText(object.getString("description"));
                            Calendar start = Calendar.getInstance();
                            try {
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                                format.setTimeZone(TimeZone.getTimeZone("UTC"));
                                Date date = format.parse(object.getString("birthdate"));
                                start.setTime(date);
                            } catch (Exception ignored) {}

                            User user = new User(
                                    object.getString("id"),
                                    object.getString("first_name") + " " + object.getString("last_name"),
                                    object.getString("phone_number"),
                                    object.getString("description"),
                                    GetFitServerRequest.imagesUrl + object.getString("avatar_url"),
                                    GetFitServerRequest.imagesUrl + object.getString("banner_url"),
                                    object.isNull("weight") ? 0 : object.getInt("weight"),
                                    object.isNull("height") ? 0 : object.getInt("height"),
                                    object.isNull("gender") ? true : object.getBoolean("gender"),
                                    start
                            );

                            DeviceInfoStore.saveUser(user);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .getUser()
                .perform();
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            ((TextView) findViewById(R.id.textView)).setText(firstName + " " + lastName);
            ((TextView) findViewById(R.id.textView2)).setText(description);
        } catch (Exception e) {

        }
    }

    private class NavigationAdapter extends FragmentPagerAdapter {

        String[] TITLES = {"STATISTICS",
                "PROGRAMS"};

        public NavigationAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0) {
                return first;
            } else {
                return second;
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