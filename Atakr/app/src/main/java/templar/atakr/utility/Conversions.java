package templar.atakr.utility;

/**
 * Created by Devin on 3/23/2017.
 */

public class Conversions {

    public static long getNegViews(long viewsPositive){
        return viewsPositive * -1;
    }

    public static long getPosViews(long viewsNegative){
        return viewsNegative * -1;
    }

    public static long getNegTimeUploaded(long timeUploadedPositive){
        return timeUploadedPositive * -1;
    }

    public static long getPosTimeUploaded(long timeUploadedNegative){
        return timeUploadedNegative * -1;
    }
}
