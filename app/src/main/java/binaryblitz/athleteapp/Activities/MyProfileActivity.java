package binaryblitz.athleteapp.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
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
import binaryblitz.athleteapp.Fragments.NewsFragment;
import binaryblitz.athleteapp.Fragments.ProgramsFragment;
import binaryblitz.athleteapp.Fragments.StatisticsFragment;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.DeviceInfoStore;
import binaryblitz.athleteapp.Server.GetFitServerRequest;
import binaryblitz.athleteapp.Server.OnRequestPerformedListener;
import binaryblitz.athleteapp.Utils.AndroidUtils;

public class MyProfileActivity extends BaseActivity {

    private ViewPager mPager;
    String base64 = null;
    StatisticsFragment first;
    MyProgramsFragment second;

    public static String firstName;
    public static String lastName;
    public static String description;

    private static boolean avatar = false;

    private ImageCropper mImageCropper;
    private File mCurrentSelectFile;
    private ImageFileSelector mImageFileSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile_layout);
        init();

        first = new StatisticsFragment();
        second = new MyProgramsFragment();

        first.setOther(false);
        second.setOther(false);

        mImageFileSelector = new ImageFileSelector(this);
        mImageFileSelector.setCallback(new ImageFileSelector.Callback() {
            @Override
            public void onSuccess(final String file) {
                if (!TextUtils.isEmpty(file)) {
                    mCurrentSelectFile = new File(file);

                    mImageCropper.setOutPut(880, 640);
                    mImageCropper.setOutPutAspect(4, 3);
                    mImageCropper.cropImage(mCurrentSelectFile);
                } else {
                    Snackbar.make(findViewById(R.id.main), R.string.load_image_error, Snackbar.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onError() {
                Snackbar.make(findViewById(R.id.main), R.string.load_image_error, Snackbar.LENGTH_SHORT).show();
            }
        });


        mImageCropper = new ImageCropper(this);
        mImageCropper.setCallback(new ImageCropper.ImageCropperCallback() {
            @Override
            public void onCropperCallback(ImageCropper.CropperResult result, File srcFile, File outFile) {
                mCurrentSelectFile = null;
                if (result == ImageCropper.CropperResult.success) {
                    loadImage(outFile);
                } else if (result == ImageCropper.CropperResult.error_illegal_input_file) {
                    Snackbar.make(findViewById(R.id.main), R.string.load_image_error, Snackbar.LENGTH_SHORT).show();
                } else if (result == ImageCropper.CropperResult.error_illegal_out_file) {
                    Snackbar.make(findViewById(R.id.main), R.string.load_image_error, Snackbar.LENGTH_SHORT).show();
                }
            }
        });


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
                if(GetFitServerRequest.with(MyProfileActivity.this).isAuthorized()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!isFinishing()) {
                                new AlertDialog.Builder(MyProfileActivity.this)
                                        .setTitle(getString(R.string.title_str))
                                        .setMessage(getString(R.string.reg_alert_str))
                                        .setCancelable(false)
                                        .setPositiveButton(getString(R.string.cont_upcase_str), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(MyProfileActivity.this, AuthActivity.class);
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
                        try {
                            if(objects[0].equals("AuthError")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!isFinishing()) {
                                            new AlertDialog.Builder(MyProfileActivity.this)
                                                    .setTitle(getString(R.string.title_str))
                                                    .setMessage(getString(R.string.reg_alert_str))
                                                    .setCancelable(false)
                                                    .setPositiveButton(getString(R.string.cont_upcase_str), new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            Intent intent = new Intent(MyProfileActivity.this, AuthActivity.class);
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
                                Picasso.with(MyProfileActivity.this)
                                        .load(GetFitServerRequest.imagesUrl + object.getString("banner_url"))
                                        .into((ImageView) findViewById(R.id.imageView2));
                                findViewById(R.id.gradient).setVisibility(View.VISIBLE);
                            }

                            findViewById(R.id.imageView2).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    avatar = false;
                                    MyProfileActivity.this.registerForContextMenu(v);
                                    MyProfileActivity.this.openContextMenu(v);
                                    MyProfileActivity.this.unregisterForContextMenu(v);
                                }
                            });

                            findViewById(R.id.gradient).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    avatar = false;
                                    MyProfileActivity.this.registerForContextMenu(v);
                                    MyProfileActivity.this.openContextMenu(v);
                                    MyProfileActivity.this.unregisterForContextMenu(v);
                                }
                            });


                            findViewById(R.id.profile_image).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    avatar = true;
                                    MyProfileActivity.this.registerForContextMenu(v);
                                    MyProfileActivity.this.openContextMenu(v);
                                    MyProfileActivity.this.unregisterForContextMenu(v);
                                }
                            });

                            if(!object.isNull("avatar_url")) {
                                Picasso.with(MyProfileActivity.this)
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

    private void loadImage(final File file) {
        final ProgressDialog dialog = new ProgressDialog();
        dialog.show(getFragmentManager(), "atheletapp");
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                base64 = AndroidUtils.encodeToBase64(bitmap);
                JSONObject object = new JSONObject();
                JSONObject user = new JSONObject();
                try {
                    if(avatar) {
                        user.accumulate("avatar", "data:image/jpg;base64," + base64);
                    } else {
                        user.accumulate("banner", "data:image/jpg;base64," + base64);
                    }
                    object.put("user", user);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                GetFitServerRequest.with(getApplicationContext())
                        .authorize()
                        .listener(new OnRequestPerformedListener() {
                            @Override
                            public void onRequestPerformedListener(Object... objects) {
                                if (objects[0].equals("Internet")) {
                                    cancelRequest();
                                }
                            }
                        })
                        .objects(object)
                        .updateUser()
                        .perform();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if(!avatar) {
                            findViewById(R.id.gradient).setVisibility(View.VISIBLE);
                            ((ImageView) findViewById(R.id.imageView2)).setImageBitmap(bitmap);
                        } else {
                            ((ImageView) findViewById(R.id.profile_image)).setImageBitmap(bitmap);
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.photo_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.gallery:
                mImageFileSelector.takePhoto(this);
                return true;
            case R.id.camera:
                mImageFileSelector.selectImage(this);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mImageFileSelector.onSaveInstanceState(outState);
        mImageCropper.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mImageFileSelector.onRestoreInstanceState(savedInstanceState);
        mImageCropper.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mImageFileSelector.onActivityResult(requestCode, resultCode, data);
        mImageCropper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        mImageFileSelector.onRequestPermissionsResult(requestCode, permissions, grantResults);
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