package binaryblitz.athleteapp.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.nineoldandroids.animation.Animator;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import binaryblitz.athleteapp.Abstract.BaseActivity;
import binaryblitz.athleteapp.Custom.ProgressDialog;
import binaryblitz.athleteapp.Data.User;
import binaryblitz.athleteapp.PhoneNumberUtils.Country;
import binaryblitz.athleteapp.PhoneNumberUtils.CountryAdapter;
import binaryblitz.athleteapp.PhoneNumberUtils.CustomPhoneNumberFormattingTextWatcher;
import binaryblitz.athleteapp.PhoneNumberUtils.OnPhoneChangedListener;
import binaryblitz.athleteapp.PhoneNumberUtils.PhoneUtils;
import binaryblitz.athleteapp.Push.QuickstartPreferences;
import binaryblitz.athleteapp.Push.RegistrationIntentService;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.DeviceInfoStore;
import binaryblitz.athleteapp.Server.GetFitServerRequest;
import binaryblitz.athleteapp.Server.OnRequestPerformedListener;
import binaryblitz.athleteapp.Utils.AndroidUtils;
import binaryblitz.athleteapp.Utils.CodeTimer;

public class PhoneActivity extends BaseActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    boolean code = false;
    static String token = "";
    static String phoneFromServer;

    static long milis;
    TextView tv;
    static String str = "";
    final Handler myHandler = new Handler();

    final Runnable myRunnable = new Runnable() {
        public void run() {
            str = getString(R.string.send_code_after_str) +
                    (int) ((double) (milis) / (double) 1000);

            if(milis < 2000) {
                str = "repeat";
            }
        }
    };

    private void UpdateGUI() {
        myHandler.post(myRunnable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_layout);

        DeviceInfoStore.resetToken(this);

        final ProgressDialog dialog1 = new ProgressDialog();

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtils.hideKeyboard(v);
                if (!code) {
                    if (((EditText) findViewById(R.id.phone)).getText().toString().isEmpty()) {
                        ((MaterialEditText) findViewById(R.id.phone)).setError(getString(R.string.empty_field_str));
                        return;
                    }

                    final ProgressDialog dialog = new ProgressDialog();
                    dialog.show(getFragmentManager(), "getfitapp");

                    GetFitServerRequest.with(PhoneActivity.this)
                            .skipAuth()
                            .listener(new OnRequestPerformedListener() {
                                @Override
                                public void onRequestPerformedListener(Object... objects) {
                                    dialog.dismiss();
                                    if (objects[0].equals("Internet")) {
                                        return;
                                    }
                                    if (objects[0].equals("Error")) {
                                        Snackbar.make(findViewById(R.id.main), R.string.error_try_str, Snackbar.LENGTH_SHORT).show();
                                        return;
                                    }
                                    code = true;
                                    tv.setVisibility(View.VISIBLE);

                                    try {
                                        token = ((JSONObject) objects[0]).getString("token");
                                        phoneFromServer = ((JSONObject) objects[0]).getString("phone_number");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    YoYo.with(Techniques.SlideOutLeft)
                                            .duration(700)
                                            .withListener(new Animator.AnimatorListener() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {
                                                    findViewById(R.id.l2).setVisibility(View.VISIBLE);

                                                    YoYo.with(Techniques.SlideInRight)
                                                            .duration(700)
                                                            .playOn(findViewById(R.id.l2));
                                                }

                                                @Override
                                                public void onAnimationEnd(Animator animation) {

                                                }

                                                @Override
                                                public void onAnimationCancel(Animator animation) {

                                                }

                                                @Override
                                                public void onAnimationRepeat(Animator animation) {

                                                }
                                            })
                                            .playOn(findViewById(R.id.l1));

                                    YoYo.with(Techniques.SlideOutLeft)
                                            .duration(700)
                                            .withListener(new Animator.AnimatorListener() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {
                                                    findViewById(R.id.textView23).setVisibility(View.VISIBLE);

                                                    YoYo.with(Techniques.SlideInRight)
                                                            .duration(700)
                                                            .playOn(findViewById(R.id.textView23));
                                                }

                                                @Override
                                                public void onAnimationEnd(Animator animation) {

                                                }

                                                @Override
                                                public void onAnimationCancel(Animator animation) {

                                                }

                                                @Override
                                                public void onAnimationRepeat(Animator animation) {

                                                }
                                            })
                                            .playOn(findViewById(R.id.textView2));

                                    CodeTimer.reset();
                                    CodeTimer.with(new CodeTimer.OnTimer() {
                                        @Override
                                        public void onTick(final long millisUntilFinished) {
                                            milis = millisUntilFinished;
                                            UpdateGUI();
                                        }

                                        @Override
                                        public void onFinish() {
                                        }
                                    });

                                    CodeTimer.start();
                                }
                            })
                            .auth(((EditText) findViewById(R.id.phone)).getText().toString())
                            .perform();
                } else {
                    if (((EditText) findViewById(R.id.phon2e)).getText().toString().isEmpty()) {
                        ((MaterialEditText) findViewById(R.id.phon2e)).setError(getString(R.string.empty_field_str));
                        return;
                    }

                    final ProgressDialog dialog = new ProgressDialog();
                    dialog.show(getFragmentManager(), "getfitapp");

                    GetFitServerRequest.with(PhoneActivity.this)
                            .skipAuth()
                            .listener(new OnRequestPerformedListener() {
                                @Override
                                public void onRequestPerformedListener(Object... objects) {
                                    dialog.dismiss();
                                    Log.e("qwerty", objects[0].toString());
                                    if (objects[0].equals("Internet")) {
                                        return;
                                    }
                                    if (objects[0].equals("Error")) {
                                        Snackbar.make(findViewById(R.id.main), R.string.error_try_str, Snackbar.LENGTH_SHORT).show();
                                        return;
                                    }

                                    JSONObject object = (JSONObject) objects[0];

                                    if (object.isNull("api_token")) {
                                        Intent intent = new Intent(PhoneActivity.this, CreateAccountActivity.class);
                                        intent.putExtra("phone", ((EditText) findViewById(R.id.phone)).getText().toString());
                                        intent.putExtra("token", token);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        try {
                                            DeviceInfoStore.saveToken(((JSONObject) objects[0]).getString("api_token"));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        if (checkPlayServices()) {
                                            Intent intent = new Intent(PhoneActivity.this, RegistrationIntentService.class);
                                            startService(intent);
                                            dialog1.show(getFragmentManager(), "getfitapp");
                                        } else {
                                            Intent intent2 = new Intent(PhoneActivity.this, SplashActivity.class);
                                            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent2);
                                            finish();
                                        }
                                    }
                                }
                            })
                            .verify(((EditText) findViewById(R.id.phone)).getText().toString(),
                                    ((EditText) findViewById(R.id.phon2e)).getText().toString(),
                                    token)
                            .perform();
                }
            }
        });

        tv = ((TextView) findViewById(R.id.textView37));

        tv.setVisibility(View.GONE);

        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (str.equals("repeat")) {
                                    SpannableString content = new SpannableString(getString(R.string.send_again_str));
                                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                                    tv.setText(content);
                                } else {
                                    tv.setText(str);
                                }

                                if (milis < 2000) {
                                    tv.setClickable(true);
                                } else {
                                    tv.setClickable(false);
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();
        ((TextView) findViewById(R.id.textView37)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AndroidUtils.isConnected(PhoneActivity.this)) {
                    return;
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                GetFitServerRequest.with(PhoneActivity.this)
                                        .skipAuth()
                                        .listener(new OnRequestPerformedListener() {
                                            @Override
                                            public void onRequestPerformedListener(Object... objects) {
                                                if (objects[0].equals("Internet")) {
                                                    return;
                                                }
                                                try {
                                                    token = ((JSONObject) objects[0]).getString("token");
                                                    phoneFromServer = ((JSONObject) objects[0]).getString("phone_number");
                                                } catch (Exception e) {
                                                    cancelRequest();
                                                    e.printStackTrace();
                                                }
                                            }
                                        })
                                        .auth(((EditText) findViewById(R.id.phone)).getText().toString())
                                        .perform();
                            }
                        }, 50);

                        CodeTimer.reset();
                        CodeTimer.with(new CodeTimer.OnTimer() {
                            @Override
                            public void onTick(final long millisUntilFinished) {
                                milis = millisUntilFinished;
                                UpdateGUI();
                            }

                            @Override
                            public void onFinish() {
                            }
                        });

                        CodeTimer.start();
                    }
                }, 50);
            }
        });

        initPhoneField();

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                dialog1.dismiss();
                Intent intent2 = new Intent(PhoneActivity.this, SplashActivity.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent2);
                finish();
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void cancelRequest() {
        Snackbar.make(findViewById(R.id.main), R.string.lost_connection_str, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if(code) {
            YoYo.with(Techniques.SlideOutRight)
                    .duration(700)
                    .withListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            findViewById(R.id.l1).setVisibility(View.VISIBLE);

                            YoYo.with(Techniques.SlideInLeft)
                                    .duration(700)
                                    .playOn(findViewById(R.id.l1));
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    })
                    .playOn(findViewById(R.id.l2));

            YoYo.with(Techniques.SlideOutRight)
                    .duration(700)
                    .withListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            findViewById(R.id.textView2).setVisibility(View.VISIBLE);

                            YoYo.with(Techniques.SlideInLeft)
                                    .duration(700)
                                    .playOn(findViewById(R.id.textView2));
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    })
                    .playOn(findViewById(R.id.textView23));
            ((EditText) findViewById(R.id.phon2e)).setText("");
            tv.setVisibility(View.GONE);
            code = false;
        } else {
            super.onBackPressed();
        }
    }

    //region Phone text field

    protected SparseArray<ArrayList<Country>> mCountriesMap = new SparseArray<ArrayList<Country>>();

    protected PhoneNumberUtil mPhoneNumberUtil = PhoneNumberUtil.getInstance();
    protected Spinner mSpinner;

    protected String mLastEnteredPhone;
    protected EditText mPhoneEdit;
    protected CountryAdapter mAdapter;

    public void initPhoneField() {
        mSpinner = (Spinner) findViewById(R.id.spinner);
        mSpinner.setOnItemSelectedListener(mOnItemSelectedListener);

        mAdapter = new CountryAdapter(this);

        mSpinner.setAdapter(mAdapter);

        mPhoneEdit = (EditText) findViewById(R.id.phone);
        mPhoneEdit.addTextChangedListener(new CustomPhoneNumberFormattingTextWatcher(mOnPhoneChangedListener));
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (dstart > 0 && !Character.isDigit(c)) {
                        return "";
                    }
                }
                return null;
            }
        };

        mPhoneEdit.setFilters(new InputFilter[]{filter});

        mPhoneEdit.setImeOptions(EditorInfo.IME_ACTION_SEND);
        mPhoneEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    AndroidUtils.hideKeyboard(mPhoneEdit);
                    mPhoneEdit.setError(null);
                    String phone = validate();
                    if (phone == null) {
                        mPhoneEdit.requestFocus();
                    }
                    return true;
                }
                return false;
            }
        });

        initCodes(this);
    }

    protected AdapterView.OnItemSelectedListener mOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Country c = (Country) mSpinner.getItemAtPosition(position);
            if (mLastEnteredPhone != null && mLastEnteredPhone.startsWith(c.getCountryCodeStr())) {
                return;
            }
            mPhoneEdit.getText().clear();
            mPhoneEdit.getText().insert(mPhoneEdit.getText().length() > 0 ? 1 : 0, String.valueOf(c.getCountryCode()));
            mPhoneEdit.setSelection(mPhoneEdit.length());
            mLastEnteredPhone = null;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    protected OnPhoneChangedListener mOnPhoneChangedListener = new OnPhoneChangedListener() {
        @Override
        public void onPhoneChanged(String phone) {
            try {
                mLastEnteredPhone = phone;
                Phonenumber.PhoneNumber p = mPhoneNumberUtil.parse(phone, null);
                ArrayList<Country> list = mCountriesMap.get(p.getCountryCode());
                Country country = null;
                if (list != null) {
                    if (p.getCountryCode() == 1) {
                        String num = String.valueOf(p.getNationalNumber());
                        if (num.length() >= 3) {
                            String code = num.substring(0, 3);
                        }
                    }
                    if (country == null) {
                        for (Country c : list) {
                            if (c.getPriority() == 0) {
                                country = c;
                                break;
                            }
                        }
                    }
                }
                if (country != null) {
                    final int position = country.getNum();
                    mSpinner.post(new Runnable() {
                        @Override
                        public void run() {
                            mSpinner.setSelection(position);
                        }
                    });
                }
            } catch (NumberParseException ignore) {
            }

        }
    };


    protected String validate() {
        String region = null;
        String phone = null;
        if (mLastEnteredPhone != null) {
            try {
                Phonenumber.PhoneNumber p = mPhoneNumberUtil.parse(mLastEnteredPhone, null);
                StringBuilder sb = new StringBuilder(16);
                sb.append('+').append(p.getCountryCode()).append(p.getNationalNumber());
                phone = sb.toString();
                region = mPhoneNumberUtil.getRegionCodeForNumber(p);
            } catch (NumberParseException ignore) {
            }
        }
        if (region != null) {
            return phone;
        } else {
            return null;
        }
    }

    protected void initCodes(Context context) {
        new AsyncPhoneInitTask(context).execute();
    }

    protected class AsyncPhoneInitTask extends AsyncTask<Void, Void, ArrayList<Country>> {

        private int mSpinnerPosition = -1;
        private Context mContext;

        public AsyncPhoneInitTask(Context context) {
            mContext = context;
        }

        @Override
        protected ArrayList<Country> doInBackground(Void... params) {
            ArrayList<Country> data = new ArrayList<Country>(233);
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(mContext.getApplicationContext().getAssets().open("countries.dat"), "UTF-8"));

                // do reading, usually loop until end of file reading
                String line;
                int i = 0;
                while ((line = reader.readLine()) != null) {
                    //process line
                    Country c = new Country(mContext, line, i);
                    data.add(c);
                    ArrayList<Country> list = mCountriesMap.get(c.getCountryCode());
                    if (list == null) {
                        list = new ArrayList<Country>();
                        mCountriesMap.put(c.getCountryCode(), list);
                    }
                    list.add(c);
                    i++;
                }
            } catch (IOException e) {
                //log the exception
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        //log the exception
                    }
                }
            }
//            if (!TextUtils.isEmpty(mPhoneEdit.getText())) {
//                return data;
//            }
            String countryRegion = PhoneUtils.getCountryRegionFromPhone(mContext);
            int code = mPhoneNumberUtil.getCountryCodeForRegion(countryRegion);
            ArrayList<Country> list = mCountriesMap.get(code);
            if (list != null) {
                for (Country c : list) {
                    if (c.getPriority() == 0) {
                        mSpinnerPosition = c.getNum();
                        break;
                    }
                }
            }
            return data;
        }

        @Override
        protected void onPostExecute(ArrayList<Country> data) {
            mAdapter.addAll(data);
            if (mSpinnerPosition > 0) {
                mSpinner.setSelection(mSpinnerPosition);
            }
        }
    }

    //endregion
}