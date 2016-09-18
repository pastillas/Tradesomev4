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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.tradesomev4.tradesomev4.R;
import com.tradesomev4.tradesomev4.m_UI.FollowingAdapter;

/**
 * Created by Pastillas-Boy on 7/22/2016.
 */
public class FollowingFragment extends Fragment {
    private SearchView sv;
    private RecyclerView rv;
    FollowingAdapter adapter;
    FirebaseUser fUser;
    boolean isAttache;
    TextView tv_items_here;
    TextView tv_internet_connection;
    ProgressBar progress_wheel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_following, container, false);
        fUser = FirebaseAuth.getInstance().getCurrentUser();


        tv_items_here = (TextView)view.findViewById(R.id.tv_items_here);
        tv_internet_connection = (TextView)view.findViewById(R.id.tv_internet_connection);
        progress_wheel = (ProgressBar)view.findViewById(R.id.progress_wheel);
        tv_items_here.setVisibility(View.GONE);
        tv_internet_connection.setVisibility(View.GONE);

        onAttach(getContext());
        isAttache = true;

        sv = (SearchView)view.findViewById(R.id.sv_following);
        rv = (RecyclerView)view.findViewById(R.id.rv_following);
        adapter = new FollowingAdapter(getContext(), fUser.getUid(),  isAttache, rv, Glide.with(this), tv_items_here, tv_internet_connection, progress_wheel, view);
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
