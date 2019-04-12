package ahat.mmsnap.rest;

import android.app.Application;

public class App extends Application
{
    private static String jwtToken;

    public static String getJwtToken()
    {
        return jwtToken;
    }

    public static void setJwtToken( String _jwtToken )
    {
        jwtToken = _jwtToken;
    }
}