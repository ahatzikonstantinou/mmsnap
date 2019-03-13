package ahat.mmsnap.JSON;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

public class JSONArrayIOHandler
{
    public static JSONArray loadItems( String filePath ) throws IOException, JSONException
    {
        JSONArray items = new JSONArray();

        File file = new File( filePath );
        if( !file.exists() )
        {
            return items;
        }

        FileInputStream is = new FileInputStream( file );

        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try
        {
            Reader reader = new BufferedReader( new InputStreamReader( is, "UTF-8" ) );
            int n;
            while( ( n = reader.read( buffer ) ) != -1 )
            {
                writer.write( buffer, 0, n );
            }

            String jsonString = writer.toString();
            items = new JSONArray( jsonString  );
        }
//        catch( Exception e )
//        {
//            e.printStackTrace();
//            Snackbar.make( view, errorMessage, Snackbar.LENGTH_INDEFINITE )
//                    .setAction( "Retry", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            activity.startActivity( activity.getIntent() );
//                        }
//                    } ).show();
//        }
        finally
        {
            is.close();
        }

        return items;
    }

    public static JSONObject loadItem( String filePath, int itemId ) throws IOException, JSONException
    {
        JSONArray items = loadItems( filePath );
        if( 0 <= itemId &&itemId < items.length() )
        {
            return (JSONObject) items.get( itemId );
        }

        return null;
    }

    public static void saveItems( Context context, JSONArray items, String filePath ) throws IOException
    {
        File file = new File( filePath );
        if(!file.exists())
        {
            file.createNewFile();
        }

        FileOutputStream fos = context.openFileOutput( file.getName(), Context.MODE_PRIVATE );
        try
        {
            fos.write( items.toString().getBytes() );
        }
        finally
        {
            fos.close();
        }
    }

    public static void saveItem( Context context, JSONObject item, String filePath ) throws IOException, JSONException
    {
        JSONArray items = loadItems( filePath );

        int itemId = item.getInt( "id" );
        if( -1 == itemId )
        {
            itemId =  items.length();
        }

        if( itemId < items.length() )
        {
            items.put( itemId, item );
        }
        else if( itemId == items.length() )
        {
            items.put( item );
        }

        JSONArrayIOHandler.saveItems( context, items, filePath );
    }
}
