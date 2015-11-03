package binaryblitz.athleteapp.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import binaryblitz.athleteapp.Adapters.NewsAdapter;
import binaryblitz.athleteapp.R;

public class NewsFragment extends Fragment {

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
        view.setAdapter(new NewsAdapter(getActivity()));

    }
}