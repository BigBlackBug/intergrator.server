package com.icl.integrator.model;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

/**
 * @author Zavyalov Alexey
 * @date 07.09.13
 * @company OJSC "ICL-KME CS"
 */
public class AbstractGUIDType implements UserType, Serializable {

    private static final int[] SQL_TYPES = new int[]{Types.VARBINARY};

    /**
     * @see org.hibernate.usertype.UserType#sqlTypes()
     */
    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    /**
     * @see org.hibernate.usertype.UserType#returnedClass()
     */
    public Class returnedClass() {
        return UUID.class;
    }

    /**
     * @see org.hibernate.usertype.UserType#equals(Object, Object)
     */
    public boolean equals(Object x, Object y) throws HibernateException {
        return (x == y) || (x != null && y != null && (x.equals(y)));
    }

    /**
     * @see org.hibernate.usertype.UserType#hashCode(Object)
     */
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    /**
     * @see org.hibernate.usertype.UserType#deepCopy(Object)
     */
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    /**
     * @see org.hibernate.usertype.UserType#isMutable()
     */
    public boolean isMutable() {
        return false;
    }

    /**
     * @see org.hibernate.usertype.UserType#disassemble(Object)
     */
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    /**
     * @see org.hibernate.usertype.UserType#assemble(java.io.Serializable, Object)
     */
    public Object assemble(Serializable cached, Object owner)
            throws HibernateException {
        return cached;
    }

    /**
     * @see org.hibernate.usertype.UserType#replace(Object, Object, Object)
     */
    public Object replace(Object original, Object target, Object owner)
            throws HibernateException {
        return original;
    }

    /**
     * @see UserType#nullSafeSet(java.sql.PreparedStatement, Object, int)
     */
    public void nullSafeSet(PreparedStatement st, Object value, int index)
            throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, java.sql.Types.VARBINARY);
        } else {
            UUID guid = (UUID) value;

            String uuid = guid.toString().replaceAll("-", "");
            st.setBytes(index, HexBin.decode(uuid));
        }
    }

    /**
     * @see UserType#nullSafeGet(java.sql.ResultSet, String[], Object)
     */
    public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
            throws HibernateException, SQLException {
        String guid = rs.getString(names[0]);
        if (guid == null) {
            return null;
        }

        guid = String.format("%s-%s-%s-%s-%s",
                             guid.substring(0x00, 0x08),
                             guid.substring(0x08, 0x0c),
                             guid.substring(0x0c, 0x10),
                             guid.substring(0x10, 0x14),
                             guid.substring(0x14, 0x20));

        return UUID.fromString(guid);
    }


}
