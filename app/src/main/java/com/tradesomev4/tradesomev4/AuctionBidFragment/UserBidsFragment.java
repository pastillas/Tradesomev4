package com.tradesomev4.tradesomev4.AuctionBidFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.tradesomev4.tradesomev4.R;
import com.tradesomev4.tradesomev4.m_UI.UserBidsAdapter;


/**
 * Created by Jorge Benigno Pante, Joshua Alarcon, Charles Torrente on 7/24/2016.
 * File Name: UserBidsFragment.java
 * File Path: H:\SP\Tradesomev4\app\src\main\java\com\tradesomev4\tradesomev4\AuctionBidFragment\UserBidsFragment.java
 * Description: fetch and display all the user's current bade items.
 */
public class UserBidsFragment extends Fragment {

    private static final String USER_NAME_KEY = "USER_NAME";
    private static final String USER_IMAGE_KEY = "USER_IMAGE";
    private static final String USER_ID_KEY = "USER_KEY";
    private static final String BUNDLE_EXTRA_KEY = "BUNDLE_EXTRAS";
    private RecyclerView rv;
    private UserBidsAdapter adapter;
    private Bundle args;
    boolean isAttache;
    TextView tv_items_here;
    TextView tv_internet_connection;
    ProgressWheel progress_wheel;

    public static UserBidsFragment getInstance(Bundle args){
        UserBidsFragment userBidsFragment = new UserBidsFragment();
        userBidsFragment.setArguments(args);
        return userBidsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_bids, container, false);

        args= getArguments();

        tv_items_here = (TextView)view.findViewById(R.id.tv_items_here);
        tv_internet_connection = (TextView)view.findViewById(R.id.tv_internet_connection);
        progress_wheel = (ProgressWheel)view.findViewById(R.id.progress_wheel);

        tv_items_here.setVisibility(View.GONE);
        tv_internet_connection.setVisibility(View.GONE);

        onAttach(getContext());
        isAttache = true;

        rv = (RecyclerView)view.findViewById(R.id.rv_my_bids);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UserBidsAdapter(getContext(), args.getString(USER_ID_KEY), isAttache, rv, Glide.with(this), tv_items_here, tv_internet_connection, progress_wheel, view);
        rv.setAdapter(adapter);

        return view;
    }
}
