package ca.mcgill.ecse429.mutation;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ca.mcgill.ecse429.mutation.runMutation.MutantRunner;
import ca.mcgill.ecse429.mutation.runMutation.ThreadedSimulator;

public class Main {
	public static final String MUTANT_RULE = "mutationRule.txt";

	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException{
		var parameter = Utils.readParameters("./parameter.csv");
		
		System.out.println("Start generating mutants for " + parameter.originalFile);
		var mutationContents = GenerateMutantInformation(parameter.sourcePath, MUTANT_RULE, parameter.originalFile, parameter.mutantInfoOutput);
		System.out.println("Generated Mutants informaiton in " + MUTANT_RULE);
		int mutantSize = GenerateMutantFiles(parameter.sourcePath, parameter.originalFile, parameter.mutantInfoOutput);
		System.out.println("Generated Mutants files in folder \"Mutants\"");
		
		var mutantRunner = new MutantRunner(parameter.originalFile, parameter.testFile,parameter.classPath, "./lib/*", mutantSize);
		mutantRunner.compileTest("Mutants/Original/", parameter.testPath, parameter.testFile);
		
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
		var runner = new ThreadedSimulator(mutantRunner, mutantFolders, Integer.parseInt(parameter.testThreads));
		try {
			runner.start();
			Utils.outputSimulationReport("Mutants/report.csv", mutationContents, mutantRunner.getResults());
			System.out.println("Report Generated in Mutants/report.csv");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static List<String> GenerateMutantInformation(String sourcePath, String mutationRuleFilename, String originalFilename, String mutantFilename) {
		MutantGenerator m = new MutantGenerator(mutationRuleFilename);
		var result = m.GenerateMutationFromFile(sourcePath + originalFilename);
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
	
	public static int GenerateMutantFiles(String sourcePath, String originalFileName, String mutantInfoFileName) {
		var mutantInfos = Utils.readMutantInfomation(mutantInfoFileName);
		Utils.generateMutantFiles(sourcePath, mutantInfos, originalFileName);
		return mutantInfos.size();
	}
}
