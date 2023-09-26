package org.moa.etlits.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.moa.etlits.R;
import org.moa.etlits.data.models.Establishment;

import java.util.ArrayList;

public class EstablishmentAdapter extends ArrayAdapter<Establishment> {

    public EstablishmentAdapter(Context context, ArrayList<Establishment> establishments) {
        super(context, 0, establishments);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.establishment_item, parent, false);
        }

        Establishment establishment = getItem(position);
        TextView establishmentText = convertView.findViewById(R.id.tv_establishment);

        if (establishment != null) {
            establishmentText.setText(establishment.toString());
        }

        return convertView;
    }
}
