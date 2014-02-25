/**
* Java LX200DebugServer class.
* Copyright (C) 1999-2001  Mark Hale
*/

package JSci.astro.telescope;

import java.io.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;

public final class LX200DebugServer extends UnicastRemoteObject implements LX200Remote {
        private String ra="00:00:00";
        private String dec="+00*00'00";

        public static void main(String arg[]) {
                if(arg.length!=1) {
                        System.out.println("Usage: LX200DebugServer <remote name>");
                        return;
                }
                System.setSecurityManager(new RMISecurityManager());
                try {
                        Registry localRegistry=LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
                        localRegistry.rebind(arg[0],new LX200DebugServer());
                } catch(IOException e) {
                        e.printStackTrace();
                }
        }
        /**
        * Constructs a telescope debug server.
        */
        public LX200DebugServer() throws RemoteException {}
        public void setFocusRate(int rate) throws IOException, RemoteException {
                System.err.println("setFocusRate: "+rate);
        }
        public void startFocus(int direction) throws IOException, RemoteException {
                System.err.println("startFocus: "+direction);
        }
        public void stopFocus() throws IOException, RemoteException {
                System.err.println("stopFocus");
        }
        public void setSlewRate(int rate) throws IOException, RemoteException {
                System.err.println("setSlewRate: "+rate);
        }
        public void startSlew(int direction) throws IOException, RemoteException {
                System.err.println("startSlew: "+direction);
        }
        public void stopSlew(int direction) throws IOException, RemoteException {
                System.err.println("stopSlew: "+direction);
        }
        public String getRA() throws IOException, RemoteException {
                System.err.println("getRA");
                return ra;
        }
        public String getDec() throws IOException, RemoteException {
                System.err.println("getDec");
                return dec;
        }
        public void setObjectCoords(String newRa,String newDec) throws IOException, RemoteException {
                System.err.println("setObjectCoords: "+newRa+", "+newDec);
                ra=newRa;
                dec=newDec;
        }
        public int slewToObject() throws IOException, RemoteException {
                System.err.println("slewToObject");
                return 0;
        }
        public void syncCoords() throws IOException, RemoteException {
                System.err.println("syncCoords");
        }
}

