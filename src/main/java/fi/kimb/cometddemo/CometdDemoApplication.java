package fi.kimb.cometddemo;

import org.cometd.server.CometDServlet;
import org.cometd.server.websocket.javax.WebSocketTransport;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

@SpringBootApplication
public class CometdDemoApplication implements ServletContextInitializer {

    public static void main(String[] args) {
        SpringApplication.run(CometdDemoApplication.class, args);
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        ServletRegistration.Dynamic cometdServlet = servletContext.addServlet("cometd", CometDServlet.class);
        cometdServlet.addMapping("/cometd/*");
        cometdServlet.setAsyncSupported(true);
        cometdServlet.setLoadOnStartup(1);
        cometdServlet.setInitParameter("ws.cometdURLMapping", "/cometd/*");
        cometdServlet.setInitParameter("transports", WebSocketTransport.class.getName());
    }
}
