package binaryblitz.athleteapp.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import binaryblitz.athleteapp.Abstract.BaseActivity;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.GetFitServerRequest;
import binaryblitz.athleteapp.Server.OnRequestPerformedListener;

public class FilterActivity extends BaseActivity {

    private ArrayList<String> collection1;
    private ArrayList<String> collection2;

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

        collection1 = new ArrayList<>();
        collection2 = new ArrayList<>();

        ((RadioButton) findViewById(R.id.radioButton2)).setChecked(true);

        findViewById(R.id.spec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(FilterActivity.this)
                        .title(R.string.select_str)
                        .items(collection2)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                StoreActivity.specFilter = collection1.get(which);

                                ((TextView) findViewById(R.id.textView35rt)).setText(text);
                            }
                        })
                        .show();
            }
        });

        findViewById(R.id.price).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(FilterActivity.this)
                        .title(R.string.select_str)
                        .items(getString(R.string.free_str), getString(R.string.paid_str), getString(R.string.all_str))
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                StoreActivity.priceFilter = which == 0 ? "0" : which == 1 ? "1" : "-1";
                                ((TextView) findViewById(R.id.textView35)).setText(
                                        which == 0 ? getString(R.string.free_str) : which == 1 ?
                                                getString(R.string.paid_str) : getString(R.string.all_str)
                                );
                            }
                        })
                        .show();
            }
        });


        findViewById(R.id.textView16).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreActivity.filter = true;

                if(((RadioButton) findViewById(R.id.radioButton2)).isChecked()) {
                    StoreActivity.orderFilter = "followers_count";
                } else if(((RadioButton) findViewById(R.id.radioButton)).isChecked()) {
                    StoreActivity.orderFilter = "rating";
                } else {
                    StoreActivity.orderFilter = "created_at";
                }

                finish();
            }
        });

        GetFitServerRequest.with(this)
                .authorize()
                .listener(new OnRequestPerformedListener() {
                    @Override
                    public void onRequestPerformedListener(Object... objects) {
                        try {
                            JSONArray array = (JSONArray) objects[0];
                            collection1.add("0");
                            collection2.add(getString(R.string.all_str));
                            for(int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);

                                collection1.add(object.getString("id"));
                                collection2.add(object.getString("name"));
                            }
                        } catch (Exception e) {

                        }
                    }
                })
                .programTypes()
                .perform();
    }
}