package com.tradesomev4.tradesomev4;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.tradesomev4.tradesomev4.m_Helpers.DateHelper;
import com.tradesomev4.tradesomev4.m_Model.UserMessage;
import com.tradesomev4.tradesomev4.m_UI.SendUserMesageAdapter;

import java.util.HashMap;
import java.util.Map;

public class SendUserMessage extends AppCompatActivity implements View.OnClickListener{

    private static final String EXTRAS_POSTER_ID = "POSTER_ID";
    private static final String EXTRAS_BUNDLE = "EXTRAS_BUNDLE";

    private static final String USER_NAME_KEY = "USER_NAME";
    private static final String USER_IMAGE_KEY = "USER_IMAGE";
    private static final String USER_ID_KEY = "USER_KEY";
    private static final String BUNDLE_EXTRA_KEY = "BUNDLE_EXTRAS";
    private Bundle extras;
    public RecyclerView recyclerView;
    private SendUserMesageAdapter adapter;
    private FirebaseUser fUser;
    private DatabaseReference mDatabase;
    private FloatingActionButton fab;
    Toolbar toolbar;
    private static final int messagesLimit = 200;
    private long messageCount;
    TextView tv_message;
    TextView tv_items_here;
    TextView tv_internet_connection;
    ProgressWheel progress_wheel;
    View content_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_send_user_message);

        initViewDb();
        messageCounter();
    }

    public void messageCounter(){
        mDatabase.child("messages").child(fUser.getUid()).child(extras.getString(USER_ID_KEY)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messageCount = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initViewDb() {
        toolbar = (Toolbar) findViewById(R.id.app_bar_messaging);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        content_main = findViewById(R.id.content_main);
        tv_items_here = (TextView) findViewById(R.id.tv_items_here);
        tv_internet_connection = (TextView) findViewById(R.id.tv_internet_connection);
        progress_wheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        tv_items_here.setVisibility(View.GONE);
        tv_internet_connection.setVisibility(View.GONE);

        extras = getIntent().getBundleExtra(BUNDLE_EXTRA_KEY);
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        String name = extras.getString(USER_NAME_KEY);
        getSupportActionBar().setTitle(name);

        boolean isAttache;
        onAttachedToWindow();
        isAttache = true;

        recyclerView = (RecyclerView) findViewById(R.id.rv_send_user_message);
        adapter = new SendUserMesageAdapter(this, extras.getString(USER_IMAGE_KEY), extras.getString(USER_ID_KEY), recyclerView, isAttache, Glide.with(this), tv_items_here, tv_internet_connection, progress_wheel, content_main);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.bt_send).setOnClickListener(this);
        tv_message = (TextView)findViewById(R.id.tv_message);
        tv_message.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_send_message_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.search:
                Bundle args = new Bundle();
                args.putString(EXTRAS_POSTER_ID, extras.getString(USER_ID_KEY));
                Intent intent = new Intent(getApplicationContext(), ViewUserProfile.class);
                intent.putExtra(EXTRAS_BUNDLE, args);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendMessage() {
        if(messageCount >= messagesLimit){
            Dialog d = new MaterialDialog.Builder(this)
                    .title("Messages limit")
                    .content("Sorry, Please delete some messages.")
                    .positiveText(R.string.continueBtn)
                    .show();
        }else{
            String messageStr = tv_message.getText().toString();
            if(!TextUtils.isEmpty(messageStr)) {
                String key = mDatabase.child("messages").child(fUser.getUid()).child(extras.getString(USER_ID_KEY)).push().getKey();
                UserMessage message = new UserMessage(messageStr, DateHelper.getCurrentDateInMil(), fUser.getUid(), key, true);
                Map<String, Object> messageValues = message.toMap();
                Map<String, Object> childUpdate = new HashMap<>();
                childUpdate.put("/messages/" + fUser.getUid() + "/" + extras.getString(USER_ID_KEY) + "/" + key, messageValues);
                mDatabase.updateChildren(childUpdate);

                key = mDatabase.child("messages").child(extras.getString(USER_ID_KEY)).child(fUser.getUid()).push().getKey();
                message = new UserMessage(messageStr, DateHelper.getCurrentDateInMil(), fUser.getUid(), key, false);
                messageValues = message.toMap();
                childUpdate = new HashMap<>();
                childUpdate.put("/messages/" + extras.getString(USER_ID_KEY) + "/" + fUser.getUid() + "/" + key, messageValues);
                mDatabase.updateChildren(childUpdate);

                tv_message.setText(null);
            }else
                tv_message.setError("Type message first.");
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_send:
                sendMessage();
                break;
        }
    }
}