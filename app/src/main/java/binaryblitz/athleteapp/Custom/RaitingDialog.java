package binaryblitz.athleteapp.Custom;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RatingBar;

import binaryblitz.athleteapp.R;

public class RaitingDialog extends DialogFragment {

    public interface OnRateDialogFinished {
        void OnRateDialogFinished(float rating);
    }

    private OnRateDialogFinished listener;

    public void setListener(OnRateDialogFinished listener) {
        this.listener = listener;
    }

    float rating = 0f;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setCancelable(true);
        return inflater.inflate(R.layout.rate_dialog, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        RatingBar bar = (RatingBar) getView().findViewById(R.id.ratingBar);
        bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                RaitingDialog.this.rating = rating;
            }
        });

        getView().findViewById(R.id.textView41).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnRateDialogFinished(rating);
                dismiss();
            }
        });
    }
}