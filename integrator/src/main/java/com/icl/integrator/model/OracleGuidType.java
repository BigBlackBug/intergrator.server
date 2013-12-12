package com.icl.integrator.model;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import org.hibernate.HibernateException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author Zavyalov Alexey
 * @date 07.09.13
 * @company OJSC "ICL-KME CS"
 */
public class OracleGuidType extends AbstractGUIDType {

    private static char[] subarray(char[] data, int sPos, int ePos) {
        char[] temp = new char[ePos - sPos];

        int x = 0;
        for (int i = sPos; i < ePos; i++) {
            temp[x] = data[i];
            x++;
        }
        return temp;
    }

    public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
            throws HibernateException, SQLException {
        byte[] data = rs.getBytes(names[0]);
        if (data == null) {
            return null;
        }

        if (data.length != 32) {
            data = hackByteArray(data);
        }

        String guid = HexBin.encode(data);
        char[] guidChars = guid.toCharArray();

        long p1 = Long.parseLong(String.copyValueOf(guidChars, 0x00, 0x08), 16);
        long p2 = Long.parseLong(String.copyValueOf(guidChars, 0x08, 0x04), 16);
        long p3 = Long.parseLong(String.copyValueOf(guidChars, 0x0c, 0x04), 16);
        long p4 = Long.parseLong(String.copyValueOf(guidChars, 0x10, 0x04), 16);
        long p5 = Long.parseLong(String.copyValueOf(guidChars, 0x14, 0x0C), 16);

        long mostSigBits = p1;
        mostSigBits <<= 16;
        mostSigBits |= p2;
        mostSigBits <<= 16;
        mostSigBits |= p3;

        long leastSigBits = p4;
        leastSigBits <<= 48;
        leastSigBits |= p5;

        return new UUID(mostSigBits, leastSigBits);
    }

    private byte[] hackByteArray(byte[] baddata) {
        byte[] data = new byte[32];
        Arrays.fill(data, (byte) 0);
        System.arraycopy(baddata, 0, data, 0, baddata.length);
        return data;
    }

}
