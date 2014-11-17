package us.pdavidson.tourine;

import com.codahale.metrics.Timer;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.google.common.base.Throwables;

import java.io.IOException;
import java.io.StringWriter;

 class TourineTimerJsonSupplier extends AbstractTimerJsonSupplier {

    public TourineTimerJsonSupplier(String name, Timer timer, JsonFactory jsonFactory, Double durationFactor, Double rateFactor) {
        super(name, timer, jsonFactory, durationFactor, rateFactor);
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
}
