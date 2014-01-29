<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.List" %>
<%@ page import="javax.jdo.PersistenceManager" %>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ page import="org.geminux.adboard.entities.Ad" %>
<%@ page import="org.geminux.adboard.persistence.PMF" %>

<html>
<head>
<link rel="stylesheet" href="css/search.css">
<script src="/js/jquery-1.9.1.min.js"></script>
<script>

	$(function() {

		// add multiple select / deselect functionality
		$("#selectall").click(function() {
			$('.case').attr('checked', this.checked);
		});

		// if all checkbox are selected, check the selectall checkbox
		// and viceversa
		$(".case").click(function() {

			if ($(".case").length == $(".case:checked").length) {
				$("#selectall").attr("checked", "checked");
			} else {
				$("#selectall").removeAttr("checked");
			}

		});
	});
</script>
</head>
<body>
	<h2>Bienvenue sur ce service de petites annonces</h2>

	<%
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user != null) {
	%>

	<p class="menu">
		Connecté en tant que
		<%=user.getNickname()%>! | <a
			href="<%=userService.createLogoutURL(request.getRequestURI())%>">Se
			déconnecter</a> | <a href="/adboard.jsp">Retour à l'accueil</a>
	</p>

	<hr>

		<% if (request.getAttribute("criteria") != null) { %>
				<h3>Résultat de la recherche pour :<%=request.getAttribute("criteria")%></h3>
		<% }
				
			if (request.getAttribute("errMsg") != null) { %>
				<div class="error"><%=request.getAttribute("errMsg")%></div>
		<% } 
			
			if (request.getAttribute("adList") != null) {
				List<Ad> ads = (List<Ad>) request.getAttribute("adList");
				%><p>Votre recherche a retourné <%=ads.size()%> résultat(s)</p><%
				if (ads.size()!=0) {	
				%> 
					<form action="/delete" method="post">
					<div>
						<table id="resultAds" class="hovertable">
							<thead>
								<tr>
									<th><input type="checkbox" id="selectall"/></th>
									<th>Titre</th>
									<th>Description</th>
									<th>Prix</th>
									<th>Date</th>
									<th>Auteur</th>
								</tr>
							</thead>
							<tbody id="ads">

								<%
											for (Ad ad : ads) {
								%>

								<tr>
									<td align="center"><input type="checkbox" class="case" name="adId" value=<%= ad.getKey().getId() %>/></td>
									<td><%= ad.getTitle() %></td>
									<td><%= ad.getDescription() %></td>
									<td><%= ad.getPrice() %> €</td>
									<td><%= ad.getDate() %></td>
									<td><%= ad.getAuthor() %></td>
								</tr>
								<%
			                    } %>

							</tbody>
						</table>
						<input type="submit" value="Supprimer les éléments séléctionnés" />
					</div>
				</form>

				<%
				}

			}

		}
	%>
</body>
</html>