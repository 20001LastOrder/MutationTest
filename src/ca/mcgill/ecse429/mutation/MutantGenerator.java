package ca.mcgill.ecse429.mutation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class MutantGenerator {
	public static String DETECTION_SEP = ":\\s|:";
	public static String MUTANTS_SEP = ",\\s|,";
	public static String SINGLE_LINE_COMMENT = "\\/\\/.*[^a\\\\n]+";
	public static String STRING_MATCH_PATTERN = "\\\".*\\\"";
	public static String MULTI_LINE_START = "/*";
	public static String MULTI_LINE_START_PATTERN = "\\/\\*.*[^a\\\\n]+";
	public static String MULTI_LINE_END = "*/";
	public static String MULTI_LINE_END_PATTERN = ".*\\/*|\\/\\*";
	
	public static final char REPLACE_SYMBOL = '.';
	
	private HashMap<String, List<String>> mutationRules;

	public MutantGenerator(String ruleFileName) {
		mutationRules = new HashMap<String, List<String>>();
		
		// generate a set of mutation rule original -> mutant from a file
		var ruleFile = new File(ruleFileName);
		try {
			var in = new Scanner(ruleFile);
			while(in.hasNextLine()) {
				var line = in.nextLine().split(DETECTION_SEP);
				var pattern = line[0];
				var mutants = Arrays.asList(line[1].split(MUTANTS_SEP));
				mutationRules.put(pattern, mutants);
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	// create a set of statistics for the list of mutant information
	public static List<String> MutantsStat(List<MutantInformation> infos) {
		var results = new ArrayList<String>();
		var statMap = new HashMap<String, Integer>();
		
		results.add("Type, Count");
		for(var info : infos) {
			statMap.put(info.getMutantInfo(), statMap.getOrDefault(info.getMutantInfo(), 0) + 1);
		}
		
		for(var pair : statMap.entrySet()) {
			results.add(pair.getKey() + ", " + pair.getValue());
		}
		
		return results;
	}
	
	// generate of set of mutant and return the mutant information for a mutation file
	public List<MutantInformation> GenerateMutationFromFile(String fileName){
		var mutantInfo = new ArrayList<MutantInformation>();
		var inputFile = new File(fileName);
		var singleLineCommentPattern = Pattern.compile(SINGLE_LINE_COMMENT);
		var stringPattern = Pattern.compile(STRING_MATCH_PATTERN);
		var multiLineCommentStartPattern = Pattern.compile(MULTI_LINE_START_PATTERN);
		var multiLineCommentEndPattern = Pattern.compile(MULTI_LINE_END_PATTERN);

		try {
			var in = new Scanner(inputFile);
			var inMultiLineComment = false;
			int lineNumber = 0;
			int id = 0;
			while(in.hasNextLine()) {
				var line = in.nextLine();
				lineNumber ++;
				// ignore comment and string in the code: they are irrelevant for the mutation test
				line = replaceMatchers(singleLineCommentPattern, line);
				line = replaceMatchers(stringPattern, line);
				
				//check multiline comment
				if(inMultiLineComment) {
					if(line.indexOf(MULTI_LINE_END) > 0) {
						line = replaceMatchers(multiLineCommentEndPattern, line);
						inMultiLineComment = false;
					}else {
						continue;
					}
				}else if(!inMultiLineComment && line.indexOf(MULTI_LINE_START) > 0) {
					line = replaceMatchers(multiLineCommentStartPattern, line);
					inMultiLineComment = true;
				}
				
				
				for(var pattern : mutationRules.keySet()) {
					int index = line.indexOf(pattern);
					while(index >= 0) {
						for(var mutant : mutationRules.get(pattern)) {
							var info = new MutantInformation(lineNumber, index, pattern, mutant);
							info.setId(id++);
							mutantInfo.add(info);
						}
					   index = line.indexOf(pattern, index+1);
					}
				}
			}	
			in.close();
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		return mutantInfo;
	}
	
	// replace certain pattern in the string with a list of default symbol of same length
	private String replaceMatchers(Pattern pattern, String line) {
		var result = line;
		var matcher = pattern.matcher(line);
		while(matcher.find()) {
			var matchLength = matcher.end() - matcher.start();
			var replaces = new String(new char[matchLength]).replace('\0' ,REPLACE_SYMBOL);
			result = result.replace(matcher.group(), replaces);
		}
		
		return result;
	}
	
	public String MutationRulesToString() {
		var output = "";
		for(var key : mutationRules.keySet()) {
			output += key+": ";
			for(var mutant : mutationRules.get(key)) {
				output += mutant + ", ";
			}
			output += "\n";
		}
		return output;
	}
}
