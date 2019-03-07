package ahat.mmsnap;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public abstract class IfThenDetailActivity extends AppCompatActivity
    implements View.OnClickListener, DatePickerDialog.OnDateSetListener
{
    private static final int SELECTED_COLOR = Color.rgb( 255, 255, 0 );
    private static final int NOT_SELECTED_COLOR = Color.rgb( 255, 255, 255 );

    enum Behavior { EATING, ACTIVITY, ALCOHOL, SMOKING }
    protected HashMap<Behavior, Boolean> BehaviorIsSelected;
    protected String actionDate = "";
    protected abstract int getActivityResLayout();
    protected abstract int getContentRootLayoutResId();
    protected abstract JSONObject getIfThenItem();
    protected abstract Class<?> getListActivityClass();
    protected abstract String getSaveErrorMessage();

    protected JSONObject item;
    private String FILENAME;
    protected String getFILENAME()
    {
        return FILENAME;
    }
    private int itemId;
    protected int getItemId()
    {
        return itemId;
    }


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
        getSupportActionBar().setIcon( getResources().getDrawable( R.drawable.if_then_section_logo ) );
        getSupportActionBar().setTitle( R.string.title_activity_if_then );

        Bundle b = getIntent().getExtras();
        FILENAME = b.getString( "FILENAME" );
        itemId = -1;
        if( b.containsKey( "itemId" ) )
        {
            itemId = b.getInt( "itemId" );
        }

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
        BehaviorIsSelected.put( Behavior.EATING, false );
        BehaviorIsSelected.put( Behavior.ACTIVITY, false );
        BehaviorIsSelected.put( Behavior.ALCOHOL, false );
        BehaviorIsSelected.put( Behavior.SMOKING, false );

        try
        {
            ifStatementTextView.setText( item.getString( "if" ) );
            thenStatementTextView.setText( item.getString( "then" ) );
            activeSwitch.setChecked( item.getBoolean( "active" ) );
            actionDate = item.getString( "date" );
            setDateTextView();

            setBehaviorIsSelected( Behavior.EATING, item.getBoolean( "EATING" ) );
            setBehaviorIsSelected( Behavior.ACTIVITY, item.getBoolean( "ACTIVITY" ) );
            setBehaviorIsSelected( Behavior.ALCOHOL, item.getBoolean( "ALCOHOL" ) );
            setBehaviorIsSelected( Behavior.SMOKING, item.getBoolean( "SMOKING" ) );
        }
        catch( Exception e )
        {
            e.printStackTrace();
            View view = findViewById( getContentRootLayoutResId() );
            Snackbar.make( view, "Could not parse IF THEN item!", Snackbar.LENGTH_LONG).show();
        }

        updateBehaviorUI( Behavior.EATING );
        updateBehaviorUI( Behavior.ACTIVITY );
        updateBehaviorUI( Behavior.ALCOHOL );
        updateBehaviorUI( Behavior.SMOKING );
    }

    @Override
    public void onClick( View view )
    {

        switch (view.getId())
        {
            case R.id.eating_image:
                toggleBehavior( Behavior.EATING );
                break;
            case R.id.activity_image:
                toggleBehavior( Behavior.ACTIVITY );
                break;
            case R.id.alcohol_image:
                toggleBehavior( Behavior.ALCOHOL );
                break;
            case R.id.smoking_image:
                toggleBehavior( Behavior.SMOKING );
                break;
            case R.id.date_selector_layout:
                //TODO: for multiple date selection use http://codesfor.in/android-multi-datepicker-calendar-example/
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this, this,
                    Calendar.getInstance().get( Calendar.YEAR ),
                    Calendar.getInstance().get( Calendar.MONTH ),
                    Calendar.getInstance().get( Calendar.DAY_OF_MONTH ) );
                datePickerDialog.show();
                break;
            case R.id.save:
                saveItem();
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
        if( 0 == actionDate.trim().length() )
        {
            error += ( error.length() > 0 ? " and " : "" ) + "select a DATE";
        }
        if( !getBehaviorIsSelected( Behavior.EATING ) &&
            !getBehaviorIsSelected( Behavior.ACTIVITY ) &&
            !getBehaviorIsSelected( Behavior.ALCOHOL ) &&
            !getBehaviorIsSelected( Behavior.SMOKING )
        )
        {
            error += ( error.length() > 0 ? " and " : "" ) + "select at least one HEALTH behavior";
        }

        if( 0 < error.length() )
        {
            return error;
        }

        item.put( "id", String.valueOf( itemId ) );
        item.put( "if", ifStatement );
        item.put( "then", thenStatement );
        item.put( "active", activeSwitch.isChecked() );
        item.put( "date", actionDate );
        item.put( "EATING", getBehaviorIsSelected( Behavior.EATING ) );
        item.put( "ACTIVITY", getBehaviorIsSelected( Behavior.ACTIVITY ) );
        item.put( "ALCOHOL", getBehaviorIsSelected( Behavior.ALCOHOL ) );
        item.put( "SMOKING", getBehaviorIsSelected( Behavior.SMOKING ) );

        return "";
    }

    private void saveItem()
    {
        try
        {
            String error = fillItemFromUI();
            if( 0 == error.length() )
            {
                JSONArrayIOHandler.saveItem( getBaseContext(), item, getFilesDir().getPath() + "/" + FILENAME );
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

    protected boolean getBehaviorIsSelected( Behavior b )
    {
        return BehaviorIsSelected.get( b );
    }
    protected boolean setBehaviorIsSelected( Behavior b, Boolean v )
    {
        return BehaviorIsSelected.put( b, v );
    }
    protected void toggleBehavior( Behavior behavior )
    {
        setBehaviorIsSelected( behavior, !getBehaviorIsSelected( behavior ) );
        updateBehaviorUI( behavior );
    }

    protected void updateBehaviorUI( Behavior behavior )
    {
        ImageView imageView;
        int color = getBehaviorIsSelected( behavior ) ? SELECTED_COLOR : NOT_SELECTED_COLOR;

        switch( behavior )
        {
            case EATING:
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

    public void onDateSet( DatePicker view, int year, int monthOfYear, int dayOfMonth)
    {
        actionDate = year + "-" + monthOfYear + "-" + dayOfMonth;
        setDateTextView();
    }

    protected void setDateTextView()
    {
        TextView dates = findViewById( R.id.plan_dates );
        if( 0 == actionDate.trim().length() )
        {
            dates.setText( R.string.select_plan_dates );
            return;
        }

        DateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd");
        final Calendar calendar = Calendar.getInstance();
        try
        {
            final Date date = dateFormat.parse( actionDate );
            calendar.setTime( date );
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        DateFormatSymbols dfs = new DateFormatSymbols();
        dates.setText( dfs.getShortWeekdays()[ calendar.get( Calendar.DAY_OF_WEEK ) ]+ " " + calendar.get( Calendar.DAY_OF_MONTH ) + " " +
                       dfs.getMonths()[ calendar.get( Calendar.MONTH ) ] + " " + calendar.get( Calendar.YEAR ));
    }
}
