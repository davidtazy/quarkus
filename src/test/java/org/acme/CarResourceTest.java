package org.acme;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CarResourceTest {

    private ICarCache mockCache;
    private CarResource resource;

    @BeforeEach
    public void setup() {
        mockCache = Mockito.mock(ICarCache.class);
        resource = new CarResource();
        resource.carCache = mockCache; // inject mock manually
    }

    @Test
    public void testGetCarFound() {
        Car car = new Car("1", "Toyota", "Corolla", 2020);
        when(mockCache.getCar("1")).thenReturn(car);

        Response response = resource.getCar("1");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(car, response.getEntity());
    }

    @Test
    public void testGetCarNotFound() {
        when(mockCache.getCar("999")).thenReturn(null);

        Response response = resource.getCar("999");

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testAddCar() {
        Car car = new Car("2", "Honda", "Civic", 2021);

        Response response = resource.addCar(car);

        verify(mockCache).addCar(car);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(car, response.getEntity());
    }

    @Test
    public void testRemoveCar() {
        Response response = resource.removeCar("3");

        verify(mockCache).removeCar("3");
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }
}
