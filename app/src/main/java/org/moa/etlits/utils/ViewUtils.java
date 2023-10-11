package org.moa.etlits.utils;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import org.moa.etlits.R;

public class ViewUtils {

    public static void showError(Context context, Integer error, View inputView, TextView errorView) {
        if (error != null) {
            inputView.setBackgroundResource(R.drawable.bg_input_error);
            errorView.setText(context.getString(error));
            errorView.setVisibility(View.VISIBLE);
        } else {
            errorView.setVisibility(View.GONE);
            inputView.setBackgroundResource(R.drawable.bg_input_default);
        }
    }
}
