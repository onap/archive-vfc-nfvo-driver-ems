/*
 * Copyright 2017 BOCO Corporation.  CMCC Technologies Co., Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onap.vfc.nfvo.emsdriver.commons.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {


    public static long[] getScanScope(Date fireTime, long collectPeriod) {
        Calendar fire = Calendar.getInstance();
        long start = 0L;
        long end = 0L;
        fire.setTime(fireTime);
        fire.set(Calendar.SECOND, 0);
        fire.set(Calendar.MILLISECOND, 0);

        if (collectPeriod < 3600) {//minute
            long minite = fire.get(Calendar.MINUTE);
            long collectMinite = (int) collectPeriod / 60;
            long s = minite % collectMinite;
            end = fire.getTimeInMillis() - s * 60 * 1000;
            start = end - collectPeriod * 1000;
        } else if (collectPeriod == 3600) {
            fire.set(Calendar.MINUTE, 0);
            end = fire.getTimeInMillis();
            start = end - collectPeriod * 1000;
        } else if (collectPeriod == 24 * 60 * 60) { //day
            fire.set(Calendar.HOUR_OF_DAY, 0);
            fire.set(Calendar.MINUTE, 0);
            end = fire.getTimeInMillis();
            start = end - collectPeriod * 1000;
        } else {

            if (collectPeriod > 0) {
                end = fire.getTimeInMillis() - (fire.getTimeInMillis() + 8 * 60 * 60 * 1000) % (collectPeriod * 1000);
            } else {
                return null;
            }
            start = end - collectPeriod * 1000;
        }

        return new long[]{start, end};
    }

    public static String TimeString(String timeString) {
        if (timeString == null) {
            return "";
        } else {
            timeString = timeString.replace("T", " ");
            if (timeString.contains("+")) {
                timeString = timeString.substring(0, timeString.indexOf("+"));
            }
            return timeString;
        }
    }

    public static String addTime(String srcTimeString, String period) throws ParseException {
        String finaldate = TimeString(srcTimeString);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse(finaldate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, Integer.valueOf(period));
        return sdf.format(calendar.getTime());
    }

}
