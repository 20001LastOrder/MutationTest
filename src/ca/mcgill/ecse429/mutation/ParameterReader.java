package ca.mcgill.ecse429.mutation;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ParameterReader {
	
	
	public static void main(String[] args) { 
		Map<String, String> map = new HashMap<String, String>();
		String fileName = "/Users/tianzhufu/Desktop/ECSE429/MutationTest/parameter.csv";	
		BufferedReader br = null;
		String line = "";
		String csvSplitor =",";
		try {
			br = new BufferedReader(new FileReader(fileName));
			while((line= br.readLine())!= null) {
				String[] content = line.split(csvSplitor);
				map.put(content[0],content[1]);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println(map);
	}
	
}

