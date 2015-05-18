/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.util;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 * 
 */
public final class ClassHelper {

    private static final Logger LOG = LoggerFactory.getLogger(ClassHelper.class);

    private ClassHelper() {
    }

    /**
     * Calculates class similarity based on hierarchy depth.
     * 
     * @param superClass
     * @param clazz
     * @return 0 for equality, -1 for non-hierarchy classes, >0 the lower the
     *         more similiar
     */
    public static int getSimiliarity(Class<?> superClass, Class<?> clazz) {
        if (clazz.isArray()) {
            if (!superClass.isArray()) {
                return -1;
            } else {
                return getSimiliarity(superClass.getComponentType(), clazz.getComponentType());
            }
        }
        if (superClass == clazz) {
            return 0;
        } else {

            int difference = -1;
            if (clazz.getSuperclass() != null) {
                difference = getSimiliarity1(superClass, clazz.getSuperclass(), -1);
            }
            if (difference != 0 && superClass.isInterface()) {
                for (Class<?> i : clazz.getInterfaces()) {
                    difference = getSimiliarity1(superClass, i, difference);
                    if (difference == 0) {
                        break;
                    }
                }
            }
            return difference < 0 ? -1 : 1 + difference;
        }
    }

    private static int getSimiliarity1(Class<?> superClass, Class<?> clazz, int difference) {
        if (superClass.isAssignableFrom(clazz)) {
            int cd = getSimiliarity(superClass, clazz);
            return (cd >= 0) ? ((difference < 0) ? cd : Math.min(difference, cd)) : difference;
        } else {
            return difference;
        }
    }

    @SuppressWarnings("unchecked")
    protected static <T> Set<Class<? extends T>> flattenPartialHierachy(Set<Class<? extends T>> alreadyFoundClasses,
            Class<T> limitingClass, Class<?> currentClass) {
        if (limitingClass.isAssignableFrom(currentClass)) {
            alreadyFoundClasses.add((Class<? extends T>) currentClass);
            if (limitingClass.isInterface()) {
                for (Class<?> c : currentClass.getInterfaces()) {
                    if (limitingClass.isAssignableFrom(c)) {
                        alreadyFoundClasses.add((Class<? extends T>) c);
                    }
                }
            }
            Class<?> superClass = currentClass.getSuperclass();
            if (superClass != null) {
                return flattenPartialHierachy(alreadyFoundClasses, limitingClass,
                        (Class<?>) currentClass.getSuperclass());
            } else {
                return alreadyFoundClasses;
            }
        } else {
            return alreadyFoundClasses;
        }
    }

    public static <T> Set<Class<? extends T>> flattenPartialHierachy(Class<T> limitingClass,
            Class<? extends T> actualClass) {
        Set<Class<? extends T>> classes =
                flattenPartialHierachy(new HashSet<Class<? extends T>>(), limitingClass, actualClass);
        LOG.debug("Flatten class hierarchy for {} extending/implementing {}; Found: {}", actualClass, limitingClass,
                Joiner.on(", ").join(classes));
        return classes;
    }
}
