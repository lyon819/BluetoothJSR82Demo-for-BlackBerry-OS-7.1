package com.rim.samples.device.bluetoothjsr82demo;

import java.io.IOException;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

public class BluetoothJSR82Screen extends MainScreen{
	
    static final int SPP_UUID = 0x8889, OPP_UUID = 0x1105, L2CAP_UUID = 0x8888;
    //UUID:34953

    private static final int CLIENT_CHOICE = 0, SERVER_CHOICE = 1;
    private static final String[] CHOICES = { "Client", "Server", "Exit" };
    private static final String[] DEMO_CHOICES = { "SPP", "OPP", "L2CAP", "Exit" };
    private static final int[] DEMO_UUIDS = {SPP_UUID, OPP_UUID, L2CAP_UUID, -1};
    
    //Using button instead of dialog
    public ButtonField clientField; 
	public ButtonField serverField;
	public ButtonField exitField;
	public LabelField title;
	
   
    // bugfile
 	//private LogFile1 bugrecord;
 	
	public BluetoothJSR82Screen() throws IOException, InterruptedException {
		title = new LabelField("Bluetooth JSR82 Demo",LabelField.ELLIPSIS | LabelField.USE_ALL_WIDTH | LabelField.HCENTER | LabelField.VCENTER);
		setTitle(title);	
		
		//bugrecord = new LogFile1();
		//bugrecord.createLogFile();
		
		//bugrecord.writeStringToLog("screen open");        	
     	//bugrecord.writeStringToLog("\n");
        ////bugrecord.closeLogFile();
		// If the device supports JSR 82 then ask the user if they want to set 
        // up a client or server.     	
        initializeMode1();   		
    }

	
	private void initializeMode1() {
		UiApplication.getUiApplication().invokeLater(new Runnable()
	       {        	
	            /**
	             * @see java.lang.Runnable#run()
	             */
	            public void run()
	            {
	                int choice = Dialog.ask("Select Mode", CHOICES, CLIENT_CHOICE);
	                //bugrecord.writeStringToLog("initializeMode");        	
	            	//bugrecord.writeStringToLog("\n");
	                if(choice == CLIENT_CHOICE)
	                {
	                    int uuid = Dialog.ask("Select Profile", DEMO_CHOICES, DEMO_UUIDS, SPP_UUID);
	                	//bugrecord.writeStringToLog("UUID:" + uuid);        	
	                	//bugrecord.writeStringToLog("\n");
	                    if(uuid == -1) {
		                	//bugrecord.writeStringToLog("uuid=1");        	
		                	//bugrecord.writeStringToLog("\n");	                    	
	                        //bugrecord.closeLogFile();
	                        System.exit(0);
	                    }
	                    else 
	                    {
	                    	//bugrecord.writeStringToLog("push client screen");        	
	                    	//bugrecord.writeStringToLog("\n");
	                        //bugrecord.closeLogFile();
	                        UiApplication.getUiApplication().pushScreen( new ClientScreen(uuid));        
	                    }
	                }
	                else if(choice == SERVER_CHOICE)
	                {
	                    int uuid = Dialog.ask("Select Demo", DEMO_CHOICES, DEMO_UUIDS, SPP_UUID);
	                	//bugrecord.writeStringToLog("UUID:" + uuid);        	
	                	//bugrecord.writeStringToLog("\n");
	                    if(uuid == -1)
	                    {
	                        //bugrecord.closeLogFile();
	                        System.exit(0);
	                    } 
	                    else 
	                    {    
	                    	//bugrecord.writeStringToLog("push server screen");        	
	                        //bugrecord.writeStringToLog("\n");
	                        //bugrecord.closeLogFile();
	                        UiApplication.getUiApplication().pushScreen(new ServerScreen(uuid));        

	                    }
	                }
	                else
	                {
	                	//bugrecord.writeStringToLog("strange close");        	
	                    //bugrecord.closeLogFile();
	                    System.exit(0);
	                }
	            }//run
	        });//UI
		
	}




}
