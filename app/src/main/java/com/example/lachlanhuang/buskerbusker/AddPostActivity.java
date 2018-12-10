package com.example.lachlanhuang.buskerbusker;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaRecorder;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.lachlanhuang.buskerbusker.database.Post;
import com.example.lachlanhuang.buskerbusker.database.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.media.MediaRecorder.VideoSource.CAMERA;



public class AddPostActivity extends AppCompatActivity {

    private static final String TAG = "AddPostActivity";
    private static final String REQUIRED = "Required";



    FirebaseDatabase database;
    DatabaseReference ref;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers; private FirebaseUser mCurrentUser;
//    private FirebaseAuth.AuthStateListener;

    private Toolbar toolbar;

    // I might change this to just a clickable imageview if that's possible
    private ImageButton imageButton;
    private Button postButton;

    private EditText buskerNameField;
    private EditText textDescField;



    private static final int GALLERY = 0;

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        setUpToolbar();


        buskerNameField = (EditText) findViewById(R.id.busker_name);
        textDescField = (EditText) findViewById(R.id.post_description);
        imageButton = (ImageButton) findViewById(R.id.photo_gallery_button);
        postButton = (Button) findViewById(R.id.post_btn);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPost();
            }
        });


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPhotoDialog();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();

        ref = FirebaseDatabase.getInstance().getReference();





    }

    private void addPost() {
        final String buskerName = buskerNameField.getText().toString();
        final String textDesc = textDescField.getText().toString();

        // Title is required
        if (TextUtils.isEmpty(buskerName)) {
            buskerNameField.setError(REQUIRED);
            return;
        }

        // Body is required
        if (TextUtils.isEmpty(textDesc)) {
            textDescField.setError(REQUIRED);
            return;
        }


        Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();


        final String userId = mCurrentUser.getUid();

        ref.child("user").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);


                        if (user == null) {

                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(AddPostActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post
                            writeNewPost(userId, user.getName(), buskerName, textDesc);
                        }

                        // Finish this Activity, back to the stream

                        finish();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());

                    }
                });



    }


    private void writeNewPost(String userId, String username, String title, String body) {
        // create in two paths simultaneously
        String key = ref.child("posts").push().getKey();
        Post post = new Post(userId, username, title, body);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        ref.updateChildren(childUpdates);
    }


    // method to set up toolbar and navigation
    private void setUpToolbar() {

        toolbar = (Toolbar) findViewById(R.id.add_post_toolbar);
        setTitle("Add a Post");
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    // dialog to choose between gallery or camera
    private void showPhotoDialog(){
        AlertDialog.Builder photoDialog = new AlertDialog.Builder(this);
        photoDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        photoDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                photoFromGallery();
                                break;
                            case 1:
                                photoFromCamera();
                                break;
                        }
                    }
                });
        photoDialog.show();
    }

    // intent to go to photo gallery activity
    public void photoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    // intent to go to camera activity
    public void photoFromCamera() {


        //check for camera permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            // permission not granted

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                // show explanation to user


            } else {
                // no explanation needed just request permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);


            }

        } else {
            // already have permission
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA);
        }




    }

    /* what happens when selecting an image
     * I hope this works?
     * Hope this covers errors if no media available
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);

                    Toast.makeText(AddPostActivity.this, "saved", Toast.LENGTH_SHORT).show();
                    imageButton.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(AddPostActivity.this, "failed", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            imageButton.setImageBitmap(thumbnail);

            Toast.makeText(AddPostActivity.this, "saved", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, CAMERA);

                } else {
                    // permission denied, close activity
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }




}
