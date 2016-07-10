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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.vending.billing.IInAppBillingService;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import binaryblitz.athleteapp.Abstract.BaseActivity;
import binaryblitz.athleteapp.Custom.ProgressDialog;
import binaryblitz.athleteapp.Custom.RaitingDialog;
import binaryblitz.athleteapp.Data.MyProgramsSet;
import binaryblitz.athleteapp.Data.Program;
import binaryblitz.athleteapp.Data.SubscriptionsSet;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.GetFitServerRequest;
import binaryblitz.athleteapp.Server.OnRequestPerformedListener;

public class ProgramActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private String id;
    private boolean purchase = false;

    private SwipeRefreshLayout layout;

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
        setContentView(R.layout.program_layout);

        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        findViewById(R.id.drawer_indicator).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        layout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        layout.setOnRefreshListener(this);
        layout.setColorSchemeResources(R.color.accent_color);

        layout.setRefreshing(true);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                layout.setRefreshing(true);
                load();
            }
        });

    }

    @Override
    public void onRefresh() {
        load();
    }

    private void load() {
        GetFitServerRequest.with(this)
                .authorize()
                .listener(new OnRequestPerformedListener() {
                    @Override
                    public void onRequestPerformedListener(Object... objects) {
                        layout.setRefreshing(false);
                        if (objects[0].equals("Internet")) {
                            cancelRequest();
                            return;
                        }
                        try {
                            final JSONObject object = (JSONObject) objects[0];
                            id = object.getString("id");

                            purchase = MyProgramsSet.load().find(id);

                            ((TextView) findViewById(R.id.textView2)).setText(object.getString("description"));

                            ((TextView) findViewById(R.id.textView3)).setText(
                                    object.isNull("rating") ? "0" : object.getString("rating"));
                            ((TextView) findViewById(R.id.textView4)).setText(object.getString("users_count"));
                            ((TextView) findViewById(R.id.textView6)).setText(object.getString("price") + "$");
                            ((TextView) findViewById(R.id.textView)).setText(object.getString("name"));
                            ((TextView) findViewById(R.id.textView3)).setText(object.isNull("rating") ? "0" :
                                    new DecimalFormat("#.#", DecimalFormatSymbols.getInstance(Locale.US)).format(object.getDouble("rating")));
                            ((TextView) findViewById(R.id.textView77)).setText(object.getJSONObject("program_type").getString("name"));
                            ((TextView) findViewById(R.id.textView8)).setText((object.getJSONArray("workouts")).length() + " workouts");

                            ((TextView) findViewById(R.id.textViewfdfd)).setText(
                                    object.getJSONObject("trainer").getString("first_name") + " " +
                                            object.getJSONObject("trainer").getString("last_name"));

                            findViewById(R.id.textView3).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if(GetFitServerRequest.with(ProgramActivity.this).isAuthorized()) {
                                        ProgramActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (!ProgramActivity.this.isFinishing()) {
                                                    new AlertDialog.Builder(ProgramActivity.this)
                                                            .setTitle(ProgramActivity.this.getString(R.string.title_str))
                                                            .setMessage(ProgramActivity.this.getString(R.string.reg_alert_str))
                                                            .setCancelable(false)
                                                            .setPositiveButton(ProgramActivity.this.getString(R.string.cont_upcase_str), new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    Intent intent = new Intent(ProgramActivity.this, AuthActivity.class);
                                                                    ProgramActivity.this.startActivity(intent);
                                                                }
                                                            })
                                                            .setNegativeButton(ProgramActivity.this.getString(R.string.cancel_upcase_str), new DialogInterface.OnClickListener() {
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
                                        if(!MyProgramsSet.load().find(object.getString("id"))) {
                                            new AlertDialog.Builder(ProgramActivity.this)
                                                    .setTitle(ProgramActivity.this.getString(R.string.title_str))
                                                    .setMessage(getString(R.string.program_rate_error_str))
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

                                            if(object.isNull("rating_id")) {
                                                try {
                                                    GetFitServerRequest.with(ProgramActivity.this)
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
                                                            .rateProgram(object.getString("id"))
                                                            .perform();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                try {
                                                    GetFitServerRequest.with(ProgramActivity.this)
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

                                    if(GetFitServerRequest.with(ProgramActivity.this).isAuthorized()) {
                                        ProgramActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (!ProgramActivity.this.isFinishing()) {
                                                    new AlertDialog.Builder(ProgramActivity.this)
                                                            .setTitle(ProgramActivity.this.getString(R.string.title_str))
                                                            .setMessage(ProgramActivity.this.getString(R.string.reg_alert_str))
                                                            .setCancelable(false)
                                                            .setPositiveButton(ProgramActivity.this.getString(R.string.cont_upcase_str), new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    Intent intent = new Intent(ProgramActivity.this, AuthActivity.class);
                                                                    ProgramActivity.this.startActivity(intent);
                                                                }
                                                            })
                                                            .setNegativeButton(ProgramActivity.this.getString(R.string.cancel_upcase_str), new DialogInterface.OnClickListener() {
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
                                        if(!MyProgramsSet.load().find(object.getString("id"))) {
                                            new AlertDialog.Builder(ProgramActivity.this)
                                                    .setTitle(ProgramActivity.this.getString(R.string.title_str))
                                                    .setMessage(getString(R.string.program_rate_error_str))
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

                                            if(object.isNull("rating_id")) {
                                                try {
                                                    GetFitServerRequest.with(ProgramActivity.this)
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
                                                            .rateProgram(object.getString("id"))
                                                            .perform();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                try {
                                                    GetFitServerRequest.with(ProgramActivity.this)
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

                            Picasso.with(ProgramActivity.this)
                                    .load(GetFitServerRequest.imagesUrl + object.getString("banner_url"))
                                    .into((ImageView) findViewById(R.id.imageView2));

                            Picasso.with(ProgramActivity.this)
                                    .load(GetFitServerRequest.imagesUrl + object.getJSONObject("trainer").getString("avatar_url"))
                                    .into((ImageView) findViewById(R.id.imageView));

                            findViewById(R.id.trainer).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(ProgramActivity.this, ProfProfileActivity.class);
                                    try {
                                        intent.putExtra("id", object.getJSONObject("trainer").getString("id"));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    startActivity(intent);
                                }
                            });

                            JSONArray array = object.getJSONArray("workouts");
                            ((LinearLayout) findViewById(R.id.container)).removeAllViews();
                            for(int i = 0; i < array.length(); i++) {
                                View v = LayoutInflater.
                                        from(ProgramActivity.this)
                                        .inflate(R.layout.workout_card,
                                                null);

                                ((TextView) v.findViewById(R.id.textView42)).setText(array.getJSONObject(i).getString("duration") + " MIN");
                                ((TextView) v.findViewById(R.id.textView22)).setText(array.getJSONObject(i).getString("name"));
                                ((LinearLayout) findViewById(R.id.container)).addView(v);

                                JSONArray array1 = array.getJSONObject(i).getJSONArray("exercises");

                                for(int j = 0; j < array1.length(); j++) {

                                    if(!purchase) {
                                        if (j == 2) {
                                            View v11 = LayoutInflater.
                                                    from(ProgramActivity.this)
                                                    .inflate(R.layout.workout_part_card,
                                                            null);

                                            ((TextView) v11.findViewById(R.id.textView22)).setText(
                                                    "+" + (array1.length() - 2) + " exercises"
                                            );

                                            ((TextView) v11.findViewById(R.id.textView22)).setTextColor(
                                                    Color.parseColor("#3695ed")
                                            );

                                            ((LinearLayout) findViewById(R.id.container)).addView(v11);
                                            break;
                                        }
                                    }

                                    View v1 = LayoutInflater.
                                            from(ProgramActivity.this)
                                            .inflate(R.layout.workout_part_card,
                                                    null);

                                    ((TextView) v1.findViewById(R.id.textView22)).setText(
                                            array1.getJSONObject(j).getJSONObject("exercise_type").getString("name"));

                                    ((LinearLayout) findViewById(R.id.container)).addView(v1);
                                }

                                if(purchase) {
                                    loadButton(array.getJSONObject(i).getString("id"));
                                }
                            }

                            if(!purchase) {
                                loadButton("");
                            }
                        } catch (Exception ignored) {
                        }
                    }
                })
                .program(getIntent().getStringExtra("id"))
                .perform();
    }

    private void loadButton(final String workout_id) {
        View v = LayoutInflater.
                from(ProgramActivity.this)
                .inflate(R.layout.unlock_btn,
                        null);

        if(purchase) {
            ((TextView) v.findViewById(R.id.textView16)).setText(R.string.add_to_calendar_str);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(GetFitServerRequest.with(ProgramActivity.this).isAuthorized()) {
                        ProgramActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!ProgramActivity.this.isFinishing()) {
                                    new AlertDialog.Builder(ProgramActivity.this)
                                            .setTitle(ProgramActivity.this.getString(R.string.title_str))
                                            .setMessage(ProgramActivity.this.getString(R.string.reg_alert_str))
                                            .setCancelable(false)
                                            .setPositiveButton(ProgramActivity.this.getString(R.string.cont_upcase_str), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent(ProgramActivity.this, AuthActivity.class);
                                                    ProgramActivity.this.startActivity(intent);
                                                }
                                            })
                                            .setNegativeButton(ProgramActivity.this.getString(R.string.cancel_upcase_str), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            })
                                            .show();
                                }
                            }
                        });
                    }
                    Intent intent = new Intent(ProgramActivity.this, NewTrainingActivity.class);
                    intent.putExtra("id", workout_id);
                    startActivity(intent);
                }
            });

        } else {
            ((TextView) v.findViewById(R.id.textView16)).setText(R.string.unlock_upcase_str);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MaterialDialog.Builder(ProgramActivity.this)
                            .title(R.string.purchase_dialog_str)
                            .content(R.string.are_you_sure_str)
                            .positiveText(R.string.yes_str)
                            .negativeText(R.string.no_str)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    if(GetFitServerRequest.with(ProgramActivity.this).isAuthorized()) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (!isFinishing()) {
                                                    ProgramActivity.this.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if (!ProgramActivity.this.isFinishing()) {
                                                                new AlertDialog.Builder(ProgramActivity.this)
                                                                        .setTitle(ProgramActivity.this.getString(R.string.title_str))
                                                                        .setMessage(ProgramActivity.this.getString(R.string.reg_alert_str))
                                                                        .setCancelable(false)
                                                                        .setPositiveButton(ProgramActivity.this.getString(R.string.cont_upcase_str), new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                Intent intent = new Intent(ProgramActivity.this, AuthActivity.class);
                                                                                ProgramActivity.this.startActivity(intent);
                                                                            }
                                                                        })
                                                                        .setNegativeButton(ProgramActivity.this.getString(R.string.cancel_upcase_str), new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialog, int which) {

                                                                            }
                                                                        })
                                                                        .show();
                                                            }
                                                        }
                                                    });
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
                            })
                            .show();
                }
            });

        }
        ((LinearLayout) findViewById(R.id.container)).addView(v);
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

                final ProgressDialog dialog1 = new ProgressDialog();
                dialog1.show(getFragmentManager(), "matesapp");

                GetFitServerRequest.with(ProgramActivity.this)
                        .authorize()
                        .listener(new OnRequestPerformedListener() {
                            @Override
                            public void onRequestPerformedListener(Object... objects) {
                                dialog1.dismiss();
                                if (objects[0].equals("Internet")) {
                                    cancelRequest();
                                    return;
                                }
                                MyProgramsSet.load().add(id);
                                load();
                            }
                        })
                        .purchaseProgram(id)
                        .perform();
            }
        }
    }
}