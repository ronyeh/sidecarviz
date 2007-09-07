package sidecarviz.experiencesampling;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.docs.DocumentEntry;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

/**
 * <p>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class ExperienceSampler {

	private SpreadsheetService spreadSheetService;
	private URL documentListFeedUrl;
	private String subjectID;
	private DocsService docsService;
	private URL spreadsheetListFeedUrl;

	public ExperienceSampler(String userID) {
		subjectID = userID;
		docsService = new DocsService("sidecarviz-experienceSampling-1");
		spreadSheetService = new SpreadsheetService("sidecarviz-experienceSampling-1");
		try {
			spreadSheetService.setUserCredentials("experiencesampling@gmail.com", "esm12345");
			docsService.setUserCredentials("experiencesampling@gmail.com", "esm12345");
		} catch (AuthenticationException e) {
			e.printStackTrace();
		}

		try {
			spreadsheetListFeedUrl = new URL("http://spreadsheets.google.com/feeds/spreadsheets/private/full");
			documentListFeedUrl = new URL("http://docs.google.com/feeds/documents/private/full");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private void getSpreadsheets() {
		SpreadsheetFeed feed;
		try {
			feed = spreadSheetService.getFeed(spreadsheetListFeedUrl, SpreadsheetFeed.class);
			List<SpreadsheetEntry> spreadsheets = feed.getEntries();
			for (int i = 0; i < spreadsheets.size(); i++) {
				SpreadsheetEntry entry = spreadsheets.get(i);
				System.out.println(entry.getTitle().getPlainText());

				List<WorksheetEntry> worksheets = entry.getWorksheets();
				for (int j = 0; j < worksheets.size(); j++) {
					WorksheetEntry worksheetEntry = worksheets.get(j);
					System.out.println("\t" + worksheetEntry.getTitle().getPlainText());

					URL listFeedUrl = worksheetEntry.getListFeedUrl();
					ListFeed listFeed = spreadSheetService.getFeed(listFeedUrl, ListFeed.class);
					int row = 0;
					for (ListEntry listEntry : listFeed.getEntries()) {
						System.out.println(listEntry.getTitle().getPlainText());
						for (String tag : listEntry.getCustomElements().getTags()) {
							System.out.println("  " + listEntry.getCustomElements().getValue(tag) + "");
						}
						row++;
					}

					// ListEntry newEntry = new ListEntry();
					// newEntry.getCustomElements().setValueLocal("Application", "Eclipse IDE");
					// ListEntry insertedRow = service.insert(listFeedUrl, newEntry);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	public void uploadFile(String filePath) {
		DocumentEntry newDocument = new DocumentEntry();
		File documentFile = new File(filePath);
		try {
			newDocument.setFile(documentFile);
			// Set the title for the new document.
			newDocument.setTitle(new PlainTextConstruct("Subject" + subjectID));
			DocumentListEntry uploaded = docsService.insert(documentListFeedUrl, newDocument);
			printDocumentEntry(uploaded);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	public void printDocumentEntry(DocumentListEntry doc) {
		String shortId = doc.getId().substring(doc.getId().lastIndexOf('/') + 1);
		System.out.println(" -- Document(" + shortId + "/" + doc.getTitle().getPlainText() + ")");
	}

	public static void main(String[] args) {
		ExperienceSampler esm = new ExperienceSampler("02");
		// esm.uploadFile("SpreadsheetTemplate.csv");
		esm.getSpreadsheets();
	}
}
