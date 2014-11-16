package us.pdavidson.tourine;

import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;
import com.fasterxml.jackson.core.JsonFactory;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;

import java.util.concurrent.TimeUnit;

public abstract class AbstractTimerJsonSupplier implements Supplier<String> {
    protected final String name;
    protected final Timer timer;
    protected final Snapshot snapshot;
    protected final JsonFactory jsonFactory;
    private static final double durationFactor = 1.0 / TimeUnit.MILLISECONDS.toNanos(1);
    private static final double rateFactor = TimeUnit.SECONDS.toSeconds(1);

    public AbstractTimerJsonSupplier(String name, Timer timer, JsonFactory jsonFactory) {
        this(name, timer, timer.getSnapshot(), jsonFactory);
    }

    protected AbstractTimerJsonSupplier(String name, Timer timer, Snapshot snapshot, JsonFactory jsonFactory) {
        this.name = name;
        this.timer = timer;
        this.snapshot = snapshot;
        this.jsonFactory = jsonFactory;

    }

    protected double convertRate(double rate) {
        return rate * rateFactor;
    }

    protected double convertDuration(double duration){
        return durationFactor * duration;
    }

    protected String getName(String name) {
        Iterable<String> strings = Splitter.on('.').split(name);
        return String.format("%s-%s", "Metrics", Iterables.getLast(strings, name));
    }
}
