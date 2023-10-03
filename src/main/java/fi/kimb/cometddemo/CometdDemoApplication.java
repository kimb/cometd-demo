package fi.kimb.cometddemo;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import lombok.extern.slf4j.Slf4j;
import org.cometd.server.CometDServlet;
import org.cometd.server.websocket.javax.WebSocketTransport;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.websocket.servlet.JettyWebSocketServletWebServerCustomizer;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
@EnableAutoConfiguration
@Slf4j
public class CometdDemoApplication implements ServletContextInitializer {

    public static void main(String[] args) {
        SpringApplication.run(CometdDemoApplication.class, args);
    }

    @Bean
    public JettyServletWebServerFactory jetty() {
        JettyServletWebServerFactory serverFactory = new JettyServletWebServerFactory();
        new JettyWebSocketServletWebServerCustomizer().customize(serverFactory);
        return serverFactory;
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        ServletRegistration.Dynamic cometdServlet = servletContext.addServlet("cometd", CometDServlet.class);
        cometdServlet.addMapping("/cometd/*");
        cometdServlet.setAsyncSupported(true);
        cometdServlet.setLoadOnStartup(1);
        cometdServlet.setInitParameter("ws.cometdURLMapping", "/cometd/*");
        cometdServlet.setInitParameter("transports", WebSocketTransport.class.getName());

//		cometdServlet.setInitParameter("services", EchoService.class.getName());
        // Possible additional CometD Servlet configuration.
    }
}
