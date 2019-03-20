package ahat.mmsnap;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import ahat.mmsnap.Models.ActionPlan;
import ahat.mmsnap.Models.ConversionException;
import ahat.mmsnap.Models.IfThenPlan;
import ahat.mmsnap.Models.Reminder;
import ahat.mmsnap.Notifications.ReminderAlarmReceiver;

// Override the abstract methods to create activities that
// - display a list of items,
// - have a fab button that starts the detail activity to add new items
// - on click start the detail activity with the corresponding item
// - on long click display checkboxes to select and copy or delete selected items
// Each such activity should
// - have each own Adapter extending the IfThenList adapter
// - specify the FILENAME where the items are stored
// Override getActivityResLayout(), getContentRootLayoutResId(), getListViewResId() if you are using a different layout
// Detail activities must get the FILENAME from the Bundle of the intent starting the detail activity, under key "FILENAME"
public abstract class IfThenListActivity extends AppCompatActivity
{

    protected boolean               selectItemsMode;
    protected FloatingActionButton  fab;
    protected ArrayList<IfThenPlan> items;
    protected IfThenListAdapter     adapter;

    protected Toolbar toolbar;

    protected abstract IfThenListAdapter createListAdapter();

//    protected abstract String getFilename();

    protected abstract String getLoadItemsErrorMessage();

    protected abstract String getDeleteItemsErrorMessage();

    protected abstract int getSubtitleStringResId();

    protected abstract int getLogoDrawableResId();

    protected abstract Class<?> getDetailActivityClass();

    protected abstract ArrayList<IfThenPlan> loadItems() throws IOException, JSONException, ConversionException;

    protected abstract void saveItems() throws IOException, JSONException, ConversionException;

//    protected abstract void deleteItems( ArrayList<Integer> selectedItemsIndex );
//
//    protected abstract void copyItems( ArrayList<Integer> selectedItemsIndex );

    protected abstract void putItemInIntent( Intent intent, int itemIndex );

    protected int getTitleStringResId()
    {
        return R.string.title_activity_if_then;
    }

    protected int getActivityResLayout()
    {
        return R.layout.activity_ifthen_list;
    }

    protected int getContentRootLayoutResId()
    {
        return R.id.if_then_list_main_layout;
    }

    protected int getListViewResId()
    {
        return R.id.if_then_list;
    }


    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( getActivityResLayout() );
        toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        // Explicitly set what the back button in the toolbar does in order to reset when deleting and showing the select checkboxes
        toolbar.setNavigationOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                onBackPressed();
            }
        });

        selectItemsMode = false;

        fab = findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View view )
            {
                Intent intent = new Intent( getBaseContext(), getDetailActivityClass() );
                startActivity( intent );
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setIcon( getResources().getDrawable( getLogoDrawableResId(), null ) );
        getSupportActionBar().setTitle( getTitleStringResId() );
        getSupportActionBar().setSubtitle( getSubtitleStringResId() );

        try
        {
            items = loadItems();
        }
        catch( Exception e )
        {
            e.printStackTrace();
            Snackbar.make( findViewById( getContentRootLayoutResId() ), getLoadItemsErrorMessage(), Snackbar.LENGTH_INDEFINITE )
                    .setAction( "RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity( getIntent() );
                        }
                    } ).show();
        }

        ListView list = findViewById( getListViewResId() );
        adapter = createListAdapter();
        list.setAdapter( adapter );
        list.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView<?> adapterView, View view, int i, long l )
            {
                if( selectItemsMode )
                {
                    if( adapter.menuActionItemIndex.contains( i ) )
                    {
                        adapter.menuActionItemIndex.remove( (Integer) i );
                    }
                    else
                    {
                        adapter.menuActionItemIndex.add( i );
                    }
                    adapter.notifyDataSetChanged();
                }
                else
                {
                    Intent intent = new Intent( getBaseContext(), getDetailActivityClass() );
                    putItemInIntent( intent, i );
                    startActivity( intent );
                }
            }
        } );

        list.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick( AdapterView<?> adapterView, View view, int i, long l )
            {
                if( selectItemsMode )
                {
////                    fab.setImageResource( R.drawable.ic_add_white_24dp );
//                    fab.setVisibility( View.VISIBLE );
//                    adapter.menuAction = false;
//                    adapter.menuActionItemIndex.clear();
//                    selectItemsMode = false;
//                    adapter.notifyDataSetChanged();
//                    showMenuActions( true );
                    setSelectItemsMode( false );
                }
                else
                {
////                    fab.setImageResource( android.R.drawable.ic_menu_delete );
//                    fab.setVisibility( View.GONE );
//                    adapter.menuAction = true;
//                    adapter.menuActionItemIndex.add( i );
//                    selectItemsMode = true;
//                    adapter.notifyDataSetChanged();
//                    showMenuActions( false );
                    adapter.menuActionItemIndex.add( i );
                    setSelectItemsMode( true );
                }

                return true;
            }
        } );

        invalidateOptionsMenu();
    }

    @Override
    protected void onSaveInstanceState( Bundle outState )
    {
        super.onSaveInstanceState(outState);

        outState.putBoolean( "selectItemsMode", selectItemsMode );
        outState.putIntegerArrayList( "menuActionItemIndex", adapter.menuActionItemIndex );
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState( savedInstanceState );

        adapter.menuActionItemIndex = savedInstanceState.getIntegerArrayList( "menuActionItemIndex" );
        setSelectItemsMode( savedInstanceState.getBoolean( "selectItemsMode" ) );
    }

    private void setSelectItemsMode( boolean set )
    {
        selectItemsMode = set;
        adapter.menuAction = set;

        if( set )
        {
            fab.setVisibility( View.GONE );
        }
        else
        {
            fab.setVisibility( View.VISIBLE );
            adapter.menuActionItemIndex.clear();
        }

        invalidateOptionsMenu();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onPrepareOptionsMenu( Menu menu )
    {
        // ahat: NOTE. The only way to show/hide action menus is to return true/false from onPrepareOptionsMenu, which is called as a result of
        // calling invalidateOptionsMenu. findViewById always crashes
        return selectItemsMode;
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu)
    {
        getMenuInflater().inflate( R.menu.plans, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder( this );//.create();
        alertDialog .setNegativeButton( android.R.string.no, null );

        switch (item.getItemId())
        {
            case R.id.action_copy:
                alertDialog.setTitle("Copy plans")
                           .setMessage("Are you sure you wish to create copies of the selected plans in the current week?" )
                           .setCancelable(true)
                           .setPositiveButton( android.R.string.yes,
                                       new DialogInterface.OnClickListener() {
                                           public void onClick( DialogInterface dialog, int which) {
                                               dialog.dismiss();
                                               copyItems();
                                               setSelectItemsMode( false );
                                           }
                                       })
                           .show();
                return false;
            case R.id.action_delete:
                alertDialog.setTitle("Delete plans")
                           .setMessage("Are you sure you wish to delete the selected plans?" )
                           .setPositiveButton( android.R.string.yes,
                                       new DialogInterface.OnClickListener() {
                                           public void onClick( DialogInterface dialog, int which) {
                                               dialog.dismiss();
                                               deleteItems();
                                               setSelectItemsMode( false );
                                           }
                                       })
                           .show();
                return false;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed()
    {
        if( selectItemsMode )
        {
            fab.setImageResource( R.drawable.ic_add_white_24dp );
            adapter.menuAction = false;
            adapter.menuActionItemIndex.clear();
            selectItemsMode = false;
            adapter.notifyDataSetChanged();
        }
        else
        {
//            startActivity( getParentActivityIntent() );
            startActivity( new Intent( this, IfThenActivity.class ) );

        }
    }

    private void copyItems()
    {
        try
        {
            ArrayList<IfThenPlan> copies = new ArrayList<>();
            int existingSize = items.size();
            for( int i = 0; i < existingSize; i++ )
            {
                if( adapter.menuActionItemIndex.contains( i ) )
                {
                    copies.add( items.get( i ).createCopyInCurrentWeek( existingSize + copies.size() ) );
                }
            }
            for( int i = 0 ; i < copies.size() ; i++ )
            {
                items.add( copies.get( i ) );
            }
            saveItems();
        }
        catch( Exception e )
        {
            Snackbar.make( findViewById( getContentRootLayoutResId() ), getDeleteItemsErrorMessage(), Snackbar.LENGTH_LONG )
                    .show();
        }
    }


    private void deleteItems()
    {
        try
        {
            for( int i = items.size() ; i >= 0  ; i-- )
            {
                if( adapter.menuActionItemIndex.contains( i ) )
                {
                    IfThenPlan item = items.get( i );
                    // cancel previous reminders
                    for( IfThenPlan.WeekDay day : item.days )
                    {
                        for( Reminder reminder : item.reminders )
                        {
                            ReminderAlarmReceiver.cancelAlarms( this, item.year, item.weekOfYear, day, reminder.hour, reminder.minute, "action" );
                        }
                    }
                    items.remove( i );
                }
            }

            //before saving re-index items
            for( int i = 0 ; i < items.size() ; i++ )
            {
                items.get( i ).id = i;
            }
            saveItems();
//            selectItemsMode = false;
//            fab.setImageResource( R.drawable.ic_add_white_24dp );
//            adapter.menuAction = false;
//            adapter.menuActionItemIndex.clear();
//            adapter.notifyDataSetChanged();
        }
        catch( Exception e )
        {
            Snackbar.make( findViewById( getContentRootLayoutResId() ), getDeleteItemsErrorMessage(), Snackbar.LENGTH_LONG )
                    .show();
        }

    }

}
