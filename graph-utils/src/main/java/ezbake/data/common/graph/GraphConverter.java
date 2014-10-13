/*   Copyright (C) 2013-2014 Computer Sciences Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. */

package ezbake.data.common.graph;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkaurelius.titan.core.TitanProperty;
import com.thinkaurelius.titan.core.TitanVertex;
import com.thinkaurelius.titan.graphdb.relations.RelationIdentifier;
import com.tinkerpop.blueprints.Direction;

import ezbake.base.thrift.Date;
import ezbake.base.thrift.DateTime;
import ezbake.base.thrift.Time;
import ezbake.base.thrift.TimeZone;
import ezbake.services.graph.thrift.types.DataType;
import ezbake.services.graph.thrift.types.Edge;
import ezbake.services.graph.thrift.types.Element;
import ezbake.services.graph.thrift.types.ElementId;
import ezbake.services.graph.thrift.types.PropValue;
import ezbake.services.graph.thrift.types.Property;
import ezbake.services.graph.thrift.types.TitanId;
import ezbake.services.graph.thrift.types.Vertex;

@SuppressWarnings({"unchecked", "CastToConcreteClass", "ChainOfInstanceofChecks", "InstanceofInterfaces"})
public final class GraphConverter {
    private static final Map<DataType, Class<?>> JAVA_TYPES = Maps.newHashMap();

    static {
        JAVA_TYPES.put(DataType.BOOL, Boolean.class);
        JAVA_TYPES.put(DataType.I32, Integer.class);
        JAVA_TYPES.put(DataType.I64, Long.class);
        JAVA_TYPES.put(DataType.DOUBLE, Double.class);
        JAVA_TYPES.put(DataType.STRING, String.class);
        JAVA_TYPES.put(DataType.BINARY, Object.class);
        JAVA_TYPES.put(DataType.DATE_TIME, java.util.Date.class);
        JAVA_TYPES.put(DataType.COORDINATE, Coordinate.class);
        JAVA_TYPES.put(DataType.LIST_I64, ArrayList.class);
        JAVA_TYPES.put(DataType.LIST_DOUBLE, ArrayList.class);
        JAVA_TYPES.put(DataType.LIST_STRING, ArrayList.class);
        JAVA_TYPES.put(DataType.LIST_BINARY, ArrayList.class);
        JAVA_TYPES.put(DataType.LIST_DATE_TIME, ArrayList.class);
        JAVA_TYPES.put(DataType.LIST_COORDINATE, ArrayList.class);
        JAVA_TYPES.put(DataType.MAP_STRING, HashMap.class);
    }

    private GraphConverter() {
    }

    /**
     * Converts the properties of a blueprints Vertex to a Map where key is property name and value is a Property. If a
     * property cannot be mapped to one of the enumerated values in PropValue then an exception is thrown.
     *
     * @param vertex - Blueprints vertex
     * @return Map of property name to property
     */
    public static Map<String, List<Property>> convertProperties(com.tinkerpop.blueprints.Vertex vertex) {
        final Map<String, List<Property>> props = Maps.newHashMap();
        if (vertex != null) {
            final TitanVertex tv = (TitanVertex) vertex;
            for (final String key : tv.getPropertyKeys()) {
                final List<Property> vals = Lists.newArrayList();
                for (final TitanProperty property : tv.getProperties(key)) {
                    vals.add(convertProperty(property.getValue()));
                }
                props.put(key, vals);
            }
        }
        return props;
    }

    /**
     * Converts the properties of a blueprints Edge to a Map where key is property name and value is PropValue. If a
     * property cannot be mapped to one of the enumerated values in PropValue then an exception is thrown.
     *
     * @param edge Blueprints edge
     * @return Map of property name to property
     */
    public static Map<String, Property> convertProperties(com.tinkerpop.blueprints.Edge edge) {
        final Map<String, Property> props = new HashMap<>();
        if (edge != null) {
            for (final String key : edge.getPropertyKeys()) {
                final Object obj = edge.getProperty(key);
                props.put(key, convertProperty(obj));
            }
        }
        return props;
    }

    /**
     * Converts the Blueprints vertices to Thrift vertices. If a property cannot be mapped to one of the enumerated
     * values in PropValue then an exception is thrown.
     *
     * @param blueVertices Blueprints vertices
     * @return List of Thrift vertices
     */
    public static List<Vertex> convertVertices(Iterable<com.tinkerpop.blueprints.Vertex> blueVertices) {
        final List<Vertex> list = new ArrayList<>();

        if (blueVertices != null) {
            for (final com.tinkerpop.blueprints.Vertex vertex : blueVertices) {
                list.add(convertElement(vertex));
            }
        }
        return list;
    }

    /**
     * Converts the Blueprints edges to Thrift edges. If a property cannot be mapped to one of the enumerated values in
     * PropValue then an exception is thrown.
     *
     * @param blueEdges Blueprints edges
     * @return List of Thrift edges
     */
    public static List<Edge> convertEdges(Iterable<com.tinkerpop.blueprints.Edge> blueEdges) {
        final ArrayList<Edge> list = new ArrayList<>();

        if (blueEdges != null) {
            for (final com.tinkerpop.blueprints.Edge edge : blueEdges) {
                list.add(convertElement(edge));
            }
        }
        return list;
    }

    /**
     * Converts a Blueprints vertex to Thrift vertex. If a property cannot be mapped to one of the enumerated values in
     * PropValue then an exception is thrown.
     *
     * @param blueVertex Blueprints vertex
     * @return Thrift vertex
     */
    public static Vertex convertElement(com.tinkerpop.blueprints.Vertex blueVertex) {
        if (blueVertex != null) {
            final Vertex thriftVertex = new Vertex();
            final ElementId id = new ElementId();
            final TitanId ti = new TitanId();
            ti.setVertexId(Long.parseLong(blueVertex.getId().toString()));
            id.setTitanId(ti);
            thriftVertex.setId(id);
            thriftVertex.setProperties(convertProperties(blueVertex));
            return thriftVertex;
        }
        return null;
    }

    /**
     * Converts a Blueprints edge to Thrift edge. If a property cannot be mapped to one of the enumerated values in
     * PropValue then an exception is thrown.
     *
     * @param blueEdge Blueprints edge
     * @return Thrift edge
     */
    public static Edge convertElement(com.tinkerpop.blueprints.Edge blueEdge) {
        if (blueEdge != null) {
            final Edge thriftEdge = new Edge();

            // map the label
            thriftEdge.setLabel(blueEdge.getLabel());

            // map the in vertex
            final String inVertexId = blueEdge.getVertex(Direction.IN).getId().toString();
            final ElementId inVertexElementId = new ElementId();
            final TitanId inVertexTitanId = new TitanId();
            inVertexTitanId.setVertexId(Long.parseLong(inVertexId));
            inVertexElementId.setTitanId(inVertexTitanId);
            thriftEdge.setInVertex(inVertexElementId);

            // map the out vertex
            final String outVertexId = blueEdge.getVertex(Direction.OUT).getId().toString();
            final ElementId outVertexElementId = new ElementId();
            final TitanId outVertexTitanId = new TitanId();
            outVertexTitanId.setVertexId(Long.parseLong(outVertexId));
            outVertexElementId.setTitanId(outVertexTitanId);
            thriftEdge.setOutVertex(outVertexElementId);

            // map the id
            final RelationIdentifier ri = (RelationIdentifier) blueEdge.getId();
            final long[] ids = ri.getLongRepresentation();
            final ArrayList<Long> list = new ArrayList<>();
            for (final long id2 : ids) {
                list.add(id2);
            }

            final ElementId edgeElementId = new ElementId();
            final TitanId edgeTitanId = new TitanId();
            edgeTitanId.setEdgeId(list);
            edgeElementId.setTitanId(edgeTitanId);
            thriftEdge.setId(edgeElementId);

            final Map<String, Property> props = convertProperties(blueEdge);
            thriftEdge.setProperties(props);

            return thriftEdge;
        }
        return null;
    }

    public static Property convertProperty(Object obj) {
        final Property p = new Property();
        p.setValue(convertObject(obj));
        return p;
    }

    /**
     * Converts an object to one of the enumerated values in PropValue. If the object doesn't match one of those, an
     * exception is thrown.
     *
     * @param obj Object to convert to a Thrift property
     * @return Thrift property
     */
    public static PropValue convertObject(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Cannot convert null to PropValue");
        }

        if (obj instanceof List<?>) {
            final List<?> l = (List<?>) obj;
            if (l.isEmpty()) {
                throw new IllegalArgumentException("Cannot map empty list");
            }

            final Class<?> c = l.get(0).getClass();
            for (final Object o : l) {
                if (c != o.getClass()) {
                    throw new IllegalArgumentException(
                            "Cannot convert a list with different object class types to PropValue list");
                }
            }

            if (c == Coordinate.class) {
                final List<Coordinate> coords = (List<Coordinate>) obj;
                final List<ezbake.base.thrift.Coordinate> thriftCoords = new ArrayList<>(coords.size());
                for (final Coordinate coord : coords) {
                    thriftCoords.add(coord.convertToThriftCoordinate());
                }

                return PropValue.list_coordinate_val(thriftCoords);
            } else if (c == ezbake.base.thrift.Coordinate.class) {
                return PropValue.list_coordinate_val((List<ezbake.base.thrift.Coordinate>) obj);
            } else if (c == Long.class) {
                return PropValue.list_i64_val((List<Long>) obj);
            } else if (c == Double.class) {
                return PropValue.list_double_val((List<Double>) obj);
            } else if (c == String.class) {
                return PropValue.list_string_val((List<String>) obj);
            } else {
                throw new IllegalArgumentException(
                        "Cannot find Thrift graph mapping for object type: " + c.getName());
            }
        } else if (obj instanceof Boolean) {
            return PropValue.bool_val((Boolean) obj);
        } else if (obj instanceof Integer) {
            return PropValue.i32_val((Integer) obj);
        } else if (obj instanceof Long) {
            return PropValue.i64_val((Long) obj);
        } else if (obj instanceof Double) {
            return PropValue.double_val((Double) obj);
        } else if (obj instanceof String) {
            return PropValue.string_val((String) obj);
        } else if (obj instanceof byte[]) {
            return PropValue.binary_val((byte[]) obj);
        } else if (obj instanceof DateTime) {
            return PropValue.date_time_val((DateTime) obj);
        } else if (obj instanceof Coordinate) {
            return PropValue.coordinate_val(((Coordinate) obj).convertToThriftCoordinate());
        } else if (obj instanceof ezbake.base.thrift.Coordinate) {
            return PropValue.coordinate_val((ezbake.base.thrift.Coordinate) obj);
        } else {
            throw new IllegalArgumentException(
                    "Cannot find Thrift graph mapping for object type: " + obj.getClass().getName());
        }
    }

    public static Class<?> getDataTypeClass(DataType dataType) {
        return JAVA_TYPES.get(dataType);
    }

    public static Class<?> getElementClass(Element element) {
        return Element.VERTEX == element ? com.tinkerpop.blueprints.Vertex.class : com.tinkerpop.blueprints.Edge.class;
    }

    public static Object getJavaPropValue(PropValue propValue) {
        switch (propValue.getSetField()) {
            case BOOL_VAL:
            case I32_VAL:
            case I64_VAL:
            case DOUBLE_VAL:
            case STRING_VAL:
            case BINARY_VAL:
                return propValue.getFieldValue();

            case DATE_TIME_VAL:
                final DateTime dateTime = (DateTime) propValue.getFieldValue();
                return getJavaDate(dateTime);

            case COORDINATE_VAL:
                final Coordinate coordinate = (Coordinate) propValue.getFieldValue();
                return getJavaCoordinate(coordinate);

            case LIST_I64_VAL:
            case LIST_DOUBLE_VAL:
            case LIST_STRING_VAL:
            case LIST_BINARY_VAL:
                final List<?> primitives = (List<?>) propValue.getFieldValue();
                return getJavaArrayList(primitives);

            case LIST_DATE_TIME_VAL:
                final List<DateTime> dateTimes = (List<DateTime>) propValue.getFieldValue();
                return getJavaDates(dateTimes);

            case LIST_COORDINATE_VAL:
                final List<Coordinate> coordinates = (List<Coordinate>) propValue.getFieldValue();
                return getJavaCoordinates(coordinates);

            case MAP_STRING_VAL:
                return propValue.getFieldValue();

            default:
                return null;
        }
    }

    public static RelationIdentifier getEdgeId(ElementId elm) {
        final List<Long> ids = elm.getTitanId().getEdgeId();
        final long[] val = new long[ids.size()];
        for (int i = 0; i < val.length; i++) {
            val[i] = ids.get(i);
        }
        return RelationIdentifier.get(val);
    }

    private static java.util.Date getJavaDate(DateTime dateTime) {
        final Date date = dateTime.getDate();
        final Time time = dateTime.getTime();
        final TimeZone tz = time.getTz();

        final int offset = (tz.getHour() * 60 + tz.getMinute()) * 60 * 1000;
        final java.util.TimeZone stz = new SimpleTimeZone(offset, "");

        final Calendar calendar = new GregorianCalendar(stz);

        //noinspection MagicConstant
        calendar.set(
                date.getYear(), date.getMonth(), date.getDay(), time.getHour(), time.getMinute(), time.getSecond());

        return calendar.getTime();
    }

    private static Coordinate getJavaCoordinate(Coordinate coordinate) {
        return new Coordinate(coordinate.getLatitude(), coordinate.getLongitude());
    }

    private static <T> ArrayList<T> getJavaArrayList(List<T> list) {
        if (list instanceof ArrayList) {
            return (ArrayList<T>) list;
        } else {
            return Lists.newArrayList(list);
        }
    }

    private static List<java.util.Date> getJavaDates(List<DateTime> dateTimes) {
        return getJavaArrayList(
                Lists.transform(
                        dateTimes, new Function<DateTime, java.util.Date>() {
                            @Override
                            public java.util.Date apply(DateTime dateTime) {
                                return getJavaDate(dateTime);
                            }
                        }));
    }

    private static List<Coordinate> getJavaCoordinates(List<Coordinate> coordinates) {
        return getJavaArrayList(
                Lists.transform(
                        coordinates, new Function<Coordinate, Coordinate>() {
                            @Override
                            public Coordinate apply(Coordinate coordinate) {
                                return getJavaCoordinate(coordinate);
                            }
                        }));
    }
}
