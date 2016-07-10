package binaryblitz.athleteapp.Activities;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import binaryblitz.athleteapp.Abstract.BaseActivity;
import binaryblitz.athleteapp.Custom.ProgressDialog;
import binaryblitz.athleteapp.Custom.RaitingDialog;
import binaryblitz.athleteapp.Data.MyProgramsSet;
import binaryblitz.athleteapp.Data.Post;
import binaryblitz.athleteapp.Data.Professional;
import binaryblitz.athleteapp.Data.ProfessionalType;
import binaryblitz.athleteapp.Data.Program;
import binaryblitz.athleteapp.Data.SubscriptionsSet;
import binaryblitz.athleteapp.Fragments.NewsFragment;
import binaryblitz.athleteapp.Fragments.ProgramsFragment;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.GetFitServerRequest;
import binaryblitz.athleteapp.Server.OnRequestPerformedListener;

public class ProfProfileActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ViewPager mPager;
    private SwipeRefreshLayout layout;
    private ProgramsFragment first;
    private NewsFragment second;

    private View vBgLike;
    private ImageView ivLike;

    private String followId = "";
    private String id;
    private boolean following = false;
    private int userCount;
    private double rating;

    private String subscriptionId = null;

    IInAppBillingService mService;

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mServiceConn);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prof_profile_layout);

        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        first = new ProgramsFragment();
        second = new NewsFragment();

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

        layout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        layout.setOnRefreshListener(this);
        layout.setColorSchemeResources(R.color.accent_color);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                layout.setRefreshing(true);
                load();
            }
        });

        //region Rating

        vBgLike = findViewById(R.id.follow_btn);
        ivLike = (ImageView) findViewById(R.id.imageView6);

        vBgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GetFitServerRequest.with(ProfProfileActivity.this).isAuthorized()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!isFinishing()) {
                                new AlertDialog.Builder(ProfProfileActivity.this)
                                        .setTitle(getString(R.string.title_str))
                                        .setMessage(getString(R.string.reg_alert_str))
                                        .setCancelable(false)
                                        .setPositiveButton(getString(R.string.cont_upcase_str), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(ProfProfileActivity.this, AuthActivity.class);
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
                final ProgressDialog dialog = new ProgressDialog();
                dialog.show(getFragmentManager(), "atheleteapp");

                if (!following) {
                    GetFitServerRequest.with(ProfProfileActivity.this)
                            .authorize()
                            .listener(new OnRequestPerformedListener() {
                                @Override
                                public void onRequestPerformedListener(Object... objects) {
                                    dialog.dismiss();
                                    if (objects[0].equals("Internet")) {
                                        cancelRequest();
                                        return;
                                    }
                                    try {
                                        followId = ((JSONObject) objects[0]).getString("id");
                                        following = true;
                                    } catch (Exception ignored) {
                                    }
                                    userCount++;
                                    ((TextView) findViewById(R.id.textView4)).setText(Integer.toString(userCount));
                                    vBgLike.setBackgroundResource(R.drawable.blue_btn);
                                    ivLike.setImageResource(R.drawable.done_ic);
                                    ((TextView) findViewById(R.id.textView6)).setTextColor(Color.WHITE);
                                }
                            })
                            .follow(id)
                            .perform();
                } else {
                    GetFitServerRequest.with(ProfProfileActivity.this)
                            .authorize()
                            .listener(new OnRequestPerformedListener() {
                                @Override
                                public void onRequestPerformedListener(Object... objects) {
                                    dialog.dismiss();
                                    if (objects[0].equals("Internet")) {
                                        cancelRequest();
                                        return;
                                    }
                                    userCount--;
                                    followId = null;
                                    following = false;
                                    ((TextView) findViewById(R.id.textView4)).setText(Integer.toString(userCount));
                                    vBgLike.setBackgroundResource(R.drawable.blue_border);
                                    ivLike.setImageResource(R.drawable.plus_ic);
                                    ((TextView) findViewById(R.id.textView6)).setTextColor(Color.parseColor("#3695ed"));
                                }
                            })
                            .unfollow(followId)
                            .perform();
                }

                following = !following;
            }
        });

        findViewById(R.id.textView3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (GetFitServerRequest.with(ProfProfileActivity.this).isAuthorized()) {
                    ProfProfileActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!ProfProfileActivity.this.isFinishing()) {
                                new AlertDialog.Builder(ProfProfileActivity.this)
                                        .setTitle(ProfProfileActivity.this.getString(R.string.title_str))
                                        .setMessage(ProfProfileActivity.this.getString(R.string.reg_alert_str))
                                        .setCancelable(false)
                                        .setPositiveButton(ProfProfileActivity.this.getString(R.string.cont_upcase_str), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(ProfProfileActivity.this, AuthActivity.class);
                                                ProfProfileActivity.this.startActivity(intent);
                                            }
                                        })
                                        .setNegativeButton(ProfProfileActivity.this.getString(R.string.cancel_upcase_str), new DialogInterface.OnClickListener() {
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

                try {
                    if (SubscriptionsSet.load().get(id) == null) {
                        new AlertDialog.Builder(ProfProfileActivity.this)
                                .setTitle(ProfProfileActivity.this.getString(R.string.title_str))
                                .setMessage(getString(R.string.rating_error_str))
                                .setCancelable(false)
                                .setPositiveButton(R.string.ok_str, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .show();

                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                RaitingDialog dialog = new RaitingDialog();
                dialog.setListener(new RaitingDialog.OnRateDialogFinished() {
                    @Override
                    public void OnRateDialogFinished(float rating) {

                        JSONObject object = new JSONObject();

                        try {
                            object.accumulate("value", (int) rating);
                            object.accumulate("content", "Gut");
                        } catch (Exception e) {
                        }

                        JSONObject toSend = new JSONObject();

                        try {
                            toSend.accumulate("rating", object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (object.isNull("rating_id")) {
                            try {
                                GetFitServerRequest.with(ProfProfileActivity.this)
                                        .authorize()
                                        .objects(toSend)
                                        .listener(new OnRequestPerformedListener() {
                                            @Override
                                            public void onRequestPerformedListener(Object... objects) {
                                                if (objects[0].equals("Internet")) {
                                                    cancelRequest();
                                                    return;
                                                }
                                                try {
                                                    ((TextView) findViewById(R.id.textView3)).setText(new DecimalFormat("#.#", DecimalFormatSymbols.getInstance(Locale.US)).format(((JSONObject) objects[0]).getDouble("rating")));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        })
                                        .rateTrainer(object.getString("id"))
                                        .perform();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                GetFitServerRequest.with(ProfProfileActivity.this)
                                        .authorize()
                                        .objects(toSend)
                                        .listener(new OnRequestPerformedListener() {
                                            @Override
                                            public void onRequestPerformedListener(Object... objects) {
                                                if (objects[0].equals("Internet")) {
                                                    cancelRequest();
                                                    return;
                                                }
                                                try {
                                                    ((TextView) findViewById(R.id.textView3)).setText(new DecimalFormat("#.#", DecimalFormatSymbols.getInstance(Locale.US)).format(((JSONObject) objects[0]).getDouble("rating")));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        })
                                        .patchRating(object.getString("rating"))
                                        .perform();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

                dialog.show(getFragmentManager(), "rating");
            }
        });

        findViewById(R.id.imageView3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (GetFitServerRequest.with(ProfProfileActivity.this).isAuthorized()) {
                    ProfProfileActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!ProfProfileActivity.this.isFinishing()) {
                                new AlertDialog.Builder(ProfProfileActivity.this)
                                        .setTitle(ProfProfileActivity.this.getString(R.string.title_str))
                                        .setMessage(ProfProfileActivity.this.getString(R.string.reg_alert_str))
                                        .setCancelable(false)
                                        .setPositiveButton(ProfProfileActivity.this.getString(R.string.cont_upcase_str), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(ProfProfileActivity.this, AuthActivity.class);
                                                ProfProfileActivity.this.startActivity(intent);
                                            }
                                        })
                                        .setNegativeButton(ProfProfileActivity.this.getString(R.string.cancel_upcase_str), new DialogInterface.OnClickListener() {
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

                try {
                    if (SubscriptionsSet.load().get(id) == null) {
                        new AlertDialog.Builder(ProfProfileActivity.this)
                                .setTitle(ProfProfileActivity.this.getString(R.string.title_str))
                                .setMessage(getString(R.string.rating_error_str))
                                .setCancelable(false)
                                .setPositiveButton(R.string.ok_str, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .show();

                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                RaitingDialog dialog = new RaitingDialog();
                dialog.setListener(new RaitingDialog.OnRateDialogFinished() {
                    @Override
                    public void OnRateDialogFinished(float rating) {

                        JSONObject object = new JSONObject();

                        try {
                            object.accumulate("value", (int) rating);
                            object.accumulate("content", "Gut");
                        } catch (Exception e) {
                        }

                        JSONObject toSend = new JSONObject();

                        try {
                            toSend.accumulate("rating", object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (object.isNull("rating_id")) {
                            try {
                                GetFitServerRequest.with(ProfProfileActivity.this)
                                        .authorize()
                                        .objects(toSend)
                                        .listener(new OnRequestPerformedListener() {
                                            @Override
                                            public void onRequestPerformedListener(Object... objects) {
                                                if (objects[0].equals("Internet")) {
                                                    cancelRequest();
                                                    return;
                                                }
                                                try {
                                                    ((TextView) findViewById(R.id.textView3)).setText(new DecimalFormat("#.#", DecimalFormatSymbols.getInstance(Locale.US)).format(((JSONObject) objects[0]).getDouble("rating")));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        })
                                        .rateTrainer(object.getString("id"))
                                        .perform();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                GetFitServerRequest.with(ProfProfileActivity.this)
                                        .authorize()
                                        .objects(toSend)
                                        .listener(new OnRequestPerformedListener() {
                                            @Override
                                            public void onRequestPerformedListener(Object... objects) {
                                                if (objects[0].equals("Internet")) {
                                                    cancelRequest();
                                                    return;
                                                }
                                                try {
                                                    ((TextView) findViewById(R.id.textView3)).setText(new DecimalFormat("#.#", DecimalFormatSymbols.getInstance(Locale.US)).format(((JSONObject) objects[0]).getDouble("rating")));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        })
                                        .patchRating(object.getString("rating"))
                                        .perform();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

                dialog.show(getFragmentManager(), "rating");
            }
        });
        //endregion
    }

    public void load() {
        //region Main Information
        GetFitServerRequest.with(this)
                .authorize()
                .listener(new OnRequestPerformedListener() {
                    @Override
                    public void onRequestPerformedListener(Object... objects) {
                        layout.setRefreshing(false);

                        try {
                            JSONObject object = (JSONObject) objects[0];

                            if (!object.isNull("banner_url")) {
                                Picasso.with(ProfProfileActivity.this)
                                        .load(GetFitServerRequest.imagesUrl + object.getString("banner_url"))
                                        .into((ImageView) findViewById(R.id.imageView2));
                                findViewById(R.id.gradient).setVisibility(View.VISIBLE);
                            }

                            Picasso.with(ProfProfileActivity.this)
                                    .load(GetFitServerRequest.imagesUrl + object.getString("avatar_url"))
                                    .into((ImageView) findViewById(R.id.profile_image));

                            ((TextView) findViewById(R.id.textView)).setText(
                                    object.getString("first_name") + " " + object.getString("last_name")
                            );

                            ((TextView) findViewById(R.id.textView2)).setText(
                                    object.getString("description")
                            );

                            userCount = object.getInt("followers_count");
                            ((TextView) findViewById(R.id.textView3)).setText(
                                    Integer.toString(object.getInt("followers_count"))
                            );

                            rating = object.isNull("rating") ? 0 : object.getDouble("rating");
                            ((TextView) findViewById(R.id.textView4)).setText(
                                    Double.toString(object.isNull("rating") ? 0 : object.getDouble("rating"))
                            );

                            switch (object.getString("category")) {
                                case "trainer":
                                    ((TextView) findViewById(R.id.textView5)).setText("TRAINER");
                                    break;
                                case "nutritionist":
                                    ((TextView) findViewById(R.id.textView5)).setText("NUTRITIONIST");
                                    break;
                                case "physician":
                                    ((TextView) findViewById(R.id.textView5)).setText("DOCTOR");
                                    break;
                                default:
                                    ((TextView) findViewById(R.id.textView5)).setText("TRAINER");
                                    break;
                            }

                            try {
                                followId = object.getJSONObject("following_id").getString("id");
                            } catch (Exception e) {
                            }

                            following = !object.isNull("following_id");

                            if (following) {
                                vBgLike.setBackgroundResource(R.drawable.blue_btn);
                                ivLike.setImageResource(R.drawable.done_ic);
                                ((TextView) findViewById(R.id.textView6)).setTextColor(Color.WHITE);
                            } else {
                                vBgLike.setBackgroundResource(R.drawable.blue_border);
                                ivLike.setImageResource(R.drawable.plus_ic);
                                ((TextView) findViewById(R.id.textView6)).setTextColor(Color.parseColor("#3695ed"));
                            }
                            id = object.getString("id");

                            try {
                                subscriptionId = SubscriptionsSet.load().get(id).getId();
                            } catch (Exception e) {
                                subscriptionId = null;
                            }

                            findViewById(R.id.imageView5).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (GetFitServerRequest.with(ProfProfileActivity.this).isAuthorized()) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (!isFinishing()) {
                                                    new AlertDialog.Builder(ProfProfileActivity.this)
                                                            .setTitle(getString(R.string.title_str))
                                                            .setMessage(getString(R.string.reg_alert_str))
                                                            .setCancelable(false)
                                                            .setPositiveButton(getString(R.string.cont_upcase_str), new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    Intent intent = new Intent(ProfProfileActivity.this, AuthActivity.class);
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
                                    if (subscriptionId != null) {
                                        Intent intent = new Intent(ProfProfileActivity.this, ChatActivity.class);
                                        intent.putExtra("id", subscriptionId);
                                        startActivity(intent);
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (!isFinishing()) {
                                                    new AlertDialog.Builder(ProfProfileActivity.this)
                                                            .setTitle(getString(R.string.getfit_str))
                                                            .setMessage(getString(R.string.messages_error_str))
                                                            .setCancelable(false)
                                                            .setPositiveButton(R.string.ok_str, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                }
                                                            }).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            });

                            findViewById(R.id.textView10).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (GetFitServerRequest.with(ProfProfileActivity.this).isAuthorized()) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (!isFinishing()) {
                                                    new AlertDialog.Builder(ProfProfileActivity.this)
                                                            .setTitle(getString(R.string.title_str))
                                                            .setMessage(getString(R.string.reg_alert_str))
                                                            .setCancelable(false)
                                                            .setPositiveButton(getString(R.string.cont_upcase_str), new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    Intent intent = new Intent(ProfProfileActivity.this, AuthActivity.class);
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

                                    Bundle buyIntentBundle = null;
                                    try {
                                        buyIntentBundle = mService.getBuyIntent(3, getPackageName(),
                                                "android.test.purchased", "inapp", "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }

                                    PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                                    try {
                                        startIntentSenderForResult(pendingIntent.getIntentSender(),
                                                1001, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
                                                Integer.valueOf(0));
                                    } catch (IntentSender.SendIntentException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } catch (Exception e) {
                        }
                    }
                })
                .trainer(getIntent().getStringExtra("id"))
                .perform();
        //endregion

        //region Fragments
        GetFitServerRequest.with(this)
                .authorize()
                .listener(new OnRequestPerformedListener() {
                    @Override
                    public void onRequestPerformedListener(Object... objects) {
                        ArrayList<Program> collection = new ArrayList<>();
                        if (objects[0].equals("Internet")) {
                            cancelRequest();
                            return;
                        }
                        try {
                            JSONArray array = (JSONArray) objects[0];

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                Program program = new Program(
                                        object.getString("id"),
                                        object.getString("name"),
                                        object.getString("price"),
                                        object.getJSONObject("program_type").getString("name"),
                                        object.getInt("workouts_count"),
                                        object.getString("preview"),
                                        object.getString("duration"),
                                        object.getInt("users_count"),
                                        object.isNull("raring") ? 0 : object.getInt("rating"),
                                        object.getJSONObject("trainer").getString("first_name") + " " +
                                                object.getJSONObject("trainer").getString("last_name"),
                                        object.getJSONObject("trainer").getString("id"),
                                        GetFitServerRequest.baseUrl + object.getString("banner_url"),
                                        object.isNull("rating_id") ? null : object.getString("rating")
                                );
                                program.setUserPhotoUrl(GetFitServerRequest.baseUrl +
                                        object.getJSONObject("trainer").getString("avatar_url"));
                                collection.add(program);
                            }

                            first.setCollection(collection);
                        } catch (Exception e) {
                        }
                    }
                })
                .trainerPrograms(getIntent().getStringExtra("id"))
                .perform();

        GetFitServerRequest.with(this)
                .authorize()
                .listener(new OnRequestPerformedListener() {
                    @Override
                    public void onRequestPerformedListener(Object... objects) {
                        ArrayList<Post> news = new ArrayList<>();
                        if (objects[0].equals("Internet")) {
                            cancelRequest();
                            return;
                        }
                        try {
                            JSONArray array = (JSONArray) objects[0];

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                Calendar start = Calendar.getInstance();

                                try {
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                                    format.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    Date date = format.parse(object.getString("updated_at"));
                                    start.setTime(date);
                                } catch (Exception ignored) {
                                }

                                news.add(new Post(
                                        object.getString("id"),
                                        object.getJSONObject("trainer").getString("first_name")
                                                + " " + object.getJSONObject("trainer").getString("last_name"),
                                        object.getJSONObject("trainer").getString("id"),
                                        GetFitServerRequest.imagesUrl + object.getJSONObject("trainer").getString("avatar_url"),
                                        object.getString("content"),
                                        GetFitServerRequest.imagesUrl + object.getString("image_url"),
                                        start,
                                        object.getInt("likes_count"),
                                        object.getInt("comments_count"),
                                        object.isNull("like_id") ? "" : object.getString("like_id"),
                                        !object.isNull("like_id")));
                            }

                            Collections.sort(news, new Comparator<Post>() {
                                @Override
                                public int compare(Post lhs, Post rhs) {
                                    if (lhs.getDate().before(rhs.getDate())) {
                                        return 1;
                                    } else {
                                        return -1;
                                    }
                                }
                            });

                            second.setCollection(news);
                        } catch (Exception e) {
                        }
                    }
                })
                .trainerPosts(getIntent().getStringExtra("id"))
                .perform();
        //endregion
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (resultCode == RESULT_OK) {

                final ProgressDialog dialog = new ProgressDialog();
                dialog.show(getFragmentManager(), "atheleteapp");

                GetFitServerRequest.with(ProfProfileActivity.this)
                        .authorize()
                        .listener(new OnRequestPerformedListener() {
                            @Override
                            public void onRequestPerformedListener(Object... objects) {
                                dialog.dismiss();
                                if (objects[0].equals("Internet")) {
                                    cancelRequest();
                                    return;
                                }
                                try {
                                    subscriptionId = ((JSONObject) objects[0]).getString("id");

                                    SubscriptionsSet.load().add(new SubscriptionsSet.Subscription(
                                            subscriptionId,
                                            id
                                    ));

                                    Intent intent = new Intent(ProfProfileActivity.this, QuestionsActivity.class);
                                    startActivity(intent);
                                } catch (Exception e) {

                                }
                            }
                        })
                        .subscript(id)
                        .perform();
            }
        }
    }

    @Override
    public void onRefresh() {
        load();
    }

    private class NavigationAdapter extends FragmentPagerAdapter {

        String[] TITLES = {getString(R.string.programs_upcase_str),
                getString(R.string.news_upcase_str)};

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
