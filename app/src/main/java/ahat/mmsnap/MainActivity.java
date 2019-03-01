package ahat.mmsnap;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Process;
import android.widget.Button;

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener, android.view.View.OnClickListener
{

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.addDrawerListener( toggle );
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener( this );

        //buttons events
        Button mmsnapButton = ( Button ) findViewById( R.id.mmsnap_btn );
        mmsnapButton.setOnClickListener( this );
        Button assessmentsButton = ( Button ) findViewById( R.id.assessments_btn );
        assessmentsButton.setOnClickListener( this );
        Button educationButton = ( Button ) findViewById( R.id.education_btn );
        educationButton.setOnClickListener( this );
        Button ifThenButton = ( Button ) findViewById( R.id.if_then_btn );
        ifThenButton.setOnClickListener( this );
        Button agentsButton = ( Button ) findViewById( R.id.agents_btn );
        agentsButton.setOnClickListener( this );
    }

    @Override
    public void onClick( View view )
    {
        Intent intent;
        switch (view.getId()){
            case R.id.mmsnap_btn:
                intent = new Intent(this, MMSNAPActivity.class);
                startActivity( intent );
                break;
            case R.id.assessments_btn:
                intent = new Intent(this, AssessmentsActivity.class);
                startActivity( intent );
                break;
            case R.id.education_btn:
                intent = new Intent(this, EduActivity.class);
                startActivity( intent );
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        if( drawer.isDrawerOpen( GravityCompat.START ) )
        {
            drawer.closeDrawer( GravityCompat.START );
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if( id == R.id.action_settings )
        {
            startActivity( new Intent( MainActivity.this, SettingsActivity.class ) );
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    @SuppressWarnings( "StatementWithEmptyBody" )
    @Override
    public boolean onNavigationItemSelected( MenuItem item )
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if( id == R.id.nav_camera )
//        {
//            // Handle the camera action
//        }
//        else if( id == R.id.nav_gallery )
//        {
//
//        }
//        else if( id == R.id.nav_slideshow )
//        {
//
//        }
//        else if( id == R.id.nav_manage )
//        {
//
//        }
//        else
        if( id == R.id.nav_exit )
        {
            Process.sendSignal(Process.myPid(), Process.SIGNAL_KILL);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        drawer.closeDrawer( GravityCompat.START );
        return true;
    }
}
