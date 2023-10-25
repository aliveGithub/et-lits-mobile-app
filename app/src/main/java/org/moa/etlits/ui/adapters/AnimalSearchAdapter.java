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
            List<AnimalSearchResult> suggestions = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(originalList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (AnimalSearchResult item : originalList) {
                    if (item.getAnimalId().toLowerCase().trim().contains(filterPattern)) {
                        suggestions.add(item);
                    }
                }
            }

            results.values = suggestions;
            results.count = suggestions.size();
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
