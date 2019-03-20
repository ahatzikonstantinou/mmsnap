package ahat.mmsnap.Models;

import java.io.Serializable;
import java.util.Comparator;

public class Reminder implements Serializable
{
    public static final Comparator<Reminder> comparator = new Comparator<Reminder>()
    {
        @Override
        public int compare( Reminder r1, Reminder r2 )
        {
            return ( r1.hour - r2.hour )*60 + ( r1.minute - r2.minute );
        }
    };

    public int hour;
    public int minute;
    public int id;

    public Reminder( int hour, int minute )
    {
        this.id = -1;
        this.hour = hour;
        this.minute = minute;
    }

    public Reminder( int id, int hour, int minute )
    {
        this.id = id;
        this.hour = hour;
        this.minute = minute;
    }

    public boolean equals( Reminder reminder )
    {
        return hour == reminder.hour && minute == reminder.minute;
    }
}
