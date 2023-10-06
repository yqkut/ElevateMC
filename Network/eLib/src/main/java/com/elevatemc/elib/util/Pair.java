package com.elevatemc.elib.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Pair<K,V>{

    @Getter private K key;
    @Getter private V value;

}
