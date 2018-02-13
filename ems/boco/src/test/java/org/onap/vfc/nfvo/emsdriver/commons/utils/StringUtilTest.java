/*
 * Copyright 2017 BOCO Corporation.  CMCC Technologies Co., Ltd
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
 * limitations under the License.
 */
package org.onap.vfc.nfvo.emsdriver.commons.utils;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class StringUtilTest {

    private Throwable throwable;

    @Before
    public void setUp() {
        throwable = new Exception("test");
    }

    @Test
    public void testGetStackTrace() {
        String str = StringUtil.getStackTrace(throwable);
        
        assertNotNull(str);
    }

    @Test
    public void testAddSlash() {
        String str = StringUtil.addSlash("aa/bb");
        String str1 = StringUtil.addSlash("aa/bb"+File.separator);

        assertTrue(str.endsWith(File.separator));
        assertTrue(str1.endsWith(File.separator));
    }

    @Test
    public void testIsBank() {
        boolean str = StringUtil.isBank("aa/bb");
        boolean str1 = StringUtil.isBank("");
        boolean str2 = StringUtil.isBank(null);
        assertFalse(str);
        assertTrue(str1);
        assertTrue(str2);
    }
}
