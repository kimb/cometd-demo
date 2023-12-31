package fi.kimb.cometddemo;

import lombok.SneakyThrows;
import org.cometd.annotation.server.ServerAnnotationProcessor;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.server.BayeuxServerImpl;
import org.cometd.server.http.JSONPTransport;
import org.cometd.server.http.JSONTransport;
import org.cometd.server.websocket.javax.WebSocketTransport;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.servlet.ServletContext;


@Component
public class CometDInitializer implements ServletContextAware {
    private ServletContext servletContext;

    @Bean(destroyMethod = "stop")
    public BayeuxServer bayeuxServer() {
        BayeuxServerImpl bean = new BayeuxServerImpl();
        bean.setTransports(new WebSocketTransport(bean), new JSONTransport(bean), new JSONPTransport(bean));
        bean.setOption("ws.cometdURLMapping", "/cometd/*");
        return bean;
    }

    @PostConstruct
    private void start() throws Exception {

    }

    @SneakyThrows
    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
        BayeuxServerImpl bean = (BayeuxServerImpl) bayeuxServer();
        servletContext.setAttribute(BayeuxServer.ATTRIBUTE, bean);
        bean.setOption(ServletContext.class.getName(), servletContext);
        bean.start();
    }

    @Component
    public static class Processor implements DestructionAwareBeanPostProcessor {
        @Inject
        private BayeuxServer bayeuxServer;
        private ServerAnnotationProcessor processor;

        @PostConstruct
        private void init() {
            this.processor = new ServerAnnotationProcessor(bayeuxServer);
        }

        @PreDestroy
        private void destroy() {
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String name) throws BeansException {
            processor.processDependencies(bean);
            processor.processConfigurations(bean);
            processor.processCallbacks(bean);
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String name) throws BeansException {
            return bean;
        }

        @Override
        public void postProcessBeforeDestruction(Object bean, String name) throws BeansException {
            processor.deprocessCallbacks(bean);
        }

        @Override
        public boolean requiresDestruction(Object bean) {
            return true;
        }
    }
}
