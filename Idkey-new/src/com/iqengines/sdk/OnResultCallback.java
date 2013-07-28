package com.iqengines.sdk;

import java.io.File;


public interface OnResultCallback {
	/**
	 * This method gets called by IQE when unique query id is assigned to search query
	 * 
	 * @param queryId 
	 * 		  A {@link String} representing the ID assigned to this search query.
	 * @param pathName
	 *        A {@link String} the local path to the picture's data.
	 * @param remoteSearch
	 * 		  A {@link Integer}, defines what sent the query (snap, scan ...).       
	 */
	public void onQueryIdAssigned(String queryId, String pathName);

	/**
	 * This method gets called by IQE whenever search result is available.
	 * 
	 *  @param queryId
	 *  	   A {@link String} giving the query ID of the search query.
	 *  @param objId
	 *  	   A {@link String} giving the object ID of match found by IQE. null if no match found.
	 *  @param objName
	 *  	   A {@link String} which is the object name (label) of the match found by IQE.  null if no match found.
	 *  @param objMeta
	 *  	   A {@link String}which are object meta information of the match found by IQE.  null if no match found.
	 *  @param Barcode
	 *  	   A {@link Integer}, gives the engine matching (local, remote, barcode).   
	 *  @param e 
	 *         An {@link Integer}, defines what sent the query (snap, scan ...). 
	 */
	public void onResult(String queryId, String objId, String objName, String objMeta);

	/**
	 *This method gets called when no results are found
	 *  
	 *  @param snap
	 *  	   A {@link boolean} if true, the query has been started with a snapped button. If false the query has been started with continuous scanning.
	 *  @param e 
	 *      An {@link Exception} that occurred during the search. null is search finished without exceptions.
	 *  @param imgFile
	 *  	   The
	 */

	public void onNoResult(Exception e, File imgFile);
}
