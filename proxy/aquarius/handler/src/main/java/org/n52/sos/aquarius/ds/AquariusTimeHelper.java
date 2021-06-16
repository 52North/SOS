package org.n52.sos.aquarius.ds;

import org.joda.time.DateTime;

public interface AquariusTimeHelper {

    String TWENTY_FOUR = "T24:";

    String TWENTY_THREE = "T23:";

    default DateTime checkDateTimeStringFor24(String time) {
        return time.contains(TWENTY_FOUR) ? new DateTime(time.replace(TWENTY_FOUR, TWENTY_THREE)).plusDays(1)
                : new DateTime(time);
    }
}
