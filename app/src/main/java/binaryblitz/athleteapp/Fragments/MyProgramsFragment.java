package binaryblitz.athleteapp.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import binaryblitz.athleteapp.Abstract.BaseActivity;
import binaryblitz.athleteapp.Activities.ProfileActivity;
import binaryblitz.athleteapp.Adapters.MyProgramsAdapter;
import binaryblitz.athleteapp.Data.Program;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.GetFitServerRequest;
import binaryblitz.athleteapp.Server.OnRequestPerformedListener;

public class MyProgramsFragment extends Fragment {

    private MyProgramsAdapter adapter;

    private boolean other = false;

    public boolean isOther() {
        return other;
    }

    public void setOther(boolean other) {
        this.other = other;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profs_layout, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        RecyclerView view = (RecyclerView) getView().findViewById(R.id.recyclerView);
        view.setItemAnimator(new DefaultItemAnimator());
        view.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MyProgramsAdapter(getActivity());
        view.setAdapter(adapter);

        if(other) {
            GetFitServerRequest.with(getActivity())
                    .authorize()
                    .listener(new OnRequestPerformedListener() {
                                  @Override
                                  public void onRequestPerformedListener(Object... objects) {
                                      if (objects[0].equals("Internet")) {
                                          ((BaseActivity) getActivity()).cancelRequest();
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
                    .userPrograms(ProfileActivity.id)
                    .perform();
        } else {
            GetFitServerRequest.with(getActivity())
                    .authorize()
                    .listener(new OnRequestPerformedListener() {
                                  @Override
                                  public void onRequestPerformedListener(Object... objects) {
                                      if (objects[0].equals("Internet")) {
                                          ((BaseActivity) getActivity()).cancelRequest();
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
                    .myPrograms()
                    .perform();
        }

    }
}