package binaryblitz.athleteapp.Activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import binaryblitz.athleteapp.Abstract.BaseActivity;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.GetFitServerRequest;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoActivity extends BaseActivity {

    public static String url;

    private ImageView mImageView;
    private PhotoViewAttacher mAttacher;

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

        mImageView = (ImageView) findViewById(R.id.pager);

        mAttacher = new PhotoViewAttacher(mImageView);
        Picasso.with(this).setLoggingEnabled(true);
        Picasso.with(this)
                .load(url)
                .into(mImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        mAttacher.update();
                    }

                    @Override
                    public void onError() {

                    }
                });

    }

}
