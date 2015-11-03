package binaryblitz.athleteapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import java.util.Date;

import binaryblitz.athleteapp.Activities.CalendarActivity;
import binaryblitz.athleteapp.Activities.TrainingActivity;
import binaryblitz.athleteapp.Data.FITTITraining;
import binaryblitz.athleteapp.Data.FITTITrainingPart;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Utils.DateTimeUtil;

public class TrainingsAdapter
        extends RecyclerView.Adapter<TrainingsAdapter.MyViewHolder>
        implements SwipeableItemAdapter<TrainingsAdapter.MyViewHolder> {

    private ArrayList<FITTITraining> trainings;
    private Activity context;

    public static int SELECTED;

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

    public static class MyViewHolder extends AbstractSwipeableItemViewHolder {
        public FrameLayout mContainer;
        public TextView name;
        public TextView type;
        public TextView count;
        public TextView date;
        public TextView time;

        public MyViewHolder(View v) {
            super(v);
            mContainer = (FrameLayout) v.findViewById(R.id.container);
            name = (TextView) v.findViewById(R.id.name);
            type = (TextView) v.findViewById(R.id.type);
            count = (TextView) v.findViewById(R.id.count);
            date = (TextView) v.findViewById(R.id.date);
            time = (TextView) v.findViewById(R.id.time);
        }

        @Override
        public View getSwipeableContainerView() {
            return mContainer;
        }
    }

    public TrainingsAdapter(Activity context) {
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

        // SwipeableItemAdapter requires stable ID, and also
        // have to implement the getItemId() method appropriately.
        setHasStableIds(true);

        trainings = new ArrayList<>();
    }

    public void addTraining(FITTITraining training) {
        trainings.add(training);
        notifyDataSetChanged();
    }

    public void setTrainings(ArrayList<FITTITraining> trainings) {
        this.trainings = trainings;
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

    @Override
    public long getItemId(int position) {
        return trainings.get(position).getIntId();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.training_card, parent, false);
        return new MyViewHolder(v);
    }

    public void changeItem(int position, Date date) {
        trainings.get(position).setDate(date);
        notifyItemChanged(position);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        // set background resource (target view ID: container)
        final int swipeState = holder.getSwipeStateFlags();

        if ((swipeState & Swipeable.STATE_FLAG_IS_UPDATED) != 0) {
            int bgResId;

            if ((swipeState & Swipeable.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_swiping_active_state;
            } else if ((swipeState & Swipeable.STATE_FLAG_SWIPING) != 0) {
                bgResId = R.drawable.bg_item_swiping_state;
            } else {
                bgResId = R.drawable.bg_item_normal_state;
            }

            holder.mContainer.setBackgroundResource(bgResId);
        }

        // set swiping properties
        holder.setSwipeItemHorizontalSlideAmount(0);

        final FITTITraining training = trainings.get(position);

        holder.name.setText(training.getName());
        holder.type.setText(training.getType());
        holder.count.setText(training.getExCount() + " " + context.getString(R.string.exsercises_string));
        holder.date.setText(DateTimeUtil.getFriendlyDateRepresentation(training.getDate()));
        holder.time.setText(training.getTime() + " " + context.getString(R.string.min_string));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TrainingActivity.class);
                TrainingActivity.setParent(training);
                SELECTED = position;
                TrainingActivity.setParts(training.getParts());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return trainings.size();
    }

    @Override
    public int onGetSwipeReactionType(MyViewHolder holder, int position, int x, int y) {
        return Swipeable.REACTION_CAN_SWIPE_BOTH_H;
    }

    @Override
    public void onSetSwipeBackground(MyViewHolder holder, int position, int type) {
        int bgRes = 0;
        switch (type) {
            case Swipeable.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND:
                bgRes = R.drawable.bg_swipe_item_neutral;
                break;
            case Swipeable.DRAWABLE_SWIPE_LEFT_BACKGROUND:
                bgRes = R.drawable.bg_swipe_item_left;
                break;
            case Swipeable.DRAWABLE_SWIPE_RIGHT_BACKGROUND:
                bgRes = R.drawable.bg_swipe_item_right;
                break;
        }

        holder.itemView.setBackgroundResource(bgRes);
    }

    @Override
    public SwipeResultAction onSwipeItem(MyViewHolder holder, final int position, int result) {
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
        private TrainingsAdapter mAdapter;
        private final int mPosition;
        private boolean mSetPinned;

        SwipeLeftResultAction(TrainingsAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();
            mSetPinned = true;
        }

        @Override
        protected void onSlideAnimationEnd() {
            super.onSlideAnimationEnd();

            if (mSetPinned && mAdapter.mEventListener != null) {
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
        private TrainingsAdapter mAdapter;
        private final int mPosition;

        SwipeRightResultAction(TrainingsAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();
            trainings.remove(mPosition);
            mAdapter.notifyItemRemoved(mPosition);
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

    private static class UnpinResultAction extends SwipeResultActionDefault {
        private TrainingsAdapter mAdapter;
        private final int mPosition;

        UnpinResultAction(TrainingsAdapter adapter, int position) {
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
}