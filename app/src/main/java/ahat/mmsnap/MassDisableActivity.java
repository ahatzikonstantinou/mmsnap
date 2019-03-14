package ahat.mmsnap;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;

public abstract class MassDisableActivity extends AssessmentSubSectionActivity //AppCompatActivity
{
    protected void enableControls(boolean enable, ViewGroup vg)
    {
        for( int i = 0; i < vg.getChildCount(); i++ )
        {
            View child = vg.getChildAt( i );
            if( child instanceof CheckBox || child instanceof RadioButton )
            {
                child.setEnabled(enable);
                ( (Button) child ).setTextColor( getResources().getColor( android.R.color.primary_text_light, null ) ); //maintain dark color instead of grey for checkboxes and radiobuttons
            }
            if( child instanceof ViewGroup )
            {
                enableControls( enable, (ViewGroup) child );
            }
        }
    }

    protected void disableAllControls()
    {
        enableControls( false, (ViewGroup) findViewById( android.R.id.content ) );
    }
}
