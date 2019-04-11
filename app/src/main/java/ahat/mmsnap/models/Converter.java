package ahat.mmsnap.models;

public interface Converter
{
    boolean isConverted();

    void from() throws ConversionException;

    void to() throws ConversionException;
}
