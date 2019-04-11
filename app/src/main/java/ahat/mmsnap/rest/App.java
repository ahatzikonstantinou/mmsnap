package ahat.mmsnap.rest;

import android.app.Application;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application
{
    private String jwtToken;

    public String getJwtToken()
    {
        return jwtToken;
    }

    public void setJwtToken( String jwtToken )
    {
        this.jwtToken = jwtToken;
    }
}