package ahat.mmsnap.json;

import java.util.Calendar;
import java.util.Date;

public class Util
{
    public static String toJsonString( Date date )
    {
        if( null == date )
        {
            return "";
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime( date );
        return cal.get( Calendar.YEAR ) + "-" + cal.get( Calendar.MONTH ) + "-" + cal.get( Calendar.DAY_OF_MONTH ) +
               " " + cal.get( Calendar.HOUR_OF_DAY ) + ":" + cal.get( Calendar.MINUTE ) + ":" + cal.get( Calendar.SECOND );
    }

    public static Date dateFromJsonString( String date )
    {
        if( 0 == date.length() )
        {
            return null;
        }

        String[] parts = date.split( " " );
        String[] dateParts =  parts[0].split( "-" );
        String[] timeParts =  parts[1].split( ":" );

        final Calendar cal = Calendar.getInstance();
        cal.set( Calendar.YEAR, Integer.parseInt( dateParts[0] ) );
        cal.set( Calendar.MONTH, Integer.parseInt( dateParts[1] ) );
        cal.set( Calendar.DAY_OF_MONTH, Integer.parseInt( dateParts[2] ) );
        cal.set( Calendar.HOUR_OF_DAY, Integer.parseInt( timeParts[0] ) );
        cal.set( Calendar.MINUTE, Integer.parseInt( timeParts[1] ) );
        cal.set( Calendar.SECOND, Integer.parseInt( timeParts[2] ) );
        cal.set( Calendar.MILLISECOND, 0 );
        return cal.getTime();
    }
}
