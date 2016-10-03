package com.tradesomev4.tradesomev4;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.util.DialogUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tradesomev4.tradesomev4.m_Helpers.Connectivity;
import com.tradesomev4.tradesomev4.m_Helpers.DateHelper;
import com.tradesomev4.tradesomev4.m_Helpers.SnackBars;
import com.tradesomev4.tradesomev4.m_Model.Auction;
import com.tradesomev4.tradesomev4.m_Model.AuctionHistory;
import com.tradesomev4.tradesomev4.m_Model.User;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReviewAndPublish extends AppCompatActivity implements View.OnClickListener {

    private Uri compressUri1 = null;
    private Uri compressUri2 = null;
    private Uri compressUri3 = null;
    private Uri compressUri4 = null;
    private String itemTitleStr;
    String startingBidStr;
    private int startingBidInt;
    private String descriptionStr;
    private String categoryStr;
    private ViewPager viewPager;
    private CustomSwip customSwip;

    private Thread thread;
    private Handler handler;
    private int primaryPreselect;
    private int accentPreselect;
    private Dialog dialog2;
    private StorageReference imagesReference;
    private DatabaseReference databaseReference;
    private User user;
    private FirebaseUser fUser;
    private String image1Name;
    private String image2Name;
    private String image3Name;
    private String image4Name;

    private TextView title;
    private TextView startingBid;
    private TextView category;
    private TextView details;
    boolean isConnected;
    boolean isConnectionDisabledShowed;
    boolean isConnectionRestoredShowed;
    SnackBars snackBars;
    View parentView;
    int puta;
    private static final String DEBUG_TAG = "DEBUG_TAG";


    public void timer(){
        final CountDownTimer c = new CountDownTimer(1000, 1000) {

            @Override
            public void onTick(long l) {
                Log.d(DEBUG_TAG, "TIMER: " + l);
            }

            public void onFinish() {
                Connectivity connectivity = new Connectivity(getApplicationContext());

                if(!connectivity.isConnected()) {
                    isConnectionRestoredShowed = false;
                    isConnected = false;

                    if(puta == 1)
                        puta++;

                    if(!isConnectionDisabledShowed){
                        snackBars.showConnectionDisabledDialog();
                        isConnectionDisabledShowed = true;
                    }
                } else {
                    isConnected = true;
                    isConnectionDisabledShowed = false;

                    if(puta != 1 && !isConnectionRestoredShowed){
                        snackBars.showConnectionRestored();
                        isConnectionRestoredShowed = true;
                    }
                }

                timer();
            }
        }.start();
    }

    class CustomSwip extends PagerAdapter {
        private Uri imageResource[] = {compressUri1, compressUri2, compressUri3,compressUri4};
        private Context ctx;
        private LayoutInflater layoutInflater;

        public CustomSwip(Context c) {
            ctx = c;
        }

        @Override
        public int getCount() {
            return imageResource.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = layoutInflater.inflate(R.layout.activity_custom_swip, container, false);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.swip_image_view);
            //TextView textView = (TextView)itemView.findViewById(R.id.imageCount);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageResource[position]);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //imageView.setImageResource(imageResource[position]);
            //textView.setText("Image counter: " + position);
            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view==object);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_and_publish);
        Bundle extras = getIntent().getExtras();


        puta = 1;
        parentView = findViewById(R.id.content_main);
        snackBars = new SnackBars(parentView, getApplicationContext());
        isConnectionDisabledShowed = false;
        isConnectionRestoredShowed = false;

        title = (TextView)findViewById(R.id.title);
        startingBid = (TextView)findViewById(R.id.startingBid);
        category = (TextView)findViewById(R.id.category);
        details = (TextView)findViewById(R.id.details);
        findViewById(R.id.post).setOnClickListener(this);

        if (extras != null && extras.containsKey("image1")) {
            compressUri1 = Uri.parse(extras.getString("image1"));
            compressUri2 = Uri.parse(extras.getString("image2"));
            compressUri3 = Uri.parse(extras.getString("image3"));
            compressUri4 = Uri.parse(extras.getString("image4"));
            itemTitleStr = extras.getString("itemTitleStr");
            title.setText(itemTitleStr);
            startingBidStr = extras.getString("startingBidInt");
            startingBidInt = Integer.parseInt(startingBidStr);
            startingBid.setText("Php" + startingBidStr);
            descriptionStr = extras.getString("descriptionStr");

            categoryStr = extras.getString("categoryStr");
            category.setText(" " + categoryStr);
            details.setText( "     " + descriptionStr);
        }

        Uri images[] = {compressUri1, compressUri2, compressUri3, compressUri4};
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        customSwip = new CustomSwip(this);
        viewPager.setAdapter(customSwip);

        handler = new Handler();
        primaryPreselect = DialogUtils.resolveColor(this, R.attr.colorPrimary);
        accentPreselect = DialogUtils.resolveColor(this, R.attr.colorAccent);
        imagesReference = FirebaseStorage.getInstance().getReference();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        user = new User();
        try{
            if(fUser.getUid()!= null){
                databaseReference.child("users").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        user = dataSnapshot.getValue(User.class);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        failed = false;
        timer();
    }


    @Override
    public void onBackPressed() {
       back();

        super.onBackPressed();
    }

    public void back(){
        Intent intent = new Intent(getApplicationContext(), AuctionYourStuff.class);
        intent.putExtra("image1", compressUri1.toString());
        intent.putExtra("image2", compressUri2.toString());
        intent.putExtra("image3", compressUri3.toString());
        intent.putExtra("image4", compressUri4.toString());

        intent.putExtra("itemTitleStr", itemTitleStr);
        intent.putExtra("startingBidInt", startingBidInt + "");
        intent.putExtra("descriptionStr", descriptionStr);
        intent.putExtra("categoryStr", categoryStr);
        startActivity(intent);
    }


    @Override
    public boolean onNavigateUp() {

        NavUtils.navigateUpFromSameTask(ReviewAndPublish.this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            back();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.post:
                new MaterialDialog.Builder(this)
                        .title("Continue?")
                        .content("The process can't be canceled.")
                        .negativeText("No")
                        .positiveText("Yes")
                        .onAny(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                String choice = which.name();
                                final DatabaseReference updateStatus = FirebaseDatabase.getInstance().getReference();
                                if (choice.equals("POSITIVE")) {
                                    dialog.dismiss();

                                    if(isConnected){
                                        dialog2 = new MaterialDialog.Builder(ReviewAndPublish.this)
                                                .title(R.string.upload_progress_dialog)
                                                .content(R.string.please_wait)
                                                .cancelable(false)
                                                .canceledOnTouchOutside(false)
                                                .progress(true, 0)
                                                .progressIndeterminateStyle(true)
                                                .show();

                                        uploadImage();
                                    }else{
                                        showUploadFailed();
                                    }
                                }
                            }
                        }).show();

                break;
        }
    }

    public void showUploadFailed(){
        dialog2 = new MaterialDialog.Builder(ReviewAndPublish.this)
                .title("Upload failed")
                .content("Please check your connection and try again later.")
                .positiveText("OK")
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (thread != null && !thread.isInterrupted() && thread.isAlive())
            thread.interrupt();
    }

    private void startThread(Runnable run) {
        if (thread != null) {
            thread.interrupt();
        }

        thread = new Thread(run);
        thread.start();
    }

    private static String generateDerictoryName() {
        Calendar now = Calendar.getInstance();
        String timeStamp = new SimpleDateFormat("yyyyyMMdd_HHmmss").format(new Date());

        return now.getTimeInMillis() + "";
    }


    public void uploadImage() {
        if (compressUri1 != null && compressUri2 != null && compressUri3 != null && compressUri4 != null) {
            final String uid = fUser.getUid();
            final String directoryName = DateHelper.getCurrentDateInMil();
            final StorageReference uploadImageReference = imagesReference
                    .child(uid + "/")
                    .child(directoryName + "/");

            //Image1
            final StorageReference image1Ref = uploadImageReference.child("image1/" + compressUri1.getLastPathSegment());
            UploadTask uploadImage1 = image1Ref.putFile(compressUri1);
            uploadImage1.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog2.dismiss();
                    showUploadFailed();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri image1Uri = taskSnapshot.getDownloadUrl();
                    image1Name = image1Ref.getName();
                    Log.d("Uri1", image1Uri.toString());

                    //Image 2
                    final StorageReference image2Ref = uploadImageReference.child("image2/" + compressUri2.getLastPathSegment());
                    UploadTask uploadImage2 = image2Ref.putFile(compressUri2);
                    uploadImage2.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog2.dismiss();
                            showUploadFailed();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            final Uri image2Uri = taskSnapshot.getDownloadUrl();
                            image2Name = image2Ref.getName();
                            Log.d("Uri2", image2Uri.toString());

                            //Image 3
                            final StorageReference image3Ref = uploadImageReference.child("image3/" + compressUri3.getLastPathSegment());
                            UploadTask uploadImage3 = image3Ref.putFile(compressUri3);
                            uploadImage3.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialog2.dismiss();
                                    showUploadFailed();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    final Uri image3Uri = taskSnapshot.getDownloadUrl();
                                    image3Name = image3Ref.getName();
                                    Log.d("Uri2", image3Uri.toString());

                                    final StorageReference image4Ref = uploadImageReference.child("image4/" + compressUri4.getLastPathSegment());
                                    UploadTask uploadImage4 = image4Ref.putFile(compressUri4);
                                    uploadImage4.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            dialog2.dismiss();
                                            showUploadFailed();
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Uri image4Uri = taskSnapshot.getDownloadUrl();
                                            image4Name = image4Ref.getName();
                                            Log.d("Uri4", image4Uri.toString());

                                            saveToDatabase(image1Uri.toString(), image2Uri.toString(), image3Uri.toString(), image4Uri.toString(), uid, directoryName);

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            dialog2.dismiss();
                                            showUploadFailed();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialog2.dismiss();
                                    showUploadFailed();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog2.dismiss();
                            showUploadFailed();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog2.dismiss();
                    showUploadFailed();
                }
            });
        }
    }

    boolean failed;

    public void saveToDatabase(final String image1Uri, final String image2Uri, final String image3Uri, final String image4Uri, final String uid, final String directoryName){
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        final String key = mDatabase.child("auction").push().getKey();

        Calendar now = Calendar.getInstance();
        long postDate = now.getTimeInMillis();

        Auction auction = new Auction(uid, key, itemTitleStr, startingBidInt, startingBidInt, categoryStr, descriptionStr, image1Uri,
                image2Uri, image3Uri, image4Uri, image1Name, image2Name, image3Name, image4Name, directoryName, true, postDate, user.getLatitude(), user.getLongitude(), false);
        Map<String, Object> postValues = auction.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/auction/" + key, postValues);

        AuctionHistory auctionHistory = new AuctionHistory(uid, key);
        Map<String, Object>auctionHisVal = auctionHistory.toMap();

        childUpdates.put("/auctionHistory/" + uid + "/" + key, auctionHisVal);
        mDatabase.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dialog2.dismiss();
                mDatabase.child("auction").child(key).child("participants").child(fUser.getUid()).child("id").setValue(fUser.getUid());
                Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();

                if(!failed){
                    Intent intent = new Intent(getApplicationContext(),     MainActivity.class);
                    startActivity(intent);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog2.dismiss();
                failed = true;
                showUploadFailed();
            }
        });

    }
}
