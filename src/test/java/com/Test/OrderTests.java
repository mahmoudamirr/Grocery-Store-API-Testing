package com.Test;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

import static io.restassured.RestAssured.given;

@Epic("Grocery Store APIs")
@Feature("Order Tests")
@Severity(SeverityLevel.CRITICAL)
@Owner("Mahmoud Amir")


public class OrderTests extends baseTest{

    public static String orderId ;

    @Description("Verify Tht User Can Successfully Create New Order")
    @Test
    public void Successfully_Create_new_Order()
    {
        String customerName = "Mahmoud Amir" ;
        HashMap<String,String> body = new HashMap<>();
        body.put("cartId",CartTests.cartId);
        body.put("customerName",customerName);
        body.put("comment","No Comment");

        Allure.step("Send POST Request To Place an order");

        Response response= given()
                .spec(request)
                .auth().oauth2(RegisterApiTests.accessToken)
                .body(body)
                .when().post("/orders");

        Allure.addAttachment("Response Body","application/json",response.asPrettyString());

        Allure.step("Verify That Status Code is 201");
        Assert.assertEquals(response.getStatusCode(),201);

        Allure.step("Verify That Order is created Successfully");
        Assert.assertTrue(response.jsonPath().getBoolean("created"));

        orderId = response.jsonPath().getString("orderId");

    }

    @Description("Verify Getting Error When Create Order With Invalid Cart id")
    @Test
    public void Verify_error_When_Create_order_with_invalid_CartId()
    {
        String invalid_cartId = "1010" ;
        String customerName = "Mahmoud Amir" ;
        HashMap<String,String> body = new HashMap<>();
        body.put("cartId",invalid_cartId);
        body.put("customerName",customerName);
        body.put("comment","No Comment");

        Allure.step("Send POST Request With invalid Cart id");
        Response response= given()
                .spec(request)
                .auth().oauth2(RegisterApiTests.accessToken)
                .body(body)
                .when().post("/orders");

        Allure.addAttachment("Response Body","application/json",response.asPrettyString());

        Allure.step("Verify That Status Code is 400");
        Assert.assertEquals(response.getStatusCode(),400);

        Allure.step("Verify The Error Message Which Describe The Error");
        Assert.assertTrue(response.asString().contains("Invalid or missing cartId."));

    }

    @Description("Verify Getting Error When Unauthorized And Try To Create an Order")
    @Test (dependsOnMethods = "Successfully_add_items_ToCart")
    public void Verify_error_When_Create_order_on_Unauthorized()
    {
        String customerName = "Mahmoud Amir" ;
        HashMap<String,String> body = new HashMap<>();
        body.put("cartId",CartTests.cartId);
        body.put("customerName",customerName);
        body.put("comment","No Comment");

        Allure.step("Send POST Request Without Authorization Header");

        Response response= given()
                .spec(request)
                .body(body)
                .when().post("/orders");

        Allure.addAttachment("Response Body","application/json",response.asPrettyString());

        Allure.step("Verify That Status Code is 401");
        Assert.assertEquals(response.getStatusCode(),401);

        Allure.step("Verify The Error Message Which Describe The Error");
        Assert.assertTrue(response.asString().contains("Missing Authorization header."));

    }

    @Description("Verify That User Can Get All Orders")
    @Test (dependsOnMethods = "Successfully_Create_new_Order")
    public void Successfully_get_all_orders()
    {
        Allure.step("Send GET Request To Get All Orders");
        Response response= given()
                .spec(request)
                .auth().oauth2(RegisterApiTests.accessToken)
                .log().all()
                .when().get("/orders");

        Allure.addAttachment("Response Body","application/json",response.asPrettyString());

        Allure.step("Verify That Status Code is 200");
        Assert.assertEquals(response.getStatusCode(),200);

    }

    @Description("Verify Getting error When Unauthorized and Try To Get Orders")
    @Test (dependsOnMethods = "Successfully_Create_new_Order")
    public void Verify_get_error_when_try_to_get_orders_and_Unauthorized()
    {
        Allure.step("Send GET Request Without Authorization");
        Response response= given()
                .spec(request)
                .log().all()
                .when().get("/orders");

        Allure.addAttachment("Response Body","application/json",response.asPrettyString());

        Allure.step("Verify That Status Code is 401");
        Assert.assertEquals(response.getStatusCode(),401);

        Allure.step("Verify The Error Message Which Describe The Error");
        Assert.assertTrue(response.asString().contains("Missing Authorization header."));

    }

    @Description("Verify That User Can Get a Single Order")
    @Test (dependsOnMethods = "Successfully_Create_new_Order")
    public void successfully_get_a_single_order()
    {
        Allure.step("Send GET Request With OrderId To Get a Specific Order");
        Response response= given()
                .spec(request)
                .auth().oauth2(RegisterApiTests.accessToken)
                .queryParam("invoice",true)
                .log().all()
                .when().get("/orders/"+orderId);

        Allure.addAttachment("Response Body","application/json",response.asPrettyString());

        Allure.step("Verify That Status Code is 200");
        Assert.assertEquals(response.getStatusCode(),200);

        Allure.step("Verify That The Ordered Returned is The Specific Order");
        Assert.assertEquals(response.jsonPath().getString("id"),orderId);

    }

    @Description("Verify Getting Error When Unauthorized And Try To Get Order")
    @Test (dependsOnMethods = "Successfully_Create_new_Order")
    public void Verify_get_error_when_try_to_get_order_and_Unauthorized()
    {
        Response response= given()
                .spec(request)
                .queryParam("invoice",true)
                .log().all()
                .when().get("/orders/"+orderId);

        Allure.addAttachment("Response Body","application/json",response.asPrettyString());

        Allure.step("Verify That Status Code is 401");
        Assert.assertEquals(response.getStatusCode(),401);

        Allure.step("Verify The Error Message Which Describe The Error");
        Assert.assertTrue(response.asString().contains("Missing Authorization header."));

    }

    @Description("Verify Getting Error When Troy To Get Order With Invalid id")
    @Test (dependsOnMethods = "Successfully_Create_new_Order")
    public void Verify_get_error_when_try_to_get_order_with_invalidId()
    {
        String invalidOrderId = "1515";

        Allure.step("Send GET Request With invalid id");
        Response response= given()
                .spec(request)
                .auth().oauth2(RegisterApiTests.accessToken)
                .queryParam("invoice",true)
                .log().all()
                .when().get("/orders/"+invalidOrderId);

        Allure.addAttachment("Response Body","application/json",response.asPrettyString());

        Allure.step("Verify That Status Code is 404");
        Assert.assertEquals(response.getStatusCode(),404);

        Allure.step("Verify The Error Message Which Describe The Error");
        Assert.assertTrue(response.asString().contains("No order with id"));

    }

    @Description("Verify That The User Can Update an Order")
    @Test (dependsOnMethods = "Successfully_Create_new_Order")
    public void Successfully_Update_an_order()
    {
        HashMap<String,String> body = new HashMap<>();
        body.put("customerName","Amir");
        body.put("comment","This order has been updated");

        Allure.step("Send PATCH Request To Update an Order");
        Response response= given()
                .spec(request)
                .auth().oauth2(RegisterApiTests.accessToken)
                .log().all()
                .body(body)
                .when().patch("/orders/"+orderId);

        Allure.addAttachment("Response Body","application/json",response.asPrettyString());

        Allure.step("Verify That Status code is 204");
        Assert.assertEquals(response.getStatusCode(),204);

    }

    @Description("Verify Getting Error When Try To Update an Order With invalid Parameters")
    @Test (dependsOnMethods = "Successfully_Create_new_Order")
    public void Verify_Get_error_When_Update_an_order_With_invalid_parameters()
    {
        String invalidJsonBody = "{ \"customerName\": Amir , \"comment\": \"This order has been updated\" }";

        Allure.step("Send PATCH Request With Invalid Parameters");
        Response response= given()
                .spec(request)
                .auth().oauth2(RegisterApiTests.accessToken)
                .log().all()
                .body(invalidJsonBody)
                .when().patch("/orders/"+orderId);

        Allure.addAttachment("Response Body","application/json",response.asPrettyString());

        Allure.step("Verify That Status Code is 400");
        Assert.assertEquals(response.getStatusCode(),400);

        Allure.step("Verify The Error Message Which Describe The Error");
        Assert.assertTrue(response.asString().contains("The request body could not be parsed."));

    }

    @Description("Verify Getting Error When Unauthorized and Try To Update an Order")
    @Test (dependsOnMethods = "Successfully_Create_new_Order")
    public void Verify_Get_error_when_Unauthorized_and_try_to_Update_an_order()
    {
        HashMap<String,String> body = new HashMap<>();
        body.put("customerName","Amir");
        body.put("comment","This order has been updated");
        Allure.step("Send PATCH Request Without Authorization");
        Response response= given()
                .spec(request)
                .log().all()
                .body(body)
                .when().patch("/orders/"+orderId);

        Allure.addAttachment("Response Body","application/json",response.asPrettyString());

        Allure.step("Verify That Status Code is 401");
        Assert.assertEquals(response.getStatusCode(),401);

        Allure.step("Verify The Error Message Which Describe The Error");
        Assert.assertTrue(response.asString().contains("Missing Authorization header."));

    }

    @Description("Verify Getting Error When Update an Order With invalid id")
    @Test (dependsOnMethods = "Successfully_Create_new_Order")
    public void Verify_Get_Error_when_try_to_update_Order_with_invalid_id()
    {
        String invalidOrderId = "000" ;
        HashMap<String,String> body = new HashMap<>();
        body.put("customerName","Amir");
        body.put("comment","This order has been updated");

        Allure.step("Send PATCH Request With Invalid Order id");

        Response response= given()
                .spec(request)
                .auth().oauth2(RegisterApiTests.accessToken)
                .log().all()
                .body(body)
                .when().patch("/orders/"+invalidOrderId);

        Allure.addAttachment("Response Body","application/json",response.asPrettyString());

        Allure.step("Verify that Status Code is 404");
        Assert.assertEquals(response.getStatusCode(),404);

        Allure.step("Verify The Error Message Which Describe The Error");
        Assert.assertTrue(response.asString().contains("No order with id"));

    }

    @Description("Verify That User Can Successfully Delete an Order")
    @Test (dependsOnMethods = {"Successfully_Create_new_Order","successfully_get_a_single_order","Successfully_Update_an_order"})
    public void Successfully_Delete_an_Order()
    {
        Allure.step("Send DELETE Request To Delete An Order");
        Response response = given()
                .spec(request)
                .log().all()
                .auth().oauth2(RegisterApiTests.accessToken)
                .when().delete("/orders/"+orderId);

        Allure.addAttachment("Response Body","application/json",response.asPrettyString());

        Allure.step("Verify That Status Code is 204");
        Assert.assertEquals(response.getStatusCode(),204);

    }

    @Description("Verify Getting Error When Unauthorized and Try To Delete an order")
    @Test (dependsOnMethods = "Successfully_Create_new_Order")
    public void Verify_Get_error_when_Try_to_Delete_Order_and_Unauthorized()
    {
        Allure.step("Send DELETE Request Without Authorization");
        Response response= given()
                .spec(request)
                .log().all()
                .when().delete("/orders/"+orderId);

        Allure.addAttachment("Response Body","application/json",response.asPrettyString());

        Allure.step("Verify That Status Code is 401");
        Assert.assertEquals(response.getStatusCode(),401);

        Allure.step("Verify The Error Message Which Describe The Error");
        Assert.assertTrue(response.asString().contains("Missing Authorization header."));

    }

    @Description("Verify Getting Error When Try To Delete an Order With invalid id")
    @Test (dependsOnMethods = "Successfully_Create_new_Order")
    public void Verify_Get_error_when_try_to_Delete_an_Order_with_invalid_Orderid()
    {
        String InvalidOrderId = "1515";
        Allure.step("Send DELETE Request With invalid id");
        Response response = given()
                .spec(request)
                .log().all()
                .auth().oauth2(RegisterApiTests.accessToken)
                .when().delete("/orders/"+InvalidOrderId);

        Allure.addAttachment("Response Body","application/json",response.asPrettyString());

        Allure.step("Verify That Status Code is 404");
        Assert.assertEquals(response.getStatusCode(),404);

        Allure.step("Verify The Error Message Which Describe The Error");
        Assert.assertTrue(response.asString().contains("No order with id"));

    }

}


