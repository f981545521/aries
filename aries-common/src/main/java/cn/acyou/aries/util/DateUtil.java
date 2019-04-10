package cn.acyou.aries.util;

import org.joda.time.DateTime;

/**
 * @author youfang
 * @version [1.0.0, 2019-04-10 上午 11:58]
 * @since [司法公证]
 **/
public class DateUtil {
    public static void main(String[] args) {
        DateTime dateTime = new DateTime();
        System.out.println(dateTime.toLocalDate());
    }
}
