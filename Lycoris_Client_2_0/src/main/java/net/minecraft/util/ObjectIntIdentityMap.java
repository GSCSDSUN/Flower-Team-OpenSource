/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.Lists
 */
package net.minecraft.util;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.IObjectIntIterable;

public class ObjectIntIdentityMap
implements IObjectIntIterable {
    private final IdentityHashMap identityMap = new IdentityHashMap(512);
    private final List objectList = Lists.newArrayList();
    private static final String __OBFID = "CL_00001203";

    public void put(Object key, int value) {
        this.identityMap.put(key, value);
        while (this.objectList.size() <= value) {
            this.objectList.add(null);
        }
        this.objectList.set(value, key);
    }

    public int get(Object key) {
        Integer integer = (Integer)this.identityMap.get(key);
        return integer == null ? -1 : integer;
    }

    public final Object getByValue(int value) {
        return value >= 0 && value < this.objectList.size() ? this.objectList.get(value) : null;
    }

    @Override
    public Iterator iterator() {
        return Iterators.filter(this.objectList.iterator(), (Predicate)Predicates.notNull());
    }

    public List getObjectList() {
        return this.objectList;
    }
}

