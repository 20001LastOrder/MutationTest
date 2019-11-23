package ca.mcgill.ecse429.mutation.runMutation;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.junit.runner.JUnitCore;
public class MutantRunner {
	private JavaCompiler compiler;
	private URL testFileURL;
	
	public MutantRunner() {
		compiler = ToolProvider.getSystemJavaCompiler();
	}
	
	@SuppressWarnings("resource")
	public void runMutant(String filePath) {
		String[] files = {filePath};
		if(!compile(files)) {
			System.out.println("Failed");
			return;
		}
		
		var sutFile = new File(filePath); 
		System.out.println(sutFile);
		try {
			// create URLs for the class loader
			final URL[] urls = new URL[] {sutFile.getParentFile().toURI().toURL(), 
					testFileURL
			};
			//create new class loader
			var loader = new URLClassLoader(urls);
			var aClass = loader.loadClass("SimpleAssertion");
			runTest(aClass);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void compileTest(String externalLibs, String filePath, String testPath) {
		//try to compile code
		System.out.println(externalLibs);
		String[] files = {"-cp", externalLibs, filePath};
		if(!compile(files)) {
			System.out.println("Failed");
		}else {
			
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
	
	private void runTest(Class testClass) {
		var core = new JUnitCore();
		var result = core.run(testClass);
		System.out.println(result.wasSuccessful());
	}
	
	private boolean compile(String[] files) {
		int compilationResult = compiler.run(null, null, null, files);
		if (compilationResult == 0) {
			return true;
		}else {
			return false;
		}
	}
}
