package ca.mcgill.ecse429.mutation.runMutation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThreadedSimulator {
	
	class Task extends Thread{
		private MutantRunner r;
		private List<String> mutantFolders;
		public Task(MutantRunner r, List<String> mutantFolders) {
			super();
			this.r = r;
			this.mutantFolders = mutantFolders;
		}
		
		public void run() {
	        for(var folder : mutantFolders) {
	        	r.runMutant(folder);
	        }
	    }
	}
	private List<Task> tasks;
	public ThreadedSimulator(MutantRunner r, List<String> mutantFolders, int numThreads) {
		tasks = new ArrayList<Task>();
		var singleThreadMutantNumber = mutantFolders.size() / numThreads;
		var leftovers = mutantFolders.size() % numThreads;
		var mutantsNumbers = new int[numThreads];
		Arrays.fill(mutantsNumbers, singleThreadMutantNumber);
		while(leftovers > 0) {
			mutantsNumbers[leftovers]++;
			leftovers--;
		}
		
		var startingIndex = 0;
		for(var i = 0; i < numThreads; i++) {
			 var task = new Task(r, mutantFolders.subList(startingIndex, startingIndex + mutantsNumbers[i]));
			 tasks.add(task);
			 
			 startingIndex += mutantsNumbers[i];
		}
	}
	
	public void start() throws InterruptedException {
		for(var task : tasks) {
			task.start();
		}
		
		for(var task : tasks) {
			task.join();
		}
	}
}
