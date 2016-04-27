package binaryblitz.athleteapp.Activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import binaryblitz.athleteapp.Abstract.BaseActivity;
import binaryblitz.athleteapp.Adapters.NewsAdapter;
import binaryblitz.athleteapp.Adapters.StoreAdapter;
import binaryblitz.athleteapp.Data.Post;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_layout);
        init();

        RecyclerView view = (RecyclerView) findViewById(R.id.recyclerView);
        view.setItemAnimator(new DefaultItemAnimator());
        view.setLayoutManager(new LinearLayoutManager(this));

        adapter = new StoreAdapter(this);
        view.setAdapter(adapter);

        fab = findViewById(R.id.fab12);

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
                                                  "Cardio",
                                                  5,
                                                  object.getString("description"),
                                                  object.getString("duration"),
                                                  159,
                                                  5,
                                                  object.getJSONObject("trainer").getString("first_name") + " " +
                                                          object.getJSONObject("trainer").getString("last_name"),
                                                  "1",
                                                  GetFitServerRequest.baseUrl + object.getString("banner_url")
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
    public void cancelRequest() {
        Snackbar.make(findViewById(R.id.main), R.string.lost_connection_str, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        load();
    }
}