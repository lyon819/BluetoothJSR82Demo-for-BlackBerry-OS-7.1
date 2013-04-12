/*
 * ServerScreen.java
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
import javax.obex.*;
import javax.bluetooth.*;
import javax.microedition.io.*;

import net.rim.device.api.command.*;
import net.rim.device.api.io.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.util.*;

/**
 * Server Screen class for the BluetoothJSR82Demo application
 */
public final class ServerScreen extends MainScreen
{  
    private static final String SERVICE_NAME_SPP = "SPPDemo";
    private static final String SERVICE_NAME_OPP = "OPPDemo";
    private static final String SERVICE_NAME_L2CAP = "L2CAPDemo";
    
    private int _uuid;
    private RichTextField _statusField;

    /**
     * Creates a new ServerScreen object
     */
    public ServerScreen(int uuid)
    {
        setTitle("Bluetooth JSR82 Demo Server");

        _uuid = uuid;
        _statusField = new RichTextField(Field.NON_FOCUSABLE);
        add(_statusField);
        
        MenuItem infoScreen = new MenuItem(new StringProvider("Info Screen"), 0x230010, 0);
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
            // Make the device discoverable
            LocalDevice device = LocalDevice.getLocalDevice();
            
            // Store the current mode so it can be restored later
            int mode = device.getDiscoverable();
            if (mode != DiscoveryAgent.GIAC)
            {
                device.setDiscoverable(DiscoveryAgent.GIAC);
            } 
            switch (_uuid) 
            {
                case BluetoothJSR82Screen.SPP_UUID:
                    SPPServerThread sppThread= new SPPServerThread();
                    sppThread.start();
                    break;
                    
                case BluetoothJSR82Screen.OPP_UUID:
                    OPPServerThread oppThread = new OPPServerThread();
                    oppThread.start();
                    break;
                    
                case BluetoothJSR82Screen.L2CAP_UUID:
                    L2CAPServerThread l2capThread = new L2CAPServerThread();
                    l2capThread.start();
                    break;
            }  
        }
        catch(BluetoothStateException bse)
        {
            BluetoothJSR82Demo.errorDialog(bse.toString());
        }
    }
    

    /**
     * Updates the text displayed by the status field
     * 
     * @param message The text to be displayed
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
            }
        });
    }
    

    //SPP SERVER THREAD *******************************************************************
    
    /**
     * A thread that opens an SPP connection, awaits client requests, accepts
     * incoming messages, and sends a response.
     */
    private class SPPServerThread extends Thread
    {
        /**
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            try
            {
                StreamConnection connection = null;
                DataOutputStream os = null;
                DataInputStream is = null;
                try 
                {
                    UUID uuid = new UUID(_uuid);
                    LocalDevice local = LocalDevice.getLocalDevice();
                    updateStatus("[SERVER] Device Address: " + local.getBluetoothAddress());
                    updateStatus("[SERVER] Device Name: " + local.getFriendlyName());
                    updateStatus("[SERVER] Listening for Client...");

                    // Open a connection and wait for client requests
                    StreamConnectionNotifier service = (StreamConnectionNotifier) Connector.open("btspp://localhost:" + uuid + ";name="
                                    + SERVICE_NAME_SPP);
                    connection = service.acceptAndOpen();
                    updateStatus("[SERVER] SPP session created");
                
                    // Read a message
                    is = connection.openDataInputStream();
                    byte[] buffer = new byte[1024];
                    int readBytes = is.read(buffer);
                    String receivedMessage = new String(buffer, 0, readBytes);
                    updateStatus("[SERVER] Message received: " + receivedMessage);
                
                    // Send a message
                    String message = "\nJSR-82 SERVER says hello!";
                    updateStatus("[SERVER] Sending message....");
                    os = connection.openDataOutputStream();
                    os.write(message.getBytes());
                    os.flush();
                }
                finally 
                {    
                    os.close();
                    is.close();
                    updateStatus("[SERVER] SPP session closed");
                }
            }
            catch(IOException ioe)
            {
                BluetoothJSR82Demo.errorDialog(ioe.toString());
            }
        }
    }
    
    
    //L2CAP SERVER THREAD *******************************************************************

    /**
     * A thread that implements an L2CAP server which accepts a single
     * line message from an L2CAP client and sends a single line response
     * back to the client.
     */
    private class L2CAPServerThread extends Thread
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
                    UUID uuid = new UUID(_uuid);
                    LocalDevice local = LocalDevice.getLocalDevice();
                    updateStatus("[SERVER] Device Address: " + local.getBluetoothAddress());
                    updateStatus("[SERVER] Device Name: " + local.getFriendlyName());
                    updateStatus("[SERVER] Listening for Client...");
                
                    // Open a connection and wait for client requests
                    L2CAPConnectionNotifier service = (L2CAPConnectionNotifier) Connector.open("btl2cap://localhost:" + uuid + ";name="
                                    + SERVICE_NAME_L2CAP);
                    connection = service.acceptAndOpen();
                    updateStatus("[SERVER] L2CAP connection established");

                    // Read a message
                    byte[] buffer = new byte[ 1024 ];
                    int readBytes = connection.receive(buffer);
                    String receivedMessage = new String(buffer, 0, readBytes);
                    updateStatus("[SERVER] Message received: " + receivedMessage);                    
                
                    // Send a message
                    String message = "\nJSR-82 SERVER says hello!";
                    updateStatus("[SERVER] Sending message....");
                    connection.send(message.getBytes());
                }
                finally 
                {
                    connection.close();
                    updateStatus("[SERVER] L2CAP session closed");
                }
            }
            catch(IOException ioe)
            {
                BluetoothJSR82Demo.errorDialog(ioe.toString());
            }
        }
    }
    
    
    //OPP SERVER THREAD *******************************************************************   
 
    /**
     * A thread that opens an OPP connection, awaits client requests, accepts 
     * incoming messages, and sends a response.
     */
    private class OPPServerThread extends Thread
    { 
        /**
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            try
            {
                UUID uuid = new UUID(_uuid);
                LocalDevice local = LocalDevice.getLocalDevice();
                updateStatus("[SERVER] Device Address: " + local.getBluetoothAddress());
                updateStatus("[SERVER] Device Name: " + local.getFriendlyName());
                
                // Open a connection and wait for client requests
                SessionNotifier sessionNotifier = (SessionNotifier) Connector.open("btgoep://localhost:" + uuid + ";name=" 
                                + SERVICE_NAME_OPP);
                updateStatus("[SERVER] Waiting for Client to connect...");
                sessionNotifier.acceptAndOpen(new ObexServerRequestHandler());
            }
            catch(IOException ioe)
            {
                BluetoothJSR82Demo.errorDialog(ioe.toString());
            }
        }
    }
    

    /**
     * A class that handles incoming requests from a connected OPP client device
     */
    class ObexServerRequestHandler extends ServerRequestHandler
    {
        /*
         * @see javax.obex.ServerRequestHandler#onConnect(HeaderSet, HeaderSet)
         */
        public int onConnect(HeaderSet request, HeaderSet reply)
        {
            updateStatus("[SERVER] OPP session created");
            return ResponseCodes.OBEX_HTTP_OK;
        }
        

        /**
         * @see javax.obex.ServerRequestHandler#onPut(Operation)
         */
        public int onPut(Operation op)
        {
            // Display information about the data received
            try
            {
                // Read the meta data of a received file
                InputStream is = op.openInputStream();
                updateStatus("[SERVER] File Name: " + op.getReceivedHeaders().getHeader(HeaderSet.NAME));
                updateStatus("[SERVER] File Type: " + op.getType());            
                
                // Read the content of a received file
                byte b[] = new byte[1024];
                int len;
                updateStatus("[SERVER] File Content: ");
                while(is.available() > 0 && (len = is.read(b)) > 0)
                {
                    updateStatus(new String(b, 0, len));
                }              
            }
            catch(IOException ioe)
            {
                BluetoothJSR82Demo.errorDialog(ioe.toString());
            }

            return ResponseCodes.OBEX_HTTP_OK;
        }
        

        /**
         * @see javax.obex.ServerRequestHandler#onDisconnect(HeaderSet, HeaderSet)
         */
        public void onDisconnect(HeaderSet req, HeaderSet resp)
        {
            updateStatus("[SERVER] OPP connection closed");
        }
    }
}
