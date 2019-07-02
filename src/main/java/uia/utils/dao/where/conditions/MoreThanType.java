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
package uia.utils.dao.where.conditions;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class MoreThanType implements ConditionType {

    private final String key;

    private final Object value;
    
    private final boolean eq;

    public MoreThanType(String key, Object value, boolean eq) {
        this.key = key;
        this.value = value;
        this.eq = eq;
    }

    @Override
    public String getStatement() {
        return this.eq ? this.key + ">=?" : this.key + ">?";
    }

    @Override
    public int accpet(PreparedStatement ps, int index) throws SQLException {
        if (this.value instanceof Date) {
            ps.setTimestamp(index++, new Timestamp(((Date) this.value).getTime()));
        }
        else {
            ps.setObject(index++, this.value);
        }
        return index;
    }

    @Override
    public String toString() {
        return this.value == null ? this.key + " is null" : this.key + "='" + this.value + "'";
    }
}
