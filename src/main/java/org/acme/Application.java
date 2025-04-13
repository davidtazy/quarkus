package org.acme;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jboss.logging.Logger;

import io.quarkus.scheduler.Scheduled;

@ApplicationScoped
public class Application implements ApplicationController {

    @Inject
    PointGenerator pointGenerator;

    private static final Logger LOG = Logger.getLogger(Application.class);

    private static final double maxRadius = 500.0;

    private volatile boolean running = false;

    private ScheduledExecutorService executor;

    private  Position position = new Position(44.0, 12.0, 0.0, 1.0);


    @Override
    public void start() {
        running = true;
        LOG.info("Application started.");
        if(executor != null) {
            return;
        }
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            onTick();
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stop() {
        running = false;
        LOG.info("Application stopped.");

        if (executor != null) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }
        executor = null;
    }

    @Override
    public boolean isStarted() {
        return running;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    void onTick() {
        if (running) {
           
                double distance = 500;//Math.random() * maxRadius;
                double angle = position.headingDegrees();//Math.random() * 360.0;
                pointGenerator.newPoint(new PolarPoint(distance, angle));
        }
    }

    @Scheduled(every = "1s")
    void scheduledTask() {
        if (running) {
           position = new Position(
                position.latDegrees(),
                position.lonDegrees(),
                (position.headingDegrees() + 1.0) % 360.0,
                position.speedKnots()
            );
        }
    }


}
