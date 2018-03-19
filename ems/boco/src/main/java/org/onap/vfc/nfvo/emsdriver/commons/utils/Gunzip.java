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
import java.util.zip.GZIPInputStream;

public class Gunzip {

	public void unCompress(String gzFileName, String toFile) throws IOException {
		try (	FileInputStream gzInput = new FileInputStream(gzFileName);
			GZIPInputStream	gzIn = new GZIPInputStream(gzInput)){
			File tofile = new File(toFile);
			enable(tofile);
			try(FileOutputStream fileOutput = new FileOutputStream(tofile, false)){
				moveBytes(gzIn, fileOutput, -1, -1, 1024);
			} 
		}catch(IOException e){
		throw e;
		}

	}

	private void enable(File tofile) throws IOException {
		if (!tofile.exists()) {
			String parentPath = tofile.getParent();
			if (parentPath != null)
				new File(parentPath).mkdirs();
			tofile.createNewFile();
		}
	}

	public long moveBytes(InputStream input, OutputStream output, long off, long len, int bufsize) throws IOException {
		long skipped=0;
		if (off > 0)
			skipped = input.skip(off); // check if skipped is same as off

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
