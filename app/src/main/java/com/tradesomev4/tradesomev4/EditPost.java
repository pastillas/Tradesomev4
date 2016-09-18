package com.tradesomev4.tradesomev4;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.util.DialogUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.iceteck.silicompressorr.SiliCompressor;
import com.tradesomev4.tradesomev4.m_Helpers.DateHelper;
import com.tradesomev4.tradesomev4.m_Model.Auction;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditPost extends AppCompatActivity implements
        View.OnClickListener,
        AdapterView.OnItemSelectedListener {
    private static final String AUCTION_ID_KEY = "AUCTION_KEY";
    private static final String BUNDLE_KEY = "BUNDLE_KEY";
    private Bundle extras;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 101;
    private static final int PICK_IMAGE_REQUEST = 102;
    private static final int REVIEW_AND_PUBLISH_CODE = 103;
    private int primaryPreselect;
    private int accentPreselect;
    private Toast mToast;
    private Thread mThread;
    private Handler mHandler;
    private int chooserDialog;
    private Button uploadImage;
    private Uri capturedUri1 = null;
    private Uri compressUri1 = null;
    private Uri capturedUri2 = null;
    private Uri compressUri2 = null;
    private Uri capturedUri3 = null;
    private Uri compressUri3 = null;
    private Uri capturedUri4 = null;
    private Uri compressUri4 = null;

    private ImageView itemImage1;
    private ImageView itemImage2;
    private ImageView itemImage3;
    private ImageView itemImage4;
    private int choice = 1;
    private String mCurrentPhotoPath;
    private StorageReference storageRef;
    private TextView categoriesLabel;
    private EditText itemTitle;
    private EditText startingBid;
    private EditText description;
    private TextView category;
    private String itemTitleStr;
    private int startingBidInt;
    private String descriptionStr;
    private String categoryStr;
    private Dialog dialog2;
    private DatabaseReference mDatabase;

    private boolean onSave = false;
    private Auction auction;
    private FirebaseUser fUser;
    RequestManager requestManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        requestManager = Glide.with(this);

        extras = getIntent().getBundleExtra(BUNDLE_KEY);

        storageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        itemImage1 = (ImageView) findViewById(R.id.itemImage1);
        itemImage2 = (ImageView) findViewById(R.id.itemImage2);
        itemImage3 = (ImageView) findViewById(R.id.itemImage3);
        itemImage4 = (ImageView) findViewById(R.id.itemImage4);

        itemImage1.setOnClickListener(this);
        itemImage2.setOnClickListener(this);
        itemImage3.setOnClickListener(this);
        itemImage4.setOnClickListener(this);
        findViewById(R.id.selectCategory).setOnClickListener(this);
        findViewById(R.id.next).setOnClickListener(this);

        itemTitle = (EditText) findViewById(R.id.itemTitle);
        startingBid = (EditText) findViewById(R.id.startingBid);
        description = (EditText) findViewById(R.id.description);
        category = (TextView) findViewById(R.id.category);

        mHandler = new Handler();
        primaryPreselect = DialogUtils.resolveColor(this, R.attr.colorPrimary);
        accentPreselect = DialogUtils.resolveColor(this, R.attr.colorAccent);

        editMode();
    }

    public void editMode() {
        if (extras != null) {
            mDatabase.child("auction").child(extras.getString(AUCTION_ID_KEY)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    auction = dataSnapshot.getValue(Auction.class);

                    requestManager.load(auction.getImage1Uri())
                            .asBitmap().centerCrop()
                            .into(itemImage1);

                    requestManager.load(auction.getImage2Uri())
                            .asBitmap().centerCrop()
                            .into(itemImage2);

                    requestManager.load(auction.getImage3Uri())
                            .asBitmap().centerCrop()
                            .into(itemImage3);

                    requestManager.load(auction.getImage4Uri())
                            .asBitmap().centerCrop()
                            .into(itemImage4);

                    itemTitleStr = auction.getItemTitle();
                    itemTitle.setText(auction.getItemTitle());
                    itemTitle.setEnabled(false);

                    startingBid.setText(String.valueOf(auction.getStaringBid()));
                    description.setText(auction.getDescription());
                    category.setText(auction.getCategory());


                    String startingBidStr = String.valueOf(auction.getStaringBid());
                    startingBidInt = auction.getStaringBid();
                    descriptionStr = auction.getDescription();
                    categoryStr = auction.getCategory();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        categoriesLabel.setText(String.valueOf(parent.getItemAtPosition(position)));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        categoriesLabel.setText(String.valueOf(parent.getItemAtPosition(0)));
    }

    public void showBasic() {
        new MaterialDialog.Builder(this)
                .title(R.string.error)
                .content(R.string.errorContent)
                .positiveText(R.string.continueBtn)
                .show();
    }

    public void showList() {
        new MaterialDialog.Builder(this)
                .title(R.string.dialogUploadIMageTitle)
                .items(R.array.dialogUPloagIMageItems)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (which == 0)
                            requestPermission();
                        else if (which == 1)
                            dispatchLunchGallery();
                    }
                })

                .positiveText(android.R.string.cancel)
                .show();
    }

    public void showSingleChoice() {
        new MaterialDialog.Builder(this)
                .title(R.string.chooseCategoryDialogTitle)
                .items(R.array.categories)
                .itemsCallbackSingleChoice(2, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        //showToast(which + ": " + text);
                        categoryStr = text.toString();
                        category.setText(text);
                        return true; // allow selection
                    }
                })
                .positiveText(R.string.md_choose_label)
                .show();
    }


    private void requestPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
        } else {
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permission[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(this, "You need enable the permission for External Storage Write" +
                            " to add photos of your item.", Toast.LENGTH_LONG).show();
                    return;
                }
                break;
        }
    }

    private void dispatchLunchGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;

            try {
                photoFile = createImageFile();
            } catch (IOException e) {
            }

            if (photoFile != null) {
                switch (choice) {
                    case 1:
                        capturedUri1 = Uri.fromFile(photoFile);

                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedUri1);
                        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                        break;

                    case 2:
                        capturedUri2 = Uri.fromFile(photoFile);

                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedUri2);
                        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                        break;

                    case 3:
                        capturedUri3 = Uri.fromFile(photoFile);

                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedUri3);
                        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                        break;

                    case 4:
                        capturedUri4 = Uri.fromFile(photoFile);

                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedUri4);
                        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                        break;
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                switch (choice) {
                    case 1:
                        new ImageCompressionAsyncTask(this).execute(capturedUri1.toString());
                        break;

                    case 2:
                        new ImageCompressionAsyncTask(this).execute(capturedUri2.toString());
                        break;

                    case 3:
                        new ImageCompressionAsyncTask(this).execute(capturedUri3.toString());
                        break;

                    case 4:
                        new ImageCompressionAsyncTask(this).execute(capturedUri4.toString());
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                switch (choice) {
                    case 1:
                        capturedUri1 = data.getData();
                        new ImageCompressionAsyncTask(this).execute(capturedUri1.toString());
                        break;
                    case 2:
                        capturedUri2 = data.getData();
                        new ImageCompressionAsyncTask(this).execute(capturedUri2.toString());
                        break;
                    case 3:
                        capturedUri3 = data.getData();
                        new ImageCompressionAsyncTask(this).execute(capturedUri3.toString());
                        break;
                    case 4:
                        capturedUri4 = data.getData();
                        new ImageCompressionAsyncTask(this).execute(capturedUri4.toString());
                        break;
                }
            }
        }
    }


    class ImageCompressionAsyncTask extends AsyncTask<String, Void, String> {
        Context mContext;

        public ImageCompressionAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected String doInBackground(String... params) {

            String filepath = SiliCompressor.with(mContext).compress(params[0]);
            return filepath;
        }

        @Override
        protected void onPostExecute(String s) {
            File imageFile = new File(s);

            switch (choice) {
                case 1:
                    compressUri1 = Uri.fromFile(imageFile);
                    break;
                case 2:
                    compressUri2 = Uri.fromFile(imageFile);
                    break;
                case 3:
                    compressUri3 = Uri.fromFile(imageFile);
                    break;
                case 4:
                    compressUri4 = Uri.fromFile(imageFile);
                    break;
            }

            try {
                switch (choice) {
                    case 1:
                        //Toast.makeText(AuctionYourStuff.this, "Compress done", Toast.LENGTH_SHORT).show();
                        Bitmap bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), compressUri1);
                        itemImage1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        itemImage1.setImageBitmap(bitmap1);
                        //uploadImage(compressUri1);
                        break;

                    case 2:
                        //Toast.makeText(AuctionYourStuff.this, "Compress done", Toast.LENGTH_SHORT).show();
                        Bitmap bitmap2 = MediaStore.Images.Media.getBitmap(getContentResolver(), compressUri2);
                        itemImage2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        itemImage2.setImageBitmap(bitmap2);
                        //uploadImage(compressUri2);
                        break;

                    case 3:
                        //Toast.makeText(AuctionYourStuff.this, "Compress done", Toast.LENGTH_SHORT).show();
                        Bitmap bitmap3 = MediaStore.Images.Media.getBitmap(getContentResolver(), compressUri3);
                        itemImage3.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        itemImage3.setImageBitmap(bitmap3);
                        //uploadImage(compressUri3);
                        break;
                    case 4:
                        //Toast.makeText(AuctionYourStuff.this, "Compress done", Toast.LENGTH_SHORT).show();
                        Bitmap bitmap4 = MediaStore.Images.Media.getBitmap(getContentResolver(), compressUri4);
                        itemImage4.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        itemImage4.setImageBitmap(bitmap4);
                        //uploadImage(compressUri4);
                        break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mThread != null && !mThread.isInterrupted() && mThread.isAlive()) {
            mThread.interrupt();
        }
    }

    private void validate() {
        boolean valid = true;

        //if (compressUri1 == null || compressUri2 == null || compressUri3 == null || compressUri4 == null)
        //valid = false;

        itemTitleStr = itemTitle.getText().toString();

        String startingBidStrTemp = startingBid.getText().toString();

        if (startingBidStrTemp.length() > 0) {
            Log.d("puta", "puta");
            startingBidInt = Integer.parseInt(startingBidStrTemp);
        }

        descriptionStr = description.getText().toString();

        if (TextUtils.isEmpty(itemTitleStr)) {
            itemTitle.setError("Required.");
            valid = false;
        } else {
            itemTitle.setError(null);
        }

        if (TextUtils.isEmpty(startingBidStrTemp)) {
            startingBid.setError("Required.");
            valid = false;
        } else {
            startingBid.setError(null);
        }

        if (TextUtils.isEmpty(descriptionStr)) {
            description.setError("Required.");
            valid = false;
        } else {
            description.setError(null);
        }

        if (valid) {
            onSave = true;
            save();
        } else {
            showBasic();
        }
    }

    private boolean image1Done = false;
    private boolean image2Done = false;
    private boolean image3Done = false;
    private boolean image4Done = false;

    public void uploadImage() {


        final String uid = fUser.getUid();
        final String directoryName = DateHelper.getCurrentDateInMil();
        final StorageReference uploadImageReference = storageRef.child(uid + "/").child(directoryName + "/");

        mDatabase.child("auction").child(auction.getAuctionId()).child("category").setValue(categoryStr);
        mDatabase.child("auction").child(auction.getAuctionId()).child("description").setValue(descriptionStr);
        mDatabase.child("auction").child(auction.getAuctionId()).child("directoryName").setValue(directoryName);
        mDatabase.child("auction").child(auction.getAuctionId()).child("staringBid").setValue(startingBidInt);


        if (compressUri1 != null) {
            final StorageReference image1Ref = uploadImageReference.child("image1/" + compressUri1.getLastPathSegment());
            UploadTask uploadImage1 = image1Ref.putFile(compressUri1);
            uploadImage1.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri image1Uri = taskSnapshot.getDownloadUrl();
                    String image1Name = image1Ref.getName();
                    mDatabase.child("auction").child(auction.getAuctionId()).child("image1Name").setValue(image1Name);
                    mDatabase.child("auction").child(auction.getAuctionId()).child("image1Uri").setValue(image1Uri.toString());
                    image1Done = true;
                    if (image1Done && image2Done && image3Done && image4Done) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        dialog2.dismiss();
                    }
                }
            });
        }
        if (compressUri2 != null) {
            final StorageReference image2Ref = uploadImageReference.child("image2" + compressUri2.getLastPathSegment());
            UploadTask uploadImage2 = image2Ref.putFile(compressUri2);
            uploadImage2.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri image2Uri = taskSnapshot.getDownloadUrl();
                    String image2Name = image2Ref.getName();
                    mDatabase.child("auction").child(auction.getAuctionId()).child("image2Name").setValue(image2Name);
                    mDatabase.child("auction").child(auction.getAuctionId()).child("image2Uri").setValue(image2Uri);
                    image2Done = true;
                    if (image1Done && image2Done && image3Done && image4Done) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        dialog2.dismiss();
                    }
                }
            });
        }
        if (compressUri3 != null) {
            final StorageReference image3Ref = uploadImageReference.child("image3" + compressUri3.getLastPathSegment());
            UploadTask uploadImage3 = image3Ref.putFile(compressUri3);
            uploadImage3.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri image3Uri = taskSnapshot.getDownloadUrl();
                    String image3Name = image3Ref.getName();
                    mDatabase.child("auction").child(auction.getAuctionId()).child("image3Name").setValue(image3Name);
                    mDatabase.child("auction").child(auction.getAuctionId()).child("image3Uri").setValue(image3Uri);
                    image3Done = true;
                    if (image1Done && image2Done && image3Done && image4Done) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        dialog2.dismiss();
                    }
                }
            });
        }
        if (compressUri4 != null) {
            final StorageReference image4Ref = uploadImageReference.child("image4" + compressUri4.getLastPathSegment());
            UploadTask uploadImage4 = image4Ref.putFile(compressUri4);
            uploadImage4.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri image4Uri = taskSnapshot.getDownloadUrl();
                    String image4Name = image4Ref.getName();
                    mDatabase.child("auction").child(auction.getAuctionId()).child("image4Name").setValue(image4Name);
                    mDatabase.child("auction").child(auction.getAuctionId()).child("image4Uri").setValue(image4Uri);
                    image4Done = true;
                    if (image1Done && image2Done && image3Done && image4Done) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        dialog2.dismiss();
                    }
                }
            });
        }
    }

    public void save() {
        new MaterialDialog.Builder(this)
                .title("Confirm")
                .content("Save changes?")
                .negativeText("No")
                .positiveText("Yes")
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String choice = which.name();
                        final DatabaseReference updateStatus = FirebaseDatabase.getInstance().getReference();
                        if (choice.equals("POSITIVE")) {
                            dialog.dismiss();

                            dialog2 = new MaterialDialog.Builder(EditPost.this)
                                    .title(R.string.upload_progress_dialog)
                                    .content(R.string.please_wait)
                                    .progress(true, 0)
                                    .progressIndeterminateStyle(true)
                                    .show();

                            if (compressUri1 == null)
                                image1Done = true;
                            if (compressUri2 == null)
                                image2Done = true;
                            if (compressUri3 == null)
                                image3Done = true;
                            if (compressUri4 == null)
                                image4Done = true;
                            uploadImage();


                        }

                    }
                }).show();
    }

    @Override
    public void onClick(View v) {
        int item = v.getId();

        switch (item) {
            case R.id.itemImage1:
                choice = 1;
                showList();
                break;

            case R.id.itemImage2:
                choice = 2;
                showList();
                break;

            case R.id.itemImage3:
                choice = 3;
                showList();
                break;

            case R.id.itemImage4:
                choice = 4;
                showList();
                break;

            case R.id.selectCategory:
                showSingleChoice();
                break;

            case R.id.next:
                validate();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(getApplicationContext(), MyAuctionsBids.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
