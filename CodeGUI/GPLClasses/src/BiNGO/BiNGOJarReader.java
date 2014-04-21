package BiNGO;
/*
  File: TextJarReader.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies
  
  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.
  
  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute 
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute 
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute 
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.
  
  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

// TextJarReader.java


//  $Revision: 7760 $ 
//  $Date: 2006-06-26 18:28:49 +0200 (Mon, 26 Jun 2006) $
//  $Author: mes $

import java.io.*;
import java.util.*;
import java.util.jar.*;
import java.net.*;

import cytoscape.*;
//---------------------------------------------------------------------------
public class BiNGOJarReader {
  String filename;
  InputStreamReader reader;
  StringBuffer sb;
//---------------------------------------------------------------------------
public BiNGOJarReader (String URI) throws IOException {
  sb = new StringBuffer ();
  filename = URI.substring(URI.lastIndexOf("/")+1);
  //System.out.println(filename);
  //we've created a new class loader that is used to load plugins and also
  //should bootstrap to the class loader that loads the Cytoscape core classes
  //However, we can't use it until it's been instantiated, so if we get a null
  //reference then we'll fall back to the class loader that loaded this class
  URL url = getClass().getResource("/" + filename);
  JarURLConnection juc = (JarURLConnection) url.openConnection ();
  JarFile jarFile = juc.getJarFile();
  InputStream is = jarFile.getInputStream (jarFile.getJarEntry (filename));
  reader = new InputStreamReader (is);

} // ctor
//-----------------------------------------------------------------------------------
public int read () throws IOException
{
  //System.out.println ("-- reading " + filename);
  char [] cBuffer = new char [1024];
  int bytesRead;
  while ((bytesRead = reader.read (cBuffer, 0, 1024)) != -1)
    sb.append (new String (cBuffer, 0, bytesRead));

  return sb.length ();

} // read
//---------------------------------------------------------------------------
public String getText ()
{
  return sb.toString ();

} // read
//---------------------------------------------------------------------------
} // TextJarReader


