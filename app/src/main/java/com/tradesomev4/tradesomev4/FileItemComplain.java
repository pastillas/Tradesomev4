package com.tradesomev4.tradesomev4;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tradesomev4.tradesomev4.m_Helpers.DateHelper;
import com.tradesomev4.tradesomev4.m_Helpers.ItemImageSwipe;
import com.tradesomev4.tradesomev4.m_Model.Auction;
import com.tradesomev4.tradesomev4.m_Model.ItemComplain;
import com.tradesomev4.tradesomev4.m_Model.User;

import java.util.HashMap;
import java.util.Map;

public class FileItemComplain extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    String[] reasonForReport = {"Inappropriate Items", "Illegal items"};
    String[] adultMaterial = {"Images of nudity", "Items that can\'t be listed at all", "Pornography", "Used underwear", "Other sexual or adult items"};
    String[] illegalItems = {"Drugs and drug paraphernalia", "Ammunition", "Assault weapons", "Explosives", "Airsoft, replica or imitation firearms"};
    ArrayAdapter<String> reasonForReportAdapter;
    ArrayAdapter<String> adultMaterialAdapter;
    ArrayAdapter<String> illegalItemsAdapter;

    Spinner reasonForReportSpinner;
    Spinner detailedReasonSpinner;

    private static final String EXTRAS_AUCTION_ID = "AUCTION_ID";
    private static final String EXTRAS_POSTER_ID = "POSTER_ID";
    private static final String EXTRAS_BUNDLE = "EXTRAS_BUNDLE";
    private String auctionId;
    private String posterId;
    private DatabaseReference mDatabase;
    private ImageView posterImage;
    private TextView posterName;
    private ViewPager swipeItem;

    private TextView itemTitle;
    private ItemImageSwipe itemImageSwipe;
    private FirebaseUser fUser;
    Bundle extras;
    private Button submit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_areport);

        reasonForReportSpinner = (Spinner) findViewById(R.id.spinner1);
        detailedReasonSpinner = (Spinner) findViewById(R.id.spinner2);

        reasonForReportAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, reasonForReport);
        reasonForReportAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reasonForReportSpinner.setAdapter(reasonForReportAdapter);

        reasonForReportSpinner.setOnItemSelectedListener(this);


        fUser = FirebaseAuth.getInstance().getCurrentUser();

        extras = getIntent().getBundleExtra(EXTRAS_BUNDLE);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        auctionId = extras.getString(EXTRAS_AUCTION_ID);
        posterId = extras.getString(EXTRAS_POSTER_ID);

        submit = (Button) findViewById(R.id.btn_submit);
        posterImage = (ImageView) findViewById(R.id.iv_poster_image);
        posterName = (TextView) findViewById(R.id.tv_poster_name);
        swipeItem = (ViewPager) findViewById(R.id.vp_swipe_item_image);
        itemTitle = (TextView) findViewById(R.id.tv_item_title);

        submit.setOnClickListener(this);
        posterImage.setOnClickListener(this);
        posterName.setOnClickListener(this);

        Query userRef = mDatabase.child("users").child(posterId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                Glide.with(getApplicationContext())
                        .load(user.getImage())
                        .asBitmap().centerCrop()
                        .into(posterImage);

                posterName.setText(user.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mDatabase.child("auction").child(auctionId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("auctionIDDD", auctionId);
                Auction auction = dataSnapshot.getValue(Auction.class);
                itemImageSwipe = new ItemImageSwipe(getApplicationContext(),
                        auction.getImage1Uri(),
                        auction.getImage2Uri(),
                        auction.getImage3Uri(),
                        auction.getImage4Uri());
                swipeItem.setAdapter(itemImageSwipe);
                itemTitle.setText(auction.getItemTitle());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private String sp1;
    private String sp2;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        sp1 = String.valueOf(reasonForReportSpinner.getSelectedItem());

        if (sp1.equals("Inappropriate Items")) {
            adultMaterialAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, adultMaterial);
            adultMaterialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            adultMaterialAdapter.notifyDataSetChanged();
            detailedReasonSpinner.setAdapter(adultMaterialAdapter);
        }

        if (sp1.equals("Illegal items")) {
            illegalItemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, illegalItems);
            illegalItemsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            illegalItemsAdapter.notifyDataSetChanged();
            detailedReasonSpinner.setAdapter(illegalItemsAdapter);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                new MaterialDialog.Builder(this)
                        .title("Confirm")
                        .content("Submit complain?")
                        .negativeText("No")
                        .positiveText("Yes")
                        .onAny(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                String choice = which.name();
                                final DatabaseReference updateStatus = FirebaseDatabase.getInstance().getReference();
                                if (choice.equals("POSITIVE")) {
                                    dialog.dismiss();

                                    showBasic();
                                }
                            }
                        }).show();
                break;
            case R.id.iv_poster_image:
                if (auctionId.equals(fUser.getUid())) {
                    Bundle extra = new Bundle();
                    extras.putString(EXTRAS_POSTER_ID, auctionId);
                    Intent intent = new Intent(this, ViewUserProfile.class);
                    intent.putExtra(EXTRAS_BUNDLE, extras);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, MyProfile.class);
                    startActivity(intent);
                }
                break;
            case R.id.tv_poster_name:
                if (auctionId.equals(fUser.getUid())) {
                    Bundle extra = new Bundle();
                    extras.putString(EXTRAS_POSTER_ID, auctionId);
                    Intent intent = new Intent(this, ViewUserProfile.class);
                    intent.putExtra(EXTRAS_BUNDLE, extras);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, MyProfile.class);
                    startActivity(intent);
                }
                break;
        }
    }

    public void showBasic() {
        Dialog d = new MaterialDialog.Builder(this)
                .title("Tradesome")
                .content("Your complain has succesfully sent.")
                .positiveText(R.string.continueBtn)
                .show();

        String key = mDatabase.child("itemComplain").push().getKey();
        ItemComplain itemComplain = new ItemComplain(
                String.valueOf(reasonForReportSpinner.getSelectedItem()),
                String.valueOf(detailedReasonSpinner.getSelectedItem()),
                auctionId, fUser.getUid(), posterId, key, DateHelper.getCurrentDateInMil());
        Map<String, Object> map = itemComplain.toMap();
        Map<String, Object> values = new HashMap<>();
        values.put("/itemComplain/" + key, map);
        mDatabase.updateChildren(values);

        d.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
