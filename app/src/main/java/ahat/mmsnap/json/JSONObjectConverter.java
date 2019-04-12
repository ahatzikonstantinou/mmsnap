package ahat.mmsnap.json;

import org.json.JSONObject;

import ahat.mmsnap.models.Converter;

public abstract class JSONObjectConverter implements Converter
{
    protected JSONObject jsonObject;
    public JSONObject getJsonObject()
    {
        return jsonObject;
    }
    public void setJsonObject( JSONObject jsonObject )
    {
        this.jsonObject = jsonObject;
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
