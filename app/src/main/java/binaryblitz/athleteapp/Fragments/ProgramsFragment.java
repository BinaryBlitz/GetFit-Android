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

import java.util.ArrayList;

import binaryblitz.athleteapp.Adapters.NewsAdapter;
import binaryblitz.athleteapp.Adapters.StoreAdapter;
import binaryblitz.athleteapp.Data.Post;
import binaryblitz.athleteapp.Data.Program;
import binaryblitz.athleteapp.R;

public class ProgramsFragment extends Fragment {

    private StoreAdapter adapter;
    private ArrayList<Program> collection;

    public void setCollection(ArrayList<Program> collection) {
        this.collection = collection;

        adapter.setCollection(collection);
        adapter.notifyDataSetChanged();
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

        adapter = new StoreAdapter(getActivity());
        view.setAdapter(adapter);

    }
}