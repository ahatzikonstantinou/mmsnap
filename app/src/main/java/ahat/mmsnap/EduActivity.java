package ahat.mmsnap;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class EduActivity extends AppCompatActivity
    implements View.OnClickListener
{

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_edu );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setIcon( getResources().getDrawable( R.drawable.edu_section_logo) );

        //buttons events
        findViewById( R.id.edu_what_btn ).setOnClickListener( this );
        findViewById( R.id.edu_if_btn ).setOnClickListener( this );
        findViewById( R.id.edu_then_btn ).setOnClickListener( this );
        findViewById( R.id.edu_test_btn ).setOnClickListener( this );
    }

    @Override
    public void onClick( View view )
    {
        Intent intent = new Intent( this, MMSNAPSubCategoryActivity.class);
        Bundle b = new Bundle();
        b.putSerializable( "section", MMSNAPSubCategoryActivity.Section.EDU );

        switch (view.getId()){
            case R.id.edu_what_btn:
                b.putInt( "subcategory", 0 );
                intent.putExtras( b );
                startActivity( intent );
                break;
            case R.id.edu_if_btn:
                b.putInt( "subcategory", 1 );
                intent.putExtras( b );
                startActivity( intent );
                break;
            case R.id.edu_then_btn:
                b.putInt( "subcategory", 2 );
                intent.putExtras( b );
                startActivity( intent );
                break;
            case R.id.edu_test_btn:
                intent = new Intent( this, TestActivity.class);
                startActivity( intent );
                break;
            default:
                break;
        }
    }
}
