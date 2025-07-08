package BookingPOJO;

import BookingPOJO.PojoClasses.BookingDates;
import BookingPOJO.PojoClasses.CreateBooking;
import BookingPOJO.PojoClasses.CreateToken;
import BookingPOJO.PojoClasses.Variables;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static BookingPOJO.PojoClasses.CreateBooking.payload;
import static io.restassured.RestAssured.*;

public class BookingPojoTest {
    String pingParameter;
    String tokenParameter;
    String bookingParameter;
    CreateBooking createBooking = new CreateBooking();
    BookingDates bookingDates = new BookingDates();
    CreateToken createToken = new CreateToken();
    Variables variables = new Variables();
    @BeforeClass
    public void setUp() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com/";
        pingParameter = "ping";
        tokenParameter = "auth";
        bookingParameter = "booking";
        createToken();
    }

    public void createToken() {
        createToken.setUsername("admin");
        createToken.setPassword("password123");

        String tokenResponse =
        given().log().all()
                .header("Content-Type", "application/json")
                .body(createToken)
                .when().post(tokenParameter)
                .then().log().all()
                .extract().response().asString();

        JsonPath js = new JsonPath(tokenResponse);
        variables.setToken(js.getString("token"));
    }

    @Test
    public void healthCheck() {
        given().log().all()
                .when().get(pingParameter)
                .then().log().all()
                .assertThat().statusCode(201);
    }

    @Test
    public void getAllBookings() {
        String response =
        given().log().all()
                .when().get(bookingParameter)
                .then().log().all()
                .assertThat().statusCode(200)
                .extract().response().asString();

        Assert.assertFalse(response.isEmpty());
    }

    @Test
    public void createBooking() {
        createBooking.setFirstname("Dragoljub");
        createBooking.setLastname("Boranijasevic");
        createBooking.setTotalprice(500);
        createBooking.setDepositpaid(true);
        bookingDates.setCheckin("2020-04-11");
        bookingDates.setCheckout("2020-05-20");
        createBooking.setBookingdates(bookingDates);
        createBooking.setAdditionalneeds("Parking");

        String response =
        given().log().all()
                .header("Content-Type", "application/json")
                .body(createBooking)
                .when().post(bookingParameter)
                .then().log().all()
                .assertThat().statusCode(200)
                .extract().response().asString();

        JsonPath js = new JsonPath(response);
        Assert.assertEquals(js.getString("booking.firstname"), createBooking.getFirstname());
        Assert.assertEquals(js.getString("booking.lastname"), createBooking.getLastname());
        Assert.assertEquals(js.getInt("booking.totalprice"), createBooking.getTotalprice());
        Assert.assertEquals(js.getBoolean("booking.depositpaid"), createBooking.isDepositpaid());
        Assert.assertEquals(js.getString("booking.bookingdates.checkin"), bookingDates.getCheckin());
        Assert.assertEquals(js.getString("booking.bookingdates.checkout"), bookingDates.getCheckout());
        Assert.assertEquals(js.getString("booking.additionalneeds"), createBooking.getAdditionalneeds());

    }

    @Test
    public void createAnyBooking() {
        String response =
                given().log().all()
                        .header("Content-Type", "application/json")
                        .body(CreateBooking.setRandomPayload())
                        .when().post(bookingParameter)
                        .then().log().all()
                        .assertThat().statusCode(200)
                        .extract().response().asString();

        JsonPath js = new JsonPath(response);
        Assert.assertFalse(js.getString("bookingid").isEmpty());
        Assert.assertEquals(js.getString("booking.firstname"), payload.getFirstname());
        Assert.assertEquals(js.getString("booking.lastname"), payload.getLastname());
        Assert.assertEquals(js.getInt("booking.totalprice"), payload.getTotalprice());
        Assert.assertEquals(js.getBoolean("booking.depositpaid"), payload.isDepositpaid());
        Assert.assertEquals(js.getString("booking.bookingdates.checkin"), CreateBooking.bookingDates.getCheckin());
        Assert.assertEquals(js.getString("booking.bookingdates.checkout"), CreateBooking.bookingDates.getCheckout());
        Assert.assertEquals(js.getString("booking.additionalneeds"), payload.getAdditionalneeds());
    }

    @Test
    public void updateBooking() {

        // Kreiram booking

        String response =
                given().log().all()
                        .header("Content-Type", "application/json")
                        .body(CreateBooking.setRandomPayload())
                        .when().post(bookingParameter)
                        .then().log().all()
                        .assertThat().statusCode(200)
                        .extract().response().asString();

        JsonPath js1 = new JsonPath(response);
        String bookingID = js1.getString("bookingid");

        // Kreiram objekat za promenu bookinga

        createBooking.setFirstname("Dragoljub");
        createBooking.setLastname("Boranijasevic");
        createBooking.setTotalprice(500);
        createBooking.setDepositpaid(true);
        bookingDates.setCheckin("2020-04-11");
        bookingDates.setCheckout("2020-05-20");
        createBooking.setBookingdates(bookingDates);
        createBooking.setAdditionalneeds("Parking");

        // Kreiram request za update

        String updateResponse =
                given().log().all()
                        .header("Content-Type", "application/json")
                        .header("Cookie", "token="+variables.getToken())
                        .body(createBooking)
                        .when().put(bookingParameter+"/"+bookingID)
                        .then().log().all()
                        .assertThat().statusCode(200)
                        .extract().response().asString();

        JsonPath js2 = new JsonPath(updateResponse);
        Assert.assertEquals(js2.getString("firstname"), createBooking.getFirstname());
        Assert.assertEquals(js2.getString("lastname"), createBooking.getLastname());
        Assert.assertEquals(js2.getInt("totalprice"), createBooking.getTotalprice());
        Assert.assertEquals(js2.getBoolean("depositpaid"), createBooking.isDepositpaid());
        Assert.assertEquals(js2.getString("bookingdates.checkin"), bookingDates.getCheckin());
        Assert.assertEquals(js2.getString("bookingdates.checkout"), bookingDates.getCheckout());
        Assert.assertEquals(js2.getString("additionalneeds"), createBooking.getAdditionalneeds());


    }

}
