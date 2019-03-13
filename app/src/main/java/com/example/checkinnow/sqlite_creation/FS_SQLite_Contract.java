package com.example.checkinnow.sqlite_creation;

import android.provider.BaseColumns;

public final class FS_SQLite_Contract {

    public FS_SQLite_Contract(){

    }

    public static class FSToSQLite implements BaseColumns{

        public static final String TABLE_NAME = "fs_to_sqlite";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_IMAGE_URL = "image_url";
        public static final String COLUMN_SCORE_CARD_REF = "score_card_ref";

    }

}
