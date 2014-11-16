package us.pdavidson.tourine.servlet;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class TourineStreamingServlet extends HttpServlet{
    protected static final String TOURINE_REPORTER_PARAM = "TourineReporter";
    public static final String TOURINE_REPORTER_DEFAULT_KEY = "default";
    private static final Logger log = LoggerFactory.getLogger(TourineStreamingServlet.class);

    /* used to track number of connections and throttle */
    private static AtomicInteger concurrentConnections = new AtomicInteger(0);
    private static final Integer maxConcurrentConnections = 5;
    private boolean isDestroyed = false;
    private String tourineReporterKey;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        this.tourineReporterKey =
                MoreObjects.firstNonNull(Strings.emptyToNull(config.getInitParameter(TOURINE_REPORTER_PARAM)), TOURINE_REPORTER_DEFAULT_KEY);

    }

    @Override
    protected void doGet(HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        int numberConnections = concurrentConnections.incrementAndGet();
        TourinePoller poller = null;
        try {
            if (numberConnections > maxConcurrentConnections) {
                log.error("MaxConcurrentConnections reached {}", maxConcurrentConnections);
                response.sendError(503, "MaxConcurrentConnections reached: " + maxConcurrentConnections);
            } else {

                int delay = 1000;
                try {
                    String d = request.getParameter("delay");
                    if (d != null) {
                        delay = Integer.parseInt(d);
                    }
                } catch (Exception e) {
                    // ignore if it's not a number
                }

                /* initialize response */
                response.setHeader("Content-Type", "text/event-stream;charset=UTF-8");
                response.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
                response.setHeader("Pragma", "no-cache");

                log.info("Connecting To Timer Poller");

                TourinePoller.TourinePollerListener listener = new TourinePoller.TourinePollerListener();
                poller = new TourinePoller(listener, tourineReporterKey);

                try {
                    while (poller.isRunning() && !isDestroyed) {
                        List<String> jsonMessages = listener.getJsonList();
                        if (jsonMessages.isEmpty()) {
                            // https://github.com/Netflix/Hystrix/issues/85 hystrix.stream holds connection open if no metrics
                            // we send a ping to test the connection so that we'll get an IOException if the client has disconnected
                            response.getWriter().println("ping: \n");
                        } else {
                            for (String json : jsonMessages) {
                                response.getWriter().println("data: " + json + "\n");
                            }
                        }

                        if(isDestroyed) {
                            break;
                        }

                        // after outputting all the messages we will flush the stream
                        response.flushBuffer();

                        // now wait the 'delay' time
                        Thread.sleep(delay);
                    }
                } catch (InterruptedException e) {
                    poller.shutdown();
                    Thread.currentThread().interrupt();
                } catch (IOException e) {
                    poller.shutdown();
                    // debug instead of error as we expect to get these whenever a client disconnects or network issue occurs
                    log.debug("IOException while trying to write (generally caused by client disconnecting). Will stop polling.");
                } catch (Exception e){
                    poller.shutdown();
                    log.error("Failed to write. Will stop polling.", e);
                }

                log.debug("Stopping Tourine stream to connection");
            }


        } finally {
            concurrentConnections.decrementAndGet();
            if (poller != null) {
                poller.shutdown();
            }
        }
    }

    @Override
    public void destroy() {
        /* set marker so the loops can break out */
        isDestroyed = true;
        super.destroy();
    }
}
