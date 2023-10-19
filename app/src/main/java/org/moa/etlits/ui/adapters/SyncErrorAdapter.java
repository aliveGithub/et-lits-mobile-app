package org.moa.etlits.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.moa.etlits.R;
import org.moa.etlits.data.models.SyncError;
import org.moa.etlits.utils.Constants;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class SyncErrorAdapter extends ArrayAdapter<SyncError> {

    public SyncErrorAdapter(Context context, ArrayList<SyncError> errors) {
        super(context, 0, errors);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.sync_error_row, parent, false);
        }

        SyncError error = getItem(position);
        TextView errorMsg = convertView.findViewById(R.id.tv_error_msg);

        if (error != null) {
            if (Constants.SYNC_VALIDATION_ERROR.equals(error.getErrorKey()) && error.getMessage() != null) {
                errorMsg.setText(error.getMessage());
            } else {
                errorMsg.setText(getErrorMessage(error.getErrorKey()));
            }
        }

        return convertView;
    }

    public int getErrorMessage(String errorKey) {
        if (String.valueOf(Constants.SERVER_UNREACHABLE).equals(errorKey)) {
            return R.string.sync_error_server_unreachable;
        } else if (String.valueOf(HttpURLConnection.HTTP_UNAUTHORIZED).equals(errorKey)) {
            return R.string.sync_error_401;
        } else {
            return R.string.sync_error_generic;
        }
    }
}
