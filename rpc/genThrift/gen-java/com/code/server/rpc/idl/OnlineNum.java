/**
 * Autogenerated by Thrift Compiler (0.10.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.code.server.rpc.idl;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.10.0)", date = "2017-09-27")
public class OnlineNum implements org.apache.thrift.TBase<OnlineNum, OnlineNum._Fields>, java.io.Serializable, Cloneable, Comparable<OnlineNum> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("OnlineNum");

  private static final org.apache.thrift.protocol.TField USER_NUM_FIELD_DESC = new org.apache.thrift.protocol.TField("userNum", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField ROOM_NUM_FIELD_DESC = new org.apache.thrift.protocol.TField("roomNum", org.apache.thrift.protocol.TType.I32, (short)2);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new OnlineNumStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new OnlineNumTupleSchemeFactory();

  public int userNum; // required
  public int roomNum; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    USER_NUM((short)1, "userNum"),
    ROOM_NUM((short)2, "roomNum");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // USER_NUM
          return USER_NUM;
        case 2: // ROOM_NUM
          return ROOM_NUM;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __USERNUM_ISSET_ID = 0;
  private static final int __ROOMNUM_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.USER_NUM, new org.apache.thrift.meta_data.FieldMetaData("userNum", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.ROOM_NUM, new org.apache.thrift.meta_data.FieldMetaData("roomNum", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(OnlineNum.class, metaDataMap);
  }

  public OnlineNum() {
  }

  public OnlineNum(
    int userNum,
    int roomNum)
  {
    this();
    this.userNum = userNum;
    setUserNumIsSet(true);
    this.roomNum = roomNum;
    setRoomNumIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public OnlineNum(OnlineNum other) {
    __isset_bitfield = other.__isset_bitfield;
    this.userNum = other.userNum;
    this.roomNum = other.roomNum;
  }

  public OnlineNum deepCopy() {
    return new OnlineNum(this);
  }

  @Override
  public void clear() {
    setUserNumIsSet(false);
    this.userNum = 0;
    setRoomNumIsSet(false);
    this.roomNum = 0;
  }

  public int getUserNum() {
    return this.userNum;
  }

  public OnlineNum setUserNum(int userNum) {
    this.userNum = userNum;
    setUserNumIsSet(true);
    return this;
  }

  public void unsetUserNum() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __USERNUM_ISSET_ID);
  }

  /** Returns true if field userNum is set (has been assigned a value) and false otherwise */
  public boolean isSetUserNum() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __USERNUM_ISSET_ID);
  }

  public void setUserNumIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __USERNUM_ISSET_ID, value);
  }

  public int getRoomNum() {
    return this.roomNum;
  }

  public OnlineNum setRoomNum(int roomNum) {
    this.roomNum = roomNum;
    setRoomNumIsSet(true);
    return this;
  }

  public void unsetRoomNum() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __ROOMNUM_ISSET_ID);
  }

  /** Returns true if field roomNum is set (has been assigned a value) and false otherwise */
  public boolean isSetRoomNum() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __ROOMNUM_ISSET_ID);
  }

  public void setRoomNumIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __ROOMNUM_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, java.lang.Object value) {
    switch (field) {
    case USER_NUM:
      if (value == null) {
        unsetUserNum();
      } else {
        setUserNum((java.lang.Integer)value);
      }
      break;

    case ROOM_NUM:
      if (value == null) {
        unsetRoomNum();
      } else {
        setRoomNum((java.lang.Integer)value);
      }
      break;

    }
  }

  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case USER_NUM:
      return getUserNum();

    case ROOM_NUM:
      return getRoomNum();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case USER_NUM:
      return isSetUserNum();
    case ROOM_NUM:
      return isSetRoomNum();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof OnlineNum)
      return this.equals((OnlineNum)that);
    return false;
  }

  public boolean equals(OnlineNum that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_userNum = true;
    boolean that_present_userNum = true;
    if (this_present_userNum || that_present_userNum) {
      if (!(this_present_userNum && that_present_userNum))
        return false;
      if (this.userNum != that.userNum)
        return false;
    }

    boolean this_present_roomNum = true;
    boolean that_present_roomNum = true;
    if (this_present_roomNum || that_present_roomNum) {
      if (!(this_present_roomNum && that_present_roomNum))
        return false;
      if (this.roomNum != that.roomNum)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + userNum;

    hashCode = hashCode * 8191 + roomNum;

    return hashCode;
  }

  @Override
  public int compareTo(OnlineNum other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetUserNum()).compareTo(other.isSetUserNum());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetUserNum()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.userNum, other.userNum);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetRoomNum()).compareTo(other.isSetRoomNum());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetRoomNum()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.roomNum, other.roomNum);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("OnlineNum(");
    boolean first = true;

    sb.append("userNum:");
    sb.append(this.userNum);
    first = false;
    if (!first) sb.append(", ");
    sb.append("roomNum:");
    sb.append(this.roomNum);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class OnlineNumStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public OnlineNumStandardScheme getScheme() {
      return new OnlineNumStandardScheme();
    }
  }

  private static class OnlineNumStandardScheme extends org.apache.thrift.scheme.StandardScheme<OnlineNum> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, OnlineNum struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // USER_NUM
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.userNum = iprot.readI32();
              struct.setUserNumIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // ROOM_NUM
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.roomNum = iprot.readI32();
              struct.setRoomNumIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, OnlineNum struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(USER_NUM_FIELD_DESC);
      oprot.writeI32(struct.userNum);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(ROOM_NUM_FIELD_DESC);
      oprot.writeI32(struct.roomNum);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class OnlineNumTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public OnlineNumTupleScheme getScheme() {
      return new OnlineNumTupleScheme();
    }
  }

  private static class OnlineNumTupleScheme extends org.apache.thrift.scheme.TupleScheme<OnlineNum> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, OnlineNum struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetUserNum()) {
        optionals.set(0);
      }
      if (struct.isSetRoomNum()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetUserNum()) {
        oprot.writeI32(struct.userNum);
      }
      if (struct.isSetRoomNum()) {
        oprot.writeI32(struct.roomNum);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, OnlineNum struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        struct.userNum = iprot.readI32();
        struct.setUserNumIsSet(true);
      }
      if (incoming.get(1)) {
        struct.roomNum = iprot.readI32();
        struct.setRoomNumIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

