package com.tradesomev4.tradesomev4.AuctionBidFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.tradesomev4.tradesomev4.AuctionYourStuff;
import com.tradesomev4.tradesomev4.R;
import com.tradesomev4.tradesomev4.m_UI.MyAuctionsAdapter;

/**
 * Created by Pastillas-Boy on 7/24/2016.
 */
public class MyAuctionsFragment extends Fragment implements View.OnClickListener{
    private RecyclerView rv;
    private MyAuctionsAdapter adapter;
    boolean isAttache;
    TextView tv_items_here;
    TextView tv_internet_connection;
    ProgressWheel progress_wheel;
    FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_auctions, container, false);

        tv_items_here = (TextView)view.findViewById(R.id.tv_items_here);
        tv_internet_connection = (TextView)view.findViewById(R.id.tv_internet_connection);
        progress_wheel = (ProgressWheel)view.findViewById(R.id.progress_wheel);
        fab = (FloatingActionButton)view.findViewById(R.id.fab);

        fab.setOnClickListener(this);

        tv_items_here.setVisibility(View.GONE);
        tv_internet_connection.setVisibility(View.GONE);

        onAttach(getContext());
            isAttache = true;

        rv =(RecyclerView)view.findViewById(R.id.rv_my_auctions);
        adapter = new MyAuctionsAdapter(getContext(), isAttache, rv, Glide.with(this), tv_items_here, tv_internet_connection, progress_wheel, view);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab:
                Intent intent = new Intent(getActivity(), AuctionYourStuff.class);
                startActivity(intent);
                break;
        }
    }
}
