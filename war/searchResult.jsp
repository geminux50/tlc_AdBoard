<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.List" %>
<%@ page import="javax.jdo.PersistenceManager" %>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ page import="org.geminux.adboard.entities.Ad" %>
<%@ page import="org.geminux.adboard.persistence.PMF" %>

<html>
<body>
	<h2>Bienvenue sur ce service de petites annonces</h2>

	<%
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user != null) {
	%>

	<p style="font-size: 10px">
		Connecté en tant que
		<%=user.getNickname()%>! | <a
			href="<%=userService.createLogoutURL(request.getRequestURI())%>">Se
			déconnecter</a>.)
	</p>

	<hr>

	<%
	if (request.getAttribute("criteria") != null) {
	%>
		<h3>Résultat de la recherche pour : <%= request.getAttribute("criteria") %></h3>
	<% 
 	}
 	%>
	<form action="/delete" method="post">
		<div>
			<table id="resultAds">
				<thead>
					<tr>
						<th>Titre</th>
						<th>Description</th>
						<th>Prix</th>
						<th>Date</th>
						<th>Auteur</th>
					</tr>
				</thead>
				<tbody id="ads">
				<%
				
                if (request.getAttribute("adList") != null) {
                	
                    List<Ad> ads = (List<Ad>) request.getAttribute("adList");
                    for(Ad ad : ads) {
                    	%>
    					<tr>
							<td><%= ad.getTitle() %></td>
							<td><%= ad.getDescription() %></td>
							<td><%= ad.getPrice() %> €</td>
							<td><%= ad.getDate() %></td>
							<td><%= ad.getAuthor() %></td>
						</tr>
                <%
                    }
                 }
                %>
				</tbody>
			</table>
		</div>
	</form>
	<%
		}
	%>
</body>
</html>