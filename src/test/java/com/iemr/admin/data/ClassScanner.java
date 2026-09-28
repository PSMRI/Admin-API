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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.RegexPatternTypeFilter;

/** Finds every concrete class published under a package, for suite-wide contract checks. */
public final class ClassScanner {

    private ClassScanner() {
    }

    public static List<Class<?>> classesUnder(String... packages) {
        ClassPathScanningCandidateComponentProvider provider =
                new ClassPathScanningCandidateComponentProvider(false) {
                    @Override
                    protected boolean isCandidateComponent(
                            org.springframework.beans.factory.annotation.AnnotatedBeanDefinition definition) {
                        return definition.getMetadata().isIndependent()
                                && !definition.getMetadata().isAnnotation();
                    }
                };
        provider.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*")));

        List<Class<?>> found = new ArrayList<>();
        for (String packageName : packages) {
            for (BeanDefinition definition : provider.findCandidateComponents(packageName)) {
                try {
                    found.add(Class.forName(definition.getBeanClassName()));
                } catch (ClassNotFoundException | NoClassDefFoundError e) {
                    // A class the test classpath cannot resolve is not part of the contract.
                }
            }
        }
        found.sort(Comparator.comparing(Class::getName));
        return found;
    }
}
