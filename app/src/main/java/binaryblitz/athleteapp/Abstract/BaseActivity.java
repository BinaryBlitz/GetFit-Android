package binaryblitz.athleteapp.Abstract;

import android.content.Intent;
import android.content.res.Resources;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import binaryblitz.athleteapp.Activities.AuthActivity;
import binaryblitz.athleteapp.Activities.CalendarActivity;
import binaryblitz.athleteapp.Activities.ChatsActivity;
import binaryblitz.athleteapp.Activities.MyProfileActivity;
import binaryblitz.athleteapp.Activities.NewsActivity;
import binaryblitz.athleteapp.Activities.ProfsActivity;
import binaryblitz.athleteapp.Activities.StoreActivity;
import binaryblitz.athleteapp.Custom.DrawerArrowDrawable;
import binaryblitz.athleteapp.Data.User;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.DeviceInfoStore;

public class BaseActivity extends AppCompatActivity implements InternetConnectionDependentActivity {

    protected DrawerArrowDrawable drawer_arrow;
    private float offset;
    private boolean flipped;

    protected void init() {
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ImageView imageView = (ImageView) findViewById(R.id.drawer_indicator);
        final Resources resources = getResources();

        drawer_arrow = new DrawerArrowDrawable(resources);
        imageView.setImageDrawable(drawer_arrow);

        drawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                offset = slideOffset;

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


        } catch (Exception e) {
            ((TextView) findViewById(R.id.name_drawer)).setText(R.string.not_logged_drawer_str);

            findViewById(R.id.photo_menu).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BaseActivity.this, AuthActivity.class);
                    startActivity(intent);
                }
            });
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
        Snackbar.make(findViewById(R.id.main), R.string.lost_connection_str, Snackbar.LENGTH_SHORT).show();
    }
}
