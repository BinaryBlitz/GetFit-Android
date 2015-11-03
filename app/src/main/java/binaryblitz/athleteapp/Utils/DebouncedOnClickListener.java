package binaryblitz.athleteapp.Utils;

import android.os.SystemClock;
import android.view.View;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * A Debounced OnClickListener
 * Rejects clicks that are too close together in time.
 * This class is safe to use as an OnClickListener for multiple views, and will debounce each one separately.
 * @see {@link} http://stackoverflow.com/questions/16534369/avoid-button-multiple-rapid-clicks
 */
public abstract class DebouncedOnClickListener implements View.OnClickListener {

    private final long minimumInterval = 500L;
    private Map<View, Long> lastClickMap;

    /**
     * Implement this in your subclass instead of onClick
     * @param v The view that was clicked
     */
    public abstract void onDebouncedClick(View v);

    /**
     * The one and only constructor
     */
    public DebouncedOnClickListener() {
        this.lastClickMap = new WeakHashMap<View, Long>();
    }

    @Override
    public void onClick(View clickedView) {
        Long previousClickTimestamp = lastClickMap.get(clickedView);
        long currentTimestamp = SystemClock.uptimeMillis();

        lastClickMap.put(clickedView, currentTimestamp);
        if(previousClickTimestamp == null || (currentTimestamp - previousClickTimestamp.longValue() > minimumInterval)) {
            onDebouncedClick(clickedView);
        }
    }
}
