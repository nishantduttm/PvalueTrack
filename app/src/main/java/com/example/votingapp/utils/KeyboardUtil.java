package com.example.votingapp.utils;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.votingapp.R;

public class KeyboardUtil {
    private View decorView;
    private View contentView;

    public KeyboardUtil(Activity act, View contentView) {
        this.decorView = act.getWindow().getDecorView();
        this.contentView = contentView;

        //only required on newer android versions. it was working on API level 19
        if (Build.VERSION.SDK_INT >= 19) {
            decorView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
        }
    }

    public void enable() {
        if (Build.VERSION.SDK_INT >= 19) {
            decorView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
        }
    }

    public void disable() {
        if (Build.VERSION.SDK_INT >= 19) {
            decorView.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
        }
    }


    //a small helper to allow showing the editText focus
    ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            Rect r = new Rect();
            //r will be populated with the coordinates of your view that area still visible.
            decorView.getWindowVisibleDisplayFrame(r);

            //get screen height and calculate the difference with the useable area from the r
            int height = decorView.getContext().getResources().getDisplayMetrics().heightPixels;
            int diff = Math.abs(r.bottom - height);
            Log.d("info", "onGlobalLayout: "+diff);
            //if it could be a keyboard add the padding to the view
            if (diff > 200) {
                // if the use-able screen height differs from the total screen height we assume that it shows a keyboard now
                //check if the padding is 0 (if yes set the padding for the keyboard)
                if (contentView.getPaddingBottom() != diff) {
                    //set the padding of the contentView for the keyboard
                    contentView.setPadding(0, 0, 0, (int)(diff * 0.85));
                    Button b = contentView.findViewById(R.id.submitButton);
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            0,
                            12
                    );
                    Resources resources = contentView.getResources();
                    int px = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            10,
                            resources.getDisplayMetrics());
                    param.setMargins(px, px, px, px);
                    b.setLayoutParams(param);
                }
            } else {
                //check if the padding is != 0 (if yes reset the padding)
                if (contentView.getPaddingBottom() != 0) {
                    //reset the padding of the contentView
                    contentView.setPadding(0, 0, 0, 0);
                    Button b = contentView.findViewById(R.id.submitButton);
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            0,
                            7
                    );
                    Resources resources = contentView.getResources();
                    int px = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            10,
                            resources.getDisplayMetrics());
                    param.setMargins(px, px, px, px);
                    b.setLayoutParams(param);
                }
            }
        }
    };


    /**
     * Helper to hide the keyboard
     *
     * @param act
     */
    public static void hideKeyboard(Activity act) {
        if (act != null && act.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) act.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(act.getCurrentFocus().getWindowToken(), 0);
        }
    }
}


