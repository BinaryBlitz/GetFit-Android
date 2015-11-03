package binaryblitz.athleteapp.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import binaryblitz.athleteapp.Activities.PostActivity;
import binaryblitz.athleteapp.Activities.ProgramActivity;
import binaryblitz.athleteapp.Data.FITTIProgram;
import binaryblitz.athleteapp.R;

public class StoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity context;
    DisplayImageOptions options;

    private ArrayList<FITTIProgram> collection;

    public StoreAdapter(Activity context) {
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

        collection.add(new FITTIProgram("1", "Intensive training program for your body", "photo", R.drawable.test2, "65", "Cardio", 5,
                "Who has taken an extended leave of absence from training?",
                "40", 159, 5, "Mike Silvestri", "1"));
        collection.get(0).setUserPhotoResId(R.drawable.test9);
        collection.add(new FITTIProgram("1", "Intensive training program for your body", "photo", R.drawable.test3, "65", "Cardio", 5,
                "Who has taken an extended leave of absence from training?",
                "40", 159, 5, "Mike Silvestri", "1"));
        collection.get(1).setUserPhotoResId(R.drawable.test10);
        collection.add(new FITTIProgram("1", "Intensive training program for your body", "photo", R.drawable.test4, "65", "Cardio", 5,
                "Who has taken an extended leave of absence from training?",
                "40", 159, 5, "Mike Silvestri", "1"));
        collection.get(2).setUserPhotoResId(R.drawable.test9);
        collection.add(new FITTIProgram("1", "Intensive training program for your body", "photo", R.drawable.test5, "65", "Cardio", 5,
                "Who has taken an extended leave of absence from training?",
                "40", 159, 5, "Mike Silvestri", "1"));
        collection.get(3).setUserPhotoResId(R.drawable.test10);
        collection.add(new FITTIProgram("1", "Intensive training program for your body", "photo", R.drawable.test6, "65", "Cardio", 5,
                "Who has taken an extended leave of absence from training?",
                "40", 159, 5, "Mike Silvestri", "1"));
        collection.get(4).setUserPhotoResId(R.drawable.test10);
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProgramActivity.class);
                context.startActivity(intent);
            }
        });

        final FITTIProgram program = collection.get(position);

        holder.user_name.setText(program.getTrainerName());
        holder.name.setText(program.getName());
        holder.post_desc.setText(program.getDesc());
        holder.like_count.setText(Double.toString(program.getStarCount()));
        holder.text_count.setText(Integer.toString(program.getUserCount()));

        holder.user_avatar.setImageResource(program.getUserPhotoResId());

        if(program.getPhoto_url() == null || program.getPhoto_url().equals("No photo")) {
            holder.post_photo.setVisibility(View.GONE);
        } else {
            holder.post_photo.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage("drawable://" + program.getPhotoResId(), holder.post_photo, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    ImageView imageView = (ImageView) view;
                    FadeInBitmapDisplayer.animate(imageView, 500);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
        }

        holder.date.setText(program.getTime() + " MIN");

        holder.price.setText("$" + program.getPrice());
        holder.type.setText(program.getType() + ", ");
        holder.ex_count.setText(program.getCount() + " exercises");
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