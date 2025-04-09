package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class CarResourceIT {

    @Inject
    ICarCache carCache;

    @BeforeEach
    void cleanCache() {
        carCache.removeCar("test-id");
    }

    @Test
    public void testAddAndGetCar() {
        Car car = new Car("test-id", "Peugeot", "208", 2022);
        carCache.addCar(car); // ajout direct

        given()
            .when()
            .get("/car/test-id")
            .then()
            .statusCode(200)
            .body("brand", equalTo("Peugeot"))
            .body("model", equalTo("208"))
            .body("year", equalTo(2022));
    }

    @Test
    public void testAddCarViaPost() {
        Car car = new Car("test-id", "Renault", "Clio", 2023);

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(car)
            .when()
            .post("/car")
            .then()
            .statusCode(201)
            .body("id", equalTo("test-id"))
            .body("brand", equalTo("Renault"));
    }

    @Test
    public void testDeleteCar() {
        Car car = new Car("test-id", "Fiat", "Panda", 2020);
        carCache.addCar(car);

        given()
            .when()
            .delete("/car/test-id")
            .then()
            .statusCode(204);

        given()
            .when()
            .get("/car/test-id")
            .then()
            .statusCode(404);
    }
}
