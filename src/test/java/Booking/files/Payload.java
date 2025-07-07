package Booking.files;

import io.restassured.path.json.JsonPath;

import static io.restassured.RestAssured.given;

public class Payload {

    public static String bookingBody1() {
        return "{\n" +
                "    \"firstname\" : \"Dragoljub\",\n" +
                "    \"lastname\" : \"Brown\",\n" +
                "    \"totalprice\" : 99,\n" +
                "    \"depositpaid\" : true,\n" +
                "    \"bookingdates\" : {\n" +
                "        \"checkin\" : \"2018-01-01\",\n" +
                "        \"checkout\" : \"2019-01-01\"\n" +
                "    },\n" +
                "    \"additionalneeds\" : \"Dinner\"\n" +
                "}";
    }

    public static String bookingBody2() {
        return "{\n" +
                "    \"firstname\" : \"Dragoljub\",\n" +
                "    \"lastname\" : \"Brown\",\n" +
                "    \"totalprice\" : 50,\n" +
                "    \"depositpaid\" : false,\n" +
                "    \"bookingdates\" : {\n" +
                "        \"checkin\" : \"2018-01-01\",\n" +
                "        \"checkout\" : \"2020-01-01\"\n" +
                "    },\n" +
                "    \"additionalneeds\" : \"Breakfast\"\n" +
                "}";
    }

    public static String tokenBody() {
        return "{\n" +
                "    \"username\" : \"admin\",\n" +
                "    \"password\" : \"password123\"\n" +
                "}";
    }

    public static String token() {
        String tokenResponse =
                given().log().all()
                        .header("Content-Type", "application/json")
                        .body(tokenBody())
                        .when().post("/auth")
                        .then().log().all()
                        .assertThat().statusCode(200)
                        .extract().response().asString();

        JsonPath js2 = new JsonPath(tokenResponse);
        return js2.getString("token");
    }

}
