package com.tradesomev4.tradesomev4.Filters;

import android.util.Log;
import android.widget.Filter;

import com.tradesomev4.tradesomev4.m_Model.Auction;
import com.tradesomev4.tradesomev4.m_UI.SearchItemAdapter;

import java.util.ArrayList;

/**
 * Created by Pastillas-Boy on 7/28/2016.
 */
public class SearchItemFilter extends Filter {
    ArrayList<Auction> auctions;
    SearchItemAdapter adapter;

    public SearchItemFilter(ArrayList<Auction> auctions, SearchItemAdapter adapter) {
        this.auctions = auctions;
        this.adapter = adapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();

        int indexOf = constraint.toString().indexOf(":");
        String category = constraint.subSequence(0, indexOf).toString();
        String query = constraint.subSequence(indexOf + 1, constraint.length()).toString().toUpperCase();

        ArrayList<Auction> filteredAuctions = new ArrayList<>();

        if (category.equals("All")) {
            if (query != null && query.length() > 0) {
                adapter.isSearching = true;
                Log.d("Query", query);
                for (int i = 0; i < auctions.size(); i++) {
                    if (auctions.get(i).getItemTitle().toUpperCase().contains(query)) {
                        filteredAuctions.add(auctions.get(i));
                    }
                }

                results.count = filteredAuctions.size();
                results.values = filteredAuctions;
            } else {
                adapter.isSearching = false;
                results.count = auctions.size();
                results.values = auctions;
            }
        } else {
            if (query != null && query.length() > 0) {
                adapter.isSearching = true;
                for (int i = 0; i < auctions.size(); i++) {
                    if (auctions.get(i).getCategory().equals(category) && auctions.get(i).getItemTitle().toUpperCase().contains(query)) {
                        filteredAuctions.add(auctions.get(i));
                    }
                }
                results.count = filteredAuctions.size();
                results.values = filteredAuctions;

            } else {
                adapter.isSearching = false;
                for (int i = 0; i < auctions.size(); i++) {
                    if (auctions.get(i).getCategory().equals(category)) {
                        filteredAuctions.add(auctions.get(i));
                    }
                }
                results.count = filteredAuctions.size();
                results.values = filteredAuctions;
            }
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.auctions = (ArrayList<Auction>) results.values;
        adapter.notifyDataSetChanged();
    }
}
