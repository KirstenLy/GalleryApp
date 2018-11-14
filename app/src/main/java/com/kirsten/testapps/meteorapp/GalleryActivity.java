package com.kirsten.testapps.meteorapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.kirsten.testapps.meteorapp.adatpers.RVAdapter;
import com.kirsten.testapps.meteorapp.another.DellAllPhotoResult;
import com.kirsten.testapps.meteorapp.functions.Functions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

import static android.os.Environment.DIRECTORY_PICTURES;

public class GalleryActivity extends AppCompatActivity {
    final int REQUEST_TAKE_PHOTO = 1;
    private File tempFile;

    private void takeAPhoto(String PATH, int REQUEST_TAKE_PHOTO) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                tempFile = Functions.createImageFile(PATH);
            } catch (IOException ex) {
                Toast.makeText(getApplicationContext(), "Denied. Create file error.", Toast.LENGTH_LONG).show();
                return;
            }
            if (tempFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                        "com.kirsten.testapps.fileprovider",
                        tempFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Denied. Can't find a camera.", Toast.LENGTH_LONG).show();
        }
    }

    private DellAllPhotoResult deleteAllPhoto(RVAdapter adapter, ArrayList<String> imageNames, String PATH) {
        boolean[] isSelected = adapter.getIsSelected();
        boolean noErrorResult = true;
        int successDeletedFileCounter = 0;
        int failedDeletedFileCounter = 0;
        StringBuilder pathString = new StringBuilder();

        for (int i = 0; i < imageNames.size(); i++) {
            if (isSelected[i]) {
                tempFile = new File(pathString.
                        append(PATH).
                        append("/").
                        append(imageNames.get(i)).
                        toString());
                if (tempFile.delete() && !tempFile.exists()) {
                    successDeletedFileCounter++;
                } else {
                    failedDeletedFileCounter++;
                }
            }
        }
        if (failedDeletedFileCounter > 0) {
            noErrorResult = false;
        }
        return new DellAllPhotoResult(noErrorResult, successDeletedFileCounter, failedDeletedFileCounter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && tempFile != null) {
            if (tempFile.length() == 0) {
                if (!tempFile.delete()) {
                    Toast.makeText(this, "Unknown error, app will be closed", Toast.LENGTH_LONG).show();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }
        }
        recreate();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton(R.string.yes_dialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                })
                .setNegativeButton(R.string.no_dialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        final String PATH;
        try {
            PATH = getExternalFilesDir(DIRECTORY_PICTURES).getPath();
        } catch (NullPointerException ex) {
            Toast.makeText(getApplicationContext(), "Unknown error. Can't get access to app directory.", Toast.LENGTH_LONG).show();
            return;
        }

        File directory = new File(PATH);
        File[] files = directory.listFiles();

        if (files == null){
            Toast.makeText(getApplicationContext(), "Unknown error. App directory is not exist.", Toast.LENGTH_LONG).show();
            return;
        }

        final RecyclerView recyclerView = findViewById(R.id.image_gallery);
        recyclerView.setItemAnimator(new SlideInLeftAnimator());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.getItemAnimator().setMoveDuration(1000);
        recyclerView.getItemAnimator().setRemoveDuration(1000);
        recyclerView.getItemAnimator().setAddDuration(1000);

        final ArrayList<String> imageNamesList = Functions.prepareData(files);
        final ImageView scaledImage = findViewById(R.id.scaled_image);
        final RVAdapter adapter = new RVAdapter(getApplicationContext(), imageNamesList, PATH, scaledImage);

        ScaleInAnimationAdapter alphaAdapter = new ScaleInAnimationAdapter(adapter);
        alphaAdapter.setFirstOnly(false);
        alphaAdapter.setDuration(500);
        recyclerView.setAdapter(alphaAdapter);

        final Button takePhotoButton = findViewById(R.id.take_photo_button);
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeAPhoto(PATH, REQUEST_TAKE_PHOTO);
            }
        });

        final Button delAllPhotoButton = findViewById(R.id.delete_all_photo_button);
        delAllPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean[] isSelected = adapter.getIsSelected();
                boolean isSomethingSelected = Functions.checkArray(isSelected);

                if (!isSomethingSelected) {
                    Toast.makeText(getApplicationContext(), "No one image selected", Toast.LENGTH_LONG).show();
                    return;
                }

                DellAllPhotoResult result = deleteAllPhoto(adapter, imageNamesList, PATH);

                if (result.isResult()) {
                    Toast.makeText(getApplicationContext(), new StringBuilder().
                            append("Done.").
                            append(result.getSuccessResults()).
                            append(" files deleted.").
                            toString(), Toast.LENGTH_LONG).show();
                    recreate();
                } else {
                    Toast.makeText(getApplicationContext(), new StringBuilder().
                            append("Done. ").
                            append("Unknown error.").
                            append(result.getSuccessResults()).
                            append(" files deleted.").
                            append(result.getFailedResults()).
                            append(" files are not.").toString(), Toast.LENGTH_LONG).show();
                    recreate();
                }
            }
        });
    }
}

