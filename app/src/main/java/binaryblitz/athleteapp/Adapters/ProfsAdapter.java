package binaryblitz.athleteapp.Adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
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
import binaryblitz.athleteapp.Custom.ProgressDialog;
import binaryblitz.athleteapp.Custom.RaitingDialog;
import binaryblitz.athleteapp.Data.Professional;
import binaryblitz.athleteapp.Data.ProfessionalType;
import binaryblitz.athleteapp.Data.SubscriptionsSet;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.GetFitServerRequest;
import binaryblitz.athleteapp.Server.OnRequestPerformedListener;

public class ProfsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity context;

    private ArrayList<Professional> collection;

    public ProfsAdapter(Activity context) {
        this.context = context;
        collection = new ArrayList<>();
    }

    public void setCollection(ArrayList<Professional> collection) {
        this.collection = collection;
    }

    public void setContext(Activity context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.prof_card, parent, false);

        return new NewsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        final NewsViewHolder holder = (NewsViewHolder) viewHolder;
        final Professional prof = collection.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfProfileActivity.class);
                intent.putExtra("id", prof.getId());
                context.startActivity(intent);
            }
        });

        holder.user_name.setText(prof.getName());
        holder.post_desc.setText(prof.getDesc());
        holder.like_count.setText(new DecimalFormat("#.#", DecimalFormatSymbols.getInstance(Locale.US)).format(prof.getStarCount()));
        holder.text_count.setText(Integer.toString(prof.getUserCount()));

        Picasso.with(context).load(prof.getUserPhotoUrl()).into(holder.user_avatar);

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

                if(SubscriptionsSet.load().get(prof.getId()) == null) {
                    new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.title_str))
                            .setMessage(context.getString(R.string.rating_error_str))
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        JSONObject toSend = new JSONObject();

                        try {
                            toSend.accumulate("rating", object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(prof.getRatingId() == null) {
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
                                                prof.setStarCount(((JSONObject) objects[0]).getDouble("rating"));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            holder.like_count.setText(new DecimalFormat("#.#", DecimalFormatSymbols.getInstance(Locale.US)).format(prof.getStarCount()));
                                        }
                                    })
                                    .rateTrainer(prof.getId())
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
                                                prof.setStarCount(((JSONObject) objects[0]).getDouble("rating"));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            holder.like_count.setText(new DecimalFormat("#.#", DecimalFormatSymbols.getInstance(Locale.US)).format(prof.getStarCount()));
                                        }
                                    })
                                    .patchRating(prof.getRatingId())
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

                if(SubscriptionsSet.load().get(prof.getId()) == null) {
                    new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.title_str))
                            .setMessage(context.getString(R.string.rating_error_str))
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        JSONObject toSend = new JSONObject();

                        try {
                            toSend.accumulate("rating", object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(prof.getRatingId() == null) {
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
                                                prof.setStarCount(((JSONObject) objects[0]).getDouble("rating"));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            holder.like_count.setText(new DecimalFormat("#.#", DecimalFormatSymbols.getInstance(Locale.US)).format(prof.getStarCount()));
                                        }
                                    })
                                    .rateTrainer(prof.getId())
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
                                                prof.setStarCount(((JSONObject) objects[0]).getDouble("rating"));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            holder.like_count.setText(new DecimalFormat("#.#", DecimalFormatSymbols.getInstance(Locale.US)).format(prof.getStarCount()));
                                        }
                                    })
                                    .patchRating(prof.getRatingId())
                                    .perform();
                        }
                    }
                });

                dialog.show(context.getFragmentManager(), "rating");
            }
        });

        if(prof.getPhotoUrl() == null || prof.getPhotoUrl().equals("No photo")) {
            holder.post_photo.setVisibility(View.GONE);
        } else {
            holder.post_photo.setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(prof.getPhotoUrl())
                    .into(holder.post_photo);
        }

        holder.date.setText(prof.getProgramCount() + context.getString(R.string.programs_adapter_str));

        if(prof.isFollowing()) {
            holder.vBgLike.setBackgroundResource(R.drawable.blue_btn);
            holder.ivLike.setImageResource(R.drawable.done_ic);
            ((TextView) holder.itemView.findViewById(R.id.textView6)).setTextColor(Color.WHITE);
        } else {
            holder.vBgLike.setBackgroundResource(R.drawable.blue_border);
            holder.ivLike.setImageResource(R.drawable.plus_ic);
            ((TextView) holder.itemView.findViewById(R.id.textView6)).setTextColor(Color.parseColor("#3695ed"));
        }

        holder.vBgLike.setOnClickListener(new View.OnClickListener() {
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
                final ProgressDialog dialog = new ProgressDialog();
                dialog.show(context.getFragmentManager(), "atheleteapp");

                if(!prof.isFollowing()) {
                    GetFitServerRequest.with(context)
                            .authorize()
                            .listener(new OnRequestPerformedListener() {
                                @Override
                                public void onRequestPerformedListener(Object... objects) {
                                    dialog.dismiss();
                                    if (objects[0].equals("Internet")) {
                                        ((BaseActivity) context).cancelRequest();
                                        return;
                                    }
                                    try {
                                        prof.setFollowId(((JSONObject) objects[0]).getString("id"));
                                        prof.setFollowing(true);
                                    } catch (Exception ignored) {}

                                    prof.setUserCount(prof.getUserCount() + 1);
                                    holder.text_count.setText(Integer.toString(prof.getUserCount()));
                                    holder.vBgLike.setBackgroundResource(R.drawable.blue_btn);
                                    holder.ivLike.setImageResource(R.drawable.done_ic);
                                    ((TextView) holder.itemView.findViewById(R.id.textView6)).setTextColor(Color.WHITE);
                                }
                            })
                            .follow(prof.getId())
                            .perform();
                } else {

                    GetFitServerRequest.with(context)
                            .authorize()
                            .listener(new OnRequestPerformedListener() {
                                @Override
                                public void onRequestPerformedListener(Object... objects) {
                                    dialog.dismiss();
                                    if (objects[0].equals("Internet")) {
                                        ((BaseActivity) context).cancelRequest();
                                        return;
                                    }
                                    prof.setUserCount(prof.getUserCount() - 1);
                                    prof.setFollowId(null);
                                    prof.setFollowing(false);
                                    holder.text_count.setText(Integer.toString(prof.getUserCount()));
                                    holder.vBgLike.setBackgroundResource(R.drawable.blue_border);
                                    holder.ivLike.setImageResource(R.drawable.plus_ic);
                                    ((TextView) holder.itemView.findViewById(R.id.textView6)).setTextColor(Color.parseColor("#3695ed"));
                                }
                            })
                            .unfollow(prof.getFollowId())
                            .perform();
                }

                prof.setFollowing(!prof.isFollowing());
            }
        });
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

        View vBgLike;
        ImageView ivLike;

        public NewsViewHolder(final View itemView) {
            super(itemView);

            user_name = (TextView) itemView.findViewById(R.id.textView);
            user_avatar = (ImageView) itemView.findViewById(R.id.profile_image);
            post_desc = (TextView) itemView.findViewById(R.id.textView2);
            date = (TextView) itemView.findViewById(R.id.textView5);
            post_photo = (ImageView) itemView.findViewById(R.id.imageView2);
            like_count = (TextView) itemView.findViewById(R.id.textView3);
            text_count = (TextView) itemView.findViewById(R.id.textView4);

            vBgLike = itemView.findViewById(R.id.follow_btn);
            ivLike = (ImageView) itemView.findViewById(R.id.imageView6);
        }
    }
}
