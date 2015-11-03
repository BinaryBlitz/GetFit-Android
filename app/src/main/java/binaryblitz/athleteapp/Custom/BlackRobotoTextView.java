package binaryblitz.athleteapp.Custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class BlackRobotoTextView extends TextView {

    public BlackRobotoTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public BlackRobotoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BlackRobotoTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/roboto_black.ttf");
        setTypeface(tf);
    }

}