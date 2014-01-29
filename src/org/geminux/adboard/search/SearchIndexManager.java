/**
 * This class handles all the interaction with Google AppEngine Search API. 
 * Currently, it has methods for:
 */
package org.geminux.adboard.search;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.geminux.adboard.global.Constants;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.PutException;
import com.google.appengine.api.search.Query;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.search.StatusCode;

public enum SearchIndexManager {
	INSTANCE;

	private static final Logger log = Logger.getLogger(SearchIndexManager.class
			.getName());

	/**
	 * This method is used to add a Document to a particular Index
	 * 
	 * @param indexName
	 *            This is the name of the Index to which the document is to be
	 *            added. An index serves as a logical collection of documents
	 * @param document
	 *            This is the Document instance that needs to be added to the
	 *            Index
	 */
	public void indexDocument(String indexName, Document document) {
		// Setup the Index
		IndexSpec indexSpec = IndexSpec.newBuilder().setName(indexName).build();
		Index index = SearchServiceFactory.getSearchService().getIndex(
				indexSpec);

		try {
			// Put the Document in the Index. If the document is already
			// existing, it will be overwritten
			index.put(document);
			log.info("Document : " + document.getId() + "successfully added.");

		} catch (PutException e) {
			if (StatusCode.TRANSIENT_ERROR.equals(e.getOperationResult()
					.getCode())) {
				// retry putting the document
			}
		}
	}

	/**
	 * This method is used to retrieve a particular Document from the Index
	 * 
	 * @param documentId
	 *            This is the key field that uniquely identifies a document in
	 *            the collection i.e. the Index. In our case it is the user id
	 * @return An instance of the Document object from the Index.
	 */
	public Document retrieveDocument(String documentId) {
		// Setup the Index
		IndexSpec indexSpec = IndexSpec.newBuilder()
				.setName(Constants.ADS_INDEX_NAME).build();
		Index index = SearchServiceFactory.getSearchService().getIndex(
				indexSpec);

		// Retrieve the Record from the Index
		return index.get(documentId);
	}

	/**
	 * This method is used to retrieve a list of documents from multiple
	 * criteria
	 * 
	 * @param criteria
	 * @param priceMin
	 * @param priceMax
	 * @param dateMin
	 * @param dateMax
	 * @return collection of Documents that were found
	 * @throws Exception
	 */
	public Results<ScoredDocument> retrieveDocuments(String criteria,
			Double priceMin, Double priceMax, Date dateMin, Date dateMax)
			throws Exception {

		int filterCnt = 0;

		// A query string
		StringBuilder queryStringBuilder = new StringBuilder();

		// keyword selector
		if (criteria != null && !criteria.trim().isEmpty()) {
			queryStringBuilder.append("(adTitle: " + criteria
					+ " OR adDescription: " + criteria + ") ");
			filterCnt++;

		}

		// price selector
		if ((priceMin == null && priceMax == null)
				|| (priceMin == 0 && priceMax == 0)) {
			// Ignore the price as selector
		} else {
			if (isValidPriceRange(priceMin, priceMax)) {
				queryStringBuilder.append("(adPrice>=" + priceMin
						+ " AND adPrice<=" + priceMax + ") ");
				filterCnt++;
			} else {
				throw new Exception("'" + priceMin + "'-'" + priceMax
						+ "' n'est pas une plage de prix valide");
			}
		}

		// date selector
		if (dateMin == null && dateMax == null) {
			// Ignore the date as selector

		} else {
			if (isValidDateRange(dateMin, dateMax)) {
				String dateMin4Request = new SimpleDateFormat("yyyy-MM-dd")
						.format(dateMin);
				String dateMax4Request = new SimpleDateFormat("yyyy-MM-dd")
						.format(dateMax);

				queryStringBuilder.append("(adDate>=" + dateMin4Request
						+ " AND adDate<=" + dateMax4Request + ") ");
				filterCnt++;
			} else {
				throw new Exception("'" + dateMin + "'-'" + dateMax
						+ "' n'est pas une plage de dates valide");
			}
		}

		if (filterCnt < 1) {
			throw new Exception(
					"Vous devez utiliser au moins un filtre (mot-clé | plage de prix | plage de date)");
		}

		Query query = Query.newBuilder().build(queryStringBuilder.toString());

		// Setup the Index
		IndexSpec indexSpec = IndexSpec.newBuilder()
				.setName(Constants.ADS_INDEX_NAME).build();
		Index index = SearchServiceFactory.getSearchService().getIndex(
				indexSpec);
		// Retrieve the Records from the Index
		return index.search(query);
	}

	/**
	 * 
	 * @param min
	 * @param max
	 * @return
	 * @throws Exception
	 */
	private boolean isValidPriceRange(Double min, Double max) {
		Double[] values = { min, max };
		boolean result = false;

		for (Double d : values) {
			if (d == null || d.isNaN() || Math.signum(d) == -1.0) {
				log.info("min|max must be a double >=0");
				return false;
			}
		}

		if (Math.signum(max.compareTo(min)) == -1.0) {
			result = false;
			log.info(" min|max seems to be inverted");
		} else {
			result = true;
		}

		return result;

	}

	/**
	 * Return true only if dateMax is strictly later than dateMin
	 * 
	 * @param dateMin
	 * @param dateMax
	 * @return
	 */
	private boolean isValidDateRange(Date dateMin, Date dateMax) {
		boolean result = false;

		if (dateMin != null && dateMax != null) {
			if (dateMax.after(dateMin)) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * This method is used to delete a document from the Index
	 * 
	 * @param documentId
	 *            This is the key field that uniquely identifies a document in
	 *            the collection i.e. the Index. In our case it is the Key id
	 */

	public void deleteDocumentFromIndex(String documentId) {
		// Setup the Index
		IndexSpec indexSpec = IndexSpec.newBuilder()
				.setName(Constants.ADS_INDEX_NAME).build();
		Index index = SearchServiceFactory.getSearchService().getIndex(
				indexSpec);

		// Delete the Records from the Index
		index.delete(documentId);
	}

	/**
	 * This method is used to delete several document from the Index
	 * 
	 * @param documentIds
	 *            This is the List of keys field that uniquely identifies a
	 *            document in the collection i.e. the Index. In our case it is
	 *            the Key id
	 */

	public void deleteDocumentFromIndex(List<String> documentIds) {
		// Setup the Index
		IndexSpec indexSpec = IndexSpec.newBuilder()
				.setName(Constants.ADS_INDEX_NAME).build();
		Index index = SearchServiceFactory.getSearchService().getIndex(
				indexSpec);

		// Delete the Records from the Index
		index.delete(documentIds);
	}

}
