package us.pdavidson.tourine;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.google.common.base.MoreObjects;

import java.util.concurrent.TimeUnit;

public class TourineReporterBuilder {
    private MetricRegistry registry;
    private MetricFilter filter;
    private TimeUnit rateUnit;
    private TimeUnit durationUnit;
    private TourineJsonFormat tourineJsonFormat;

    protected TourineReporterBuilder() {};

    protected TourineReporterBuilder withRegistry(MetricRegistry registry) {
        this.registry = registry;
        return this;
    }

    public TourineReporterBuilder withFilter(MetricFilter filter) {
        this.filter = filter;
        return this;
    }

    public TourineReporterBuilder withRateUnit(TimeUnit rateUnit) {
        this.rateUnit = rateUnit;
        return this;
    }

    public TourineReporterBuilder withDurationUnit(TimeUnit durationUnit) {
        this.durationUnit = durationUnit;
        return this;
    }

    public TourineReporterBuilder withTourineJsonFormat(TourineJsonFormat tourineJsonFormat) {
        this.tourineJsonFormat = tourineJsonFormat;
        return this;
    }

    public TourineReporter build() {
        return new TourineReporter(
                registry,
                MoreObjects.firstNonNull(filter, MetricFilter.ALL),
                rateUnit,
                durationUnit,
                MoreObjects.firstNonNull(tourineJsonFormat, TourineJsonFormat.HYSTRIX));
    }
}