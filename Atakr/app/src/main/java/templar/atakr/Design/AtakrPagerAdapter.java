package templar.atakr.design;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import templar.atakr.framework.VideoBrowseFragment;

/**
 * Created by Devin on 3/15/2017.
 */

public class AtakrPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = AtakrPagerAdapter.class.getName();
    private final int PAGE_COUNT = 3;

    private final String tabTitles[] = new String[] {
            "Top",
            "Trending",
            "New"
    };

    public AtakrPagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public int getCount(){
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position){
        return VideoBrowseFragment.newInstance(position);
    }

    @Override
    public CharSequence getPageTitle(int position){
        return tabTitles[position];
    }
}
