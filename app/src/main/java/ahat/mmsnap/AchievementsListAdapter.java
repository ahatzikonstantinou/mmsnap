package ahat.mmsnap;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import ahat.mmsnap.models.DailyEvaluation;

import static ahat.mmsnap.ApplicationStatus.Behavior.ACTIVITY;
import static ahat.mmsnap.ApplicationStatus.Behavior.ALCOHOL;
import static ahat.mmsnap.ApplicationStatus.Behavior.DIET;
import static ahat.mmsnap.ApplicationStatus.Behavior.SMOKING;

public class AchievementsListAdapter extends ArrayAdapter
{
    class Achievement
    {
//        class BehaviorScore
//        {
//            public ApplicationStatus.Behavior behavior;
//            int score;
//        }
        public int year;
        public int weekOfYear;
//        public ArrayList<BehaviorScore> behaviorScores = new ArrayList<>();
        public HashMap<ApplicationStatus.Behavior, Integer> behaviorScores = new HashMap<>();
        public void addPoint( ApplicationStatus.Behavior behavior )
        {
            if( !behaviorScores.containsKey( behavior ) )
            {
                behaviorScores.put( behavior, 1 );
            }
            else
            {
                behaviorScores.put( behavior, behaviorScores.get( behavior ) + 1 );
            }
        }
    }

    private Activity                         context;
    private final ArrayList<DailyEvaluation> evaluations;

    private ArrayList<Achievement> achievements = new ArrayList<>();

    public AchievementsListAdapter( @NonNull Activity context, ArrayList<DailyEvaluation> evaluations, int resource )
    {
        super( context, resource );
        this.context = context;
        this.evaluations = evaluations;

        achievements = generateAchievements( evaluations );
    }

    private ArrayList<Achievement> generateAchievements( ArrayList<DailyEvaluation> evaluations )
    {
        ArrayList<Achievement> achievements = new ArrayList<>();
        try
        {
            ApplicationStatus as = ApplicationStatus.getInstance( context );
            Calendar start = Calendar.getInstance();
            start.setTime( as.getStartDate() );
            Calendar now = Calendar.getInstance();

            for( Calendar cal = start ; now.after( cal ) ; cal.add( Calendar.WEEK_OF_YEAR, 1 ) )
            {
                Achievement achievement = new Achievement();
                achievement.year = cal.get( Calendar.YEAR );
                achievement.weekOfYear = cal.get( Calendar.WEEK_OF_YEAR );
                for( DailyEvaluation evaluation : evaluations )
                {
                    if( evaluation.isEvaluated() && evaluation.isSuccessful() &&
                        evaluation.plan.year == cal.get( Calendar.YEAR ) && evaluation.plan.weekOfYear == cal.get( Calendar.WEEK_OF_YEAR )
                    )
                    {
                        for( ApplicationStatus.Behavior behavior : evaluation.plan.targetBehaviors )
                        {
                            achievement.addPoint( behavior );
                        }
                    }
                }
                achievements.add( achievement );
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
            Snackbar.make( context.findViewById( android.R.id.content ), "Could not load daily evaluations", Snackbar.LENGTH_SHORT ).show();
        }
        return achievements;
    }

    @Override
    public int getCount()
    {
        return achievements.size();
    }

    public View getView( int position, View view, ViewGroup parent)
    {
        LayoutInflater inflater = context.getLayoutInflater();

        view = inflater.inflate( R.layout.achievements_list_item, null, true );


        DateFormatSymbols dfs = new DateFormatSymbols();

        Achievement achievement = achievements.get( position );
        Calendar startCal = Calendar.getInstance();
        startCal.set( Calendar.YEAR, achievement.year );
        startCal.set( Calendar.WEEK_OF_YEAR, achievement.weekOfYear );

        Calendar c = Calendar.getInstance();
        c.set( Calendar.YEAR, startCal.get( Calendar.YEAR ) );
        c.set( Calendar.WEEK_OF_YEAR, startCal.get( Calendar.WEEK_OF_YEAR ) );
        c.set( Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() );   // go to the first day of this week

        TextView start = view.findViewById( R.id.startdate );
        start.setText( dfs.getShortWeekdays()[ c.get( Calendar.DAY_OF_WEEK ) ]+ " " + c.get( Calendar.DAY_OF_MONTH ) + " " +
                       dfs.getMonths()[ c.get( Calendar.MONTH ) ] + " " + c.get( Calendar.YEAR ) );

        c.add( Calendar.DATE, 6);
        TextView end = view.findViewById( R.id.endDate );
        end.setText( dfs.getShortWeekdays()[ c.get( Calendar.DAY_OF_WEEK ) ]+ " " + c.get( Calendar.DAY_OF_MONTH ) + " " +
                       dfs.getMonths()[ c.get( Calendar.MONTH ) ] + " " + c.get( Calendar.YEAR ) );

//        view.findViewById( R.id.diet_layout ).setVisibility( achievement.behaviorScores.containsKey( DIET ) ? View.VISIBLE : View.GONE );
//        view.findViewById( R.id.activity_layout ).setVisibility( achievement.behaviorScores.containsKey( ACTIVITY ) ? View.VISIBLE : View.GONE );
//        view.findViewById( R.id.alcohol_layout ).setVisibility( achievement.behaviorScores.containsKey( ALCOHOL ) ? View.VISIBLE : View.GONE );
//        view.findViewById( R.id.smoking_layout ).setVisibility( achievement.behaviorScores.containsKey( SMOKING ) ? View.VISIBLE : View.GONE );
//
//        if( achievement.behaviorScores.containsKey( DIET ) )
//        {
//            view.findViewById( R.id.diet_success_img ).setVisibility( achievement.behaviorScores.get( DIET ) == ApplicationStatus.MIN_ACTIVE_PLANS_PER_WEEK ? View.VISIBLE : View.GONE );
//            view.findViewById( R.id.diet_fail_img ).setVisibility( achievement.behaviorScores.get( DIET ) < ApplicationStatus.MIN_ACTIVE_PLANS_PER_WEEK ? View.VISIBLE : View.GONE );
//            view.findViewById( R.id.diet_achievement_img ).setVisibility( achievement.behaviorScores.get( DIET ) > ApplicationStatus.MIN_ACTIVE_PLANS_PER_WEEK ? View.VISIBLE : View.GONE );
//            LinearLayout scoreLayout = view.findViewById( R.id.diet_score_layout );
//            for( int i = 0 ; i < achievement.behaviorScores.get( DIET ) ; i++ )
//            {
//                ImageView badge = new ImageView( context );
//                int srcCheck = R.drawable.ic_check_24dp;
//                int srcAchievement = R.drawable.ic_achievement_light_24dp;
//                badge.setImageResource( i < ApplicationStatus.MIN_ACTIVE_PLANS_PER_WEEK ? srcCheck : srcAchievement );
//                scoreLayout.addView( badge );
//            }
//        }

        setScores( achievement, DIET, view, R.id.diet_layout, R.id.diet_success_img, R.id.diet_fail_img, R.id.diet_achievement_img, R.id.diet_score_layout );
        setScores( achievement, ACTIVITY, view, R.id.activity_layout, R.id.activity_success_img, R.id.activity_fail_img, R.id.activity_achievement_img, R.id.activity_score_layout );
        setScores( achievement, ALCOHOL, view, R.id.alcohol_layout, R.id.alcohol_success_img, R.id.alcohol_fail_img, R.id.alcohol_achievement_img, R.id.alcohol_score_layout );
        setScores( achievement, SMOKING, view, R.id.smoking_layout, R.id.smoking_success_img, R.id.smoking_fail_img, R.id.smoking_achievement_img, R.id.smoking_score_layout );


        return view;
    }

    private void setScores( Achievement achievement, ApplicationStatus.Behavior behavior, View view, int behaviorLayoutResId, int successImgResId, int failImgResId, int achievementImgResId, int scoreLayoutResId )
    {
        view.findViewById( behaviorLayoutResId ).setVisibility( achievement.behaviorScores.containsKey( behavior ) ? View.VISIBLE : View.GONE );

        if( achievement.behaviorScores.containsKey( behavior ) )
        {
            view.findViewById( successImgResId ).setVisibility( achievement.behaviorScores.get( behavior ) == ApplicationStatus.MIN_ACTIVE_PLANS_PER_WEEK ? View.VISIBLE : View.GONE );
            view.findViewById( failImgResId ).setVisibility( achievement.behaviorScores.get( behavior ) < ApplicationStatus.MIN_ACTIVE_PLANS_PER_WEEK ? View.VISIBLE : View.GONE );
            view.findViewById( achievementImgResId ).setVisibility( achievement.behaviorScores.get( behavior ) > ApplicationStatus.MIN_ACTIVE_PLANS_PER_WEEK ? View.VISIBLE : View.GONE );
            LinearLayout scoreLayout = view.findViewById( scoreLayoutResId );
            for( int i = 0 ; i < achievement.behaviorScores.get( behavior ) ; i++ )
            {
                ImageView badge = new ImageView( context );
                int srcCheck = R.drawable.ic_check_24dp;
                int srcAchievement = R.drawable.ic_achievement_light_24dp;
                badge.setImageResource( i < ApplicationStatus.MIN_ACTIVE_PLANS_PER_WEEK ? srcCheck : srcAchievement );
                scoreLayout.addView( badge );
            }
        }

    }
}
