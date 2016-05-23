/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.sos.util;

import static java.lang.Boolean.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.n52.sos.util.CollectionHelper.unionOfListOfLists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Sets;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * @since 4.0.0
 * 
 */
public class CollectionHelperTest {
    private final Set<String> EMPTY_COLLECTION = new HashSet<String>(0);

    @Test
    public void should_return_empty_list_when_union_receives_null() {
        assertThat(unionOfListOfLists((Set<Set<String>>) null), is(EMPTY_COLLECTION));
    }

    @Test
    public void should_return_empty_list_when_unionOfListOfLists_receives_empty_list() {
        final Collection<? extends Collection<String>> emptyList = new ArrayList<Set<String>>(0);
        assertThat(unionOfListOfLists(emptyList), is(EMPTY_COLLECTION));
    }

    @Test
    public void should_return_union_of_values_without_duplicates() {
        final Collection<String> listA = new ArrayList<String>(2);
        listA.add("A");
        listA.add("B");

        final Collection<String> listB = new ArrayList<String>(4);
        listB.add("B");
        listB.add("C");
        listB.add(null);

        final Collection<String> listC = new ArrayList<String>(2);
        listC.add("");

        final Collection<Collection<String>> col = new ArrayList<Collection<String>>(4);
        col.add(listA);
        col.add(listB);
        col.add(listC);
        col.add(null);
        col.add(new ArrayList<String>(0));

        final Collection<String> check = new HashSet<String>(4);
        check.add("A");
        check.add("B");
        check.add("C");
        check.add("");
        assertThat(unionOfListOfLists(col), is(check));
    }

    @Test
    public void isNotEmpty_should_return_true_if_map_is_not_empty() {
        final Map<String, String> map = new HashMap<String, String>(1);
        map.put("key", "value");
        assertThat(CollectionHelper.isNotEmpty(map), is(TRUE));
    }

    @Test
    public void isNotEmpty_should_return_false_if_map_is_empty() {
        final Map<String, String> map = new HashMap<String, String>(0);
        assertThat(CollectionHelper.isNotEmpty(map), is(FALSE));
    }

    @Test
    public void isEmpty_should_return_true_if_map_is_empty() {
        final Map<String, String> map = new HashMap<String, String>(0);
        assertThat(CollectionHelper.isEmpty(map), is(TRUE));
    }

    @Test
    public void isEmpty_should_return_false_if_map_is_not_empty() {
        final Map<String, String> map = new HashMap<String, String>(1);
        map.put("key", "value");
        assertThat(CollectionHelper.isEmpty(map), is(FALSE));
    }
    
    @Test
    public void should_return_String() {
        String empty = "()";
        String full = "(a,b,c)";
        Set<String> set = Sets.newLinkedHashSet();
        assertThat(CollectionHelper.collectionToString(set), is(empty));
        set.add("a");
        set.add("b");
        set.add("c");
        assertThat(CollectionHelper.collectionToString(set), is(full));
    }
    
    @Test
    public void should_return_set_sorted_by_value() {
    	Map<String,Integer> unsorted = new HashMap<>();
    	unsorted.put("A", 3);
    	unsorted.put("B", 4);
    	unsorted.put("C", 2);
    	unsorted.put("D", 1);
    	Map<String, Integer> sorted = CollectionHelper.sortByValue(unsorted);
    	for (Entry<String, Integer> string : unsorted.entrySet()) {
			if (!sorted.containsKey(string.getKey()) || !sorted.containsValue(string.getValue())){
				fail("sorted set doesn't contain all values of unsorted");
			}
		}
    	Iterator<Integer> iterator = sorted.values().iterator();
    	assertThat(iterator.next(),is(1));
    	assertThat(iterator.next(),is(2));
    	assertThat(iterator.next(),is(3));
    	assertThat(iterator.next(),is(4));
    }
}
