package ahat.mmsnap;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

import org.json.JSONArray;

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
    public static JSONArray loadItems( final Activity activity, View view, String errorMessage, String filePath )
    {
        JSONArray items = new JSONArray();

        File file = new File( filePath );
        if( !file.exists() )
        {
            return items;
        }

        try
        {
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
            catch( Exception e )
            {
                e.printStackTrace();
                Snackbar.make( view, errorMessage, Snackbar.LENGTH_INDEFINITE )
                        .setAction( "Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                activity.startActivity( activity.getIntent() );
                            }
                        } ).show();
            }
            finally
            {
                is.close();
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return items;
    }

    public static void saveItems( Context context, JSONArray items, String filePath ) throws IOException
    {
        File file = new File( filePath );
        if(!file.exists())
        {
            file.createNewFile();
        }

        FileOutputStream fos = context.openFileOutput( file.getName(), Context.MODE_PRIVATE );
        fos.write( items.toString().getBytes() );
        fos.close();
    }
}
