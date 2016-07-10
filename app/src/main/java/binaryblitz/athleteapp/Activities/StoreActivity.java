package binaryblitz.athleteapp.Activities;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.vending.billing.IInAppBillingService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import binaryblitz.athleteapp.Abstract.BaseActivity;
import binaryblitz.athleteapp.Adapters.StoreAdapter;
import binaryblitz.athleteapp.Custom.ProgressDialog;
import binaryblitz.athleteapp.Data.MyProgramsSet;
import binaryblitz.athleteapp.Data.Program;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.GetFitServerRequest;
import binaryblitz.athleteapp.Server.OnRequestPerformedListener;
import binaryblitz.athleteapp.Utils.AndroidUtils;
import binaryblitz.athleteapp.Utils.ShowHideScrollListener;

public class StoreActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    View fab;
    StoreAdapter adapter;
    private SwipeRefreshLayout layout;

    public static boolean filter = false;
    public static String specFilter = "0";
    public static String orderFilter = "followers_count";
    public static String priceFilter = "0";

    private String id = "";

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

    public void buyProgram(String id) {
        this.id = id;

        new MaterialDialog.Builder(StoreActivity.this)
                .title(R.string.purchase_dialog_str)
                .content(R.string.are_you_sure_str)
                .positiveText(R.string.yes_str)
                .negativeText(R.string.no_str)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        if(GetFitServerRequest.with(StoreActivity.this).isAuthorized()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isFinishing()) {
                                        StoreActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (!StoreActivity.this.isFinishing()) {
                                                    new AlertDialog.Builder(StoreActivity.this)
                                                            .setTitle(StoreActivity.this.getString(R.string.title_str))
                                                            .setMessage(StoreActivity.this.getString(R.string.reg_alert_str))
                                                            .setCancelable(false)
                                                            .setPositiveButton(StoreActivity.this.getString(R.string.cont_upcase_str), new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    Intent intent = new Intent(StoreActivity.this, AuthActivity.class);
                                                                    StoreActivity.this.startActivity(intent);
                                                                }
                                                            })
                                                            .setNegativeButton(StoreActivity.this.getString(R.string.cancel_upcase_str), new DialogInterface.OnClickListener() {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mServiceConn);
        }
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

                GetFitServerRequest.with(StoreActivity.this)
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_layout);
        init();

        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        RecyclerView view = (RecyclerView) findViewById(R.id.recyclerView);
        view.setItemAnimator(new DefaultItemAnimator());
        view.setLayoutManager(new LinearLayoutManager(this));

        adapter = new StoreAdapter(this);
        view.setAdapter(adapter);

        fab = findViewById(R.id.fab12);

        findViewById(R.id.fab12).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StoreActivity.this, FilterActivity.class);
                startActivity(intent);
            }
        });

        view.addOnScrollListener(new ShowHideScrollListener() {
            @Override
            public void onHide() {
                AndroidUtils.hideFab(fab);
            }

            @Override
            public void onShow() {
                AndroidUtils.showFab(fab);
            }
        });

        layout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        layout.setOnRefreshListener(this);
        layout.setColorSchemeResources(R.color.accent_color);

        layout.setRefreshing(true);
        load();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(filter) {
            filter = false;
            filter();
        }
    }

    protected void filter() {
        GetFitServerRequest.with(this)
                .authorize()
                .listener(new OnRequestPerformedListener() {
                              @Override
                              public void onRequestPerformedListener(Object... objects) {
                                  layout.setRefreshing(false);
                                  if (objects[0].equals("Error")) {
                                      cancelRequest();
                                      return;
                                  }

                                  ArrayList<Program> collection = new ArrayList<>();

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
                                                  null,
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

                                      adapter.setCollection(collection);
                                      adapter.notifyDataSetChanged();
                                  } catch (Exception e) {
                                  }
                              }
                          }

                )
                .filterStore(orderFilter, specFilter, priceFilter)
                .perform();
    }

    private void load() {
        GetFitServerRequest.with(this)
                .authorize()
                .listener(new OnRequestPerformedListener() {
                              @Override
                              public void onRequestPerformedListener(Object... objects) {
                                  layout.setRefreshing(false);
                                  if (objects[0].equals("Error")) {
                                      cancelRequest();
                                      return;
                                  }

                                  ArrayList<Program> collection = new ArrayList<>();

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
                                                  null,
                                                  object.getInt("users_count"),
                                                  object.isNull("rating") ? 0 : Double.parseDouble(object.getString("rating")),
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

                                      adapter.setCollection(collection);
                                      adapter.notifyDataSetChanged();
                                  } catch (Exception e) {
                                  }
                              }
                          }

                )
                .programs()
                .perform();
    }

    @Override
    public void onRefresh() {
        load();
    }
}