package com.example.dataType;

import java.util.Collection;
import java.util.LinkedList;

/**
 * <h1>有序去重list</h1>
 * Created by hanqf on 2022/6/27 14:32.
 */


public class DeduplicationList<E> extends LinkedList<E> {


    private static final long serialVersionUID = -488961948974598940L;

    @Override
    public boolean add(E e) {
        if (size() == 0) {
            return super.add(e);
        } else {
            int count = 0;
            for (E t : this) {
                if (t.equals(e)) {
                    count++;
                    break;
                }
            }
            if (count == 0) {
                return super.add(e);
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (size() == 0) {
            return super.addAll(c);
        } else {
            for (E e : c) {
                this.add(e);
            }
            return true;
        }
    }
}
