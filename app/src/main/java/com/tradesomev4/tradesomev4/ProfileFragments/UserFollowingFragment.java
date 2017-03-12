package com.tradesomev4.tradesomev4.ProfileFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tradesomev4.tradesomev4.R;
import com.tradesomev4.tradesomev4.m_UI.UserFollowingAdapter;

/**
 * Created by Jorge Benigno Pante, Charles Torrente, Joshua Alarcon on 7/17/2016.
 * File name: UserFollowingFragment.java
 * File Path: Tradesomev4\app\src\main\java\com\tradesomev4\tradesomev4\ProfileFragments\UserFollowingFragment.java
 * Description: View other user's following.
 */
public class UserFollowingFragment extends Fragment {
    private static final String EXTRAS_POSTER_ID = "POSTER_ID";
    private static final String EXTRAS_BUNDLE = "EXTRAS_BUNDLE";
    private SearchView sv;
    private RecyclerView rv;
    UserFollowingAdapter adapter;
    private Bundle extras;
    boolean isAttache;
    TextView tv_items_here;
    TextView tv_internet_connection;
    ProgressBar progress_wheel;

    public static UserFollowingFragment getInstance(Bundle extras) {
        UserFollowingFragment userFollowingFragment = new UserFollowingFragment();
        userFollowingFragment.setArguments(extras);
        return userFollowingFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_following, container, false);

        tv_items_here = (TextView)view.findViewById(R.id.tv_items_here);
        tv_internet_connection = (TextView)view.findViewById(R.id.tv_internet_connection);
        progress_wheel = (ProgressBar) view.findViewById(R.id.progress_wheel);
        tv_items_here.setVisibility(View.GONE);
        tv_internet_connection.setVisibility(View.GONE);

        onAttach(getContext());
        isAttache = true;

        extras = getArguments();

        sv = (SearchView)view.findViewById(R.id.sv_following);
        rv = (RecyclerView)view.findViewById(R.id.rv_following);
        adapter = new UserFollowingAdapter(getContext(), extras.getString(EXTRAS_POSTER_ID),  isAttache, rv, Glide.with(this), tv_items_here, tv_internet_connection, progress_wheel, view);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });


        return view;
    }
}
