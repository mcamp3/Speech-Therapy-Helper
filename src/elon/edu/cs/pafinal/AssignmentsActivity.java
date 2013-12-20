//Miles Camp
//Speech Therapy Helper
//Assignment Activity

package elon.edu.cs.pafinal;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class AssignmentsActivity extends Activity {

	private ArrayList<Practice> fileList;
	private ArrayList<String> practiceNames;
	private ListView listView;
	private ListAdapter listAdapter;
	private ArrayList<File> files;
	private ArrayList<File> folders;
	private File dfile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_assignments);
	}

	@Override
	public void onResume() {
		super.onResume();

		getFiles();

		updateListView();
	}

	//Creates ArrayList of strings from list of folders
	private void makePracticeNames() {

		practiceNames = new ArrayList<String>();

		if (folders.size() > 0) {
			for (File f : folders) {
				practiceNames.add(f.toString().substring(
						f.toString().lastIndexOf("/") + 1));
			}
		}
	}

	// makes list of .csv files and folders
	private void getFiles() {

		File[] filesArray;
		files = new ArrayList<File>();
		folders = new ArrayList<File>();

		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {

			File path = getExternalFilesDir("practice");

			filesArray = path.listFiles();

			for (File f : filesArray) {

				if (f.isFile()) {
					files.add(f);
				} else {
					folders.add(f);
				}

			}

		}
	}

	/**
	 * updates the ListView on the screen to accurately display the current
	 * AudioNotes folder
	 */
	private void updateListView() {

		createFileLists();

		makePracticeNames();

		listAdapter = new ArrayAdapter<String>(this,
				R.layout.activity_notelist, practiceNames);

		listView = (ListView) findViewById(R.id.listviewpractice);
		listView.setTextFilterEnabled(true);

		listView.setAdapter(listAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				String current = practiceNames.get(position);

				Bundle sendBundle = new Bundle();
				sendBundle.putString("folderName", current);

				Intent i = new Intent(AssignmentsActivity.this,
						PreviewActivity.class);
				i.putExtras(sendBundle);
				startActivity(i);

			}
		});

		/**
		 * long clicks are used to delete
		 */
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {

				String current = practiceNames.get(position);

				dfile = getExternalFilesDir("/practice/" + current);

				deleteDialog();

				return false;

			}
		});
	}

	/**
	 * creates the file list the ListView will access to display on the main
	 * screen
	 */
	private void createFileLists() {

		boolean sameNames = false;

		for (File f : files) {
			sameNames = false;

			for (File d : folders) {
				if (f.toString().substring(0, f.toString().length() - 4)
						.equals(d.toString())) {
					sameNames = true;
				}
			}

			if (folders.size() == 0) {
				Practice prac = new Practice(f.toString(), f);

				String folderName = prac.getFolderName();
				File path = getExternalFilesDir("/practice/" + folderName);
				generateNoteOnSD(path.toString(), "description.txt",
						prac.getDescription());

				for (Word w : prac.getWordList()) {

					File wordsFolders = getExternalFilesDir("/practice/"
							+ folderName + "/" + w.getWord());

					for (int i = 1; i <= w.getRequiredFreq(); i++) {
						File freqFolders = getExternalFilesDir("/practice/"
								+ folderName + "/" + w.getWord() + "/"
								+ w.getWord() + i);
					}
				}
			}

			if (sameNames == false) {

				Practice prac = new Practice(f.toString(), f);
				String folderName = prac.getFolderName();
				File path = getExternalFilesDir("/practice/" + folderName);
				generateNoteOnSD(path.toString(), "description.txt",
						prac.getDescription());

				for (Word w : prac.getWordList()) {
					File wordsFolders = getExternalFilesDir("/practice/"
							+ folderName + "/" + w.getWord());

					for (int i = 1; i <= w.getRequiredFreq(); i++) {
						File freqFolders = getExternalFilesDir("/practice/"
								+ folderName + "/" + w.getWord() + "/"
								+ w.getWord() + i);
					}
				}

			}

		}

		getFiles();

	}

	private void deleteDialog() {

		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("Delete Practice");
		adb.setMessage("Are you sure you want to delete this practice and .csv file?  All progress will be lost.");

		adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int id) {
				// Action for 'Ok' Button
				File folder = new File(dfile.toString());
				File file = new File(dfile.toString() + ".csv");

				DeleteRecursive(folder);
				file.delete();
				updateListView();
			}
		});
		adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				dialog.cancel();
			}
		});

		adb.show();

	}

	// from stack overflow
	private void DeleteRecursive(File fileOrDirectory) {
		if (fileOrDirectory.isDirectory())
			for (File child : fileOrDirectory.listFiles())
				DeleteRecursive(child);

		fileOrDirectory.delete();
	}

	// from stack overflow
	public void generateNoteOnSD(String location, String sFileName, String sBody) {
		try {
			File root = new File(location);
			if (!root.exists()) {
				root.mkdirs();
			}
			File gpxfile = new File(root, sFileName);
			FileWriter writer = new FileWriter(gpxfile);
			writer.append(sBody);
			writer.flush();
			writer.close();

		} catch (IOException e) {

		}
	}
}
