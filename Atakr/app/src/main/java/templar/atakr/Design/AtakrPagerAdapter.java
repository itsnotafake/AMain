package templar.atakr.design;

import android.content.Context;
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
    private Context mContext;
    private final int PAGE_COUNT = 3;

    private String tabTitles[] = new String[] {
            "Top",
            "Trending",
            "New"
    };

    public AtakrPagerAdapter(FragmentManager fm, Context context){
        super(fm);
        mContext = context;
    }

    @Override
    public int getCount(){
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position){
        switch (position){
            case 0:
                return VideoBrowseFragment.newInstance(position);
            case 1:
                return VideoBrowseFragment.newInstance(position);
            case 2:
                return VideoBrowseFragment.newInstance(position);
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position){
        return tabTitles[position];
    }
}
