package us.pdavidson.tourine.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Subscription;
import rx.functions.Action1;
import us.pdavidson.tourine.TourineReporterInstanceHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class TourinePoller {
    private static final Logger log = LoggerFactory.getLogger(TourinePoller.class);
    private final TourinePollerListener listener;
    private final String tourineInstanceKey;
    private final Subscription subscription;
    private boolean running;

    public TourinePoller(final TourinePollerListener listener, final String tourineInstanceKey){
        this.listener = listener;
        this.tourineInstanceKey = tourineInstanceKey;

        this.subscription = TourineReporterInstanceHolder
                            .get(tourineInstanceKey)
                                .getTimerObservable()
                .subscribe(new Action1<String>() {
            @Override
            public void call(String json) {
                listener.put(json);
            }
        });
        this.running = true;
    }

    public void shutdown() {
        subscription.unsubscribe();
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public TourinePollerListener getListener() {
        return listener;
    }

    public String getTourineInstanceKey() {
        return tourineInstanceKey;
    }

    public static class TourinePollerListener {
        private final LinkedBlockingQueue<String> jsonList = new LinkedBlockingQueue<String>(1000);

        public void put(String json){
            jsonList.add(json);
        }


        public List<String> getJsonList() {
            ArrayList<String> metrics = new ArrayList<String>();
            jsonList.drainTo(metrics);
            return metrics;
        }


    }
}
