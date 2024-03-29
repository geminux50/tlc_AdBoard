package org.geminux.adboard.servlets;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.geminux.adboard.entities.Ad;
import org.geminux.adboard.persistence.PMF;
import org.geminux.adboard.utils.ImportAdIntoIndex;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class AddAdsServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(AddAdsServlet.class
			.getName());

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		// Si l'utilisateur est connecté
		if (user != null) {
			// On récupère les valeurs du POST. On a autant de valeur de
			// (title|description|price) que d'annonces à ajouter
			String[] titles = req.getParameterValues("title");
			String[] descriptions = req.getParameterValues("description");
			String[] prices = req.getParameterValues("price");

			// On itère sur ces valeur
			for (int i = 0; i < titles.length; i++) {
				String title = titles[i];
				String description = descriptions[i];
				float price = 0;
				try {
					price = Float.parseFloat(prices[i]);
					if (title != null && !title.trim().isEmpty()) {
						if (description != null
								&& !description.trim().isEmpty()) {
							if (price != 0) {

								Date date = new Date();
								// On crée un objet Ad
								Ad ad = new Ad(user, title, description, price,
										date);
								PersistenceManager pm = PMF.get()
										.getPersistenceManager();
								try {
									// On rend l'objet persistant (JDO)
									pm.makePersistent(ad);
									// On index l'objet pour pouvoir faire des
									// recherches (FTS, Ranges....)
									ImportAdIntoIndex.processAd(ad);
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									pm.close();
								}
								log.info("Ad '" + title + "' by user "
										+ user.getNickname());
								resp.sendRedirect("/adboard.jsp");

							} else {
								log.info("Ad id='" + i + "' has no price");
							}
						} else {
							log.info("Ad id='" + i + "' has no description");
						}
					} else {
						log.info("Ad id='" + i + "' has no title");
					}
				} catch (NumberFormatException e) {
					log.info("Ad id='" + i + "' has a wrong price format");
				}
			}
			// On redirige vers la page d'accueil
			//TODO Ajouter un callback indiquant le succès
			resp.sendRedirect("/adboard.jsp");

		} else {
			log.info("User not logged in");
			resp.sendRedirect("/adboard.jsp");
		}
	}
}