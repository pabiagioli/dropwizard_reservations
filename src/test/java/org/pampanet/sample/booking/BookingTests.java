package org.pampanet.sample.booking;

import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.*;
import org.pampanet.sample.booking.config.BookingConfig;
import org.pampanet.sample.booking.gen.tables.pojos.Reservations;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BookingTests {


    LocalDateTime today = LocalDate.now().atStartOfDay();
    LocalDateTime monthFromNow = today.plusMonths(1);


    @ClassRule
    public static final DropwizardAppRule<BookingConfig> RULE =
            new DropwizardAppRule<>(Application.class,
                    ResourceHelpers.resourceFilePath("dev.yml"));

    Client client = new JerseyClientBuilder(RULE.getEnvironment()).build("test client");

    @Test
    public void testAlreadyBooked(){
        Reservations pablo = new Reservations(UUID.randomUUID(), new LocalDateTime [] {today.plusDays(2), today.plusDays(4)}, "Pablo");
        Reservations fede = new Reservations(UUID.randomUUID(), new LocalDateTime [] {today.plusDays(4), today.plusDays(6)}, "Fede");
        Reservations mom = new Reservations(UUID.randomUUID(), new LocalDateTime [] {today.plusDays(2), today.plusDays(5)}, "Mom");

        List<UUID> ids = Stream.of(pablo, fede, mom)
                .map(dto->Entity.entity(dto, MediaType.APPLICATION_JSON_TYPE))
                .map(reservationsEntity -> client.target(
                        String.format("http://localhost:%d/booking", RULE.getLocalPort()))
                        .request().post(reservationsEntity))
                .filter(Response::hasEntity)
                .map(response -> response.readEntity(UUID.class))
                .collect(Collectors.toList());

        //Mom couldn't book the place
        Assert.assertFalse(ids.contains(mom.getId()));

        List<Boolean> availables = Stream.of(today.plusDays(2), today.plusDays(3), today.plusDays(15))
                .parallel()
                .map(localDateTime -> client.target(String.format("http://localhost:%d/booking", RULE.getLocalPort()))
                        .queryParam("date", localDateTime.toString())
                        .request()
                        .get())
                .filter(Response::hasEntity)
                .map(response -> response.readEntity(Boolean.class))
                .collect(Collectors.toList());

        // (today + 2) and (today + 3) should not be available
        Assert.assertTrue(availables.stream().filter(val-> !val).count() == 2);
        Assert.assertTrue(availables.stream().filter(val-> val).count() == 1);

    }

    @Test
    public void integrationTest() {

        Reservations pabloDTO = new Reservations(UUID.randomUUID(),new LocalDateTime[]{ today.plusDays(13), today.plusDays(16)}, "Pablo");
        Reservations noWayDTO = new Reservations(UUID.randomUUID(),new LocalDateTime[]{ today.plusDays(13), today.plusDays(16)}, "NoWay");
        Reservations fedeDTO = new Reservations(UUID.randomUUID(),new LocalDateTime[]{ today.plusDays(13), today.plusDays(16)}, "fede");

        List<UUID> ids = Stream.of(pabloDTO, noWayDTO, fedeDTO)
                .map(dto->Entity.entity(dto, MediaType.APPLICATION_JSON_TYPE))
                .parallel()
                .map(reservationsEntity -> client.target(
                        String.format("http://localhost:%d/booking", RULE.getLocalPort()))
                        .request().post(reservationsEntity))
                .filter(Response::hasEntity)
                .map(response -> response.readEntity(UUID.class))
                .collect(Collectors.toList());

        Assert.assertTrue(!ids.isEmpty());
        Assert.assertEquals(1, ids.size());
    }

    @Before
    @After
    public void tearDown(){
        client.target(String.format("http://localhost:%d/booking", RULE.getLocalPort())).request().delete();
    }

}
