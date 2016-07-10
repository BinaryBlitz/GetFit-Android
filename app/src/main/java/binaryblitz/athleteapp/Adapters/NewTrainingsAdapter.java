package binaryblitz.athleteapp.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import binaryblitz.athleteapp.Activities.NewTrainingActivity;
import binaryblitz.athleteapp.Activities.ProgramActivity;
import binaryblitz.athleteapp.Data.Training;
import binaryblitz.athleteapp.Data.TrainingPart;
import binaryblitz.athleteapp.R;

public class NewTrainingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity context;

    private ArrayList<Training> trainings;

    public NewTrainingsAdapter(Activity context) {
        this.context = context;

        trainings = new ArrayList<>();
    }

    public void setContext(Activity context) {
        this.context = context;
    }

    public ArrayList<Training> getTrainings() {
        return trainings;
    }

    public void setTrainings(ArrayList<Training> trainings) {
        this.trainings = trainings;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.new_training_card, parent, false);

        return new NewsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        final NewsViewHolder holder = (NewsViewHolder) viewHolder;


        final Training training = trainings.get(position);

        holder.name.setText(training.getName());
        holder.type.setText(training.getProgramName());
        holder.count.setText(training.getExCount() + " " + context.getString(R.string.exsercises_string));
        holder.time.setText(training.getTime() + " " + context.getString(R.string.min_string));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewTrainingActivity.setTraining(training);
                ((NewTrainingActivity) context).showDialog();
            }
        });

        holder.itemView.findViewById(R.id.imageView14).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProgramActivity.class);
                intent.putExtra("id", training.getProgramId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return trainings.size();
    }

    private class NewsViewHolder extends RecyclerView.ViewHolder {
        public FrameLayout mContainer;
        public TextView name;
        public TextView type;
        public TextView count;
        public TextView time;

        public NewsViewHolder(View v) {
            super(v);
            mContainer = (FrameLayout) v.findViewById(R.id.container);
            name = (TextView) v.findViewById(R.id.textView9);
            type = (TextView) v.findViewById(R.id.textView7);
            count = (TextView) v.findViewById(R.id.textView8);
            time = (TextView) v.findViewById(R.id.textView5);
        }
    }
}