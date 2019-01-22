package org.pampanet.sample.booking.config;

import com.bendb.dropwizard.jooq.JooqFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.constraints.NotNull;

public class BookingConfig extends Configuration {

    @JsonProperty
    @NotNull
    private JooqFactory jooqFactory = new JooqFactory();

    @JsonProperty
    @NotNull
    private DataSourceFactory dbFactory;


    public DataSourceFactory getDbFactory() {
        return dbFactory;
    }

    public JooqFactory getJooqFactory() {
        return jooqFactory;
    }
}
