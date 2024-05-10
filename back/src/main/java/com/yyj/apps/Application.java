package com.yyj.apps;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.nio.charset.Charset;
import java.util.TimeZone;


@SpringBootApplication
@ComponentScan("com.yyj.apps")
@Slf4j
public class Application {

    private static ConfigurableApplicationContext applicationContext;

    protected  static boolean procArguments(String[] args) {
        for (String arg:args) {
            if (arg.equals("-version") || arg.equals("-v") || arg.equals("version")) {
                System.exit(0);
            }
        }

        return false;
    }

    /**
     * 退出所有服务
     */
    private static void quitServices() {

    }

    public static void main(String[] args) {
        System.out.println("Current time zone: " + TimeZone.getDefault().getID());
        System.out.println("Current Charset: " + Charset.defaultCharset().name());
        //重新设置 user.dir
        String home = System.getProperty("agent.install.home");
        if(home!=null && home.length()>0) {
            System.setProperty("user.dir", home);
        }
        System.out.println("Current home: " + System.getProperty("user.dir"));
        System.setProperty("oracle.jdbc.V8Compatible", "true");
        if (args.length > 0) {
            procArguments(args);
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                log.info("interrupt received, killing server…");
                try {
                    quitServices();
                } catch (Exception e) {
                } finally {
                    if (applicationContext != null) {
                        log.info("exit spring application.");
                        try {
                            SpringApplication.exit(applicationContext);
                        }catch (Exception e) {
                        }
                    }
                    log.info("server exited.");
                }
            }
        });
        applicationContext = SpringApplication.run(Application.class, args);
    }
}