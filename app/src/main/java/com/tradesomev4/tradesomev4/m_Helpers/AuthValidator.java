package com.tradesomev4.tradesomev4.m_Helpers;

import android.content.Context;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tradesomev4.tradesomev4.CreateAccount;

/**
 * Created by Pastillas-Boy on 9/12/2016.
 */
public class AuthValidator {
    public static void validate(Context context){
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(fUser == null){
            Intent intent = new Intent(context.getApplicationContext(), CreateAccount.class);
            context.startActivity(intent);
        }
    }
}
