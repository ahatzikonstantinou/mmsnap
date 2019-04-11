package ahat.mmsnap.json;

import android.content.Context;

import org.json.JSONException;

import java.io.IOException;

import ahat.mmsnap.models.ConversionException;

public class JSONStorage
{
    protected Context context;
    protected String filePath;

    public JSONStorage( Context context, String filePath )
    {
        this.context = context;
        this.filePath = filePath;
    }

    public void read( JSONArrayConverter jsonArrayConverter ) throws IOException, JSONException, ConversionException
    {
        jsonArrayConverter.setJsonArray( JSONArrayIOHandler.loadItems( filePath ) );
        jsonArrayConverter.from();
    }

    public void write( JSONArrayConverter jsonArrayConverter ) throws IOException, JSONException, ConversionException
    {
        jsonArrayConverter.to();
        JSONArrayIOHandler.saveItems( this.context, jsonArrayConverter.getJsonArray(), filePath );
    }

}
