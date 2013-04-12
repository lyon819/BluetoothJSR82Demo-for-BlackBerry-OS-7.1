/*
 * BluetoothJSR82Demo.java
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

import java.io.IOException;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

/**
 * A sample application that demonstrates the JSR 82 API by establishing a
 * Bluetooth connection between two BlackBerry smartphone devices through a
 * given protocol.
 * 
 * This application uses the JSR 82 API to allow a device to act as a server or
 * a client in a Bluetooth connection. The server waits for a connection from a
 * client while the client inquires as to the available devices and chooses
 * which device to connect to. The server is identified by a service name
 * (labeled as SERVICE_NAME), which can be retrieved in accordance with
 * the JSR 82 specifications.
 */
public class BluetoothJSR82Demo extends UiApplication
{    
    /**
     * Entry point for application
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args)throws IOException, InterruptedException {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        BluetoothJSR82Demo app = new BluetoothJSR82Demo();
        app.enterEventDispatcher();
    }   

    private BluetoothJSR82Demo() throws IOException, InterruptedException {
		pushScreen (new BluetoothJSR82Screen());        
	} 
    
    /**
     * Presents a dialog to the user with a given message
     * 
     * @param message The text to display
     */
    public static void errorDialog(final String message)
    {
        UiApplication.getUiApplication().invokeLater(new Runnable()
        {
            /**
             * @see java.lang.Runnable#run()
             */
            public void run()
            {
                Dialog.alert(message);
            }
        });
    }
}
