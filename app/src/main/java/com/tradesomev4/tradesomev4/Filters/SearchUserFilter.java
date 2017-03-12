package com.tradesomev4.tradesomev4.Filters;

import android.widget.Filter;

import com.tradesomev4.tradesomev4.m_Model.User;
import com.tradesomev4.tradesomev4.m_UI.SearchUserAdapter;

import java.util.ArrayList;


/**
 * Created by Charles Torrente, Jorge Benigno Pante, Joshua Alarcon on 7/22/2016.
 * File Name: SearchUserFilter.java
 * File Path: Tradesomev4\app\src\main\java\com\tradesomev4\tradesomev4\Filters\SearchUserFilter.java
 * Description: Search filter for Users.
 */
public class SearchUserFilter extends Filter {
    SearchUserAdapter adapter;
    ArrayList<User> users;

    public SearchUserFilter(ArrayList<User> users, SearchUserAdapter adapter) {
        this.adapter = adapter;
        this.users = users;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();

        if (constraint != null && constraint.length() > 0) {
            adapter.isSearching = true;
            constraint = constraint.toString().toUpperCase();
            ArrayList<User> filteredUsers = new ArrayList<>();

            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getName().toUpperCase().contains(constraint)) {
                    filteredUsers.add(users.get(i));
                }
            }
            results.count = filteredUsers.size();
            results.values = filteredUsers;
        } else {
            adapter.isSearching = false;
            results.count = users.size();
            results.values = users;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.users = (ArrayList<User>) results.values;
        adapter.notifyDataSetChanged();
    }
}
