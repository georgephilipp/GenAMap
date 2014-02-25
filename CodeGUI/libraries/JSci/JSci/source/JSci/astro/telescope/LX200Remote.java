/**
* Java LX200Remote class.
* Copyright (C) 1999-2001  Mark Hale
*/

package JSci.astro.telescope;

import java.io.*;
import java.rmi.*;

public interface LX200Remote extends Remote {
        void setFocusRate(int rate) throws IOException, RemoteException;
        void startFocus(int direction) throws IOException, RemoteException;
        void stopFocus() throws IOException, RemoteException;
        void setSlewRate(int rate) throws IOException, RemoteException;
        void startSlew(int direction) throws IOException, RemoteException;
        void stopSlew(int direction) throws IOException, RemoteException;
        String getRA() throws IOException, RemoteException;
        String getDec() throws IOException, RemoteException;
        void setObjectCoords(String ra,String dec) throws IOException, RemoteException;
        int slewToObject() throws IOException, RemoteException;
        void syncCoords() throws IOException, RemoteException;
}

