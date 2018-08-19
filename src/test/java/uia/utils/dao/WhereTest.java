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

import org.junit.Assert;
import org.junit.Test;

import uia.utils.dao.where.SimpleWhere;
import uia.utils.dao.where.Where;
import uia.utils.dao.where.WhereAnd;
import uia.utils.dao.where.WhereOr;

public class WhereTest {

    @Test
    public void testOr() {
        Where and1 = SimpleWhere.createAnd().eq("A", "A").eq("B", "B");
        System.out.println("and1: " + and1.generate());
        Where and2 = SimpleWhere.createAnd().eq("C", "C").eq("D", "D");
        System.out.println("and2: " + and2.generate());

        Where where = new WhereOr().add(and1).add(and2);
        System.out.println("(and) or (and): " + where.generate());
        Assert.assertEquals("(A=? and B=?) or (C=? and D=?)", where.generate());
    }

    @Test
    public void testAnd() {
        Where or1 = SimpleWhere.createOr().eq("A", "A").eq("B", "B");
        System.out.println("or1: " + or1.generate());
        Where or2 = SimpleWhere.createOr().eq("C", "C").eq("D", "D");
        System.out.println("or2: " + or2.generate());
        Where and3 = SimpleWhere.createAnd().eq("E", "E").eq("F", "F");
        System.out.println("and3: " + and3.generate());

        Where where1 = new WhereAnd().add(or1).add(or2);
        System.out.println("(or) and (or):" + where1.generate());
        Assert.assertEquals("(A=? or B=?) and (C=? or D=?)", where1.generate());

        Where where2 = new WhereOr().add(where1).add(and3);
        System.out.println("(...) or (and):" + where2.generate());
        Assert.assertEquals("((A=? or B=?) and (C=? or D=?)) or (E=? and F=?)", where2.generate());
    }
}
