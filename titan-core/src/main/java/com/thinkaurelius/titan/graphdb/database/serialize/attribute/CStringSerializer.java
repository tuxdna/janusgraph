package com.thinkaurelius.titan.graphdb.database.serialize.attribute;

import com.google.common.base.Preconditions;
import com.thinkaurelius.titan.core.AttributeSerializer;
import com.thinkaurelius.titan.core.attribute.CString;
import com.thinkaurelius.titan.diskstorage.ScanBuffer;
import com.thinkaurelius.titan.diskstorage.WriteBuffer;
import com.thinkaurelius.titan.graphdb.database.serialize.OrderPreservingSerializer;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */

public class CStringSerializer implements AttributeSerializer<CString>, OrderPreservingSerializer {

    private final CharacterSerializer cs = new CharacterSerializer();

    @Override
    public CString read(ScanBuffer buffer) {
        StringBuilder s = new StringBuilder();
        while (true) {
            char c = cs.read(buffer);
            if (((int) c) > 0) s.append(c);
            else break;
        }
        return new CString(s.toString());
    }

    @Override
    public void writeObjectData(WriteBuffer buffer, CString attribute) {
        for (int i = 0; i < attribute.length(); i++) {
            char c = attribute.charAt(i);
            Preconditions.checkArgument(((int) c) > 0, "No null characters allowed in string @ position %s: %s", i, attribute);
            cs.writeObjectData(buffer, c);
        }
        cs.writeObjectData(buffer, (char) 0);
    }

    @Override
    public void verifyAttribute(CString value) {
        for (int i = 0; i < value.length(); i++) {
            Preconditions.checkArgument(((int) value.charAt(i)) > 0, "No null characters allowed in string @ position %s: %s", i, value);
        }
    }

    @Override
    public CString convert(Object value) {
        Preconditions.checkNotNull(value);
        return new CString(value.toString());
    }

}
