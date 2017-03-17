package templar.atakr.design;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import templar.atakr.VideoBrowseFragment;

/**
 * Created by Devin on 3/15/2017.
 */

public class AtakrPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;
    final int PAGE_COUNT = 3;

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
        return VideoBrowseFragment.newInstance(position+1);
    }

    @Override
    public CharSequence getPageTitle(int position){
        return tabTitles[position];
    }
}
