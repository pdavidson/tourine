package us.pdavidson.tourine;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.google.common.base.MoreObjects;

import java.util.concurrent.TimeUnit;

public class TourineReporterBuilder {
    private MetricRegistry registry;
    private String name;
    private MetricFilter filter;
    private TimeUnit rateUnit;
    private TimeUnit durationUnit;
    private TourineJsonFormat tourineJsonFormat;

    public TourineReporterBuilder setRegistry(MetricRegistry registry) {
        this.registry = registry;
        return this;
    }

    public TourineReporterBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public TourineReporterBuilder setFilter(MetricFilter filter) {
        this.filter = filter;
        return this;
    }

    public TourineReporterBuilder setRateUnit(TimeUnit rateUnit) {
        this.rateUnit = rateUnit;
        return this;
    }

    public TourineReporterBuilder setDurationUnit(TimeUnit durationUnit) {
        this.durationUnit = durationUnit;
        return this;
    }

    public TourineReporterBuilder setTourineJsonFormat(TourineJsonFormat tourineJsonFormat) {
        this.tourineJsonFormat = tourineJsonFormat;
        return this;
    }

    public TourineReporter build() {
        return new TourineReporter(
                registry,
                name,
                MoreObjects.firstNonNull(filter, MetricFilter.ALL),
                rateUnit,
                durationUnit,
                MoreObjects.firstNonNull(tourineJsonFormat, TourineJsonFormat.HYSTRIX));
    }
}