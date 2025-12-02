package com.Test;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

import static io.restassured.RestAssured.given;

@Epic("Grocery Store APIs")
@Feature("Register Api Tests")
@Severity(SeverityLevel.CRITICAL)
@Owner("Mahmoud Amir")

public class RegisterApiTests extends baseTest {

    public static String accessToken ;

    @Description("Verify that user can successfully register an API client")
    @Test (groups = "Register")
    public void Successfully_Register_Api_Client()
    {

        HashMap<String,String> body = new HashMap();
        body.put("clientName","Mahmoud Amir");
        body.put("clientEmail","mahmoudamiwrrrrrrrcmnmnccnryaabrrrrrrrhhbtrrrrrrrrr@example.com");


        Allure.step("Send POST request to register new client");

        Response response=  given()
                .spec(request)
                .body(body)
                .log().all()
                .when().post("api-clients");

        Allure.addAttachment("Response Body", "application/json", response.asPrettyString());


        Allure.step("Validate status code is 201");
        Assert.assertEquals(response.getStatusCode(), 201);

        Allure.step("Validate token exists in response");
        Assert.assertTrue(response.asString().contains("accessToken"));

        accessToken = response.jsonPath().getString("accessToken");

    }


    @Description("Verify Getting Conflict Error When Try To Register With an already Registered Email")
    @Test
    public void Conflict_Register_Api_Client()
    {

        HashMap<String,String> body = new HashMap();
        body.put("clientName","Mahmoud Amir");
        body.put("clientEmail","mahmoudamir@example.com");


        Allure.step("Send POST request to register new client");

        Response response=  given()
                .spec(request)
                .body(body)
                .log().all()
                .when().post("api-clients");

        Allure.addAttachment("Response Body", "application/json", response.asPrettyString());


        Allure.step("Validate status code is 409");
        Assert.assertEquals(response.getStatusCode(), 409);

        Allure.step("Validate Conflict Error");
        Assert.assertTrue(response.asString().contains("API client already registered. Try a different email."));

    }

    @Description("Verify Getting Error When Try To Register With Missing parameters")
    @Test
    public void Missing_Parameters_Register_Api_Client()
    {

        HashMap<String,String> body = new HashMap();
        body.put("clientName","Mahmoud Amir");

        Allure.step("Send POST request to register new client Without Email");

        Response response=  given()
                .spec(request)
                .body(body)
                .log().all()
                .when().post("api-clients");

        Allure.addAttachment("Response Body", "application/json", response.asPrettyString());


        Allure.step("Validate status code is 400");
        Assert.assertEquals(response.getStatusCode(), 400);

        Allure.step("Validate Missing Email Error");
        Assert.assertTrue(response.asString().contains("Invalid or missing client email."));

    }

    @Description("Verify Getting Error When Try To Register With invalid parameters")
    @Test
    public void Invalid_Parameters_Register_Api_Client()
    {

        HashMap<String,String> body = new HashMap();
        body.put("clientName","Mahmoud Amir");
        body.put("clientEmail","mahmoudamir");

        Allure.step("Send POST request to register new client with an invalid email");

        Response response=  given()
                .spec(request)
                .body(body)
                .log().all()
                .when().post("api-clients");

        Allure.addAttachment("Response Body", "application/json", response.asPrettyString());


        Allure.step("Validate status code is 400");
        Assert.assertEquals(response.getStatusCode(), 400);

        Allure.step("Validate invalid Email Error");
        Assert.assertTrue(response.asString().contains("Invalid or missing client email."));

    }


    @Description("Verify That API is Running as expected")
    @Test
    public void Check_Status()
    {
        Allure.step("Send GET Request To Check Status");
        Response response= given()
                .spec(request)
                .log().all()
                .when().get("/status");

        Allure.addAttachment("Response Body","application/json",response.asPrettyString());

        Allure.step("Validate Status Code is 200");
        Assert.assertEquals(response.getStatusCode(),200);

        Allure.step("Validate The Status is 'UP'");
        Assert.assertEquals(response.jsonPath().getString("status"),"UP");

    }

}
