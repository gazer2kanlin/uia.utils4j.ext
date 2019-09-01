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
package uia.utils.dao;

import java.sql.SQLException;

import org.junit.Test;

import uia.utils.dao.sqlite.SQLite;

/**
 *
 * @author Kyle K. Lin
 *
 */
public class JavaClassPrinterTest {

    @Test
    public void testGenerateTable1() throws Exception {
        Database db = sqlite();
        String tableName = "equip";
        String dtoPackage = "uia.utils";
        String daoPackage = "uia.utils.dao";

        JavaClassPrinter.Result result = new JavaClassPrinter(db, tableName).generate(
                daoPackage,
                dtoPackage,
                CamelNaming.upper(tableName),
                true);
        System.out.println(result.dto);
    }

    @Test
    public void testGenerateTable2() throws Exception {
        Database db = sqlite();
        String tableName = "part_group_part";
        String dtoPackage = "uia.utils";
        String daoPackage = "uia.utils.dao";

        new JavaClassPrinter(db, tableName).generate(
                daoPackage,
                dtoPackage,
                CamelNaming.upper(tableName));
    }

    @Test
    public void testGenerateView() throws Exception {
        Database db = sqlite();
        String viewName = "view_equip";
        String dtoPackage = "uia.utils";
        String daoPackage = "uia.utils.dao";

        new JavaClassPrinter(db, viewName).generate4View(
                daoPackage,
                dtoPackage,
                CamelNaming.upper(viewName));
    }

    private Database sqlite() throws SQLException {
        return new SQLite("test/sqlite1");
    }

}
