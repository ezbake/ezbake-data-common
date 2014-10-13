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

import com.google.common.base.Preconditions;
import com.thinkaurelius.titan.core.AttributeSerializer;
import com.thinkaurelius.titan.diskstorage.ScanBuffer;
import com.thinkaurelius.titan.diskstorage.WriteBuffer;

public final class CoordinateSerializer implements AttributeSerializer<Coordinate> {
    @Override
    public Coordinate read(ScanBuffer buffer) {
        final double latitude = buffer.getDouble();
        final double longitude = buffer.getDouble();
        return new Coordinate(latitude, longitude);
    }

    @Override
    public void writeObjectData(WriteBuffer buffer, Coordinate attribute) {
        buffer.putDouble(attribute.getLatitude());
        buffer.putDouble(attribute.getLongitude());
    }

    @Override
    public void verifyAttribute(Coordinate value) {
        Preconditions.checkArgument(!Double.isNaN(value.getLatitude()), "Coordinate latitude may not be NaN");
        Preconditions.checkArgument(!Double.isNaN(value.getLongitude()), "Coordinate longitude may not be NaN");
    }

    @Override
    public Coordinate convert(Object value) {
        return null;
    }
}
