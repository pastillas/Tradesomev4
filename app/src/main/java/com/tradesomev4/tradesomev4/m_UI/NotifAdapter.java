package com.tradesomev4.tradesomev4.m_UI;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.tradesomev4.tradesomev4.R;
import com.tradesomev4.tradesomev4.m_Helpers.CalendarUtils;
import com.tradesomev4.tradesomev4.m_Helpers.Connectivity;
import com.tradesomev4.tradesomev4.m_Helpers.SnackBars;
import com.tradesomev4.tradesomev4.m_Model.Notif;

import java.util.ArrayList;

/**
 * Created by Pastillas-Boy on 7/17/2016.
 */
public class NotifAdapter extends RecyclerView.Adapter <NotifAdapter.NotifHolder> {
    private static final String DEBUG_TAG = "DEBUG_TAG";
    ArrayList <Notif> notifs;
    Context context;
    LayoutInflater inflater;
    String userId;
    DatabaseReference mDatabase;
    int prevPos = 0;
    boolean isAttached;
    RecyclerView recyclerView;
    TextView tv_items_here;
    TextView tv_internet_connection;
    ProgressWheel progress_wheel;
    RequestManager glide;
    boolean isConnected;
    boolean isConnectionDisabledShowed;
    boolean isConnectionRestoredShowed;
    SnackBars snackBars;
    CountDownTimer timeOuttimer;
    View parentView;
    int puta;


    public void timeOut(){
        timeOuttimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long l) {
                if(notifs.size() > 0){
                    hideAll();
                }
            }

            @Override
            public void onFinish() {
                if(isConnected && notifs.size() == 0)
                    showItemsHere();
            }
        };

        timeOuttimer.start();
    }


    public void timer(){
        final CountDownTimer c = new CountDownTimer(1000, 1000) {

            @Override
            public void onTick(long l) {
                Log.d(DEBUG_TAG, "TIMER: " + l);
            }

            public void onFinish() {
                Connectivity connectivity = new Connectivity(context.getApplicationContext());

                if(!connectivity.isConnected()) {
                    isConnectionRestoredShowed = false;
                    isConnected = false;

                    if(puta == 1)
                        puta++;

                    if(!isConnectionDisabledShowed){
                        snackBars.showConnectionDisabledDialog();
                        isConnectionDisabledShowed = true;
                    }

                    if(timeOuttimer != null)
                        timeOuttimer.cancel();

                    showConnectionError();
                } else {
                    isConnected = true;
                    isConnectionDisabledShowed = false;

                    if(puta != 1 && !isConnectionRestoredShowed){
                        snackBars.showConnectionRestored();
                        isConnectionRestoredShowed = true;
                    }
                    if(notifs.size() == 0){
                        hideAll();
                        showLoading();
                        timeOut();
                    }
                }

                if(notifs.size() > 0){
                    hideAll();
                }

                timer();
            }
        }.start();
    }

    public void hideAll(){
        if(progress_wheel.getVisibility() == View.VISIBLE)
            progress_wheel.setVisibility(View.GONE);

        if(tv_items_here.getVisibility() == View.VISIBLE)
            tv_items_here.setVisibility(View.GONE);

        if(tv_internet_connection.getVisibility() == View.VISIBLE)
            tv_internet_connection.setVisibility(View.GONE);
    }

    public void showItemsHere(){
        if(progress_wheel.getVisibility() == View.VISIBLE)
            progress_wheel.setVisibility(View.GONE);

        if(tv_internet_connection.getVisibility() == View.VISIBLE)
            tv_internet_connection.setVisibility(View.GONE);

        if(tv_items_here.getVisibility() == View.GONE)
            tv_items_here.setVisibility(View.VISIBLE);
    }

    public void showLoading(){
        if(progress_wheel.getVisibility() == View.GONE)
            progress_wheel.setVisibility(View.VISIBLE);

        if(tv_internet_connection.getVisibility() == View.VISIBLE)
            tv_internet_connection.setVisibility(View.GONE);

        if(tv_items_here.getVisibility() == View.VISIBLE)
            tv_items_here.setVisibility(View.GONE);
    }

    public void showConnectionError(){
        if(progress_wheel.getVisibility() == View.VISIBLE)
            progress_wheel.setVisibility(View.GONE);

        if(tv_items_here.getVisibility() == View.VISIBLE)
            tv_items_here.setVisibility(View.GONE);

        if(tv_internet_connection.getVisibility() == View.GONE && notifs.size() == 0)
            tv_internet_connection.setVisibility(View.VISIBLE);

    }

    public NotifAdapter(Context context, String userId, boolean isAttached, RecyclerView recyclerView, RequestManager glide,
                        final TextView tv_items_here, final TextView tv_internet_connection, final ProgressWheel progress_wheel, View view) {
        this.notifs = new ArrayList<>();
        this.context = context;
        this.userId = userId;
        this.inflater = LayoutInflater.from(context);
        this.isAttached = isAttached;
        this.recyclerView = recyclerView;
        this.glide = glide;
        this.tv_items_here = tv_items_here;
        this.tv_internet_connection= tv_internet_connection;
        this.progress_wheel = progress_wheel;
        isConnectionDisabledShowed = false;
        isConnectionRestoredShowed = false;
        parentView = view;
        snackBars = new SnackBars(view, context.getApplicationContext());
        puta = 1;

        initSwipe();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("notifications").child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Notif notif = dataSnapshot.getValue(Notif.class);
                addItem(0, notif);

                hideAll();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        timer();
        timeOut();
    }

    public void addItem(int position, Notif notif){
        notifs.add(position, notif);
        notifyItemInserted(position);
    }

    @Override
    public NotifHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = inflater.inflate(R.layout.notif_model, parent, false);
        NotifHolder notifHolder = new NotifHolder(view);


        if (position > prevPos)
            AnimationUtil.animate(notifHolder, true);
        else
            AnimationUtil.animate(notifHolder, false);

        return notifHolder;
    }

    @Override
    public void onBindViewHolder(NotifHolder holder, final int position) {
        if(notifs.get(position).getNotifType().equals("alert")){
            holder.appAvatar.setImageResource(R.drawable.ic_report_problem_gray_36dp);
        }else
            holder.appAvatar.setImageResource(R.drawable.logo);

        holder.title.setText(notifs.get(position).getContent());
        holder.subTitle.setText(CalendarUtils.ConvertMilliSecondsToFormattedDate(notifs.get(position).getDate()));

        if (position > prevPos)
            AnimationUtil.animate(holder, true);
        else
            AnimationUtil.animate(holder, false);

        prevPos = position;
        final int currentPosition = position;
    }

    @Override
    public int getItemCount() {
        return notifs.size();
    }

    class NotifHolder extends RecyclerView.ViewHolder{
        ImageView appAvatar;
        TextView title;
        TextView subTitle;
        View container;

        public NotifHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.cont_notif_root);
            appAvatar = (ImageView) itemView.findViewById(R.id.iv_app_avatar);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            subTitle = (TextView) itemView.findViewById(R.id.tv_subtitle);
        }
    }

    public void initSwipe(){
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int pos = viewHolder.getAdapterPosition();
                parentView.setTag(pos);
                final Notif notif= notifs.get(pos);
                notifs.remove(pos);
                notifyItemRemoved(pos);
                //Snackbar.make(view, R.string.notice_removed, Snackbar.LENGTH_SHORT).show();
                Snackbar.make(parentView, "Bid log will be deleted.", Snackbar.LENGTH_LONG)
                        .setCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                //Log.d(DEBUG_TAG, "ONDISMESSED: TRUE" + notif.getId());
                                if(isConnected){
                                        //mDatabase.child("bidHistory").child(fUser.getUid()).child(bid.getId()).setValue(null);
                                }else{
                                    notifs.add(pos, notif);
                                    notifyItemInserted(pos);
                                    snackBars.showCheckYourConnection();
                                }
                            }
                        })
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                notifs.add(pos, notif);
                                notifyItemInserted(pos);
                            }
                        })
                        .show();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}
