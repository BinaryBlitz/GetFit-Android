package binaryblitz.athleteapp.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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

import binaryblitz.athleteapp.Activities.ProfsActivity;
import binaryblitz.athleteapp.Adapters.ProfsAdapter;
import binaryblitz.athleteapp.Data.Professional;
import binaryblitz.athleteapp.Data.ProfessionalType;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.GetFitServerRequest;
import binaryblitz.athleteapp.Server.OnRequestPerformedListener;
import binaryblitz.athleteapp.Utils.AndroidUtils;
import binaryblitz.athleteapp.Utils.ShowHideScrollListener;

public class ProfsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ProfsAdapter adapter;
    private SwipeRefreshLayout layout;
    private ProfessionalType type;

    public void setType(ProfessionalType type) {
        this.type = type;
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
        adapter = new ProfsAdapter(getActivity());
        view.setAdapter(adapter);

        layout = (SwipeRefreshLayout) getView().findViewById(R.id.refresh);
        layout.setOnRefreshListener(this);
        layout.setColorSchemeResources(R.color.accent_color);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                load();
            }
        }, 200);

        view.addOnScrollListener(new ShowHideScrollListener() {
            @Override
            public void onHide() {
                AndroidUtils.hideFab(((ProfsActivity) getActivity()).getFab());
            }

            @Override
            public void onShow() {
                AndroidUtils.showFab(((ProfsActivity) getActivity()).getFab());
            }
        });

    }

    private void load() {
        GetFitServerRequest.with(getActivity())
                .authorize()
                .listener(new OnRequestPerformedListener() {
                    @Override
                    public void onRequestPerformedListener(Object... objects) {
                        layout.setRefreshing(false);

                        try {
                            JSONArray array = (JSONArray) objects[0];
                            ArrayList<Professional> collection = new ArrayList<>();

                            for(int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);

                                Professional professional = new Professional(
                                        object.getString("id"),
                                        GetFitServerRequest.imagesUrl + object.getString("banner_url"),
                                        GetFitServerRequest.imagesUrl + object.getString("avatar_url"),
                                        object.getString("first_name") + " " + object.getString("last_name"),
                                        object.getString("description"),
                                        type,
                                        false,
                                        object.getInt("programs_count"),
                                        object.getInt("followers_count"),
                                        object.getDouble("rating")
                                );

                                try {
                                    professional.setFollowId(object.getJSONObject("following_id").getString("id"));
                                } catch (Exception ignored) {}

                                professional.setFollowing(!object.isNull("following_id"));

                                collection.add(professional);
                            }

                            adapter.setCollection(collection);
                            adapter.notifyDataSetChanged();

                            if(adapter.getItemCount() == 0) {
                                getView().findViewById(R.id.noitems).setVisibility(View.VISIBLE);
                            } else {
                                getView().findViewById(R.id.noitems).setVisibility(View.GONE);
                            }
                        } catch (Exception ignored) {
                        }
                    }
                })
                .trainers(type)
                .perform();
    }

    public void filter(String spec, String order) {
        GetFitServerRequest.with(getActivity())
                .authorize()
                .listener(new OnRequestPerformedListener() {
                    @Override
                    public void onRequestPerformedListener(Object... objects) {

                        layout.setRefreshing(false);

                        try {
                            JSONArray array = (JSONArray) objects[0];
                            ArrayList<Professional> collection = new ArrayList<>();

                            for(int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);

                                Professional professional = new Professional(
                                        object.getString("id"),
                                        GetFitServerRequest.imagesUrl + object.getString("banner_url"),
                                        GetFitServerRequest.imagesUrl + object.getString("avatar_url"),
                                        object.getString("first_name") + " " + object.getString("last_name"),
                                        object.getString("description"),
                                        type,
                                        false,
                                        object.getInt("programs_count"),
                                        object.getInt("followers_count"),
                                        object.getDouble("rating")
                                );

                                try {
                                    professional.setFollowId(object.getJSONObject("following_id").getString("id"));
                                } catch (Exception ignored) {}

                                professional.setFollowing(!object.isNull("following_id"));

                                collection.add(professional);
                            }

                            adapter.setCollection(collection);
                            adapter.notifyDataSetChanged();

                            if(adapter.getItemCount() == 0) {
                                getView().findViewById(R.id.noitems).setVisibility(View.VISIBLE);
                            } else {
                                getView().findViewById(R.id.noitems).setVisibility(View.GONE);
                            }
                        } catch (Exception ignored) {
                        }
                    }
                })
                .filterTrainers(type, spec, order)
                .perform();
    }

    @Override
    public void onRefresh() {
        load();
    }
}
