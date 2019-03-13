package ahat.mmsnap;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.HashMap;

import ahat.mmsnap.Models.ConversionException;
import ahat.mmsnap.Models.IfThenPlan;

import static ahat.mmsnap.ApplicationStatus.Behavior.ACTIVITY;
import static ahat.mmsnap.ApplicationStatus.Behavior.ALCOHOL;
import static ahat.mmsnap.ApplicationStatus.Behavior.DIET;
import static ahat.mmsnap.ApplicationStatus.Behavior.SMOKING;
import static ahat.mmsnap.Models.IfThenPlan.Day.FRIDAY;
import static ahat.mmsnap.Models.IfThenPlan.Day.MONDAY;
import static ahat.mmsnap.Models.IfThenPlan.Day.SATURDAY;
import static ahat.mmsnap.Models.IfThenPlan.Day.SUNDAY;
import static ahat.mmsnap.Models.IfThenPlan.Day.THURSDAY;
import static ahat.mmsnap.Models.IfThenPlan.Day.TUESDAY;
import static ahat.mmsnap.Models.IfThenPlan.Day.WEDNESDAY;

public abstract class IfThenDetailActivity extends AppCompatActivity
    implements View.OnClickListener //, DatePickerDialog.OnDateSetListener
{
    private static final int SELECTED_COLOR = Color.rgb( 255, 255, 0 );
    private static final int NOT_SELECTED_COLOR = Color.rgb( 255, 255, 255 );

    protected HashMap<ApplicationStatus.Behavior, Boolean> BehaviorIsSelected;
//    protected String                                       actionDate = "";
    protected abstract int getActivityResLayout();
    protected abstract int getContentRootLayoutResId();
//    protected abstract JSONObject getIfThenItem();
    protected abstract IfThenPlan getIfThenItem();
    protected abstract Class<?> getListActivityClass();
    protected abstract String getSaveErrorMessage();
    protected abstract void saveItem() throws IOException, JSONException, ConversionException;

//    protected JSONObject item;
    protected IfThenPlan item;
//    private String FILENAME;
//    protected String getFILENAME()
//    {
//        return FILENAME;
//    }
//    private int itemId;
//    protected int getItemId()
//    {
//        return itemId;
//    }


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

//        Bundle b = getIntent().getExtras();
//        FILENAME = b.getString( "FILENAME" );
//        itemId = -1;
//        if( b.containsKey( "itemId" ) )
//        {
//            itemId = b.getInt( "itemId" );
//        }

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

        setBehaviorIsSelected( DIET, item.isTarget( DIET ) );
        setBehaviorIsSelected( ACTIVITY, item.isTarget( ACTIVITY ) );
        setBehaviorIsSelected( ALCOHOL, item.isTarget( ALCOHOL ) );
        setBehaviorIsSelected( SMOKING, item.isTarget( SMOKING ) );

        updateBehaviorUI( DIET );
        updateBehaviorUI( ACTIVITY );
        updateBehaviorUI( ALCOHOL );
        updateBehaviorUI( SMOKING );

        // if the plan is passed it's week it can only be evaluated
        Calendar now = Calendar.getInstance();
        if( now.get( Calendar.WEEK_OF_YEAR ) != item.weekOfYear || now.get( Calendar.YEAR ) != item.year )
        {
            ifStatementTextView.setEnabled( false );
            thenStatementTextView.setEnabled( false );
            activeSwitch.setEnabled( false );

            findViewById( R.id.day_mon_chk ).setEnabled( item.days.contains( MONDAY ) );
            findViewById( R.id.day_tue_chk ).setEnabled( item.days.contains( TUESDAY ) );
            findViewById( R.id.day_wed_chk ).setEnabled( item.days.contains( WEDNESDAY ) );
            findViewById( R.id.day_thu_chk ).setEnabled( item.days.contains( THURSDAY ) );
            findViewById( R.id.day_fri_chk ).setEnabled( item.days.contains( FRIDAY) );
            findViewById( R.id.day_sat_chk ).setEnabled( item.days.contains( SATURDAY ) );
            findViewById( R.id.day_sun_chk ).setEnabled( item.days.contains( SUNDAY ) );

            findViewById( R.id.day_mon_chk ).setOnClickListener( this );
            findViewById( R.id.day_tue_chk ).setOnClickListener( this );
            findViewById( R.id.day_wed_chk ).setOnClickListener( this );
            findViewById( R.id.day_thu_chk ).setOnClickListener( this );
            findViewById( R.id.day_fri_chk ).setOnClickListener( this );
            findViewById( R.id.day_sat_chk ).setOnClickListener( this );
            findViewById( R.id.day_sun_chk ).setOnClickListener( this );
            
            if( !item.isEvaluated() )
            {
                Calendar c = Calendar.getInstance();
                c.set( Calendar.YEAR, item.year );
                c.set( Calendar.WEEK_OF_YEAR, item.weekOfYear );

                c.set( Calendar.DAY_OF_WEEK, Calendar.MONDAY );
                if( now.after( c ) && item.days.contains( MONDAY ) ){ findViewById( R.id.mon_layout ).setOnClickListener( this ); }

                c.set( Calendar.DAY_OF_WEEK, Calendar.TUESDAY );
                if( now.after( c ) && item.days.contains( TUESDAY ) ){ findViewById( R.id.tue_layout ).setOnClickListener( this ); }

                c.set( Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY );
                if( now.after( c ) && item.days.contains( WEDNESDAY ) ){ findViewById( R.id.wed_layout ).setOnClickListener( this ); }

                c.set( Calendar.DAY_OF_WEEK, Calendar.THURSDAY );
                if( now.after( c ) && item.days.contains( THURSDAY ) ){ findViewById( R.id.thu_layout ).setOnClickListener( this ); }

                c.set( Calendar.DAY_OF_WEEK, Calendar.FRIDAY );
                if( now.after( c ) && item.days.contains( FRIDAY ) ){ findViewById( R.id.fri_layout ).setOnClickListener( this ); }

                c.set( Calendar.DAY_OF_WEEK, Calendar.SATURDAY );
                if( now.after( c ) && item.days.contains( SATURDAY ) ){ findViewById( R.id.sat_layout ).setOnClickListener( this ); }

                c.set( Calendar.DAY_OF_WEEK, Calendar.SUNDAY );
                if( now.after( c ) && item.days.contains( SUNDAY ) ){ findViewById( R.id.sun_layout ).setOnClickListener( this ); }
            }

            findViewById( R.id.day_mon_check_img ).setVisibility( item.isEvaluated( MONDAY ) && item.isSuccessful( MONDAY ) ? View.VISIBLE : View.INVISIBLE );
            findViewById( R.id.day_tue_check_img ).setVisibility( item.isEvaluated( IfThenPlan.Day.TUESDAY ) && item.isSuccessful( IfThenPlan.Day.TUESDAY ) ? View.VISIBLE : View.INVISIBLE );
            findViewById( R.id.day_wed_check_img ).setVisibility( item.isEvaluated( IfThenPlan.Day.WEDNESDAY ) && item.isSuccessful( IfThenPlan.Day.WEDNESDAY ) ? View.VISIBLE : View.INVISIBLE );
            findViewById( R.id.day_thu_check_img ).setVisibility( item.isEvaluated( IfThenPlan.Day.THURSDAY ) && item.isSuccessful( IfThenPlan.Day.THURSDAY ) ? View.VISIBLE : View.INVISIBLE );
            findViewById( R.id.day_fri_check_img ).setVisibility( item.isEvaluated( IfThenPlan.Day.FRIDAY ) && item.isSuccessful( IfThenPlan.Day.FRIDAY ) ? View.VISIBLE : View.INVISIBLE );
            findViewById( R.id.day_sat_check_img ).setVisibility( item.isEvaluated( IfThenPlan.Day.SATURDAY ) && item.isSuccessful( IfThenPlan.Day.SATURDAY ) ? View.VISIBLE : View.INVISIBLE );
            findViewById( R.id.day_sun_check_img ).setVisibility( item.isEvaluated( IfThenPlan.Day.SUNDAY ) && item.isSuccessful( IfThenPlan.Day.SUNDAY ) ? View.VISIBLE : View.INVISIBLE );
            findViewById( R.id.day_mon_fail_img ).setVisibility( item.isEvaluated( MONDAY ) && !item.isSuccessful( MONDAY ) ? View.VISIBLE : View.INVISIBLE );
            findViewById( R.id.day_tue_fail_img ).setVisibility( item.isEvaluated( IfThenPlan.Day.TUESDAY ) && !item.isSuccessful( IfThenPlan.Day.TUESDAY ) ? View.VISIBLE : View.INVISIBLE );
            findViewById( R.id.day_wed_fail_img ).setVisibility( item.isEvaluated( IfThenPlan.Day.WEDNESDAY ) && !item.isSuccessful( IfThenPlan.Day.WEDNESDAY ) ? View.VISIBLE : View.INVISIBLE );
            findViewById( R.id.day_thu_fail_img ).setVisibility( item.isEvaluated( IfThenPlan.Day.THURSDAY ) && !item.isSuccessful( IfThenPlan.Day.THURSDAY ) ? View.VISIBLE : View.INVISIBLE );
            findViewById( R.id.day_fri_fail_img ).setVisibility( item.isEvaluated( IfThenPlan.Day.FRIDAY ) && !item.isSuccessful( IfThenPlan.Day.FRIDAY ) ? View.VISIBLE : View.INVISIBLE );
            findViewById( R.id.day_sat_fail_img ).setVisibility( item.isEvaluated( IfThenPlan.Day.SATURDAY ) && !item.isSuccessful( IfThenPlan.Day.SATURDAY ) ? View.VISIBLE : View.INVISIBLE );
            findViewById( R.id.day_sun_fail_img ).setVisibility( item.isEvaluated( IfThenPlan.Day.SUNDAY ) && !item.isSuccessful( IfThenPlan.Day.SUNDAY ) ? View.VISIBLE : View.INVISIBLE );

            findViewById( R.id.save ).requestFocus();

        }
        else
        {
            //behavior layout events
            findViewById( R.id.eating_image ).setOnClickListener( this );
            findViewById( R.id.activity_image ).setOnClickListener( this );
            findViewById( R.id.alcohol_image ).setOnClickListener( this );
            findViewById( R.id.smoking_image ).setOnClickListener( this );
        }

        ( (CheckBox) findViewById( R.id.day_mon_chk ) ).setChecked( item.days.contains( MONDAY ) );
        ( (CheckBox) findViewById( R.id.day_tue_chk ) ).setChecked( item.days.contains( IfThenPlan.Day.TUESDAY ) );
        ( (CheckBox) findViewById( R.id.day_wed_chk ) ).setChecked( item.days.contains( IfThenPlan.Day.WEDNESDAY ) );
        ( (CheckBox) findViewById( R.id.day_thu_chk ) ).setChecked( item.days.contains( IfThenPlan.Day.THURSDAY ) );
        ( (CheckBox) findViewById( R.id.day_fri_chk ) ).setChecked( item.days.contains( IfThenPlan.Day.FRIDAY ) );
        ( (CheckBox) findViewById( R.id.day_sat_chk ) ).setChecked( item.days.contains( IfThenPlan.Day.SATURDAY ) );
        ( (CheckBox) findViewById( R.id.day_sun_chk ) ).setChecked( item.days.contains( IfThenPlan.Day.SUNDAY ) );


        DateFormatSymbols dfs = new DateFormatSymbols();

        Calendar startCal = Calendar.getInstance();
        startCal.set( Calendar.YEAR, item.year );
        startCal.set( Calendar.WEEK_OF_YEAR, item.weekOfYear );

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
//            case R.id.mon_layout:
//                toggleSuccess( R.id.day_mon_check_img, R.id.day_mon_fail_img );
//                break;
//            case R.id.tue_layout:
//                toggleSuccess( R.id.day_tue_check_img, R.id.day_tue_fail_img );
//                break;
//            case R.id.wed_layout:
//                toggleSuccess( R.id.day_wed_check_img, R.id.day_wed_fail_img );
//                break;
//            case R.id.thu_layout:
//                toggleSuccess( R.id.day_thu_check_img, R.id.day_thu_fail_img );
//                break;
//            case R.id.fri_layout:
//                toggleSuccess( R.id.day_fri_check_img, R.id.day_fri_fail_img );
//                break;
//            case R.id.sat_layout:
//                toggleSuccess( R.id.day_sat_check_img, R.id.day_sat_fail_img );
//                break;
//            case R.id.sun_layout:
//                toggleSuccess( R.id.day_sun_check_img, R.id.day_sun_fail_img );
//                break;
//
            case R.id.day_mon_chk:
                toggleSuccess( R.id.day_mon_check_img, R.id.day_mon_fail_img );
                ( (CheckBox) findViewById( R.id.day_mon_chk ) ).setChecked( true );
                break;
            case R.id.day_tue_chk:
                toggleSuccess( R.id.day_tue_check_img, R.id.day_tue_fail_img );
                ( (CheckBox) findViewById( R.id.day_tue_chk ) ).setChecked( true );
                break;
            case R.id.day_wed_chk:
                toggleSuccess( R.id.day_wed_check_img, R.id.day_wed_fail_img );
                ( (CheckBox) findViewById( R.id.day_wed_chk ) ).setChecked( true );
                break;
            case R.id.day_thu_chk:
                toggleSuccess( R.id.day_thu_check_img, R.id.day_thu_fail_img );
                ( (CheckBox) findViewById( R.id.day_thu_chk ) ).setChecked( true );
                break;
            case R.id.day_fri_chk:
                toggleSuccess( R.id.day_fri_check_img, R.id.day_fri_fail_img );
                ( (CheckBox) findViewById( R.id.day_fri_chk ) ).setChecked( true );
                break;
            case R.id.day_sat_chk:
                toggleSuccess( R.id.day_sat_check_img, R.id.day_sat_fail_img );
                ( (CheckBox) findViewById( R.id.day_sat_chk ) ).setChecked( true );
                break;
            case R.id.day_sun_chk:
                toggleSuccess( R.id.day_sun_check_img, R.id.day_sun_fail_img );
                ( (CheckBox) findViewById( R.id.day_sun_chk ) ).setChecked( true );
                break;


            default:
                break;
        }
    }

    private void toggleSuccess( int checkImgResId, int failImgResId )
    {
        ImageView check = findViewById( checkImgResId );
        ImageView fail = findViewById( failImgResId );
        if( check.getVisibility() == View.VISIBLE )
        {
            fail.setVisibility( View.VISIBLE );
            check.setVisibility( View.GONE );
        }
        else
        {
            fail.setVisibility( View.GONE );
            check.setVisibility( View.VISIBLE );
        }
    }

    private Boolean getDayEvaluationFromUI( int checkImgResId, int failImgResId )
    {
        if( findViewById( checkImgResId ).getVisibility() == View.VISIBLE )
        {
            return true;
        }

        if( findViewById( failImgResId).getVisibility() == View.VISIBLE )
        {
            return false;
        }

        return null;
    }

    protected String fillItemFromUI() throws JSONException
    {
        String error = "";

        Calendar now = Calendar.getInstance();
        // if the plan is passed it's week it can only be evaluated
        if( now.get( Calendar.WEEK_OF_YEAR ) != item.weekOfYear || now.get( Calendar.YEAR ) != item.year )
        {
            Calendar ic = Calendar.getInstance();
            ic.set( Calendar.YEAR, item.year );
            ic.set( Calendar.WEEK_OF_YEAR, item.weekOfYear );

            for( int i = 0 ; i < item.days.size() ; i++ )
            {
                Boolean check = null;
                switch( item.days.get( i ) )
                {
                    case MONDAY:
                        ic.set( Calendar.DAY_OF_WEEK, Calendar.MONDAY );
                        if( ic.after( now ) ){ break; }
                        check = getDayEvaluationFromUI( R.id.day_mon_check_img, R.id.day_mon_fail_img );
                        if( null == check ){ error = "evaluate all past days"; } else { item.evaluate( MONDAY, check ); }
                        break;
                    case TUESDAY:
                        ic.set( Calendar.DAY_OF_WEEK, Calendar.TUESDAY );
                        if( ic.after( now ) ){ break; }
                        check = getDayEvaluationFromUI( R.id.day_tue_check_img, R.id.day_tue_fail_img );
                        if( null == check ){ error = "evaluate all past days"; } else { item.evaluate( TUESDAY, check ); }
                        break;
                    case WEDNESDAY:
                        ic.set( Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY );
                        if( ic.after( now ) ){ break; }
                        check = getDayEvaluationFromUI( R.id.day_wed_check_img, R.id.day_wed_fail_img );
                        if( null == check ){ error = "evaluate all past days"; } else { item.evaluate( WEDNESDAY, check ); }
                        break;
                    case THURSDAY:
                        ic.set( Calendar.DAY_OF_WEEK, Calendar.THURSDAY );
                        if( ic.after( now ) ){ break; }
                        check = getDayEvaluationFromUI( R.id.day_thu_check_img, R.id.day_thu_fail_img );
                        if( null == check ){ error = "evaluate all past days"; } else { item.evaluate( THURSDAY, check ); }
                        break;
                    case FRIDAY:
                        ic.set( Calendar.DAY_OF_WEEK, Calendar.FRIDAY );
                        if( ic.after( now ) ){ break; }
                        check = getDayEvaluationFromUI( R.id.day_fri_check_img, R.id.day_fri_fail_img );
                        if( null == check ){ error = "evaluate all past days"; } else { item.evaluate( FRIDAY, check ); }
                        break;
                    case SATURDAY:
                        ic.set( Calendar.DAY_OF_WEEK, Calendar.SATURDAY );
                        if( ic.after( now ) ){ break; }
                        check = getDayEvaluationFromUI( R.id.day_sat_check_img, R.id.day_sat_fail_img );
                        if( null == check ){ error = "evaluate all past days"; } else { item.evaluate( SATURDAY, check ); }
                        break;
                    case SUNDAY:
                        ic.set( Calendar.DAY_OF_WEEK, Calendar.SUNDAY );
                        if( ic.after( now ) ){ break; }
                        check = getDayEvaluationFromUI( R.id.day_sun_check_img, R.id.day_sun_fail_img );
                        if( null == check ){ error = "evaluate all past days"; } else { item.evaluate( SUNDAY, check ); }
                        break;
                }
            }
            return error;
        }


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
//        if( 0 == actionDate.trim().length() )
//        {
//            error += ( error.length() > 0 ? " and " : "" ) + "select a DATE";
//        }
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

//        item.put( "id", String.valueOf( itemId ) );
//        item.put( "if", ifStatement );
//        item.put( "then", thenStatement );
//        item.put( "active", activeSwitch.isChecked() );
//        item.put( "date", actionDate );
//        item.put( "DIET", getBehaviorIsSelected( DIET ) );
//        item.put( "ACTIVITY", getBehaviorIsSelected( ACTIVITY ) );
//        item.put( "ALCOHOL", getBehaviorIsSelected( ALCOHOL ) );
//        item.put( "SMOKING", getBehaviorIsSelected( SMOKING ) );

        item.ifStatement = ifStatement;
        item.thenStatement = thenStatement;
        item.active = activeSwitch.isChecked();

        item.targetBehaviors.clear();
        if( getBehaviorIsSelected( DIET ) ) { item.targetBehaviors.add( DIET ); }
        if( getBehaviorIsSelected( ACTIVITY ) ) { item.targetBehaviors.add( ACTIVITY ); }
        if( getBehaviorIsSelected( ALCOHOL ) ) { item.targetBehaviors.add( ALCOHOL ); }
        if( getBehaviorIsSelected( SMOKING ) ) { item.targetBehaviors.add( SMOKING ); }

        item.days.clear();

        if( ( (CheckBox) findViewById( R.id.day_mon_chk ) ).isChecked() ) { item.days.add( MONDAY ); }
        if( ( (CheckBox) findViewById( R.id.day_tue_chk ) ).isChecked() ) { item.days.add( IfThenPlan.Day.TUESDAY ); }
        if( ( (CheckBox) findViewById( R.id.day_wed_chk ) ).isChecked() ) { item.days.add( IfThenPlan.Day.WEDNESDAY ); }
        if( ( (CheckBox) findViewById( R.id.day_thu_chk ) ).isChecked() ) { item.days.add( IfThenPlan.Day.THURSDAY ); }
        if( ( (CheckBox) findViewById( R.id.day_fri_chk ) ).isChecked() ) { item.days.add( IfThenPlan.Day.FRIDAY ); }
        if( ( (CheckBox) findViewById( R.id.day_sat_chk ) ).isChecked() ) { item.days.add( IfThenPlan.Day.SATURDAY ); }
        if( ( (CheckBox) findViewById( R.id.day_sun_chk ) ).isChecked() ) { item.days.add( IfThenPlan.Day.SUNDAY ); }
        return "";
    }

    private void save()
    {
        try
        {
            String error = fillItemFromUI();
            if( 0 == error.length() )
            {
//                JSONArrayIOHandler.saveItem( getBaseContext(), item, getFilesDir().getPath() + "/" + FILENAME );
                saveItem();
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


