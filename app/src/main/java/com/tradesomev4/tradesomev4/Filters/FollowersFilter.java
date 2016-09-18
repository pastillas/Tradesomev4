package com.tradesomev4.tradesomev4.Filters;

import android.widget.Filter;

import com.tradesomev4.tradesomev4.m_Model.Follower;
import com.tradesomev4.tradesomev4.m_UI.FollowersAdapter;

import java.util.ArrayList;

/**
 * Created by Pastillas-Boy on 7/22/2016.
 */
public class FollowersFilter extends Filter {
    ArrayList<Follower>followers;
    FollowersAdapter adapter;

    public FollowersFilter(ArrayList<Follower>followers, FollowersAdapter adapter){
        this.followers = followers;
        this.adapter = adapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();

        if(constraint != null && constraint.length() > 0){
            adapter.isSearching = true;
            constraint = constraint.toString().toUpperCase();
            ArrayList<Follower>filteredFollowers = new ArrayList<>();

            for(int i = 0; i < followers.size(); i++){
                if(followers.get(i).getName().toUpperCase().contains(constraint)){
                    filteredFollowers.add(followers.get(i));
                }
            }

            results.count = filteredFollowers.size();
            results.values = filteredFollowers;

        }else{
            adapter.isSearching = false;
            results.count = followers.size();
            results.values = followers;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.followers = (ArrayList<Follower>) results.values;
        adapter.notifyDataSetChanged();
    }
}
