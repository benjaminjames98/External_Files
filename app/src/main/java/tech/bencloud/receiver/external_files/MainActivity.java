package tech.bencloud.receiver.external_files;

import android.content.Context;
import android.os.Environment;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    // Define the filename of our preferences file
    public static final String FILENAME = "MyExternalFile";

    private final static String NEW_LINE = System.lineSeparator();

    // Class properties for preferences, including default values
    private String faveColour = "Undefined";
    private Integer faveNumber = -1;
    private Boolean silentMode = false;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        readExternalFile(FILENAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        writeExternalFile(FILENAME);
    }

    public boolean isExternalStorageWritable() {
        // Get the state of the external media
        // Potential states are: MEDIA_UNKNOWN, MEDIA_REMOVED, MEDIA_UNMOUNTED, MEDIA_CHECKING,
        // MEDIA_NOFS, MEDIA_MOUNTED, MEDIA_MOUNTED_READ_ONLY, MEDIA_SHARED, MEDIA_BAD_REMOVAL, or
        // MEDIA_UNMOUNTABLE.
        String state = Environment.getExternalStorageState();

        // If the external media state is MEDIA_MOUNTED, then we can write to
        // it so return true, otherwise return false!
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    // Checks if external storage is available to at least read
    public boolean isExternalStorageReadable() {
        // Get the state of the external media
        String state = Environment.getExternalStorageState();

        // If it's either MEDIA_MOUNTED or MEDIA_MOUNTED_READ_ONLY...
        // ...then we can read from it, so return true
        return state.equals(Environment.MEDIA_MOUNTED) || state.equals(Environment.MEDIA_MOUNTED_READ_ONLY);
    }

    public void writeExternalFile(String filename) {

        // Update our preference properties with the latest values the user has
        // entered into the UI
        EditText tempET = findViewById(R.id.faveColourET);
        faveColour = tempET.getText().toString();
        tempET = findViewById(R.id.faveNumberET);
        faveNumber = Integer.valueOf(tempET.getText().toString());
        CheckBox cb = findViewById(R.id.silentModeCB);
        silentMode = cb.isChecked();

        if (isExternalStorageWritable()) {
            try (FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE)) {
                fos.write(faveColour.getBytes());
                fos.write(NEW_LINE.getBytes());

                fos.write(faveNumber.toString().getBytes());
                fos.write(NEW_LINE.getBytes());

                fos.write(silentMode.toString().getBytes());
                fos.write(NEW_LINE.getBytes());
            } catch (IOException fnfe) {
                fnfe.printStackTrace();
            }
        } else {
            // External media not writable? Alert the user of why!
            String state = Environment.getExternalStorageState();

            Context context = getApplicationContext();
            CharSequence text = "Cannot write external media - state is: " + state;
            int duration = Toast.LENGTH_LONG;
            Toast.makeText(context, text, duration).show();
        }
    }

    public void readExternalFile(String filename) {
        if (isExternalStorageReadable()) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(openFileInput(filename)))) {
                // We know the format of the file is 3 strings, so we can work with that assumption
                faveColour = br.readLine();

                String temp = br.readLine();
                faveNumber = Integer.valueOf(temp);

                temp = br.readLine();
                silentMode = Boolean.valueOf(temp);
            } catch (IOException fnfe) {
                fnfe.printStackTrace();
            }
        } else {
            // External media not readable? Alert the user of why!
            String state = Environment.getExternalStorageState();

            Context context = getApplicationContext();
            CharSequence text = "Cannot read external media! State is: " + state;
            int duration = Toast.LENGTH_LONG;
            Toast.makeText(context, text, duration).show();
        }

        // Update the values in our interface with the most recent values
        EditText tempET = (EditText) findViewById(R.id.faveColourET);
        tempET.setText(faveColour);

        tempET = (EditText) findViewById(R.id.faveNumberET);
        tempET.setText(faveNumber.toString());

        CheckBox cb = (CheckBox) findViewById(R.id.silentModeCB);
        cb.setChecked(silentMode);
    }

}