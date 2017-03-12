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
import android.widget.Toast;

import com.bumptech.glide.RequestManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.tradesomev4.tradesomev4.R;
import com.tradesomev4.tradesomev4.m_Helpers.CalendarUtils;
import com.tradesomev4.tradesomev4.m_Helpers.Connectivity;
import com.tradesomev4.tradesomev4.m_Helpers.SnackBars;
import com.tradesomev4.tradesomev4.m_Model.UserMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pastillas-Boy on 7/19/2016.
 */
public class SendUserMesageAdapter extends RecyclerView.Adapter<SendUserMesageAdapter.UserViewHolder> {
    private static final String DEBUG_TAG = "DEBUG_TAG";
    Context context;
    LayoutInflater inflater;
    ArrayList<UserMessage> messages;
    DatabaseReference mDatabase;
    FirebaseUser fUser;
    String senderImageUrl;
    String senderId;
    int prevPos = 0;
    private final static int HEADER_VIEW = 0;
    private final static int CONTENT_VIEW = 1;
    boolean isAttached;
    RecyclerView recyclerView;
    RequestManager glide;
    TextView tv_items_here;
    TextView tv_internet_connection;
    ProgressWheel progress_wheel;
    boolean isConnected;
    boolean isConnectionDisabledShowed;
    boolean isConnectionRestoredShowed;
    SnackBars snackBars;
    CountDownTimer timeOuttimer;
    int puta;
    View parentView;


    public void timeOut() {
        timeOuttimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long l) {
                if (messages.size() > 0) {
                    hideAll();
                }
            }

            @Override
            public void onFinish() {
                if (isConnected && messages.size() == 0)
                    showItemsHere();
            }
        };

        timeOuttimer.start();
    }

    public void timer() {
        final CountDownTimer c = new CountDownTimer(1000, 1000) {

            @Override
            public void onTick(long l) {
                Log.d(DEBUG_TAG, "TIMER: " + l);
            }

            public void onFinish() {
                Connectivity connectivity = new Connectivity(context.getApplicationContext());

                if (!connectivity.isConnected()) {
                    isConnectionRestoredShowed = false;
                    isConnected = false;

                    if (puta == 1)
                        puta++;

                    if (!isConnectionDisabledShowed) {
                        snackBars.showConnectionDisabledDialog();
                        isConnectionDisabledShowed = true;
                    }

                    if (timeOuttimer != null)
                        timeOuttimer.cancel();

                    showConnectionError();
                } else {
                    isConnected = true;
                    isConnectionDisabledShowed = false;

                    if (puta != 1 && !isConnectionRestoredShowed) {
                        snackBars.showConnectionRestored();
                        isConnectionRestoredShowed = true;
                    }
                    if (messages.size() == 0 && puta == 2) {
                        Log.d(DEBUG_TAG, "PUTA 2: TRUE");
                        hideAll();
                        showLoading();
                        puta--;
                        timeOut();
                    }
                }

                if (messages.size() > 0) {
                    //hideAll();
                }

                timer();
            }
        }.start();
    }

    public void hideAll() {
        if (progress_wheel.getVisibility() == View.VISIBLE)
            progress_wheel.setVisibility(View.GONE);

        if (tv_items_here.getVisibility() == View.VISIBLE)
            tv_items_here.setVisibility(View.GONE);

        if (tv_internet_connection.getVisibility() == View.VISIBLE)
            tv_internet_connection.setVisibility(View.GONE);
    }

    public void showItemsHere() {
        if (progress_wheel.getVisibility() == View.VISIBLE)
            progress_wheel.setVisibility(View.GONE);

        if (tv_internet_connection.getVisibility() == View.VISIBLE)
            tv_internet_connection.setVisibility(View.GONE);

        if (tv_items_here.getVisibility() == View.GONE)
            tv_items_here.setVisibility(View.VISIBLE);
    }

    public void showLoading() {
        if (progress_wheel.getVisibility() == View.GONE)
            progress_wheel.setVisibility(View.VISIBLE);

        if (tv_internet_connection.getVisibility() == View.VISIBLE)
            tv_internet_connection.setVisibility(View.GONE);

        if (tv_items_here.getVisibility() == View.VISIBLE)
            tv_items_here.setVisibility(View.GONE);
    }

    public void showConnectionError() {
        if (progress_wheel.getVisibility() == View.VISIBLE)
            progress_wheel.setVisibility(View.GONE);

        if (tv_items_here.getVisibility() == View.VISIBLE)
            tv_items_here.setVisibility(View.GONE);

        if (tv_internet_connection.getVisibility() == View.GONE && messages.size() == 0)
            tv_internet_connection.setVisibility(View.VISIBLE);

    }

    public void addMessage(UserMessage message) {
        messages.add(message);
        notifyItemInserted(getItemCount());
        mDatabase.child("messages").child(fUser.getUid()).child(senderId).child(message.getKey()).child("read").setValue(true);
        recyclerView.smoothScrollToPosition(getItemCount());
    }

    public SendUserMesageAdapter(final Context context, String senderImageUrl, String senderId, RecyclerView recyclerView, boolean isAttached, RequestManager glide,
                                 final TextView tv_items_here, final TextView tv_internet_connection, final ProgressWheel progress_wheel, View view) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        messages = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        this.senderImageUrl = senderImageUrl;
        this.senderId = senderId;
        this.recyclerView = recyclerView;
        this.isAttached = isAttached;
        this.glide = glide;
        this.tv_items_here = tv_items_here;
        this.tv_internet_connection = tv_internet_connection;
        this.progress_wheel = progress_wheel;
        isConnectionDisabledShowed = false;
        isConnectionRestoredShowed = false;
        snackBars = new SnackBars(view, context.getApplicationContext());
        puta = 1;
        parentView = view;
        initSwipe();

        Query messagesRef = mDatabase.child("messages").child(fUser.getUid()).child(senderId).limitToLast(50);
        messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                UserMessage message = dataSnapshot.getValue(UserMessage.class);
                addMessage(message);
                Log.d(DEBUG_TAG, "PUTA MESSAGE!!: " + message.getMessage());
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
                Toast.makeText(context, "Message Not Sent", Toast.LENGTH_LONG).show();
            }
        });

        timer();
        timeOut();
    }


    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        UserViewHolder holder = null;
        int layoutRes = 0;
        switch (viewType) {
            case HEADER_VIEW:
                layoutRes = R.layout.current_user_view_model;
                view = inflater.inflate(layoutRes, parent, false);
                holder = new CurrentUserHolder(view);
                break;
            case CONTENT_VIEW:
                layoutRes = R.layout.sender_user_view_model;
                view = inflater.inflate(layoutRes, parent, false);
                holder = new SenderUserHolder(view);
                break;
        }

        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        Log.d("pos", String.valueOf(position));
        UserMessage message = messages.get(position);

        if (fUser.getUid().equals(message.getSenderId()))
            return HEADER_VIEW;
        else
            return CONTENT_VIEW;

       /* switch(fUser.getUid()) {
            case message.getSenderId():
                return HEADER_VIEW;
            default:
                return CONTENT_VIEW;
        }*/
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, final int position) {
        if (getItemViewType(position) == HEADER_VIEW) {
            CurrentUserHolder mHolder = (CurrentUserHolder) holder;
            mHolder.message.setText(messages.get(position).getMessage());

            String date = CalendarUtils.ConvertMilliSecondsToFormattedDate(messages.get(position).getSendDate());
            mHolder.dateSent.setText(date);

            if (position > prevPos)
                AnimationUtil.animate(mHolder, true);
            else
                AnimationUtil.animate(mHolder, false);

            mHolder.cont.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    return true;
                }
            });
        } else {
            SenderUserHolder mHolder = (SenderUserHolder) holder;
            mHolder.message.setText(messages.get(position).getMessage());

            mHolder.message.setText(messages.get(position).getMessage());
            String date = CalendarUtils.ConvertMilliSecondsToFormattedDate(messages.get(position).getSendDate());
            mHolder.dateSent.setText(date);

            if (isAttached) {
                glide.load(senderImageUrl)
                        .asBitmap().centerCrop()
                        .into(mHolder.senderImage);
            }

            if (position > prevPos)
                AnimationUtil.animate(mHolder, true);
            else
                AnimationUtil.animate(mHolder, false);
        }

        prevPos = position;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public abstract class UserViewHolder extends RecyclerView.ViewHolder {
        public UserViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class SenderUserHolder extends UserViewHolder {
        public TextView message;
        public TextView dateSent;
        public ImageView senderImage;
        public final View mView;
        public View contSender;

        public SenderUserHolder(View itemView) {
            super(itemView);
            mView = itemView;
            contSender = itemView.findViewById(R.id.cont_sender);
            message = (TextView) mView.findViewById(R.id.tv_message);
            dateSent = (TextView) mView.findViewById(R.id.tv_send_date);
            senderImage = (ImageView) mView.findViewById(R.id.iv_send_image);
        }
    }

    public class CurrentUserHolder extends UserViewHolder {
        public TextView message;
        public TextView dateSent;
        public final View mView;
        public View cont;
        ;

        public CurrentUserHolder(View itemView) {
            super(itemView);

            mView = itemView;
            cont = mView.findViewById(R.id.cont_current);
            message = (TextView) mView.findViewById(R.id.tv_message);
            dateSent = (TextView) mView.findViewById(R.id.tv_send_date);
        }
    }

    public void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int pos = viewHolder.getAdapterPosition();
                parentView.setTag(pos);
                final UserMessage userMessage = messages.get(pos);
                //Snackbar.make(view, R.string.notice_removed, Snackbar.LENGTH_SHORT).show();

                messages.remove(pos);
                notifyItemRemoved(pos);
                notifyItemRangeChanged(pos, getItemCount());
                Snackbar.make(parentView, "Done.", Snackbar.LENGTH_LONG)
                        .setCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                try {
                                    if (event != DISMISS_EVENT_ACTION && messages.size() != 0) {
                                        Log.d(DEBUG_TAG, "ONDISMESSED: TRUE" + userMessage.getKey());
                                        Map<String, Object> children = new HashMap<String, Object>();
                                        children.put("/messages/" + fUser.getUid() + "/" + senderId + "/" + userMessage.getKey(), null);

                                        mDatabase.updateChildren(children);

                                        if (messages.size() == 0)
                                            showItemsHere();
                                    }

                                } catch (IndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                messages.add(pos, userMessage);
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
