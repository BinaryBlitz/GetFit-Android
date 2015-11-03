package binaryblitz.athleteapp.Custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressWarnings("unused")
public class ThinRobotoTextView extends TextView {

    public ThinRobotoTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ThinRobotoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ThinRobotoTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/roboto_thin.ttf");
        setTypeface(tf);
    }

}
