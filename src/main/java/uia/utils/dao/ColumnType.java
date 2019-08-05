/*******************************************************************************
 * Copyright 2018 UIA
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
package uia.utils.dao;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.NClob;
import java.sql.SQLException;

public abstract class ColumnType {

    public enum DataType {

        VARCHAR,

        NVARCHAR,

        VARCHAR2,

        NVARCHAR2,

        INTEGER,

        LONG,

        NUMERIC,

        FLOAT,

        DOUBLE,

        TIMESTAMP,

        DATE,

        TIME,

        BLOB,

        CLOB,

        NCLOB,

        OTHERS;

        DataType() {
        }
    }

    protected boolean pk;

    protected String columnName;

    protected DataType dataType;

    protected int dataTypeCode;

    protected String dataTypeName;

    protected long columnSize;

    protected boolean nullable;

    protected int decimalDigits;

    protected String remark;

    public boolean isPk() {
        return this.pk;
    }

    public void setPk(boolean pk) {
        this.pk = pk;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public DataType getDataType() {
        return this.dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public int getDataTypeCode() {
        return this.dataTypeCode;
    }

    public void setDataTypeCode(int dataTypeCode) {
        this.dataTypeCode = dataTypeCode;
    }

    public String getDataTypeName() {
        return this.dataTypeName;
    }

    public void setDataTypeName(String dataTypeName) {
        this.dataTypeName = dataTypeName;
    }

    public boolean isNullable() {
        return this.nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public long getColumnSize() {
        return this.columnSize;
    }

    public void setColumnSize(long columnSize) {
        this.columnSize = columnSize;
    }

    public int getDecimalDigits() {
        return this.decimalDigits;
    }

    public void setDecimalDigits(int decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
     * Check if this column is string including NVARCHAR, NVARCHAR2, VARCHAR, VARCHAR2.
     * @return Result.
     */
    public boolean isStringType() {
        // THINK: include BLOB?
        switch (this.dataType) {
            case NVARCHAR:
            case NVARCHAR2:
            case VARCHAR:
            case VARCHAR2:
                return true;
            default:
                return false;
        }
    }

    /**
     * Check if this column is date-time including Date, TIME, TIMESTAMP.
     * @return Result.
     */
    public boolean isDateTimeType() {
        switch (this.dataType) {
            case DATE:
            case TIME:
            case TIMESTAMP:
                return true;
            default:
                return false;
        }
    }

    /**
     * Check if this column is numeric including INTEGER, LONG, NUMERIC, FLOAT, DOUBLE.
     * @return Result.
     */
    public boolean isNumericType() {
        switch (this.dataType) {
            case INTEGER:
            case LONG:
            case NUMERIC:
            case FLOAT:
            case DOUBLE:
                return true;
            default:
                return false;
        }
    }

    public String getJavaTypeName() {
        switch (this.dataType) {
            case INTEGER:
            	return this.nullable ? "Integer" : "int";
            case LONG:
            	return this.nullable ? "Long" : "long";
            case NUMERIC:
            case FLOAT:
            case DOUBLE:
            	return "BigDecimal";
            case DATE:
            case TIME:
            case TIMESTAMP:
            	return "Date";
            case CLOB:
            	return "Clob";
            case NCLOB:
            	return "NClob";
            case BLOB:
            	return "byte[]";
            default:
                return "String";
        }
    }

    public Object read(Connection conn, Object orig) throws SQLException {
        switch (this.dataType) {
            case CLOB:
                Clob clob = conn.createClob();
                clob.setString(1, orig.toString());
                return orig;
            case NCLOB:
                NClob nclob = conn.createNClob();
                nclob.setString(1, orig.toString());
                return orig;
            default:
                return orig;
        }
    }

    public boolean sameAs(ColumnType targetColumn, ComparePlan plan, CompareResult cr) {
        if (targetColumn == null) {
            cr.setPassed(false);
            cr.addMessage(this.columnName + " not found");
            return false;
        }

        // column name
        if (!this.columnName.equalsIgnoreCase(targetColumn.getColumnName())) {
            cr.setPassed(false);
            cr.addMessage(this.columnName + " columnName not the same");
            return false;
        }

        // null
        if (plan.checkNullable) {
            if (this.nullable != targetColumn.isNullable()) {
                cr.setPassed(false);
                cr.addMessage(String.format("%s nullable not the same: (%s,%s)",
                        this.columnName,
                        this.nullable,
                        targetColumn.isNullable()));
                return false;
            }
        }

        // primary key
        if (this.pk != targetColumn.isPk()) {
            cr.setPassed(false);
            cr.addMessage(String.format("%s pk not the same: (%s,%s)",
                    this.columnName,
                    this.pk,
                    targetColumn.isPk()));
            return false;
        }

        // string
        if (!plan.strictVarchar && isStringType() && targetColumn.isStringType()) {
            return true;
        }

        // numeric
        if (!plan.strictNumeric && isNumericType() && targetColumn.isNumericType()) {
            return true;
        }

        // data, time, timestamp
        if (!plan.strictDateTime && isDateTimeType() && targetColumn.isDateTimeType()) {
            return true;
        }

        // data type
        if (this.dataType != targetColumn.getDataType()) {
            cr.setPassed(false);
            cr.addMessage(String.format("%s dataType not the same: (%s,%s)",
                    this.columnName,
                    this.dataType,
                    targetColumn.getDataType()));
            return false;
        }

        // date/time
        if (isDateTimeType() && targetColumn.isDateTimeType()) {
            return true;
        }

        // BLOB, CLOB, NCLOB
        if (this.dataType == DataType.BLOB || this.dataType == DataType.CLOB || this.dataType == DataType.NCLOB) {
            return true;
        }

        // decimal digit
        if (this.decimalDigits != targetColumn.getDecimalDigits()) {
            cr.setPassed(false);
            cr.addMessage(String.format("%s decimalDigits not the same, (%s:%s,%s:%s)",
                    this.columnName,
                    this.dataTypeName,
                    this.decimalDigits,
                    targetColumn.getDataTypeName(),
                    targetColumn.getDecimalDigits()));
            return false;
        }

        // column size
        if (plan.checkDataSize) {
            if (this.columnSize != targetColumn.getColumnSize()) {
                cr.setPassed(false);
                cr.addMessage(String.format("%s columnSize not the same: (%s,%s)",
                        this.columnName,
                        this.columnSize,
                        targetColumn.getColumnSize()));
                return false;
            }
        }

        return true;
    }

    String genPsSet(int index) {
    	String propertyName = CamelNaming.upper(this.columnName);
        switch (this.dataType) {
            case DATE:
            case TIME:
            case TIMESTAMP:
                return String.format("ps.setTimestamp(%s, new Timestamp(data.get%s().getTime()));",
                        index,
                        propertyName);
            case INTEGER:
                return String.format("ps.setInt(%s, data.get%s());",
                        index,
                        propertyName);
            case LONG:
                return String.format("ps.setLong(%s, data.get%s());",
                        index,
                        propertyName);
            case NUMERIC:
            case FLOAT:
            case DOUBLE:
                return String.format("ps.setBigDecimal(%s, data.get%s());",
                        index,
                        propertyName);
            case CLOB:
                return String.format("ps.setClob(%s, data.get%s());",
                        index,
                        propertyName);
            case NCLOB:
                return String.format("ps.setNClob(%s, data.get%s());",
                        index,
                        propertyName);
            case BLOB:
                return String.format("ps.setBlob(%s, data.get%s());",
                        index,
                        propertyName);

            default:
                return String.format("ps.setString(%s, data.get%s());",
                        index,
                        propertyName);
        }
    }

    String genPsSetEx(int index) {
    	String propertyName = CamelNaming.lower(this.columnName);
        switch (this.dataType) {
            case DATE:
            case TIME:
            case TIMESTAMP:
                return String.format("ps.setTimestamp(%s, new Timestamp(%s.getTime()));",
                        index,
                        propertyName);
            case INTEGER:
                return String.format("ps.setInt(%s, %s);",
                        index,
                        propertyName);
            case LONG:
                return String.format("ps.setLong(%s, %s);",
                        index,
                        propertyName);
            case NUMERIC:
            case FLOAT:
            case DOUBLE:
                return String.format("ps.setBigDecimal(%s, %s);",
                        index,
                        propertyName);
            case CLOB:
                return String.format("ps.setClob(%s, %s);",
                        index,
                        propertyName);
            case NCLOB:
                return String.format("ps.setNClob(%s, %s);",
                        index,
                        propertyName);
            case BLOB:
                return String.format("ps.setBlob(%s, %s);",
                        index,
                        propertyName);
            default:
                return String.format("ps.setString(%s, %s);",
                        index,
                        propertyName);
        }
    }

    String genRsGet(String index) {
        String type = "String";
        switch (this.dataType) {
            case INTEGER:
                type = "Int";
                break;
            case LONG:
                type = "Long";
                break;
            case NUMERIC:
            case FLOAT:
            case DOUBLE:
                type = "BigDecimal";
                break;
            case DATE:
            case TIME:
            case TIMESTAMP:
                type = "Timestamp";
                break;
            case CLOB:
                type = "Clob";
                break;
            case NCLOB:
                type = "NClob";
                break;
            case BLOB:
                type = "Blob";
                break;
            default:
                type = "String";
        }

        String rsGet = String.format("data.set%s(rs.get%s(%s));",
        		CamelNaming.upper(this.columnName),
                type,
                index);
        return rsGet;
    }

    @Override
    public String toString() {
        return String.format("%-30s, pk:%-5s, %-9s, %4s:%-13s [%s], %s",
                this.columnName,
                this.pk,
                this.dataType,
                this.dataTypeCode,
                this.dataTypeName,
                this.columnSize,
                this.remark);
    }
}
