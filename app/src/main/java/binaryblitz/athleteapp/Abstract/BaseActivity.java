package binaryblitz.athleteapp.Abstract;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer.util.Util;
import com.squareup.picasso.Picasso;

import binaryblitz.athleteapp.Activities.CalendarActivity;
import binaryblitz.athleteapp.Activities.ChatsActivity;
import binaryblitz.athleteapp.Activities.MyProfileActivity;
import binaryblitz.athleteapp.Activities.NewsActivity;
import binaryblitz.athleteapp.Activities.PlayerActivity;
import binaryblitz.athleteapp.Activities.ProfsActivity;
import binaryblitz.athleteapp.Activities.StoreActivity;
import binaryblitz.athleteapp.Custom.DrawerArrowDrawable;
import binaryblitz.athleteapp.Data.User;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.DeviceInfoStore;
import binaryblitz.athleteapp.Server.GetFitServerRequest;

public class BaseActivity extends AppCompatActivity implements InternetConnectionDependentActivity {

    protected DrawerArrowDrawable drawer_arrow;
    private float offset;
    private boolean flipped;

    protected void init() {
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ImageView imageView = (ImageView) findViewById(R.id.drawer_indicator);
        final Resources resources = getResources();

        drawer_arrow = new DrawerArrowDrawable(resources);
        //drawer_arrow.setStrokeColor(Color.WHITE);
        imageView.setImageDrawable(drawer_arrow);

        drawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                offset = slideOffset;

                // Sometimes slideOffset ends up so close to but not quite 1 or 0.
                if (slideOffset >= .995) {
                    flipped = true;
                    drawer_arrow.setFlip(flipped);
                } else if (slideOffset <= .005) {
                    flipped = false;
                    drawer_arrow.setFlip(flipped);
                }

                drawer_arrow.setParameter(offset);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {

                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });


        try {
            User user = User.fromString(DeviceInfoStore.getUser());

            Picasso.with(this)
                    .load(user.getBannerUrl())
                    .into((ImageView) findViewById(R.id.photo_menu));

            Picasso.with(this)
                    .load(user.getAvatarUrl())
                    .into((ImageView) findViewById(R.id.avatar_drawer));

            ((TextView) findViewById(R.id.name_drawer)).setText(user.getName());

            findViewById(R.id.photo_menu).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mpdIntent = new Intent(BaseActivity.this, PlayerActivity.class)
                            .setData(Uri.parse("http://www.youtube.com/api/manifest/dash/id/bf5bb2419360daf1/source/youtube?"
                                    + "as=fmp4_audio_clear,fmp4_sd_hd_clear&sparams=ip,ipbits,expire,source,id,as&ip=0.0.0.0&"
                                    + "ipbits=0&expire=19000000000&signature=51AF5F39AB0CEC3E5497CD9C900EBFEAECCCB5C7."
                                    + "8506521BFC350652163895D4C26DEE124209AA9E&key=ik0"))
                            .putExtra(PlayerActivity.CONTENT_ID_EXTRA, "Video")
                            .putExtra(PlayerActivity.CONTENT_TYPE_EXTRA, Util.TYPE_DASH)
                            .putExtra(PlayerActivity.PROVIDER_EXTRA, "");
                    startActivity(mpdIntent);
                }
            });


        } catch (Exception e) {
        }

        findViewById(R.id.messages_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseActivity.this, ProfsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.my_events_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseActivity.this, NewsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.my_invites_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseActivity.this, StoreActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.friends_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseActivity.this, CalendarActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.my_profile_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseActivity.this, MyProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.messages_btnfdfd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseActivity.this, ChatsActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void cancelRequest() {

    }
}
