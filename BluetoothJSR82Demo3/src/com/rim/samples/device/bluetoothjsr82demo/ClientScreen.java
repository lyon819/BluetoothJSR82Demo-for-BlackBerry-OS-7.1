/*
 * ClientScreen.java
 *
 * Copyright © 1998-2012 Research In Motion Ltd.
 * 
 * Note: For the sake of simplicity, this sample application may not leverage
 * resource bundles and resource strings.  However, it is STRONGLY recommended
 * that application developers make use of the localization features available
 * within the BlackBerry development platform to ensure a seamless application
 * experience across a variety of languages and geographies.  For more information
 * on localizing your application, please refer to the BlackBerry Java Development
 * Environment Development Guide associated with this release.
 */

package com.rim.samples.device.bluetoothjsr82demo;

import java.io.*;
import java.util.*;

import javax.obex.*;
import javax.bluetooth.*;
import javax.microedition.io.*;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.command.*;
import net.rim.device.api.system.*;
import net.rim.device.api.ui.component.table.*;
import net.rim.device.api.ui.decor.*;
import net.rim.device.api.util.*;

/**
 * Client Screen class for the BluetoothJSR82Demo application
 */
public final class ClientScreen extends MainScreen implements DiscoveryListener
{   
    private int _uuid;
    private RichTextField _statusField;
    private String _url;
    private BluetoothTableModelAdapter _model;
    private BluetoothTableModelAdapter _model1;

    private TableView _view;
    private TableView _view1;
    private DiscoveryAgent _discoveryAgent;
    private Vector _remoteDevices = new Vector();
    
    // bugfile
 	private LogFile1 bugrecord;

    /**
     * Creates a new ClientScreen object
     */
    public ClientScreen(int uuid)
    { 
        super(Manager.NO_VERTICAL_SCROLL);
        
		bugrecord = new LogFile1();
		bugrecord.createLogFile();
    
        setTitle("Bluetooth JSR82 Demo Client");
        
        _uuid = uuid;
        bugrecord.writeStringToLog("URL:" + _uuid);
        bugrecord.writeStringToLog("\n");
        _statusField = new RichTextField(Field.NON_FOCUSABLE);
        add(_statusField);
        
        MenuItem infoScreen = new MenuItem(new StringProvider("Info Screen"), 0x230010, 1);
        infoScreen.setCommand(new Command(new CommandHandler() 
        {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata, Object)
             */
            public void execute(ReadOnlyCommandMetadata metadata, Object context) 
            {
                UiApplication.getUiApplication().pushScreen(new InfoScreen());
            }
        }));
        
        addMenuItem(infoScreen);
        
        try
        {
            Dialog.alert("Search for Devices");            
            
            _model = new BluetoothTableModelAdapter();
            
            _view = new TableView(_model);
            TableController controller = new TableController(_model, _view);
            controller.setFocusPolicy(TableController.ROW_FOCUS);
            _view.setController(controller);
            
            // Set the highlight style for the view
            _view.setDataTemplateFocus(BackgroundFactory.createLinearGradientBackground(Color.LIGHTBLUE, Color.LIGHTBLUE,Color.BLUE,Color.BLUE));
            
            // Create a data template that will format the model data as an array of Fields
            DataTemplate dataTemplate = new DataTemplate(_view, 1, 1)
            {
                public Field[] getDataFields(int modelRowIndex)
                {
                    RemoteDevice _remoteDevice = (RemoteDevice) _model.getRow(modelRowIndex);
                    
                    Field[] fields = {new LabelField(_remoteDevice.toString(), Field.NON_FOCUSABLE)};      
                    return fields;
                }
            };
            
            // Define the regions of the data template and column/row size
            dataTemplate.createRegion(new XYRect(0,0,1,1));
            dataTemplate.setColumnProperties(0, new TemplateColumnProperties(Display.getWidth()));
            dataTemplate.setRowProperties(0, new TemplateRowProperties(24));
            
            _view.setDataTemplate(dataTemplate);
            dataTemplate.useFixedHeight(true);
            
            // Add the bluetooth list to the screen
            add(_view);
            
            _remoteDevices.setSize(0);
            _discoveryAgent = LocalDevice.getLocalDevice().getDiscoveryAgent();
            _discoveryAgent.startInquiry(DiscoveryAgent.GIAC, ClientScreen.this);            
            
            updateStatus("Searching...");
        }
        catch(BluetoothStateException bse)
        {
            BluetoothJSR82Demo.errorDialog(bse.toString());
        }       
    }
    

    /**
     * Updates the text displayed by the status field
     * 
     * @param message the text to be displayed 
     */
    public void updateStatus(final String message)
    {
        UiApplication.getUiApplication().invokeLater(new Runnable()
        {
            /**
             * @see java.lang.Runnable#run()
             */
            public void run()
            {
                _statusField.setText(_statusField.getText() + "\n" + message);
                //bugrecord.writeStringToLog("updateStatus\n"); did
            }
        });
    }
    
    
    /**
     * Adapter for displaying Bluetooth device information in table format
     */
    private class BluetoothTableModelAdapter extends TableModelAdapter
    {
        
        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#getNumberOfRows()
         */
        public int getNumberOfRows()
        {
            return _remoteDevices.size();
        }
        
        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#getNumberOfColumns()
         */
        public int getNumberOfColumns()
        {
            return 1;
        }
        
        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#doAddRow(Object)
         */
        public boolean doAddRow(Object row)
        {
            _remoteDevices.addElement((RemoteDevice)row);
            return true;
        }
        
        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#doGetRow(int)
         */
        public Object doGetRow(int index)
        {
            return _remoteDevices.elementAt(index);
        }
    }
    

    //DiscoveryListener methods*************************************************
   
    /**
     * @see javax.bluetooth.DiscoveryListener#deviceDiscovered(RemoteDevice, DeviceClass)
     */
    public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass cod)
    {
        bugrecord.writeStringToLog("deviceDiscovered\n");

        if(!_remoteDevices.contains(remoteDevice))
        {
            bugrecord.writeStringToLog("_remoteDevices.contains\n");

            _model.addRow(remoteDevice);
        }
    }
    

    /**
     * @see javax.bluetooth.DiscoveryListener#serviceSearchCompleted(int, int)
     */
    public void inquiryCompleted(int discType)
    {
        bugrecord.writeStringToLog("inquiryCompleted0\n");

        delete(_statusField);        
        //add(_view);
        bugrecord.writeStringToLog("inquiryCompleted1\n");
        
        MenuItem connectToDevice = new MenuItem(new StringProvider("Connect to Device"), 0x230020, 0);
        connectToDevice.setCommand(new Command(new CommandHandler() 
        {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata, Object)
             */
            public void execute(ReadOnlyCommandMetadata metadata, Object context) 
            {
                ClientScreen.this.deleteAll();
                _statusField = new RichTextField();
                ClientScreen.this.add(_statusField);
                try
                {
                    UUID[] uuidSet = {new UUID(_uuid)};
                    int[] attrSet = {0x0100};
                    bugrecord.writeStringToLog("UUID[] uuidSet\n");
                    RemoteDevice _remoteDevice = (RemoteDevice) _model.getRow(_view.getRowNumberWithFocus());
                    _discoveryAgent.searchServices(attrSet, uuidSet, _remoteDevice, ClientScreen.this);
                }
                catch(BluetoothStateException bse)
                {
                    BluetoothJSR82Demo.errorDialog(bse.toString());
                }
            }
        }));
        addMenuItem(connectToDevice);
    }
    

    /**
     * @see javax.bluetooth.DiscoveryListener#servicesDiscovered(int, ServiceRecord[])
     */
    public void servicesDiscovered(int transID, ServiceRecord[] servRecord)
    {
        bugrecord.writeStringToLog("servicesDiscoveredbefore"); 
        bugrecord.writeStringToLog("\n"); 
        _url = servRecord[0].getConnectionURL(0, false);
      bugrecord.writeStringToLog("servicesDiscoveredafter"); 
      bugrecord.writeStringToLog("\n"); 

    }
    

    /**
     * @see javax.bluetooth.DiscoveryListener#serviceSearchCompleted(int, int)
     */
    public void serviceSearchCompleted(int transID, int respCode)
    {
        switch(respCode)
        {

            // If the search is completed and the server URL was found,
            // connect to the URL and handle the connection.
            case DiscoveryListener.SERVICE_SEARCH_COMPLETED:
                if(_url != null)
                {
                    bugrecord.writeStringToLog("serviceSearchCompleted\n");

                    switch(_uuid)
                    {
                        case BluetoothJSR82Screen.SPP_UUID:
                            SPPConnectThread sppThread = new SPPConnectThread();
                            bugrecord.writeStringToLog("SPPConnectThread\n");

                            sppThread.start();
                            break;
                        
                        case BluetoothJSR82Screen.L2CAP_UUID:
                            L2CAPConnectThread l2capThread = new L2CAPConnectThread();
                            l2capThread.start();
                            break;
                            
                        case BluetoothJSR82Screen.OPP_UUID:
                            OPPConnectThread oppThread = new OPPConnectThread();
                            oppThread.start();
                            break;
                    }
                }
                break;

            case DiscoveryListener.SERVICE_SEARCH_DEVICE_NOT_REACHABLE:
                Dialog.alert("Search service device not reachable");
                break;

            case DiscoveryListener.SERVICE_SEARCH_NO_RECORDS:
                Dialog.alert("Service search has found no records");
                break;

            case DiscoveryListener.SERVICE_SEARCH_ERROR:
                Dialog.alert("Service search error");
                break;

            case DiscoveryListener.SERVICE_SEARCH_TERMINATED:
                Dialog.alert("Service search terminated");
                break;
        }
    }    
    
 
    // SPP CLIENT THREAD ********************************************************************
    
    /**
     * A thread that connects to an SPP Server, sends a single lined message
     * and waits for a reponse.
     */
    class SPPConnectThread extends Thread
    { 
        /**
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
        	
            bugrecord.writeStringToLog("SPPConnectThread.start\n");
            try
            {
                StreamConnection connection = null;
                DataOutputStream os = null;
                DataInputStream is = null;
                try 
                {
                    // Send the server a request to open a connection
                    connection = (StreamConnection) Connector.open(_url);
                    updateStatus("[CLIENT] SPP session created");
                    
                    // Send a message to the server
                    String message = "\nJSR-82 CLIENT says hello!";
                    updateStatus("[CLIENT] Sending message....");
                    os = connection.openDataOutputStream();
                    os.write(message.getBytes());
                    os.flush();

                    // Read a message
                    is = connection.openDataInputStream();
                    byte[] buffer = new byte[ 1024 ];
                    int readBytes = is.read(buffer);
                    String receivedMessage = new String(buffer, 0, readBytes);
                    updateStatus("[CLIENT] Message received: " + receivedMessage);                
                }
                finally 
                {
                    os.close();
                    is.close();
                    connection.close();
                    updateStatus("[CLIENT] SPP session closed");
                } 
            }
            catch(IOException ioe)
            {
                BluetoothJSR82Demo.errorDialog(ioe.toString());
            }   
        }
    }
    
   
    // L2CAP CLIENT THREAD ********************************************************************  
   
   /**
    * A thread that connects to a selected L2CAP server, sends a single lined
    * message and waits for a response.
    */
    class L2CAPConnectThread extends Thread
    {
        /**
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            try
            {
                L2CAPConnection connection = null;
                try 
                {
                    // Send the server a request to open a connection
                    connection = (L2CAPConnection) Connector.open(_url);
                    updateStatus("[ClIENT] L2CAP session created");
    
                    // Send a message to the server
                    String message = "\n[CLIENT] JSR-82 CLIENT says hello!";
                    updateStatus("Sending message....");
                    connection.send(message.getBytes());

                    // Read a message
                    byte[] buffer = new byte[ 1024 ];
                    int readBytes = connection.receive(buffer);
                    String receivedMessage = new String(buffer, 0, readBytes);
                    updateStatus("[CLIENT] Message received: " + receivedMessage);
                }
                finally 
                {
                    connection.close();
                    updateStatus("[ClIENT] L2CAP session closed");
                }
            }
            catch(IOException ioe)
            {
                BluetoothJSR82Demo.errorDialog(ioe.toString());
            }
        }
    }
      
    
    // OPP CLIENT THREAD ********************************************************************   
    
    /**
     * A thread that connects to a selected OPP Server and pushes a 
     * single file with metadata.
     */
    class OPPConnectThread extends Thread
    {
        /**
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            try
            {
                Connection connection = null;
                OutputStream outputStream = null;
                Operation putOperation = null;
                ClientSession cs = null;
                try
                {
                    // Send a request to the server to open a connection
                    connection = Connector.open(_url);
                    cs = (ClientSession) connection;
                    cs.connect(null);
                    updateStatus("[CLIENT] OPP session created");
                
                    // Send a file with meta data to the server
                    byte filebytes[] = "[CLIENT] Hello..".getBytes();
                    HeaderSet hs = cs.createHeaderSet();
                    hs.setHeader(HeaderSet.NAME, "test.txt");
                    hs.setHeader(HeaderSet.TYPE, "text/plain");
                    hs.setHeader(HeaderSet.LENGTH, new Long(filebytes.length));

                    putOperation = cs.put(hs);
                    updateStatus("[CLIENT] Pushing file: " + "test.txt");
                    updateStatus("[CLIENT] Total file size: " + filebytes.length + " bytes");

                    outputStream = putOperation.openOutputStream();
                    outputStream.write(filebytes);
                    updateStatus("[CLIENT] File push complete");                    
                }
                finally 
                {
                    outputStream.close();                    
                    putOperation.close();
                    cs.disconnect(null);
                    connection.close();
                    updateStatus("[CLIENT] Connection Closed");
                }
            }
            catch(Exception e)
            {
                BluetoothJSR82Demo.errorDialog(e.toString());
            }
        }
    }  
}
