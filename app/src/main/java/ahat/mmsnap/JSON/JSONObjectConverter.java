package ahat.mmsnap.JSON;

import org.json.JSONObject;

import ahat.mmsnap.Models.Converter;

public abstract class JSONObjectConverter implements Converter
{
    protected JSONObject jsonObject;
    public JSONObject getJsonObject()
    {
        return jsonObject;
    }
    protected boolean converted;
    @Override
    public boolean isConverted()
    {
        return converted;
    }

    public JSONObjectConverter()
    {
        converted = false;
        jsonObject = new JSONObject();
    }

    public JSONObjectConverter( JSONObject jsonObject )
    {
        converted = false;
        this.jsonObject = jsonObject;
    }

}
