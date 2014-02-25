/**
* Java LX200Server class.
* Copyright (C) 1999-2001  Mark Hale
*/

package JSci.astro.telescope;

import java.io.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;

public final class LX200Server extends UnicastRemoteObject implements LX200Remote {
        private final LX200 lx200;

        public static void main(String arg[]) {
                if(arg.length!=2) {
                        System.out.println("Usage: LX200Server <com port> <remote name>");
                        return;
                }
                System.setSecurityManager(new RMISecurityManager());
                try {
                        Registry localRegistry=LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
                        localRegistry.rebind(arg[1],new LX200Server(new LX200(arg[0])));
                } catch(IOException e) {
                        e.printStackTrace();
                }
        }
        /**
        * Constructs a telescope server.
        * @param telescope the telescope to serve.
        */
        public LX200Server(LX200 telescope) throws RemoteException {
                lx200=telescope;
        }
        public void setFocusRate(int rate) throws IOException, RemoteException {
                lx200.setFocusRate(rate);
        }
        public void startFocus(int direction) throws IOException, RemoteException {
                lx200.startFocus(direction);
        }
        public void stopFocus() throws IOException, RemoteException {
                lx200.stopFocus();
        }
        public void setSlewRate(int rate) throws IOException, RemoteException {
                lx200.setSlewRate(rate);
        }
        public void startSlew(int direction) throws IOException, RemoteException {
                lx200.startSlew(direction);
        }
        public void stopSlew(int direction) throws IOException, RemoteException {
                lx200.stopSlew(direction);
        }
        public String getRA() throws IOException, RemoteException {
                return lx200.getRA();
        }
        public String getDec() throws IOException, RemoteException {
                return lx200.getDec();
        }
        public void setObjectCoords(String ra,String dec) throws IOException, RemoteException {
                lx200.setObjectCoords(ra,dec);
        }
        public int slewToObject() throws IOException, RemoteException {
                return lx200.slewToObject();
        }
        public void syncCoords() throws IOException, RemoteException {
                lx200.syncCoords();
        }
}

