package ahat.mmsnap.json;

import android.content.Context;

public class WeeklyEvaluationsStorage extends JSONStorage
{
    public static final String FILENAME = "weekly_evaluations.json";

    public WeeklyEvaluationsStorage( Context context )
    {
        super( context, context.getFilesDir().getPath() + "/" + FILENAME );
    }
}
