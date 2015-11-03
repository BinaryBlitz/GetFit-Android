package binaryblitz.athleteapp.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDefault;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionMoveToSwipedDirection;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionRemoveItem;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils;

import java.util.ArrayList;

import binaryblitz.athleteapp.Activities.ProfProfileActivity;
import binaryblitz.athleteapp.Activities.TrainingActivity;
import binaryblitz.athleteapp.Activities.TrainingPartActivity;
import binaryblitz.athleteapp.Data.FITTITraining;
import binaryblitz.athleteapp.Data.FITTITrainingPart;
import binaryblitz.athleteapp.R;

public class TrainingPartsAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements SwipeableItemAdapter<RecyclerView.ViewHolder> {

    private ArrayList<Pair<Integer, Pair<Integer, Object>>> trainings;
    private Activity context;

    private FITTITraining parent;

    public static final int SPACE = 1;
    public static final int HEADER = 2;
    public static final int DONE = 3;
    public static final int BASIC = 4;
    public static final int FOOTER = 5;

    public static int SELECTED = 0;

    private int count;
    private int inc = 3;

    // NOTE: Make accessible with short name
    private interface Swipeable extends SwipeableItemConstants {
    }

    private EventListener mEventListener;
    private View.OnClickListener mItemViewOnClickListener;
    private View.OnClickListener mSwipeableViewContainerOnClickListener;

    public interface EventListener {
        void onItemRemoved(int position);

        void onItemPinned(int position);

        void onItemViewClicked(View v, boolean pinned);
    }

    public void remove(int position) {
        notifyItemRemoved(position);
    }

    public void setTrainings(ArrayList<Pair<Integer, Pair<Integer, Object>>> trainings) {
        this.trainings = trainings;

        int done = 0;
        int basic = 0;

        for(int i = 0; i < trainings.size(); i++) {
            if(trainings.get(i).second.first == DONE) {
                done++;
            } else if (trainings.get(i).second.first == BASIC) {
                basic++;
            }
        }

        ((TrainingActivity) context).setPercents((int) (((double) done / (double) (done + basic)) * 100));
    }

    public TrainingPartsAdapter(Activity context, FITTITraining parent) {
        mItemViewOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemViewClick(v);
            }
        };
        mSwipeableViewContainerOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwipeableViewContainerClick(v);
            }
        };

        this.context = context;
        this.parent = parent;

        setHasStableIds(true);

        trainings = new ArrayList<>();
    }

    private void onItemViewClick(View v) {
        if (mEventListener != null) {
            mEventListener.onItemViewClicked(v, true); // true --- pinned
        }
    }

    private void onSwipeableViewContainerClick(View v) {
        if (mEventListener != null) {
            mEventListener.onItemViewClicked(RecyclerViewAdapterUtils.getParentViewHolderItemView(v), false);  // false --- not pinned
        }
    }

    public void doneTraining(int mPosition) {
        Pair<Integer, Pair<Integer, Object>> obj = trainings.get(mPosition);
        trainings.remove(mPosition);
        notifyItemRemoved(mPosition);
        for(int i = 0; i < trainings.size(); i++) {
            if(trainings.get(i).second.first == SPACE) {
                trainings.add(i + 1, new Pair<>(trainings.size() + inc, new Pair<>(DONE, obj.second.second)));
                notifyItemInserted(i + 1);
                inc++;
                break;
            }
        }

        int done = 0;
        int basic = 0;

        for(int i = 0; i < trainings.size(); i++) {
            if(trainings.get(i).second.first == DONE) {
                done++;
            } else if (trainings.get(i).second.first == BASIC) {
                basic++;
            }
        }

        ((TrainingActivity) context).setPercents((int) (((double) done / (double) (done + basic)) * 100));
    }

    @Override
    public int getItemViewType(int position) {
        return trainings.get(position).second.first;
    }

    @Override
    public long getItemId(int position) {
        return trainings.get(position).first;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case SPACE:
                final View v = inflater.inflate(R.layout.space_card, parent, false);
                return new SpaceViewHolder(v);
            case HEADER:
                final View v2 = inflater.inflate(R.layout.header_card, parent, false);
                return new HeaderViewHolder(v2);
            case DONE:
                final View v3 = inflater.inflate(R.layout.training_done_card, parent, false);
                return new DoneViewHolder(v3);
            case BASIC:
                final View v4 = inflater.inflate(R.layout.training_part_card, parent, false);
                return new BasicViewHolder(v4);
            case FOOTER:
                final View v6 = inflater.inflate(R.layout.final_card, parent, false);
                return new FooterViewHolder(v6);
            default:
                final View v5 = inflater.inflate(R.layout.space_card, parent, false);
                return new SpaceViewHolder(v5);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

//        // set listeners
//        // (if the item is *not pinned*, click event comes to the itemView)
//        holder.itemView.setOnClickListener(mItemViewOnClickListener);
//        // (if the item is *pinned*, click event comes to the mContainer)
//        holder.mContainer.setOnClickListener(mSwipeableViewContainerOnClickListener);

        // set background resource (target view ID: container)

        if(getItemViewType(position) == DONE) {
            final int swipeState = ((DoneViewHolder) holder).getSwipeStateFlags();
            ((DoneViewHolder) holder).setSwipeItemHorizontalSlideAmount(0);
            final DoneViewHolder basicViewHolder = (DoneViewHolder) holder;
            final FITTITrainingPart part = (FITTITrainingPart) trainings.get(position).second.second;
            if ((swipeState & Swipeable.STATE_FLAG_IS_UPDATED) != 0) {
                int bgResId;

                if ((swipeState & Swipeable.STATE_FLAG_IS_ACTIVE) != 0) {
                    bgResId = R.drawable.bg_item_swiping_active_state;
                } else if ((swipeState & Swipeable.STATE_FLAG_SWIPING) != 0) {
                    bgResId = R.drawable.bg_item_swiping_state;
                } else {
                    bgResId = R.drawable.bg_item_normal_state;
                }

                ((DoneViewHolder) holder).mContainer.setBackgroundResource(bgResId);

                basicViewHolder.name.setText(part.getName());

                if(part.getTime() == 0) {
                    basicViewHolder.time.setVisibility(View.GONE);
                } else {
                    basicViewHolder.time.setVisibility(View.VISIBLE);
                    basicViewHolder.time.setText(part.getTime() + " MIN");
                }

                if(part.getReps() == 0) {
                    basicViewHolder.reps.setVisibility(View.GONE);
                } else {
                    basicViewHolder.reps.setVisibility(View.VISIBLE);
                    basicViewHolder.reps.setText(part.getReps() + " REPS");
                }

                if(part.getWeight() == 0) {
                    basicViewHolder.weight.setVisibility(View.GONE);
                } else {
                    basicViewHolder.weight.setVisibility(View.VISIBLE);
                    basicViewHolder.weight.setText(part.getWeight() + " KG");
                }

                if(part.getCount() == 0) {
                    basicViewHolder.count.setVisibility(View.GONE);
                } else {
                    basicViewHolder.count.setVisibility(View.VISIBLE);
                    basicViewHolder.count.setText(part.getCount() + " TIMES");
                }
            }

        } else if(getItemViewType(position) == BASIC) {
            final int swipeState = ((BasicViewHolder) holder).getSwipeStateFlags();
            ((BasicViewHolder) holder).setSwipeItemHorizontalSlideAmount(0);
            final FITTITrainingPart part = (FITTITrainingPart) trainings.get(position).second.second;

            final BasicViewHolder basicViewHolder = (BasicViewHolder) holder;

            if ((swipeState & Swipeable.STATE_FLAG_IS_UPDATED) != 0) {
                int bgResId;

                if ((swipeState & Swipeable.STATE_FLAG_IS_ACTIVE) != 0) {
                    bgResId = R.drawable.bg_item_swiping_active_state;
                } else if ((swipeState & Swipeable.STATE_FLAG_SWIPING) != 0) {
                    bgResId = R.drawable.bg_item_swiping_state;
                } else {
                    bgResId = R.drawable.bg_item_normal_state;
                }

                ((BasicViewHolder) holder).mContainer.setBackgroundResource(bgResId);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, TrainingPartActivity.class);
                        SELECTED = position;
                        TrainingPartActivity.setPart(part);
                        context.startActivity(intent);
                    }
                });
            }

            basicViewHolder.weight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TrainingActivity) context).show(new TrainingActivity.DialogFinishedListener() {
                        @Override
                        public void onDialogFinished(int val) {
                            basicViewHolder.weight.setText(val + " KG");
                        }
                    }, part.getWeight());
                }
            });

            basicViewHolder.count.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TrainingActivity) context).show(new TrainingActivity.DialogFinishedListener() {
                        @Override
                        public void onDialogFinished(int val) {
                            basicViewHolder.count.setText(val + " TIMES");
                        }
                    }, part.getCount());
                }
            });

            basicViewHolder.time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TrainingActivity) context).show(new TrainingActivity.DialogFinishedListener() {
                        @Override
                        public void onDialogFinished(int val) {
                            basicViewHolder.time.setText(val + " MIN");
                        }
                    }, part.getTime());
                }
            });

            basicViewHolder.reps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TrainingActivity) context).show(new TrainingActivity.DialogFinishedListener() {
                        @Override
                        public void onDialogFinished(int val) {
                            basicViewHolder.reps.setText(val + " REPS");
                        }
                    }, part.getReps());
                }
            });

            basicViewHolder.name.setText(part.getName());

            if(part.getTime() == 0) {
                basicViewHolder.time.setVisibility(View.GONE);
            } else {
                basicViewHolder.time.setVisibility(View.VISIBLE);
                basicViewHolder.time.setText(part.getTime() + " MIN");
            }

            if(part.getReps() == 0) {
                basicViewHolder.reps.setVisibility(View.GONE);
            } else {
                basicViewHolder.reps.setVisibility(View.VISIBLE);
                basicViewHolder.reps.setText(part.getReps() + " REPS");
            }

            if(part.getWeight() == 0) {
                basicViewHolder.weight.setVisibility(View.GONE);
            } else {
                basicViewHolder.weight.setVisibility(View.VISIBLE);
                basicViewHolder.weight.setText(part.getWeight() + " KG");
            }

            if(part.getCount() == 0) {
                basicViewHolder.count.setVisibility(View.GONE);
            } else {
                basicViewHolder.count.setVisibility(View.VISIBLE);
                basicViewHolder.count.setText(part.getCount() + " TIMES");
            }


        } else if(getItemViewType(position) == HEADER) {
            ((HeaderViewHolder) holder).itemView.findViewById(R.id.textView16).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TrainingActivity) context).animateTip();
                }
            });

            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;

            headerViewHolder.desc.setText(parent.getDesc());
            headerViewHolder.name.setText(parent.getOwner().getName());
            headerViewHolder.avatar.setImageResource(parent.getOwner().getDebug_userPhotoResId());

            headerViewHolder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ProfProfileActivity.class);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return trainings.size();
    }

    @Override
    public int onGetSwipeReactionType(RecyclerView.ViewHolder holder, int position, int x, int y) {
        switch (getItemViewType(position)) {
            case SPACE:
                return Swipeable.REACTION_CAN_NOT_SWIPE_ANY;
            case HEADER:
                return Swipeable.REACTION_CAN_NOT_SWIPE_ANY;
            case DONE:
                return Swipeable.REACTION_CAN_SWIPE_LEFT;
            case BASIC:
                return Swipeable.REACTION_CAN_SWIPE_RIGHT;
            default:
                return Swipeable.REACTION_CAN_NOT_SWIPE_ANY;
        }
    }

    @Override
    public void onSetSwipeBackground(RecyclerView.ViewHolder holder, int position, int type) {
        int bgRes = 0;
        switch (type) {
            case Swipeable.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND:
                bgRes = R.drawable.bg_swipe_item_neutral;
                break;
            case Swipeable.DRAWABLE_SWIPE_LEFT_BACKGROUND:
                bgRes = R.drawable.red_dismiss_back;
                break;
            case Swipeable.DRAWABLE_SWIPE_RIGHT_BACKGROUND:
                bgRes = R.drawable.bg_swipe_item_right;
                break;
        }

        holder.itemView.setBackgroundResource(bgRes);
    }

    @Override
    public SwipeResultAction onSwipeItem(RecyclerView.ViewHolder holder, final int position, int result) {
        switch (result) {
            // swipe right
            case Swipeable.RESULT_SWIPED_RIGHT:
                return new SwipeRightResultAction(this, position);
            // swipe left -- pin
            case Swipeable.RESULT_SWIPED_LEFT:
                return new SwipeLeftResultAction(this, position);
            // other --- do nothing
            case Swipeable.RESULT_CANCELED:
            default:
                return new UnpinResultAction(this, position);
        }
    }

    public EventListener getEventListener() {
        return mEventListener;
    }

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }

    private class SwipeLeftResultAction extends SwipeResultActionMoveToSwipedDirection {
        private TrainingPartsAdapter mAdapter;
        private final int mPosition;
        private boolean mSetPinned;

        SwipeLeftResultAction(TrainingPartsAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();
            Pair<Integer, Pair<Integer, Object>> obj = trainings.get(mPosition);
            trainings.remove(mPosition);
            mAdapter.notifyItemRemoved(mPosition);
            for(int i = trainings.size() - 1; i >= 0; i--) {
                if(trainings.get(i).second.first == SPACE) {
                    if(i == trainings.size() - 1) {
                        trainings.add(new Pair<>(trainings.size() + inc, new Pair<>(BASIC, obj.second.second)));
                        notifyItemInserted(trainings.size() + 1);
                    } else {
                        trainings.add(i + 1, new Pair<>(trainings.size() + inc, new Pair<>(BASIC, obj.second.second)));
                        notifyItemInserted(i + 1);
                    }
                    inc++;
                    break;
                }
            }

            int done = 0;
            int basic = 0;

            for(int i = 0; i < trainings.size(); i++) {
                if(trainings.get(i).second.first == DONE) {
                    done++;
                } else if (trainings.get(i).second.first == BASIC) {
                    basic++;
                }
            }

            ((TrainingActivity) context).setPercents((int) (((double) done / (double) (done + basic)) * 100));
        }

        @Override
        protected void onSlideAnimationEnd() {
            super.onSlideAnimationEnd();
            if (mAdapter.mEventListener != null) {
                mAdapter.mEventListener.onItemPinned(mPosition);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }

    private class SwipeRightResultAction extends SwipeResultActionRemoveItem {
        private TrainingPartsAdapter mAdapter;
        private final int mPosition;

        SwipeRightResultAction(TrainingPartsAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();
            Pair<Integer, Pair<Integer, Object>> obj = trainings.get(mPosition);
            trainings.remove(mPosition);
            mAdapter.notifyItemRemoved(mPosition);
            for(int i = 0; i < trainings.size(); i++) {
                if(trainings.get(i).second.first == SPACE) {
                    trainings.add(i + 1, new Pair<>(trainings.size() + inc, new Pair<>(DONE, obj.second.second)));
                    notifyItemInserted(i + 1);
                    inc++;
                    break;
                }
            }

            int done = 0;
            int basic = 0;

            for(int i = 0; i < trainings.size(); i++) {
                if(trainings.get(i).second.first == DONE) {
                    done++;
                } else if (trainings.get(i).second.first == BASIC) {
                    basic++;
                }
            }

            ((TrainingActivity) context).setPercents((int) (((double) done / (double) (done + basic)) * 100));

        }

        @Override
        protected void onSlideAnimationEnd() {
            super.onSlideAnimationEnd();

            if (mAdapter.mEventListener != null) {
                mAdapter.mEventListener.onItemRemoved(mPosition);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }

    private class UnpinResultAction extends SwipeResultActionDefault {
        private TrainingPartsAdapter mAdapter;
        private final int mPosition;

        UnpinResultAction(TrainingPartsAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            mAdapter.notifyItemChanged(mPosition);
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }

    public static class BasicViewHolder extends AbstractSwipeableItemViewHolder {
        public FrameLayout mContainer;
        public TextView weight;
        public TextView time;
        public TextView reps;
        public TextView count;

        public TextView name;

        public BasicViewHolder(View v) {
            super(v);
            mContainer = (FrameLayout) v.findViewById(R.id.container);
            weight = (TextView) v.findViewById(R.id.textView5fdfd);
            name = (TextView) v.findViewById(R.id.textView9);
            reps = (TextView) v.findViewById(R.id.textView5);
            count = (TextView) v.findViewById(R.id.textView5fdfddsds);
            time = (TextView) v.findViewById(R.id.textView5fdf);
        }

        @Override
        public View getSwipeableContainerView() {
            return mContainer;
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public FrameLayout mContainer;
        public TextView name;
        public TextView desc;
        public View btn;
        public ImageView avatar;

        public HeaderViewHolder(View v) {
            super(v);

            desc = (TextView) v.findViewById(R.id.textView19);
            name = (TextView) v.findViewById(R.id.textViewfdfd);
            btn = v.findViewById(R.id.trainer);
            avatar = (ImageView) v.findViewById(R.id.imageView);

        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View v) {
            super(v);
        }
    }

    public static class SpaceViewHolder extends RecyclerView.ViewHolder {

        public SpaceViewHolder(View v) {
            super(v);
        }
    }

    public static class DoneViewHolder extends AbstractSwipeableItemViewHolder {
        public FrameLayout mContainer;
        public TextView weight;
        public TextView time;
        public TextView reps;
        public TextView count;

        public TextView name;

        public DoneViewHolder(View v) {
            super(v);
            mContainer = (FrameLayout) v.findViewById(R.id.container);
            weight = (TextView) v.findViewById(R.id.textView5fdfd);
            name = (TextView) v.findViewById(R.id.textView9);
            reps = (TextView) v.findViewById(R.id.textView5);
            count = (TextView) v.findViewById(R.id.textView5fdfddsds);
            time = (TextView) v.findViewById(R.id.textView5fdf);
        }

        @Override
        public View getSwipeableContainerView() {
            return mContainer;
        }
    }
}