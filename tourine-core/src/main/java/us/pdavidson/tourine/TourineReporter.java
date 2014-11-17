package us.pdavidson.tourine;

import com.codahale.metrics.*;
import com.fasterxml.jackson.core.JsonFactory;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.subjects.PublishSubject;

import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

/**
 * Class is a Metrics {@link com.codahale.metrics.ScheduledReporter} exposes reported metrics as Observables.
 *
 * As metrics are reported, they are serialized to configured format and emitted as json.
 *
 * Currently the only Metrics that are reported by Tourine are Timers.
 *
 */
public class TourineReporter extends ScheduledReporter {
    private static final Logger log = LoggerFactory.getLogger(TourineReporter.class);
    private final PublishSubject<String> timerSubject = PublishSubject.create();
    private final JsonFactory jsonFactory = new JsonFactory();
    private final double durationFactor;
    private final double rateFactor;
    private final TourineJsonFormat jsonType;

    /**
     * Static Builder similar to ConsoleReporter
     * @param registry
     * @return
     */
    public static TourineReporterBuilder forRegistry(MetricRegistry registry){
        return new TourineReporterBuilder()
                .withRegistry(registry);
    }

    protected TourineReporter(MetricRegistry registry, MetricFilter filter, TimeUnit rateUnit,
                              TimeUnit durationUnit, TourineJsonFormat jsonType) {

        super(Preconditions.checkNotNull(registry, "Registry Cannot Be Null"),
                "tourine-reporter",
                Preconditions.checkNotNull(filter, "Filter Cannot be Null"),
                Preconditions.checkNotNull(rateUnit, "RateUnit Cannot Be Null"),
                Preconditions.checkNotNull(durationUnit, "DurationUnit Cannot Be Null"));

        this.jsonType = Preconditions.checkNotNull(jsonType, "JsonType Cannot Be Null");

        durationFactor = 1.0 / durationUnit.toNanos(1);
        rateFactor = rateUnit.toSeconds(1);

    }

    @Override
    public void report(SortedMap<String, Gauge> gauges, SortedMap<String, Counter> counters, SortedMap<String, Histogram> histograms, SortedMap<String, Meter> meters, SortedMap<String, Timer> timers) {
        if (timers != null && !timers.isEmpty()) {
            emitTimers(timers);
        }
    }

    protected void emitTimers(SortedMap<String, Timer> timers){
        for (Map.Entry<String, Timer> timerEntry: timers.entrySet()){
            try {
                String timerJson = getSupplier(timerEntry).get();
                log.debug("Emitting JSON {}", timerJson);
                timerSubject.onNext(timerJson);
            } catch (Exception e) {
                log.error("Unable to Emit to the Subject for Timer {}", timerEntry.getKey(), e);
            }
        }
    }

    protected AbstractTimerJsonSupplier getSupplier(Map.Entry<String, Timer> timerEntry) {
        AbstractTimerJsonSupplier supplier;
        if (jsonType == TourineJsonFormat.HYSTRIX){
            supplier = new TourineTimerHystrixCommandJsonSupplier(timerEntry.getKey(), timerEntry.getValue(), jsonFactory, durationFactor, rateFactor);
        } else{
            supplier = new TourineTimerJsonSupplier(timerEntry.getKey(), timerEntry.getValue(), jsonFactory, durationFactor, rateFactor);
        }
        return supplier;
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
