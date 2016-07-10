package binaryblitz.athleteapp.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import binaryblitz.athleteapp.Abstract.BaseActivity;
import binaryblitz.athleteapp.Adapters.CommentsAdapter;
import binaryblitz.athleteapp.Adapters.NewsAdapter;
import binaryblitz.athleteapp.Custom.ProgressDialog;
import binaryblitz.athleteapp.Data.Comment;
import binaryblitz.athleteapp.Data.Post;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.GetFitServerRequest;
import binaryblitz.athleteapp.Server.OnRequestPerformedListener;
import binaryblitz.athleteapp.Utils.DateUtils;

public class PostActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static String id;
    private SwipeRefreshLayout layout;
    private CommentsAdapter adapter;

    public static Post post;

    public void deleteComment() {
        int count = Integer.parseInt(((TextView) findViewById(R.id.textView4)).getText().toString());

        ((TextView) findViewById(R.id.textView4)).setText(Integer.toString(count - 1));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_layout);

        findViewById(R.id.drawer_indicator).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.imageView10).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (((EditText) findViewById(R.id.editText)).getText().toString().isEmpty()) {
                    return;
                }

                if(GetFitServerRequest.with(PostActivity.this).isAuthorized()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!isFinishing()) {
                                new AlertDialog.Builder(PostActivity.this)
                                        .setTitle(getString(R.string.title_str))
                                        .setMessage(getString(R.string.reg_alert_str))
                                        .setCancelable(false)
                                        .setPositiveButton(getString(R.string.cont_upcase_str), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(PostActivity.this, AuthActivity.class);
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
                dialog.show(getFragmentManager(), "athleteapp");

                JSONObject object = new JSONObject();

                try {
                    object.accumulate("content", ((EditText) findViewById(R.id.editText)).getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }


                JSONObject toSend = new JSONObject();
                try {
                    toSend.accumulate("comment", object);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                GetFitServerRequest.with(PostActivity.this)
                        .authorize()
                        .objects(toSend)
                        .listener(new OnRequestPerformedListener() {
                            @Override
                            public void onRequestPerformedListener(Object... objects) {
                                dialog.dismiss();

                                if (objects[0].equals("Internet")) {
                                    cancelRequest();
                                    return;
                                }
                                ((EditText) findViewById(R.id.editText)).setText("");

                                if (objects[0].equals("Error")) {
                                    cancelRequest();
                                    return;
                                }

                                try {
                                    JSONObject object = (JSONObject) objects[0];
                                    JSONObject user = object.getJSONObject("author");

                                    Calendar start = Calendar.getInstance();

                                    try {
                                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                                        format.setTimeZone(TimeZone.getTimeZone("UTC"));
                                        Date date = format.parse(object.getString("created_at"));
                                        start.setTime(date);
                                    } catch (Exception e) {

                                    }

                                    Comment comment = new Comment(
                                            object.getString("id"),
                                            object.getString("content"),
                                            user.getString("first_name") + " " + user.getString("last_name"),
                                            user.getString("id"),
                                            start,
                                            user.isNull("avatar_thumb_url") ? null : GetFitServerRequest.imagesUrl + user.getString("avatar_thumb_url")
                                    );

                                    adapter.add(comment);

                                    int count = Integer.parseInt(((TextView) findViewById(R.id.textView4)).getText().toString());

                                    ((TextView) findViewById(R.id.textView4)).setText(Integer.toString(count + 1));

                                    ((ScrollView) findViewById(R.id.scroll)).post(new Runnable() {

                                        @Override
                                        public void run() {
                                            ((ScrollView) findViewById(R.id.scroll)).fullScroll(ScrollView.FOCUS_DOWN);
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .createComment(id)
                        .perform();
            }
        });

        RecyclerView view = (RecyclerView) findViewById(R.id.recyclerView);
        view.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommentsAdapter(this);
        view.setAdapter(adapter);

        id = getIntent().getStringExtra("id");

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
                        layout.setRefreshing(false);
                        if (objects[0].equals("Internet")) {
                            cancelRequest();
                            return;
                        }
                        if (objects[0].equals("Error")) {
                            cancelRequest();
                            return;
                        }

                        ((TextView) findViewById(R.id.textView)).setText(post.getUserName());
                        ((TextView) findViewById(R.id.textView2)).setText(post.getDesc());
                        ((TextView) findViewById(R.id.textView3)).setText(Integer.toString(post.getLikeCount()));
                        ((TextView) findViewById(R.id.textView4)).setText(Integer.toString(post.getCommentsCount()));

                        if(post.getProgramName() == null) {
                            findViewById(R.id.program).setVisibility(View.GONE);
                        } else {
                            findViewById(R.id.program).setVisibility(View.VISIBLE);
                            findViewById(R.id.program).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(PostActivity.this, ProgramActivity.class);
                                    intent.putExtra("id", post.getProgramId());
                                    startActivity(intent);
                                }
                            });
                            ((TextView) findViewById(R.id.textView123)).setText(post.getProgramName());
                            ((TextView) findViewById(R.id.textView6)).setText(post.getProgramPrice());
                            ((TextView) findViewById(R.id.textView2123)).setText(post.getProgramType() + ", " + post.getProgramWorkouts());
                        }

                        Picasso.with(PostActivity.this)
                                .load(post.getUserPhotoUrl())
                                .into(((ImageView) findViewById(R.id.imageView)));

                        if(post.getPhotoUrl() == null) {
                            ((ImageView) findViewById(R.id.imageView2)).setVisibility(View.GONE);
                        } else {
                            ((ImageView) findViewById(R.id.imageView2)).setVisibility(View.VISIBLE);
                            Picasso.with(PostActivity.this)
                                    .load(post.getPhotoUrl())
                                    .into(((ImageView) findViewById(R.id.imageView2)));

                            ((ImageView) findViewById(R.id.imageView2)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(PostActivity.this, PhotoActivity.class);
                                    PhotoActivity.url = post.getPhotoUrl();
                                    startActivity(intent);
                                }
                            });
                        }


                        ((TextView) findViewById(R.id.textView)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(PostActivity.this, ProfProfileActivity.class);
                                intent.putExtra("id", post.getUserId());
                                startActivity(intent);
                            }
                        });

                        ((ImageView) findViewById(R.id.imageView)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(PostActivity.this, ProfProfileActivity.class);
                                intent.putExtra("id", post.getUserId());
                                startActivity(intent);
                            }
                        });

                        ((TextView) findViewById(R.id.textView5)).setText(DateUtils.getDateStringRepresentationForNews(post.getDate(), PostActivity.this));

                        if(post.isLiked()) {
                            ((ImageView) findViewById(R.id.imageView3)).setImageResource(R.drawable.like_filled);
                        } else {
                            ((ImageView) findViewById(R.id.imageView3)).setImageResource(R.drawable.like_active_ic);
                        }

                        ((TextView) findViewById(R.id.textView3)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(post.isLiked()) {
                                    ((ImageView) findViewById(R.id.imageView3)).setImageResource(R.drawable.like_active_ic);
                                    post.setLikeCount(post.getLikeCount() - 1);
                                    ((TextView) findViewById(R.id.textView3)).setText(Integer.toString(post.getLikeCount()));
                                    GetFitServerRequest.with(PostActivity.this)
                                            .authorize()
                                            .listener(new OnRequestPerformedListener() {
                                                @Override
                                                public void onRequestPerformedListener(Object... objects) {
                                                    if (objects[0].equals("Internet")) {
                                                        cancelRequest();
                                                    }
                                                }
                                            })
                                            .deleteLike(post.getLikeId())
                                            .perform();
                                } else {
                                    ((ImageView) findViewById(R.id.imageView3)).setImageResource(R.drawable.like_filled);
                                    post.setLikeCount(post.getLikeCount() + 1);
                                    ((TextView) findViewById(R.id.textView3)).setText(Integer.toString(post.getLikeCount()));
                                    GetFitServerRequest.with(PostActivity.this)
                                            .authorize()
                                            .listener(new OnRequestPerformedListener() {
                                                @Override
                                                public void onRequestPerformedListener(Object... objects) {
                                                    if (objects[0].equals("Internet")) {
                                                        cancelRequest();
                                                    }
                                                }
                                            })
                                            .like(post.getId())
                                            .perform();
                                }

                                post.setLiked(!post.isLiked());
                            }
                        });

                        ((ImageView) findViewById(R.id.imageView3)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(post.isLiked()) {
                                    ((ImageView) findViewById(R.id.imageView3)).setImageResource(R.drawable.like_active_ic);
                                    post.setLikeCount(post.getLikeCount() - 1);
                                    ((TextView) findViewById(R.id.textView3)).setText(Integer.toString(post.getLikeCount()));
                                    GetFitServerRequest.with(PostActivity.this)
                                            .authorize()
                                            .listener(new OnRequestPerformedListener() {
                                                @Override
                                                public void onRequestPerformedListener(Object... objects) {
                                                    if (objects[0].equals("Internet")) {
                                                        cancelRequest();
                                                    }
                                                }
                                            })
                                            .deleteLike(post.getLikeId())
                                            .perform();
                                } else {
                                    ((ImageView) findViewById(R.id.imageView3)).setImageResource(R.drawable.like_filled);
                                    post.setLikeCount(post.getLikeCount() + 1);
                                    ((TextView) findViewById(R.id.textView3)).setText(Integer.toString(post.getLikeCount()));
                                    GetFitServerRequest.with(PostActivity.this)
                                            .authorize()
                                            .listener(new OnRequestPerformedListener() {
                                                @Override
                                                public void onRequestPerformedListener(Object... objects) {
                                                    if (objects[0].equals("Internet")) {
                                                        cancelRequest();
                                                    }
                                                }
                                            })
                                            .like(post.getId())
                                            .perform();
                                }

                                post.setLiked(!post.isLiked());
                            }
                        });

                    }
                })
                .post(id)
                .perform();

        GetFitServerRequest.with(this)
                .authorize()
                .listener(new OnRequestPerformedListener() {
                    @Override
                    public void onRequestPerformedListener(Object... objects) {
                        try {
                            if (objects[0].equals("Internet")) {
                                cancelRequest();
                            }
                            JSONArray array = (JSONArray) objects[0];
                            ArrayList<Comment> comments = new ArrayList<>();

                            for(int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                JSONObject user = object.getJSONObject("author");

                                Calendar start = Calendar.getInstance();

                                try {
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                                    format.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    Date date = format.parse(object.getString("created_at"));
                                    start.setTime(date);
                                } catch (Exception e) {

                                }

                                Comment comment = new Comment(
                                        object.getString("id"),
                                        object.getString("content"),
                                        user.getString("first_name") + " " + user.getString("last_name"),
                                        user.getString("id"),
                                        start,
                                        user.isNull("avatar_thumb_url") ? null : GetFitServerRequest.imagesUrl + user.getString("avatar_thumb_url")
                                );

                                comments.add(comment);
                            }

                            adapter.setCollection(comments);
                            adapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .getComments(id)
                .perform();
    }

    @Override
    public void onRefresh() {
        load();
    }
}