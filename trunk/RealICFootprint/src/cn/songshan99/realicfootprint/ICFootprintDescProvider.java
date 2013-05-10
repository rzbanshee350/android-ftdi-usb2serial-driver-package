package cn.songshan99.realicfootprint;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class ICFootprintDescProvider extends ContentProvider {
	
	private ICFootprintDescDatabase mICFootprintDescDatabase;
	

	public static String AUTHORITY = "cn.songshan99.realicfootprint.ICFootprintDescPrivider";
	public static String PATH = "description";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/"+PATH);
	
	private static final int SEARCH_DESCS = 0;
    private static final int GET_DESC_BY_ROWID = 1;
    private static final int SEARCH_SUGGEST = 2;
    private static final int REFRESH_SHORTCUT = 3;
    
    private static final UriMatcher sURIMatcher = buildUriMatcher();
    
	/**
     * Builds up a UriMatcher for search suggestion and shortcut refresh queries.
     */
    private static UriMatcher buildUriMatcher() {
    	//TODO: this is a similar copy of DictionaryProvider.java. Need to see if this suits our need.
    	UriMatcher matcher =  new UriMatcher(UriMatcher.NO_MATCH);
        // to get definitions...30700
        matcher.addURI(AUTHORITY, PATH, SEARCH_DESCS);
        matcher.addURI(AUTHORITY, PATH+"/#", GET_DESC_BY_ROWID);
        // to get suggestions...
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);

        /* The following are unused in this implementation, but if we include
         * {@link SearchManager#SUGGEST_COLUMN_SHORTCUT_ID} as a column in our suggestions table, we
         * could expect to receive refresh queries when a shortcutted suggestion is displayed in
         * Quick Search Box, in which case, the following Uris would be provided and we
         * would return a cursor with a single item representing the refreshed suggestion data.
         */
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT, REFRESH_SHORTCUT);
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT + "/*", REFRESH_SHORTCUT);
        return matcher;
    }
    
	@Override
	public String getType(Uri uri) {
		// TODO: implement
		return null;
	}

	@Override
	public boolean onCreate() {
		mICFootprintDescDatabase = new ICFootprintDescDatabase(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// Use the UriMatcher to see what kind of query we have and format the
		// db query accordingly
		switch (sURIMatcher.match(uri)) {
		case SEARCH_SUGGEST:
			if (selectionArgs == null) {
				throw new IllegalArgumentException(
						"selectionArgs must be provided for the Uri: " + uri);
			}
			return getSuggestions(selectionArgs[0]);

		case SEARCH_DESCS:
			if (selectionArgs == null) {
				throw new IllegalArgumentException(
						"selectionArgs must be provided for the Uri: " + uri);
			}
			return searchAllMatching(selectionArgs[0]);

		case GET_DESC_BY_ROWID:
			return getICFootprint(uri);
			
		case REFRESH_SHORTCUT:
			return refreshShortcut(uri);
			
		default:
			throw new IllegalArgumentException("Unknown Uri: " + uri);
		}
		// return null;
	}
	
    private Cursor getSuggestions(String query) {
        query = query.toLowerCase();
        String[] columns = new String[] {
            BaseColumns._ID,
            ICFootprintDescDatabase.KEY_DESCRIPTION,
            ICFootprintDescDatabase.KEY_FILENAME,
         /* SearchManager.SUGGEST_COLUMN_SHORTCUT_ID,
                          (only if you want to refresh shortcuts) */
            SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID};

        return mICFootprintDescDatabase.getICFootprintMatches(query, columns);
    }
    
	private Cursor searchAllMatching(String query) {
		query = query.toLowerCase();
		String[] columns = new String[] { BaseColumns._ID,
				ICFootprintDescDatabase.KEY_DESCRIPTION, ICFootprintDescDatabase.KEY_FILENAME };

		return mICFootprintDescDatabase.getICFootprintMatches(query, columns);
	}
	
    private Cursor getICFootprint(Uri uri) {
        String rowId = uri.getLastPathSegment();
        String[] columns = new String[] {
        	ICFootprintDescDatabase.KEY_DESCRIPTION, ICFootprintDescDatabase.KEY_FILENAME};

        return mICFootprintDescDatabase.getICFootprintByRowId(rowId, columns);
    }
    
    private Cursor refreshShortcut(Uri uri) {
        /* This won't be called with the current implementation, but if we include
         * {@link SearchManager#SUGGEST_COLUMN_SHORTCUT_ID} as a column in our suggestions table, we
         * could expect to receive refresh queries when a shortcutted suggestion is displayed in
         * Quick Search Box. In which case, this method will query the table for the specific
         * word, using the given item Uri and provide all the columns originally provided with the
         * suggestion query.
         */
        String rowId = uri.getLastPathSegment();
        String[] columns = new String[] {
            BaseColumns._ID,
            ICFootprintDescDatabase.KEY_DESCRIPTION, 
            ICFootprintDescDatabase.KEY_FILENAME,
            SearchManager.SUGGEST_COLUMN_SHORTCUT_ID,
            SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID};

        return mICFootprintDescDatabase.getICFootprintByRowId(rowId, columns);
    }
    
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}
}
