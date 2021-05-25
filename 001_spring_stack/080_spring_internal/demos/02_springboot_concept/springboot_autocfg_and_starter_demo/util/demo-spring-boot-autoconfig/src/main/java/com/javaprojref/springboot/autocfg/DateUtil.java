package com.javaprojref.springboot.autocfg;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;

public class DateUtil {
    @Autowired
    private UtilProperties props;

    public String getLocalTime() {
        // 除了joda time，也可以使用Spring自己的类
        int offsetHours = 0;
        if (null != props.getLongitude()) {
            offsetHours = (int)Math.round((props.getLongitude() * DateTimeConstants.HOURS_PER_DAY) / 360);
        }
        DateTimeZone dz = DateTimeZone.forOffsetHours(offsetHours);
        return new DateTime(dz).toString(props.getPattern());
    }
}
