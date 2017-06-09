package com.shashank.notes;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by shashank on 10/06/16.
 */
public class BindingUtils {

    @BindingAdapter("shouldFocusOnClick")
    public static void onEditTextClick(final EditText editText, final Boolean shouldFocus) {
            editText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setFocusableInTouchMode(true);
                    v.requestFocus();
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(v,0);
                }
            });
    }

}