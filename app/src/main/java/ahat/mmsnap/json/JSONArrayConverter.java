package ahat.mmsnap.json;

import org.json.JSONArray;

import ahat.mmsnap.models.Converter;

public abstract class JSONArrayConverter implements Converter
{
    protected JSONArray jsonArray;
    public JSONArray getJsonArray()
    {
        return jsonArray;
    }
    public void setJsonArray( JSONArray jsonArray )
    {
        this.jsonArray = jsonArray;
    }

    protected boolean converted;
    @Override
    public boolean isConverted()
    {
        return converted;
    }

    public JSONArrayConverter()
    {
        converted = false;
        jsonArray = new JSONArray();
    }

    public JSONArrayConverter( JSONArray jsonArray )
    {
        converted = false;
        this.jsonArray = jsonArray;
    }

}
