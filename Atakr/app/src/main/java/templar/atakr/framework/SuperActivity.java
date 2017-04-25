package templar.atakr.framework;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;

import templar.atakr.R;

/**
 * Created by Devin on 4/15/2017.
 */

public class SuperActivity extends AppCompatActivity {
    private static final String TAG = SuperActivity.class.getSimpleName();
    private Context mContext = this;
    private DrawerLayout mDrawerLayout;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //Inflate the main_menu; adding items to action bar if present
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();
        /*switch(id){
            case(R.id.toolbar_search):
                onSearchRequested();
            default:
                return true;
        }*/
        return true;
    }

    @Override
    public void onBackPressed(){
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    /*
    Activities using layouts that contain appbar type 1 (MainActivity) will
    initialize differently than layouts that use appbar type 2 (UserActivity)
     */
    void initializeDrawer(final Activity activity) {
        mActivity = activity;
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.super_toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );
        mDrawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.drawer_navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_menu_games) {
                    //TODO
                } else if (id == R.id.navigation_menu_game_genre) {
                    //TODO
                } else if (id == R.id.navigation_menu_video_genre) {
                    //TODO
                } else if (id == R.id.navigation_menu_signout) {
                    AuthUI.getInstance().signOut(activity);
                } else if (id == R.id.navigation_menu_profile){
                    if(mActivity.getClass() != ProfileActivity.class) {
                        Intent intent = new Intent(mContext, ProfileActivity.class);
                        startActivity(intent);
                    }
                }

                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }
}
