package binaryblitz.athleteapp.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.nineoldandroids.view.ViewPropertyAnimator;

import binaryblitz.athleteapp.CircularReveal.animation.SupportAnimator;
import binaryblitz.athleteapp.CircularReveal.animation.ViewAnimationUtils;

public class AndroidUtils {

    public int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result; // + (int) convertDpToPixel(56f, getContext());
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    public static void animateRevealShow(final View v) {
        // get the center for the clipping circle
        int cx = 0;
        int cy = 0;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(v.getWidth(), v.getHeight());

        SupportAnimator animator =
                ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, finalRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(350);
        animator.addListener(new SupportAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart() {
                v.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd() {
            }

            @Override
            public void onAnimationCancel() {
            }

            @Override
            public void onAnimationRepeat() {
            }
        });
        animator.start();
    }

    public static void animateRevealShowFirst(final View v, Activity activity) {
        // get the center for the clipping circle
        int cx = 0;
        int cy = 0;

        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(width, height);

        SupportAnimator animator =
                ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, finalRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(350);
        animator.addListener(new SupportAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart() {
                v.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd() {
            }

            @Override
            public void onAnimationCancel() {
            }

            @Override
            public void onAnimationRepeat() {
            }
        });
        animator.start();
    }

    public static void animateRevealHide(final View v) {
        // get the center for the clipping circle
        int cx = (v.getLeft() + v.getRight()) / 2;
        int cy = (v.getTop() + v.getBottom()) / 2;

        // get the final radius for the clipping circle
        int finalRadius = v.getWidth();

        SupportAnimator animator =
                ViewAnimationUtils.createCircularReveal(v, cx, cy, finalRadius, 0);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(350);
        animator.addListener(new SupportAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart() {
            }

            @Override
            public void onAnimationEnd() {
                v.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel() {

            }

            @Override
            public void onAnimationRepeat() {

            }
        });
        animator.start();
    }

    public static void showFab(final View fa_button) {
        fa_button.setVisibility(View.VISIBLE);
        ViewPropertyAnimator.animate(fa_button).cancel();
        ViewPropertyAnimator.animate(fa_button).scaleX(1).scaleY(1).setDuration(200).start();
    }

    public static void hideFab(final View fa_button) {
        ViewPropertyAnimator.animate(fa_button).cancel();
        ViewPropertyAnimator.animate(fa_button).scaleX(0).scaleY(0).setDuration(200).start();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fa_button.setVisibility(View.GONE);
            }
        }, 200);
    }
}
