package ca.mcgill.ecse429.mutation;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.ArrayList;
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
	
	public static void main(String[] args) throws MalformedURLException {
		System.out.println("Start generating mutants for " + ORIGINAL);
		GenerateMutantInformation(MUTANT_RULE, ORIGINAL, MUTANT_INFO);
		System.out.println("Generated Mutants informaiton in " + MUTANT_RULE);
		int mutantSize = GenerateMutantFiles(ORIGINAL, MUTANT_INFO);
		System.out.println("Generated Mutants files in folder \"Mutants\"");
		String[] externalLibs = {"lib/junit-4.12.jar"};
		
		var mutantRunner = new MutantRunner(ORIGINAL, TEST,CLASS_PATH, "./lib/*");
		mutantRunner.compileTest("Mutants/Original/", "test/"+TEST);
		
		var mutantFolders = new ArrayList<String>();
		//run original
		mutantFolders.add("Mutants/Original/");
		//run mutant
		for(var i = 0; i < mutantSize; i++) {
			mutantFolders.add("Mutants/Mutant_"+i+"/");
		}
		
		var runner = new ThreadedSimulator(mutantRunner, mutantFolders, 3);
		try {
			runner.start();
			System.out.println(mutantRunner.getResults().size());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void GenerateMutantInformation(String mutationRuleFilename, String originalFilename, String mutantFilename) {
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
	}
	
	public static int GenerateMutantFiles(String originalFileName, String mutantInfoFileName) {
		var mutantInfos = Utils.readMutantInfomation(mutantInfoFileName);
		Utils.generateMutantFiles(mutantInfos, originalFileName);
		return mutantInfos.size();
	}
}
