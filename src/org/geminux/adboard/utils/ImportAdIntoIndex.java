/**
 * This utility is used to load Ad in the Google App Engine Search Index. 
 */

package org.geminux.adboard.utils;

import java.util.Date;
import java.util.logging.Logger;

import org.geminux.adboard.entities.Ad;
import org.geminux.adboard.global.Constants;
import org.geminux.adboard.search.SearchIndexManager;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;

public class ImportAdIntoIndex {
	private static final Logger log = Logger.getLogger(ImportAdIntoIndex.class
			.getName());

	/**
	 * 1. Retrieve Ad attributes 2. Create a Document Object with each attribute
	 * of the Document object populated accordingly. 3. The Document is then
	 * added to the index via the SearchIndexManager method
	 * 
	 * @param ad
	 *            An ad
	 * @throws Exception
	 */
	public static void processAd(Ad ad) throws Exception {

		long adId = ad.getKey().getId();
		String adAuthor = ad.getAuthor().getUserId();
		String adDescription = ad.getDescription();
		String adTitle = ad.getTitle();
		float adPrice = ad.getPrice();
		Date adDate = ad.getDate();
		try {
			if (adId != 0) {
				// Build a Document Object
				// Add all the attributes on which search can be done
				Document newDoc = Document
						.newBuilder()
						.setId(String.valueOf(adId))
						.addField(
								Field.newBuilder().setName("adAuthor")
										.setText(adAuthor))
						.addField(
								Field.newBuilder().setName("adDescription")
										.setText(adDescription))
						.addField(
								Field.newBuilder().setName("adTitle")
										.setText(adTitle))
						.addField(
								Field.newBuilder().setName("adPrice")
										.setNumber(adPrice))
						.addField(
								Field.newBuilder().setName("adDate")
										.setDate(adDate)).build();

				// Add the Document instance to the Search Index
				SearchIndexManager.INSTANCE.indexDocument(
						Constants.ADS_INDEX_NAME, newDoc);
			} else {
				throw new Exception("adId field is empty");
			}
		} catch (Exception ex) {
			log.warning("Could not process Record for Ad : " + adId
					+ ".Reason : " + ex.getMessage());
		}
	}
}
