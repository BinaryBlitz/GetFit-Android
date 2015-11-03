package binaryblitz.athleteapp.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
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
import binaryblitz.athleteapp.Data.FITTIComment;
import binaryblitz.athleteapp.Data.FITTIPost;
import binaryblitz.athleteapp.R;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity context;
    DisplayImageOptions options;

    private ArrayList<FITTIPost> news;

    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();

    private static final long DOUBLE_PRESS_INTERVAL = 250; // in millis
    private long lastPressTime;
    boolean mHasDoubleClicked;

    public NewsAdapter(Activity context) {
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

        news = new ArrayList<>();

        news.add(new FITTIPost("1", "Henry Harrison", "1", null,
                R.drawable.test10, "Who has taken an extended leave of absence from training?",
                "No photo", R.drawable.test2, "TODAY", 43, new ArrayList<FITTIComment>()));

        news.add(new FITTIPost("1", "Henry Harrison", "1", null,
                R.drawable.test10, "Who has taken an extended leave of absence from training?",
                "Photo", R.drawable.test3, "TODAY", 43, new ArrayList<FITTIComment>()));

        news.add(new FITTIPost("1", "Henry Harrison", "1", null,
                R.drawable.test10, "Who has taken an extended leave of absence from training?",
                "No photo", R.drawable.test4, "TODAY", 43, new ArrayList<FITTIComment>()));

        news.add(new FITTIPost("1", "Henry Harrison", "1", null,
                R.drawable.test10, "Who has taken an extended leave of absence from training?",
                "Photo", R.drawable.test5, "TODAY", 43, new ArrayList<FITTIComment>()));

        news.add(new FITTIPost("1", "Henry Harrison", "1", null,
                R.drawable.test10, "Who has taken an extended leave of absence from training?",
                "No photo", R.drawable.test6, "TODAY", 43, new ArrayList<FITTIComment>()));

        news.add(new FITTIPost("1", "Henry Harrison", "1", null,
                R.drawable.test10, "Who has taken an extended leave of absence from training?",
                "No photo", R.drawable.test3, "TODAY", 43, new ArrayList<FITTIComment>()));

        news.add(new FITTIPost("1", "Henry Harrison", "1", null,
                R.drawable.test10, "Who has taken an extended leave of absence from training?",
                "Photo", R.drawable.test6, "TODAY", 43, new ArrayList<FITTIComment>()));

        news.add(new FITTIPost("1", "Henry Harrison", "1", null,
                R.drawable.test10, "Who has taken an extended leave of absence from training?",
                "Photo", R.drawable.test3, "TODAY", 43, new ArrayList<FITTIComment>()));

        news.add(new FITTIPost("1", "Henry Harrison", "1", null,
                R.drawable.test10, "Who has taken an extended leave of absence from training?",
                "No photo", R.drawable.test3, "TODAY", 43, new ArrayList<FITTIComment>()));

    }

    public void setContext(Activity context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.news_card, parent, false);

        return new NewsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        final NewsViewHolder holder = (NewsViewHolder) viewHolder;

        final FITTIPost post = news.get(position);

        holder.user_name.setText(post.getUserName());
        holder.post_desc.setText(post.getDesc());
        holder.like_count.setText(Integer.toString(post.getLikeCount()));
        holder.text_count.setText(Integer.toString(post.getComments().size()));

        holder.user_avatar.setImageResource(post.getUserPhotoResId());

        if(post.getPhotoUrl() == null || post.getPhotoUrl().equals("No photo")) {
            holder.post_photo.setVisibility(View.GONE);
        } else {
            holder.post_photo.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage("drawable://" + post.getPhotoResId(), holder.post_photo, new ImageLoadingListener() {
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
            //holder.post_photo.setImageResource(post.getPhotoResId());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long pressTime = System.currentTimeMillis();
                // If double click...
                if (pressTime - lastPressTime <= DOUBLE_PRESS_INTERVAL) {
                    mHasDoubleClicked = true;
                    if (!post.isLiked()) {
                        animatePhotoLike(holder);
                        holder.like_image.setImageResource(R.drawable.like_filled);
                        post.setLiked(true);
                    }
                } else { // If not double click....
                    mHasDoubleClicked = false;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!mHasDoubleClicked) {
                                Intent intent = new Intent(context, PostActivity.class);
                                context.startActivity(intent);
                            }
                        }
                    }, DOUBLE_PRESS_INTERVAL);
                }
                lastPressTime = pressTime;
            }
        });

//        holder.post_photo.setImageResource(post.getPhotoResId());

        holder.date.setText(post.getDate());

        if(post.isLiked()) {
            holder.like_image.setImageResource(R.drawable.like_filled);
        } else {
            holder.like_image.setImageResource(R.drawable.like_active_ic);
        }

        holder.like_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(post.isLiked()) {
                    holder.like_image.setImageResource(R.drawable.like_active_ic);
                } else {
                    holder.like_image.setImageResource(R.drawable.like_filled);
                }

                post.setLiked(!post.isLiked());
            }
        });
    }

    private boolean findDoubleClick() {
        // Get current time in nano seconds.
        long pressTime = System.currentTimeMillis();
        // If double click...
        if (pressTime - lastPressTime <= DOUBLE_PRESS_INTERVAL) {
            mHasDoubleClicked = true;

        } else { // If not double click....

        }
        lastPressTime = pressTime;
        return mHasDoubleClicked;
    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    private void animatePhotoLike(final NewsViewHolder holder) {
        holder.vBgLike.setVisibility(View.VISIBLE);
        holder.ivLike.setVisibility(View.VISIBLE);

        holder.vBgLike.setScaleY(0.1f);
        holder.vBgLike.setScaleX(0.1f);
        holder.vBgLike.setAlpha(1f);
        holder.ivLike.setScaleY(0.1f);
        holder.ivLike.setScaleX(0.1f);

        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator bgScaleYAnim = ObjectAnimator.ofFloat(holder.vBgLike, "scaleY", 0.1f, 1f);
        bgScaleYAnim.setDuration(200);
        bgScaleYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
        ObjectAnimator bgScaleXAnim = ObjectAnimator.ofFloat(holder.vBgLike, "scaleX", 0.1f, 1f);
        bgScaleXAnim.setDuration(200);
        bgScaleXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
        ObjectAnimator bgAlphaAnim = ObjectAnimator.ofFloat(holder.vBgLike, "alpha", 1f, 0f);
        bgAlphaAnim.setDuration(200);
        bgAlphaAnim.setStartDelay(150);
        bgAlphaAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

        ObjectAnimator imgScaleUpYAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleY", 0.1f, 1f);
        imgScaleUpYAnim.setDuration(300);
        imgScaleUpYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
        ObjectAnimator imgScaleUpXAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleX", 0.1f, 1f);
        imgScaleUpXAnim.setDuration(300);
        imgScaleUpXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

        ObjectAnimator imgScaleDownYAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleY", 1f, 0f);
        imgScaleDownYAnim.setDuration(300);
        imgScaleDownYAnim.setInterpolator(ACCELERATE_INTERPOLATOR);
        ObjectAnimator imgScaleDownXAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleX", 1f, 0f);
        imgScaleDownXAnim.setDuration(300);
        imgScaleDownXAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        animatorSet.playTogether(bgScaleYAnim, bgScaleXAnim, bgAlphaAnim, imgScaleUpYAnim, imgScaleUpXAnim);
        animatorSet.play(imgScaleDownYAnim).with(imgScaleDownXAnim).after(imgScaleUpYAnim);
        animatorSet.start();
    }

    private class NewsViewHolder extends RecyclerView.ViewHolder {
        private TextView user_name;
        private ImageView user_avatar;
        private TextView post_desc;
        private ImageView post_photo;

        private TextView like_count;
        private TextView date;
        private TextView text_count;

        private ImageView like_image;

        View vBgLike;
        ImageView ivLike;

        public NewsViewHolder(final View itemView) {
            super(itemView);

            user_name = (TextView) itemView.findViewById(R.id.textView);
            user_avatar = (ImageView) itemView.findViewById(R.id.imageView);
            post_desc = (TextView) itemView.findViewById(R.id.textView2);
            date = (TextView) itemView.findViewById(R.id.textView5);
            post_photo = (ImageView) itemView.findViewById(R.id.imageView2);
            like_count = (TextView) itemView.findViewById(R.id.textView3);
            like_image = (ImageView) itemView.findViewById(R.id.imageView3);
            text_count = (TextView) itemView.findViewById(R.id.textView4);

            vBgLike = itemView.findViewById(R.id.vBgLike);
            ivLike = (ImageView) itemView.findViewById(R.id.ivLike);
        }
    }
}
