package ahat.mmsnap;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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

    enum Behavior { EATING, ACTIVITY, ALCOHOL, SMOKING } ;
    protected HashMap<Behavior, Boolean> BehaviorIsSelected;
    protected String actionDate;
    protected abstract int getActivityResLayout();
    protected abstract int getContentRootLayoutResId();
    protected abstract JSONObject getIfThenItem();

    private JSONObject item;
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
        findViewById( R.id.eating ).setOnClickListener( this );
        findViewById( R.id.activity ).setOnClickListener( this );
        findViewById( R.id.alcohol ).setOnClickListener( this );
        findViewById( R.id.smoking ).setOnClickListener( this );
        findViewById( R.id.date_selector_layout ).setOnClickListener( this );
        findViewById( R.id.action_plan_data ).setOnClickListener( this );
        findViewById( R.id.coping_plan_data ).setOnClickListener( this );

        ifStatementTextView = findViewById( R.id.item_if_statement );
        thenStatementTextView = findViewById( R.id.item_then_statement );

        BehaviorIsSelected = new HashMap<>();
        BehaviorIsSelected.put( Behavior.EATING, false );
        BehaviorIsSelected.put( Behavior.ACTIVITY, false );
        BehaviorIsSelected.put( Behavior.ALCOHOL, false );
        BehaviorIsSelected.put( Behavior.SMOKING, false );
    }

    @Override
    public void onClick( View view )
    {

        switch (view.getId())
        {
            case R.id.eating:
                toggleBehavior( Behavior.EATING );
                break;
            case R.id.activity:
                toggleBehavior( Behavior.ACTIVITY );
                break;
            case R.id.alcohol:
                toggleBehavior( Behavior.ALCOHOL );
                break;
            case R.id.smoking:
                toggleBehavior( Behavior.SMOKING );
                break;
            case R.id.date_selector_layout:
                //TODO: for multiple date selectio use http://codesfor.in/android-multi-datepicker-calendar-example/
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this, this,
                    Calendar.getInstance().get( Calendar.YEAR ),
                    Calendar.getInstance().get( Calendar.MONTH ),
                    Calendar.getInstance().get( Calendar.DAY_OF_MONTH ) );
                datePickerDialog.show();
                break;
            case R.id.action_plan_data:
                // custom dialog
                final Dialog dialog = new Dialog( this );
                dialog.setContentView( R.layout.if_then_dlg_layout );
                dialog.setTitle("Enter IF THEN statements");

                // set the custom dialog components
                Button dialogButton = dialog.findViewById( R.id.dialogButtonOK);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText ifEdit = v.findViewById( R.id.if_statement );
                        EditText thenEdit = v.findViewById( R.id.then_statement );

                        try
                        {
                            item.put( "if", ifEdit.getText().toString() );
                            item.put( "then", thenEdit.getText().toString() );
                        }
                        catch( JSONException e )
                        {
                            e.printStackTrace();
                            Snackbar.make( findViewById( getContentRootLayoutResId() ), "Could not read data from the IF THEN dialog", Snackbar.LENGTH_SHORT ).show();
                        }

                        ifStatementTextView.setText( ifEdit.getText().toString() );
                        thenStatementTextView.setText( thenEdit.getText().toString() );

                        dialog.dismiss();
                    }
                });
                dialogButton = dialog.findViewById( R.id.dialogButtonCancel);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                TextView ifText = dialog.findViewById( R.id.if_statement);
                TextView thenText = dialog.findViewById( R.id.then_statement);
                try
                {
                    ifText.setText( item.getString( "if" ) );
                    thenText.setText( item.getString( "then" ) );

                    dialog.show();
                }
                catch( Exception e )
                {
                    e.printStackTrace();
                    Snackbar.make( findViewById( getContentRootLayoutResId() ), "Could not open the IF THEN dialog", Snackbar.LENGTH_SHORT ).show();
                }
                break;
            default:
                break;
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
        ImageView imageView;
        boolean isSelected = getBehaviorIsSelected( behavior );
        setBehaviorIsSelected( behavior, !isSelected );
        int color = !isSelected ? SELECTED_COLOR : NOT_SELECTED_COLOR;

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
        TextView dates = findViewById( R.id.plan_dates );
        DateFormatSymbols dfs = new DateFormatSymbols();
        dates.setText( dfs.getShortWeekdays()[ calendar.get( Calendar.DAY_OF_WEEK ) ]+ " " + dayOfMonth + " " +
                       dfs.getMonths()[ calendar.get( Calendar.MONTH ) ] + " " + year );
    }
}
