package com.tradesomev4.tradesomev4;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.tradesomev4.tradesomev4.BackgroundProcesses.LocationService;
import com.tradesomev4.tradesomev4.m_Helpers.IsBlockedListener;
import com.tradesomev4.tradesomev4.m_Helpers.Keys;
import com.tradesomev4.tradesomev4.m_Helpers.RecyclerViewLayoutManager;
import com.tradesomev4.tradesomev4.BackgroundProcesses.ServiceBroadcastReceiver;
import com.tradesomev4.tradesomev4.m_Model.User;
import com.tradesomev4.tradesomev4.m_UI.AuctionAdapter;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String DEBUG_TAG = "DEBUG_TAG";
    private ImageView profilePicture;
    private TextView nameT;
    private TextView emailT;
    private FirebaseUser fUser;
    private DatabaseReference mDatabase;
    private User user;
    private RecyclerView recyclerView;
    AuctionAdapter adapter;
    TextView tv_items_here;
    TextView tv_internet_connection;
    ProgressWheel progress_wheel;
    View content_main;
    private GoogleApiClient mGoogleApiClient;
    NavigationView navigationView;
    SwipeRefreshLayout mSwipeRefreshLayout;

    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        //createLocationRequest();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(MainActivity.this, 1);
                        } catch (IntentSender.SendIntentException e) {

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;

                    default:
                }
            }
        });
    }

    public void initGoogleApi() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initGoogleApi();
        initUserDb();

        initViews(toolbar);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getApplicationContext(), R.color.orange),
                ContextCompat.getColor(getApplicationContext(), R.color.green),
                ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initRecyclerView();

                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 5000);
            }
        });

        new IsBlockedListener(this, false, fUser.getUid());
        startService(new Intent(getBaseContext(), LocationService.class));
        Intent intent = new Intent(this, ServiceBroadcastReceiver.class);
        boolean isRunning = (PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_NO_CREATE) != null);
        if (isRunning == false) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 1000, pendingIntent);
        }
        //startService(new Intent(getBaseContext(), CurrentUserNotifications.class));
        //startService(new Intent(getBaseContext(), BaseNotificationManager.class));
    }

    public void initUserDb() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        if (fUser == null) {
            Intent intent = new Intent(getApplicationContext(), CreateAccount.class);
            startActivity(intent);
        }

        user = new User();
        user.setName(fUser.getDisplayName());
        user.setId(fUser.getUid());
        user.setEmail(fUser.getEmail());
        user.setImage(fUser.getPhotoUrl().toString());
    }

    public void initRecyclerView() {
        boolean isAttached;
        onAttachedToWindow();
        isAttached = true;

        try {
            recyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
            recyclerView.setHasFixedSize(true);
            adapter = new AuctionAdapter(this, fUser, isAttached, recyclerView, Glide.with(this), tv_items_here, tv_internet_connection, progress_wheel, content_main);
            recyclerView.setLayoutManager(new RecyclerViewLayoutManager(this));
            recyclerView.setAdapter(adapter);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }


    public void initViews(Toolbar toolbar) {
        findViewById(R.id.fab).setOnClickListener(this);
        content_main = findViewById(R.id.content_main);
        tv_items_here = (TextView) findViewById(R.id.tv_items_here);
        tv_internet_connection = (TextView) findViewById(R.id.tv_internet_connection);
        progress_wheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        tv_items_here.setVisibility(View.GONE);
        tv_internet_connection.setVisibility(View.GONE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View v = navigationView.getHeaderView(0);
        emailT = (TextView) v.findViewById(R.id.name);
        nameT = (TextView) v.findViewById(R.id.email);
        profilePicture = (ImageView) v.findViewById(R.id.profilePicture);

        initRecyclerView();

        emailT.setText(user.getEmail());
        nameT.setText(user.getName());
        Glide.with(this)
                .load(user.getImage())
                .asBitmap().centerCrop()
                .error(R.drawable.ic_account_circle_black_24dp)
                .into(profilePicture);
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.search) {
            Bundle bundle = new Bundle();
            bundle.putString(Keys.CATEGORY_KEY, "All");
            Intent intent = new Intent(getApplicationContext(), SearchItem.class);
            intent.putExtra(Keys.BUNDLE_KEY, bundle);
            startActivity(intent);
        }

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //  return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int item = v.getId();

        switch (item) {
            case R.id.fab:
                Intent auctionYourStuffIntent = new Intent(getApplicationContext(), AuctionYourStuff.class);
                startActivity(auctionYourStuffIntent);
                break;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
                Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivityIntent);
                break;
            case R.id.nav_auction:
                Intent auctionItemIntent = new Intent(getApplicationContext(), AuctionYourStuff.class);
                startActivity(auctionItemIntent);
                break;
            case R.id.nav_auctions_bids:
                Intent auctionsBids = new Intent(getApplicationContext(), MyAuctionsBids.class);
                startActivity(auctionsBids);
                break;
            case R.id.nav_notifiction:
                Intent userNotification = new Intent(getApplicationContext(), UserNotification.class);
                startActivity(userNotification);
                break;
            case R.id.nav_messages:
                Intent messages = new Intent(getApplicationContext(), Messages.class);
                startActivity(messages);
                break;
            case R.id.nav_categories:
                Intent categories = new Intent((getApplicationContext()), Categories.class);
                startActivity(categories);
                break;
            case R.id.nav_profile:
                Intent myProfileIntent = new Intent(getApplicationContext(), MyProfile.class);
                startActivity(myProfileIntent);
                break;
            case R.id.nav_sign_out:
                FirebaseAuth.getInstance().signOut();
                Log.d(DEBUG_TAG, "FIREBASE USER: SIGNEDOUT");
                Intent createAccount = new Intent(getApplicationContext(), CreateAccount.class);
                startActivity(createAccount);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        createLocationRequest();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onResume() {

        navigationView.getMenu().getItem(0).setChecked(true);
        super.onResume();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}