package org.moa.etlits.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import org.moa.etlits.R;
import org.moa.etlits.data.models.Establishment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EstablishmentSearchAdapter extends ArrayAdapter<Establishment> {
    private List<Establishment> originalList;
    private CustomFilter filter;

    public EstablishmentSearchAdapter(Context context, ArrayList<Establishment> establishments) {
        super(context, 0, establishments);
        originalList = new ArrayList<>(establishments);
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

        Establishment establishment = getItem(position);
        TextView establishmentText = convertView.findViewById(R.id.tv_autocomplete_result_item);

        if (establishment != null) {
            establishmentText.setText(establishment.toString());
        }

        return convertView;
    }

    public void submitList(List<Establishment> list) {
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
            List<Establishment> suggestions = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(originalList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Establishment item : originalList) {
                    if (item.getCodeAndName().toLowerCase().trim().contains(filterPattern)) {
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
            addAll((List<Establishment>) results.values);
            notifyDataSetChanged();
        }
    }
}
