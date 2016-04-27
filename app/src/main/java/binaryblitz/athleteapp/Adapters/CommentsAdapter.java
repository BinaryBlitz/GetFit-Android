package binaryblitz.athleteapp.Adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import binaryblitz.athleteapp.Data.Chat;
import binaryblitz.athleteapp.R;

public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity context;

    private ArrayList<Chat> collection;

    public CommentsAdapter(Activity context) {
        this.context = context;
        collection = new ArrayList<>();
//        collection.add(new Chat(2, "Mike Silvestri", "14:02", "How your training?", R.drawable.test9));
//        collection.add(new Chat(0, "Henry Harrison", "14:02", "How your training?", R.drawable.test10));
//        collection.add(new Chat(0, "Efanov Evgeniy", "18:08", "I cant start training. So many parties.", R.drawable.evgen));
//        collection.add(new Chat(1, "Jack Sparrow", "14:02", "Capitan!!! Capitan Jack Sparrow!!!", R.drawable.sparrow));

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

        Chat chat = collection.get(position);

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