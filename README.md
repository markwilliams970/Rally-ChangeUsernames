Rally-ChangeUsernames
=====================

- Requires the following External Jars:
- [Apache Commons](http://archive.apache.org/dist/httpcomponents/httpclient/binary/httpcomponents-client-4.2.1-bin.zip):
- commons-codec-1.6.jar
- commons-logging-1.1.1.jar
- httpclient-4.2.1.jar
- httpclient-cache-4.2.1.jar
- httpcore-4.2.1.jar
- httpmime-4.2.1.jar
- [Google GSON](http://google-gson.googlecode.com/files/google-gson-2.1-release.zip):
- gson-2.1.jar
- gson-2.1-javadoc.jar
- gson-2.1-sources.jar
- [OpenCSV](http://sourceforge.net/projects/opencsv/files/latest/download):
- opencsv-2.3.jar
- opencsv-2.3-src-with-libs.tar.gz
- [Rally REST API](http://people.rallydev.com/connector/RallyRestApiJava/rally-rest-api-1.0.6.jar):
- rally-rest-api-1.0.6.jar


- Usage: java -classpath $CLASSPATH Rally_ChangeUsernames
- Specify the User-Defined variables required by the code to run:
<pre>
    // Credentials
    String userName = "rallysubadmin@company.com";
    String userPassword = "password";
    
    // CSV file containing User Mapping
    String userMappingFile = "C:\\Users\\username\\Documents\\RallyUtilities\\ChangeUsernames\\UserMappingTemplate.csv";
    
</pre>

- Requires input file specifying desired change in UserID's, formatted as follows (example):
<pre>
    ExistUsername,NewUsername
    user1@company.com,userOne@company.com
    user2@company.com,userTwo@company.com
    user3@company.com,userThree@company.com
</pre>

- Script will iterate through all UserIDs listed in userMappingFile and attemt to update to the specified new UserID. The Rally user running this code must have Subscription Administrator privileges.