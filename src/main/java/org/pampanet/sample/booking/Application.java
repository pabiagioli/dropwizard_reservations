package org.pampanet.sample.booking;

import com.bendb.dropwizard.jooq.JooqBundle;
import com.bendb.dropwizard.jooq.JooqFactory;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.pampanet.sample.booking.config.BookingConfig;
import org.pampanet.sample.booking.rest.BookingResource;

public class Application extends io.dropwizard.Application<BookingConfig> {


    public static void main(String [] args) throws Exception {
        new Application().run(args);
    }

    @Override
    public void run(BookingConfig configuration, Environment environment) throws Exception {

        environment.jersey().register(new BookingResource());
    }

    @Override
    public void initialize(Bootstrap<BookingConfig> bootstrap) {
        super.initialize(bootstrap);
        bootstrap.addBundle(new JooqBundle<BookingConfig>() {
            @Override
            public DataSourceFactory getDataSourceFactory(BookingConfig configuration) {
                return configuration.getDbFactory();
            }

            @Override
            public JooqFactory getJooqFactory(BookingConfig configuration) {
                return configuration.getJooqFactory();
            }
        });
    }
}
