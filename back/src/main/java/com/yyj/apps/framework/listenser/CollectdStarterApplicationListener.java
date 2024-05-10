package com.yyj.apps.framework.listenser;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CollectdStarterApplicationListener implements ApplicationListener<ContextRefreshedEvent> {


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        try {
            initServices();
            startServices();
        } catch (Exception e) {
            log.warn("onapplication exception. " + e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void initServices() throws Exception {

    }

    private void startServices() {

    }

}

