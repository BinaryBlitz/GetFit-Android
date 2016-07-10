package binaryblitz.athleteapp.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import binaryblitz.athleteapp.Abstract.BaseActivity;
import binaryblitz.athleteapp.Activities.MyProfileActivity;
import binaryblitz.athleteapp.Activities.PostActivity;
import binaryblitz.athleteapp.Activities.ProfileActivity;
import binaryblitz.athleteapp.Custom.ProgressDialog;
import binaryblitz.athleteapp.Data.Comment;
import binaryblitz.athleteapp.Data.User;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.DeviceInfoStore;
import binaryblitz.athleteapp.Server.GetFitServerRequest;
import binaryblitz.athleteapp.Server.OnRequestPerformedListener;
import binaryblitz.athleteapp.Utils.DateUtils;

public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity context;

    private ArrayList<Comment> collection;

    public CommentsAdapter(Activity context) {
        this.context = context;
        collection = new ArrayList<>();
    }

    public void add(Comment comment) {
        collection.add(comment);
        notifyItemInserted(collection.size() - 1);
    }

    public void setCollection(ArrayList<Comment> collection) {
        this.collection = collection;
    }

    public void setContext(Activity context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.comment_card, parent, false);

        return new NewsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        final NewsViewHolder holder = (NewsViewHolder) viewHolder;

        final Comment comment = collection.get(position);
        holder.itemView.findViewById(R.id.delete_btn).setVisibility(View.GONE);
        holder.user_name.setText(comment.getUserName());

        holder.date.setText(DateUtils.getDateStringRepresentation(comment.getDate()));
        holder.post_desc.setText(comment.getText());

        holder.itemView.findViewById(R.id.user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(comment.getUserId().equals(User.fromString(DeviceInfoStore.getUser()).getId())) {
                    Intent intent = new Intent(context, MyProfileActivity.class);
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("id", comment.getUserId());
                    context.startActivity(intent);
                }
            }
        });

        Picasso.with(context).load(comment.getAvatarUrl()).into(holder.user_avatar);

        try {
            if(comment.getUserId().equals(User.fromString(DeviceInfoStore.getUser()).getId())) {
                holder.itemView.findViewById(R.id.delete_btn).setVisibility(View.VISIBLE);
            } else {
                holder.itemView.findViewById(R.id.delete_btn).setVisibility(View.GONE);
            }
        } catch (Exception e) {}

        holder.itemView.findViewById(R.id.delete_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog dialog = new ProgressDialog();

                dialog.show(context.getFragmentManager(), "atheletapp");

                GetFitServerRequest.with(context)
                        .authorize()
                        .listener(new OnRequestPerformedListener() {
                            @Override
                            public void onRequestPerformedListener(Object... objects) {
                                if (objects[0].equals("Internet")) {
                                    ((BaseActivity) context).cancelRequest();
                                    return;
                                }
                                dialog.dismiss();
                                collection.remove(position);
                                ((PostActivity) context).deleteComment();
                                notifyItemRemoved(position);
                            }
                        })
                        .deleteComment(comment.getId())
                        .perform();
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

        public NewsViewHolder(final View itemView) {
            super(itemView);

            user_name = (TextView) itemView.findViewById(R.id.user_text);
            user_avatar = (ImageView) itemView.findViewById(R.id.avatar);
            post_desc = (TextView) itemView.findViewById(R.id.comment_text);
            date = (TextView) itemView.findViewById(R.id.date_text);
        }
    }
}