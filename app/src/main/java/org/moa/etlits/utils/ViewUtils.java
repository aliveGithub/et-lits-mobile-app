package org.moa.etlits.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.moa.etlits.R;

public class ViewUtils {

    public static void showError(Context context, Integer error, View inputView, TextView errorView) {
        if (error != null) {
            if (inputView instanceof Spinner || inputView instanceof AutoCompleteTextView) {
                inputView.setBackgroundResource(R.drawable.bg_dropdown_error);
            } else {
                inputView.setBackgroundResource(R.drawable.bg_input_error);
            }
            errorView.setText(context.getString(error));
            errorView.setVisibility(View.VISIBLE);
        } else {
            errorView.setVisibility(View.GONE);
            if (inputView instanceof Spinner || inputView instanceof AutoCompleteTextView) {
                inputView.setBackgroundResource(R.drawable.bg_dropdown_default);
            } else {
                inputView.setBackgroundResource(R.drawable.bg_input_default);
            }
        }
    }

    public static Dialog showDialog(Context context, int titleRId, int messageRId,
                              int positiveButtonRId,
                              int negativeButtonRId,
                              int neutralButtonRId,
                              boolean showPositiveButton,
                                    boolean showNegativeButton,
                                boolean showNeutralButton,
                              View.OnClickListener positiveButtonListener,
                              View.OnClickListener negativeButtonListener,
                                    View.OnClickListener neutralButtonListener) {

        final Dialog customDialog = new Dialog(context);
        customDialog.setContentView(R.layout.custom_dialog);
        customDialog.setCancelable(false);

        TextView title = customDialog.findViewById(R.id.dialog_title);
        title.setText(titleRId);

        TextView message = customDialog.findViewById(R.id.dialog_message);
        message.setText(messageRId);

        Button positiveButton = customDialog.findViewById(R.id.positive_button);
        positiveButton.setVisibility(showPositiveButton ? View.VISIBLE : View.GONE);
        positiveButton.setText(positiveButtonRId);
        positiveButton.setOnClickListener(positiveButtonListener);

        Button negativeButton = customDialog.findViewById(R.id.negative_button);
        negativeButton.setVisibility(showNegativeButton ? View.VISIBLE : View.GONE);
        negativeButton.setText(negativeButtonRId);
        negativeButton.setOnClickListener(negativeButtonListener);
        customDialog.show();
        return customDialog;
    }
}
