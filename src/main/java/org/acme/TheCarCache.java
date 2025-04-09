package org.acme;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TheCarCache implements ICarCache {

    private static final long TTL_SECONDS = 3600; // 1 heure
    private final ValueCommands<String, Car> commands;

    public TheCarCache(RedisDataSource ds) {
        commands = ds.value(Car.class);
    }

    @Override
    public void addCar(Car car) {
        commands.setex(key(car.id()), TTL_SECONDS, car);
    }

    @Override
    public Car getCar(String id) {
        return commands.get(key(id));
    }

    @Override
    public void removeCar(String id) {
        commands.getdel(key(id));
    }

    private String key(String id) {
        return "car:" + id;
    }
}
