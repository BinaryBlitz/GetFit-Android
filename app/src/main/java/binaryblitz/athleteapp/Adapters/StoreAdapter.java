package binaryblitz.athleteapp.Adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import binaryblitz.athleteapp.Abstract.BaseActivity;
import binaryblitz.athleteapp.Activities.AuthActivity;
import binaryblitz.athleteapp.Activities.ProfProfileActivity;
import binaryblitz.athleteapp.Activities.ProgramActivity;
import binaryblitz.athleteapp.Activities.StoreActivity;
import binaryblitz.athleteapp.Custom.RaitingDialog;
import binaryblitz.athleteapp.Data.MyProgramsSet;
import binaryblitz.athleteapp.Data.Program;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.GetFitServerRequest;
import binaryblitz.athleteapp.Server.OnRequestPerformedListener;

public class StoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity context;

    private ArrayList<Program> collection;

    public void setCollection(ArrayList<Program> collection) {
        this.collection = collection;
    }

    public StoreAdapter(Activity context) {
        this.context = context;
        collection = new ArrayList<>();
    }

    public void setContext(Activity context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.program_card, parent, false);

        return new NewsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        final NewsViewHolder holder = (NewsViewHolder) viewHolder;

        final Program program = collection.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProgramActivity.class);
                intent.putExtra("id", program.getId());
                context.startActivity(intent);
            }
        });

        holder.itemView.findViewById(R.id.trainer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfProfileActivity.class);
                intent.putExtra("id", program.getTrainerId());
                context.startActivity(intent);
            }
        });

        holder.itemView.findViewById(R.id.imageView3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(GetFitServerRequest.with(context).isAuthorized()) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!context.isFinishing()) {
                                new AlertDialog.Builder(context)
                                        .setTitle(context.getString(R.string.title_str))
                                        .setMessage(context.getString(R.string.reg_alert_str))
                                        .setCancelable(false)
                                        .setPositiveButton(context.getString(R.string.cont_upcase_str), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(context, AuthActivity.class);
                                                context.startActivity(intent);
                                            }
                                        })
                                        .setNegativeButton(context.getString(R.string.cancel_upcase_str), new DialogInterface.OnClickListener() {
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

                if(!MyProgramsSet.load().find(program.getId())) {
                    new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.title_str))
                            .setMessage(context.getString(R.string.program_rate_error_str))
                            .setCancelable(false)
                            .setPositiveButton(R.string.ok_str, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();

                    return;
                }

                RaitingDialog dialog = new RaitingDialog();
                dialog.setListener(new RaitingDialog.OnRateDialogFinished() {
                    @Override
                    public void OnRateDialogFinished(float rating) {

                        JSONObject object = new JSONObject();

                        try {
                            object.accumulate("value", (int) rating);
                            object.accumulate("content", "Gut");
                        } catch (Exception e) {
                            Log.e("qwerty", e.getLocalizedMessage());
                        }

                        JSONObject toSend = new JSONObject();

                        try {
                            toSend.accumulate("rating", object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(program.getRatingId() == null) {
                            GetFitServerRequest.with(context)
                                    .authorize()
                                    .objects(toSend)
                                    .listener(new OnRequestPerformedListener() {
                                        @Override
                                        public void onRequestPerformedListener(Object... objects) {
                                            if (objects[0].equals("Internet")) {
                                                ((BaseActivity) context).cancelRequest();
                                                return;
                                            }
                                            try {
                                                program.setStarCount(((JSONObject) objects[0]).getDouble("rating"));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            holder.like_count.setText(new DecimalFormat("#.#", DecimalFormatSymbols.getInstance(Locale.US)).format(program.getStarCount()));
                                        }
                                    })
                                    .rateProgram(program.getId())
                                    .perform();
                        } else {
                            GetFitServerRequest.with(context)
                                    .authorize()
                                    .objects(toSend)
                                    .listener(new OnRequestPerformedListener() {
                                        @Override
                                        public void onRequestPerformedListener(Object... objects) {
                                            if (objects[0].equals("Internet")) {
                                                ((BaseActivity) context).cancelRequest();
                                                return;
                                            }
                                            try {
                                                program.setStarCount(((JSONObject) objects[0]).getDouble("rating"));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            holder.like_count.setText(new DecimalFormat("#.#", DecimalFormatSymbols.getInstance(Locale.US)).format(program.getStarCount()));
                                        }
                                    })
                                    .patchRating(program.getRatingId())
                                    .perform();
                        }
                    }
                });

                dialog.show(context.getFragmentManager(), "rating");
            }
        });

        holder.itemView.findViewById(R.id.textView3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(GetFitServerRequest.with(context).isAuthorized()) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!context.isFinishing()) {
                                new AlertDialog.Builder(context)
                                        .setTitle(context.getString(R.string.title_str))
                                        .setMessage(context.getString(R.string.reg_alert_str))
                                        .setCancelable(false)
                                        .setPositiveButton(context.getString(R.string.cont_upcase_str), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(context, AuthActivity.class);
                                                context.startActivity(intent);
                                            }
                                        })
                                        .setNegativeButton(context.getString(R.string.cancel_upcase_str), new DialogInterface.OnClickListener() {
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

                if(!MyProgramsSet.load().find(program.getId())) {
                    new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.title_str))
                            .setMessage(context.getString(R.string.program_rate_error_str))
                            .setCancelable(false)
                            .setPositiveButton(R.string.ok_str, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();

                    return;
                }

                RaitingDialog dialog = new RaitingDialog();
                dialog.setListener(new RaitingDialog.OnRateDialogFinished() {
                    @Override
                    public void OnRateDialogFinished(float rating) {

                        JSONObject object = new JSONObject();

                        try {
                            object.accumulate("value", (int) rating);
                            object.accumulate("content", "Gut");
                        } catch (Exception e) {
                            Log.e("qwerty", e.getLocalizedMessage());
                        }

                        JSONObject toSend = new JSONObject();

                        try {
                            toSend.accumulate("rating", object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(program.getRatingId() == null) {
                            GetFitServerRequest.with(context)
                                    .authorize()
                                    .objects(toSend)
                                    .listener(new OnRequestPerformedListener() {
                                        @Override
                                        public void onRequestPerformedListener(Object... objects) {
                                            if (objects[0].equals("Internet")) {
                                                ((BaseActivity) context).cancelRequest();
                                                return;
                                            }
                                            try {
                                                program.setStarCount(((JSONObject) objects[0]).getDouble("rating"));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            holder.like_count.setText(new DecimalFormat("#.#", DecimalFormatSymbols.getInstance(Locale.US)).format(program.getStarCount()));
                                        }
                                    })
                                    .rateProgram(program.getId())
                                    .perform();
                        } else {
                            GetFitServerRequest.with(context)
                                    .authorize()
                                    .objects(toSend)
                                    .listener(new OnRequestPerformedListener() {
                                        @Override
                                        public void onRequestPerformedListener(Object... objects) {
                                            if (objects[0].equals("Internet")) {
                                                ((BaseActivity) context).cancelRequest();
                                                return;
                                            }
                                            try {
                                                program.setStarCount(((JSONObject) objects[0]).getDouble("rating"));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            holder.like_count.setText(new DecimalFormat("#.#", DecimalFormatSymbols.getInstance(Locale.US)).format(program.getStarCount()));
                                        }
                                    })
                                    .patchRating(program.getRatingId())
                                    .perform();
                        }
                    }
                });

                dialog.show(context.getFragmentManager(), "rating");
            }
        });

        holder.user_name.setText(program.getTrainerName());
        holder.name.setText(program.getName());
        holder.post_desc.setText(program.getDesc());
        holder.like_count.setText(new DecimalFormat("#.#", DecimalFormatSymbols.getInstance(Locale.US)).format(program.getStarCount()));
        holder.text_count.setText(Integer.toString(program.getUserCount()));

        if(program.getPhotoUrl() == null || program.getPhotoUrl().equals("No photo")) {
            holder.post_photo.setVisibility(View.GONE);
        } else {
            holder.post_photo.setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(program.getPhotoUrl())
                    .into(holder.post_photo);
        }

        if(program.getUserPhotoUrl() == null || program.getUserPhotoUrl().equals("No photo")) {
            holder.user_avatar.setVisibility(View.GONE);
        } else {
            holder.user_avatar.setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(program.getUserPhotoUrl())
                    .into(holder.user_avatar);
        }

        holder.user_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfProfileActivity.class);
                intent.putExtra("id", program.getTrainerId());
                context.startActivity(intent);
            }
        });

        holder.price.setText("$" + program.getPrice());

        holder.price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((StoreActivity) context).buyProgram(program.getId());
            }
        });

        holder.type.setText(program.getType().toUpperCase());
        holder.ex_count.setText(program.getCount() + context.getString(R.string.work_adapter_str));
    }

    @Override
    public int getItemCount() {
        return collection.size();
    }

    private class NewsViewHolder extends RecyclerView.ViewHolder {
        private TextView user_name;
        private ImageView user_avatar;
        private TextView post_desc;
        private ImageView post_photo;

        private TextView like_count;
        private TextView date;
        private TextView text_count;

        private TextView name;

        private TextView price;

        private TextView type;
        private TextView ex_count;

        public NewsViewHolder(final View itemView) {
            super(itemView);

            user_name = (TextView) itemView.findViewById(R.id.textViewfdfd);
            name = (TextView) itemView.findViewById(R.id.textView);
            user_avatar = (ImageView) itemView.findViewById(R.id.imageView);
            post_desc = (TextView) itemView.findViewById(R.id.textView2);
            date = (TextView) itemView.findViewById(R.id.textView5);
            post_photo = (ImageView) itemView.findViewById(R.id.imageView2);
            like_count = (TextView) itemView.findViewById(R.id.textView3);
            text_count = (TextView) itemView.findViewById(R.id.textView4);

            price = (TextView) itemView.findViewById(R.id.textView6);
            type = (TextView) itemView.findViewById(R.id.textView7);
            ex_count = (TextView) itemView.findViewById(R.id.textView8);
        }
    }
}