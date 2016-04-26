package binaryblitz.athleteapp.Abstract;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import binaryblitz.athleteapp.Activities.CalendarActivity;
import binaryblitz.athleteapp.Activities.ChatsActivity;
import binaryblitz.athleteapp.Activities.MyProfileActivity;
import binaryblitz.athleteapp.Activities.NewsActivity;
import binaryblitz.athleteapp.Activities.ProfsActivity;
import binaryblitz.athleteapp.Activities.StoreActivity;
import binaryblitz.athleteapp.Custom.DrawerArrowDrawable;
import binaryblitz.athleteapp.R;

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
