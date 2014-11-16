package us.pdavidson.tourine;

import com.google.common.collect.Maps;

import java.util.Map;

public class TourineReporterInstanceHolder {
    private static Map<String, TourineReporter> instanceMap = Maps.newHashMap();

    public static synchronized void set(String key, TourineReporter tourineReporter) throws IllegalStateException{
        if(instanceMap.containsKey(key)){
            throw new IllegalStateException("TourineReporter already set");
        }
        instanceMap.put(key, tourineReporter);
    }

    public static TourineReporter get(String key) throws IllegalStateException{
        TourineReporter instance = instanceMap.get(key);
        if (instance == null){
            throw new IllegalStateException("TourineReporter must be set prior to Calling Get");
        }
        return instance;
    }

    public static synchronized void clear(String key){
        instanceMap.remove(key);
    }

}
