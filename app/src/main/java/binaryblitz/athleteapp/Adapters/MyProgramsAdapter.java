package binaryblitz.athleteapp.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import binaryblitz.athleteapp.Activities.ProgramActivity;
import binaryblitz.athleteapp.Data.Program;
import binaryblitz.athleteapp.R;

public class MyProgramsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity context;

    private ArrayList<Program> collection;

    public MyProgramsAdapter(Activity context) {
        this.context = context;

        collection = new ArrayList<>();
//
//        collection.add(new Program("1", "Intensive training program for your body", "photo", R.drawable.test2, "65", "Cardio", 5,
//                "Who has taken an extended leave of absence from training?",
//                "40", 159, 5, "Mike Silvestri", "1"));
//        collection.get(0).setUserPhotoResId(R.drawable.test10);
//        collection.add(new Program("1", "Intensive training program for your body", "photo", R.drawable.test3, "65", "Cardio", 5,
//                "Who has taken an extended leave of absence from training?",
//                "40", 159, 5, "Mike Silvestri", "1"));
//        collection.get(1).setUserPhotoResId(R.drawable.test10);
//        collection.add(new Program("1", "Intensive training program for your body", "photo", R.drawable.test4, "65", "Cardio", 5,
//                "Who has taken an extended leave of absence from training?",
//                "40", 159, 5, "Mike Silvestri", "1"));
//        collection.get(2).setUserPhotoResId(R.drawable.test10);
//        collection.add(new Program("1", "Intensive training program for your body", "photo", R.drawable.test5, "65", "Cardio", 5,
//                "Who has taken an extended leave of absence from training?",
//                "40", 159, 5, "Mike Silvestri", "1"));
//        collection.get(3).setUserPhotoResId(R.drawable.test10);
//        collection.add(new Program("1", "Intensive training program for your body", "photo", R.drawable.test6, "65", "Cardio", 5,
//                "Who has taken an extended leave of absence from training?",
//                "40", 159, 5, "Mike Silvestri", "1"));
//        collection.get(4).setUserPhotoResId(R.drawable.test10);
    }

    public void setContext(Activity context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.my_program_card, parent, false);

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

        final Program program = collection.get(position);

        holder.user_name.setText(program.getTrainerName());
        holder.name.setText(program.getName());
        holder.post_desc.setText(program.getDesc());
        holder.like_count.setText(Double.toString(program.getStarCount()));
        holder.text_count.setText(Integer.toString(program.getUserCount()));

        if(program.getPhotoUrl() == null || program.getPhotoUrl().equals("No photo")) {
            holder.post_photo.setVisibility(View.GONE);
        } else {
            holder.post_photo.setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(program.getPhotoUrl())
                    .into(holder.post_photo);
        }

        holder.date.setText(program.getTime() + " MIN");

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

            type = (TextView) itemView.findViewById(R.id.textView7);
            ex_count = (TextView) itemView.findViewById(R.id.textView8);
        }
    }
}