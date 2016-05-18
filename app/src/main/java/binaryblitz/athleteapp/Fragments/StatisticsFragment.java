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
import android.widget.TextView;

import org.json.JSONObject;

import binaryblitz.athleteapp.Adapters.StoreAdapter;
import binaryblitz.athleteapp.Data.User;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.DeviceInfoStore;
import binaryblitz.athleteapp.Server.GetFitServerRequest;
import binaryblitz.athleteapp.Server.OnRequestPerformedListener;

/**
 * Created by evgenijefanov on 24.10.15.
 */
public class StatisticsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.stat_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
//
//        RecyclerView view = (RecyclerView) getView().findViewById(R.id.recyclerView);
//        view.setItemAnimator(new DefaultItemAnimator());
//        view.setLayoutManager(new LinearLayoutManager(getContext()));
//        view.setAdapter(new StoreAdapter(getActivity()));

        try {
            GetFitServerRequest.with(getActivity())
                    .authorize()
                    .listener(new OnRequestPerformedListener() {
                        @Override
                        public void onRequestPerformedListener(Object... objects) {
                            Log.e("qwerty", objects[0].toString());

                            try {
                                JSONObject object = (JSONObject) objects[0];

                                ((TextView) getView().findViewById(R.id.textView12)).setText(Integer.toString(object.getInt("workouts_count")));
                                ((TextView) getView().findViewById(R.id.textView13)).setText(Integer.toString(object.getInt("total_duration")));
                                ((TextView) getView().findViewById(R.id.textView14)).setText(Integer.toString(object.getInt("total_distance")));
                            } catch (Exception e) {

                            }
                        }
                    })
                    .statistics(User.fromString(DeviceInfoStore.getUser()).getId())
                    .perform();
        } catch (Exception e) {

        }

    }
}