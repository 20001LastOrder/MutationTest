package ca.mcgill.ecse429.mutation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.junit.runner.Result;

public final class Utils {
	
	// output a list of string (lines) to a file
	public static void outputToFile(String filename, List<String> contents) throws FileNotFoundException {
		var printer = new PrintWriter(filename);
		contents.forEach(line -> printer.println(line));
		printer.close();
	}
	
	// read a file and return a list of lines
	public static List<String> readFile(String filename){
		var contents = new ArrayList<String>();
		var file = new File(filename);
			
		try {
			var in = new Scanner(file);
			while(in.hasNextLine()) {
				contents.add(in.nextLine());
			}
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return contents;
	}
	
	/**
	 * read a list of mutantInformation from a file
	 * @param filename
	 * @return
	 */
	public static List<MutantInformation> readMutantInfomation(String filename){
		var infos = new ArrayList<MutantInformation>();
		
		//read the information
		var file = new File(filename);
		try {
			var in = new Scanner(file);
			// skip the first header line
			in.nextLine(); 
			
			while(in.hasNextLine()) {
				var line = in.nextLine().replace("\n", "");
				// if get an empty line, that means we reach the end of mutant information part
				if("".equals(line)) {
					break;
				}
				var infoContents = line.split(",\\s|,");
				// the format of a line is id, lineNumber, charNumber, original, mutant
				var info = new MutantInformation(Integer.parseInt(infoContents[1]), 
							Integer.parseInt(infoContents[2]), infoContents[3], infoContents[4]);
				info.setId(Integer.parseInt(infoContents[0]));
				infos.add(info);
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return infos;
	}
	
	/**
	 * generate mutation files provide original file and a set of mutant information
	 * @param mutantInfos: a set of mutant information generated for the file
	 * @param originalFilename: the original file information
	 */
	public static void generateMutantFiles(String sourcePath ,List<MutantInformation> mutantInfos, String originalFilename) {
		final var mutantDir = "Mutants";
		var originalContents = readFile(sourcePath + originalFilename);
		
		//create mutant folder
		//new File(mutantDir).mkdir();
		
		//copy original file first
		var originalFileFolder = mutantDir + "/Original/";
		//new File(originalFileFolder).mkdir();
		try {
			createDirectoriesFor(originalFileFolder + "/" + originalFilename);
			outputToFile(originalFileFolder + "/" + originalFilename, originalContents);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		for(var info : mutantInfos) {
			var mutatedContents = generateMutantContent(info, originalContents);
			//create specific mutant info
			var fileDir = mutantDir + "/Mutant_" + info.getId();
			new File(fileDir).mkdir();
			try {
				createDirectoriesFor(fileDir + "/" + originalFilename);
				outputToFile(fileDir + "/" + originalFilename, mutatedContents);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
	}
	
	public static MutationParameter readParameters(String fileName) throws IllegalArgumentException, IllegalAccessException {
		var parameter = new MutationParameter();
		Map<String, String> map = new HashMap<String, String>();
		BufferedReader br = null;
		String line = "";
		String csvSplitor =",";
		try {
			br = new BufferedReader(new FileReader(fileName));
			while((line= br.readLine())!= null) {
				String[] content = line.split(csvSplitor);
				map.put(content[0].strip().toLowerCase(),content[1]);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		for(var field : parameter.getClass().getFields()) {
			var value = map.get(field.getName().toLowerCase());
			field.set(parameter, value);
		}
		return parameter;
	}
	
	public static void createDirectoriesFor(String filePath) throws IOException {
		Path fp = Paths.get(filePath);
	    Files.createDirectories(fp.getParent());
	}
	
	/**
	 * Given original content of a file, generate a mutated content based on the mutant information
	 * @param info
	 * @param originalContents
	 * @return
	 */
	public static List<String> generateMutantContent(MutantInformation info, List<String> originalContents) {
		var mutantContents = new ArrayList<String>(originalContents);
		
		// line number starts from 0
		var mutantLine = new StringBuilder(mutantContents.get(info.getLineNumber() - 1));
		if(info.getOriginalInfo().charAt(0) != mutantLine.charAt(info.getCharNumber())) {
			throw new RuntimeException("original information does not match");
		}
		
		var startIndex = info.getCharNumber();
		var endIndex = info.getCharNumber() + info.getMutantInfo().length();
		mutantLine.replace(startIndex, endIndex, info.getMutantInfo());
		mutantContents.set(info.getLineNumber() - 1, mutantLine.toString());
		return mutantContents;
	}
	
	/**
	 * Output the simulation report to a file
	 * @param filename
	 * @param mutationContents
	 * @param simulationResults
	 * @throws FileNotFoundException
	 */
	public static void outputSimulationReport(String filename, List<String> mutationContents, List<Result> simulationResults) throws FileNotFoundException {
		// first add some more header info in the csv
		var header = mutationContents.get(0);
		header += ", Status, Killed by";
		mutationContents.set(0, header);
		var killCount = 0;
		for(var i = 0; i < simulationResults.size(); i++) {
			// mutation contents is off by one because of the header
			var mutantProperty = mutationContents.get(i+1);
			var result = simulationResults.get(i);
			
			//mutant killed by compiler
			if(result == null) {
				mutantProperty += ", Killed, Compiler";
				mutationContents.set(i+1, mutantProperty);
				killCount ++;
				continue;
			}
			
			// mutant alive
			if(result.wasSuccessful()) {
				mutantProperty += ", Alive, None";
				mutationContents.set(i+1, mutantProperty);
				continue;
			}
			
			// mutant killed
			killCount++;
			mutantProperty += ", Killed, ";
			for(var failures : result.getFailures()) {
				mutantProperty += failures.getTestHeader() + "; ";
			}
			mutationContents.set(i+1, mutantProperty);
		}
		
		var mutationTestConclusion = "Conclusion, " + killCount + " mutants are killed out of " + (simulationResults.size()-1) + " mutants";
		var mutationRatio = "Mutation Ratio, " + ((double) killCount / (simulationResults.size()-1));
		mutationContents.add(mutationTestConclusion);
		mutationContents.add(mutationRatio);
		
		// write file
		outputToFile(filename, mutationContents);
	}
}
