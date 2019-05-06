package ahat.mmsnap;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONException;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

import ahat.mmsnap.models.ConversionException;
import ahat.mmsnap.models.IfThenPlan;
import ahat.mmsnap.models.Reminder;

import static ahat.mmsnap.ApplicationStatus.Behavior.ACTIVITY;
import static ahat.mmsnap.ApplicationStatus.Behavior.ALCOHOL;
import static ahat.mmsnap.ApplicationStatus.Behavior.DIET;
import static ahat.mmsnap.ApplicationStatus.Behavior.SMOKING;
import static ahat.mmsnap.models.IfThenPlan.WeekDay.FRIDAY;
import static ahat.mmsnap.models.IfThenPlan.WeekDay.MONDAY;
import static ahat.mmsnap.models.IfThenPlan.WeekDay.SATURDAY;
import static ahat.mmsnap.models.IfThenPlan.WeekDay.SUNDAY;
import static ahat.mmsnap.models.IfThenPlan.WeekDay.THURSDAY;
import static ahat.mmsnap.models.IfThenPlan.WeekDay.TUESDAY;
import static ahat.mmsnap.models.IfThenPlan.WeekDay.WEDNESDAY;


public abstract class IfThenDetailActivity extends MassDisableActivity //AppCompatActivity
    implements View.OnClickListener //, DatePickerDialog.OnDateSetListener
{
    private static final int SELECTED_COLOR = Color.rgb( 255, 255, 0 );
    private static final int NOT_SELECTED_COLOR = Color.rgb( 255, 255, 255 );

    protected HashMap<ApplicationStatus.Behavior, Boolean> BehaviorIsSelected;
    protected boolean planIsExpired;
    private int weekOfYear;
    private int year;

    protected abstract int getActivityResLayout();
    protected abstract int getContentRootLayoutResId();
    protected abstract IfThenPlan getIfThenItem();
    protected abstract Class<?> getListActivityClass();
    protected abstract String getSaveErrorMessage();
    protected abstract void saveItem( int year, int weekOfYear, ArrayList<IfThenPlan.WeekDay> days, ArrayList<Reminder> reminders ) throws IOException, JSONException, ConversionException;

    protected IfThenPlan item;
    protected ArrayList<Reminder> reminders = new ArrayList<>();
    ArrayList<IfThenPlan.WeekDay> days = new ArrayList<>();
    private TextView ifStatementTextView;
    private TextView thenStatementTextView;
    private Switch   activeSwitch;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( getActivityResLayout() );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setIcon( getResources().getDrawable( R.drawable.if_then_section_logo, null ) );
        getSupportActionBar().setTitle( R.string.title_activity_if_then );

        toolbar.setNavigationOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                onBackPressed();
            }
        });

        item = getIfThenItem();

        findViewById( R.id.save ).setOnClickListener( this );

        ifStatementTextView = findViewById( R.id.item_if_statement );
        thenStatementTextView = findViewById( R.id.item_then_statement );
        activeSwitch = findViewById( R.id.active_switch );

        BehaviorIsSelected = new HashMap<>();
        BehaviorIsSelected.put( DIET, false );
        BehaviorIsSelected.put( ACTIVITY, false );
        BehaviorIsSelected.put( ALCOHOL, false );
        BehaviorIsSelected.put( SMOKING, false );

        ifStatementTextView.setText( item.ifStatement );
        thenStatementTextView.setText( item.thenStatement );
        activeSwitch.setChecked( item.active );

        for( ApplicationStatus.Behavior behavior : ApplicationStatus.Behavior.values() )
        {
            try
            {
                ApplicationStatus as = ApplicationStatus.getInstance( this );
                if( !as.problematicBehaviors.contains( behavior ) )
                {
                    hideBehaviorUI( behavior );
                }
                else
                {
                    setBehaviorIsSelected( behavior, item.isTarget( behavior ) );
                    updateBehaviorUI( behavior );
                }
            }
            catch( Exception e )
            {
                Snackbar.make( findViewById( android.R.id.content ), "Could not load the application status", Snackbar.LENGTH_LONG ).show();
            }
        }

        // if the plan is passed it's week it is disabled
        Calendar now = Calendar.getInstance();
        Calendar pc = Calendar.getInstance();
        pc.set( Calendar.YEAR, item.year );
        pc.set( Calendar.WEEK_OF_YEAR, item.weekOfYear );
        pc.set( Calendar.DAY_OF_WEEK, pc.getFirstDayOfWeek() + 6 );

//        planIsExpired = now.get( Calendar.WEEK_OF_YEAR ) != item.weekOfYear || now.get( Calendar.YEAR ) != item.year;
        planIsExpired = now.after( pc );

        if( planIsExpired )
        {
            //stop the soft keyboard from displaying, the user will only evaluate the days
            this.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN );

            disableAllControls();

            // user cannot edit expired plans
            findViewById( R.id.save ).setVisibility( View.GONE );

//            // user cannot add reminders
//            findViewById( R.id.reminder_btn ).setVisibility( View.GONE );
        }

        // health behaviors are editable only when not expired
        if( !planIsExpired )
        {
            //behavior layout events
            findViewById( R.id.eating_image ).setOnClickListener( this );
            findViewById( R.id.activity_image ).setOnClickListener( this );
            findViewById( R.id.alcohol_image ).setOnClickListener( this );
            findViewById( R.id.smoking_image ).setOnClickListener( this );
        }

        ( (CheckBox) findViewById( R.id.day_mon_chk ) ).setChecked( item.hasDay( MONDAY ) );
        ( (CheckBox) findViewById( R.id.day_tue_chk ) ).setChecked( item.hasDay( TUESDAY ) );
        ( (CheckBox) findViewById( R.id.day_wed_chk ) ).setChecked( item.hasDay( WEDNESDAY ) );
        ( (CheckBox) findViewById( R.id.day_thu_chk ) ).setChecked( item.hasDay( THURSDAY ) );
        ( (CheckBox) findViewById( R.id.day_fri_chk ) ).setChecked( item.hasDay( FRIDAY ) );
        ( (CheckBox) findViewById( R.id.day_sat_chk ) ).setChecked( item.hasDay( SATURDAY ) );
        ( (CheckBox) findViewById( R.id.day_sun_chk ) ).setChecked( item.hasDay( SUNDAY ) );

        if( planIsExpired )
        {
            //When the plan is expired the user cannot edit the days
            findViewById( R.id.day_mon_chk ).setEnabled( false );
            findViewById( R.id.day_tue_chk ).setEnabled( false );
            findViewById( R.id.day_wed_chk ).setEnabled( false );
            findViewById( R.id.day_thu_chk ).setEnabled( false );
            findViewById( R.id.day_fri_chk ).setEnabled( false );
            findViewById( R.id.day_sat_chk ).setEnabled( false );
            findViewById( R.id.day_sun_chk ).setEnabled( false );
        }
        else
        {
            // while the plan is not expired, the user can edit only the days that have not passed yet
            findViewById( R.id.day_mon_chk ).setEnabled( !item.dayHasPassed( MONDAY ) );
            findViewById( R.id.day_tue_chk ).setEnabled( !item.dayHasPassed( TUESDAY ) );
            findViewById( R.id.day_wed_chk ).setEnabled( !item.dayHasPassed( WEDNESDAY ) );
            findViewById( R.id.day_thu_chk ).setEnabled( !item.dayHasPassed( THURSDAY ) );
            findViewById( R.id.day_fri_chk ).setEnabled( !item.dayHasPassed( FRIDAY ) );
            findViewById( R.id.day_sat_chk ).setEnabled( !item.dayHasPassed( SATURDAY ) );
            findViewById( R.id.day_sun_chk ).setEnabled( !item.dayHasPassed( SUNDAY ) );

            findViewById( R.id.day_mon_chk ).setOnClickListener( this );
            findViewById( R.id.day_tue_chk ).setOnClickListener( this );
            findViewById( R.id.day_wed_chk ).setOnClickListener( this );
            findViewById( R.id.day_thu_chk ).setOnClickListener( this );
            findViewById( R.id.day_fri_chk ).setOnClickListener( this );
            findViewById( R.id.day_sat_chk ).setOnClickListener( this );
            findViewById( R.id.day_sun_chk ).setOnClickListener( this );

            findViewById( R.id.reminder_btn ).setOnClickListener( this );
            findViewById( R.id.select_week_btn ).setOnClickListener( this );
        }





//            if( !item.isEvaluated() )
//            {
//                Calendar c = Calendar.getInstance();
//                c.set( Calendar.YEAR, item.year );
//                c.set( Calendar.WEEK_OF_YEAR, item.weekOfYear );
//
//                c.set( Calendar.DAY_OF_WEEK, Calendar.MONDAY );
//                if( now.after( c ) && item.hasDay( MONDAY ) ){ findViewById( R.id.mon_layout ).setOnClickListener( this ); }
//
//                c.set( Calendar.DAY_OF_WEEK, Calendar.TUESDAY );
//                if( now.after( c ) && item.hasDay( TUESDAY ) ){ findViewById( R.id.tue_layout ).setOnClickListener( this ); }
//
//                c.set( Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY );
//                if( now.after( c ) && item.hasDay( WEDNESDAY ) ){ findViewById( R.id.wed_layout ).setOnClickListener( this ); }
//
//                c.set( Calendar.DAY_OF_WEEK, Calendar.THURSDAY );
//                if( now.after( c ) && item.hasDay( THURSDAY ) ){ findViewById( R.id.thu_layout ).setOnClickListener( this ); }
//
//                c.set( Calendar.DAY_OF_WEEK, Calendar.FRIDAY );
//                if( now.after( c ) && item.hasDay( FRIDAY ) ){ findViewById( R.id.fri_layout ).setOnClickListener( this ); }
//
//                c.set( Calendar.DAY_OF_WEEK, Calendar.SATURDAY );
//                if( now.after( c ) && item.hasDay( SATURDAY ) ){ findViewById( R.id.sat_layout ).setOnClickListener( this ); }
//
//                c.set( Calendar.DAY_OF_WEEK, Calendar.SUNDAY );
//                if( now.after( c ) && item.hasDay( SUNDAY ) ){ findViewById( R.id.sun_layout ).setOnClickListener( this ); }
//            }


        // success (/check) and fail images are displayed only if a plan is expired or in evaluation mode
//        if( planIsExpired )
//        {
//            findViewById( R.id.day_mon_check_img ).setVisibility( ( planIsExpired || evaluationMode || item.dayHasPassed( MONDAY ) ) && item.isEvaluated( MONDAY ) && item.isSuccessful( MONDAY ) ? View.VISIBLE : View.INVISIBLE );
//            findViewById( R.id.day_tue_check_img ).setVisibility( ( planIsExpired || evaluationMode || item.dayHasPassed( TUESDAY ) ) && item.isEvaluated( TUESDAY ) && item.isSuccessful( TUESDAY ) ? View.VISIBLE : View.INVISIBLE );
//            findViewById( R.id.day_wed_check_img ).setVisibility( ( planIsExpired || evaluationMode || item.dayHasPassed( WEDNESDAY ) ) && item.isEvaluated( WEDNESDAY ) && item.isSuccessful( WEDNESDAY ) ? View.VISIBLE : View.INVISIBLE );
//            findViewById( R.id.day_thu_check_img ).setVisibility( ( planIsExpired || evaluationMode || item.dayHasPassed( THURSDAY ) ) && item.isEvaluated( THURSDAY ) && item.isSuccessful( THURSDAY ) ? View.VISIBLE : View.INVISIBLE );
//            findViewById( R.id.day_fri_check_img ).setVisibility( ( planIsExpired || evaluationMode || item.dayHasPassed( FRIDAY ) ) && item.isEvaluated( FRIDAY ) && item.isSuccessful( FRIDAY ) ? View.VISIBLE : View.INVISIBLE );
//            findViewById( R.id.day_sat_check_img ).setVisibility( ( planIsExpired || evaluationMode || item.dayHasPassed( SATURDAY ) ) && item.isEvaluated( SATURDAY ) && item.isSuccessful( SATURDAY ) ? View.VISIBLE : View.INVISIBLE );
//            findViewById( R.id.day_sun_check_img ).setVisibility( ( planIsExpired || evaluationMode || item.dayHasPassed( SUNDAY ) ) && item.isEvaluated( SUNDAY ) && item.isSuccessful( SUNDAY ) ? View.VISIBLE : View.INVISIBLE );
//            findViewById( R.id.day_mon_fail_img ).setVisibility( ( planIsExpired || evaluationMode || item.dayHasPassed( MONDAY ) ) && item.isEvaluated( MONDAY ) && !item.isSuccessful( MONDAY ) ? View.VISIBLE : View.INVISIBLE );
//            findViewById( R.id.day_tue_fail_img ).setVisibility( ( planIsExpired || evaluationMode || item.dayHasPassed( TUESDAY ) ) && item.isEvaluated( TUESDAY ) && !item.isSuccessful( TUESDAY ) ? View.VISIBLE : View.INVISIBLE );
//            findViewById( R.id.day_wed_fail_img ).setVisibility( ( planIsExpired || evaluationMode || item.dayHasPassed( WEDNESDAY ) ) && item.isEvaluated( WEDNESDAY ) && !item.isSuccessful( WEDNESDAY ) ? View.VISIBLE : View.INVISIBLE );
//            findViewById( R.id.day_thu_fail_img ).setVisibility( ( planIsExpired || evaluationMode || item.dayHasPassed( THURSDAY ) ) && item.isEvaluated( THURSDAY ) && !item.isSuccessful( THURSDAY ) ? View.VISIBLE : View.INVISIBLE );
//            findViewById( R.id.day_fri_fail_img ).setVisibility( ( planIsExpired || evaluationMode || item.dayHasPassed( FRIDAY ) ) && item.isEvaluated( FRIDAY ) && !item.isSuccessful( FRIDAY ) ? View.VISIBLE : View.INVISIBLE );
//            findViewById( R.id.day_sat_fail_img ).setVisibility( ( planIsExpired || evaluationMode || item.dayHasPassed( SATURDAY ) ) && item.isEvaluated( SATURDAY ) && !item.isSuccessful( SATURDAY ) ? View.VISIBLE : View.INVISIBLE );
//            findViewById( R.id.day_sun_fail_img ).setVisibility( ( planIsExpired || evaluationMode || item.dayHasPassed( SUNDAY ) ) && item.isEvaluated( SUNDAY ) && !item.isSuccessful( SUNDAY ) ? View.VISIBLE : View.INVISIBLE );
//        }

        year = item.year;
        weekOfYear = item.weekOfYear;
        setWeekUI( year, weekOfYear );

        // create a copy of the reminders so that additions and removals will not affect the original item before it is actually saved
        for( Reminder reminder : item.reminders )
        {
            reminders.add( new Reminder( reminder ) );
        }
        Collections.sort( reminders, Reminder.comparator );
        showReminders( reminders );
    }

    private void setWeekUI( int year, int weekOfYear )
    {
        DateFormatSymbols dfs = new DateFormatSymbols();

        Calendar startCal = Calendar.getInstance();
        startCal.set( Calendar.YEAR, year );
        startCal.set( Calendar.WEEK_OF_YEAR, weekOfYear );

        Calendar c = Calendar.getInstance();
        c.set( Calendar.YEAR, startCal.get( Calendar.YEAR ) );
        c.set( Calendar.WEEK_OF_YEAR, startCal.get( Calendar.WEEK_OF_YEAR ) );
        c.set( Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() );   // go to the first day of this week

        TextView start = findViewById( R.id.start_date );
        start.setText( dfs.getShortWeekdays()[ c.get( Calendar.DAY_OF_WEEK ) ]+ " " + c.get( Calendar.DAY_OF_MONTH ) + " " +
                       dfs.getShortMonths()[ c.get( Calendar.MONTH ) ] + " " + c.get( Calendar.YEAR ) );

        c.add( Calendar.DATE, 6);
        TextView end = findViewById( R.id.end_date );
        end.setText( dfs.getShortWeekdays()[ c.get( Calendar.DAY_OF_WEEK ) ]+ " " + c.get( Calendar.DAY_OF_MONTH ) + " " +
                     dfs.getShortMonths()[ c.get( Calendar.MONTH ) ] + " " + c.get( Calendar.YEAR ) );
    }

    @Override
    public abstract void onBackPressed();

    @Override
    public void onClick( View view )
    {

        switch (view.getId())
        {
            case R.id.eating_image:
                toggleBehavior( DIET );
                break;
            case R.id.activity_image:
                toggleBehavior( ACTIVITY );
                break;
            case R.id.alcohol_image:
                toggleBehavior( ALCOHOL );
                break;
            case R.id.smoking_image:
                toggleBehavior( SMOKING );
                break;
            case R.id.save:
                save();
                break;
            case R.id.reminder_btn:
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get( Calendar.HOUR_OF_DAY );
                int minute = mcurrentTime.get( Calendar.MINUTE );
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(IfThenDetailActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet( TimePicker timePicker, int selectedHour, int selectedMinute)
                    {
                        addReminder( selectedHour, selectedMinute );
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle( "Select Reminder Time" );
                mTimePicker.show();
                break;
            case R.id.select_week_btn:
                Calendar calendar = Calendar.getInstance();
                int year    = calendar.get( Calendar.YEAR );
                int month   = calendar.get( Calendar.MONTH );
                int day     = calendar.get( Calendar.DAY_OF_MONTH );
                DatePickerDialog dialog = new DatePickerDialog(
                    IfThenDetailActivity.this,
                    new DatePickerDialog.OnDateSetListener()
                    {
                        public void onDateSet( DatePicker view, int year, int monthOfYear, int dayOfMonth )
                        {
                            Calendar newDate = Calendar.getInstance();
                            newDate.set( year, monthOfYear, dayOfMonth );
                            year = newDate.get( Calendar.YEAR );
                            weekOfYear = newDate.get( Calendar.WEEK_OF_YEAR );
                            setWeekUI( year, weekOfYear );
                        }
                    },
                    year, month, day
                );
                // from https://stackoverflow.com/a/4555487
                final int appFlags = getApplicationInfo().flags;
                final boolean isDebug = ( appFlags & ApplicationInfo.FLAG_DEBUGGABLE ) != 0;
                // TODO: ensure that I send the release version to users
                if( !isDebug )
                {
                    dialog.getDatePicker().setMinDate( calendar.getTimeInMillis() );
//                    TODO: should I add a maxdate to stop users from planning in the future?
//                    calendar.add( Calendar.DATE, 7 );
//                    dialog.getDatePicker().setMaxDate( calendar.getTimeInMillis() );
                }
                dialog.show();
                break;
            default:
                break;
        }
    }

    private void addReminder( int hour, int minute )
    {
        boolean found = false;
        for( Reminder reminder : reminders )
        {
            if( reminder.hour == hour && reminder.minute == minute )
            {
                found = true;
                break;
            }
        }

        if( !found )
        {
            reminders.add( new Reminder( hour, minute ) );
            Collections.sort( reminders, Reminder.comparator );
            showReminders( reminders );
        }
    }

    private void removeReminder( Reminder reminder )
    {
        for( Reminder r : reminders )
        {
            if( r.equals( reminder ) )
            {
                reminders.remove( r );
                showReminders( reminders );
                break;
            }
        }
    }

    private void showReminders( ArrayList<Reminder> reminders )
    {
        ( (FlexboxLayout) findViewById( R.id.reminder_layout ) ).removeAllViews();
        for( Reminder reminder : reminders )
        {
            addUIReminder( reminder.hour, reminder.minute );
        }
    }

    private void addUIReminder( int hour, int minute )
    {
        FlexboxLayout reminderLayout = findViewById( R.id.reminder_layout );
        LinearLayout timeLayout = new LinearLayout( this );
        timeLayout.setLayoutParams( new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT ) );
        timeLayout.setOrientation( LinearLayout.HORIZONTAL );
        timeLayout.setBackground( getDrawable( R.drawable.rounded_border_lightgrey_bkg ) );
        timeLayout.setGravity( Gravity.CENTER_VERTICAL );
        setMargins( timeLayout, 4, 4, 4, 4 );
        setPadding( timeLayout, 8, 4, 4, 4 );
//        timeLayout.setBackgroundColor( Color.parseColor( "#888888" )  );
//        timeLayout.getBackground().setColorFilter( Color.parseColor("#888888" ), PorterDuff.Mode.SRC_ATOP);

        TextView time = new TextView( this );
        time.setLayoutParams( new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT ) );
        time.setText( ( hour < 10 ? "0": "" ) + String.valueOf( hour ) + ":" + ( minute < 10 ? "0" : "" ) + String.valueOf( minute ) );
        time.setTextColor( getResources().getColor( android.R.color.secondary_text_light ) );
        timeLayout.addView( time );

        if( remindersAreEditable() )
        {
            ImageButton del = new ImageButton( this );
            setSize( del, 16, 16 );
            setMargins( del, 6, 2, 0, 2 );  //negative margins and excessive padding are used to increase the clickable area
            setPadding( del, 1, 1, 1, 1 );
//            setPadding( del, 24, 14, 24, 14 );
            Drawable d = getResources().getDrawable( R.drawable.rounded_border_red_bkg, null );
            del.setBackground( d );
            del.setImageResource( android.R.drawable.ic_menu_close_clear_cancel );
            del.setColorFilter( getResources().getColor( android.R.color.white ) );
            del.setScaleType( ImageView.ScaleType.CENTER_INSIDE );
            del.setTag( new Reminder( hour, minute ) );
            del.setOnClickListener( new View.OnClickListener()
            {
                @Override
                public void onClick( View view )
                {
                    Reminder reminder = (Reminder) view.getTag();
                    removeReminder( reminder );
                }
            } );
            timeLayout.addView( del );
        }

        reminderLayout.addView( timeLayout );
    }

    protected boolean remindersAreEditable()
    {
        return !planIsExpired;
    }

    //set width height in dp
    private void setSize( View view, int width, int height )
    {
        final float scale = getBaseContext().getResources().getDisplayMetrics().density;
        // convert the DP into pixel
        int w =  (int)(width * scale + 0.5f);
        int h =  (int)(height * scale + 0.5f);

        view.setLayoutParams( new LinearLayout.LayoutParams( w, h ) );
        view.requestLayout();
    }

    //set margins in dp
    private void setMargins( View view, int left, int top, int right, int bottom )
    {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();

            final float scale = getBaseContext().getResources().getDisplayMetrics().density;
            // convert the DP into pixel
            int l =  (int)(left * scale + 0.5f);
            int r =  (int)(right * scale + 0.5f);
            int t =  (int)(top * scale + 0.5f);
            int b =  (int)(bottom * scale + 0.5f);

            p.setMargins(l, t, r, b);
            view.requestLayout();
        }
    }

    //set padding in dp
    private void setPadding( View view, int left, int top, int right, int bottom )
    {
        final float scale = getBaseContext().getResources().getDisplayMetrics().density;
        // convert the DP into pixel
        int l =  (int)(left * scale + 0.5f);
        int r =  (int)(right * scale + 0.5f);
        int t =  (int)(top * scale + 0.5f);
        int b =  (int)(bottom * scale + 0.5f);

        view.setPadding(l, t, r, b);
        view.requestLayout();
    }

    protected String fillItemFromUI() throws JSONException
    {
        String error = "";

        String ifStatement = ifStatementTextView.getText().toString().trim();
        String thenStatement = thenStatementTextView.getText().toString().trim();
        if( 0 == ifStatement.length() )
        {
            error = "enter an IF statement";
        }
        if( 0 == thenStatement.length() )
        {
            error += ( error.length() > 0 ? " and " : "enter " ) + "a THEN statement";
        }

        if( !getBehaviorIsSelected( DIET ) &&
            !getBehaviorIsSelected( ACTIVITY ) &&
            !getBehaviorIsSelected( ALCOHOL ) &&
            !getBehaviorIsSelected( SMOKING )
        )
        {
            error += ( error.length() > 0 ? " and " : "" ) + "select at least one HEALTH behavior";
        }

        if( 0 < error.length() )
        {
            return error;
        }

        item.ifStatement = ifStatement;
        item.thenStatement = thenStatement;
        item.active = activeSwitch.isChecked();

        item.targetBehaviors.clear();
        if( getBehaviorIsSelected( DIET ) ) { item.targetBehaviors.add( DIET ); }
        if( getBehaviorIsSelected( ACTIVITY ) ) { item.targetBehaviors.add( ACTIVITY ); }
        if( getBehaviorIsSelected( ALCOHOL ) ) { item.targetBehaviors.add( ALCOHOL ); }
        if( getBehaviorIsSelected( SMOKING ) ) { item.targetBehaviors.add( SMOKING ); }

        days.clear();

        if( ( (CheckBox) findViewById( R.id.day_mon_chk ) ).isChecked() ) { days.add( MONDAY ); }
        if( ( (CheckBox) findViewById( R.id.day_tue_chk ) ).isChecked() ) { days.add( TUESDAY ); }
        if( ( (CheckBox) findViewById( R.id.day_wed_chk ) ).isChecked() ) { days.add( WEDNESDAY ); }
        if( ( (CheckBox) findViewById( R.id.day_thu_chk ) ).isChecked() ) { days.add( THURSDAY ); }
        if( ( (CheckBox) findViewById( R.id.day_fri_chk ) ).isChecked() ) { days.add( FRIDAY ); }
        if( ( (CheckBox) findViewById( R.id.day_sat_chk ) ).isChecked() ) { days.add( SATURDAY ); }
        if( ( (CheckBox) findViewById( R.id.day_sun_chk ) ).isChecked() ) { days.add( SUNDAY ); }

        if( 0 == days.size() )
        {
            error += ( error.length() > 0 ? " and " : "" ) + "select at least one day to apply your plan";
        }
        return error;
    }

    private void save()
    {
        try
        {
            String error = fillItemFromUI();
            if( 0 == error.length() )
            {
//                JSONArrayIOHandler.saveItem( getBaseContext(), item, getFilesDir().getPath() + "/" + FILENAME );
                saveItem( year, weekOfYear, days, reminders );
                startActivity( new Intent( getBaseContext(), getListActivityClass() ) );
            }
            else
            {
                error = "Please " + error;  // Do not put a fullstop at the end in case a class overriding this method wishes to add it's own errors.
                View view = findViewById( getContentRootLayoutResId() );
                Snackbar.make( view, error, Snackbar.LENGTH_LONG ).show();
            }
        }
        catch( Exception e )
        {
            View view = findViewById( getContentRootLayoutResId() );
            Snackbar.make( view, getSaveErrorMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    protected boolean getBehaviorIsSelected( ApplicationStatus.Behavior b )
    {
        return BehaviorIsSelected.get( b );
    }
    protected boolean setBehaviorIsSelected( ApplicationStatus.Behavior b, Boolean v )
    {
        return BehaviorIsSelected.put( b, v );
    }
    protected void toggleBehavior( ApplicationStatus.Behavior behavior )
    {
        setBehaviorIsSelected( behavior, !getBehaviorIsSelected( behavior ) );
        updateBehaviorUI( behavior );
    }

    protected void updateBehaviorUI( ApplicationStatus.Behavior behavior )
    {
        ImageView imageView;
        int color = getBehaviorIsSelected( behavior ) ? SELECTED_COLOR : NOT_SELECTED_COLOR;

        switch( behavior )
        {
            case DIET:
                imageView = findViewById( R.id.eating_image );
                imageView.setColorFilter( color );
                break;
            case ACTIVITY:
                imageView = findViewById( R.id.activity_image );
                imageView.setColorFilter( color );
                break;
            case ALCOHOL:
                imageView = findViewById( R.id.alcohol_image);
                imageView.setColorFilter( color );
                break;
            case SMOKING:
                imageView = findViewById( R.id.smoking_image );
                imageView.setColorFilter( color );
                break;
        }
    }

    protected void hideBehaviorUI( ApplicationStatus.Behavior behavior )
    {
        switch( behavior )
        {
            case DIET:
                findViewById( R.id.eating_image ).setVisibility( View.GONE );
                break;
            case ACTIVITY:
                findViewById( R.id.activity_image ).setVisibility( View.GONE );
                break;
            case ALCOHOL:
                findViewById( R.id.alcohol_image).setVisibility( View.GONE );
                break;
            case SMOKING:
                findViewById( R.id.smoking_image ).setVisibility( View.GONE );
                break;
        }
    }


//    public void onDateSet( DatePicker view, int year, int monthOfYear, int dayOfMonth)
//    {
//        actionDate = year + "-" + monthOfYear + "-" + dayOfMonth;
//        setDateTextView();
//    }
//
//    protected void setDateTextView()
//    {
//        TextView dates = findViewById( R.id.plan_dates );
//        if( 0 == actionDate.trim().length() )
//        {
//            dates.setText( R.string.select_plan_dates );
//            return;
//        }
//
//        Calendar cal = getCalendarFromYYYYMMDD( actionDate );
//        DateFormatSymbols dfs = new DateFormatSymbols();
//        dates.setText( dfs.getShortWeekdays()[ cal.get( Calendar.DAY_OF_WEEK ) ]+ " " + cal.get( Calendar.DAY_OF_MONTH ) + " " +
//                       dfs.getMonths()[ cal.get( Calendar.MONTH ) ] + " " + cal.get( Calendar.YEAR ));
//    }

    /*
     * date must be in the form yyyy-mm-dd where mm is the 0 based month and dd the 1 based day-of-month
     */
    public static Calendar getCalendarFromYYYYMMDD( String date )
    {
        //ahat: DO NOT USE SimpleDateFormat because the strings it understands have 1 based months, while calendar widget and Calendar have 0 based months
        final Calendar cal = Calendar.getInstance();
        String[] dateParts = date.split( "-" );
        cal.set( Calendar.YEAR, Integer.parseInt( dateParts[0] ) );
        cal.set( Calendar.MONTH, Integer.parseInt( dateParts[1] ) );
        cal.set( Calendar.DAY_OF_MONTH, Integer.parseInt( dateParts[2] ) );
        cal.set( Calendar.HOUR_OF_DAY, 0 );
        cal.set( Calendar.MINUTE, 0 );
        cal.set( Calendar.SECOND, 0 );
        cal.set( Calendar.MILLISECOND, 0 );

        return cal;
    }
}


