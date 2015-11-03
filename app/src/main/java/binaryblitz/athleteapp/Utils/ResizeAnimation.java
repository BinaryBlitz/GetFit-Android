package binaryblitz.athleteapp.Utils;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * an animation for resizing the view.
 */
public class ResizeAnimation extends Animation {
    private View mView;
    private float mToHeight;
    private float mFromHeight;

    public ResizeAnimation(View v, float fromHeight, float toHeight) {
        mToHeight = toHeight;
        mFromHeight = fromHeight;
        mView = v;
        setDuration(300);
    }

    public ResizeAnimation(View v, float fromHeight, float toHeight, long duration) {
        mToHeight = toHeight;
        mFromHeight = fromHeight;
        mView = v;
        setDuration(duration);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float height =
                (mToHeight - mFromHeight) * interpolatedTime + mFromHeight;
        ViewGroup.LayoutParams p = mView.getLayoutParams();
        p.height = (int) height;
        mView.requestLayout();
    }
}
