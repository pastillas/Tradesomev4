package com.tradesomev4.tradesomev4;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.util.DialogUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tradesomev4.tradesomev4.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UploadSample extends AppCompatActivity implements View.OnClickListener {
    public static final int BROWSE_KEY = 100;
    public static final int BROWSE_KEY2 = 101;
    private Uri imageUri;
    private Uri imageUri2;
    private ImageView imageView;
    private StorageReference imagesReference;
    private Thread thread;
    private Handler handler;
    private int primaryPreselect;
    private int accentPreselect;
    private Dialog dialog;
    private FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_sample);

        findViewById(R.id.browse).setOnClickListener(this);
        findViewById(R.id.upload).setOnClickListener(this);
        findViewById(R.id.browse2).setOnClickListener(this);

        imagesReference = FirebaseStorage.getInstance().getReference();

        handler = new Handler();
        primaryPreselect = DialogUtils.resolveColor(this, R.attr.colorPrimary);
        accentPreselect = DialogUtils.resolveColor(this, R.attr.colorAccent);
        fUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    protected  void onDestroy(){
        super.onDestroy();
        handler = null;
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(thread != null && !thread.isInterrupted() && thread.isAlive())
            thread.interrupt();
    }

    private void startThread(Runnable run){
        if(thread != null){
            thread.interrupt();
        }

        thread = new Thread(run);
        thread.start();
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.browse:
                dispatchLunchGallery(BROWSE_KEY);
                break;
            case R.id.browse2:
                dispatchLunchGallery(BROWSE_KEY2);
                break;
            case R.id.upload:
                 dialog = new MaterialDialog.Builder(this)
                        .title(R.string.upload_progress_dialog)
                        .content(R.string.please_wait)
                        .progress(true, 0)
                        .progressIndeterminateStyle(true)
                        .show();
                uploadImage();
                break;
        }
    }


    private void dispatchLunchGallery(int key) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), key);
    }

    private static String generateDerictoryName() {
        String timeStamp = new SimpleDateFormat("yyyyyMMdd_HHmmss").format(new Date());

        return timeStamp;
    }

    public void uploadImage(){
        if(imageUri != null && imageUri2 != null) {
            final String uid = fUser.getUid();
            final String directoryName = generateDerictoryName();
            final StorageReference uploadImageReference = imagesReference
                    .child(uid + "/")
                    .child(directoryName + "/");

            StorageReference image1Ref = uploadImageReference.child("image1/" + imageUri.getLastPathSegment());
            UploadTask uploadImage1 = image1Ref.putFile(imageUri);
            uploadImage1.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri image1Uri = taskSnapshot.getDownloadUrl();

                    Log.d("Uri1", image1Uri.toString());

                    StorageReference image2Ref = uploadImageReference.child("image2/" + imageUri2.getLastPathSegment());
                    UploadTask uploadImage2 = image2Ref.putFile(imageUri2);
                    uploadImage2.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri image2Uri = taskSnapshot.getDownloadUrl();

                            Log.d("Uri2",  image2Uri.toString());
                            dialog.dismiss();
                        }
                    });
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BROWSE_KEY && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
        }

        if (requestCode == BROWSE_KEY2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri2 = data.getData();
        }
    }

}