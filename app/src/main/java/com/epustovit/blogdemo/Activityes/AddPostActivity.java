package com.epustovit.blogdemo.Activityes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.epustovit.blogdemo.Model.Blog;
import com.epustovit.blogdemo.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {
    private ImageButton mPostImage;
    private EditText mPostTitle;
    private EditText mPostDescription;
    private Button mSubmitButton;
    private DatabaseReference mPostDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    //for Progress dialog
    private ProgressDialog mProgress;

    // для выбора фото из галереи
    private Uri mImageUri;
    private static final int GALLERY_CODE = 1;
    // для хранения в базе
    private StorageReference mStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        mProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance().getReference();

        mPostDatabase = FirebaseDatabase.getInstance().getReference().child("MBlog");

        mPostImage = (ImageButton)findViewById(R.id.imageButton);
        mPostTitle = (EditText)findViewById(R.id.postTitleEt);
        mPostDescription = (EditText)findViewById(R.id.descriptionEt);
        mSubmitButton = (Button)findViewById(R.id.submitPost);

        /**
         * Для загрузки фото
         */
        mPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // выбрать фото из галереи
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");   // выбрать все типы изображений
                startActivityForResult(galleryIntent, GALLERY_CODE);
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Posting to our database
                startPosting();
            }
        });
    }

    /**
     *
     * Для загрузки фото
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            mImageUri = data.getData();
            mPostImage.setImageURI(mImageUri);
        }
    }

    private void startPosting() {

        mProgress.setMessage("Posting to blog...");
        mProgress.show();

        final String titleVal = mPostTitle.getText().toString().trim();
        final String descVal = mPostDescription.getText().toString().trim();

        if (!TextUtils.isEmpty(titleVal) && !TextUtils.isEmpty(descVal) && mImageUri != null){
            //start the uploading...
            //mImageUri.getLastPathSegment() == /image/myphoto.jpeg
            final StorageReference filepath = mStorage.child("MBlog_images")
                    .child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    DatabaseReference newPost = mPostDatabase.push(); // create new item with unique reference

                    Map<String, String> dataToSave = new HashMap<>();
                    // save data with variables ve are create in Blog class
                    dataToSave.put("title", titleVal);
                    dataToSave.put("description", descVal);
                    dataToSave.put("image",downloadUrl.toString());
                    dataToSave.put("timestamp", String.valueOf(java.lang.System.currentTimeMillis()));
                    dataToSave.put("userId", mUser.getUid());

                    // после того как сконструировали HashMap
                    newPost.setValue(dataToSave);
                    /**
                     * сделали как раньше
                     * newPost.child("title").setValue(titleVal);
                     * newPost.child("desc").setValue(descrVal);
                     * newPost.child("image").setValue(downloadUrl.toString());
                     * newPost.child("timestamp").setValue(java.lang.System.currentTimeMillis());
                     */

                    mProgress.dismiss();

                    startActivity(new Intent(AddPostActivity.this, PostListActivity.class));
                    finish();

                }
            });
        }

    }
}
