package us.pdavidson.tourine.example;


import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.base.Throwables;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Random;

import static com.codahale.metrics.MetricRegistry.name;


public class GoodbyeServlet extends HttpServlet {

    private final Random random = new Random(System.currentTimeMillis());
    private final Timer timer;

    public GoodbyeServlet(MetricRegistry metrics) {
        this.timer = metrics.timer(name(GoodbyeServlet.class));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Timer.Context context = timer.time();
        try {

            long l = random.nextInt(200);

            try {
                Thread.sleep(l);
            } catch (InterruptedException e) {
                throw Throwables.propagate(e);
            }

            Writer w = resp.getWriter();
            w.write("Goodbye Sweet World!");
            w.flush();
        } finally {
            context.stop();
        }
    }
}
