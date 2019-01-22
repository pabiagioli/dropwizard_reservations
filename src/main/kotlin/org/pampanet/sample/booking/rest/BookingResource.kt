package org.pampanet.sample.booking.rest

import org.jooq.DSLContext
import org.pampanet.sample.booking.gen.tables.pojos.Reservations
import org.pampanet.sample.booking.service.BookingService
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType

@Path("/booking")
class BookingResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun sayHello() : String {
        return "Hello World"
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    fun isAvailable(@QueryParam("startDate") startDate: String, @QueryParam("endDate") endDate: String,
                    @Context dslContext: DSLContext): Boolean {

        val bookingManager = BookingService(dslContext)
        var days: Long = 0
        if (startDate == null || endDate == null) {
            val today = LocalDate.now().atStartOfDay()
            days = Duration.between(today, today.plusMonths(1)).toDays()
        } else {
            days = Duration.between(LocalDateTime.parse(startDate), LocalDateTime.parse(endDate)).toDays()
        }

        val isValidRange = bookingManager.validateRange(LocalDateTime.parse(startDate), LocalDateTime.parse(endDate))
        if (!isValidRange)
            return false

        for (i in 0 until days) {
            if (!bookingManager.isAvailable(LocalDateTime.parse(startDate).plusDays(i)))
                return false
        }

        return true
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    fun makeReservation(reservations: Reservations,
                        @Context dslContext: DSLContext) : UUID? {
        return BookingService(dslContext).makeReservation(reservations)
    }


    @DELETE
    fun clearDB(@Context dslContext: DSLContext){
        dslContext.delete(org.pampanet.sample.booking.gen.tables.Reservations.RESERVATIONS).execute()
    }
}