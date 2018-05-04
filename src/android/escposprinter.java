package cordova.plugin.escposprinter;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Set;
import java.util.UUID;
import java.io.*;
import java.net.*;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Vector;

import com.zebra.sdk.comm.BluetoothConnectionInsecure;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.PrinterStatus;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;

/**
 * This class echoes a string called from JavaScript.
 */

public class escposprinter extends CordovaPlugin {

    private static final String LOG_TAG = "escposprinter";

    public escposprinter() {
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("printAndroid")) {
            JSONObject arg_object = args.getJSONObject(0);
            String dataPrint = arg_object.getString("dataPrint");
            String ipAddress = arg_object.getString("ipAddress");
            Boolean isShowLogo = arg_object.getBoolean("isShowLogo");

            if (!SocketCheck(ipAddress)) {
                callbackContext.error("Printer is not available");
                return true;
            }
            try {
                Socket clientSocket = new Socket(ipAddress, 9100);
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

                if (isShowLogo == true) {
                    byte[] cmdAlignmentCenter = new byte[] { 0x1B, 0x61, 0x1 };
                    byte[] cmdShowLogo = new byte[] { 0x1C, 0x70, 0x1, 0x0 };
                    outToServer.write(cmdAlignmentCenter);
                    outToServer.write(cmdShowLogo);
                    outToServer.write(dataPrint.getBytes("CP-874"));
                    outToServer.writeBytes("");

                } else {
                    outToServer.write(dataPrint.getBytes("CP-874"));
                }

                outToServer.flush();
                outToServer.close();
                clientSocket.close();
                callbackContext.success("Print Completed");
                
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage());
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    boolean SocketCheck(String ipAddress) {

        boolean available = true;
        try {
            Socket s = new Socket(ipAddress, 9100);
            if (s.isConnected()) {
                s.close();
            }
        } catch (UnknownHostException e) { // unknown host 
            available = false;
            //s = null;
        } catch (IOException e) { // io exception, service probably not running 
            available = false;
            //s = null;
        } catch (NullPointerException e) {
            available = false;
            //s=null;
        }
        return available;
    }
}