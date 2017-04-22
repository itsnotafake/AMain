package templar.atakr.framework;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import templar.atakr.R;
import templar.atakr.databaseobjects.Video;
import templar.atakr.design.VideoAdapter;

/**
 * Created by Devin on 3/29/2017.
 */

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = SearchActivity.class.getName();

    private Activity mActivity;
    private Context mContext;
    private VideoAdapter mVideoAdapter;
    private ProgressBar mProgressBar;
    public static ArrayList<Video> mSearchList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mActivity = this;
        mContext = this;
        mVideoAdapter = new VideoAdapter(mContext, 3);

        //initialize drawer
        initializeDrawer();

        //Handle spinning progress bar
        mProgressBar = (ProgressBar) findViewById(R.id.search_progress);
        mProgressBar.setVisibility(View.VISIBLE);

        //Set RecyclerView
        initializeRecyclerView();

        //handle search query
        doSearch(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
        mSearchList.clear();
        mVideoAdapter.notifyDataSetChanged();
        mProgressBar.setVisibility(View.VISIBLE);
        doSearch(intent);
    }

    private void initializeDrawer() {
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.search_drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.super_toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.search_drawer_navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_menu_signout) {
                    AuthUI.getInstance().signOut(mActivity);
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }

    private void initializeRecyclerView(){
        final int columns = getResources().getInteger(R.integer.main_list_columns);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, columns);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.search_recyclerview);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mVideoAdapter);
    }

    private void doSearch(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.e(TAG, "Query is " + query);
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference videoReference = firebaseDatabase.getReference().child("Videos");

            Query searchVideoQueryYTitle = videoReference
                    .orderByChild("youtubeName")
                    .equalTo(query, "youtubeName");
            Query searchVideoQueryATitle = videoReference.equalTo(query, "atakrName");

            searchVideoQueryYTitle.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot videoSnapshot : dataSnapshot.getChildren()) {
                        Video video = videoSnapshot.getValue(Video.class);
                        mSearchList.add(video);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            searchVideoQueryATitle.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot videoSnapshot : dataSnapshot.getChildren()) {
                        Video video = videoSnapshot.getValue(Video.class);
                        if (!mSearchList.contains(video)) {
                            mSearchList.add(video);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            mProgressBar.setVisibility(View.GONE);
            mVideoAdapter.notifyDataSetChanged();
        }
    }
}
