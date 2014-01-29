<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.List"%>
<%@ page import="javax.jdo.PersistenceManager"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ page import="org.geminux.adboard.entities.Ad"%>
<%@ page import="org.geminux.adboard.persistence.PMF"%>

<html>
<head>
<link rel="stylesheet" href="css/site.css">
<script src="/js/jquery-1.9.1.min.js"></script>
<script>
	// 	Bulk add of Ads
	$(document).ready(
			function() {
				$('input#add_new').click(
						function($e) {
							$e.preventDefault();
							$('div#a').clone(true).removeAttr('id')
									.appendTo('div#addDivInner');
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

	<h3>Ajouter des annonces</h3>
	<form action="/add" method="post" id="addForm">
		<div id="addDivOuter">
			<div id="addDivInner">
				<div id="a" class="oneAd">
					<table id="addTable">
						<tr>
							<td><label for="title">Titre:</label></td>
							<td class="field"><input name=title id="title"></input></td>
						</tr>
						<tr>
							<td><label for="description">Description:</label></td>
							<td><textarea name="description"
									id="description" rows="5" cols="35"></textarea></td>
						</tr>
						<tr>
							<td><label for="price">Prix:</label></td>
							<td><input name="price"></input> €</td>
						</tr>
					</table>
				</div>
			</div>
			<input type="submit" value="Envoyer" /> <input type="button"
				id='add_new' value='Ajouter une autre annonce' />
		</div>
	</form>

	<hr>

	<h3>Chercher une annonce</h3>
	<form action="/search" method="post" id="searchForm">
		<div>
			<table>
				<tr>
					<td><label for="criteria">Mot-clé:</label></td>
					<td><input name=criteria id="criteria"></input></td>
				</tr>
				<tr></tr>
				<tr>
					<td><label for="pricemin">Prix Min:</label></td>
					<td><input name=pricemin id="pricemin"></input> <i>(utiliser le point (.) comme séparateur)</i></td>
				</tr>
				<tr></tr>
				<tr>
					<td><label for="pricemax">Prix Max:</label></td>
					<td><input name=pricemax id="pricemax"></input> <i>(utiliser le point (.) comme séparateur)</i></td>
				</tr>
				<tr></tr>
				<tr>
					<td><label for="datemin">Date Min:</label></td>
					<td><input name=datemin id="datemin"></input> <i>(format jj/mm/aaaa)</i></td>
				</tr>
				<tr></tr>
				<tr>
					<td><label for="datemax">Date Max:</label></td>
					<td><input name=datemax id="datemax"></input> <i>(format jj/mm/aaaa)</i></td>
				</tr>

			</table>
			<input type="submit" value="Chercher" />
		</div>
	</form>
	<%
		} else {
	%>
	<p class="menu">
		Vous devez vous <a
			href="<%=userService.createLoginURL(request.getRequestURI())%>">connecter</a>
		pour accéder au service.
	</p>
	<%
		}
	%>
</body>
</html>