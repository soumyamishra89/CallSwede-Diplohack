package diplohack.callswede.view.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by SOMU on 30/05/16.
 */
public class CustomBoldTextView extends TextView {
    public CustomBoldTextView(Context context){
        super(context);
    }

    public CustomBoldTextView(Context context, AttributeSet attrs){
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Bold.ttf"));
    }

    public CustomBoldTextView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }

    public CustomBoldTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
