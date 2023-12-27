package org.moa.etlits.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.moa.etlits.R;
import org.moa.etlits.data.models.CategoryValue;

import java.util.List;

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

    public static String getValue(String valueId, List<CategoryValue> list, String categoryKey) {
        if (valueId != null && list != null) {
              for (CategoryValue categoryValue : list) {
                if (categoryValue.getValueId().equals(valueId) && categoryValue.getCategoryKey().equals(categoryKey)) {
                    return categoryValue.getValue();
                }
            }
        }
        return valueId;
    }
}
