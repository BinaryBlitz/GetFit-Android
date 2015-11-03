package binaryblitz.athleteapp.Custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressWarnings("unused")
public class BoldRobotoTextView extends TextView {

    public BoldRobotoTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public BoldRobotoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BoldRobotoTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/robot_bold.ttf");
        setTypeface(tf);
    }

}
