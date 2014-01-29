package org.geminux.adboard.servlets;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

@SuppressWarnings("serial")
public class SearchAdsServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(SearchAdsServlet.class
			.getName());

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		PersistenceManager pm = PMF.get().getPersistenceManager();

		// Si l'utilisateur est connecté
		if (user != null) {

			// StringBuilder for logs
			StringBuilder logStr = new StringBuilder("Retrieved value are :");

			// Retrieved RAW fields from HTTP<$_POST>
			String paramCriteria = req.getParameter("criteria");
			String paramPriceMin = req.getParameter("pricemin");
			String paramPriceMax = req.getParameter("pricemax");
			String paramDateMin = req.getParameter("datemin");
			String paramDateMax = req.getParameter("datemax");

			// Retrieved TYPED fields from HTTP<$_POST>
			String criteria = null;
			Double priceMin = null;
			Double priceMax = null;
			Date dateMin = null;
			Date dateMax = null;

			// Documents from the Indexed DB than match search params
			Results<ScoredDocument> results = null;

			// List of Ads to be passed to the JSP for display
			List<Ad> adsList = new ArrayList<Ad>();

			// View where we'll redirect the user to
			RequestDispatcher view = req
					.getRequestDispatcher("/searchResult.jsp");

			if (paramCriteria != null && !paramCriteria.trim().isEmpty()) {
				criteria = paramCriteria;
				logStr.append("[criteria='" + criteria + "']");
			}

			try {

				if (paramPriceMin != null && !paramPriceMin.trim().isEmpty()) {
					try {
						priceMin = Double.valueOf(paramPriceMin);
					} catch (NumberFormatException e) {
						throw new Exception("'" + paramPriceMin
								+ "' n'est pas un nombre valide");
					}
					logStr.append("[priceMin='" + priceMin + "']");
				}

				if (paramPriceMax != null && !paramPriceMax.trim().isEmpty()) {
					try {
						priceMax = Double.valueOf(paramPriceMax);
					} catch (NumberFormatException e) {
						throw new Exception("'" + paramPriceMax
								+ "' n'est pas un nombre valide");
					}
					logStr.append("[priceMax='" + priceMax + "']");
				}

				if (paramDateMin != null && !paramDateMin.trim().isEmpty()) {
					try {
						dateMin = new SimpleDateFormat("dd/MM/yyyy")
								.parse(paramDateMin);
					} catch (ParseException e) {
						throw new Exception("'" + paramDateMin
								+ "' n'est pas une date valide");
					}
					logStr.append("[dateMin='" + dateMin + "']");
				}

				if (paramDateMax != null && !paramDateMax.trim().isEmpty()) {
					try {
						dateMax = new SimpleDateFormat("dd/MM/yyyy")
								.parse(paramDateMax);
					} catch (ParseException e) {
						throw new Exception("'" + paramDateMax
								+ "' n'est pas une date valide");
					}
					logStr.append("[dateMax='" + dateMax + "']");
				}

				log.info(logStr.toString());

				// Retrieve the list of Documents that match the parameters
				// entered.
				results = SearchIndexManager.INSTANCE.retrieveDocuments(
						criteria, priceMin, priceMax, dateMin, dateMax);

				// For each of the documents, extract out the id
				for (ScoredDocument scoredDocument : results) {
					long id = Long.valueOf(scoredDocument.getId());

					// Retrieve the Ad from JDO
					Ad ad = pm.getObjectById(Ad.class, id);

					// Add the Ad to the returned list of Ads
					adsList.add(ad);
				}

				// Set $_POST attributes to be passed to the JSP
				req.setAttribute("adList", adsList);
				req.setAttribute("criteria", criteria);

				view.forward(req, resp);
			} catch (ParseException e) {
				req.setAttribute("errMsg", e.getMessage());

				try {
					view.forward(req, resp);
				} catch (ServletException e1) {
					e1.printStackTrace();
				}
			} catch (NumberFormatException e) {
				req.setAttribute("errMsg", e.getMessage());

				try {
					view.forward(req, resp);
				} catch (ServletException e1) {
					e1.printStackTrace();
				}
			} catch (Exception e) {
				req.setAttribute("errMsg", e.getMessage());

				try {
					view.forward(req, resp);
				} catch (ServletException e1) {
					e1.printStackTrace();
				}

			} finally {
				pm.close();
			}

		} else {
			log.info("User not logged in");
			resp.sendRedirect("/adboard.jsp");
		}
	}
}