package com.elevatemc.elib.nametag.construct;

import com.elevatemc.elib.nametag.provider.NameTagProvider;
import com.google.common.primitives.Ints;

import java.util.Comparator;

public class NameTagComparator implements Comparator<NameTagProvider> {

    public int compare(NameTagProvider a,NameTagProvider b) {
        return Ints.compare(b.getWeight(), a.getWeight());
    }

}
