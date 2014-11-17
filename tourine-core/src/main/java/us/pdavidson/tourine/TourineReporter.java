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
    private final Class<? extends AbstractTimerJsonSupplier> timerSupplierClass;
    private final double durationFactor;
    private final double rateFactor;

    /**
     * Static Builder similar to ConsoleReporter
     * @param registry
     * @return
     */
    public static TourineReporterBuilder forRegistry(MetricRegistry registry){
        return new TourineReporterBuilder()
                .setRegistry(registry);
    }

    protected TourineReporter(MetricRegistry registry, String name, MetricFilter filter, TimeUnit rateUnit,
                              TimeUnit durationUnit, TourineJsonFormat jsonType) {

        super(Preconditions.checkNotNull(registry), Preconditions.checkNotNull(name),
                Preconditions.checkNotNull(filter), Preconditions.checkNotNull(rateUnit), Preconditions.checkNotNull(durationUnit));

        Preconditions.checkNotNull(jsonType);

        if (jsonType == TourineJsonFormat.HYSTRIX){
            timerSupplierClass = TourineTimerHystrixCommandJsonSupplier.class;
        } else{
            timerSupplierClass = TourineTimerJsonSupplier.class;
        }

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
                AbstractTimerJsonSupplier supplier = timerSupplierClass.getConstructor(String.class, Timer.class, JsonFactory.class, Double.class, Double.class).newInstance(timerEntry.getKey(), timerEntry.getValue(), jsonFactory, durationFactor, rateFactor);
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
