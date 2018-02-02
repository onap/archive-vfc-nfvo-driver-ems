/**
 * Copyright 2017 CMCC Technologies Co., Ltd
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onap.vfc.nfvo.emsdriver.commons.utils;

import java.io.*;
import java.util.zip.GZIPOutputStream;

public class Gzip {

    public void compress(String srcFileName, String toGzFile)
            throws IOException {
        FileInputStream fileInput = new FileInputStream(srcFileName);
        compress(fileInput, toGzFile);
        fileInput.close();


    }

    public void compress(InputStream src, String toGzFile)
            throws IOException {
        File theFile = new File(toGzFile);
        if (!theFile.exists()) {
            String parentPath = theFile.getParent();
            if (parentPath != null)
                new File(parentPath).mkdirs();
            theFile.createNewFile();
        }
        GZIPOutputStream gzOutput = new GZIPOutputStream(new FileOutputStream(theFile, false));

        moveBytes(src, gzOutput, -1, -1, 1024);
        gzOutput.close();
    }

    public long moveBytes(InputStream input, OutputStream output, long off, long len, int bufsize)
            throws IOException {
        if (off > 0)
            input.skip(off);

        long totalNum = 0;
        byte[] buf = new byte[bufsize];

        while (true) {
            if (len > 0 && (len - totalNum) <= 0)
                break;

            else if (len > 0 && bufsize > (len - totalNum))
                bufsize = (int) (len - totalNum);

            int readNum = input.read(buf, 0, bufsize);
            if (readNum <= 0)
                break;

            output.write(buf, 0, readNum);
            totalNum += readNum;
        }
        buf = null;
        return totalNum;
    }


}
