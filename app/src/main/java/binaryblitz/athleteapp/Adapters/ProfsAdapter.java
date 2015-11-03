package binaryblitz.athleteapp.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.ArrayList;

import binaryblitz.athleteapp.Activities.CalendarActivity;
import binaryblitz.athleteapp.Activities.ProfProfileActivity;
import binaryblitz.athleteapp.Data.FITTIComment;
import binaryblitz.athleteapp.Data.FITTIPost;
import binaryblitz.athleteapp.Data.FITTIProfessional;
import binaryblitz.athleteapp.Data.FITTIProfessionalType;
import binaryblitz.athleteapp.R;

public class ProfsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity context;
    DisplayImageOptions options;

    private ArrayList<FITTIProfessional> collection;

    public ProfsAdapter(Activity context) {
        this.context = context;
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());

        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)
                .build();

        collection = new ArrayList<>();

    }

    public void setCollection(final int position) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(position == 0) {
                    collection.add(new FITTIProfessional("1", "photo", null, R.drawable.test4, R.drawable.test9,
                            "Mike Silvestri", "Cross-fit coach. Your faithful assistant to athletic body in short time.",
                            FITTIProfessionalType.COACH, false, 8, 32, 4.7));
                    notifyItemInserted(collection.size());
                    collection.add(new FITTIProfessional("1", "photo", null, R.drawable.test5, R.drawable.test10,
                            "Henry Harrison", "Professional marathoner. The best choice to improve your stamina and get fit.",
                            FITTIProfessionalType.COACH, false, 12, 68, 4.5));
                    notifyItemInserted(collection.size());
                    collection.add(new FITTIProfessional("1", "photo", null, R.drawable.test6, R.drawable.tina,
                            "Tina Kandelaki", "Fitness fan. Get the training celebrity uses, with all the features eventually developed.",
                            FITTIProfessionalType.COACH, false, 5, 188, 4.9));
                    notifyItemInserted(collection.size());
                } else if(position == 1) {
                    collection.add(new FITTIProfessional("1", "photo", null, R.drawable.sports_medicine, R.drawable.arkov,
                            "Vladimir Arkov", "Ph.D in Medical Sciences, Head of Physiotherapy Department in Moscow Sports Medicine Clinic",
                            FITTIProfessionalType.DOCTOR, false, 6, 224, 4.9));
                    notifyItemInserted(collection.size());
                }
            }
        }, 100);
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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfProfileActivity.class);
                context.startActivity(intent);
            }
        });

        final FITTIProfessional prof = collection.get(position);

        holder.user_name.setText(prof.getName());
        holder.post_desc.setText(prof.getDesc());
        holder.like_count.setText(Double.toString(prof.getStarCount()));
        holder.text_count.setText(Integer.toString(prof.getUserCount()));

        holder.user_avatar.setImageResource(prof.getDebug_userPhotoResId());

        if(prof.getPhotoUrl() == null || prof.getPhotoUrl().equals("No photo")) {
            holder.post_photo.setVisibility(View.GONE);
        } else {
            holder.post_photo.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage("drawable://" + prof.getPhotoResId(), holder.post_photo);
           // holder.post_photo.setImageResource(prof.getPhotoResId());
        }

        holder.date.setText(prof.getProgramCount() + " PROGRAMS");

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
                if(!prof.isFollowing()) {
                    holder.vBgLike.setBackgroundResource(R.drawable.blue_btn);
                    holder.ivLike.setImageResource(R.drawable.done_ic);
                    ((TextView) holder.itemView.findViewById(R.id.textView6)).setTextColor(Color.WHITE);
                } else {
                    holder.vBgLike.setBackgroundResource(R.drawable.blue_border);
                    holder.ivLike.setImageResource(R.drawable.plus_ic);
                    ((TextView) holder.itemView.findViewById(R.id.textView6)).setTextColor(Color.parseColor("#3695ed"));
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
