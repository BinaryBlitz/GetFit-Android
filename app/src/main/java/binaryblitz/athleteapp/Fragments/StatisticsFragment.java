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

import binaryblitz.athleteapp.Abstract.BaseActivity;
import binaryblitz.athleteapp.Activities.ProfileActivity;
import binaryblitz.athleteapp.Adapters.StoreAdapter;
import binaryblitz.athleteapp.Data.User;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.DeviceInfoStore;
import binaryblitz.athleteapp.Server.GetFitServerRequest;
import binaryblitz.athleteapp.Server.OnRequestPerformedListener;

public class StatisticsFragment extends Fragment {

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
        return inflater.inflate(R.layout.stat_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            GetFitServerRequest.with(getActivity())
                    .authorize()
                    .listener(new OnRequestPerformedListener() {
                        @Override
                        public void onRequestPerformedListener(Object... objects) {
                            if (objects[0].equals("Internet")) {
                                ((BaseActivity) getActivity()).cancelRequest();
                                return;
                            }
                            try {
                                JSONObject object = (JSONObject) objects[0];

                                ((TextView) getView().findViewById(R.id.textView12)).setText(Integer.toString(object.getInt("workouts_count")));
                                ((TextView) getView().findViewById(R.id.textView13)).setText(Integer.toString(object.getInt("total_duration")));
                                ((TextView) getView().findViewById(R.id.textView14)).setText(Integer.toString(object.getInt("total_distance")));
                            } catch (Exception e) {

                            }
                        }
                    })
                    .statistics(other ? ProfileActivity.id : User.fromString(DeviceInfoStore.getUser()).getId())
                    .perform();
        } catch (Exception e) {

        }

    }
}