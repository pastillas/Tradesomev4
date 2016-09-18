package com.tradesomev4.tradesomev4.Filters;

import android.widget.Filter;

import com.tradesomev4.tradesomev4.m_Model.MessageRoot;
import com.tradesomev4.tradesomev4.m_UI.MessagesAdapter;

import java.util.ArrayList;

/**
 * Created by Pastillas-Boy on 7/21/2016.
 */
public class MessageFilter extends Filter {
    MessagesAdapter adapter;
    ArrayList<MessageRoot> messageRoots;

    public MessageFilter(MessagesAdapter adapter, ArrayList<MessageRoot> messageRoots) {
        this.adapter = adapter;
        this.messageRoots = messageRoots;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();

        if(constraint != null && constraint.length() > 0){
            adapter.isSearching = true;
            constraint = constraint.toString().toUpperCase();
            ArrayList<MessageRoot>filteredMessageRoots = new ArrayList<>();

            for(int i = 0; i < messageRoots.size(); i++){
                if(messageRoots.get(i).getName().toUpperCase().contains(constraint) ){
                    filteredMessageRoots.add(messageRoots.get(i));
                }
            }

            results.count = filteredMessageRoots.size();
            results.values = filteredMessageRoots;
        }else{
            adapter.isSearching = false;
            results.count = messageRoots.size();
            results.values = messageRoots;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.messageRoots = (ArrayList<MessageRoot>) results.values;
        adapter.notifyDataSetChanged();
    }
}
