package ca.mcgill.ecse429.mutation;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import ca.mcgill.ecse429.mutation.runMutation.MutantRunner;

public class Main {
	public static final String MUTANT_RULE = "mutationRule.txt";
	public static final String ORIGNAL = "TestMethods.java";
	public static final String MUTANT_INFO = "result.csv";
	
	public static void main(String[] args) throws MalformedURLException {
		System.out.println("Start generating mutants for " + ORIGNAL);
		GenerateMutantInformation(MUTANT_RULE, ORIGNAL, MUTANT_INFO);
		System.out.println("Generated Mutants informaiton in " + MUTANT_RULE);
		GenerateMutantFiles(ORIGNAL, MUTANT_INFO);
		System.out.println("Generated Mutants files in folder \"Mutants\"");
		var mutantRunner = new MutantRunner();
		mutantRunner.compileTest("./lib/junit-4.12.jar", "Mutants/Original/TestMethods.java", "test/SimpleAssertion.java");	
		for(var i = 0; i <= 12; i++) {
			mutantRunner.runMutant("Mutants/Mutant_"+i+"/TestMethods.java");
		}
	}
	
	public static void GenerateMutantInformation(String mutationRuleFilename, String originalFilename, String mutantFilename) {
		MutantGenerator m = new MutantGenerator(mutationRuleFilename);
		var result = m.GenerateMutationFromFile(originalFilename);
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
	
	public static void GenerateMutantFiles(String originalFileName, String mutantInfoFileName) {
		var mutantInfos = Utils.readMutantInfomation(mutantInfoFileName);
		Utils.generateMutantFiles(mutantInfos, originalFileName);
	}
}
