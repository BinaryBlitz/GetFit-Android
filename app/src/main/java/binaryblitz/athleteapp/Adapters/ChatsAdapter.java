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

import java.util.ArrayList;

import binaryblitz.athleteapp.Activities.ChatActivity;
import binaryblitz.athleteapp.Activities.ProgramActivity;
import binaryblitz.athleteapp.Data.Chat;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Utils.DateUtils;

public class ChatsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity context;

    private ArrayList<Chat> collection;

    public void setCollection(ArrayList<Chat> collection) {
        this.collection = collection;
    }

    public ChatsAdapter(Activity context) {
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
                inflate(R.layout.chat_card, parent, false);

        return new NewsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        final NewsViewHolder holder = (NewsViewHolder) viewHolder;

        final Chat chat = collection.get(position);

        holder.user_name.setText(chat.getName());

        if(chat.getUnRead() == 0) {
            holder.text_count.setVisibility(View.GONE);
        } else {
            holder.text_count.setVisibility(View.VISIBLE);
            holder.text_count.setText(Integer.toString(chat.getUnRead()));
        }
        holder.date.setText(DateUtils.getDateStringRepresentationForMessager(chat.getTime()));
        holder.post_desc.setText(chat.getLast());

        if(chat.getLast().equals("Image")) {
            holder.post_desc.setTextColor(Color.argb(255, 54,149,237));
        } else {
            holder.post_desc.setTextColor(Color.parseColor("#212121"));
        }

        holder.user_avatar.setImageResource(chat.getAvatar());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("id", chat.getSubscriptionId());
                context.startActivity(intent);
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