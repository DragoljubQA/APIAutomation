package BookingPOJO.PojoClasses;

import com.github.javafaker.Faker;

public class CreateBooking {

    private String firstname;
    private String lastname;
    private int totalprice;
    private boolean depositpaid;
    private Object bookingdates;
    private String additionalneeds;
    static Faker faker = new Faker();
    public static CreateBooking payload;
    public static BookingDates bookingDates = new BookingDates();

    public static Object setRandomPayload() {
        payload = new CreateBooking();

        String checkinMonth = String.valueOf(faker.number().numberBetween(01,12));
        String checkinDay = String.valueOf(faker.number().numberBetween(01,30));
        String year = String.valueOf(faker.number().numberBetween(2025,2035));
        String checkoutMonth = String.valueOf(faker.number().numberBetween(Integer.parseInt(checkinMonth),12));
        String checkoutDay = String.valueOf(faker.number().numberBetween(Integer.parseInt(checkinDay),30));

        if (Integer.parseInt(checkinMonth) < 10) {
            checkinMonth = "0" + checkinMonth;
        }
        if (Integer.parseInt(checkinDay) < 10) {
            checkinDay = "0" + checkinDay;
        }
        if (Integer.parseInt(checkoutMonth) < 10) {
            checkoutMonth = "0" + checkoutMonth;
        }
        if (Integer.parseInt(checkinDay) < 10) {
            checkoutDay = "0" + checkoutDay;
        }
        payload.setFirstname(faker.name().firstName());
        payload.setLastname(faker.name().lastName());
        payload.setTotalprice(faker.number().numberBetween(100, 1000));
        payload.setDepositpaid(faker.bool().bool());
        bookingDates.setCheckin(year+"-"+checkinMonth+"-"+checkinDay);
        bookingDates.setCheckout(year+"-"+checkoutMonth+"-"+checkoutDay);
        payload.setBookingdates(bookingDates);
        payload.setAdditionalneeds(faker.food().dish());
        return payload;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(int totalprice) {
        this.totalprice = totalprice;
    }

    public boolean isDepositpaid() {
        return depositpaid;
    }

    public void setDepositpaid(boolean depositpaid) {
        this.depositpaid = depositpaid;
    }

    public Object getBookingdates() {
        return bookingdates;
    }

    public void setBookingdates(Object bookingdates) {
        this.bookingdates = bookingdates;
    }

    public String getAdditionalneeds() {
        return additionalneeds;
    }

    public void setAdditionalneeds(String additionalneeds) {
        this.additionalneeds = additionalneeds;
    }
}
