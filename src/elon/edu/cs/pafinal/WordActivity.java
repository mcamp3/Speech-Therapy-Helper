//Miles Camp
//Speech Therapy Helper
//Word Activity

package elon.edu.cs.pafinal;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class WordActivity extends Activity implements OnInitListener {

	private Bundle receiveBundle;
	private String folderName;
	private ArrayList<String> wordsList;
	private int current;
	private TextView word;
	private TextView wordCount;
	private TextView wordFreq;
	private Button recordButton;
	private Button playButton;
	private Button nextButton;

	private int playCount = 0;

	private String newWord;
	private MediaRecorder mediaRecorder = null;
	private boolean isRecording = true;
	private String wordFileName;
	private ArrayList<File> wordFoldersEmpty;
	private ArrayList<File> wordFoldersFilled;
	private File wordFolderPath;
	private SoundPool soundPool;
	private HashMap<Integer, Integer> soundsMap;
	private TextToSpeech tts;
	private boolean completed;
	private String wordFolder;
	private boolean loaded;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_word);

		receiveBundle = this.getIntent().getExtras();

		folderName = receiveBundle.getString("folderName");
		wordsList = receiveBundle.getStringArrayList("words");
		current = receiveBundle.getInt("current");

		if (current == -1) {
			completed = true;
			current = 0;
		}

		TextView word = (TextView) findViewById(R.id.textviewWord);
		TextView wordCount = (TextView) findViewById(R.id.textviewwordcount);
		TextView wordFreq = (TextView) findViewById(R.id.textviewfreq);

		wordFoldersEmpty = new ArrayList<File>();
		wordFoldersFilled = new ArrayList<File>();

		newWord = wordsList.get(current);
		doFileName();

		word.setText(newWord);
		wordCount.setText("Word: " + (current + 1) + "/" + (wordsList.size()));

		Button recordButton = (Button) findViewById(R.id.buttonrecord);
		recordButton.setOnClickListener(recordClicker);

		Button playButton = (Button) findViewById(R.id.buttonplay);
		playButton.setOnClickListener(playClicker);

		Button nextButton = (Button) findViewById(R.id.buttonnext);

		if (wordFoldersEmpty.size() == 0) {
			recordButton = (Button) findViewById(R.id.buttonrecord);
			recordButton.setEnabled(false);
			playButton.setEnabled(true);
			nextButton.setEnabled(true);
		} else {
			recordButton = (Button) findViewById(R.id.buttonrecord);
			recordButton.setEnabled(true);
			playButton.setEnabled(false);
			nextButton.setEnabled(false);
		}

		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 100);
		soundPool
				.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
					@Override
					public void onLoadComplete(SoundPool soundPool,
							int mySoundId, int status) {
						loaded = true;
					}
				});

		soundsMap = new HashMap<Integer, Integer>();
		soundsMap.clear();

		if (completed) {
			for (File f : wordFoldersFilled) {
				String path = f.listFiles()[0].toString();
				soundsMap.put(soundsMap.size(), soundPool.load(path, 1));
			}

		}

		tts = new TextToSpeech(this, this);
	}

	@Override
	public void onPause() {
		super.onPause();

		if (mediaRecorder != null) {
			mediaRecorder.release();
			mediaRecorder = null;
		}

		if (!completed) {
			File f = new File(wordFolder);
			for (File s : f.listFiles()) {
				deleteSounds(s);
			}
		}

	}

	@Override
	public void onDestroy() {
		// Don't forget to shutdown tts!
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
		super.onDestroy();
	}

	//Changes the word
	public void next(View view) {

		soundsMap.clear();
		loaded = false;

		word = (TextView) findViewById(R.id.textviewWord);

		wordFoldersEmpty.clear();
		wordFoldersFilled.clear();

		recordButton = (Button) findViewById(R.id.buttonrecord);
		playButton = (Button) findViewById(R.id.buttonplay);
		nextButton = (Button) findViewById(R.id.buttonnext);

		if (current < wordsList.size() - 1) {
			current = current + 1;

			wordCount = (TextView) findViewById(R.id.textviewwordcount);
			wordCount.setText("Word: " + (current + 1) + "/"
					+ (wordsList.size()));

			newWord = wordsList.get(current);
			word.setText(newWord);

			doFileName();

			if (wordFoldersEmpty.size() == 0) {
				recordButton = (Button) findViewById(R.id.buttonrecord);
				recordButton.setEnabled(false);
				playButton.setEnabled(true);
				nextButton.setEnabled(true);
			} else {
				recordButton = (Button) findViewById(R.id.buttonrecord);
				recordButton.setEnabled(true);
				playButton.setEnabled(false);
				nextButton.setEnabled(false);
			}

			if (completed) {
				for (File f : wordFoldersFilled) {
					String path = f.listFiles()[0].toString();
					soundsMap.put(soundsMap.size(), soundPool.load(path, 1));
				}

			}

		} else {

			completed = true;
			Bundle sendBundle = new Bundle();
			sendBundle.putString("folderName", folderName);

			Intent i = new Intent(WordActivity.this, PreviewActivity.class);
			i.putExtras(sendBundle);
			startActivity(i);
		}

	}

	//Text To Speech
	public void talk(View v) {
		tts.speak(newWord, TextToSpeech.QUEUE_FLUSH, null);
	}

	
	private void onRecord(boolean start) {
		if (start) {
			startRecording();
		} else {
			stopRecording();

		}
	}

	// creates the correct filename and path for new recording and creates lists of folders for each word
	private void doFileName() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media

			File wordFolderPath = getExternalFilesDir("practice/" + folderName
					+ "/" + newWord);

			File[] filesArray;

			wordFolder = wordFolderPath.toString();

			filesArray = wordFolderPath.listFiles();

			for (File f : filesArray) {

				if (!f.isFile()) {

					File[] subArray = f.listFiles();

					if (subArray.length > 0) {
						for (File s : subArray) {
							wordFoldersFilled.add(f);
						}
					} else {
						wordFoldersEmpty.add(f);

					}
				}

			}

			// Create filename from date and time
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy_MM_dd-HH_mm_ss");
			Date now = new Date();
			String saveName = formatter.format(now) + "_" + newWord + ".3gp";

			File file = new File(wordFolderPath, saveName);

			wordFileName = saveName;

			wordFreq = (TextView) findViewById(R.id.textviewfreq);
			wordFreq.setText("Record " + wordFoldersEmpty.size()
					+ " more times.");
		}
	}

	//starts the media recorder
	private void startRecording() {

		mediaRecorder = new MediaRecorder();
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

		File temp = new File(wordFolderPath, wordFoldersEmpty.get(0).toString()
				+ "/" + wordFileName);

		wordFoldersFilled.add(wordFoldersEmpty.get(0));
		wordFoldersEmpty.remove(0);

		mediaRecorder.setOutputFile(temp.toString());
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			mediaRecorder.prepare();
		} catch (IOException e) {
			Log.e("mediaRecorder", "prepare() failed");
		}
		mediaRecorder.start();
	}

	//ends recording and saves audio
	private void stopRecording() {

		mediaRecorder.stop();
		mediaRecorder.release();
		mediaRecorder = null;

		recordButton = (Button) findViewById(R.id.buttonrecord);
		playButton = (Button) findViewById(R.id.buttonplay);
		nextButton = (Button) findViewById(R.id.buttonnext);

		File a = wordFoldersFilled.get(wordFoldersFilled.size() - 1)
				.listFiles()[0];

		soundsMap.put(soundsMap.size(), soundPool.load(a.toString(), 1));

		if (wordFoldersEmpty.size() == 0) {
			recordButton.setEnabled(false);
			playButton.setEnabled(true);
			nextButton.setEnabled(true);

		} else {
			recordButton.setEnabled(true);
			playButton.setEnabled(false);
			nextButton.setEnabled(false);
		}

		wordFreq = (TextView) findViewById(R.id.textviewfreq);
		wordFreq.setText("Record " + wordFoldersEmpty.size() + " more times.");
	}

	OnClickListener recordClicker = new OnClickListener() {
		public void onClick(View v) {
			recordButton = (Button) findViewById(R.id.buttonrecord);
			onRecord(isRecording);
			if (isRecording) {
				recordButton.setText("RECORDING!");
				recordButton.setTextColor(Color.RED);
			} else {
				recordButton.setSelected(false);
				recordButton.setText("Record");
				recordButton.setTextColor(Color.BLACK);
			}
			isRecording = !isRecording;
		}
	};

	OnClickListener playClicker = new OnClickListener() {
		public void onClick(View v) {

			AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			float streamVolumeCurrent = mgr
					.getStreamVolume(AudioManager.STREAM_MUSIC);
			float streamVolumeMax = mgr
					.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			float volume = streamVolumeCurrent / streamVolumeMax;

			if (playCount < soundsMap.size()) {
				soundPool.play(soundsMap.get(playCount), volume, 1, 0, 0, 1);
				playCount++;
			}

			if (playCount == soundsMap.size()) {
				playCount = 0;
			}

		}
	};

	public void delete(View v) {
		if (!wordFoldersFilled.isEmpty()) {
			for (File f : wordFoldersFilled) {
				deleteSounds(f);
			}
			deleteNext();
		}
	}

	public void deleteNext() {

		soundsMap.clear();

		wordFoldersEmpty = new ArrayList<File>();
		wordFoldersFilled = new ArrayList<File>();

		recordButton = (Button) findViewById(R.id.buttonrecord);
		playButton = (Button) findViewById(R.id.buttonplay);
		nextButton = (Button) findViewById(R.id.buttonnext);

		doFileName();

		if (wordFoldersEmpty.size() == 0) {

			recordButton.setEnabled(false);
			playButton.setEnabled(true);
			nextButton.setEnabled(true);
		} else {

			recordButton.setEnabled(true);
			playButton.setEnabled(false);
			nextButton.setEnabled(false);
		}

	}

	private void deleteSounds(File fileOrDirectory) {
		if (fileOrDirectory.isDirectory())
			for (File child : fileOrDirectory.listFiles()) {
				child.delete();
			}
	}

	@Override
	public void onInit(int status) {

		if (status == TextToSpeech.SUCCESS) {

			int result = tts.setLanguage(Locale.US);

			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("TTS", "This Language is not supported");
			}

		} else {
			Log.e("TTS", "Initilization Failed!");
		}
	}

}
