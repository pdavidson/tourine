package us.pdavidson.tourine;

import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.google.common.base.Throwables;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Converts a {@link com.codahale.metrics.Timer} to a Json object for consumption by the Hystrix dashboard.
 */
class TourineTimerHystrixCommandJsonSupplier extends AbstractTimerJsonSupplier {
    public TourineTimerHystrixCommandJsonSupplier(String name, Timer timer, JsonFactory jsonFactory, Double durationFactor, Double rateFactor) {
        super(name, timer, jsonFactory, durationFactor, rateFactor);
    }

    protected TourineTimerHystrixCommandJsonSupplier(String name, Timer timer, Snapshot snapshot, JsonFactory jsonFactory, Double durationFactor, Double rateFactor) {
        super(name, timer, snapshot, jsonFactory, durationFactor, rateFactor);
    }

    @Override
    public String get() {
        try {
            StringWriter jsonString = new StringWriter();
            JsonGenerator json = jsonFactory.createGenerator(jsonString);

            json.writeStartObject();
            json.writeStringField("type", "HystrixCommand");
            json.writeStringField("name", getName(name));
            json.writeStringField("group", name);
            json.writeNumberField("currentTime", System.currentTimeMillis());

            json.writeBooleanField("isCircuitBreakerOpen", false);


            json.writeNumberField("errorPercentage", 0);
            json.writeNumberField("errorCount", 0);
            json.writeNumberField("requestCount", timer.getMeanRate());

            // rolling counters
            json.writeNumberField("rollingCountCollapsedRequests", 0);
            json.writeNumberField("rollingCountExceptionsThrown", 0);
            json.writeNumberField("rollingCountFailure", 0);
            json.writeNumberField("rollingCountFallbackFailure", 0);
            json.writeNumberField("rollingCountFallbackRejection", 0);
            json.writeNumberField("rollingCountFallbackSuccess", 0);
            json.writeNumberField("rollingCountResponsesFromCache", 0);
            json.writeNumberField("rollingCountSemaphoreRejected", 0);
            json.writeNumberField("rollingCountShortCircuited", 0);
            json.writeNumberField("rollingCountSuccess", (int) (convertRate(timer.getMeanRate()) * 10.0) ); //we'll make this a 10 second window
            json.writeNumberField("rollingCountThreadPoolRejected", 0);
            json.writeNumberField("rollingCountTimeout", 0);

            json.writeNumberField("currentConcurrentExecutionCount", timer.getCount());

            // latency percentiles
            json.writeNumberField("latencyExecute_mean", convertDuration(snapshot.getMean()));
            json.writeObjectFieldStart("latencyExecute");
            json.writeNumberField("0", convertDuration(snapshot.getMin()));
            json.writeNumberField("25", convertDuration(snapshot.getValue(.25)));
            json.writeNumberField("50", convertDuration(snapshot.getMedian()));
            json.writeNumberField("75", convertDuration(snapshot.get75thPercentile()));
            json.writeNumberField("90", convertDuration(snapshot.getValue(.900)));
            json.writeNumberField("95", convertDuration(snapshot.get95thPercentile()));
            json.writeNumberField("99", convertDuration(snapshot.get99thPercentile()));
            json.writeNumberField("99.5", convertDuration(snapshot.getValue(.995)));
            json.writeNumberField("100", convertDuration(snapshot.getMax()));
            json.writeEndObject();
            //
            json.writeNumberField("latencyTotal_mean", 0);
            json.writeObjectFieldStart("latencyTotal");
            json.writeNumberField("0", 0);
            json.writeNumberField("25", 0);
            json.writeNumberField("50", 0);
            json.writeNumberField("75", 0);
            json.writeNumberField("90", 0);
            json.writeNumberField("95", 0);
            json.writeNumberField("99", 0);
            json.writeNumberField("99.5", 0);
            json.writeNumberField("100", 0);
            json.writeEndObject();

            // property values for reporting what is actually seen by the command rather than what was set somewhere

            json.writeNumberField("propertyValue_circuitBreakerRequestVolumeThreshold", 0);
            json.writeNumberField("propertyValue_circuitBreakerSleepWindowInMilliseconds", 0);
            json.writeNumberField("propertyValue_circuitBreakerErrorThresholdPercentage", 0);
            json.writeBooleanField("propertyValue_circuitBreakerForceOpen", false);
            json.writeBooleanField("propertyValue_circuitBreakerForceClosed", false);
            json.writeBooleanField("propertyValue_circuitBreakerEnabled", false);

            json.writeStringField("propertyValue_executionIsolationStrategy", "Semaphore");
            json.writeNumberField("propertyValue_executionIsolationThreadTimeoutInMilliseconds", 0);
            json.writeBooleanField("propertyValue_executionIsolationThreadInterruptOnTimeout", false);
            json.writeStringField("propertyValue_executionIsolationThreadPoolKeyOverride", "?");
            json.writeNumberField("propertyValue_executionIsolationSemaphoreMaxConcurrentRequests", 0);
            json.writeNumberField("propertyValue_fallbackIsolationSemaphoreMaxConcurrentRequests", 0);

                    /*
                     * The following are commented out as these rarely change and are verbose for streaming for something people don't change.
                     * We could perhaps allow a property or request argument to include these.
                     */

            //                    json.put("propertyValue_metricsRollingPercentileEnabled", commandProperties.metricsRollingPercentileEnabled().get());
            //                    json.put("propertyValue_metricsRollingPercentileBucketSize", commandProperties.metricsRollingPercentileBucketSize().get());
            //                    json.put("propertyValue_metricsRollingPercentileWindow", commandProperties.metricsRollingPercentileWindowInMilliseconds().get());
            //                    json.put("propertyValue_metricsRollingPercentileWindowBuckets", commandProperties.metricsRollingPercentileWindowBuckets().get());
            //                    json.put("propertyValue_metricsRollingStatisticalWindowBuckets", commandProperties.metricsRollingStatisticalWindowBuckets().get());
            json.writeNumberField("propertyValue_metricsRollingStatisticalWindowInMilliseconds", 1000);

            json.writeBooleanField("propertyValue_requestCacheEnabled", false);
            json.writeBooleanField("propertyValue_requestLogEnabled", true);

            json.writeNumberField("reportingHosts", 1); // this will get summed across all instances in a cluster

            json.writeEndObject();
            json.close();

            return jsonString.getBuffer().toString();
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

}
