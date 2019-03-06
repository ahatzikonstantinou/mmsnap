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

    protected boolean delete;
    protected FloatingActionButton fab;
    protected JSONArray items;
    protected IfThenListAdapter adapter;

    protected abstract IfThenListAdapter createListAdapter();

    protected abstract String getFilename();

    protected abstract String getLoadItemsErrorMessage();

    protected abstract String getDeleteItemsErrorMessage();

    protected abstract int getSubtitleStringResId();

    protected abstract int getLogoDrawableResId();

    protected abstract Class<?> getDetailActivityClass();

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
                    Bundle b = new Bundle();
                    b.putString( "FILENAME", getFilename() );
                    Intent intent = new Intent( getBaseContext(), getDetailActivityClass() );
                    intent.putExtras( b );
                    startActivity( intent );
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setIcon( getResources().getDrawable( getLogoDrawableResId() ) );
        getSupportActionBar().setTitle( getTitleStringResId() );
        getSupportActionBar().setSubtitle( getSubtitleStringResId() );


        items = JSONArrayIOHandler.loadItems( this, findViewById( getContentRootLayoutResId() ),
                                              getLoadItemsErrorMessage(),
                                              getFilesDir().getPath() + "/" + getFilename()
                                              );
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
                    Bundle b = new Bundle();
                    b.putInt( "itemId", i );
                    b.putString( "FILENAME", getFilename() );
                    intent.putExtras( b );
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
            startActivity( getParentActivityIntent() );
        }
    }

    private void deleteItems( View view )
    {
        try
        {
            for( int i = items.length() ; i >= 0  ; i-- )
            {
                if( adapter.deleteIndex.contains( i ) )
                {
                    items.remove( i );
                }
            }

            JSONArrayIOHandler.saveItems( getBaseContext(), items, getFilesDir().getPath() + "/" + getFilename() );
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
