package templar.atakr.framework;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initializeDrawer(mActivity);
        initializeHeader();
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

}
