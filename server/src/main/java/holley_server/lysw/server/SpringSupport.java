package holley_server.lysw.server;

import org.springframework.context.ApplicationContext;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringSupport {

    public static ApplicationContext springHandle = null;

    public static void initHandle() {
        if (springHandle == null) {

            // 初始化Spring
           // springHandle = new ClassPathXmlApplicationContext("classpath:spring*.xml"  );
            springHandle = new ClassPathXmlApplicationContext(new String[]{"spring/spring.xml","spring/spring-job.xml"});
        }
    }
}
