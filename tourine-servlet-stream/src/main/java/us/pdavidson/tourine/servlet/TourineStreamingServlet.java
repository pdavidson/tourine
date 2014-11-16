package us.pdavidson.tourine.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.functions.Action1;
import us.pdavidson.tourine.TourineReporterInstanceHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class TourineStreamingServlet extends HttpServlet{

    private static final Logger log = LoggerFactory.getLogger(TourineStreamingServlet.class);

    /* used to track number of connections and throttle */
    private static AtomicInteger concurrentConnections = new AtomicInteger(0);
    private static final Integer maxConcurrentConnections = 5;


    @Override
    protected void doGet(HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        int numberConnections = concurrentConnections.incrementAndGet();
        try {
            if (numberConnections > maxConcurrentConnections) {
                log.error("MaxConcurrentConnections reached {}", maxConcurrentConnections);
                response.sendError(503, "MaxConcurrentConnections reached: " + maxConcurrentConnections);
            } else {

                /* initialize response */
                response.setHeader("Content-Type", "text/event-stream;charset=UTF-8");
                response.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
                response.setHeader("Pragma", "no-cache");

                log.info("Connecting To Timer Observable");

                Observable<String> timerObservable = TourineReporterInstanceHolder.get().getTimerObservable();
                timerObservable.subscribe(new Action1<String>() {
                    @Override
                    public void call(String json) {
                        try {
                            response.getWriter().println("data: " + json + "\n");
                        } catch (IOException e) {
                            log.debug("Error Writing json {}", json, e);
                        }
                    }
                });

            }


        } finally {
            concurrentConnections.decrementAndGet();
        }
    }
}
