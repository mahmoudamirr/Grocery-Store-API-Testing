package com.Test;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;

@Epic("Grocery Store APIs")
@Feature("Products Tests")
@Severity(SeverityLevel.CRITICAL)
@Owner("Mahmoud Amir")

public class ProductsTests extends baseTest{

    @Description("Verify That The User Can Get All Products")
    @Test
    public void Successfully_Get_All_Products()
    {
        Allure.step("Send Get Request To Return All Products");

        Response response=given()
                .spec(request)
                .log().all()
                .when().get("/products");

        Allure.addAttachment("Response Body","application/json",response.asPrettyString());

        Allure.step("Validate Status Code is 200");
        Assert.assertEquals(response.getStatusCode(),200);

    }

    @Description("Verify That The User Can Get a Specific Product")
    @Test
    public void Successfully_Get_Specific_Products()
    {
        int Specific_ProductId = 1225 ;
        Allure.step("Send GET Request With A Specific Order ID");
        Response response= given()
                .spec(request)
                .log().all()
                .when().get("products/" + Specific_ProductId);

        Allure.addAttachment("Response Body","application/json",response.asPrettyString());

        Allure.step("Verify That Status Code is 200");
        Assert.assertEquals(response.getStatusCode(),200);

        Allure.step("Verify That The item Returned is The Specific item");
        Assert.assertEquals(response.jsonPath().getInt("id"),Specific_ProductId);

    }

    @Description("Verify That The User Get Error Message When Search With Invalid Product id")
    @Test
    public void verify_error_on_invalid_productID()
    {
        int invalid_ProductId = 101010 ;

        Allure.step("Send Get Request With Invalid Product id");

        Response response= given()
                .spec(request)
                .log().all()
                .when().get("products/" + invalid_ProductId);

        Allure.addAttachment("Response Body","application/json",response.asPrettyString());

        Allure.step("Verify That The Status Code is 404");
        Assert.assertEquals(response.getStatusCode(),404);

        Allure.step("Verify The Error Message Which Describe The Error");
        Assert.assertTrue(response.asString().contains("No product with id"));

    }

    @Description("Verify That The User Can Get All Products in Stock")
    @Test
    public void Successfully_get_products_inStock()
    {
        Allure.step("Send Get Request To Return All Products in Stock");

        Response response= given()
                .spec(request)
                .log().all()
                .queryParam("inStock","true")
                .when().get("/products");

        Allure.addAttachment("Response Body","application/json",response.asPrettyString());

        Allure.step("Verify That Status Code is 200");
        Assert.assertEquals(response.getStatusCode(),200);

        List<Boolean> inStockList = response.jsonPath().getList("inStock", Boolean.class);

        Allure.step("Verify That All Products Returned are in Stock");
        for (Boolean inStock : inStockList)
        {
            Assert.assertTrue(inStock, "Error: There are items not in stock.");
        }
//        bug -> there are 2 products not in stock
    }

}
