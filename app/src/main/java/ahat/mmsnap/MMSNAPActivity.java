package ahat.mmsnap;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MMSNAPActivity extends AppCompatActivity
    implements OnClickListener
{

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_mmsnap );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        getSupportActionBar().setIcon( getResources().getDrawable( R.drawable.mmsnap_section_logo ) );

        //buttons events
        Button forButton = ( Button ) findViewById( R.id.mmsnap_for_btn );
        forButton.setOnClickListener( this );
        Button mmButton = ( Button ) findViewById( R.id.mmsnap_mm_btn );
        mmButton.setOnClickListener( this );
        Button mbButton = ( Button ) findViewById( R.id.mmsnap_mb_btn );
        mbButton.setOnClickListener( this );
        Button assocButton = ( Button ) findViewById( R.id.mmsnap_assoc_btn );
        assocButton.setOnClickListener( this );
        Button appButton = ( Button ) findViewById( R.id.mmsnap_app_btn );
        appButton.setOnClickListener( this );
    }

    private void applyLocalStatePolicy()
    {
        try
        {
            ApplicationStatus as = ApplicationStatus.getInstance( this );
            TextView messageView = findViewById( R.id.message );
            if( ApplicationStatus.NoInitialAssessments.NAME == as.getState().name() )
            {
                messageView.setText( R.string.please_complete_the_initial_assessments );
                messageView.setVisibility( View.VISIBLE );
            }
            else if( ApplicationStatus.NoFinalAssessments.NAME == as.getState().name() )
            {
                messageView.setText( R.string.please_complete_the_final_assessments );
                messageView.setVisibility( View.VISIBLE );
            }
            else
            {
                findViewById( R.id.message_layout ).setVisibility( View.GONE );
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
            Toast.makeText( this, "An error occurred retrieving the application status", Toast.LENGTH_SHORT ).show();
        }
    }
    @Override
    public void onClick( View view )
    {
        Intent intent = new Intent( this, MMSNAPSubCategoryActivity.class);
        Bundle b = new Bundle();
        b.putSerializable( "section", MMSNAPSubCategoryActivity.Section.MMSNAP );

        switch (view.getId()){
            case R.id.mmsnap_for_btn:
                b.putInt( "subcategory", 0 );
                intent.putExtras( b );
                startActivity( intent );
                break;
            case R.id.mmsnap_mm_btn:
                b.putInt( "subcategory", 1 );
                intent.putExtras( b );
                startActivity( intent );
                break;
            case R.id.mmsnap_mb_btn:
                b.putInt( "subcategory", 2 );
                intent.putExtras( b );
                startActivity( intent );
                break;
            case R.id.mmsnap_assoc_btn:
                b.putInt( "subcategory", 3 );
                intent.putExtras( b );
                startActivity( intent );
                break;
            case R.id.mmsnap_app_btn:
                b.putInt( "subcategory", 4 );
                intent.putExtras( b );
                startActivity( intent );
                break;
            default:
                break;
        }
    }
}
