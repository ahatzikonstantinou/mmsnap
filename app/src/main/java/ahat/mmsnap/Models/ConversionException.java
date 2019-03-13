package ahat.mmsnap.Models;

public class ConversionException extends Exception
{
    private Exception inner;

    public ConversionException( Exception e )
    {
        inner = e;
    }

    public Exception getInner()
    {
        return inner;
    }
}
