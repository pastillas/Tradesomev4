package com.tradesomev4.tradesomev4.m_Helpers;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Pastillas-Boy on 9/19/2016.
 */
public class FirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String DEBUG_TAG = "DEBUG_TAG";

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(DEBUG_TAG, "ON REFRESH: " + token);

        updateFcmToke(token);
    }

    public void updateFcmToke(String token){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser != null)
            databaseReference.child("user").child(firebaseUser.getUid()).child("fcmToken").setValue(token);
    }
}
