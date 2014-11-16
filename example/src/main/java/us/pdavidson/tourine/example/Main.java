package us.pdavidson.tourine.example;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

public class Main {


    public static void main(String[] args) throws LifecycleException {

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);

        Context ctx = tomcat.addContext("/", new File(".").getAbsolutePath());

        Tomcat.addServlet(ctx, "hello", new HelloServlet());
        Tomcat.addServlet(ctx, "bye", new GoodbyeServlet());
        ctx.addServletMapping("/hello", "hello");
        ctx.addServletMapping("/bye", "bye");

        tomcat.start();
        tomcat.getServer().await();


    }
}
