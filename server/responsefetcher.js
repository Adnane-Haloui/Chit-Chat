
/**
 * Module dependencies.
 */

var express = require('express')
  , http = require('http')
  , path = require('path')
  , firebase = require("firebase")
  , request = require('request');
var config = require('./config.json');
var app = express();
console.log("state");
firebase.initializeApp({
  serviceAccount: config.service_account_file_name,
  databaseURL: config.firebase_database_url
});


var db = firebase.database();
var ref = db.ref("botChatItems");
function sendMessaggeToUser(data,userContact,sender){
  console.log("sending to ",userContact,data);
   var fbRef=db.ref("chatItems/"+userContact.substr(1));
   var nnodeRef=fbRef.push();
   nnodeRef.set({
     "sender":sender,
     "receiver":userContact,
     "is_bot":true,
     "type":"text",
     "content":data,
     "id":nnodeRef.key.substr(1),
     "timestamp":Number(new Date()),
     "g_timestamp": firebase.database.ServerValue.TIMESTAMP
   }, function(error) {
  if (error) {
    console.log("Data could not be saved." + error);
  } else {
    console.log("Data saved successfully.");
  }
});
 
}
function sendMessageToBot(data,botLink,sender,receiver) {
  request({
    url: botLink,
    method: 'POST',
    headers: {
      'Content-Type' :' application/json',
    },
    body: JSON.stringify(data)
  }, function(error, response, body) {
    if (error) { 
      console.log("error",error);
       }
    else if (response.statusCode >= 400) { 
      console.log('HTTP Error: '+response.statusCode+' - '+response.statusMessage); 
    }
    else{
      console.log("success: ",body);
      sendMessaggeToUser(body,receiver,sender);
    }
  });
}
 console.log("stated");
 
ref.on("child_added", function(snapshot, prevChildKey) {
  var botName=snapshot.key;
  var botRef=db.ref("botChatItems/"+botName);
  botRef.on("child_added",function(snapshot,prevChildKey){
    var data=snapshot.val();
	console.log("got",snapshot.key);
	var delRef=db.ref("botChatItems"+"/"+botName+"/"+snapshot.key);
	if(typeof(data.processed)==="undefined"){
    var bRef=db.ref("botList/"+botName);
	    bRef.once("value",function(snap) {
      var brefObj=snap.val();
//  console.log("got",brefObj);
        var messageData={ "type" : data.type 
            ,"messageText" : data.content
            ,"sender": data.sender
            ,"timestamp": data.timestamp
            ,"secret":brefObj.secret
          };
          console.log("sending",messageData);
        sendMessageToBot(messageData,brefObj.endpoint,botName,data.sender);
	  });
	    delRef.update({"processed":true});
	}

  });
});
