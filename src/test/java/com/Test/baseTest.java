package com.Test;

import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;

import static io.restassured.RestAssured.given;

public abstract class baseTest {

    RequestSpecification request ;

    @BeforeClass
    public void beforeclass()
    {
        request = given()
                .baseUri("https://simple-grocery-store-api.click")
                .headers("Content-Type","application/json");
    }
}
