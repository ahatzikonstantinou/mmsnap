package ahat.mmsnap;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

import org.json.JSONException;

import java.io.IOException;

import ahat.mmsnap.models.ConversionException;

public class LoginActivity extends ResetPasswordActivity
{

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        ( (Button) findViewById( R.id.reset_password_button ) ).setText( R.string.action_sign_in );
    }

    @Override
    protected void setActionBar()
    {
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
    }


    @Override
    protected void onBeforeResetPasswordFinish( ApplicationStatus applicationStatus ) throws IOException, JSONException, ConversionException
    {
        applicationStatus.userLoggedIn();
    }

}

