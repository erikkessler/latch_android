package com.inspiredo.latch;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ViewSwitcher;

/**
 * Class that gives the view switcher the ability to hold info about the String being swtiched
 */
public class StepViewSwitcher extends ViewSwitcher {

    private String mString;

    public StepViewSwitcher(Context context) {
        this(context, "");
    }

    public StepViewSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StepViewSwitcher(Context context, String string) {
        super(context);
        mString = string;
    }

    public void setString(String string) {
        mString = string;
    }

    public String getString() {
        return mString;
    }
}
