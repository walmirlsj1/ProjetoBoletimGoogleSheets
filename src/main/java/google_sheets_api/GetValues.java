package google_sheets_api;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.security.GeneralSecurityException;

/* Class to demonstrate the use of Spreadsheet Batch Get Values API */
public class GetValues {

    /**
     * Returns a range of values from a spreadsheet.
     *
     * @param spreadsheetId - Id of the spreadsheet.
     * @param range         - Range of cells of the spreadsheet.
     * @return Values in the range
     * @throws IOException - if credentials file not found.
     */
    public static ValueRange getValues(String spreadsheetId, String range) throws IOException, GeneralSecurityException {
        // Create the sheets API client
        Sheets service = SheetsQuickstart.getService(GoogleNetHttpTransport.newTrustedTransport());

        ValueRange result = null;
        try {
            // Gets the values of the cells in the specified range.
            result = service.spreadsheets().values().get(spreadsheetId, range).execute();
            int numRows = result.getValues() != null ? result.getValues().size() : 0;
            System.out.printf("%d rows retrieved.", numRows);
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 404) {
                System.out.printf("Spreadsheet not found with id '%s'.\n", spreadsheetId);
            } else {
                throw e;
            }
        }
        return result;
    }
}