/*******************************************************************************
 * Copyright 2019 UIA
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package uia.utils.cube;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Cube implementation.
 *
 * @author Kyle K. Lin
 *
 * @param <T> Type of value.
 */
public class StreamCube<T> implements Cube<T> {

    private final Stream<Data<T>> data;

    StreamCube(Stream<Data<T>> data) {
        this.data = data;
    }

    @Override
    public T single() {
        Optional<Data<T>> opt = this.data.findFirst();
        return opt.isPresent() ? opt.get().value : null;
    }

    @Override
    public Stream<T> values() {
        return this.data.map(t -> t.value);
    }

    @Override
    public Map<String, List<T>> valuesMapping(final String tagName) {
        return this.data.collect(
                Collectors.groupingBy(
                        d -> d.getTag(tagName),
                        Collectors.mapping(v -> v.value, Collectors.toList())));
    }

    @Override
    public <R> Map<String, List<R>> valuesMapping(String tagName, Function<T, R> f) {
        return this.data.collect(
                Collectors.groupingBy(
                        d -> d.getTag(tagName),
                        Collectors.mapping(v -> f.apply(v.value), Collectors.toList())));
    }

    @Override
    public Map<String, T> singleMapping(final String tagName) {
        TreeMap<String, T> result = new TreeMap<>();
        valuesMapping(tagName).forEach((k, v) -> result.put(k, v.get(0)));
        return result;
    }

    @Override
    public <R> Map<String, R> singleMapping(String tagName, Function<T, R> f) {
        TreeMap<String, R> result = new TreeMap<>();
        valuesMapping(tagName).forEach((k, v) -> result.put(k, f.apply(v.get(0))));
        return result;
    }

    @Override
    public Cube<T> select(String tagName, String tagValue) {
        return new StreamCube<T>(this.data.filter(d -> tagValue.equals(d.getTag(tagName))));
    }

    @Override
    public Cube<T> selectNot(String tagName, String tagValue) {
        return new StreamCube<T>(this.data.filter(d -> !tagValue.equals(d.getTag(tagName))));
    }

    @Override
    public Cube<T> select(Function<Data<T>, Boolean> function) {
        return new StreamCube<>(this.data.filter(function::apply));
    }

    @Override
    public Map<String, Cube<T>> cubes(String tagName) {
        final TreeMap<String, Cube<T>> result = new TreeMap<>();
        Map<String, List<Data<T>>> raw = this.data.collect(Collectors.groupingBy(d -> d.getTag(tagName)));
        raw.forEach((k, v) -> result.put(k, new ListCube<T>(v)));
        return result;
    }
}
