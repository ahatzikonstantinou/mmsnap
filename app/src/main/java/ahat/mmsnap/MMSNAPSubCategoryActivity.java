package ahat.mmsnap;


import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Resources;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class MMSNAPSubCategoryActivity extends AppCompatActivity
{
    public static class MMSNAPSubCategoryPagerAdapter extends FragmentPagerAdapter
    {
        private static int NUM_ITEMS = 5;

        public MMSNAPSubCategoryPagerAdapter( FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem( int position) {
            switch (position) {
                case 0:
                    return MMSNAPSubCategoryFragment.newInstance( 0,  R.string.what_this_up_is_for, R.string.what_this_up_is_for_text, R.color.how_to_use_this_app );
                case 1:
                    return MMSNAPSubCategoryFragment.newInstance(1,  R.string.about_multimorbidity_mm, R.string.about_multimorbidity_mm_text, R.color.about_multimorbidity_mm );
                case 2:
                    return MMSNAPSubCategoryFragment.newInstance(2,  R.string.about_multibehaviour_mb, R.string.about_multibehaviour_mb_text, R.color.about_multibehaviour_mb );
                case 3:
                    return MMSNAPSubCategoryFragment.newInstance(3,  R.string.the_association_between_mm_mb, R.string.the_association_between_mm_mb_text, R.color.the_association_between_mm_mb );
                case 4:
                    return MMSNAPSubCategoryFragment.newInstance(4,  R.string.how_to_use_this_app, R.string.how_to_use_this_app_text, R.color.how_to_use_this_app );
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }

    }

    FragmentPagerAdapter adapterViewPager;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_mmsnapsub_category );

        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        ViewPager vpPager = (ViewPager) findViewById( R.id.mmsnap_subcategory_viewpager );
        adapterViewPager = new MMSNAPSubCategoryPagerAdapter( getSupportFragmentManager() );
        vpPager.setAdapter( adapterViewPager );

        TabLayout tabLayout = (TabLayout) findViewById( R.id.mmsnap_subcategory_tablayout);
        tabLayout.setupWithViewPager( vpPager, true);

        Bundle b = getIntent().getExtras();
        int value = -1;
        if(b != null)
        {
            value = b.getInt("subcategory");
            if( value >=0 && value < MMSNAPSubCategoryPagerAdapter.NUM_ITEMS )
            {
                vpPager.setCurrentItem( value );
            }
        }
    }
}
