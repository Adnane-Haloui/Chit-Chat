# Chit-Chat
it is an android app to chat with people. it has support for chat bots in two forms-2nd person and assistant chat bots.

### this project has 2 apps and 1 library. the apps are in "app","chitchatdevelopers" directory and the library is in "authenticator" library.

### the apks can be found in apk directory.

## Steps to build the project-

####PART 1-
1) Goto console.firebase.google.com and create a new project- lets name it "chit-chat".
2) click add app. add package name as- "com.rajora.arun.chat.chit.chitchat" (without quotes).
3) a google-services.json file would be offered to download. Download it and put it in this projects "app" directory.

4)click add app again. this time package name is- "com.rajora.arun.chat.chit.chitchatdevelopers" (without quotes).
5) a google-services.json file would be offered to download. this time save it in "chitchatdevelopers" directory.

6)in the settings of this firebase project, goto "cloud messaging tab and copy the "server key" in the "config.json" file in project's "server" directory as "fcm_auth_key".

6)in firebase database select "database" section. copy the database url-(which looks like "https://xxx.firebaseio.com/").
  copy this url in "config.json" in project's "server" directory as "firebase_database_url" value.
 
####PART 2-
1) go to https://firebase.google.com/docs/admin/setup and follow steps under the second heading- "add firebase to your app"(under Prerequisites heading).
2) a service account credential json file is received.
3)save this file in projects "server" directory.
4) in the same "server" directory there is a "config.json" file.add the file name you just downloaded in this file's "service_account_file_name" field. example if the file name is "abc.json"(without quotes) enter "abc.json" in the "service_account_file_name_field".
