package by.vfedorenko.budgetwatcher.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Iterator;

import by.vfedorenko.budgetwatcher.realm.Operation;
import by.vfedorenko.budgetwatcher.realm.OperationTag;
import by.vfedorenko.budgetwatcher.realm.Tag;
import io.realm.Realm;
import io.realm.RealmResults;

public class DriveActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private class DeleteFileTask extends AsyncTask<DriveFile, Void, Void> {

        @Override
        protected Void doInBackground(DriveFile... params) {
            DriveFile file = params[0];

            com.google.android.gms.common.api.Status status = file.delete(mGoogleApiClient).await();
            if (!status.isSuccess()) {
                //showMessage("Unable to delete app data.");
            }

            Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(driveContentsCallback);
            return null;
        }
    }

    private class EditFileTask extends AsyncTask<DriveContents, Void, Void> {
        @Override
        protected Void doInBackground(DriveContents... params) {
            DriveContents driveContents = params[0];
            try {
                JSONArray data = buildJsonDatabase();

                OutputStream outputStream = driveContents.getOutputStream();
                outputStream.write(data.toString().getBytes());

                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                        .setTitle(DATABASE_FILENAME)
                        .build();
                Drive.DriveApi.getAppFolder(mGoogleApiClient)
                        .createFile(mGoogleApiClient, changeSet, driveContents)
                        .setResultCallback(fileCallback);
            } catch (IOException e) {
                Log.e(TAG, "IOException while appending to the output stream", e);
            } catch (JSONException e) {
                Log.e(TAG, "Failed to build database JSON", e);
            }

            return null;
        }
    }

    private class ReadContentTask extends AsyncTask<DriveFile, Void, Void> {

        @Override
        protected Void doInBackground(DriveFile... params) {
            DriveFile file = params[0];
            DriveApi.DriveContentsResult driveContentsResult = file.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null).await();
            if (!driveContentsResult.getStatus().isSuccess()) {
                return null;
            }

            DriveContents driveContents = driveContentsResult.getDriveContents();
            BufferedReader reader = new BufferedReader(new InputStreamReader(driveContents.getInputStream()));

            StringBuilder builder = new StringBuilder();
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                JSONArray data = new JSONArray(builder.toString());
                updateDatabase(data);
            } catch (IOException e) {
                Log.e(TAG, "IOException while reading from the stream", e);
            } catch (JSONException e) {
                Log.e(TAG, "Failed to parse data to JSONArray", e);
            }

            driveContents.discard(mGoogleApiClient);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            showMessage("Data was loaded");
        }
    }

    public static final int LAUNCH_TYPE_EXPORT = 1;
    public static final int LAUNCH_TYPE_IMPORT = 2;

    private static final String LAUNCH_TYPE_EXTRA = "LAUNCH_TYPE_EXTRA";
    private static final String TAG = "DriveActivity";

    private static final String DATABASE_FILENAME = "realmDatabase";

    /**
     * Request code for auto Google Play Services error resolution.
     */
    private static final int REQUEST_CODE_RESOLUTION = 1;

    private int mLaunchType;
    private GoogleApiClient mGoogleApiClient;

    final private ResultCallback<DriveApi.MetadataBufferResult> metadataCallback =
            new ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(DriveApi.MetadataBufferResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Problem while retrieving results");
                        return;
                    }

                    Iterator<Metadata> iterator = result.getMetadataBuffer().iterator();
                    if (iterator.hasNext()) {
                        DriveFile file = iterator.next().getDriveId().asDriveFile();
                        if (mLaunchType == LAUNCH_TYPE_EXPORT) {
                            new DeleteFileTask().execute(file);
                        } else if (mLaunchType == LAUNCH_TYPE_IMPORT) {
                            new ReadContentTask().execute(file);
                        }
                    } else {
                        showMessage("No file");
                        if (mLaunchType == LAUNCH_TYPE_EXPORT) {
                            Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(driveContentsCallback);
                        }
                    }
                }
            };

    // [START drive_contents_callback]
    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create new file contents");
                        return;
                    }

                    DriveContents driveContents = result.getDriveContents();
                    new EditFileTask().execute(driveContents);
                }
            };

    // [END drive_contents_callback]
    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create the file");
                    } else {
                        showMessage("Created a file in App Folder: " + result.getDriveFile());
                    }
                    finish();
                }
            };

    public static Intent buildIntent(Context context, int launchType) {
        Intent intent = new Intent(context, DriveActivity.class);
        intent.putExtra(LAUNCH_TYPE_EXTRA, launchType);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLaunchType = getIntent().getIntExtra(LAUNCH_TYPE_EXTRA, 0);
        if (mLaunchType != LAUNCH_TYPE_EXPORT && mLaunchType != LAUNCH_TYPE_IMPORT) {
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mGoogleApiClient == null) {
            // Create the API client and bind it to an instance variable.
            // We use this instance as the callback for connection and connection
            // failures.
            // Since no account name is passed, the user is prompted to choose.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_APPFOLDER)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        // Connect the client. Once connected, the camera is launched.
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    /**
     * Handles resolution callbacks.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "requestCode = " + requestCode + ", resultCode = " + resultCode + ", data =" + data);
        if (requestCode == REQUEST_CODE_RESOLUTION && resultCode == RESULT_OK) {
            Log.i(TAG, "connecting after resolution");
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "GoogleApiClient connected");

        Query query = new Query.Builder()
                .addFilter(Filters.contains(SearchableField.TITLE, DATABASE_FILENAME))
                .build();
        Drive.DriveApi.query(mGoogleApiClient, query).setResultCallback(metadataCallback);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Called whenever the API client fails to connect.
        Log.e(TAG, "GoogleApiClient connection failed: " + connectionResult.toString());
        if (!connectionResult.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, connectionResult.getErrorCode(), 0).show();
            return;
        }
        // The failure has a resolution. Resolve it.
        // Called typically when the app is not yet authorized, and an
        // authorization
        // dialog is displayed to the user.
        try {
            connectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private JSONArray buildJsonDatabase() throws JSONException {
        JSONArray result = new JSONArray();

        RealmResults<Operation> operations = Realm.getDefaultInstance().where(Operation.class).findAll();
        for (Operation operation : operations) {
            JSONObject jsonOperation = new JSONObject();
            JSONArray jsonTags = new JSONArray();

            for (OperationTag tag : operation.getTags()) {
                JSONObject jsonOperationTag = new JSONObject();

                JSONObject jsonTag = new JSONObject();
                jsonTag.put(Tag.FIELD_NAME, tag.getTag().getName());

                jsonOperationTag.put(OperationTag.FIELD_PERCENT, tag.getPercent());
                jsonOperationTag.put(OperationTag.FIELD_TAG, jsonTag);

                jsonTags.put(jsonOperationTag);
            }

            jsonOperation.put(Operation.FIELD_AMOUNT, operation.getAmount());
            jsonOperation.put(Operation.FIELD_DATE, operation.getDate());
            jsonOperation.put(Operation.FIELD_TYPE, operation.getType());
            jsonOperation.put(Operation.FIELD_TAGS, jsonTags);

            result.put(jsonOperation);
        }

        return result;
    }

    private void updateDatabase(JSONArray data) {
        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();
        realm.createOrUpdateAllFromJson(Operation.class, data);
        realm.commitTransaction();
    }
}
