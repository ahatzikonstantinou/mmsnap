package ahat.mmsnap.JSON;

import android.content.Context;

import ahat.mmsnap.JSON.JSONStorage;

public class WeeklyEvaluationsStorage extends JSONStorage
{
    public static final String FILENAME = "weekly_evaluations.json";

    public WeeklyEvaluationsStorage( Context context )
    {
        super( context, context.getFilesDir().getPath() + "/" + FILENAME );
    }
}
