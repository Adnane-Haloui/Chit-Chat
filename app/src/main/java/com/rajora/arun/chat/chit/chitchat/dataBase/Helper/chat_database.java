package com.rajora.arun.chat.chit.chitchat.dataBase.Helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractChat;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractChatList;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractChatListMessage;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractContacts;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractLastMessage;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractLastMessageId;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractLastMessageTime;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractNotificationList;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractNotificationTempList;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractUnreadCount;


public class chat_database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION=40;
    private static final String DATABASE_NAME="chat.db";

    public chat_database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
	    final String QCREATE_TABLE=" CREATE TABLE ";
	    final String QTEXT=" TEXT ";
	    final String QBOOLEAN=" BOOLEAN ";
	    final String QINTEGER=" INTEGER ";
	    final String QNOTNULL=" NOT NULL ";
	    final String QUNIQUE=" UNIQUE ";
	    final String QSELECT=" SELECT ";
	    final String QWHERE=" WHERE ";
	    final String QFROM=" FROM ";
	    final String QDEFAULT0=" DEFAULT 0 ";
	    final String QAUTOINCREMENT=" AUTOINCREMENT ";
	    final String QPRIMARYKEY=" PRIMARY KEY ";
	    final String QON=" ON ";
	    final String QCREATEVIEW=" CREATE VIEW ";
	    final String QAS=" AS ";
	    final String QLEFT_OUTER_JOIN=" LEFT OUTER JOIN ";
	    final String QCOMMA=" , ";
	    final String QBRACESOPEN=" ( ";
	    final String QBRACESCLOSE=" ) ";
	    final String QSEMICOLON=" ; ";
	    final String QSPACE=" ";
	    final String QORDER_BY=" ORDER BY ";
	    final String QLIMIT=" LIMIT ";
	    final String QDESC=" DESC ";
	    final String QCOUNTALL=" COUNT(*) ";
	    final String QGROUPBY=" GROUP BY ";
	    final String QMAX=" MAX ";
	    final String QAND=" AND ";
	    final String QEQUAL=" = ";

		final String SQL_CREATE_CONTACTS_TABLE=QCREATE_TABLE+ ContractContacts.TABLE_NAME+QBRACESOPEN+
				ContractContacts._ID+QINTEGER+QPRIMARYKEY+QAUTOINCREMENT+QCOMMA+
	            ContractContacts.COLUMN_CONTACT_ID+QTEXT+QNOTNULL+QCOMMA+
			    ContractContacts.COLUMN_IS_BOT+QBOOLEAN+QNOTNULL+QDEFAULT0+QCOMMA+
	            ContractContacts.COLUMN_NAME+QTEXT+QCOMMA+
	            ContractContacts.COLUMN_ABOUT+QTEXT+QCOMMA+
	            ContractContacts.COLUMN_PIC_URL+QTEXT+QCOMMA+
	            ContractContacts.COLUMN_PIC_URI+QTEXT+QCOMMA+
	            ContractContacts.COLUMN_IS_USER+QBOOLEAN+QNOTNULL+QDEFAULT0+QCOMMA+
	            ContractContacts.COLUMN_DEV_NAME+QTEXT+QCOMMA+
				QUNIQUE+QBRACESOPEN+ContractContacts.COLUMN_CONTACT_ID+QCOMMA+
				ContractContacts.COLUMN_IS_BOT+QBRACESCLOSE+QBRACESCLOSE+QSEMICOLON;

	    final String SQL_CREATE_CHAT_TABLE=QCREATE_TABLE+ ContractChat.TABLE_NAME+QBRACESOPEN+
			    ContractChat._ID+QINTEGER+QPRIMARYKEY+QAUTOINCREMENT+QCOMMA+
			    ContractChat.COLUMN_CONTACT_ID+QTEXT+QNOTNULL+QCOMMA+
			    ContractChat.COLUMN_CHAT_ID+QTEXT+QNOTNULL+QCOMMA+
	            ContractChat.COLUMN_IS_BOT+QBOOLEAN+QNOTNULL+QDEFAULT0+QCOMMA+
			    ContractChat.COLUMN_MESSAGE_DIRECTION+QTEXT+QCOMMA+
			    ContractChat.COLUMN_TIMESTAMP+QINTEGER+QNOTNULL+QCOMMA+
	            ContractChat.COLUMN_MESSAGE+QTEXT+QCOMMA+
		        ContractChat.COLUMN_MESSAGE_TYPE+QTEXT+QCOMMA+
		        ContractChat.COLUMN_MESSAGE_STATUS+QTEXT+QCOMMA+
			    ContractChat.COLUMN_UPLOAD_STATUS+QTEXT+QCOMMA+
		        ContractChat.COLUMN_EXTRA_URI+QTEXT+QCOMMA+
			    QUNIQUE+QBRACESOPEN+ContractChat.COLUMN_CHAT_ID+QCOMMA+
			    ContractChat.COLUMN_CONTACT_ID+QCOMMA+
			    ContractChat.COLUMN_IS_BOT+QBRACESCLOSE+QBRACESCLOSE+QSEMICOLON;

	    final String SQL_CREATE_NOTIFICATION_TEMP_LIST_TABLE=QCREATEVIEW+
			    ContractNotificationTempList.TABLE_NAME+QAS+
			    QSELECT+ContractChat.TN_COLUMN_CONTACT_ID+QSPACE+
			    ContractNotificationTempList.COLUMN_CONTACT_ID+QCOMMA+
			    ContractChat.TN_COLUMN_IS_BOT+QSPACE+
			    ContractNotificationTempList.COLUMN_IS_BOT+QCOMMA+
			    ContractChat.TN_COLUMN_TIMESTAMP+QSPACE+
			    ContractNotificationTempList.COLUMN_MESSAGE_TIMESTAMP+QCOMMA+
			    ContractChat.TN_COLUMN_MESSAGE+QSPACE+
			    ContractNotificationTempList.COLUMN_MESSAGE+QCOMMA+
			    ContractChat.TN_COLUMN_MESSAGE_TYPE+QSPACE+
			    ContractNotificationTempList.COLUMN_MESSAGE_TYPE+
			    QFROM+ContractChat.TABLE_NAME+QWHERE+ContractChat.TN_COLUMN_MESSAGE_STATUS+
			    " = \"UNREAD\" AND "+ContractChat.TN_COLUMN_MESSAGE_DIRECTION+
			    "= \"RECEIVED\" "+QORDER_BY+ContractChat.TN_COLUMN_TIMESTAMP+
			    QDESC+QLIMIT+" 10 "+QSEMICOLON;

	    final String SQL_CREATE_NOTIFICATION_LIST_TABLE=QCREATEVIEW+
			    ContractNotificationList.TABLE_NAME+QAS+
			    QSELECT+ContractNotificationTempList.TN_COLUMN_CONTACT_ID+QSPACE+
			    ContractNotificationList.COLUMN_CONTACT_ID+QCOMMA+
			    ContractNotificationTempList.TN_COLUMN_IS_BOT+QSPACE+
			    ContractNotificationList.COLUMN_IS_BOT+QCOMMA+
			    ContractNotificationTempList.TN_COLUMN_MESSAGE_TIMESTAMP+QSPACE+
			    ContractNotificationList.COLUMN_MESSAGE_TIMESTAMP+QCOMMA+
			    ContractNotificationTempList.TN_COLUMN_MESSAGE+QSPACE+
			    ContractNotificationList.COLUMN_MESSAGE+QCOMMA+
			    ContractNotificationTempList.TN_COLUMN_MESSAGE_TYPE+QSPACE+
			    ContractNotificationList.COLUMN_MESSAGE_TYPE+QCOMMA+
			    ContractContacts.COLUMN_NAME+QSPACE+
			    ContractNotificationList.COLUMN_NAME+QCOMMA+
			    ContractContacts.TN_COLUMN_PIC_URL+QSPACE+
			    ContractNotificationList.COLUMN_PIC_URL+QCOMMA+
			    ContractContacts.TN_COLUMN_PIC_URI+QSPACE+
			    ContractNotificationList.COLUMN_PIC_URI+
			    QFROM+ContractNotificationTempList.TABLE_NAME+QLEFT_OUTER_JOIN+ContractContacts.TABLE_NAME
			    +QON+ContractNotificationTempList.TN_COLUMN_CONTACT_ID+" = "+
			    ContractContacts.TN_COLUMN_CONTACT_ID+" AND "+ContractNotificationTempList.TN_COLUMN_IS_BOT
			    +" = "+ContractContacts.TN_COLUMN_IS_BOT+QSEMICOLON;

	    final String SQL_CREATE_UNREAD_COUNT_TABLE=QCREATEVIEW+ ContractUnreadCount.TABLE_NAME+QAS+
			    QSELECT+ContractChat.TN_COLUMN_ID+QSPACE+ContractUnreadCount._ID+QCOMMA+
			    QCOUNTALL+QSPACE+ContractUnreadCount.COLUMN_UNREAD_COUNT+
				QFROM+ContractChat.TABLE_NAME+QWHERE+ContractChat.TN_COLUMN_MESSAGE_STATUS+" = \"UNREAD\" "+
			    QGROUPBY+ContractChat.TN_COLUMN_CONTACT_ID+QCOMMA+ContractChat.TN_COLUMN_IS_BOT+QSEMICOLON;


	    final String SQL_CREATE_LAST_MESSAGE_TIME=QCREATEVIEW+ ContractLastMessageTime.TABLE_NAME+QAS+
			    QSELECT+ContractChat.TN_COLUMN_CONTACT_ID+QSPACE+ContractLastMessageTime.COLUMN_CONTACT_ID+QCOMMA+
			    ContractChat.TN_COLUMN_IS_BOT+QSPACE+ContractLastMessageTime.COLUMN_IS_BOT+QCOMMA+
			    QMAX+QBRACESOPEN+ContractChat.TN_COLUMN_TIMESTAMP+QBRACESCLOSE+QSPACE+ContractLastMessageTime.COLUMN_LAST_MESSAGE_TIMESTAMP+
			    QFROM+ContractChat.TABLE_NAME+QGROUPBY+ContractChat.TN_COLUMN_CONTACT_ID+QCOMMA+ContractChat.TN_COLUMN_IS_BOT+QSEMICOLON;

	    final String SQL_CREATE_LAST_MESSAGE_ID=QCREATEVIEW+ ContractLastMessageId.TABLE_NAME+QAS+
			    QSELECT+QMAX+QBRACESOPEN+ContractChat.TN_COLUMN_ID+QBRACESCLOSE+QSPACE+ContractLastMessageId._ID+
			    QFROM+ContractLastMessageTime.TABLE_NAME+QLEFT_OUTER_JOIN+ContractChat.TABLE_NAME+
			    QON+ContractLastMessageTime.TN_COLUMN_CONTACT_ID+QEQUAL+ContractChat.TN_COLUMN_CONTACT_ID+
			    QAND+ContractLastMessageTime.TN_COLUMN_IS_BOT+QEQUAL+ContractChat.TN_COLUMN_IS_BOT+
			    QAND+ContractLastMessageTime.TN_COLUMN_LAST_MESSAGE_TIMESTAMP+QEQUAL+ContractChat.TN_COLUMN_TIMESTAMP+
			    QGROUPBY+ContractChat.TN_COLUMN_CONTACT_ID+QCOMMA+ContractChat.TN_COLUMN_IS_BOT+QSEMICOLON;

	    final String SQL_CREATE_LAST_MESSAGE=QCREATEVIEW+ ContractLastMessage.TABLE_NAME+QAS+
			    QSELECT+ContractChat.TN_COLUMN_ID+QSPACE+ContractLastMessage._ID+QCOMMA+
			    ContractChat.TN_COLUMN_CONTACT_ID+QSPACE+ContractLastMessage.COLUMN_CONTACT_ID+QCOMMA+
			    ContractChat.TN_COLUMN_IS_BOT+QSPACE+ContractLastMessage.COLUMN_IS_BOT+QCOMMA+
			    ContractChat.TN_COLUMN_TIMESTAMP+QSPACE+ContractLastMessage.COLUMN_LAST_MESSAGE_TIMESTAMP+QCOMMA+
			    ContractChat.TN_COLUMN_MESSAGE+QSPACE+ContractLastMessage.COLUMN_LAST_MESSAGE+QCOMMA+
			    ContractChat.TN_COLUMN_MESSAGE_TYPE+QSPACE+ContractLastMessage.COLUMN_LAST_MESSAGE_TYPE+QSPACE+
			    QFROM+ContractLastMessageId.TABLE_NAME+QLEFT_OUTER_JOIN+ContractChat.TABLE_NAME+QON+
			    ContractChat.TN_COLUMN_ID+QEQUAL+ContractLastMessageId.TN_COLUMN_ID+QSEMICOLON;

	    final String SQL_CREATE_CHAT_LIST_MESSAGE=QCREATEVIEW+ ContractChatListMessage.TABLE_NAME+QAS+
			    QSELECT+ContractLastMessage.TN_COLUMN_ID+QSPACE+ContractChatListMessage._ID+QCOMMA+
			    ContractLastMessage.TN_COLUMN_CONTACT_ID+QSPACE+ContractChatListMessage.COLUMN_CONTACT_ID+QCOMMA+
			    ContractLastMessage.TN_COLUMN_IS_BOT+QSPACE+ContractChatListMessage.COLUMN_IS_BOT+QCOMMA+
			    ContractLastMessage.TN_COLUMN_LAST_MESSAGE+QSPACE+ContractChatListMessage.COLUMN_LAST_MESSAGE+QCOMMA+
			    ContractLastMessage.COLUMN_LAST_MESSAGE_TYPE+QSPACE+ContractChatListMessage.COLUMN_LAST_MESSAGE_TYPE+QCOMMA+
			    ContractLastMessage.TN_COLUMN_LAST_MESSAGE_TIMESTAMP+QSPACE+ContractChatListMessage.COLUMN_LAST_MESSAGE_TIMESTAMP+QCOMMA+
			    ContractUnreadCount.TN_COLUMN_UNREAD_COUNT+QSPACE+ContractChatListMessage.COLUMN_UNREAD_COUNT+
			    QFROM+ContractLastMessage.TABLE_NAME+QLEFT_OUTER_JOIN+ContractUnreadCount.TABLE_NAME+
			    QON+ContractLastMessage.TN_COLUMN_ID+QEQUAL+ContractUnreadCount.TN_COLUMN_ID+QSEMICOLON;

	    final String SQL_CREATE_CHAT_LIST_TABLE=QCREATEVIEW+ ContractChatList.TABLE_NAME+QAS+
			    QSELECT+ContractChatListMessage.TN_COLUMN_ID+QSPACE+ContractChatList._ID+QCOMMA+
			    ContractChatListMessage.TN_COLUMN_CONTACT_ID+QSPACE+ContractChatList.COLUMN_CONTACT_ID+QCOMMA+
			    ContractChatListMessage.TN_COLUMN_IS_BOT+QSPACE+ContractChatList.COLUMN_IS_BOT+QCOMMA+
			    ContractChatListMessage.TN_COLUMN_LAST_MESSAGE+QSPACE+ContractChatList.COLUMN_LAST_MESSAGE+QCOMMA+
			    ContractChatListMessage.TN_COLUMN_LAST_MESSAGE_TYPE+QSPACE+ContractChatList.COLUMN_LAST_MESSAGE_TYPE+QCOMMA+
			    ContractChatListMessage.TN_COLUMN_LAST_MESSAGE_TIMESTAMP+QSPACE+ContractChatList.COLUMN_LAST_MESSAGE_TIMESTAMP+QCOMMA+
			    ContractChatListMessage.TN_COLUMN_UNREAD_COUNT+QSPACE+ContractChatList.COLUMN_UNREAD_COUNT+QCOMMA+
			    ContractContacts.TN_COLUMN_NAME+QSPACE+ContractChatList.COLUMN_NAME+QCOMMA+
			    ContractContacts.TN_COLUMN_PIC_URL+QSPACE+ContractChatList.COLUMN_PIC_URL+QCOMMA+
			    ContractContacts.TN_COLUMN_PIC_URI+QSPACE+ContractChatList.COLUMN_PIC_URI+
			    QFROM+ContractChatListMessage.TABLE_NAME+QLEFT_OUTER_JOIN+ContractContacts.TABLE_NAME+
			    QON+ContractChatListMessage.TN_COLUMN_CONTACT_ID+QEQUAL+ContractContacts.TN_COLUMN_CONTACT_ID+QAND+
			    ContractChatListMessage.TN_COLUMN_IS_BOT+QEQUAL+ContractContacts.TN_COLUMN_IS_BOT+QSEMICOLON;


        Log.d("findme",SQL_CREATE_CONTACTS_TABLE);
        Log.d("findme",SQL_CREATE_CHAT_TABLE);
        Log.d("findme",SQL_CREATE_NOTIFICATION_TEMP_LIST_TABLE);
        Log.d("findme",SQL_CREATE_NOTIFICATION_LIST_TABLE);
        Log.d("findme",SQL_CREATE_UNREAD_COUNT_TABLE);
        Log.d("findme",SQL_CREATE_LAST_MESSAGE_TIME);
        Log.d("findme",SQL_CREATE_LAST_MESSAGE_ID);
        Log.d("findme",SQL_CREATE_LAST_MESSAGE);
        Log.d("findme",SQL_CREATE_CHAT_LIST_MESSAGE);
        Log.d("findme",SQL_CREATE_CHAT_LIST_TABLE);

            db.execSQL(SQL_CREATE_CONTACTS_TABLE);
            db.execSQL(SQL_CREATE_CHAT_TABLE);
            db.execSQL(SQL_CREATE_NOTIFICATION_TEMP_LIST_TABLE);
            db.execSQL(SQL_CREATE_NOTIFICATION_LIST_TABLE);
            db.execSQL(SQL_CREATE_UNREAD_COUNT_TABLE);
            db.execSQL(SQL_CREATE_LAST_MESSAGE_TIME);
            db.execSQL(SQL_CREATE_LAST_MESSAGE_ID);
            db.execSQL(SQL_CREATE_LAST_MESSAGE);
            db.execSQL(SQL_CREATE_CHAT_LIST_MESSAGE);
            db.execSQL(SQL_CREATE_CHAT_LIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ContractContacts.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ContractChat.TABLE_NAME);;

        db.execSQL("DROP VIEW IF EXISTS "+ContractNotificationTempList.TABLE_NAME);
        db.execSQL("DROP VIEW IF EXISTS "+ContractNotificationList.TABLE_NAME);
        db.execSQL("DROP VIEW IF EXISTS "+ContractUnreadCount.TABLE_NAME);
        db.execSQL("DROP VIEW IF EXISTS "+ContractLastMessageTime.TABLE_NAME);
        db.execSQL("DROP VIEW IF EXISTS "+ContractLastMessageId.TABLE_NAME);
        db.execSQL("DROP VIEW IF EXISTS "+ContractLastMessage.TABLE_NAME);
        db.execSQL("DROP VIEW IF EXISTS "+ContractChatListMessage.TABLE_NAME);
        db.execSQL("DROP VIEW IF EXISTS "+ContractChatList.TABLE_NAME);

        onCreate(db);
    }
}
