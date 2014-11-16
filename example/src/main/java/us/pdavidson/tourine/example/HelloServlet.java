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

public class HelloServlet extends HttpServlet {

    private final Random random = new Random(System.currentTimeMillis());
    private final Timer timer;

    public HelloServlet(MetricRegistry metrics) {
        this.timer = metrics.timer(name(HelloServlet.class));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final Timer.Context context = timer.time();
        Writer w;
        try {
            long l = random.nextInt(400);

            try {
                Thread.sleep(l);
            } catch (InterruptedException e) {
                throw Throwables.propagate(e);
            }

            w = resp.getWriter();
            w.write("Hello, World!");
            w.flush();
        } finally {
            context.stop();
        }

    }
}
