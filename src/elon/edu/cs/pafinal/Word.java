package elon.edu.cs.pafinal;


import java.util.ArrayList;

public class Word {
	
	private String word;
	private int requiredFreq;
	private int currentFreq;
	private ArrayList<String> audioNames;
	private boolean isCompleted;
	
	
	public Word(String w, int rF){
		word = w;
		requiredFreq = rF;
		currentFreq = 0;
		isCompleted = false;
	}


	public String getWord() {
		return word;
	}


	public void setWord(String word) {
		this.word = word;
	}


	public int getRequiredFreq() {
		return requiredFreq;
	}


	public void setRequiredFreq(int requiredFreq) {
		this.requiredFreq = requiredFreq;
	}


	public int getCurrentFreq() {
		return currentFreq;
	}


	public void setCurrentFreq(int currentFreq) {
		this.currentFreq = currentFreq;
	}


	public boolean isCompleted() {
		return isCompleted;
	}


	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}


	@Override
	public String toString() {
		return "Word [word=" + word + ", requiredFreq=" + requiredFreq + "]";
	}

}
