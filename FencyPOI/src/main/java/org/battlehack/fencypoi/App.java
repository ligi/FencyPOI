package org.battlehack.fencypoi;

import com.squareup.otto.Bus;

/**
 * Created by ligi on 6/18/13.
 */
public class App {
    private static final Bus BUS = new Bus();

    public static Bus getBus() {
        return BUS;
    }

}
