package ahat.mmsnap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;

public abstract class AssessmentSubSectionActivity extends AppCompatActivity
{
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( getActivityResLayout() );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        // Explicitly set what the back button in the toolbar does in order to reset when deleting and showing the select checkboxes
        toolbar.setNavigationOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                onBackPressed();
            }
        } );
    }

    public void onBackPressed()
    {
        startActivity( new Intent( this, AssessmentsActivity.class ) );
    }

    protected abstract int getActivityResLayout();

}