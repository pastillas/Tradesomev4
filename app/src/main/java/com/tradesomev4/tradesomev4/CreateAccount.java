package com.tradesomev4.tradesomev4;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.tradesomev4.tradesomev4.m_Helpers.Connectivity;
import com.tradesomev4.tradesomev4.m_Helpers.Dialogs;
import com.tradesomev4.tradesomev4.m_Helpers.GPSTracker;
import com.tradesomev4.tradesomev4.m_Helpers.SnackBars;
import com.tradesomev4.tradesomev4.m_Model.User;

public /*abstract*/ class CreateAccount extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, LocationListener {
    private static final String DEBUG_TAG = "DEBUG_TAG";
    private static final int RC_SIGN_IN = 9001;
    public User user;
    private LoginButton fbSignInB;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CallbackManager callbackManager;
    private GoogleSignInOptions gso;
    private SignInButton signInButton;
    private GoogleApiClient mGoogleApiClient;
    private static final String DIALOG_ERROR = "dialog_error";
    private static final String STATE_RESOLVING_ERROR = "resolving_error";
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private static final int REQUEST_CHECK_SETTINGS = 1002;
    private boolean mRequestingLocationUpdates = true;
    private boolean mResolvingError = false;
    private Location mCurrentLocation;
    private LocationManager locationManager;
    private LocationRequest mLocationRequest;
    private boolean gps_enabled = false;
    private boolean network_enabled = false;
    private String mLastUpdateTime;
    boolean isConnected;
    boolean isGpsDisabledShowed = true;
    boolean isConnectionDisabledShowed = true;
    MaterialDialog dialog;
    View view;
    TextView goodVibes;
    SnackBars snackbars;
    Dialog loadingDialog;
    FirebaseUser fUser;
    boolean isLoading;
    boolean isUserExists;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = new User();
        initFirebaseAuth();
        isUserExists = false;
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.create_account_activity);
        view = findViewById(R.id.layout);
        snackbars = new SnackBars(view, getApplicationContext());

        initGoodVibes();

        Log.d(DEBUG_TAG, "TIMER: 1");
        timer();

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


        findViewById(R.id.signInButton).setOnClickListener(this);
        initGoogleApi(savedInstanceState);
        initFbSignIn();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        if(isFacebookLoggedIn())
            signoutFacebook();
    }

    public boolean isFacebookLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    public void checkUserIfExists(final int type){
        databaseReference.child("users").child(fUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(DEBUG_TAG, String.valueOf(dataSnapshot.getChildrenCount()));

                if(dataSnapshot.getChildrenCount() > 0){
                    user = dataSnapshot.getValue(User.class);

                    if(user.isBlocked()){
                        isLoading = true;
                        loadingDialog.dismiss();
                        signOut();

                        if(type == 0)
                            signoutFacebook();

                        snackbars.isAccountBlocked();
                    }else{
                        String token = FirebaseInstanceId.getInstance().getToken();
                        databaseReference.child("users").child(fUser.getUid()).child("fcmToken").setValue(token).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                loadingDialog.dismiss();
                                toMainActivity();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loadingDialog.dismiss();
                                if(type == 0)
                                    signoutFacebook();
                                snackbars.showCheckYourConnection();
                                FirebaseAuth.getInstance().signOut();
                            }
                        });
                    }
                }else{
                    String token  = FirebaseInstanceId.getInstance().getToken();
                    user.setId(fUser.getUid());
                    user.setName(fUser.getDisplayName());
                    user.setBlocked(false);
                    user.setImage(fUser.getPhotoUrl().toString());
                    user.setEmail(fUser.getEmail());
                    user.setFcmToken(token);

                    if(String.valueOf(user.getLatitude()) != null && String.valueOf(user.getLongitude()) != null){
                        databaseReference.child("users").child(fUser.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                loadingDialog.dismiss();
                                toMainActivity();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loadingDialog.dismiss();
                                signOut();

                                if(type == 0)
                                    LoginManager.getInstance().logOut();

                                showPakonSwelo();
                            }
                        });
                    }else{
                        snackbars.failedToGetLocation();
                    }
                }

                //toMainActivity();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(DEBUG_TAG, String.valueOf(databaseError.getCode()));
            }
        });
    }

    private void signoutFacebook() {
        LoginManager.getInstance().logOut();
        Log.d(DEBUG_TAG, "FACEBOOK LOGOUT: TRUE");
    }

    public void showPakonSwelo(){
        Dialogs.showPakonSwelo(this);
    }

    public void initFirebaseAuth(){
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d(DEBUG_TAG, "GETFIREBASEUSER");
            }
        };
    }


    public void getFirebaseUser(int type){
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fUser != null) {
            Log.d(DEBUG_TAG, "TIMER: 2");
            if(gps_enabled && network_enabled && isConnected) {
                Log.d(DEBUG_TAG, "AUTH: onAuthStateChanged: signed_in:" + fUser.getUid());
                checkUserIfExists(type);


                Log.d(DEBUG_TAG, "PUTANG INA FACEBOOK: TRUE");
            }else{
                loadingDialog.dismiss();
                signOut();

                Log.d(DEBUG_TAG, "PUTANG INA FACEBOOK: FALSE");


                if(type == 0)
                    signoutFacebook();

                if(!gps_enabled || !network_enabled)
                    snackbars.showGpsDiabledDialog();
                else
                    snackbars.showCheckYourConnection();
            }
        } else {
            if(loadingDialog != null)
            loadingDialog.dismiss();

            if(type == 0)
                signoutFacebook();

            Log.d(DEBUG_TAG, "AUTH: onAuthStateChanged:signed_out");
        }
    }


    public void initGoodVibes(){
        goodVibes = (TextView)findViewById(R.id.textView2);
        String str = "@ Get Started";
        SpannableString tmp = new SpannableString(str);

        int index = str.indexOf('@');
        Drawable icon = getResources().getDrawable(R.drawable.ic_thumb_up_white_24dp);
        icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(icon, ImageSpan.ALIGN_BOTTOM);

        tmp.setSpan(span, index, index + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        goodVibes.setText(tmp);
    }

    public void showLoadingDialog(){
        loadingDialog = new MaterialDialog.Builder(CreateAccount.this)
                .title("Login")
                .content("Please wait...")
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .progress(true, 0)
                .progressIndeterminateStyle(false)
                .show();
    }

    public void timeOutTimer(){
        final CountDownTimer c = new CountDownTimer(20000, 1000) {
            @Override
            public void onTick(long l) {
                Log.d(DEBUG_TAG, "timeOutTimer:  " + String.valueOf(l));
            }

            @Override
            public void onFinish() {
                loadingDialog.dismiss();
                snackbars.showCheckYourConnection();
            }
        };

        c.start();
    }

    public void signOut(){
        FirebaseAuth.getInstance().signOut();
    }

    public void timer(){
        final CountDownTimer c = new CountDownTimer(1000, 1000) {

            @Override
            public void onTick(long l) {
                Log.d(DEBUG_TAG, "TIMER: " + l);
            }

            public void onFinish() {
                Connectivity connectivity = new Connectivity(getApplicationContext());

                if(!connectivity.isConnected()) {
                    if(!isConnectionDisabledShowed){
                        //Log.d(DEBUG_TAG, "CONNECTION: OFFLINE");
                        if(loadingDialog != null)
                            loadingDialog.dismiss();

                        snackbars.showConnectionDisabledDialog();
                        isConnectionDisabledShowed = true;
                    }
                    isConnected = false;
                } else {
                    if(isConnectionDisabledShowed){
                        snackbars.showConnectionRestored();
                        isConnectionDisabledShowed = false;
                    }
                    isConnected = true;
                    //Log.d(DEBUG_TAG, "CONNECTION: ONLINE");
                }

                if(!connectivity.gpsOn()){
                    if(isLoading)
                        loadingDialog.dismiss();

                    if(!isGpsDisabledShowed){

                        snackbars.showGpsDiabledDialog();
                        isGpsDisabledShowed = true;
                    }

                    gps_enabled = false;
                    network_enabled = false;
                } else {
                    if(isGpsDisabledShowed){
                        snackbars.gpsRestored();
                        isGpsDisabledShowed = false;
                    }
                    //Log.d(DEBUG_TAG, "GPS: ON");
                    gps_enabled = true;
                    network_enabled = true;
                }

                isConnected = connectivity.isConnected() && connectivity.gpsOn();
                //Log.d(DEBUG_TAG, "LOGIN STATUS: " + isEnabled);

                timer();
            }
        }.start();
    }


    public void initFbSignIn(){
        callbackManager = CallbackManager.Factory.create();
        fbSignInB = (LoginButton) findViewById(R.id.fbSignInB);
        fbSignInB.setReadPermissions("email", "public_profile");
        fbSignInB.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //showLoadingDialog();
                Log.d(DEBUG_TAG, "facebook:onSuccess:" + loginResult);
                showLoadingDialog();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(DEBUG_TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                snackbars.showCheckYourConnection();
                Log.d(DEBUG_TAG, "facebook:onError", error);
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(DEBUG_TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(DEBUG_TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(DEBUG_TAG, "signInWithCredential", task.getException());

                            loadingDialog.dismiss();
                            signoutFacebook();
                            signOut();

                            snackbars.isEmailAlreadyUsed();
                        }else{
                            getFirebaseUser(0);
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                showLoadingDialog();
                firebaseAuthWithGoogle(account);
                Log.d(DEBUG_TAG, "Success Google SignIn");

            } else {
                if(loadingDialog != null)
                    loadingDialog.dismiss();

                snackbars.showCheckYourConnection();
                Log.d(DEBUG_TAG, "Error Google SignIn");
            }
        } else {
            if (requestCode == REQUEST_RESOLVE_ERROR) {
                mResolvingError = false;
                if (resultCode == RESULT_OK) {
                    // Make sure the app is not already connected or attempting to connect
                    if (!mGoogleApiClient.isConnecting() &&
                            !mGoogleApiClient.isConnected()) {
                        mGoogleApiClient.connect();
                    }
                }
            } else
                callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(DEBUG_TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(DEBUG_TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.

                        if (!task.isSuccessful()) {

                            Log.w(DEBUG_TAG, "signInWithCredential", task.getException());
                            loadingDialog.dismiss();
                            signOut();

                            if(!gps_enabled || !network_enabled){
                                snackbars.showGpsDiabledDialog();
                            }

                            if(!isConnected){
                                snackbars.showConnectionDisabledDialog();
                            }
                        }else{
                            if(gps_enabled && network_enabled && isConnected){
                                getFirebaseUser(1);
                            }
                            else {
                                loadingDialog.dismiss();
                                snackbars.showGpsDiabledDialog();
                            }
                        }
                    }
                });
    }


    public void initGoogleApi(Bundle savedInstanceState){
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("901040923243-g3qbi16p2sbdr3qtspc5vjlk77trboh1.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);
    }


    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;

        user.setLatitude(mCurrentLocation.getLatitude());
        user.setLongitude(mCurrentLocation.getLongitude());
        Log.d(DEBUG_TAG, "LATITUDE: " + mCurrentLocation.getLatitude());
        Log.d(DEBUG_TAG, "LONGITUDE: " + mCurrentLocation.getLongitude());
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (LocationListener) this);

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates states = result.getLocationSettingsStates();

                locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                startLocationUpdates();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        createLocationRequest();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(CreateAccount.this, REQUEST_CHECK_SETTINGS);
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            createLocationRequest();
            if(dialog != null)
                dialog.dismiss();
            return;
        } else {
            if (gps_enabled) {
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mCurrentLocation != null) {
                    mLocationRequest = new LocationRequest();
                    mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
                    mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

                    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                    if (mRequestingLocationUpdates)
                        startLocationUpdates();
                }
            } else {
                createLocationRequest();
            }
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Not Connected!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();

        if (mResolvingError) {
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                mGoogleApiClient.connect();
            }
        } else {
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }

    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() {

        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);

            return GoogleApiAvailability.getInstance().getErrorDialog(this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((GPSTracker) getActivity()).onDialogDismissed();
        }
    }

    private void showErrorDialog(int errorCode) {
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "Errordialog");
    }

    public void onDialogDismissed() {
        mResolvingError = false;
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {

        super.onStart();
        mGoogleApiClient.connect();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);

        savedInstanceState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.signInButton:
                signIn();
                break;
        }
    }

    public void saveData(){
        user.saveUser(user);

        if(loadingDialog != null)
            loadingDialog.dismiss();
    }

    public void toMainActivity(){
        if(loadingDialog != null)
            loadingDialog.dismiss();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

}
