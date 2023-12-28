package org.moa.etlits.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import org.moa.etlits.R;
import org.moa.etlits.data.models.AnimalSearchResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.provider.Settings.Secure.getString;

public class AnimalSearchAdapter extends ArrayAdapter<AnimalSearchResult> {
    private List<AnimalSearchResult> originalList;
    private CustomFilter filter;

    public AnimalSearchAdapter(Context context, ArrayList<AnimalSearchResult> animals) {
        super(context, 0, animals);
        originalList = new ArrayList<>(animals);
        filter = new CustomFilter();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.autocomplete_animal_result_item, parent, false);
        }

        AnimalSearchResult animal = getItem(position);
        TextView firstRow = convertView.findViewById(R.id.tv_row1);
        TextView secondRow = convertView.findViewById(R.id.tv_row2);

        if (animal != null) {
            firstRow.setText(animal.getAnimalId() + " - " + animal.getSpecies());
            secondRow.setText(getContext().getString(R.string.animl_search_eid, animal.getEid()));
        }

        return convertView;
    }

    public void submitList(List<AnimalSearchResult> list) {
        Collections.sort(list);
        originalList.clear();
        originalList.addAll(list);

        clear();
        addAll(list);
        notifyDataSetChanged();
    }


    private class CustomFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            results.values = originalList;
            results.count = originalList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List<AnimalSearchResult>) results.values);
            notifyDataSetChanged();
        }
    }
}
