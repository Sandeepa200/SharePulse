package com.example.sharepulse;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;

public class ImagePickerHelper {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Activity activity;
    private ImagePickerCallback callback;

    public ImagePickerHelper(Activity activity, ImagePickerCallback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    public void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), imageUri);
                callback.onImageSelected(bitmap, imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface ImagePickerCallback {
        void onImageSelected(Bitmap bitmap, Uri imageUri);
    }
}

