package binaryblitz.athleteapp.Custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

@SuppressWarnings("unused")
public class RobotoEditText extends EditText {

    public RobotoEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RobotoEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RobotoEditText(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/roboto.ttf");
        setTypeface(tf);
    }

}