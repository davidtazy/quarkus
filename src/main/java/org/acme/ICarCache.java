package org.acme;

public interface ICarCache {

   
    void addCar( Car car);

    Car getCar(String id);

    void removeCar(String id);




}
