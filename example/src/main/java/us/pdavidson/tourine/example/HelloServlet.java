package us.pdavidson.tourine.example;


import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

import static com.codahale.metrics.MetricRegistry.name;

public class HelloServlet extends HttpServlet {


    private final Timer timer;

    public HelloServlet(MetricRegistry metrics) {
        this.timer = metrics.timer(name(HelloServlet.class));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final Timer.Context context = timer.time();
        Writer w;
        try {
            w = resp.getWriter();
            w.write("Hello, World!");
            w.flush();
        } finally {
            context.stop();
        }

    }
}
