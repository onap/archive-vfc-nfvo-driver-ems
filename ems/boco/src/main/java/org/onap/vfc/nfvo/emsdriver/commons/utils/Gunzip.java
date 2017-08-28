package org.onap.vfc.nfvo.emsdriver.commons.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;


public class Gunzip {
	
	/**
	 * 
	 */
	public void unCompress(String gzFileName, String toFile) 
		throws IOException {
		GZIPInputStream gzIn = null;
		FileOutputStream fileOutput = null;
		FileInputStream gzInput = new FileInputStream(gzFileName);
		try {
			gzIn = new GZIPInputStream(gzInput);
			File tofile = new File(toFile);
			enable(tofile);
			fileOutput = new FileOutputStream(tofile, false);

			moveBytes(gzIn, fileOutput, -1, -1, 1024);
		} finally{
			if(gzIn != null){
				gzIn.close();
			}
			if(fileOutput != null){
				fileOutput.close();
			}
			
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


	public long moveBytes(InputStream input, OutputStream output, long off, long len, int bufsize) 
			throws IOException {
			if (off > 0)
				input.skip(off);
			
			long totalNum = 0;
			byte[] buf = new byte[bufsize];

			while (true) {
				if (len>0 && (len-totalNum)<=0)
					break;
				
				else if (len>0 && bufsize>(len-totalNum))
					bufsize = (int)(len-totalNum);
				
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