package ahat.mmsnap.json;

import android.content.Context;

public class DailyEvaluationsStorage extends JSONStorage
{
    public static final String FILENAME = "daily_evaluations.json";

    public DailyEvaluationsStorage( Context context )
    {
        super( context, context.getFilesDir().getPath() + "/" + FILENAME );
    }
}
