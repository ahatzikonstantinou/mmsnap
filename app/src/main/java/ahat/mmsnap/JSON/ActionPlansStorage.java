package ahat.mmsnap.JSON;

import android.content.Context;

public class ActionPlansStorage extends JSONStorage
{
    public static final String FILENAME = "action_plans.json";

    public ActionPlansStorage( Context context )
    {
        super( context, context.getFilesDir().getPath() + "/" + FILENAME );
    }
}
