package ahat.mmsnap.json;

import android.content.Context;

public class CopingPlansStorage extends JSONStorage
{
    public static final String FILENAME = "coping_plans.json";

    public CopingPlansStorage( Context context )
    {
        super( context, context.getFilesDir().getPath() + "/" + FILENAME );
    }

}
