package org.geminux.adboard.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.geminux.adboard.entities.Ad;
import org.geminux.adboard.persistence.PMF;
import org.geminux.adboard.search.SearchIndexManager;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class DeleteAdsServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(DeleteAdsServlet.class
			.getName());

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		PersistenceManager pm = PMF.get().getPersistenceManager();

		// Si l'utilisateur est connecté
		if (user != null) {

			// Retrieved fields from HTTP<$_POST>
			String[] adIds = req.getParameterValues("adId");

			// Au moins 1 enregistrement coché
			if (adIds != null) {
				
				// List of Ads to be deleted from the Persistence DB
				List<Ad> adsToDeleteList = new ArrayList<Ad>();

				// List of Doc Ids to be deleted from the Search DB
				List<String> docIdsToDeleteList = new ArrayList<String>();


				for (int i = 0; i < adIds.length; i++) {
					long adIdLong = Long.parseLong(adIds[i].replaceAll("/", ""));
					log.info("Find: " + adIdLong);

					// Retrieve the Ad from JDO
					Ad ad = pm.getObjectById(Ad.class, adIdLong);

					// Add the ad to the returned list of Ads
					adsToDeleteList.add(ad);

					// Add the id to the returned list of Ads
					docIdsToDeleteList.add(String.valueOf(ad.getKey().getId()));
				}

				try {
					// Delete objects from DB (JDO)
					pm.deletePersistentAll(adsToDeleteList);
					// Also delete objects from the indexed DB
					SearchIndexManager.INSTANCE
							.deleteDocumentFromIndex(docIdsToDeleteList);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					pm.close();
					resp.sendRedirect("/adboard.jsp");
				}
			} else {
				log.info("Nothing to delete");
				resp.sendRedirect("/adboard.jsp");
			}
			

		} else {
			log.info("User not logged in");
			resp.sendRedirect("/adboard.jsp");
		}
	}
}