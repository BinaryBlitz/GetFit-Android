package binaryblitz.athleteapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import binaryblitz.athleteapp.Abstract.BaseActivity;
import binaryblitz.athleteapp.Adapters.ChatsAdapter;
import binaryblitz.athleteapp.Adapters.NewsAdapter;
import binaryblitz.athleteapp.Data.Chat;
import binaryblitz.athleteapp.Data.SubscriptionsSet;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.GetFitServerRequest;
import binaryblitz.athleteapp.Server.OnRequestPerformedListener;


public class ChatsActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ChatsAdapter adapter;
    private SwipeRefreshLayout layout;

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
                        Log.e("qwerty", objects[0].toString());
                        layout.setRefreshing(false);
                        try {
                            SubscriptionsSet
                                    .load()
                                    .clear();
                            JSONArray array = (JSONArray) objects[0];

                            ArrayList<Chat> list = new ArrayList<>();

                            for(int i = 0; i < array.length(); i++) {
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
                                } catch (Exception ignored) {}

                                String last = "";

                                if(object.isNull("message_preview")) {
                                    last = "No messages yet";
                                } else {
                                    if(!object.getJSONObject("message_preview").isNull("image_url")) {
                                        last = "Image";
                                    } else {
                                        last = object.getJSONObject("message_preview").getString("content");
                                    }
                                }

                                list.add(new Chat(
                                        0,
                                        "qwerty",
                                        start,
                                        last,
                                        0,
                                        object.getString("id")
                                ));
                            }

                            adapter.setCollection(list);
                            adapter.notifyDataSetChanged();

                        } catch (Exception e) {

                        }
                    }
                })
                .subscriptions()
                .perform();
    }

    @Override
    public void onRefresh() {
        load();
    }
}