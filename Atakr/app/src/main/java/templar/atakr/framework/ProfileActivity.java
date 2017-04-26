package templar.atakr.framework;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.List;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import templar.atakr.R;

/**
 * Created by Devin on 4/16/2017.
 */

public class ProfileActivity extends SuperActivity{

    private final String TAG = ProfileActivity.class.getSimpleName();
    private Activity mActivity = this;

    private final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 100;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 101;
    private final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_AND_CAMERA = 200;

    private StorageReference mStorageReference;
    private StorageReference mUserReference;
    private StorageReference mProfilePictureReference;
    private String mUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeFirebaseStorage();
        initializeDrawer(mActivity);
        initializeHeader();
    }

    private void initializeFirebaseStorage(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        mStorageReference = storage.getReference();
        SharedPreferences sharedPreferences = mActivity.getSharedPreferences(
                getString(R.string.preference_userID_key),
                Context.MODE_PRIVATE);
        mUserID = sharedPreferences.getString(getString(R.string.preference_userID_key), "DEFAULT");
        mUserReference = mStorageReference.child(mUserID);
        mProfilePictureReference = mUserReference.child("profilepicture.jpg");
    }

    private void initializeHeader(){
        ImageView profile_image = (ImageView) findViewById(R.id.profile_picture);
        TextView username_tv = (TextView) findViewById(R.id.profile_username);
        TextView shared_tv = (TextView) findViewById(R.id.profile_shared);
        TextView followers_tv = (TextView) findViewById(R.id.profile_followers);

        SharedPreferences sharedPref = mActivity.getSharedPreferences(
                getString(R.string.preference_username_key),
                Context.MODE_PRIVATE);
        String username = sharedPref.getString(getString(R.string.preference_username_key), "DEFAULT");

        username_tv.setText(username);
        shared_tv.setText(getString(R.string.profile_shared_none));
        followers_tv.setText(getString(R.string.profile_followers_none));

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleProfilePicturePermissions();
            }
        });
    }

    /**
     * When the profile picture is clicked, we check to make sure that all of the necessary permissions are enabled
     * If the user doesn't want to accept the permissions, then we quit the profile picture selection process
     */
    private void handleProfilePicturePermissions(){
        if(ContextCompat.checkSelfPermission
                (mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                    mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    mActivity,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_AND_CAMERA
            );
        }
        else if(ContextCompat.checkSelfPermission(mActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    mActivity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
            );
        }else if(ContextCompat.checkSelfPermission(mActivity,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    mActivity,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA
            );
        }else{
            handleProfilePicture();
        }
    }

    /**
     * After permissions are request and set, we use the easyimage library to handle getting or taking
     * a profile picture
     */
    private void handleProfilePicture(){
        EasyImage.openChooserWithDocuments(mActivity, null, 0);
    }

    /**
     * Callback method after ActivityCompat.requestPermissions in handleProfilePicturePermissions.
     * Determines what to do if we get a GRANTED OR DENIED
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults){
        switch(requestCode){
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_AND_CAMERA:
                if(grantResults.length > 1 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    handleProfilePicture();
                }
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if(grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(ContextCompat.checkSelfPermission(mActivity,
                            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                        handleProfilePicture();
                    }
                }
                break;
            case MY_PERMISSIONS_REQUEST_CAMERA:
                if(grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(mActivity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED){
                        handleProfilePicture();
                    }
                }
                break;
            default:
                Log.e(TAG, "Not a valid Permissions Result Request Code");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type){
                Log.e(TAG, "Exception: " + e + ", ImagePickerError");
            }
            @Override
            public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
                onPhotosReturned(imageFiles);
            }
        });
    }

    /**
     * Handles the selected image. Uploads it to FirebaseStorage then sets the profile picture.
     * @param imageFiles
     */
    private void onPhotosReturned(List<File> imageFiles){
        Uri picture = android.net.Uri.parse(imageFiles.get(0).toURI().toString());
        UploadTask uploadTask = mProfilePictureReference.putFile(picture);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Hello, BEEP BOOP, uploadTask failure");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.e(TAG, "Hello, BEEP BOOP LETTUCE, uploadTask success");
            }
        });
    }



}
