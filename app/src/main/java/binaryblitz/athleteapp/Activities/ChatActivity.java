package binaryblitz.athleteapp.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import binaryblitz.athleteapp.Abstract.BaseActivity;
import binaryblitz.athleteapp.Adapters.ChatAdapter;
import binaryblitz.athleteapp.Data.Chat;
import binaryblitz.athleteapp.Data.Message;
import binaryblitz.athleteapp.Data.SubscriptionsSet;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.GetFitServerRequest;
import binaryblitz.athleteapp.Server.OnRequestPerformedListener;
import binaryblitz.athleteapp.Utils.AndroidUtils;

public class ChatActivity extends BaseActivity {

    private ChatAdapter adapter;
    private RecyclerView view;

    private String id;

    private static CountDownTimer timer;

    private void openImagePicker() {
        Config config = new Config();
        config.setSelectionMin(1);
        config.setSelectionLimit(3);
        ImagePickerActivity.setConfig(config);

        Intent intent = new Intent(ChatActivity.this, ImagePickerActivity.class);
        startActivityForResult(intent, 13);
    }

    @Override
    public void onResume() {
        super.onResume();
        timer.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_layout);

        findViewById(R.id.drawer_indicator).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        id = getIntent().getStringExtra("id");

        view = (RecyclerView) findViewById(R.id.recyclerView);
        view.setItemAnimator(new DefaultItemAnimator());
        view.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatAdapter(this);
        view.setAdapter(adapter);
        view.scrollToPosition(adapter.getItemCount() - 1);

        findViewById(R.id.imageView16).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ChatActivity.this,
                                new String[]{
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.CAMERA},
                                2);
                    } else {
                        openImagePicker();
                    }
                } else {
                    openImagePicker();
                }
            }
        });

        findViewById(R.id.send_comment_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((EditText) findViewById(R.id.editText)).getText().toString().isEmpty()) {
                    return;
                }

                GetFitServerRequest.with(ChatActivity.this)
                        .authorize()
                        .listener(new OnRequestPerformedListener() {
                            @Override
                            public void onRequestPerformedListener(Object... objects) {
                                if (objects[0].equals("Internet")) {
                                    cancelRequest();
                                }
                            }
                        })
                        .sendMessage(id, null, ((EditText) findViewById(R.id.editText)).getText().toString())
                        .perform();

                adapter.add(new Message("1", ((EditText) findViewById(R.id.editText)).getText().toString(), Calendar.getInstance(), null, true, null));
                view.scrollToPosition(adapter.getItemCount() - 1);
                ((EditText) findViewById(R.id.editText)).setText("");
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(findViewById(R.id.editText).getWindowToken(), 0);
            }
        });

        load();

        timer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                load();
            }
        };
    }

    public void scroll() {
        view.scrollToPosition(adapter.getItemCount() - 1);
    }

    private void load() {
        if(!id.equals("notif")) {
            GetFitServerRequest.with(this)
                    .authorize()
                    .listener(new OnRequestPerformedListener() {
                        @Override
                        public void onRequestPerformedListener(Object... objects) {
                            try {
                                JSONArray array = (JSONArray) objects[0];
                                if (adapter.getItemCount() == array.length() + 1) {
                                    timer.cancel();
                                    timer.start();
                                    return;
                                }

                                if (objects[0].equals("AuthError")) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!isFinishing()) {
                                                new AlertDialog.Builder(ChatActivity.this)
                                                        .setTitle(getString(R.string.title_str))
                                                        .setMessage(getString(R.string.reg_alert_str))
                                                        .setCancelable(false)
                                                        .setPositiveButton(getString(R.string.cont_upcase_str), new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                Intent intent = new Intent(ChatActivity.this, AuthActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        })
                                                        .setNegativeButton(getString(R.string.cancel_upcase_str), new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {

                                                            }
                                                        })
                                                        .show();
                                            }
                                        }
                                    });

                                    return;
                                }

                                if (objects[0].equals("Internet")) {
                                    cancelRequest();
                                    return;
                                }
                                if (objects[0].equals("Error")) {
                                    Snackbar.make(findViewById(R.id.main), R.string.error_try_str, Snackbar.LENGTH_SHORT).show();
                                    return;
                                }

                                ArrayList<Message> messages = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = array.getJSONObject(i);

                                    Calendar start = Calendar.getInstance();
                                    try {
                                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                                        format.setTimeZone(TimeZone.getTimeZone("UTC"));
                                        Date date = format.parse(object.getString("created_at"));
                                        start.setTime(date);
                                    } catch (Exception ignored) {
                                    }


                                    messages.add(0, new Message(
                                            object.getString("id"),
                                            object.getString("content"),
                                            start,
                                            object.isNull("image_url") ? null : object.getString("image_url"),
                                            object.getString("category").equals("user"),
                                            object.isNull("image_url") ? null : object.getString("image_url")
                                    ));
                                }
                                adapter.setCollection(messages);
                                adapter.notifyDataSetChanged();

                                scroll();
                                timer.cancel();
                                timer.start();
                            } catch (Exception ignored) {
                            }
                        }
                    })
                    .messages(id)
                    .perform();
        } else {

            GetFitServerRequest.with(this)
                    .authorize()
                    .listener(new OnRequestPerformedListener() {
                        @Override
                        public void onRequestPerformedListener(Object... objects) {
                            try {
                                if (objects[0].equals("Internet")) {
                                    cancelRequest();
                                    return;
                                }
                                if (objects[0].equals("Error")) {
                                    Snackbar.make(findViewById(R.id.main), R.string.error_try_str, Snackbar.LENGTH_SHORT).show();
                                    return;
                                }
                                JSONArray array = (JSONArray) objects[0];

                                ArrayList<Message> messages = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = array.getJSONObject(i);

                                    Calendar start = Calendar.getInstance();
                                    try {
                                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                                        format.setTimeZone(TimeZone.getTimeZone("UTC"));
                                        Date date = format.parse(object.getString("created_at"));
                                        start.setTime(date);
                                    } catch (Exception ignored) {
                                    }

                                    messages.add(0, new Message(
                                            object.getString("id"),
                                            object.getString("content"),
                                            start,
                                            object.isNull("image_url") ? null : object.getString("image_url"),
                                            false,
                                            object.isNull("image_url") ? null : object.getString("image_url")
                                    ));
                                }
                                adapter.setCollection(messages);
                                adapter.notifyDataSetChanged();

                            } catch (Exception ignored) {

                            }
                        }
                    })
                    .notifications()
                    .perform();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 13) {
            if (data != null) {
                ArrayList<Uri> photos = data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

                for(Uri photo : photos) {
                    adapter.add(new Message("1", "Hello", Calendar.getInstance(), "file:///" + photo.toString(), true, "file:///" + photo.toString()));
                    view.scrollToPosition(adapter.getItemCount() - 1);

                    GetFitServerRequest.with(this)
                            .authorize()
                            .listener(new OnRequestPerformedListener() {
                                @Override
                                public void onRequestPerformedListener(Object... objects) {
                                    if (objects[0].equals("Internet")) {
                                        cancelRequest();
                                    }
                                }
                            })
                            .sendMessage(id, photo.toString(), null)
                            .perform();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 2: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImagePicker();
                }
            }
        }
    }
}