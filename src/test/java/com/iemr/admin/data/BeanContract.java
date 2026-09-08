/*
* AMRIT - Accessible Medical Records via Integrated Technologies
* Integrated EHR (Electronic Health Records) Solution
*
* Copyright (C) "Piramal Swasthya Management and Research Institute"
*
* This file is part of AMRIT.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see https://www.gnu.org/licenses/.
*/
package com.iemr.admin.data;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Reflective checks for the accessors, equality and string representation of the
 * plain data carriers this service exchanges over its APIs.
 *
 * <p>The carriers are mostly Lombok {@code @Data} entities, so the generated
 * {@code equals}, {@code hashCode} and {@code toString} are exercised alongside
 * every readable/writable property. A carrier that cannot be built through a
 * no-argument constructor is reported by {@link #isVerifiable(Class)} so the
 * calling suite can skip it rather than fail.
 */
public final class BeanContract {

    private BeanContract() {
    }

    /** Answers whether the type can be exercised through the reflective contract. */
    public static boolean isVerifiable(Class<?> type) {
        if (type.isInterface() || type.isEnum() || type.isAnnotation()
                || Modifier.isAbstract(type.getModifiers())
                || type.isMemberClass() && !Modifier.isStatic(type.getModifiers())) {
            return false;
        }
        try {
            type.getDeclaredConstructor();
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public static void verify(Class<?> type) throws Exception {
        Object left = newInstance(type);
        Object right = newInstance(type);
        copyFields(type, left, right);

        assertNotNull(left.toString(), type.getSimpleName() + " must render a string form");
        assertTrue(left.equals(left), type.getSimpleName() + " must equal itself");
        assertFalse(left.equals(null), type.getSimpleName() + " must never equal null");
        assertFalse(left.equals(new Object()), type.getSimpleName() + " must never equal an unrelated type");
        left.hashCode();

        if (left.equals(right)) {
            assertEquals(left.hashCode(), right.hashCode(),
                    type.getSimpleName() + " must hash consistently with equals");
        }

        verifyProperties(type, left, right);
    }

    private static Object newInstance(Class<?> type) throws Exception {
        java.lang.reflect.Constructor<?> constructor = type.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    private static void verifyProperties(Class<?> type, Object left, Object right) throws Exception {
        for (Field field : type.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                continue;
            }
            Method getter = findGetter(type, field);
            Method setter = findSetter(type, field);
            if (getter == null || setter == null) {
                continue;
            }
            Object value = sampleValue(field.getType());
            if (value == null) {
                continue;
            }

            setter.invoke(left, value);
            assertEquals(value, getter.invoke(left),
                    type.getSimpleName() + "." + field.getName() + " must round-trip through its accessors");

            setter.invoke(right, value);
            assertEquals(getter.invoke(left), getter.invoke(right),
                    type.getSimpleName() + "." + field.getName() + " must read back the same on both instances");
        }
        assertNotNull(left.toString(), type.getSimpleName() + " must render a populated string form");
        left.hashCode();
        left.equals(right);
    }

    private static void copyFields(Class<?> type, Object from, Object to) throws Exception {
        for (Field field : type.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            field.set(to, field.get(from));
        }
    }

    private static Method findGetter(Class<?> type, Field field) {
        String suffix = capitalise(field.getName());
        for (String prefix : new String[] { "get", "is" }) {
            try {
                Method candidate = type.getMethod(prefix + suffix);
                if (candidate.getParameterCount() == 0) {
                    return candidate;
                }
            } catch (NoSuchMethodException ignored) {
                // try the next accessor style
            }
        }
        return null;
    }

    private static Method findSetter(Class<?> type, Field field) {
        try {
            return type.getMethod("set" + capitalise(field.getName()), field.getType());
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static String capitalise(String name) {
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    private static Object sampleValue(Class<?> type) {
        if (type == String.class) {
            return "sample";
        }
        if (type == Long.class || type == long.class) {
            return 7L;
        }
        if (type == Integer.class || type == int.class) {
            return 7;
        }
        if (type == Short.class || type == short.class) {
            return (short) 7;
        }
        if (type == Double.class || type == double.class) {
            return 7.5d;
        }
        if (type == Float.class || type == float.class) {
            return 7.5f;
        }
        if (type == Character.class || type == char.class) {
            return 'y';
        }
        if (type == Byte.class || type == byte.class) {
            return (byte) 7;
        }
        if (type == Boolean.class || type == boolean.class) {
            return Boolean.TRUE;
        }
        if (type == java.math.BigDecimal.class) {
            return java.math.BigDecimal.valueOf(7.5d);
        }
        if (type == BigInteger.class) {
            return BigInteger.valueOf(7L);
        }
        if (type == Timestamp.class) {
            return Timestamp.valueOf("2026-02-17 09:30:00");
        }
        if (type == Date.class) {
            return Date.valueOf("2026-02-17");
        }
        if (type == Time.class) {
            return Time.valueOf("09:30:00");
        }
        if (type == java.util.Date.class) {
            return new java.util.Date(1_771_286_400_000L);
        }
        if (type == LocalDate.class) {
            return LocalDate.of(2026, 2, 17);
        }
        if (type == LocalTime.class) {
            return LocalTime.of(9, 30);
        }
        if (type == LocalDateTime.class) {
            return LocalDateTime.of(2026, 2, 17, 9, 30);
        }
        if (type == List.class) {
            return new ArrayList<>(List.of("first", "second"));
        }
        if (type == Set.class) {
            return new HashSet<>(Set.of("first"));
        }
        if (type == Map.class) {
            return new HashMap<>(Map.of("key", "value"));
        }
        if (type == Object.class) {
            return "sample";
        }
        if (type.isPrimitive() || type.isEnum() || type.isArray() || type.isInterface()) {
            return null;
        }
        try {
            java.lang.reflect.Constructor<?> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException | SecurityException e) {
            // A carrier that needs arguments is simply left at its default value.
            return null;
        }
    }
}
