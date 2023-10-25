package org.moa.etlits.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.moa.etlits.R;
import org.moa.etlits.data.models.AnimalSearchResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnimalSearchAdapter extends ArrayAdapter<AnimalSearchResult> {

    public AnimalSearchAdapter(Context context, ArrayList<AnimalSearchResult> animals) {
        super(context, 0, animals);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.autocomplete_result_item, parent, false);
        }

        AnimalSearchResult animal = getItem(position);
        TextView animalText = convertView.findViewById(R.id.tv_autocomplete_result_item);

        if (animal != null) {
            animalText.setText(animal.toString());
        }

        return convertView;
    }

    public void submitList(List<AnimalSearchResult> list) {
        Collections.sort(list);

        clear();
        addAll(list);
        notifyDataSetChanged();
    }
}
