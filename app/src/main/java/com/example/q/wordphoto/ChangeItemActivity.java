package com.example.q.wordphoto;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Q on 8/31/17.
 */

public class ChangeItemActivity extends WordPhotoParentActivity {
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_SELECT_PHOTO = 2;
    static final int REQUEST_SEARCH_PHOTO = 3;
    static final int REQUEST_SELECT_MUSIC = 4;

    Bitmap photo = null;
    String mCurrentPhotoPath;

    EditText wordView;
    ImageView photoView;

    // photo related methods
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                photo = getBitmap(mCurrentPhotoPath);
                photoView.setImageBitmap(photo);
            } else {
                Toast.makeText(this, "사진을 촬영하지 않았습니다.",Toast.LENGTH_SHORT).show();
            }
        } else if(requestCode == REQUEST_SELECT_PHOTO) {
            if (resultCode == RESULT_OK) {
                Uri photoUri = data.getData();
                mCurrentPhotoPath = photoUri.toString();
                photo = getBitmap(mCurrentPhotoPath);
                photoView.setImageBitmap(photo);
            } else {
                Toast.makeText(this, "사진을 선택하지 않았습니다.",Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void takePhotoByCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = createImageFile();
        Uri photoUri;

        if (photoFile != null) {
            photoUri = FileProvider.getUriForFile(this, "com.example.q.wordphoto",photoFile);
            mCurrentPhotoPath = photoUri.toString();
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(cameraIntent, REQUEST_TAKE_PHOTO);
        }
    }

    protected void selectPhotoFromAlbum() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUEST_SELECT_PHOTO);
    }

    protected void searchPhoto() {
        String url = "https://www.google.com/search?tbm=isch&q=";
        String query = wordView.getText().toString();
        Uri uri = Uri.parse(url + query);

        if(wordView.getText().toString().equals("")) {
            Toast.makeText(this, "단어를 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            Intent searchIntent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(searchIntent);
        }
    }

    protected File createImageFile() {
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "photoword_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    protected Bitmap getBitmap(String photoPath) {
        Bitmap tempPhoto = null;

        if(photoPath == null)
            return null;

        try {
            Uri imageUri = Uri.parse(photoPath);
            InputStream imageStream = null;
            imageStream = getContentResolver().openInputStream(imageUri);
            tempPhoto = BitmapFactory.decodeStream(imageStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return tempPhoto;
    }
}
