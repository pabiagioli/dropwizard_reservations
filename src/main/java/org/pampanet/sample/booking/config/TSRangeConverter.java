package org.pampanet.sample.booking.config;

import org.jooq.Converter;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TSRangeConverter implements Converter<Object, LocalDateTime[]> {
    private static final Pattern PATTERN =
            Pattern.compile("\\((.*?),(.*?)\\)");

    @Override
    public LocalDateTime[] from(Object databaseObject) {
        if(databaseObject == null)
            return null;
        Matcher m = PATTERN.matcher(databaseObject.toString());
        if (m.find()){
            return new LocalDateTime []{
                    LocalDateTime.parse(m.group(1)),
                    LocalDateTime.parse(m.group(2))
            };
        }
        return new LocalDateTime[0];
    }

    @Override
    public Object to(LocalDateTime[] userObject) {
        if(userObject == null)
            return null;

        return String.format("(%s,%s)", userObject[0], userObject[1]);
    }

    @Override
    public Class<Object> fromType() {
        return Object.class;
    }

    @Override
    public Class<LocalDateTime[]> toType() {
        return (LocalDateTime[].class);
    }
}