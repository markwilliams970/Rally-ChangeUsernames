/* Copyright 2002-2013 Rally Software Development Corp. All Rights Reserved.
*
* This code is open source and is provided on an as-is basis. Rally provides
* no official support for nor guarantee of the functionality, usability, or
* effectiveness of this code, nor its suitability for any application that
* an end-user might have in mind. Use at your own risk: user assumes any and
* all risk associated with use and implementation of this script in his or
* her own environment.

* Usage: java -classpath $CLASSPATH Rally_ChangeUsernames
* Specify the User-Defined variables below. Script will iterate through all UserIDs listed
* in userMappingFile and attemt to update to the specified new UserID. The Rally user
* running this code must have Subscription Administrator privileges.
*/

// Google JSON libs
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

// OpenCSV libs
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

// Rally REST libs
import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.CreateRequest;
import com.rallydev.rest.request.DeleteRequest;
import com.rallydev.rest.request.GetRequest;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.request.UpdateRequest;
import com.rallydev.rest.response.CreateResponse;
import com.rallydev.rest.response.DeleteResponse;
import com.rallydev.rest.response.GetResponse;
import com.rallydev.rest.response.QueryResponse;
import com.rallydev.rest.response.UpdateResponse;
import com.rallydev.rest.util.Fetch;
import com.rallydev.rest.util.QueryFilter;
import com.rallydev.rest.util.Ref;

// Java standard libs
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Rally_ChangeUsernames {

    public static void main(String[] args) throws URISyntaxException, IOException {
	// Create and configure a new instance of RallyRestApi
	// Connection parameters
	String rallyURL = "https://rally1.rallydev.com";
	String wsapiVersion = "1.40";
	String applicationName = "Rally_ChangeUsernames";
	String applicationVendor = "Rally Labs";
    
	// Credentials
	String userName = "rallysubadmin@company.com";
	String userPassword = "password";
	
	// Reference to RallyRestApi
	RallyRestApi restApi = new RallyRestApi(
		new URI(rallyURL),
		userName,
		userPassword);
	restApi.setWsapiVersion(wsapiVersion);
	restApi.setApplicationName(applicationName);
	restApi.setApplicationVendor(applicationVendor);
    
	try {		    
	    //CSV file containing User Mapping
	    String userMappingFile = "C:\\Users\\username\\Documents\\RallyUtilities\\ChangeUsernames\\UserMappingTemplate.csv";
	    CSVReader myCSVReader = new CSVReader(new FileReader(userMappingFile));
		    
	    String [] readLine;
	    String existUsername;
	    String newUsername;
		    
	    int lineNumber = 0;
		    
	    while ((readLine = myCSVReader.readNext()) != null) {
		lineNumber++;
				    
		// Skip first line (CSV Headers)
		if (lineNumber > 1) {
		    //readLine[] is an array of values from the line
		    existUsername = readLine[0];
		    newUsername = readLine[1];
			    
		    // Lookup user in Rally
		    //Read User
		    QueryRequest userRequest = new QueryRequest("User");
		    userRequest.setFetch(new Fetch("UserName", "Subscription", "DisplayName"));
		    userRequest.setQueryFilter(new QueryFilter("UserName", "=", existUsername));
		    QueryResponse userQueryResponse = restApi.query(userRequest);
			
			
		    // Check to see if we found the user
		    int numberResults = userQueryResponse.getTotalResultCount();
			    
		    if (numberResults > 0) {
			// Parse query result
			JsonObject myFoundUser = userQueryResponse.getResults().get(0).getAsJsonObject();
			String foundUserName = myFoundUser.get("UserName").toString();
				
			System.out.println("Found Exist UserName: " + foundUserName + " In Rally.");			        
			String userRef = myFoundUser.get("_ref").toString();
			
			if (!newUsername.equals(foundUserName.toString())) {		                    
			    // Update UserID
			    // Setup JsonObject for updated UserID
			    JsonObject userIdUpdate = new JsonObject();
			    userIdUpdate.addProperty("UserName", newUsername);
				    
			    // Setup UpdateRequest
			    UpdateRequest updateUserIDRequest = new UpdateRequest(userRef,userIdUpdate);
				    
			    // Attempt update of UserID
			    UpdateResponse updateResponse = restApi.update(updateUserIDRequest);
				    
			    if (updateResponse.wasSuccessful()) {
				System.out.println("Successfully updated UserName: " + existUsername +
						" to: " + newUsername);
				System.out.println(String.format("Updated %s", updateResponse.getObject().get("_ref").getAsString()));
				String[] warningList;
				warningList = updateResponse.getWarnings();
				for (int i=0;i<warningList.length;i++) {
				    System.out.println(warningList[i]);
				}
			    } else {
				System.out.println("Error occurred attempting to update UserName: " + existUsername +
						" to: " + newUsername);
				String[] errorList;
				errorList = updateResponse.getErrors();
				for (int i=0;i<errorList.length;i++) {
				    System.out.println(errorList[i]);
				}
			    }
			} else {
			    System.out.println("New Username: " + newUsername + " Same as Exist Username: " + foundUserName);
			}
		    } else {
			System.out.println("UserID: " + existUsername  + " Not found in Rally. Skipping...");
		    }
		}			  
	    }
	} catch (Exception e) {
		System.out.println("Exception occurred while attempting to update Usernames.");
		e.printStackTrace();        	
	} finally {
	    //Release all resources
	    restApi.close();
	}    				
    }
}