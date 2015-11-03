package binaryblitz.athleteapp.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import binaryblitz.athleteapp.Activities.ProfsActivity;
import binaryblitz.athleteapp.Adapters.ProfsAdapter;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Utils.AndroidUtils;
import binaryblitz.athleteapp.Utils.ShowHideScrollListener;

public class ProfsFragment extends Fragment {

    private int position;

    public void setPosition(int position) {
        this.position = position;
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
        final ProfsAdapter adapter = new ProfsAdapter(getActivity());
        view.setAdapter(adapter);
        adapter.setCollection(position);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(adapter.getItemCount() == 0) {
                    getView().findViewById(R.id.noitems).setVisibility(View.VISIBLE);
                } else {
                    getView().findViewById(R.id.noitems).setVisibility(View.GONE);
                }
            }
        }, 250);

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
}
