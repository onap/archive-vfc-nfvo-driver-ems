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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class UnZipTest {
    private String file = "./test.txt";
    private String zipPath = "./test.zip";
    private String toPath = System.getProperty("user.dir") + "/data/";
    private UnZip unZip = null;

    public static void main(String[] str) {
        System.out.println(System.getProperty("user.dir"));
    }

    @Before
    public void setUp() throws IOException {
        new File(file).createNewFile();
        Zip zip = new Zip(file, zipPath);
        zip.compress();
        unZip = new UnZip(zipPath, toPath);

    }

    @Test
    public void deCompress() throws IOException {
        unZip.deCompress();

        assertTrue(new File(toPath).listFiles().length > 0);
    }

    @After
    public void setDown() throws IOException {
        new File(zipPath).delete();
        new File(file).delete();
        new File(toPath + file).delete();
    }
}
