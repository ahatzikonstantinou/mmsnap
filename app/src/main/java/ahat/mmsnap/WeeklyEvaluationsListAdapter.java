package ahat.mmsnap;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;

import ahat.mmsnap.Models.WeeklyEvaluation;

public class WeeklyEvaluationsListAdapter extends ArrayAdapter
{
    private Activity                          context;
    private final ArrayList<WeeklyEvaluation> evaluations;

    public WeeklyEvaluationsListAdapter( @NonNull Activity context, ArrayList<WeeklyEvaluation> evaluations, int resource )
    {
        super( context, resource );
        this.context = context;
        this.evaluations = evaluations;
    }

    @Override
    public int getCount()
    {
        return evaluations.size();
    }

    public View getView( int position, View view, ViewGroup parent)
    {
        LayoutInflater inflater = context.getLayoutInflater();

        // Check if an existing view is being reused, otherwise inflate the view
        if( null == view )
        {
            view = inflater.inflate( R.layout.weekly_evaluation_list_item, null, true );
        }


        DateFormatSymbols dfs = new DateFormatSymbols();

        WeeklyEvaluation evaluation = evaluations.get( position );
        Calendar startCal = Calendar.getInstance();
        startCal.setTime( evaluation.start() );

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

        view.findViewById( R.id.eating_image ).setVisibility( evaluation.targetDiet ? View.VISIBLE : View.GONE );
        TextView dietScore = view.findViewById( R.id.diet_score );
        dietScore.setVisibility( evaluation.targetDiet ? View.VISIBLE : View.GONE );
        dietScore.setText( evaluation.dietIsScored() ? String.valueOf( evaluation.getDiet() ) : "" );

        view.findViewById( R.id.activity_image ).setVisibility( evaluation.targetPhysicalActivity ? View.VISIBLE : View.GONE );
        TextView activityScore = view.findViewById( R.id.physical_activity_score );
        activityScore.setVisibility( evaluation.targetPhysicalActivity ? View.VISIBLE : View.GONE );
        activityScore.setText( evaluation.physicalActivityIsScored() ? String.valueOf( evaluation.getPhysicalActivity() ) : "" );

        view.findViewById( R.id.alcohol_image ).setVisibility( evaluation.targetAlcohol ? View.VISIBLE : View.GONE );
        TextView alcoholScore = view.findViewById( R.id.alcohol_score );
        alcoholScore.setVisibility( evaluation.targetAlcohol ? View.VISIBLE : View.GONE );
        alcoholScore.setText( evaluation.alcoholIsScored() ? String.valueOf( evaluation.getAlcohol() ) : "" );

        view.findViewById( R.id.smoking_image ).setVisibility( evaluation.targetSmoking ? View.VISIBLE : View.GONE );
        TextView smokingScore = view.findViewById( R.id.smoking_score );
        smokingScore.setVisibility( evaluation.targetSmoking ? View.VISIBLE : View.GONE );
        smokingScore.setText( evaluation.smokingIsScored() ? String.valueOf( evaluation.getSmoking() ) : "" );

        view.findViewById( R.id.scored ).setVisibility( evaluation.isScored() ? View.VISIBLE : View.GONE );
        view.findViewById( R.id.pending ).setVisibility( evaluation.isScored() ? View.GONE: View.VISIBLE );
        return view;
    }
}
