package us.pdavidson.tourine.example;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import us.pdavidson.tourine.TourineReporter;
import us.pdavidson.tourine.TourineReporterInstanceHolder;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class Main {

    static final MetricRegistry metrics = new MetricRegistry();
    private TourineReporter tourineReporter;

    public static void main(String[] args) throws LifecycleException {

        Main main = new Main();
        main.registerConsoleReporter();
        main.start();

    }

    public void registerTourineReporter() {
        this.tourineReporter = TourineReporter.forRegistry(metrics)
                .setDurationUnit(TimeUnit.MILLISECONDS)
                .setRateUnit(TimeUnit.SECONDS)
                .build();
        this.tourineReporter.start(1, TimeUnit.SECONDS);
        TourineReporterInstanceHolder.set(this.tourineReporter);
    }

    public void registerConsoleReporter() {
        ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(1, TimeUnit.SECONDS);
    }


    public void start() throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);

        Context ctx = tomcat.addContext("/", new File(".").getAbsolutePath());

        Tomcat.addServlet(ctx, "hello", new HelloServlet(metrics));
        Tomcat.addServlet(ctx, "bye", new GoodbyeServlet(metrics));
        ctx.addServletMapping("/hello", "hello");
        ctx.addServletMapping("/bye", "bye");

        tomcat.start();
        tomcat.getServer().await();

    }
}
