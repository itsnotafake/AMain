package templar.atakr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import templar.atakr.DatabaseObjects.User;
import templar.atakr.DatabaseObjects.Video;
import templar.atakr.YouTube.YoutubeData;

public class ShareActivity extends AppCompatActivity {
    private static final String TAG = ShareActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 111;

    //Firebase related variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mVideoDatabaseReference;
    private DatabaseReference mUserDatabaseReference;

    //Youtube variables
    private String mYoutubeUrl;
    private String mYoutubeId;
    private String mYoutubeTitle;
    private String mAtakrTitle;
    private String mYoutubeUploader;
    private String mYoutubeViews;
    private String mYoutubeThumbnailUrl;

    //Layout variables
    private ProgressBar mProgressBar;
    private ImageView mShareYoutubeThumbnail;
    private EditText mAtakrTitleEditText;
    private EditText mGameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_activity);

        TextView shareUsername = (TextView) findViewById(R.id.share_username);
        mShareYoutubeThumbnail = (ImageView) findViewById(R.id.share_youtubeThumbnail);
        TextView shareYoutubeTitle = (TextView) findViewById(R.id.share_youtubeTitle);
        TextView shareYoutubeUploader = (TextView) findViewById(R.id.share_youtubeUploader);
        TextView shareYoutubeViews = (TextView) findViewById(R.id.share_youtubeViews);
        mAtakrTitleEditText = (EditText) findViewById(R.id.share_atakrtitle_edittext);

        Button shareButton = (Button) findViewById(R.id.share_button);
        mProgressBar = (ProgressBar) findViewById(R.id.share_progress);


        //After the layout is setup, we insure the user is logged in
        initializeAuthStateListener();
        shareUsername.setText(MainActivity.mUsername);

        //get action_send intent and Youtube url from it
        Intent intent = getIntent();
        if(intent != null){
            mYoutubeUrl = intent.getStringExtra(Intent.EXTRA_TEXT);
            mYoutubeId = getYoutubeVideoId(mYoutubeUrl);
            Bundle videoContent = new Bundle();

            try {
                videoContent = getVideoValues();
            }catch(InterruptedException e){
                Log.e(TAG, "Exception: " + e);
            }
            if(!videoContent.isEmpty()) {
                mYoutubeTitle = videoContent.getString(YoutubeData.VIDEO_TITLE);
                mYoutubeUploader = videoContent.getString(YoutubeData.VIDEO_UPLOADER);
                mYoutubeViews = videoContent.getString(YoutubeData.VIDEO_VIEWS);
                mYoutubeThumbnailUrl = videoContent.getString(YoutubeData.VIDEO_THUMBNAIL);

                loadThumbnail(mYoutubeThumbnailUrl);

                //Title and View have edited text rather than the raw values,
                //so we create the editted strings and then place them in their
                //respective text views
                String titleString = titleLimit(mYoutubeTitle);
                String viewString = mYoutubeViews + " Views";

                shareYoutubeTitle.setText(titleString);
                shareYoutubeUploader.setText(mYoutubeUploader);
                shareYoutubeViews.setText(viewString);
            }

            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initializeDatabase();
                    addToDatabase();
                    finish();
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_CANCELED){
                finish();
            }
        }
    }

    private void initializeAuthStateListener(){
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if(firebaseUser != null){
                    initializeUser();
                }else{
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .setTheme(R.style.AppTheme)
                                    .setIsSmartLockEnabled(false)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    private void initializeDatabase(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mVideoDatabaseReference = mFirebaseDatabase.getReference().child("Videos");
    }

    private void addToDatabase(){
        mVideoDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(mYoutubeId)) {
                    mAtakrTitle = mAtakrTitleEditText.getText().toString();
                    Video video = new Video(
                            mYoutubeId,
                            mYoutubeUrl,
                            mYoutubeTitle,
                            mAtakrTitle,
                            mFirebaseAuth.getCurrentUser().getUid(),
                            mYoutubeThumbnailUrl,
                            String.valueOf(1)
                    );
                    mVideoDatabaseReference.child(mYoutubeId).setValue(video);
                    Toast.makeText(
                            getApplicationContext(),
                            "Video Posted!",
                            Toast.LENGTH_SHORT
                    ).show();
                }else{
                    Toast.makeText(
                            getApplicationContext(),
                            "This video has already been uploaded",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void initializeUser(){
        mUserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            private FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
            private String firebaseUserID = firebaseUser.getUid();
            private String firebaseDisplayName = firebaseUser.getDisplayName();
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MainActivity.mUsername = firebaseDisplayName;
                if(!dataSnapshot.hasChild(firebaseUserID)){
                    User user = new User(firebaseUserID, firebaseDisplayName);
                    mUserDatabaseReference.child(firebaseUserID).setValue(user);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private String getYoutubeVideoId(String youtubeURL){
        int index = youtubeURL.lastIndexOf("/");
        return youtubeURL.substring(index+1);
    }

    //Use a future to get Youtube Video Values
    private Bundle getVideoValues() throws InterruptedException {
        mProgressBar.setVisibility(View.VISIBLE);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        FutureTask<Bundle> future = new FutureTask<>(new Callable<Bundle>() {
            @Override
            public Bundle call() throws Exception {
                return YoutubeData.getVideoValues(mYoutubeId);
            }
        });
        executorService.execute(future);
        try{
            return future.get();
        }catch(ExecutionException e){
            Log.e(TAG, "Exception: " + e);
            return null;
        }
    }

    //Use a future to get thumbnail image and load it into the imageview
    private void loadThumbnail(final String url_string){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        FutureTask<Bitmap> future = new FutureTask<>(new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {
                try{
                    URL url = new URL(url_string);
                    return BitmapFactory.decodeStream(url.openConnection().getInputStream());
                }catch (MalformedURLException e){
                    Log.e(TAG, "MalformedException " + e);
                }catch(IOException e){
                    Log.e(TAG, "IOException " + e);
                }
                return null;
            }
        });
        executorService.execute(future);
        try {
            mShareYoutubeThumbnail.setImageBitmap(future.get());

            //Remove the Progress Bar at this point in time
            mProgressBar.setVisibility(View.GONE);
        }catch(ExecutionException e){
            Log.e(TAG, "ExecutionExcpetion " + e);
        }catch(InterruptedException e){
            Log.e(TAG, "InterruptedException " + e);
        }
    }

    private static String titleLimit(String title){
        if(title.length() > 20){
            title = title.substring(0,19) + "...";
        }
        return title;
    }
}
