package controller;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;

import java.io.Reader;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InterruptedIOException;

public class ConfigManager {
	
	private static FileReader fileReader;
	private static BufferedReader bufferedReader;
	private static ConcurrentHashMap<String, ConcurrentHashMap<String, ConcurrentHashMap<String, String[]>>> configData;
	private static String defaultConfigFile;
	
	public static void initialize() {
		
		defaultConfigFile = Constants.defaultConfigFile;
		configData = new ConcurrentHashMap<String, ConcurrentHashMap<String, ConcurrentHashMap<String, String[]>>>();
		initializeReaders(defaultConfigFile);
		readFile(defaultConfigFile);
		closeReaders();
	}
	
	public static void readCustomFile(String fileName) {
		initializeReaders(fileName.trim());
		readFile(fileName);
		closeReaders();
	}
	
	public static String[] getValue(String groupName, String elementName) {
		String[] values;
		ConcurrentHashMap<String, String[]> data;
		
		groupName = groupName.trim();
		elementName = elementName.trim();
		data = configData.get(defaultConfigFile).get(groupName);
		if(data == null) {
                    return null;
		}
		values = data.get(elementName);
		return values;
	}
	
	public static String[] getValue(String fileName, String groupName, String elementName) {
		String[] values;
		ConcurrentHashMap<String, String[]> data;
		
		groupName = groupName.trim();
		elementName = elementName.trim();
		data = configData.get(fileName).get(groupName);
		if(data == null) {
			System.out.println("Error: no group name");
			return null;
		}
		values = data.get(elementName);
		if(values == null) {
			System.out.println("Error: I have no details");
		}
		return values;
	}
	
	private static void readFile(String fileName) {
		String line = null;
		String groupName = null;
		ConcurrentHashMap<String, ConcurrentHashMap<String, String[]>> tempData = new ConcurrentHashMap<String, ConcurrentHashMap<String, String[]>>();
		do {
			try {
				bufferedReader.mark(999);
				line = bufferedReader.readLine();
				if(line == null) {
					break;
				}
				line = line.trim();
			}
			catch(InterruptedIOException in) {
				try {
					bufferedReader.reset();
				}
				catch(IOException ie) {

				}
				continue;
			}
			catch(IOException ioe) {
				ioe.printStackTrace();
			}
			if(!(line.startsWith("#") || line.equals(""))) {
				if(line.startsWith("[") && line.endsWith("]")) {
					groupName = line.replace('[', ' ');
					groupName = groupName.replace(']', ' ');
					groupName = groupName.trim();
					ConcurrentHashMap<String, String[]> data = parseElementValues();
					tempData.put(groupName, data);
				}
			}
		}
		while(true);
		configData.put(fileName, tempData);
	}
	
	private static void initializeReaders(String filename) {
		try {
			fileReader = new FileReader("conf/" + filename);
		}
		catch(FileNotFoundException e) {
                    System.out.println("ConfigManager.initializeReaders, File not Find " + filename);
                    System.exit(0);
		}

		bufferedReader = new BufferedReader((Reader)fileReader);
		
	}
	private static void closeReaders() {
		try {
			bufferedReader.close();
		}
		catch(Exception E) {
			E.printStackTrace();
		}
	}

        private static ConcurrentHashMap<String, String[]> parseElementValues() {
		String line = null;
		String[] values = null;
		String[] keyValuePair = null;
		
		ConcurrentHashMap<String, String[]> elementValues = new ConcurrentHashMap<String, String[]>();
		do {
			try {
				bufferedReader.mark(999);
				line = bufferedReader.readLine();
				if(line == null) {
					break;
				}
				line = line.trim();
			}
			catch(IOException ioe) {
				try {
					bufferedReader.reset();
				} catch(Exception ie) {
					ie.printStackTrace();
				}
				ioe.printStackTrace();
			}
			if(line.startsWith("[") && line.endsWith("]")) {
				try {
					bufferedReader.reset();
				} catch(Exception ee) {
					ee.printStackTrace();
				} finally {
					break;
				}
			}
			if(!(line.startsWith("#") || line.equals(""))) {
				keyValuePair = line.split("=");
				values = keyValuePair[1].split(",");
				elementValues.put(keyValuePair[0].trim(), values);
			}
		}
		while(true);
		
		return elementValues;
	}
		
	public static void printTable() {
		ConcurrentHashMap<String, String[]> data;
		String groupName;
		String elementName;
		String[] values;
		int total;
		Iterator<String> it;
		Iterator<String> iterator = configData.get(defaultConfigFile).keySet().iterator();
		while(iterator.hasNext()) {
			groupName = iterator.next();
			System.out.println("[" + groupName + "]");
			data = configData.get(defaultConfigFile).get(groupName);
			
			it = data.keySet().iterator();
			while(it.hasNext()) {
				elementName = it.next();
				values = data.get(elementName);
				total = values.length;
				System.out.print(elementName + "=");
				for(int i = 0; i < total; ++i) {
					System.out.print(values[i] + ", ");
				}
				System.out.println();
			}
		}
	}
		
	public static ConcurrentHashMap<String, ConcurrentHashMap<String, ConcurrentHashMap<String, String[]>>> getConfigData() {
		return configData;
	}
}