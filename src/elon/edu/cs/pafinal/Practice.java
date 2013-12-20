package elon.edu.cs.pafinal;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Practice{

	protected ArrayList<Word> wordList;
	
	protected String date;
	protected String description;
	protected String fileName;
	protected String folderName;
	protected boolean isComplete;
	protected File csv;
	

	public Practice(String fileN, File file) {

		// ***Change this
		date = "03/28/1991";
		csv = file;
		fileName = fileN;
		wordList = new ArrayList<Word>();
		folderName = csv.toString()
				.substring(csv.toString().lastIndexOf("/") + 1,
						csv.toString().length() - 4);
		
		buildList();
		

	}

	public void buildList() {
		try {
			
			
			//File file = new File(fileName);
			Scanner scan = new Scanner(csv);
			
			String word = "";
			String freq = "";

			description = scan.nextLine().substring(12);
			//System.out.println(description);

			String nothing = scan.next();

			while (scan.hasNext()) {

				String next = scan.nextLine();
				StringTokenizer tokenizer = new StringTokenizer(next);
				
				while (tokenizer.hasMoreTokens()) {
					
					word = tokenizer.nextToken(",");
					freq = tokenizer.nextToken(",");
					Word current = new Word(word, Integer.parseInt(freq));
					wordList.add(current);
				}
				
			}

		} catch (IOException exception) {
			System.out.println("Error processing file: " + exception);
		}
	}

	public ArrayList<Word> getWordList() {
		return wordList;
	}

	public void setWordList(ArrayList<Word> wordList) {
		this.wordList = wordList;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}
	
	public void printLogs(){
		for(Word w: wordList){
			Log.d("prac", w.getWord());
		}
	}

	public File getCsv() {
		return csv;
	}

	public void setCsv(File csv) {
		this.csv = csv;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	

}
