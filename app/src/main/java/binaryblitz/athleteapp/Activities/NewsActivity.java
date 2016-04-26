package binaryblitz.athleteapp.Activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import binaryblitz.athleteapp.Adapters.NewsAdapter;
import binaryblitz.athleteapp.Data.Comment;
import binaryblitz.athleteapp.Data.Post;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.GetFitServerRequest;
import binaryblitz.athleteapp.Server.OnRequestPerformedListener;

public class NewsActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    NewsAdapter adapter;
    private SwipeRefreshLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_layout);
        init();

        RecyclerView view = (RecyclerView) findViewById(R.id.recyclerView);
        view.setItemAnimator(new DefaultItemAnimator());
        view.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewsAdapter(this);
        view.setAdapter(adapter);

        layout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        layout.setOnRefreshListener(this);
        layout.setColorSchemeResources(R.color.accent_color);

        layout.setRefreshing(true);
        load();
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

                        ArrayList<Post> news = new ArrayList<>();

                        try {
                            Log.e("qwerty", objects[0].toString());
                            JSONArray array = (JSONArray) objects[0];

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                Calendar start = Calendar.getInstance();

                                try {
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                                    format.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    Date date = format.parse(object.getString("updated_at"));
                                    start.setTime(date);
                                } catch (Exception ignored) {}

                                news.add(new Post(
                                        object.getString("id"),
                                        "Henry Harrison",
                                        "1",
                                        null,
                                        R.drawable.test10,
                                        object.getString("content"),
                                        GetFitServerRequest.baseUrl + object.getString("image_url"),
                                        R.drawable.test2,
                                        start,
                                        object.getInt("likes_count"),
                                        new ArrayList<Comment>(),
                                        object.getString("like_id"),
                                        !object.isNull("like_id")));
                            }

                            adapter.setNews(news);
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {

                        }
                    }
                })
                .posts()
                .perform();
    }

    @Override
    public void cancelRequest() {
        Snackbar.make(findViewById(R.id.main), R.string.lost_connection_str, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        load();
    }
}
