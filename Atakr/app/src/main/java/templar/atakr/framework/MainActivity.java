package templar.atakr.framework;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.BuildConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

import templar.atakr.R;
import templar.atakr.databaseobjects.User;
import templar.atakr.databaseobjects.Video;
import templar.atakr.design.AtakrPagerAdapter;
import templar.atakr.sync.VideoSyncIntentService;

/**
 * Created by Devin on 2/20/2017.
 */

public class MainActivity extends SuperActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int RC_SIGN_IN = 111;

    private Activity mActivity = this;
    private Context mContext = this;

    //Firebase related variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabaseReference;
    private DatabaseReference mVideoDatabaseReference;

    /**
    VideoSyncIntentService uses this instance of database reference so that
    we have a consistent image of the database. (I believe that) doing so means
     that we won't see duplicates of a video if, for example, the view count changes
     on the videos and then new videos are loaded in when scrolling. This database
     reference is reinitialized when a refresh is called.
     */
    public static DatabaseReference mTopVideoDatabaseReference;
    public static DatabaseReference mHotVideoDatabaseReference;
    public static DatabaseReference mNewVideoDatabaseReference;

    //Layout related variables
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    //Data variables & Variables for holding the video data
    // synced from FBDB
    public static String mUsername;
    public static ArrayList<Video> mTopVideoList = new ArrayList<>();
    public static ArrayList<Video> mHotVideoList = new ArrayList<>();
    public static ArrayList<Video> mNewVideoList = new ArrayList<>();
    public static ArrayList<Video> mGameVideoList = new ArrayList<>();

    //Variables for filtering data from Firebasedatabase
    public static long mStartTopQueryAt = 0;
    public static double mStartHotQueryAt = 0;
    public static double mStartNewQueryAt = 0;

    //For setting tab icons
    private final int images[] = new int[]{
            R.drawable.ic_top,
            R.drawable.ic_top,
            R.drawable.ic_top,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Firebase Variable initialization
        initializeDatabase();
        initializeAuthStateListener();
        //Drawer&Toolbar related initialization
        initializeDrawer(mActivity);
        //Setup ViewPager and Tabs
        initializeViewPager();
        //Begin syncing content provider with firebase
        doSync();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause(){
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop(){
        super.onStop();
        //TODO delete contents of Content Provider
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_CANCELED){
                finish();
            }else if(resultCode == RESULT_OK){
                doSync();
            }
        }
    }

    //Initializes our Authentication State Listener
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
                                    .setTheme(R.style.LoginTheme)
                                    .setLogo(R.drawable.logo)
                                    .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    //initializes our Firebase database
    private void initializeDatabase(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserDatabaseReference = mFirebaseDatabase.getReference().child("Users");
        mVideoDatabaseReference = mFirebaseDatabase.getReference().child("Videos");
    }

    //Initialize view pager along with tabs
    private void initializeViewPager(){
        mViewPager = (ViewPager)findViewById(R.id.main_viewpager);
        mViewPager.setAdapter(new AtakrPagerAdapter(getSupportFragmentManager(), this));
        mTabLayout = (TabLayout) findViewById(R.id.main_tablayout);
        mTabLayout.setupWithViewPager(mViewPager);
        /*try{
            mTabLayout.getTabAt(0).setIcon(R.drawable.ic_top);
            mTabLayout.getTabAt(1).setIcon(images[1]);
            mTabLayout.getTabAt(2).setIcon(images[2]);
        }catch(NullPointerException e){
            Log.e(TAG, "Can't set tab icon, an image is null: " + e);
        }*/
    }

    /**
    If the user is new, it creates a new user account in the Firebase database.
    Also creates SharedPreference key-value pair for username
    */
    private void initializeUser(){
        mUserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            private FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
            private String firebaseUserID = firebaseUser.getUid();
            private String firebaseDisplayName = firebaseUser.getDisplayName();
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(firebaseUserID)){
                    User user = new User(firebaseUserID, firebaseDisplayName);
                    mUserDatabaseReference.child(firebaseUserID).setValue(user);
                }
                mUsername = firebaseDisplayName;
                SharedPreferences sharedPref = mContext.getSharedPreferences(
                        getString(R.string.preference_username_key),
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.preference_username_key), mUsername);
                editor.apply();
                TextView tv = (TextView)findViewById(R.id.main_navigation_user);
                tv.setText(mUsername);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void doSync(){
        if(mTopVideoList.isEmpty() && mHotVideoList.isEmpty() && mNewVideoList.isEmpty()){
            initializeVideoSync(
                    VideoSyncIntentService.ALL_REQUEST_MG,
                    VideoSyncIntentService.NO_DELETE,
                    null
            );
        }else if(mTopVideoList.isEmpty()){
            initializeVideoSync(
                    VideoSyncIntentService.TOP_REQUEST,
                    VideoSyncIntentService.NO_DELETE,
                    null
            );
        }else if(mHotVideoList.isEmpty()){
            initializeVideoSync(
                    VideoSyncIntentService.HOT_REQUEST,
                    VideoSyncIntentService.NO_DELETE,
                    null
            );
        }else if(mNewVideoList.isEmpty()){
            initializeVideoSync(
                    VideoSyncIntentService.NEW_REQUEST,
                    VideoSyncIntentService.NO_DELETE,
                    null
            );
        }
    }

    /**
    For some reason this sync that does nothing is necessary. Taking it out
     produces null pointer exceptions. Maybe TODO?? I dunno
    */
    public void initializeVideoSync(int requestCode, int deleteCode, String title){
        Intent intent = new Intent(this, VideoSyncIntentService.class);
        intent.putExtra(VideoSyncIntentService.INTENT_REQUEST, requestCode);
        intent.putExtra(VideoSyncIntentService.INTENT_DELETE, deleteCode);
        intent.putExtra(VideoSyncIntentService.INTENT_INIT_DB, true);
        if(title == null || title.isEmpty()){
            intent.putExtra(VideoSyncIntentService.INTENT_TITLE, "");
        }else{
            intent.putExtra(VideoSyncIntentService.INTENT_TITLE, title);
        }
        startService(intent);
    }
}
