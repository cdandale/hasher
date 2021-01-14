package com.central1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class StringMasker {

	public static void main(String[] args) {
		StringMasker sObs = new StringMasker();

		String[] arrayOfStringToMask = { "Rahul", "Prathamesh", "Amita", "Done" };

		System.out.println("Masking Single String");
		System.out.println("----------------------------------------------------------------");
		System.out.println(sObs.ObfuscateSingleString("Rahul"));
		System.out.println("----------------------");
// 		 Use this function to print the result
		System.out.println("Masking Array Of String");
		System.out.println("----------------------------------------------------------------");
		sObs.ObfuscateArrayOfString(arrayOfStringToMask);
		sObs.printStringArray(sObs.ObfuscateArrayOfString(arrayOfStringToMask));

		try {
			sObs.parseJsonFile(sObs.readJsonFileToString());
			System.out.println();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected String[] ObfuscateArrayOfString(String[] arrayOfStringToMask) {

		String stringToMask;

		if (arrayOfStringToMask.length == 0) {
			String[] returnError = { "Invalid Input" };
			return returnError;
		}

		String[] returnArray = new String[arrayOfStringToMask.length];

		for (int i = 0; i < arrayOfStringToMask.length; i++) {
			stringToMask = arrayOfStringToMask[i];
			returnArray[i] = ObfuscateSingleString(stringToMask);
		}
		return returnArray;
	}

	protected String ObfuscateSingleString(String stringToChange) {

		byte[] sha256hex = DigestUtils.sha256(stringToChange);
		return bytesToHex(sha256hex);
	}

	private String bytesToHex(byte[] hash) {

		StringBuilder hexString = new StringBuilder(2 * hash.length);

		for (int i = 0; i < hash.length; i++) {
			String hex = Integer.toHexString(0xff & hash[i]);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}

	protected void printStringArray(String arrayToPrint[]) {

		for (String maskString : arrayToPrint) {
			System.out.println(maskString);
		}
	}

	private String readJsonFileToString() throws IOException {
		return readFile("C:/anand/JsonToMask.json");
	}

	private String readFile(String file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");

		try {
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}

			return stringBuilder.toString();
		} finally {
			reader.close();
		}
	}

	@SuppressWarnings("unchecked")
	private void parseJsonFile(String JsonToMask) {
		try {
			Object objToRead = new JSONParser().parse(JsonToMask);
			JSONObject jsonObjReader = (JSONObject) objToRead;
			
			JSONObject jsonObjWriter = new JSONObject(); 
			
			Map<Object, String> maskedJsonData = new LinkedHashMap<Object, String>(); 
			
			
//			Map dataToMaskInJson = ((Map) jsonObjReader.get("Data"));
//			Iterator<Map.Entry> jsonIterator = dataToMaskInJson.entrySet().iterator();
			Map dataToMaskInJson = ((Map<?, ?>) jsonObjReader.get("Data"));
			@SuppressWarnings("rawtypes")
			Iterator<Map.Entry> jsonIterator = dataToMaskInJson.entrySet().iterator();
			
			while (jsonIterator.hasNext()) {
				Map.Entry pair = jsonIterator.next();
				System.out.println(pair.getKey() + " : " + pair.getValue());
				maskedJsonData.put(pair.getKey(), ObfuscateSingleString((String)pair.getValue()));
			}
			
			// Write the Json Object
			jsonObjWriter.put("Data", maskedJsonData); 
			System.out.println("Final Output -------------");
			System.out.println(jsonObjWriter.toString());

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
