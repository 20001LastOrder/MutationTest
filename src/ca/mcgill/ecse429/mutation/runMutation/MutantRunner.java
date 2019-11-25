package ca.mcgill.ecse429.mutation.runMutation;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
public class MutantRunner {
	private JavaCompiler compiler;
	private URL testFileURL;
	private String originalClassName;
	private String testClass;
	private File classPathFolder;
	private String externalLibPath;
	private List<Result> results;
	
	public MutantRunner(String originalClassName, String testClass, String classPathFolder,
			String externalLibPath, int mutantSize) {
		//+1 for the original file
		results = Arrays.asList(new Result[mutantSize+1]);
		this.originalClassName = originalClassName;
		this.classPathFolder = new File(classPathFolder);
		this.externalLibPath = externalLibPath;
		this.testClass = testClass.replace(".java", "");
		compiler = ToolProvider.getSystemJavaCompiler();
	}

	/**
	 * Run the mutant file given the directory of the mutant and the test 
	 * The test has to be compile first: compileTest has to be called first
	 * @param mutantDirectory
	 * @param testClassName
	 */
	public void runMutant(String mutantDirectory, int id) {
		try {
			
			var sutFile = new File(mutantDirectory + originalClassName); 
			
			// call the compiler and compile the mutant
			var classPaths = buildClassPath(classPathFolder.toString());
			String[] files = {"-cp", classPaths, sutFile.toString()};
			System.out.println("Running " + mutantDirectory + originalClassName);
			var reportFile = new File(mutantDirectory + "/report.txt");
			var output = new PrintStream(reportFile);
			if(!compile(files, output, output)) {
				output.println("Compilation Failed, Mutant killed by the compiler");
				addResult(null, id);
				return;
			}
			
			output.println("Compilation Succeed");
			var directory = new File(mutantDirectory);
			// create URLs for the class loader
			final URL[] urls = new URL[] {directory.toURI().toURL(),
					testFileURL,
					classPathFolder.toURI().toURL()
			};
			
			//create new class loader and run the mutant with the loaded class
			var loader = URLClassLoader.newInstance(urls);
			var aClass = loader.loadClass(testClass);
			var result = runTest(aClass);
			var report = logTest(result);
			output.println(report);
			output.close();
			addResult(result, id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void compileTest(String fileDir, String testPath) {
		//try to compile code
		var classPaths = buildClassPath(externalLibPath, classPathFolder.toString());
		String[] files = {"-cp", classPaths, fileDir + originalClassName, testPath};
		
		if(!compile(files, null, null)) {
			System.out.println("Failed");
		}else {
			System.out.println("Test Compile Successfull");
			// get directory url
			var testFile = new File(testPath);
			try {
				testFileURL = testFile.getParentFile().toURI().toURL();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private Result runTest(Class<?> testClass) {
		var core = new JUnitCore();
		var result = core.run(testClass);
		return result;
	}
	
	private synchronized void addResult(Result result, int id) {
		results.set(id, result);
	}
	
	public List<Result> getResults(){
		return results;
	}
	
	private String logTest(Result r) {
		String report = "-----------------------------------------------\n";
		report += "Test Successful: " + r.wasSuccessful() + "\n";
		report += r.getRunCount() + " Test run in " + r.getRunTime() + "ms. "
				  + "Passed " + (r.getRunCount() - r.getFailureCount() - r.getIgnoreCount())
				  + ", Ignored " + r.getIgnoreCount() + ", Failed " + r.getFailureCount() + "\n";
		if(r.getFailureCount() > 0) {
			report += "Test cases killed mutants: \n";
			for(var failure : r.getFailures()) {
				report += failure.toString() + "\n";
			}
		}
		return report;
	}
	
	/**
	 * This function builds a classpath from the passed Strings
	 * 
	 * @param paths classpath elements
	 * @return returns the complete classpath with wildcards expanded
	 */
	private static String buildClassPath(String... paths) {
	    StringBuilder sb = new StringBuilder();
	    for (String path : paths) {
	        if (path.endsWith("*")) {
	            path = path.substring(0, path.length() - 1);
	            File pathFile = new File(path);
	            for (File file : pathFile.listFiles()) {
	                if (file.isFile() && file.getName().endsWith(".jar")) {
	                    sb.append(path);
	                    sb.append(file.getName());
	                    sb.append(System.getProperty("path.separator"));
	                }
	            }
	        } else {
	            sb.append(path);
	            sb.append(System.getProperty("path.separator"));
	        }
	    }
	    return sb.toString();
	}
	
	private boolean compile(String[] files, OutputStream out, OutputStream err) {
		int compilationResult = compiler.run(null, out, err, files);
		if (compilationResult == 0) {
			return true;
		}else {
			return false;
		}
	}
}
