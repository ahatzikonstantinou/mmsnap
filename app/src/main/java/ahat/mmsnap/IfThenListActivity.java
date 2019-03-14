package ahat.mmsnap;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import ahat.mmsnap.JSON.JSONArrayIOHandler;
import ahat.mmsnap.Models.ConversionException;
import ahat.mmsnap.Models.IfThenPlan;

// Override the abstract methods to create activities that
// - display a list of items,
// - have a fab button that starts the detail activity to add new items or delete selected items
// - on click start the detail activity with the corresponding item
// - on long click display checkboxes to select and delete selected items
// Each such activity should
// - have each own Adapter extending the IfThenList adapter
// - specify the FILENAME where the items are stored
// Override getActivityResLayout(), getContentRootLayoutResId(), getListViewResId() if you are using a different layout
// Detail activities must get the FILENAME from the Bundle of the intent starting the detail activity, under key "FILENAME"
public abstract class IfThenListActivity extends AppCompatActivity
{

    protected boolean               delete;
    protected FloatingActionButton  fab;
//    protected ArrayList<IfThenPlan> items;
    protected IfThenListAdapter     adapter;

    protected abstract IfThenListAdapter createListAdapter();

//    protected abstract String getFilename();

    protected abstract String getLoadItemsErrorMessage();

    protected abstract String getDeleteItemsErrorMessage();

    protected abstract int getSubtitleStringResId();

    protected abstract int getLogoDrawableResId();

    protected abstract Class<?> getDetailActivityClass();

    protected abstract void loadItems() throws IOException, JSONException, ConversionException;

    protected abstract void saveItems() throws IOException, JSONException, ConversionException;

    protected abstract void deleteItems( ArrayList<Integer> deleteIndex );

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
        });

        delete = false;

        fab = findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View view )
            {
                if( delete )
                {
                    deleteItems( findViewById( getContentRootLayoutResId() ) );
                }
                else
                {
                    Intent intent = new Intent( getBaseContext(), getDetailActivityClass() );
                    startActivity( intent );
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setIcon( getResources().getDrawable( getLogoDrawableResId(), null ) );
        getSupportActionBar().setTitle( getTitleStringResId() );
        getSupportActionBar().setSubtitle( getSubtitleStringResId() );

        try
        {
            loadItems();
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
                if( delete )
                {
                    if( adapter.deleteIndex.contains( i ) )
                    {
                        adapter.deleteIndex.remove( (Integer) i );
                    }
                    else
                    {
                        adapter.deleteIndex.add( i );
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
                if( !delete )
                {
                    fab.setImageResource( android.R.drawable.ic_menu_delete );
                    adapter.deleteAction = true;
                    adapter.deleteIndex.add( i );
                    delete = true;
                    adapter.notifyDataSetChanged();
                }
                else
                {
                    fab.setImageResource( R.drawable.ic_add_white_24dp );
                    adapter.deleteAction = false;
                    adapter.deleteIndex.clear();
                    delete = false;
                    adapter.notifyDataSetChanged();
                }
                return true;
            }
        } );

    }



    @Override
    public void onBackPressed()
    {
        if( delete )
        {
            fab.setImageResource( R.drawable.ic_add_white_24dp );
            adapter.deleteAction = false;
            adapter.deleteIndex.clear();
            delete = false;
            adapter.notifyDataSetChanged();
        }
        else
        {
//            startActivity( getParentActivityIntent() );
            startActivity( new Intent( this, IfThenActivity.class ) );

        }
    }

    private void deleteItems( View view )
    {
        try
        {
//            for( int i = items.length() ; i >= 0  ; i-- )
//            {
//                if( adapter.deleteIndex.contains( i ) )
//                {
//                    items.remove( i );
//                }
//            }
            deleteItems( adapter.deleteIndex );

//            JSONArrayIOHandler.saveItems( getBaseContext(), items, getFilesDir().getPath() + "/" + getFilename() );
            saveItems();
            delete = false;
            fab.setImageResource( R.drawable.ic_add_white_24dp );
            adapter.deleteAction = false;
            adapter.deleteIndex.clear();
            adapter.notifyDataSetChanged();
        }
        catch( Exception e )
        {
            Snackbar.make( view, getDeleteItemsErrorMessage(), Snackbar.LENGTH_INDEFINITE )
                    .setAction( "RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity( getIntent() );
                        }
                    } ).show();
        }

    }

}
