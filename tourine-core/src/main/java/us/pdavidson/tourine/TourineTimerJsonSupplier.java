package us.pdavidson.tourine;

import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;

import java.io.IOException;
import java.io.StringWriter;

public class TourineTimerJsonSupplier implements Supplier<String> {
    private final String name;
    private final Timer timer;
    private final Snapshot snapshot;
    private final JsonFactory jsonFactory;


    public TourineTimerJsonSupplier(String name, Timer timer, JsonFactory jsonFactory) {
        this(name, timer, timer.getSnapshot(), jsonFactory);
    }

    public TourineTimerJsonSupplier(String name, Timer timer, Snapshot snapshot, JsonFactory jsonFactory) {
        this.name = name;
        this.timer = timer;
        this.snapshot = snapshot;
        this.jsonFactory = jsonFactory;

    }

    @Override
    public String get() {
        StringWriter jsonString = new StringWriter();
        try {
            JsonGenerator json = jsonFactory.createGenerator(jsonString);

            json.writeStartObject();
            json.writeStringField("type", "Timer");
            json.writeStringField("name", name);
            json.writeNumberField("currentTime", System.currentTimeMillis());

            json.writeNumberField("count", timer.getCount());

            json.writeObjectFieldStart("rate");
            json.writeNumberField("meanRate", convertRate(timer.getMeanRate()));
            json.writeNumberField("1minRate", convertRate(timer.getOneMinuteRate()));
            json.writeNumberField("5minRate", convertRate(timer.getFiveMinuteRate()));
            json.writeNumberField("15minRate", convertRate(timer.getFifteenMinuteRate()));
            json.writeEndObject();

            json.writeObjectFieldStart("latency");
            json.writeNumberField("min", convertDuration(snapshot.getMin()));
            json.writeNumberField("max", convertDuration(snapshot.getMax()));
            json.writeNumberField("mean", convertDuration(snapshot.getMean()));
            json.writeNumberField("stddev", convertDuration(snapshot.getStdDev()));
            json.writeNumberField("median", convertDuration(snapshot.getMedian()));
            json.writeNumberField("75", convertDuration(snapshot.get75thPercentile()));
            json.writeNumberField("95", convertDuration(snapshot.get95thPercentile()));
            json.writeNumberField("98", convertDuration(snapshot.get98thPercentile()));
            json.writeNumberField("99", convertDuration(snapshot.get99thPercentile()));
            json.writeNumberField("99.9", convertDuration(snapshot.get999thPercentile()));
            json.writeEndObject();

            json.writeNumberField("reportingHosts", 1);

            json.writeEndObject();

            json.close();

            return jsonString.getBuffer().toString();
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }

    }

    private double convertRate(double rate) {
        return rate;
    }

    private double convertDuration(double duration){
        return duration;
    }

    /*
       final Snapshot snapshot = timer.getSnapshot();
        output.printf(locale, "             count = %d%n", timer.getCount());
        output.printf(locale, "         mean rate = %2.2f calls/%s%n", convertRate(timer.getMeanRate()), getRateUnit());
        output.printf(locale, "     1-minute rate = %2.2f calls/%s%n", convertRate(timer.getOneMinuteRate()), getRateUnit());
        output.printf(locale, "     5-minute rate = %2.2f calls/%s%n", convertRate(timer.getFiveMinuteRate()), getRateUnit());
        output.printf(locale, "    15-minute rate = %2.2f calls/%s%n", convertRate(timer.getFifteenMinuteRate()), getRateUnit());

        output.printf(locale, "               min = %2.2f %s%n", convertDuration(snapshot.getMin()), getDurationUnit());
        output.printf(locale, "               max = %2.2f %s%n", convertDuration(snapshot.getMax()), getDurationUnit());
        output.printf(locale, "              mean = %2.2f %s%n", convertDuration(snapshot.getMean()), getDurationUnit());
        output.printf(locale, "            stddev = %2.2f %s%n", convertDuration(snapshot.getStdDev()), getDurationUnit());
        output.printf(locale, "            median = %2.2f %s%n", convertDuration(snapshot.getMedian()), getDurationUnit());
        output.printf(locale, "              75%% <= %2.2f %s%n", convertDuration(snapshot.get75thPercentile()), getDurationUnit());
        output.printf(locale, "              95%% <= %2.2f %s%n", convertDuration(snapshot.get95thPercentile()), getDurationUnit());
        output.printf(locale, "              98%% <= %2.2f %s%n", convertDuration(snapshot.get98thPercentile()), getDurationUnit());
        output.printf(locale, "              99%% <= %2.2f %s%n", convertDuration(snapshot.get99thPercentile()), getDurationUnit());
        output.printf(locale, "            99.9%% <= %2.2f %s%n", convertDuration(snapshot.get999thPercentile()), getDurationUnit());
     */
}
