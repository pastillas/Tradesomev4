package com.tradesomev4.tradesomev4.m_Helpers;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tradesomev4.tradesomev4.CreateAccount;
import com.tradesomev4.tradesomev4.MainActivity;

/**
 * Created by Pastillas-Boy on 10/3/2016.
 */
public class IsBlockedListener {
    Context context;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;


    public IsBlockedListener(final Context context, final boolean inAuction, String uid) {
        this.context = context;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (uid.equals(firebaseUser.getUid())) {
            databaseReference.child("users").child(uid).child("blocked").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean isBlocked = dataSnapshot.getValue(Boolean.class);
                    if (isBlocked) {
                        new MaterialDialog.Builder(context)
                                .title("Account issue")
                                .content("Sorry your account is blocked by our administrators due to multiple malicious activities reported by the community.")
                                .cancelable(false)
                                .canceledOnTouchOutside(false)
                                .positiveText("SIGNOUT")
                                .onAny(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        if (which.toString().equals("POSITIVE")) {
                                            FirebaseAuth.getInstance().signOut();
                                            Intent intent = new Intent(context.getApplicationContext(), CreateAccount.class);
                                            context.startActivity(intent);
                                        }
                                    }
                                }).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            databaseReference.child("users").child(uid).child("blocked").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean isBlocked = dataSnapshot.getValue(Boolean.class);
                    if (isBlocked) {
                        if (inAuction) {
                            new MaterialDialog.Builder(context)
                                    .title("Item unavailable")
                                    .content("Sorry this item is unavailable due to multiple issues reported by the community.")
                                    .cancelable(false)
                                    .canceledOnTouchOutside(false)
                                    .positiveText("BACK")
                                    .onAny(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            if (which.toString().equals("POSITIVE")) {
                                                Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);
                                                context.startActivity(intent);
                                            }
                                        }
                                    }).show();
                        }else{
                            new MaterialDialog.Builder(context)
                                    .title("User issues.")
                                    .content("Sorry this user is unavailable due to multiple issues reported by the community.")
                                    .cancelable(false)
                                    .canceledOnTouchOutside(false)
                                    .positiveText("BACK")
                                    .onAny(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            if (which.toString().equals("POSITIVE")) {
                                                Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);
                                                context.startActivity(intent);
                                            }
                                        }
                                    }).show();
                        }

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


}
