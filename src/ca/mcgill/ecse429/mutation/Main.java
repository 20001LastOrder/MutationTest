package ca.mcgill.ecse429.mutation;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ca.mcgill.ecse429.mutation.runMutation.MutantRunner;
import ca.mcgill.ecse429.mutation.runMutation.ThreadedSimulator;

public class Main {
	public static final String MUTANT_RULE = "mutationRule.txt";
	public static final String ORIGINAL = "calculator/Calculator.java";
	public static final String TEST = "TestMethodsCalTest.java";
	public static final String MUTANT_INFO = "result.csv";
	public static final String SOURCE_PATH = "./";
	public static final String CLASS_PATH = "binary/";
	public static final String EXTERNAL_LIB = "./lib/*";
	public static void main(String[] args) throws MalformedURLException {
		System.out.println("Start generating mutants for " + ORIGINAL);
		var mutationContents = GenerateMutantInformation(MUTANT_RULE, ORIGINAL, MUTANT_INFO);
		System.out.println("Generated Mutants informaiton in " + MUTANT_RULE);
		int mutantSize = GenerateMutantFiles(ORIGINAL, MUTANT_INFO);
		System.out.println("Generated Mutants files in folder \"Mutants\"");
		
		var mutantRunner = new MutantRunner(ORIGINAL, TEST,CLASS_PATH, "./lib/*", mutantSize);
		mutantRunner.compileTest("Mutants/Original/", "test/"+TEST);
		
		var mutantFolders = new ArrayList<String>();
		//run original
		//run mutant
		for(var i = 0; i < mutantSize; i++) {
			mutantFolders.add("Mutants/Mutant_"+i+"/");
		}
		// first simulate the default one and put it to the end of the list
		mutantRunner.runMutant("Mutants/Original/", mutantFolders.size());
		String sutInformation = "Origin, none, none, none, none";
		mutationContents.add(mutantSize+1, sutInformation);
		// run the mutants in parallel
		var runner = new ThreadedSimulator(mutantRunner, mutantFolders, 3);
		try {
			runner.start();
			Utils.outputSimulationReport("Mutants/report.csv", mutationContents, mutantRunner.getResults());
			System.out.println("Report Generated in Mutants/report.csv");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static List<String> GenerateMutantInformation(String mutationRuleFilename, String originalFilename, String mutantFilename) {
		MutantGenerator m = new MutantGenerator(mutationRuleFilename);
		var result = m.GenerateMutationFromFile(Main.SOURCE_PATH + originalFilename);
		var stringResult = new ArrayList<String>();
		stringResult.add(MutantInformation.FILE_HEADER);
		stringResult.addAll(result.stream().map(MutantInformation::toString).collect(Collectors.toList()));
		stringResult.add("");
		stringResult.addAll(MutantGenerator.MutantsStat(result));
		
		try {
			Utils.outputToFile(mutantFilename, stringResult);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return stringResult;
	}
	
	public static int GenerateMutantFiles(String originalFileName, String mutantInfoFileName) {
		var mutantInfos = Utils.readMutantInfomation(mutantInfoFileName);
		Utils.generateMutantFiles(mutantInfos, originalFileName);
		return mutantInfos.size();
	}
}
