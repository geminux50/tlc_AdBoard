package org.geminux.adboard.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.geminux.adboard.entities.Ad;
import org.geminux.adboard.persistence.PMF;
import org.geminux.adboard.search.SearchIndexManager;

import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class SearchAdsServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(SearchAdsServlet.class
			.getName());

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		PersistenceManager pm = PMF.get().getPersistenceManager();

		if (user != null) {
			String logStr = null;
			String criteria = req.getParameter("criteria");
			String priceMin = req.getParameter("pricemin");
			String priceMax = req.getParameter("pricemax");
			Double priceMinDouble = null;
			Double priceMaxDouble = null;

			if (criteria != null && !criteria.trim().isEmpty()) {
				logStr = "Looking for ad with criteria '" + criteria;
				log.info(logStr);

				List<Ad> adsList = new ArrayList<Ad>();

				// Retrieve the list of Documents that match the SearchText
				// entered.
				Results<ScoredDocument> results = SearchIndexManager.INSTANCE
						.retrieveDocuments(criteria, priceMinDouble, priceMaxDouble, null, null);

				// For each of the documents, extract out the attributes from
				// the document and populate the Employee entity class
				// Add the Employee object to the Employees collection
				for (ScoredDocument scoredDocument : results) {
					long id = Long.valueOf(scoredDocument.getId());

					// Retrieve the Ad from JDO
					Ad ad = pm.getObjectById(Ad.class, id);

					// Add the add to the returned list of Ads
					adsList.add(ad);
				}

				// String query = "select from " + Ad.class.getName()
				// + " where title:" + criteria +" order by date desc";
				// List<Ad> ads = (List<Ad>) pm.newQuery(query)
				// .execute();
				// req.setAttribute("adList", ads);
				req.setAttribute("adList", adsList);

				req.setAttribute("criteria", criteria);

				RequestDispatcher view = req
						.getRequestDispatcher("/searchResult.jsp");
				try {
					view.forward(req, resp);
				} catch (ServletException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					pm.close();
				}
				// resp.sendRedirect("/adboard.jsp");
			} else {
				log.info("Missing criteria");
				resp.sendRedirect("/adboard.jsp");
			}
		} else {
			log.info("User not logged in");
			resp.sendRedirect("/adboard.jsp");
		}
	}
}