package us.pdavidson.tourine;

import com.codahale.metrics.*;
import com.fasterxml.jackson.core.JsonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.subjects.PublishSubject;

import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

public class TourineReporter extends ScheduledReporter {
    private static final Logger log = LoggerFactory.getLogger(TourineReporter.class);
    private final PublishSubject<String> timerSubject = PublishSubject.create();
    private final JsonFactory jsonFactory = new JsonFactory();
    private final Class<? extends AbstractTimerJsonSupplier> timerSupplierClass;
    /**
     * Static Helper similar to ConsoleReporter
     * @param registry
     * @return
     */
    public static TourineReporterBuilder forRegistry(MetricRegistry registry){
        return new TourineReporterBuilder()
                .setRegistry(registry);
    }

    protected TourineReporter(MetricRegistry registry, String name, MetricFilter filter, TimeUnit rateUnit, TimeUnit durationUnit, TourineJsonFormat jsonType) {
        super(registry, name, filter, rateUnit, durationUnit);

        if (jsonType == TourineJsonFormat.HYSTRIX){
            timerSupplierClass = TourineTimerHystrixCommandJsonSupplier.class;
        } else{
            timerSupplierClass = TourineTimerJsonSupplier.class;
        }


    }

    @Override
    public void report(SortedMap<String, Gauge> gauges, SortedMap<String, Counter> counters, SortedMap<String, Histogram> histograms, SortedMap<String, Meter> meters, SortedMap<String, Timer> timers) {
        if (!timers.isEmpty()) {
            emitTimers(timers);
        }
    }

    protected void emitTimers(SortedMap<String, Timer> timers){
        for (Map.Entry<String, Timer> timerEntry: timers.entrySet()){
            try {
                AbstractTimerJsonSupplier supplier = timerSupplierClass.getConstructor(String.class, Timer.class, JsonFactory.class).newInstance(timerEntry.getKey(), timerEntry.getValue(), jsonFactory);
                String timerJson = supplier.get();
                log.debug("Emitting JSON {}", timerJson);
                timerSubject.onNext(timerJson);
            } catch (Exception e) {
                log.error("Unable to Emit to the Subject for Timer {}", timerEntry.getKey(), e);
            }
        }
    }

    /**
     * Returns an Observable that Emits Timer Metric Events
     *
     * @return TourineTimer Json Object
     */
    public Observable<String> getTimerObservable(){
        return timerSubject.asObservable();
    }

}
