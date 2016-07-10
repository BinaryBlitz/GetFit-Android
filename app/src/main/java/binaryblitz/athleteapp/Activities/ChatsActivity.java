package binaryblitz.athleteapp.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import binaryblitz.athleteapp.Abstract.BaseActivity;
import binaryblitz.athleteapp.Adapters.ChatsAdapter;
import binaryblitz.athleteapp.Data.Chat;
import binaryblitz.athleteapp.Data.SubscriptionsSet;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.GetFitServerRequest;
import binaryblitz.athleteapp.Server.OnRequestPerformedListener;

public class ChatsActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ChatsAdapter adapter;
    private SwipeRefreshLayout layout;

    ArrayList<Chat> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);
        init();

        RecyclerView view = (RecyclerView) findViewById(R.id.recyclerView);
        view.setItemAnimator(new DefaultItemAnimator());
        view.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ChatsAdapter(this);
        view.setAdapter(adapter);

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
    }

    private void load() {
        GetFitServerRequest.with(this)
                .authorize()
                .listener(new OnRequestPerformedListener() {
                    @Override
                    public void onRequestPerformedListener(Object... objects) {
                        try {
                            JSONArray array = (JSONArray) objects[0];

                            JSONObject object;
                            if (objects[0].equals("Internet")) {
                                cancelRequest();
                                return;
                            }
                            object = array.getJSONObject(0);

                            Calendar start = Calendar.getInstance();
                            try {
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                                format.setTimeZone(TimeZone.getTimeZone("UTC"));
                                Date date = format.parse(object.getJSONObject("message_preview").getString("created_at"));
                                start.setTime(date);
                            } catch (Exception ignored) {
                            }


                            list.add(new Chat(
                                    0,
                                    getString(R.string.notifications_str),
                                    start,
                                    object.getString("content"),
                                    "",
                                    "notif"
                            ));

                            adapter.setCollection(list);
                            adapter.notifyDataSetChanged();

                            GetFitServerRequest.with(ChatsActivity.this)
                                    .authorize()
                                    .listener(new OnRequestPerformedListener() {
                                        @Override
                                        public void onRequestPerformedListener(Object... objects) {
                                            layout.setRefreshing(false);
                                            try {
                                                if (objects[0].equals("Internet")) {
                                                    cancelRequest();
                                                    return;
                                                }
                                                if (objects[0].equals("AuthError")) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if (!isFinishing()) {
                                                                new AlertDialog.Builder(ChatsActivity.this)
                                                                        .setTitle(getString(R.string.title_str))
                                                                        .setMessage(getString(R.string.reg_alert_str))
                                                                        .setCancelable(false)
                                                                        .setPositiveButton(getString(R.string.cont_upcase_str), new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                Intent intent = new Intent(ChatsActivity.this, AuthActivity.class);
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

                                                SubscriptionsSet
                                                        .load()
                                                        .clear();
                                                JSONArray array = (JSONArray) objects[0];

                                                for (int i = 0; i < array.length(); i++) {
                                                    JSONObject object = array.getJSONObject(i);

                                                    SubscriptionsSet
                                                            .load()
                                                            .add(new SubscriptionsSet.Subscription(
                                                                    object.getString("id"),
                                                                    object.getString("trainer_id")
                                                            ));

                                                    Calendar start = Calendar.getInstance();
                                                    try {
                                                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                                                        format.setTimeZone(TimeZone.getTimeZone("UTC"));
                                                        Date date = format.parse(object.getJSONObject("message_preview").getString("created_at"));
                                                        start.setTime(date);
                                                    } catch (Exception ignored) {
                                                    }

                                                    String last;

                                                    if (object.isNull("message_preview")) {
                                                        last = getString(R.string.no_messages_yet);
                                                    } else {
                                                        if (!object.getJSONObject("message_preview").isNull("image_url")) {
                                                            last = getString(R.string.image_str);
                                                        } else {
                                                            last = object.getJSONObject("message_preview").getString("content");
                                                        }
                                                    }

                                                    list.add(new Chat(
                                                            0,
                                                            "qwerty",
                                                            start,
                                                            last,
                                                            "",
                                                            object.getString("id")
                                                    ));
                                                }

                                                adapter.setCollection(list);
                                                adapter.notifyDataSetChanged();

                                            } catch (Exception ignored) {

                                            }
                                        }
                                    })
                                    .subscriptions()
                                    .perform();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .notifications()
                .perform();
    }

    @Override
    public void onRefresh() {
        load();
    }
}