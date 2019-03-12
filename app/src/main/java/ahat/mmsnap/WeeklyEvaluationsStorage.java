package ahat.mmsnap;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

public class WeeklyEvaluationsStorage
{
    private static final String FILENAME = "weekly_evaluations.json";
    private Context context;
    private String filePath;

    public WeeklyEvaluationsStorage( Context context )
    {
        this.context = context;
        filePath = context.getFilesDir().getPath() + "/" + FILENAME;
    }

    public ArrayList<WeeklyEvaluation> read() throws IOException, JSONException
    {
        ArrayList<WeeklyEvaluation> evaluations = new ArrayList<>();

        JSONArray jsonEvaluations = JSONArrayIOHandler.loadItems( filePath );

        for( int i = 0 ; i < jsonEvaluations.length() ; i++ )
        {
            JSONObject je = (JSONObject) jsonEvaluations.get( i );
            evaluations.add(
                new WeeklyEvaluation(
                    je.getInt( "year" ),
                    je.getInt( "week" ),
                    je.getBoolean( "target_diet" ),
                    je.getBoolean( "target_smoking" ),
                    je.getBoolean( "target_physical_activity" ),
                    je.getBoolean( "target_alcohol" ),
                    je.getInt( "diet" ),
                    je.getInt( "smoking" ),
                    je.getInt( "physical_activity" ),
                    je.getInt( "alcohol" )
                )
            );
        }
        return evaluations;
    }

    public void write( ArrayList<WeeklyEvaluation> evaluations ) throws IOException, JSONException
    {
        JSONArray jsonEvaluations = new JSONArray();

        for( int i = 0 ; i < evaluations.size() ; i++ )
        {
            JSONObject je = new JSONObject();
            je.put( "year", evaluations.get( i ).getYear() );
            je.put( "week", evaluations.get( i ).getWeekOfYear() );
            je.put( "target_diet", evaluations.get( i ).targetDiet );
            je.put( "target_smoking", evaluations.get( i ).targetSmoking );
            je.put( "target_physical_activity", evaluations.get( i ).targetPhysicalActivity );
            je.put( "target_alcohol", evaluations.get( i ).targetAlcohol );
            je.put( "diet", evaluations.get( i ).getDiet() );
            je.put( "smoking", evaluations.get( i ).getSmoking() );
            je.put( "physical_activity", evaluations.get( i ).getPhysicalActivity() );
            je.put( "alcohol", evaluations.get( i ).getAlcohol() );

            jsonEvaluations.put( je );
        }

        JSONArrayIOHandler.saveItems( this.context, jsonEvaluations, filePath );
    }
}
