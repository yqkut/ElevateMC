package com.elevatemc.equeue.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class SortedList<E> extends ArrayList<E>
{
    private Comparator<E> comparator;

    public SortedList(final Comparator<E> comparator)
    {
        this.comparator = comparator;
    }

    @Override
    public boolean add(E e) {
        super.add(e);
        Collections.sort(this, comparator);
        return true;
    }

    @Override
    public void add(int index, E element) {
        super.add(index, element);
        Collections.sort(this, comparator);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean returned = super.addAll(c);
        Collections.sort(this, comparator);
        return returned;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        boolean returned = super.addAll(index, c);
        Collections.sort(this, comparator);
        return returned;
    }
}