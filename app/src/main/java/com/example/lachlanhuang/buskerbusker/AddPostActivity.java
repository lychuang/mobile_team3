package com.example.lachlanhuang.buskerbusker;

import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaRecorder;
import android.content.Intent;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import static android.media.MediaRecorder.VideoSource.CAMERA;


public class AddPostActivity extends AppCompatActivity {

    private Toolbar toolbar;

    // I might change this to just a clickable imageview if that's possible
    private ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        setUpToolbar();

        imageButton = (ImageButton) findViewById(R.id.photo_gallery_button);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPhotoDialog();
            }
        });


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

        startActivityForResult(galleryIntent, 0);
    }

    // intent to go to camera activity
    public void photoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);

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
        if (requestCode == 0) {
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




}
