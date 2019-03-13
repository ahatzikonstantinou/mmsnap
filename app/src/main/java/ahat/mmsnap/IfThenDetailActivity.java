package ahat.mmsnap;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import ahat.mmsnap.Models.ConversionException;
import ahat.mmsnap.Models.IfThenPlan;

import static ahat.mmsnap.ApplicationStatus.Behavior.ACTIVITY;
import static ahat.mmsnap.ApplicationStatus.Behavior.ALCOHOL;
import static ahat.mmsnap.ApplicationStatus.Behavior.DIET;
import static ahat.mmsnap.ApplicationStatus.Behavior.SMOKING;

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

        //behavior layout events
        findViewById( R.id.eating_image ).setOnClickListener( this );
        findViewById( R.id.activity_image ).setOnClickListener( this );
        findViewById( R.id.alcohol_image ).setOnClickListener( this );
        findViewById( R.id.smoking_image ).setOnClickListener( this );
        findViewById( R.id.date_selector_layout ).setOnClickListener( this );
        findViewById( R.id.save ).setOnClickListener( this );

        ifStatementTextView = findViewById( R.id.item_if_statement );
        thenStatementTextView = findViewById( R.id.item_then_statement );
        activeSwitch = findViewById( R.id.active_switch );

        BehaviorIsSelected = new HashMap<>();
        BehaviorIsSelected.put( DIET, false );
        BehaviorIsSelected.put( ACTIVITY, false );
        BehaviorIsSelected.put( ALCOHOL, false );
        BehaviorIsSelected.put( SMOKING, false );

        try
        {
            ifStatementTextView.setText( item.ifStatement );
            thenStatementTextView.setText( item.thenStatement );
            activeSwitch.setChecked( item.active );
//            actionDate = item.getString( "date" );
//            setDateTextView();

            setBehaviorIsSelected( DIET, item.isTarget( DIET ) );
            setBehaviorIsSelected( ACTIVITY, item.isTarget( ACTIVITY ) );
            setBehaviorIsSelected( ALCOHOL, item.isTarget( ALCOHOL ) );
            setBehaviorIsSelected( SMOKING, item.isTarget( SMOKING ) );
        }
        catch( Exception e )
        {
            e.printStackTrace();
            View view = findViewById( getContentRootLayoutResId() );
            Snackbar.make( view, "Could not parse IF THEN item!", Snackbar.LENGTH_LONG).show();
        }

        updateBehaviorUI( DIET );
        updateBehaviorUI( ACTIVITY );
        updateBehaviorUI( ALCOHOL );
        updateBehaviorUI( SMOKING );
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
//            case R.id.date_selector_layout:
//
//                final Calendar calendar = getCalendarFromYYYYMMDD( actionDate );
//
//                //TODO: for multiple date selection use http://codesfor.in/android-multi-datepicker-calendar-example/
//                DatePickerDialog datePickerDialog = new DatePickerDialog(
//                    this, this,
//                    calendar.get( Calendar.YEAR ),
//                    calendar.get( Calendar.MONTH ),
//                    calendar.get( Calendar.DAY_OF_MONTH ) );
//                datePickerDialog.show();
//                break;
            case R.id.save:
                save();
                break;
            default:
                break;
        }
    }

    protected String fillItemFromUI() throws JSONException
    {
        String ifStatement = ifStatementTextView.getText().toString().trim();
        String thenStatement = thenStatementTextView.getText().toString().trim();
        String error = "";
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
