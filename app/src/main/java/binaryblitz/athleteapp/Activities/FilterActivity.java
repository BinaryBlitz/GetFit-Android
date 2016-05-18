package binaryblitz.athleteapp.Activities;

import android.os.Bundle;
import android.view.View;

import binaryblitz.athleteapp.Abstract.BaseActivity;
import binaryblitz.athleteapp.R;

public class FilterActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_layout);

        findViewById(R.id.drawer_indicator).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}