package ahat.mmsnap;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import ahat.mmsnap.json.ActionPlansStorage;
import ahat.mmsnap.json.CopingPlansStorage;
import ahat.mmsnap.json.JSONArrayConverterActionPlan;
import ahat.mmsnap.json.JSONArrayConverterCopingPlan;
import ahat.mmsnap.models.ReminderAlarms;
import ahat.mmsnap.notifications.ReminderAlarmReceiver;

public class TestingActivity extends AppCompatActivity implements View.OnClickListener
{

    private Date startDate;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_testing );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        findViewById( R.id.start_date_calendar_btn ).setOnClickListener( this );
        findViewById( R.id.start_date_set_btn ).setOnClickListener( this );
        findViewById( R.id.delete_weekly_evaluations_btn ).setOnClickListener( this );
        findViewById( R.id.delete_daily_evaluations_btn ).setOnClickListener( this );
        findViewById( R.id.delete_all_plans_btn ).setOnClickListener( this );
        findViewById( R.id.delete_all_alarms_btn ).setOnClickListener( this );
        findViewById( R.id.reset_initial_assessments_btn ).setOnClickListener( this );
        findViewById( R.id.reset_final_assessments_btn ).setOnClickListener( this );

        startDate = new Date();
        try
        {
            ApplicationStatus as = ApplicationStatus.getInstance( TestingActivity.this );
            startDate = as.getStartDate();
        }
        catch( Exception e )
        {
            e.printStackTrace();
            Toast.makeText( TestingActivity.this, "An error occurred while getting the Application Start Date.", Toast.LENGTH_SHORT ).show();
        }
        Calendar c = Calendar.getInstance();
        c.setTime( startDate );
        setApplicationStartDateUI( c );
    }

    @Override
    public void onClick( View view )
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder( this );//.create();
        alertDialog.setNegativeButton( android.R.string.no, null );

        switch( view.getId() )
        {
            case R.id.start_date_calendar_btn:
                final Calendar calendar = Calendar.getInstance();
                int year    = calendar.get( Calendar.YEAR );
                int month   = calendar.get( Calendar.MONTH );
                int day     = calendar.get( Calendar.DAY_OF_MONTH );
                DatePickerDialog dialog = new DatePickerDialog(
                    TestingActivity.this,
                    new DatePickerDialog.OnDateSetListener()
                    {
                        public void onDateSet( DatePicker view, int year, int monthOfYear, int dayOfMonth )
                        {
                            Calendar newDate = Calendar.getInstance();
                            newDate.set( year, monthOfYear, dayOfMonth );

                            startDate = newDate.getTime();

                            setApplicationStartDateUI( newDate );
                        }
                    },
                    year, month, day
                );
                dialog.show();
                break;
            case R.id.start_date_set_btn:
                alertDialog.setTitle("Modify Application Start Date")
                           .setMessage("Are you sure you wish to modify the application start date?" )
                           .setCancelable(true)
                           .setPositiveButton( android.R.string.yes,
                                               new DialogInterface.OnClickListener() {
                                                   public void onClick( DialogInterface dialog, int which) {
                                                       dialog.dismiss();
                                                       try
                                                       {
                                                           ApplicationStatus as = ApplicationStatus.getInstance( TestingActivity.this );
                                                           as.setStartDate( startDate );
                                                           as.save();
                                                       }
                                                       catch( Exception e )
                                                       {
                                                           e.printStackTrace();
                                                           Toast.makeText( TestingActivity.this, "An error occurred while modifying the application start date.", Toast.LENGTH_SHORT ).show();
                                                       }
                                                   }
                                               })
                           .show();
                break;
            case R.id.delete_weekly_evaluations_btn:
                alertDialog.setTitle("Delete Weekly Evaluations")
                           .setMessage("Are you sure you wish to delete all weekly evaluations?" )
                           .setCancelable(true)
                           .setPositiveButton( android.R.string.yes,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick( DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            try
                                            {
                                                ApplicationStatus as = ApplicationStatus.getInstance( TestingActivity.this );
                                                as.weeklyEvaluations = new ArrayList<>();
                                                as.save();
                                            }
                                            catch( Exception e )
                                            {
                                                e.printStackTrace();
                                                Toast.makeText( TestingActivity.this, "An error occurred while deleting the weekly evaluations.", Toast.LENGTH_SHORT ).show();
                                            }
                                        }
                                    })
                .show();
                break;
            case R.id.delete_daily_evaluations_btn:
                alertDialog.setTitle("Delete Daily Evaluations")
                           .setMessage("Are you sure you wish to delete all daily evaluations?" )
                           .setCancelable(true)
                           .setPositiveButton( android.R.string.yes,
                                               new DialogInterface.OnClickListener() {
                                                   public void onClick( DialogInterface dialog, int which) {
                                                       dialog.dismiss();
                                                       try
                                                       {
                                                           ApplicationStatus as = ApplicationStatus.getInstance( TestingActivity.this );
                                                           as.dailyEvaluations = new ArrayList<>();
                                                           as.save();
                                                       }
                                                       catch( Exception e )
                                                       {
                                                           e.printStackTrace();
                                                           Toast.makeText( TestingActivity.this, "An error occurred while deleting the daily evaluations.", Toast.LENGTH_SHORT ).show();
                                                       }
                                                   }
                                               })
                           .show();
                break;
            case R.id.delete_all_plans_btn:
                alertDialog.setTitle("Delete Action and Coping Plans")
                           .setMessage("Are you sure you wish to delete all daily evaluations?" )
                           .setCancelable(true)
                           .setPositiveButton( android.R.string.yes,
                                               new DialogInterface.OnClickListener() {
                                                   public void onClick( DialogInterface dialog, int which) {
                                                       dialog.dismiss();
                                                       try
                                                       {
                                                           ActionPlansStorage aps = new ActionPlansStorage( TestingActivity.this );
                                                           JSONArrayConverterActionPlan jacap = new JSONArrayConverterActionPlan();
                                                           aps.write( jacap );
                                                           CopingPlansStorage cps = new CopingPlansStorage ( TestingActivity.this );
                                                           JSONArrayConverterCopingPlan jaccp = new JSONArrayConverterCopingPlan();
                                                           cps.write( jaccp );
                                                       }
                                                       catch( Exception e )
                                                       {
                                                           e.printStackTrace();
                                                           Toast.makeText( TestingActivity.this, "An error occurred while deleting all action and coping plans.", Toast.LENGTH_SHORT ).show();
                                                       }
                                                   }
                                               })
                           .show();
                break;
            case R.id.delete_all_alarms_btn:
                alertDialog.setTitle("Delete Alarms")
                           .setMessage("Are you sure you wish to delete all alarms?" )
                           .setCancelable(true)
                           .setPositiveButton( android.R.string.yes,
                                               new DialogInterface.OnClickListener() {
                                                   public void onClick( DialogInterface dialog, int which) {
                                                       dialog.dismiss();
                                                       try
                                                       {
                                                           ReminderAlarms reminders = ReminderAlarms.getInstance( TestingActivity.this );
                                                           Iterator it = reminders.getAlarms().entrySet().iterator();
                                                           while( it.hasNext() )
                                                           {
                                                               Map.Entry pair = (Map.Entry) it.next();
                                                               ReminderAlarmReceiver.cancelAlarm( TestingActivity.this,(String) pair.getKey() );
                                                               it.remove();  // avoids a ConcurrentModificationException
                                                           }
                                                           reminders.write(  );
                                                       }
                                                       catch( Exception e )
                                                       {
                                                           e.printStackTrace();
                                                           Toast.makeText( TestingActivity.this, "An error occurred while deleting all alarms.", Toast.LENGTH_SHORT ).show();
                                                       }
                                                   }
                                               })
                           .show();
                break;
            case R.id.reset_initial_assessments_btn:
                alertDialog.setTitle("Reset Initial Assessments")
                           .setMessage("Are you sure you wish reset initial assessments? This action will return the application back to the \"No Initial Assessments submitted\" state which is right after the \"Initial Login confirmed\" state." )
                           .setCancelable(true)
                           .setPositiveButton( android.R.string.yes,
                                               new DialogInterface.OnClickListener() {
                                                   public void onClick( DialogInterface dialog, int which) {
                                                       dialog.dismiss();
                                                       try
                                                       {
                                                           ApplicationStatus as = ApplicationStatus.getInstance( TestingActivity.this );
                                                           // remove record of initial assessments and return to previous state
                                                           as.clearInitialAssessments();
                                                           as.setState( as.new NoInitialAssessments( as ) );
                                                           as.save();
                                                       }
                                                       catch( Exception e )
                                                       {
                                                           e.printStackTrace();
                                                           Toast.makeText( TestingActivity.this, "An error occurred while reseting initial assessments.", Toast.LENGTH_SHORT ).show();
                                                       }
                                                   }
                                               })
                           .show();
                break;
            case R.id.reset_final_assessments_btn:
                alertDialog.setTitle("Reset Final Assessments")
                           .setMessage("Are you sure you wish reset final assessments? This action will return the application back to the \"No Final Assessments submitted\" state\" which is right after the \"Program has expired\" state." )
                           .setCancelable(true)
                           .setPositiveButton( android.R.string.yes,
                                               new DialogInterface.OnClickListener() {
                                                   public void onClick( DialogInterface dialog, int which) {
                                                       dialog.dismiss();
                                                       try
                                                       {
                                                           ApplicationStatus as = ApplicationStatus.getInstance( TestingActivity.this );
                                                           as.clearFinalAssessments();
                                                           as.setState( as.new NoFinalAssessments( as ) );
                                                           as.save();
                                                       }
                                                       catch( Exception e )
                                                       {
                                                           e.printStackTrace();
                                                           Toast.makeText( TestingActivity.this, "An error occurred while reseting final assessments.", Toast.LENGTH_SHORT ).show();
                                                       }
                                                   }
                                               })
                           .show();
                break;
        }

    }

    private void setApplicationStartDateUI( Calendar newDate )
    {
        DateFormatSymbols dfs = new DateFormatSymbols();
        ( (TextView) findViewById( R.id.start_date ) )
            .setText( dfs.getShortWeekdays()[ newDate.get( Calendar.DAY_OF_WEEK ) ] + " " +
                      newDate.get( Calendar.DAY_OF_MONTH ) + " " +
                      dfs.getShortMonths()[ newDate.get( Calendar.MONTH ) ] + " " +
                      newDate.get( Calendar.YEAR )
            );
    }
}
