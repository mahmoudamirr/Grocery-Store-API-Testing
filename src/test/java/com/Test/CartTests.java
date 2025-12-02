package com.Test;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

import static io.restassured.RestAssured.given;

@Epic("Grocery Store APIs")
@Feature("Cart Tests")
@Severity(SeverityLevel.CRITICAL)
@Owner("Mahmoud Amir")

public class CartTests extends baseTest{

    public static String cartId ;
    public static int itemId ;

    @Description("Verify That User Can Successfully Create New Cart")
    @Test
    public void Successfully_Create_NewCart()
    {
        Allure.step("Send POST Request To Create New Cart");
        Response response= given()
                .spec(request)
                .log().all()
                .when().post("/carts");

        Allure.addAttachment("Response Body","application/json",response.asPrettyString());

        Allure.step("Verify That Status Code is 201");
        Assert.assertEquals(response.getStatusCode(),201);

        Allure.step("Verify That Cart Is Created");
        Assert.assertTrue(response.jsonPath().getBoolean("created"),"Cart Failed To be Created");

        cartId = response.jsonPath().getString("cartId");

    }

    @Description("Verify That The User Can Successfully Get The Created Cart")
    @Test (dependsOnMethods = "Successfully_Create_NewCart")
    public void Successfully_get_the_CreatedCart()
    {
        Allure.step("Send GET Request To Return The Created Cart");
        Response response= given()
                .spec(request)
                .log().all()
                .when().get("carts/" + cartId);

        Allure.addAttachment("Response Body","application/json",response.asPrettyString());

        Allure.step("Verify That The Status Code is 200");
        Assert.assertEquals(response.getStatusCode(),200);

    }

    @Description("Verify That The User Gets Error When Searches With invalid Cart Id")
    @Test
    public void verify_error_on_invalid_CartID()
    {
        int invalid_CartId = 1212;

        Allure.step("Send Get Request With invalid Cart Id");
        Response response= given()
                .spec(request)
                .log().all()
                .when().get("carts/" + invalid_CartId);

        Allure.addAttachment("Response Body","application/json",response.asPrettyString());

        Allure.step("Verify That The Status Code is 404");
        Assert.assertEquals(response.getStatusCode(),404);

        Allure.step("Verify The Error Message Which Describe The Error");
        Assert.assertTrue(response.asString().contains("No cart with id"));

    }

    @Description("Verify That The User Can Successfully add items To Cart")
    @Test (dependsOnMethods = "Successfully_Create_NewCart")
    public void Successfully_add_items_ToCart()
    {
        HashMap<String , Integer> body = new HashMap<>();
        body.put("productId",2585);
        body.put("quantity",10);

        Allure.step("Send POST Request To Add Items To Cart");
        Response response= given()
                .spec(request)
                .body(body)
                .log().all()
                .when().post("/carts/" + cartId +"/items");

        Allure.addAttachment("Response Body","application/json",response.asPrettyString());

        Allure.step("Verify That Status Code is 201");
        Assert.assertEquals(response.getStatusCode(),201);

        Allure.step("Verify That Item is Added Successfully");
        Assert.assertTrue(response.jsonPath().getBoolean("created"));

        itemId = response.jsonPath().getInt("itemId");

    }

    @Description("Verify That User Can add Another Item to Cart")
    @Test (dependsOnMethods = "Successfully_Create_NewCart")
    public void Successfully_add_another_item_ToCart()
    {
        HashMap<String , Integer> body = new HashMap<>();
        body.put("productId",1709);
        body.put("quantity",10);

        Allure.step("Send POST Request to Add Another item to cart");

        Response response= given()
                .spec(request)
                .body(body)
                .log().all()
                .when().post("/carts/" + cartId +"/items");

        Allure.addAttachment("Response Body","application/json",response.asPrettyString());

        Allure.step("Verify That Status Code is 201");
        Assert.assertEquals(response.getStatusCode(),201);

        Allure.step("Verify That Item is Added Successfully");
        Assert.assertTrue(response.jsonPath().getBoolean("created"));

        itemId = response.jsonPath().getInt("itemId");

    }

    @Description("Verify That User Gets Error When try to add invalid items")
    @Test (dependsOnMethods = "Successfully_Create_NewCart")
    public void verify_error_when_add_invalid_items()
    {
        Integer invalid_productid = 1111222 ;
        HashMap<String , Integer> body = new HashMap<>();
        body.put("productId",invalid_productid);
        body.put("quantity",10);

        Allure.step("Send Post Request With invalid item id");

        Response response= given()
                .spec(request)
                .body(body)
                .log().all()
                .when().post("/carts/" + cartId +"/items");

        Allure.addAttachment("Response Body","application/json",response.asPrettyString());

        Allure.step("Verify That Status Code is 400");
        Assert.assertEquals(response.getStatusCode(),400);

        Allure.step("Verify The Error Message Which Describe The Error");
        Assert.assertTrue(response.asString().contains("Invalid or missing productId."));

    }

    @Description("Verify That The User Can Successfully Modify Cart")
    @Test (dependsOnMethods = "Successfully_add_items_ToCart")
    public void Successfully_modify_cart()
    {
        HashMap<String,Object> body = new HashMap<>();
        body.put("cartId",cartId);
        body.put("itemId",itemId);
        body.put("quantity",5);

        Allure.step("Send PUT Request With Updated Data");
        Response response= given()
                .spec(request)
                .log().all()
                .body(body)
                .when().put("carts/"+cartId+"/items/"+itemId);

        Allure.addAttachment("Response Body","application/json",response.asPrettyString());

        Allure.step("Verify That Status Code is 204");
        Assert.assertEquals(response.getStatusCode(),204);

    }

    @Description("Verify That User Gets Error When Try To modify Cart With Invalid Quantity")
    @Test (dependsOnMethods = "Successfully_add_items_ToCart")
    public void verify_error_on_modify_cart_with_invalid_Quantity()
    {
        Integer invalid_quantity = 500 ;
        HashMap<String,Object> body = new HashMap<>();
        body.put("cartId",cartId);
        body.put("itemId",itemId);
        body.put("quantity",invalid_quantity);

        Allure.step("Send PUT Request With Invalid Quantity");
        Response response= given()
                .spec(request)
                .log().all()
                .body(body)
                .when().put("carts/"+cartId+"/items/"+itemId);

        Allure.addAttachment("Response Body","application/json",response.asPrettyString());

        Allure.step("Verify That Status Code is 400");
        Assert.assertEquals(response.getStatusCode(),400);

        Allure.step("Verify The Error Message Which Describe The Error");
        Assert.assertTrue(response.asString().contains("The quantity requested is not available in stock."));

    }

    @Description("Verify Getting Error When Try To modify Cart With Invalid EndPoint")
    @Test (dependsOnMethods = "Successfully_add_items_ToCart")
    public void verify_error_on_modify_cart_with_invalid_endpoint()
    {
        HashMap<String,Object> body = new HashMap<>();
        body.put("cartId",cartId);
        body.put("itemId",itemId);
        body.put("quantity",5);

        Allure.step("Send PUT Request With invalid EndPoint");
        Response response= given()
                .spec(request)
                .log().all()
                .body(body)
                .when().put("carts/"+cartId+"/items/");

        Allure.addAttachment("Response Body","application/json",response.asPrettyString());

        Allure.step("Verify That Status Code is 404");
        Assert.assertEquals(response.getStatusCode(),404);

        Allure.step("Verify The Error Message Which Describe The Error");
        Assert.assertTrue(response.asString().contains("The resource could not be found. Check your endpoint and request method."));

    }

    @Description("Verify That User Can Replace Items in Cart")
    @Test (dependsOnMethods = "Successfully_add_items_ToCart")
    public void Successfully_replace_item_inCart()
    {
        Integer valid_productId = 4646 ;
        HashMap<String,Object> body = new HashMap<>();
        body.put("cartId",cartId);
        body.put("itemId",itemId);
        body.put("productId",valid_productId);
        body.put("quantity",5);

        Allure.step("Send PUT Request With Valid Modified Data");

        Response response= given()
                .spec(request)
                .log().all()
                .body(body)
                .when().put("carts/"+cartId+"/items/"+itemId);

        Allure.addAttachment("Response Body","application/json",response.asPrettyString());

        Allure.step("Verify That Status code is 204");
        Assert.assertEquals(response.getStatusCode(),204);

    }

    @Description("Verify Getting Error When Try To Update Cart With Invalid Parameters")
    @Test (dependsOnMethods = "Successfully_add_items_ToCart")
    public void verify_error_when_update_cart_with_invalidParameters()
    {
        Integer valid_productId = 4875 ;
        Integer invalid_quantity = 500 ;
        HashMap<String,Object> body = new HashMap<>();
        body.put("cartId",cartId);
        body.put("itemId",itemId);
        body.put("productId",valid_productId);
        body.put("quantity",invalid_quantity);

        Allure.step("Send PUT Request With invalid Parameters");

        Response response= given()
                .spec(request)
                .log().all()
                .body(body)
                .when().put("carts/"+cartId+"/items/"+itemId);

        Allure.addAttachment("Response Body","application/json",response.asPrettyString());

        Allure.step("Verify That Status Code is 400");
        Assert.assertEquals(response.getStatusCode(),400);

        Allure.step("Verify The Error Message Which Describe The Error");
        Assert.assertTrue(response.asString().contains("The quantity requested is not available in stock."));

    }

    @Description("Verify Getting Error When Update Cart With invalid End Point")
    @Test (dependsOnMethods = "Successfully_add_items_ToCart")
    public void verify_error_when_update_cart_with_invalidEndpoint()
    {
        Integer valid_productId = 4875 ;
        Integer invalid_quantity = 500 ;
        HashMap<String,Object> body = new HashMap<>();
        body.put("cartId",cartId);
        body.put("itemId",itemId);
        body.put("productId",valid_productId);
        body.put("quantity",invalid_quantity);

        Response response= given()
                .spec(request)
                .log().all()
                .body(body)
                .when().put("carts/"+cartId+"/items/");

        Allure.addAttachment("Response Body","application/json",response.asPrettyString());

        Allure.step("Verify That Status Code is 404");
        Assert.assertEquals(response.getStatusCode(),404);

        Allure.step("Verify The Error Message Which Describe The Error");
        Assert.assertTrue(response.asString().contains("The resource could not be found. Check your endpoint and request method."));

    }

    @Description("Verify That User Can Successfully Delete Item in Cart")
    @Test (dependsOnMethods = {"Successfully_add_items_ToCart","Successfully_modify_cart",
            "Successfully_replace_item_inCart","verify_error_on_modify_cart_with_invalid_Quantity","verify_error_when_update_cart_with_invalidParameters"})
    public void Successfully_delete_item_inCart()
    {
        Allure.step("Send Delete Request");
        Response response= given()
                .spec(request)
                .log().all()
                .when().delete("/carts/"+cartId+"/items/"+itemId);

        Allure.addAttachment("Response Body","application/json",response.asPrettyString());

        Allure.step("Verify That Status Code is 204");
        Assert.assertEquals(response.getStatusCode(),204);

    }

    @Description("Verify Getting Error When Try To Delete item With Invalid End Point")
    @Test (dependsOnMethods = "Successfully_add_items_ToCart")
    public void verify_error_when_delete_item_with_invalidEndpoint()
    {
        Allure.step("Send DELETE Request With Invalid EndPoint");

        Response response= given()
                .spec(request)
                .log().all()
                .when().delete("/carts/"+cartId+"/items/");

        Allure.addAttachment("Response Body","application/json",response.asPrettyString());

        Allure.step("Verify That Status Code is 404");
        Assert.assertEquals(response.getStatusCode(),404);

        Allure.step("Verify The Error Message Which Describe The Error");
        Assert.assertTrue(response.asString().contains("The resource could not be found. Check your endpoint and request method."));

    }

}
