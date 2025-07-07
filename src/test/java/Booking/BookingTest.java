package Booking;

import Booking.files.Payload;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;

public class BookingTest {

    // Kada pravimo API testove mozemo odmah krenuti od samih testova
    // Sto se tice biblioteka dovoljne su mi RestAssured i testng
    @Test
    public void healthCheck() {
        // Za pocetak je potrebno pozvati biblioteku RestAssured
        // i podesiti baseURI koji ce se automatski podesiti u endpoint

        RestAssured.baseURI = "https://restful-booker.herokuapp.com";

        // RestAssured komande koriste Gherkin sintaksu - given, when, then
        // Najmanje sta nam je potrebno za slanje requesta je
        when().get("/ping");

        // Posle when komande biramo koju cemo metodu koristiti - get, post, put, patch, delete
        // i na taj poziv prosledjujemo ostatak endpointa

        // Sa given komandom pocinjemo request i dodajemo log all kako bismo u konzoli ispisali
        // poslati request
        // log all nije potreban za slanje requeste ali nama pomaze da procitamo sta smo poslali
        // i sta smo dobili kao response
        given().log().all()
                .when().get("/ping");

        // Ako bismo hteli da dodamo asertacije onda moramo ti posle then komande
        // Takodje posle then komande dodajemo log all da bismo ispisali response
        given().log().all()
                .when().get("/ping")
                .then().log().all()
                .assertThat().statusCode(201);
    }

    @Test
    public void getBookingIds() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
        given().log().all()
                .when().get("/booking")
                .then().log().all()
                .assertThat().statusCode(200);
    }

    @Test
    public void createBooking() {
        // Kada kreiramo post metodu potrebno je proslediti header i body
        // U body mozemo da pisemo direktno sta zelimo da unesemo,
        // ali moze da bude neuredno i dugacko

        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
        given().log().all()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"firstname\" : \"Dragoljub\",\n" +
                        "    \"lastname\" : \"Brown\",\n" +
                        "    \"totalprice\" : 99,\n" +
                        "    \"depositpaid\" : true,\n" +
                        "    \"bookingdates\" : {\n" +
                        "        \"checkin\" : \"2018-01-01\",\n" +
                        "        \"checkout\" : \"2019-01-01\"\n" +
                        "    },\n" +
                        "    \"additionalneeds\" : \"Dinner\"\n" +
                        "}")
                .when().post("/booking")
                .then().log().all()
                .assertThat().statusCode(200);
    }

    @Test
    public void getBooking() {
        // Kada zelimo da posaljemo zahtev za get booking treba nam bookingid
        // Da bismo dobili bookingid potrebno je da prvo kreiramo booking
        // I onda iz response-a da izvucemo taj ID
        // Kada bilo sta izvlacimo iz response-a onda treba na kraju da dodamo
        // .extract().response().asString();
        // i na pocetku pozvati da se ceo poziv smesti u neki string
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
        String response =
                given().log().all()
                        .header("Content-Type", "application/json")
                        .body(Payload.bookingBody1())
                        .when().post("/booking")
                        .then().log().all()
                        .assertThat().statusCode(200)
                        .extract().response().asString();

        System.out.println("RESPONSE: " + response);
        // JsonPath nam pomaze da procita response i da izvlacimo neke delove
        // kao na primer bookingid u ovom slucaju
        JsonPath js1 = new JsonPath(response);
        String bookingID = js1.getString("bookingid");
        System.out.println("ID: " + bookingID);
        Assert.assertTrue(response.contains(bookingID));

        // Sad kad imamo bookingid, mozemo da procitamo taj booking
        // tako sto ovaj ID prosledimo u endpoint
        String responseAfterGetMethod =
                given().log().all()
                .when().get("/booking/"+bookingID)
                .then().log().all()
                .assertThat().statusCode(200)
                .extract().response().asString();

        // JsonPath nam je takodje potreban kod asertacija
        JsonPath js2 = new JsonPath(responseAfterGetMethod);

        Assert.assertFalse(js2.getString("firstname").isBlank());
        Assert.assertFalse(js2.getString("lastname").isBlank());
        Assert.assertFalse(js2.getString("totalprice").isBlank());
        Assert.assertFalse(js2.getString("depositpaid").isBlank());
        Assert.assertFalse(js2.getString("bookingdates").isBlank());
        // Kada zelimo da dohvatimo objekat u response-u potrebno je pozvati prvo parent element
        Assert.assertFalse(js2.getString("bookingdates.checkin").isBlank());
        Assert.assertFalse(js2.getString("bookingdates.checkout").isBlank());
        Assert.assertFalse(js2.getString("additionalneeds").isBlank());
    }

    @Test
    public void updateBooking() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";

        // Kreiram booking

        String response =
                given().log().all()
                        .header("Content-Type", "application/json")
                        .body(Payload.bookingBody1())
                        .when().post("/booking")
                        .then().log().all()
                        .assertThat().statusCode(200)
                        .extract().response().asString();

        JsonPath js1 = new JsonPath(response);
        String bookingID = js1.getString("bookingid");

        String tokenResponse =
        given().log().all()
                .header("Content-Type", "application/json")
                .body(Payload.tokenBody())
                .when().post("/auth")
                .then().log().all()
                .assertThat().statusCode(200)
                .extract().response().asString();

        JsonPath js2 = new JsonPath(tokenResponse);
        String token = js2.getString("token");

        // Kreiram update

        // Kod requestova gde je potreban token mozemo upisati vise od jednog header-a
        String updateResponse =
        given().log().all()
                .header("Content-Type", "application/json")
                .header("Cookie", "token="+token)
                .body(Payload.bookingBody2())
                .when().put("/booking/"+bookingID)
                .then().log().all()
                .assertThat().statusCode(200)
                .extract().response().asString();

        JsonPath js3 = new JsonPath(updateResponse);
        Assert.assertEquals(js3.getString("totalprice"), "50");
        Assert.assertEquals(js3.getString("depositpaid"), "false");
        Assert.assertEquals(js3.getString("bookingdates.checkout"), "2020-01-01");
        Assert.assertEquals(js3.getString("additionalneeds"), "Breakfast");
    }

    @Test
    public void partialUpdateBooking() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";

        // Kreiram booking

        String response =
                given().log().all()
                        .header("Content-Type", "application/json")
                        .body(Payload.bookingBody1())
                        .when().post("/booking")
                        .then().log().all()
                        .assertThat().statusCode(200)
                        .extract().response().asString();

        JsonPath js1 = new JsonPath(response);
        String bookingID = js1.getString("bookingid");

        // Kreiram token

        String tokenResponse =
                given().log().all()
                        .header("Content-Type", "application/json")
                        .body(Payload.tokenBody())
                        .when().post("/auth")
                        .then().log().all()
                        .assertThat().statusCode(200)
                        .extract().response().asString();

        JsonPath js2 = new JsonPath(tokenResponse);
        String token = js2.getString("token");

        // Kreiram update

        String updateResponse =
                given().log().all()
                        .header("Content-Type", "application/json")
                        .header("Cookie", "token="+token)
                        .body("{\n" +
                                "    \"totalprice\" : 499\n" +
                                "}")
                        .when().patch("/booking/"+bookingID)
                        .then().log().all()
                        .assertThat().statusCode(200)
                        .extract().response().asString();

        JsonPath js3 = new JsonPath(updateResponse);
        Assert.assertEquals(js3.getString("totalprice"), "499");
    }

    @Test
    public void deleteBooking() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
        String response =
                given().log().all()
                        .header("Content-Type", "application/json")
                        .body(Payload.bookingBody1())
                        .when().post("/booking")
                        .then().log().all()
                        .assertThat().statusCode(200)
                        .extract().response().asString();

        JsonPath js1 = new JsonPath(response);
        String bookingID = js1.getString("bookingid");

        /*String tokenResponse =
                given().log().all()
                        .header("Content-Type", "application/json")
                        .body(Payload.tokenBody())
                        .when().post("/auth")
                        .then().log().all()
                        .assertThat().statusCode(200)
                        .extract().response().asString();

        JsonPath js2 = new JsonPath(tokenResponse);
        String token = js2.getString("token");*/

        // Token izvlacim iz Payload klase, da ne bih morao da posebno pozivam token request

        given().log().all()
                .header("Cookie", "token="+Payload.token())
                .when().delete("/booking/"+bookingID)
                .then().log().all()
                .assertThat().statusCode(201);
    }


}
