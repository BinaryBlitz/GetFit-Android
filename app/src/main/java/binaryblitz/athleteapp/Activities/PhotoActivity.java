package binaryblitz.athleteapp.Activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;

import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.GetFitServerRequest;

public class PhotoActivity extends FragmentActivity {

    public static String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_activity);

        findViewById(R.id.drawer_indicator).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Picasso.with(this)
                .load(GetFitServerRequest.imagesUrl + url)
                .into((ImageView) findViewById(R.id.pager));

    }

}
