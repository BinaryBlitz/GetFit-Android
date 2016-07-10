package binaryblitz.athleteapp.Activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import binaryblitz.athleteapp.Abstract.BaseActivity;
import binaryblitz.athleteapp.Adapters.NewsAdapter;
import binaryblitz.athleteapp.Data.Post;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.GetFitServerRequest;
import binaryblitz.athleteapp.Server.OnRequestPerformedListener;

public class NewsActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private NewsAdapter adapter;
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
                        layout.setRefreshing(false);
                        if (objects[0].equals("Internet")) {
                            cancelRequest();
                            return;
                        }
                        if (objects[0].equals("Error")) {
                            cancelRequest();
                            return;
                        }

                        ArrayList<Post> news = new ArrayList<>();

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
                                } catch (Exception ignored) {}

                                Post post = new Post(
                                        object.getString("id"),
                                        object.getJSONObject("trainer").getString("first_name")
                                                + " " + object.getJSONObject("trainer").getString("last_name"),
                                        object.getJSONObject("trainer").getString("id"),
                                        object.getJSONObject("trainer").isNull("avatar_url") ? null : GetFitServerRequest.imagesUrl + object.getJSONObject("trainer").getString("avatar_url"),
                                        object.getString("content"),
                                        object.isNull("image_url") ? null : GetFitServerRequest.imagesUrl + object.getString("image_url"),
                                        start,
                                        object.getInt("likes_count"),
                                        object.getInt("comments_count"),
                                        object.isNull("like_id") ? "" : object.getString("like_id"),
                                        !object.isNull("like_id"));

                                if(!object.isNull("program")) {
                                    post.setProgramName(object.getJSONObject("program").getString("name"));
                                    post.setProgramId(object.getJSONObject("program").getString("id"));
                                    post.setProgramPrice(object.getJSONObject("program").getString("price") + "$");
                                    post.setProgramType(object.getJSONObject("program").getJSONObject("program_type").getString("name"));
                                    post.setProgramWorkouts(object.getJSONObject("program").getString("workouts_count") + " workouts");
                                }

                                news.add(post);
                            }

                            Collections.sort(news, new Comparator<Post>() {
                                        @Override
                                        public int compare(Post lhs, Post rhs) {
                                            if(lhs.getDate().before(rhs.getDate())) {
                                                return 1;
                                            } else {
                                                return -1;
                                            }
                                        }
                                    });

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
    public void onRefresh() {
        load();
    }
}
