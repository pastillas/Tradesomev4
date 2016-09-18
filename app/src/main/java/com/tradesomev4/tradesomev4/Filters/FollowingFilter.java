package com.tradesomev4.tradesomev4.Filters;

import android.widget.Filter;

import com.tradesomev4.tradesomev4.m_Model.Following;
import com.tradesomev4.tradesomev4.m_UI.FollowingAdapter;

import java.util.ArrayList;

/**
 * Created by Pastillas-Boy on 7/22/2016.
 */
public class FollowingFilter extends Filter {
    ArrayList<Following>followings;
    FollowingAdapter adapter;

    public FollowingFilter(ArrayList<Following>followings, FollowingAdapter adapter){
        this.followings = followings;
        this.adapter = adapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();

        if(constraint != null && constraint.length() > 0){
            adapter.isSearching = true;
            constraint = constraint.toString().toUpperCase();
            ArrayList<Following>filteredFollowings = new ArrayList<>();

            for(int i = 0; i < followings.size(); i++){
                if(followings.get(i).getName().toUpperCase().contains(constraint)){
                    filteredFollowings.add(followings.get(i));
                }
            }
            results.count = filteredFollowings.size();
            results.values = filteredFollowings;

        }else{
            adapter.isSearching = false;
            results.count = followings.size();
            results.values = followings;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.followings = (ArrayList<Following>) results.values;
        adapter.notifyDataSetChanged();
    }
}
