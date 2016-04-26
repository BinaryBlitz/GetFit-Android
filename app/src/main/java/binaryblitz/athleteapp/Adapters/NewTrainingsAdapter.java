package binaryblitz.athleteapp.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.ArrayList;
import java.util.Date;

import binaryblitz.athleteapp.Activities.NewTrainingActivity;
import binaryblitz.athleteapp.Activities.NewTrainingDetailsActivity;
import binaryblitz.athleteapp.Data.Professional;
import binaryblitz.athleteapp.Data.ProfessionalType;
import binaryblitz.athleteapp.Data.Training;
import binaryblitz.athleteapp.Data.TrainingPart;
import binaryblitz.athleteapp.R;

public class NewTrainingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity context;
    DisplayImageOptions options;

    private ArrayList<Training> trainings;

    public NewTrainingsAdapter(Activity context) {
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

        trainings = new ArrayList<>();

        ArrayList<TrainingPart> parts = new ArrayList<>();

        parts.add(new TrainingPart("1", "Squats", 0, 50, 5 , 0, "Your legs on your shoulders line. Keep your arms outstretched in front of you. Do not tear off your feet from the ground. Slowly let your pelvis down till your arms reach your knees."));
        parts.add(new TrainingPart("1", "Power Ups", 0, 30, 4 , 0, "Put your arms on the floor on your shoulders line, same as your legs. Let down your body. Do not let your loin sag. Keep your body straight as an arrow."));
        parts.add(new TrainingPart("1", "Pull-Ups", 0, 10, 4 , 0, "Jump on the horizontal bar. Your arms on your shoulders line. Pull your body up, till your elbow reaches the bar. Try not to shake your body. Keep it straight. Go down slowly till you fully release you arms."));
        parts.add(new TrainingPart("1", "Jumping", 0, 20, 4 , 0, "Your legs on your shoulders line. Keep your arms outstretched in front of you. Do not tear off your feet from the ground. Slowly let your pelvis down till your arms reach your knees. Jump out as high as you can. Do not pull your legs up. They just lie down."));

        trainings.add(new Training("1", 1, "Intensive cross-fit training", "Crossfit, Power", 4,
                new Date(2015, 11, 4), 80, parts,
                new Professional("1", "photo", null, R.drawable.test4, R.drawable.test9,
                        "Mike Silvestri", "Cross-fit coach. Your faithful assistant to athletic body in short time.",
                        ProfessionalType.COACH, false, 8, 32, 4.7),
                "A complex of 18 exercises to get your body in shape, and achieve great results in stamina and power. Appreciated by more than 1000+ users."));


        ArrayList<TrainingPart> parts2 = new ArrayList<>();

        parts2.add(new TrainingPart("1", "Warm-up walk", 0, 0, 0 , 5, "So to warm up your body, walk slowly."));
        parts2.add(new TrainingPart("1", "Jogging", 0, 0, 0 , 15, "Slowly start to jog. Check out your breathe."));
        parts2.add(new TrainingPart("1", "Run From Problems", 0, 0, 0 , 30, "Simple run. Speed about 7-9 km/h. Check out your breathe."));
        parts2.add(new TrainingPart("1", "Speed Ups", 0, 10, 0 , 3, "Speed Up and run as fast as you can. Do not stop after each speed up! Run the way you did before speed up. "));
        parts2.add(new TrainingPart("1", "Shuttle Run", 0, 2, 0 , 5, "Make two checkpoints on the ground(line, stones, bottles, whatever). Run from one checkpoint to another as fast as you can touching the checkpoint with your arm each time."));
        parts2.add(new TrainingPart("1", "Run From Problems", 0, 0, 0 , 30, "Simple run. Speed about 7-9 km/h. Check out your breathe. "));

        trainings.add(new Training("1", 2, "Run Forrest, Run!", "Running, Stamina", 6,
                new Date(2015, 11, 8), 80, parts2,
                new Professional("1", "photo", null, R.drawable.test5, R.drawable.test10,
                        "Henry Harrison", "Professional marathoner. The best choice to improve your stamina and get fit.",
                        ProfessionalType.COACH, false, 12, 68, 4.5),
                "A set of running programs to maintain your shape and develop stamina. Breath right, run fast and GetFit with other 2000 subscribers."));

        ArrayList<TrainingPart> parts3 = new ArrayList<>();

        parts3.add(new TrainingPart("1", "Warm-up", 0, 0, 0 , 5, "A very simple warm-up complex for 5 minutes."));
        parts3.add(new TrainingPart("1", "Warm-up run", 0, 0, 0 , 15, "Slowly start to jog. Check out your breathe. Do not try to run fast. Your speed should be about 5-6 km/h."));
        parts3.add(new TrainingPart("1", "Hyperextension", 0, 10, 4 , 0, "Fix your body on the hyperextension kit. Slowly let your body down till your breast reaches your knees level. Slowly go up. If the exercise is to easy for you, you can take some additional weight in your arms."));
        parts3.add(new TrainingPart("1", "Squats with weight", 0, 12, 4 , 0, "Your legs on your shoulders line. Put a bar with needed weight on your shoulders and hold it with your arms. Do not tear off your feet from the ground. Slowly let your pelvis down till your arms reach your knees. "));

        trainings.add(new Training("1", 3, "Tinas’ Choice", "Fitness", 4,
                new Date(2015, 11, 12), 80, parts3,
                new Professional("1", "photo", null, R.drawable.test6, R.drawable.tina,
                        "Tina Kandelaki", "Fitness fan. Get the training celebrity uses, with all the features eventually developed.",
                        ProfessionalType.COACH, false, 5, 188, 4.9),
                "A training developed by Tina! Get her shape, with all the tips provided by strong woman who uses this set for years!"));

        ArrayList<TrainingPart> parts4 = new ArrayList<>();

        parts4.add(new TrainingPart("1", "Lunges on one leg", 0, 10, 4 , 0, "Slowly lunge on one leg, than on another. Starting position: Legs on your shoulders line. Arms lying down. Put your leg a big step(70 cm) forward and let your pelvis down, putting weight on your forward leg."));
        parts4.add(new TrainingPart("1", "Cycle on your back", 0, 0, 4 , 2, "Lye on the ground on your back. Arms on the ground on your shoulders line. Put your legs up in the air. Make moves like you are cycling on a bike."));
        parts4.add(new TrainingPart("1", "Leg pushes", 50, 10, 4 , 0, "Take a position in a push kit. Put legs on your shoulders line. Slowly push the weight up, and slowly let it down"));

        trainings.add(new Training("1", 4, "Rehabilitation after knee injury", "Rehabilitation", 3,
                new Date(2015, 11, 15), 80, parts4,
                new Professional("1", "photo", null, R.drawable.sports_medicine, R.drawable.arkov,
                        "Vladimir Arkov", "Ph.D in Medical Sciences, Head of Physiotherapy Department in Moscow Sports Medicine Clinic",
                        ProfessionalType.DOCTOR, false, 6, 224, 4.9),
                "This program is a complex of exercises for sportsmen rehabilitation after knee injuries."));

        notifyDataSetChanged();

    }

    public void setContext(Activity context) {
        this.context = context;
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
        holder.type.setText(training.getType());
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
                Intent intent = new Intent(context, NewTrainingDetailsActivity.class);
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