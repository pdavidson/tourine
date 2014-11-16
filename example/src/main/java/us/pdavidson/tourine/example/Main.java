package us.pdavidson.tourine.example;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import us.pdavidson.tourine.TourineJsonFormat;
import us.pdavidson.tourine.TourineReporter;
import us.pdavidson.tourine.TourineReporterInstanceHolder;
import us.pdavidson.tourine.servlet.TourineStreamingServlet;

import java.util.concurrent.TimeUnit;

public class Main {

    static final MetricRegistry metrics = new MetricRegistry();
    private TourineReporter tourineReporter;

    public static void main(String[] args) throws Exception {

        Main main = new Main();
//        main.registerConsoleReporter();
        main.registerTourineReporter();
        main.start();

    }

    public void registerTourineReporter() {
        this.tourineReporter = TourineReporter.forRegistry(metrics)
                .setDurationUnit(TimeUnit.MILLISECONDS)
                .setRateUnit(TimeUnit.SECONDS)
                .setTourineJsonFormat(TourineJsonFormat.HYSTRIX)
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


    public void start() throws Exception {
        Server server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.NO_SESSIONS);
        context.addServlet(new ServletHolder(new HelloServlet(metrics)), "/hello");
        context.addServlet(new ServletHolder(new GoodbyeServlet(metrics)), "/bye");
        context.addServlet(new ServletHolder(new TourineStreamingServlet()), "/tourine.stream");
        server.start();

        System.out.println("Started Jetty On Port 8080");
        server.join();


    }
}
