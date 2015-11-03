package binaryblitz.athleteapp.Activities;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import binaryblitz.athleteapp.Abstract.BaseActivity;
import binaryblitz.athleteapp.Adapters.NewsAdapter;
import binaryblitz.athleteapp.Adapters.StoreAdapter;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Utils.AndroidUtils;
import binaryblitz.athleteapp.Utils.ShowHideScrollListener;

public class StoreActivity extends BaseActivity {

    View fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_layout);
        init();

        RecyclerView view = (RecyclerView) findViewById(R.id.recyclerView);
        view.setItemAnimator(new DefaultItemAnimator());
        view.setLayoutManager(new LinearLayoutManager(this));
        view.setAdapter(new StoreAdapter(this));

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
    }
}