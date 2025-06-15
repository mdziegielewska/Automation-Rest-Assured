package tests.base;

import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.mapper.ObjectMapperType;

/**
 * Base class for all API test cases.
 * Configurations defined here are applied once when the class is loaded,
 * making them available for all test methods in subclasses.
 */
public class BaseTest {

    static {
        RestAssured.baseURI = "https://automationintesting.online/api";

        RestAssured.config = RestAssured.config()
                .objectMapperConfig(new ObjectMapperConfig(ObjectMapperType.JACKSON_2));
    }
}