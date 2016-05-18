package binaryblitz.athleteapp.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

import binaryblitz.athleteapp.Activities.PhotoActivity;
import binaryblitz.athleteapp.Data.Message;
import binaryblitz.athleteapp.R;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity context;
    ArrayList<Message> collection = new ArrayList<>();

    public ChatAdapter(Activity context) {
        this.context = context;
        collection = new ArrayList<>();
    }

    public void setContext(Activity context) {
        this.context = context;
    }

    public void setCollection(ArrayList<Message> collection) {
        this.collection = collection;
    }

    public void clear() {
        collection.clear();
        notifyDataSetChanged();
    }

    public void add(Message message) {
        if(message.getId().equals("-1")) {
            for (int i = 0; i < collection.size(); i++) {
                if(collection.get(i).getId().equals("-1")) {
                    return;
                }
            }
        }
        collection.add(message);
        notifyItemInserted(collection.size());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 0) {
            final View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.me_message_card, parent, false);
            return new NewsViewHolder(itemView);
        } else {
            final View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.message_card, parent, false);
            return new NewsViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        final NewsViewHolder holder = (NewsViewHolder) viewHolder;
        final Message message = collection.get(position);

        if(message.getPhotoUrl() == null) {
            holder.image.setVisibility(View.GONE);
            holder.text.setVisibility(View.VISIBLE);

            holder.text.setText(message.getText());
        } else {
            holder.image.setVisibility(View.VISIBLE);
            holder.text.setVisibility(View.GONE);

            Picasso.with(context)
                    .load(message.getPhotoThumb())
                    .into(holder.image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(context)
                                    .load(message.getPhotoThumb())
                                    .into(holder.image, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError() {
                                            holder.image.setImageBitmap(null);
                                        }
                                    });
                        }
                    });

            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PhotoActivity.url = message.getPhotoUrl();
                    Intent intent = new Intent(context, PhotoActivity.class);
                    context.startActivity(intent);
                }
            });
        }

        holder.time.setText(message.getDate());
    }

    @Override
    public int getItemViewType(int position) {
        return collection.get(position).isMyMessage() ? 0 : 1;
    }

    @Override
    public int getItemCount() {
        return collection.size();
    }

    private class NewsViewHolder extends RecyclerView.ViewHolder {
        private TextView text;
        private ImageView image;
        private TextView time;

        public NewsViewHolder(final View itemView) {
            super(itemView);
            time = (TextView) itemView.findViewById(R.id.textView32);
            text = (TextView) itemView.findViewById(R.id.textView31);
            image = (ImageView) itemView.findViewById(R.id.imageView17);
        }
    }
}