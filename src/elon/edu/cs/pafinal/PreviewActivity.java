//Miles Camp
//Speech Therapy Helper
//Preview Activity

package elon.edu.cs.pafinal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PreviewActivity extends Activity {

	private TextView wordDisplay;
	private ArrayList<File> folders;
	private File folderPath;
	private String folderName;
	private Bundle receiveBundle;
	private String description;
	private Button beginButton;
	private ArrayList<String> words;
	private TextView statusText;
	private ArrayList<File> wordFoldersEmpty;
	private ArrayList<File> wordFoldersFilled;
	private Boolean completed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preview);

		wordDisplay = (TextView) findViewById(R.id.wordstext);
		statusText = (TextView) findViewById(R.id.textviewstatus);
		beginButton = (Button) findViewById(R.id.buttonstart);

		receiveBundle = this.getIntent().getExtras();

		folderName = receiveBundle.getString("folderName");

		words = new ArrayList<String>();
		completed = false;

		makeWordList();
		readDesc();

	}

	@Override
	public void onResume() {
		super.onResume();

		checkStatus();

		beginButton = (Button) findViewById(R.id.buttonstart);

		if (completed) {
			beginButton.setText("Review");
		} else {
			beginButton.setText("Start!");
		}
	}

	//Calculates how many words have recordings
	public void checkStatus() {

		wordFoldersEmpty = new ArrayList<File>();
		wordFoldersFilled = new ArrayList<File>();

		statusText = (TextView) findViewById(R.id.textviewstatus);

		for (File f : folders) {

			if (!f.isFile()) {

				File[] subArray = f.listFiles();

				Boolean hasFiles = false;

				if (subArray.length > 0) {
					for (File s : subArray) {
						File[] subSubArray = s.listFiles();

						if (subSubArray.length > 0) {
							hasFiles = true;
						} else {
							hasFiles = false;
						}
					}
					if (hasFiles)
						wordFoldersFilled.add(f);
					else
						wordFoldersEmpty.add(f);

				}
			}
		}
		if (wordFoldersFilled.size() == folders.size()) {

			completed = true;
			statusText.setText("Status: Completed!");
			statusText.setTextColor(Color.GREEN);
		} else {

			completed = false;
			statusText.setText("Status: " + wordFoldersFilled.size() + "/"
					+ folders.size());
		}
	}

	//intents to word activity
	public void begin(View view) {

		Bundle sendBundle = new Bundle();

		sendBundle.putStringArrayList("words", words);
		sendBundle.putString("folderName", folderName);

		if (completed)
			sendBundle.putInt("current", -1);
		else if (wordFoldersFilled.isEmpty())
			sendBundle.putInt("current", 0);
		else
			sendBundle.putInt("current", (wordFoldersFilled.size()));

		Intent i = new Intent(PreviewActivity.this, WordActivity.class);
		i.putExtras(sendBundle);
		startActivity(i);
	}

	//reads description file
	private void readDesc() {

		StringBuilder text = new StringBuilder();

		try {

			File file = getExternalFilesDir("practice/" + folderName
					+ "/description.txt");

			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;

			while ((line = br.readLine()) != null) {
				text.append(line);
				text.append('\n');
			}
		}

		catch (IOException e) {
			e.printStackTrace();
		}

		TextView desc = (TextView) findViewById(R.id.textviewdescription);

		desc.setText(text.toString());
	}

	//creates list of words in practice
	private void makeWordList() {

		File[] filesArray;

		folders = new ArrayList<File>();

		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {

			File path = getExternalFilesDir("practice/" + folderName);

			filesArray = path.listFiles();

			for (File f : filesArray) {

				if (!f.isFile()) {
					folders.add(f);
				}
			}
		}

		String wordText = "";

		for (File f : folders) {

			wordText = wordText
					+ f.toString().substring(f.toString().lastIndexOf("/") + 1)
					+ "\n";
			words.add(f.toString().substring(f.toString().lastIndexOf("/") + 1));
		}
		wordDisplay.setText(wordText);
	}

}
