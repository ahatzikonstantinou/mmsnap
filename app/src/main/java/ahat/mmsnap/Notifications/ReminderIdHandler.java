package ahat.mmsnap.Notifications;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/*
 * In order to show notifications (reminders) for plans, an alarm needs to be fired at the notification specified time. The alarm event carries
 * a PendingIntent which is required if the alarm is to be cancelled in the future.
 * In order to identify the PendingIntent of one notification from another's the requestCode used when creating the PendingIntent
 * needs to be unique. This requestCode is copied from the Notification id which is handled by this class
 */
public class ReminderIdHandler
{
    private static final String FILENAME = "notification_ids.txt";
    private static ArrayList<Integer> ids = new ArrayList<>();

    private static ReminderIdHandler instance = null;

    // synchronized is necessary for thread safety
    public static synchronized ReminderIdHandler getInstance()
    {
        if( null == instance )
        {
            instance = new ReminderIdHandler();
            read();
        }

        return instance;
    }

    public int getNewId()
    {
        int i;
        do
        {
            i = new Random().nextInt( Integer.MAX_VALUE );
        }while( ids.contains( i ) );

        ids.add( i );
        write();
        return i;
    }

    public void removeId( int id )
    {
        if( ids.contains( id ) )
        {
            ids.remove( (Integer) id );
            write();
        }
    }

    private static void write()
    {
        try
        {
            DataOutputStream dos = new DataOutputStream( new BufferedOutputStream( new FileOutputStream( FILENAME ) ) );

            for( int i = 0 ; i < ids.size() ; i++ )
            {
                dos.writeInt( ids.get( i ) );
            }
            dos.close();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }

    }

    private static void read()
    {
        ids = new ArrayList<>();
        try
        {
            DataInputStream dis = new DataInputStream( new FileInputStream( FILENAME ) );
            try
            {
                while( true )
                {
                    ids.add( dis.readInt() );
                }
            }
            catch( EOFException e )
            {
                //this is how we can tell DataInputStream reached the end of file
                dis.close();
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }

    }
}
