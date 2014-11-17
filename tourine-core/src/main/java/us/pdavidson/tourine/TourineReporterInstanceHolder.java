package us.pdavidson.tourine;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;


/**
 * Class holds instances of {@link us.pdavidson.tourine.TourineReporter} by key, so that the observers can be consumed
 * by other modules.
 */
public class TourineReporterInstanceHolder {
    private static Map<String, TourineReporter> instanceMap = Maps.newHashMap();

    /**
     * Added the TourineReporter Entry to the Backing Map
     *
     * @param key
     * @param tourineReporter
     * @throws IllegalStateException Thrown when the Entry is already set
     */
    public static synchronized void set(String key, TourineReporter tourineReporter) throws IllegalStateException{
        if(instanceMap.containsKey(key)){
            throw new IllegalStateException("TourineReporter already set");
        }
        instanceMap.put(key, tourineReporter);
    }

    /**
     * Returns the TourineReporter Entry from the bmap
     *
     * @param key
     * @return
     * @throws IllegalStateException Thrown when the entry is not present in the backing map
     */
    public static TourineReporter get(String key) throws IllegalStateException{
        TourineReporter instance = instanceMap.get(key);
        if (instance == null){
            throw new IllegalStateException("TourineReporter must be set prior to Calling Get");
        }
        return instance;
    }

    /**
     * Removes the entry from the backing map
     *
     * @param key
     */
    public static synchronized void clear(String key){
        instanceMap.remove(key);
    }

    /**
     * Returns if backing map contains the entry
     *
     * @param key
     * @return
     */
    public static boolean contains(String key){
        return instanceMap.containsKey(key);
    }


    @VisibleForTesting
    protected static Set<String> keySet(){
        return Sets.newHashSet(instanceMap.keySet()) ;
    }

}
