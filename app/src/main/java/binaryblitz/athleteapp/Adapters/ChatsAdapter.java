package binaryblitz.athleteapp.Adapters;

import android.app.Activity;
import android.graphics.Bitmap;
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

import binaryblitz.athleteapp.Data.FITTIChat;
import binaryblitz.athleteapp.R;

public class ChatsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity context;
    DisplayImageOptions options;

    private ArrayList<FITTIChat> collection;

    public ChatsAdapter(Activity context) {
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

        collection.add(new FITTIChat(2, "Mike Silvestri", "14:02", "How your training?", R.drawable.test9));
        collection.add(new FITTIChat(0, "Henry Harrison", "14:02", "How your training?", R.drawable.test10));
        collection.add(new FITTIChat(0, "Efanov Evgeniy", "18:08", "I cant start training. So many parties.", R.drawable.evgen));
        collection.add(new FITTIChat(1, "Jack Sparrow", "14:02", "Capitan!!! Capitan Jack Sparrow!!!", R.drawable.sparrow));

    }

    public void setContext(Activity context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.chat_card, parent, false);

        return new NewsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        final NewsViewHolder holder = (NewsViewHolder) viewHolder;

        FITTIChat chat = collection.get(position);

        holder.user_name.setText(chat.getName());

        if(chat.getUnRead() == 0) {
            holder.text_count.setVisibility(View.GONE);
        } else {
            holder.text_count.setVisibility(View.VISIBLE);
            holder.text_count.setText(Integer.toString(chat.getUnRead()));
        }
        holder.date.setText(chat.getTime());
        holder.post_desc.setText(chat.getLast());
        holder.user_avatar.setImageResource(chat.getAvatar());
    }

    @Override
    public int getItemCount() {
        return collection.size();
    }

    private class NewsViewHolder extends RecyclerView.ViewHolder {
        private TextView user_name;
        private ImageView user_avatar;
        private TextView post_desc;

        private TextView date;
        private TextView text_count;

        public NewsViewHolder(final View itemView) {
            super(itemView);

            user_name = (TextView) itemView.findViewById(R.id.name_text);
            user_avatar = (ImageView) itemView.findViewById(R.id.profile_image);
            post_desc = (TextView) itemView.findViewById(R.id.age_text);
            date = (TextView) itemView.findViewById(R.id.time_text);
            text_count = (TextView) itemView.findViewById(R.id.unread);
        }
    }
}