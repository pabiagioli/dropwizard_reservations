package org.pampanet.sample.booking.config;

import org.jooq.*;
import org.jooq.impl.DSL;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class TSRangeBinder implements Binding<Object, LocalDateTime[]> {
    @Override
    public Converter<Object, LocalDateTime[]> converter() {
        return new TSRangeConverter();
    }

    @Override
    public void sql(BindingSQLContext<LocalDateTime[]> ctx) throws SQLException {
        ctx.render()
                .visit(DSL.val(ctx.convert(converter()).value()))
                .sql("::tsrange");
    }

    @Override
    public void register(BindingRegisterContext<LocalDateTime[]> ctx) throws SQLException {

    }

    @Override
    public void set(BindingSetStatementContext<LocalDateTime[]> ctx) throws SQLException {

    }

    @Override
    public void set(BindingSetSQLOutputContext<LocalDateTime[]> ctx) throws SQLException {

    }

    @Override
    public void get(BindingGetResultSetContext<LocalDateTime[]> ctx) throws SQLException {

    }

    @Override
    public void get(BindingGetStatementContext<LocalDateTime[]> ctx) throws SQLException {

    }

    @Override
    public void get(BindingGetSQLInputContext<LocalDateTime[]> ctx) throws SQLException {

    }

}
