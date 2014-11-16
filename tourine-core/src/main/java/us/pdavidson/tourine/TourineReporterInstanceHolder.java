package us.pdavidson.tourine;

public class TourineReporterInstanceHolder {
    private static TourineReporter instance;
    public static synchronized void set(TourineReporter tourineReporter){
        if(instance != null){
            throw new IllegalStateException("TourineReporter already set");
        }
        instance = tourineReporter;
    }

    public static TourineReporter get() throws IllegalStateException{
        if (instance == null){
            throw new IllegalStateException("TourineReporter must be set prior to Calling Get");
        }
        return instance;
    }

    public static synchronized void clear(){
        instance = null;
    }

}
