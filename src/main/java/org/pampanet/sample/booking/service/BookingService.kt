package org.pampanet.sample.booking.service

import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.pampanet.sample.booking.gen.tables.Reservations
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class BookingService (val dslContext: DSLContext) {

    fun validateRange (start: LocalDateTime, end: LocalDateTime): Boolean {
        val days = Duration.between(start, end).toDays()
        val today = LocalDate.now().atStartOfDay()
        val upperBound = today.plusDays(2).plusMonths(1)
        return days <= 3L && start.isAfter(today) && end.isBefore(upperBound)
    }

     fun isAvailable(date:LocalDateTime): Boolean = dslContext.selectFrom(Reservations.RESERVATIONS)
         .where(DSL.condition("lower(during) = {0}", DSL.`val`(date))
             .or("upper(during) = {0}", DSL.`val`(date.plusDays(1)))
             .or("during @> {0}",DSL.`val`(date)))
         .fetch()
         .isEmpty()


    fun makeReservation(reservations: org.pampanet.sample.booking.gen.tables.pojos.Reservations) : UUID? {
        try {
            val id = UUID.randomUUID()
            dslContext.insertInto(Reservations.RESERVATIONS)
                .set(dslContext.newRecord(Reservations.RESERVATIONS, reservations).setId(id))
                .execute()
            return id
        }catch (e:Exception){
            e.printStackTrace()
        }
        return null
    }

}
