package org.moa.etlits.utils;

import android.content.Context;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import org.moa.etlits.R;
import org.moa.etlits.data.models.CategoryValue;

import java.util.List;

public class ViewUtils {

    private static final String LANG_EN = "en";

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

    public static String getCategoryLabel(String valueId, List<CategoryValue> list, String categoryKey) {
        if (valueId != null && list != null) {
            for (CategoryValue categoryValue : list) {
                if (categoryValue.getLanguage().equals(LANG_EN) && categoryValue.getValueId().equals(valueId)
                        && categoryValue.getCategoryKey().equals(categoryKey)) {
                    return categoryValue.getValue();
                }
            }
        }

        return valueId;
    }
}
