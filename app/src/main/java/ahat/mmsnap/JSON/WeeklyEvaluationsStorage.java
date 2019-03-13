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

//    public ArrayList<WeeklyEvaluation> read() throws IOException, JSONException
//    {
//        ArrayList<WeeklyEvaluation> evaluations = new ArrayList<>();
//
//        JSONArray jsonEvaluations = JSONArrayIOHandler.loadItems( filePath );
//
//        for( int i = 0 ; i < jsonEvaluations.length() ; i++ )
//        {
//            JSONObject je = (JSONObject) jsonEvaluations.get( i );
//            evaluations.add(
//                new WeeklyEvaluation(
//                    je.getInt( "year" ),
//                    je.getInt( "week" ),
//                    je.getBoolean( "target_diet" ),
//                    je.getBoolean( "target_smoking" ),
//                    je.getBoolean( "target_physical_activity" ),
//                    je.getBoolean( "target_alcohol" ),
//                    je.getInt( "diet" ),
//                    je.getInt( "smoking" ),
//                    je.getInt( "physical_activity" ),
//                    je.getInt( "alcohol" )
//                )
//            );
//        }
//        return evaluations;
//    }
//
//    public void write( ArrayList<WeeklyEvaluation> evaluations ) throws IOException, JSONException
//    {
//        JSONArray jsonEvaluations = new JSONArray();
//
//        for( int i = 0 ; i < evaluations.size() ; i++ )
//        {
//            JSONObject je = new JSONObject();
//
//            je.put( "year", evaluations.get( i ).getYear() );
//            je.put( "week", evaluations.get( i ).getWeekOfYear() );
//            je.put( "target_diet", evaluations.get( i ).targetDiet );
//            je.put( "target_smoking", evaluations.get( i ).targetSmoking );
//            je.put( "target_physical_activity", evaluations.get( i ).targetPhysicalActivity );
//            je.put( "target_alcohol", evaluations.get( i ).targetAlcohol );
//            je.put( "diet", evaluations.get( i ).getDiet() );
//            je.put( "smoking", evaluations.get( i ).getSmoking() );
//            je.put( "physical_activity", evaluations.get( i ).getPhysicalActivity() );
//            je.put( "alcohol", evaluations.get( i ).getAlcohol() );
//
//            jsonEvaluations.put( je );
//        }
//
//        JSONArrayIOHandler.saveItems( this.context, jsonEvaluations, filePath );
//    }
//
//    public void read( JSONArrayConverter jsonArrayConverter ) throws IOException, JSONException, ConversionException
//    {
//
//        jsonArrayConverter.setJsonArray( JSONArrayIOHandler.loadItems( filePath ) );
//        jsonArrayConverter.from();
//    }
//
//    public void write( JSONArrayConverter jsonArrayConverter ) throws IOException, ConversionException
//    {
//        jsonArrayConverter.to();
//        JSONArrayIOHandler.saveItems( this.context, jsonArrayConverter.getJsonArray(), filePath );
//    }
}
