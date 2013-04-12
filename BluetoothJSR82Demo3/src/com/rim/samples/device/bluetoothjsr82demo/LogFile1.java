package com.rim.samples.device.bluetoothjsr82demo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.rim.device.api.io.IOUtilities;

public class LogFile1 {

	private OutputStreamWriter bugFile;
	private OutputStream setoutput;
	private FileConnection fconn0;
	int _logcounter=0;
	int _selflogcounter=0;
	static String strDate0=null;
	public static int bLogcount;
	public static int bSelflogcount;
	
	public LogFile1() {
	}	

	void createLogFile()
	{    	
		String FileNamelog=System.getProperty("fileconn.dir.memorycard")+"BlackBerry/Rawtmp/";
		FileConnection file = null;
		try {
			file = (FileConnection) Connector.open(FileNamelog);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (!(file.exists())) {
			try {
				file.mkdir();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		getCurrentTimeStamp();
		try{
			if (fconn0==null){

				fconn0 = (FileConnection) Connector.open(FileNamelog+ "Debug" +strDate0+ "-" + _selflogcounter + ".txt");

				// If the file exists, increment the counter until we find
				// one that hasn't been created yet.
				while (fconn0.exists()) {
					fconn0.close();
					++_selflogcounter;
					fconn0 = (FileConnection) Connector.open(FileNamelog+ "Debug" +strDate0+ "-" + _selflogcounter + ".txt");
				}
    			// We know the file doesn't exist yet, so create it.
    			fconn0.create();
    			setoutput = fconn0.openOutputStream(999999999);  //Append to end of file
    			bugFile= new OutputStreamWriter(setoutput);
			}
		}

		catch(IOException ioFileex) 
		{
			//Catch and re-throw the exception.
			throw new RuntimeException(ioFileex.toString());
		}    
	}
	
	public static String getCurrentTimeStamp() {
	       net.rim.device.api.i18n.SimpleDateFormat formDate0 = new net.rim.device.api.i18n.SimpleDateFormat("ddMMyyyy");
	        strDate0 = formDate0.formatLocal(System.currentTimeMillis()); // option 1
	       //String strDate = formDate.format(new Date()); // option 2
	       return strDate0;
	  } 

	public void closeLogFile()
	{
		try{
			bugFile.flush();
			setoutput.close();
			bugFile.close();
			fconn0.close();    	
			fconn0=null;
		}    	
		catch (IOException ioex) {
			//Catch and re-throw the exception.
			throw new RuntimeException(ioex.toString());
		}
	}

	public void writeStringToLog(String logString)
	{    	
		try{    		
			bugFile.write(logString);    		
		}   	
		catch(IOException ioFileex) 
		{
			//Catch and re-throw the exception.
			throw new RuntimeException(ioFileex.toString());
		}
	}


	public void writeStringToLog(int logInt)
	{    	
		try{
			bugFile.write(Integer.toString(logInt));    		
		}    	
		catch(IOException ioFileex) 
		{
			//Catch and re-throw the exception.
			throw new RuntimeException(ioFileex.toString());
		}
	}

	public void writeStringToLog(long logLong)
	{
		String temp=Long.toString(logLong);
		try{
			bugFile.write(temp);    		
		}  	
		catch(IOException ioFileex) 
		{
			//Catch and re-throw the exception.
			throw new RuntimeException(ioFileex.toString());
		}
	}

	public void writeStringToLog(float logfloat)
	{
		String temp=Float.toString(logfloat);
		try{
			bugFile.write(temp);    		
		}   	
		catch(IOException ioFileex) 
		{
			//Catch and re-throw the exception.
			throw new RuntimeException(ioFileex.toString());
		}
	}
	public void writeStringToLog(double logfloat)
	{
		String temp=Double.toString(logfloat);
		try{
			bugFile.write(temp);    		
		}    	
		catch(IOException ioFileex) 
		{
			//Catch and re-throw the exception.
			throw new RuntimeException(ioFileex.toString());
		}
	}

	public void writeStringToLog(byte[] cmdSetSampleRate) {
		// TODO Auto-generated method stub
		String str = new String(cmdSetSampleRate);
		try{
			bugFile.write(str);    		
		}    	
		catch(IOException ioFileex) 
		{
			//Catch and re-throw the exception.
			throw new RuntimeException(ioFileex.toString());
		}
		

	}
	
	static String byteArrayToHexString(byte in[]) {
        byte ch = 0x00;
        int i = 0; 
        if (in == null || in.length <= 0)
            return null;

        String pseudo[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        StringBuffer out = new StringBuffer(in.length * 2);

        while (i < in.length) {
            ch = (byte) (in[i] & 0xF0); // Strip off high nibble
            ch = (byte) (ch >>> 4);      // shift the bits down
            ch = (byte) (ch & 0x0F);    // must do this is high order bit is on!
            out.append(pseudo[ ch]); // convert the nibble to a String Character
            ch = (byte) (in[i] & 0x0F); // Strip off low nibble 
            out.append(pseudo[ ch]); // convert the nibble to a String Character
            i++;
        }
        String rslt = new String(out);
        return rslt;
	}
	
}