package binaryblitz.athleteapp.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sw926.imagefileselector.ImageCropper;
import com.sw926.imagefileselector.ImageFileSelector;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import binaryblitz.athleteapp.Abstract.BaseActivity;
import binaryblitz.athleteapp.Custom.ProgressDialog;
import binaryblitz.athleteapp.Data.User;
import binaryblitz.athleteapp.Fragments.MyProgramsFragment;
import binaryblitz.athleteapp.Fragments.ProgramsFragment;
import binaryblitz.athleteapp.Fragments.StatisticsFragment;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.DeviceInfoStore;
import binaryblitz.athleteapp.Server.GetFitServerRequest;
import binaryblitz.athleteapp.Server.OnRequestPerformedListener;
import binaryblitz.athleteapp.Utils.AndroidUtils;

public class ProfileActivity extends BaseActivity {

    private ViewPager mPager;
    StatisticsFragment first;
    MyProgramsFragment second;

    public static String firstName;
    public static String lastName;
    public static String description;

    public static String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        findViewById(R.id.drawer_indicator).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        first = new StatisticsFragment();
        second = new MyProgramsFragment();

        first.setOther(true);
        second.setOther(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        final NavigationAdapter mPagerAdapter = new NavigationAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(3);

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
                        try {
                            if(objects[0].equals("AuthError")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!isFinishing()) {
                                            new AlertDialog.Builder(ProfileActivity.this)
                                                    .setTitle(getString(R.string.title_str))
                                                    .setMessage(getString(R.string.reg_alert_str))
                                                    .setCancelable(false)
                                                    .setPositiveButton(getString(R.string.cont_upcase_str), new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            Intent intent = new Intent(ProfileActivity.this, AuthActivity.class);
                                                            startActivity(intent);
                                                        }
                                                    })
                                                    .setNegativeButton(getString(R.string.cancel_upcase_str), new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    })
                                                    .show();
                                        }
                                    }
                                });

                                return;
                            }

                            if (objects[0].equals("Internet")) {
                                cancelRequest();
                                return;
                            }

                            JSONObject object = (JSONObject) objects[0];

                            if(!object.isNull("banner_url")) {
                                Picasso.with(ProfileActivity.this)
                                        .load(GetFitServerRequest.imagesUrl + object.getString("banner_url"))
                                        .into((ImageView) findViewById(R.id.imageView2));
                                findViewById(R.id.gradient).setVisibility(View.VISIBLE);
                            }

                            if(!object.isNull("avatar_url")) {
                                Picasso.with(ProfileActivity.this)
                                        .load(GetFitServerRequest.imagesUrl + object.getString("avatar_url"))
                                        .into((ImageView) findViewById(R.id.profile_image));
                            }

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
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .profile(getIntent().getStringExtra("id"))
                .perform();

        id = getIntent().getStringExtra("id");
    }

    private class NavigationAdapter extends FragmentPagerAdapter {

        String[] TITLES = {getString(R.string.statistics_upcase_str),
                getString(R.string.programs_upcase_str)};

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