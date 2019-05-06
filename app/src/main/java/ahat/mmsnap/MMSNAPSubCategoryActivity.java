package ahat.mmsnap;


import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class MMSNAPSubCategoryActivity extends AppCompatActivity
{

//    public static class MMSNAPSubCategoryPagerAdapter extends FragmentPagerAdapter
//    {
//        private static int NUM_ITEMS = 5;
//
//        public MMSNAPSubCategoryPagerAdapter( FragmentManager fragmentManager) {
//            super(fragmentManager);
//        }
//
//        // Returns total number of pages
//        @Override
//        public int getCount() {
//            return NUM_ITEMS;
//        }
//
//        // Returns the fragment to display for that page
//        @Override
//        public Fragment getItem( int position) {
//            switch (position) {
//                case 0:
//                    return MMSNAPSubCategoryFragment.newInstance( 0,  R.string.what_this_app_is_for, R.string.what_this_up_is_for_text, R.color.how_to_use_this_app );
//                case 1:
//                    return MMSNAPSubCategoryFragment.newInstance(1,  R.string.about_multimorbidity_mm, R.string.about_multimorbidity_mm_text, R.color.about_multimorbidity_mm );
//                case 2:
//                    return MMSNAPSubCategoryFragment.newInstance(2,  R.string.about_multibehaviour_mb, R.string.about_multibehaviour_mb_text, R.color.about_multibehaviour_mb );
//                case 3:
//                    return MMSNAPSubCategoryFragment.newInstance(3,  R.string.the_association_between_mm_mb, R.string.the_association_between_mm_mb_text, R.color.the_association_between_mm_mb );
//                case 4:
//                    return MMSNAPSubCategoryFragment.newInstance(4,  R.string.how_to_use_this_app, R.string.how_to_use_this_app_text, R.color.how_to_use_this_app );
//                default:
//                    return null;
//            }
//        }
//
//        // Returns the page title for the top indicator
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return "";
//        }
//
//    }

//    FragmentPagerAdapter adapterViewPager;

    enum Section { MMSNAP, EDU }
    private static final int MMSNAP_SUBCATEGORIES = 5;
    private static final int EDU_SUBCATEGORIES = 3;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_mmsnapsub_category );

        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

//        ahat: replaced view pager because user experience was inconsistent with other sections of the app
//        ViewPager vpPager = (ViewPager) findViewById( R.id.mmsnap_subcategory_viewpager );
//        adapterViewPager = new MMSNAPSubCategoryPagerAdapter( getSupportFragmentManager() );
//        vpPager.setAdapter( adapterViewPager );

//        TabLayout tabLayout = (TabLayout) findViewById( R.id.mmsnap_subcategory_tablayout);
//        tabLayout.setupWithViewPager( vpPager, true);

//                vpPager.setCurrentItem( value );

        Bundle b = getIntent().getExtras();
        int value = -1;
        if(b != null)
        {
            Section section = (Section) b.get( "section" );
            value = b.getInt("subcategory");
            Fragment f = null;
            switch( section )
            {
                case MMSNAP:
                    if( value < 0 || value >= MMSNAP_SUBCATEGORIES )
                    {
                        return;
                    }

                    getSupportActionBar().setTitle( R.string.title_activity_mmsnap );
                    getSupportActionBar().setIcon( getResources().getDrawable( R.drawable.mmsnap_section_logo ) );

                    switch( value )
                    {
                        case 0:
                            f = MMSNAPSubCategoryFragment.newInstance( 0,  R.string.what_this_app_is_for, R.string.what_this_up_is_for_text, R.color.how_to_use_this_app );
                            getSupportActionBar().setSubtitle( R.string.what_this_app_is_for );
                            getSupportActionBar().setBackgroundDrawable( new ColorDrawable( getResources().getColor( R.color.how_to_use_this_app ) ) );
                            getSupportActionBar().setElevation(0);
                            break;
                        case 1:
                            f = MMSNAPSubCategoryFragment.newInstance(1,  R.string.about_multimorbidity_mm, R.string.about_multimorbidity_mm_text, R.color.about_multimorbidity_mm );
                            getSupportActionBar().setSubtitle( R.string.about_multimorbidity_mm );
                            getSupportActionBar().setBackgroundDrawable( new ColorDrawable( getResources().getColor( R.color.about_multimorbidity_mm ) ) );
                            getSupportActionBar().setElevation(0);
                            break;
                        case 2:
                            f = MMSNAPSubCategoryFragment.newInstance(2,  R.string.about_multibehaviour_mb, R.string.about_multibehaviour_mb_text, R.color.about_multibehaviour_mb );
                            getSupportActionBar().setSubtitle( R.string.about_multibehaviour_mb );
                            getSupportActionBar().setBackgroundDrawable( new ColorDrawable( getResources().getColor( R.color.about_multibehaviour_mb ) ) );
                            getSupportActionBar().setElevation(0);
                            break;
                        case 3:
                            f = MMSNAPSubCategoryFragment.newInstance(3,  R.string.the_association_between_mm_mb, R.string.the_association_between_mm_mb_text, R.color.the_association_between_mm_mb );
                            getSupportActionBar().setSubtitle( R.string.the_association_between_mm_mb );
                            getSupportActionBar().setBackgroundDrawable( new ColorDrawable( getResources().getColor( R.color.the_association_between_mm_mb ) ) );
                            getSupportActionBar().setElevation(0);
                            break;
                        case 4:
                            f = MMSNAPSubCategoryFragment.newInstance(4,  R.string.how_to_use_this_app, R.string.how_to_use_this_app_text, R.color.how_to_use_this_app );
                            getSupportActionBar().setSubtitle( R.string.how_to_use_this_app );
                            getSupportActionBar().setBackgroundDrawable( new ColorDrawable( getResources().getColor( R.color.how_to_use_this_app ) ) );
                            getSupportActionBar().setElevation(0);
                            break;
                        default:
                            break;
                    }
                    break;
                case EDU:
                    if( value < 0 || value >= EDU_SUBCATEGORIES )
                    {
                        return;
                    }

                    getSupportActionBar().setTitle( R.string.title_activity_edu );
                    getSupportActionBar().setIcon( getResources().getDrawable( R.drawable.edu_section_logo) );

                    switch( value )
                    {
                        case 0:
                            f = MMSNAPSubCategoryFragment.newInstance( 0,  R.string.what_is_the_if_then_statements, R.string.what_is_the_if_then_statements_text, R.color.edu_what_btn );
                            getSupportActionBar().setSubtitle( R.string.what_is_the_if_then_statements );
                            getSupportActionBar().setBackgroundDrawable( new ColorDrawable( getResources().getColor( R.color.edu_what_btn ) ) );
                            getSupportActionBar().setElevation(0);
                            break;
                        case 1:
                            f = MMSNAPSubCategoryFragment.newInstance( 1,  R.string.how_to_word_effective_if, R.string.how_to_word_effective_if_text, R.color.edu_if_btn );
                            getSupportActionBar().setSubtitle( R.string.how_to_word_effective_if );
                            getSupportActionBar().setBackgroundDrawable( new ColorDrawable( getResources().getColor( R.color.edu_if_btn ) ) );
                            getSupportActionBar().setElevation(0);
                            break;
                        case 2:
                            f = MMSNAPSubCategoryFragment.newInstance( 2,  R.string.how_to_word_effective_then, R.string.how_to_word_effective_then_text, R.color.edu_then_btn );
                            getSupportActionBar().setSubtitle( R.string.how_to_word_effective_then );
                            getSupportActionBar().setBackgroundDrawable( new ColorDrawable( getResources().getColor( R.color.edu_then_btn ) ) );
                            getSupportActionBar().setElevation(0);
                            break;
                        default:
                            break;
                    }
                    break;
            }


            FragmentManager fm = getSupportFragmentManager();

            // Begin Fragment transaction.
            FragmentTransaction fragmentTransaction = fm.beginTransaction();

            // Replace the layout holder with the required Fragment object.
            fragmentTransaction.replace( R.id.mmsnap_subcategory_fragment, f );

            // Commit the Fragment replace action.
            fragmentTransaction.commit();

        }

    }
}
