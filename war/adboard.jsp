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
<script src="/js/jquery.validate.js"></script>
<script src="/js/additional-methods.js"></script>
<script>
	$(document).ready(

			function() {
				$('input#add_new').click(
						function($e) {
							$e.preventDefault();
							$('div#a').clone(true).removeAttr('id')
									.appendTo('div#addDivInner');
						});
			});

// 	window.onload = function() {

// 		jQuery.validator.setDefaults({
// 			debug : false,
// 			success : "valid"
// 		});

// 		$("#addForm").validate({
// 			rules : {
// 				price : {
// 					required : true,
// 					number : true
// 				}
// 			}
// 		});

// 		$("#searchForm").validate({
// 			rules : {
// 				pricemin : {
// 					required : false,
// 					number : true
// 				},
// 				pricemax : {
// 					required : false,
// 					number : true
// 				},
// 			}
// 		});
// 	};
</script>


</head>
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

	<h3>Ajouter des annonces</h3>
	<form action="/add" method="post" id="addForm">
		<div id="addDivOuter">
			<div id="addDivInner">
				<div id="a" class="oneAd">
					<table id="addTable">
						<tr>
							<td><label for="title">Titre:</label></td>
							<td class="field"><input class="left" name=title id="title"></input></td>
						</tr>
						<tr>
							<td><label for="description">Description:</label></td>
							<td><textarea class="left" name="description"
									id="description" rows="5" cols="35"></textarea></td>
						</tr>
						<tr>
							<td><label for="price">Prix:</label></td>
							<td><input class="left price" name="price"></input> €</td>
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
					<td><input class="left" name=criteria id="criteria"></input></td>
				</tr>
				<tr></tr>
				<tr>
					<td><label for="pricemin">Prix Min:</label></td>
					<td><input class="left" name=pricemin id="pricemin"></input> <i>(utiliser le point (.) comme séparateur)</i></td>
				</tr>
				<tr></tr>
				<tr>
					<td><label for="pricemax">Prix Max:</label></td>
					<td><input class="left" name=pricemax id="pricemax"></input> <i>(utiliser le point (.) comme séparateur)</i></td>
				</tr>
				<tr></tr>
				<tr>
					<td><label for="datemin">Date Min:</label></td>
					<td><input class="left" name=datemin id="datemin"></input> <i>(format jj/mm/aaaa)</i></td>
				</tr>
				<tr></tr>
				<tr>
					<td><label for="datemax">Date Max:</label></td>
					<td><input class="left" name=datemax id="datemax"></input> <i>(format jj/mm/aaaa)</i></td>
				</tr>

			</table>
			<input type="submit" value="Chercher" />
		</div>
	</form>


	<%
		} else {
	%>
	<p style="font-size: 10px">
		Vous devez vous <a
			href="<%=userService.createLoginURL(request.getRequestURI())%>">connecter</a>
		pour accéder au service.
	</p>
	<%
		}
	%>

	<hr>

	<%
		PersistenceManager pm = PMF.get().getPersistenceManager();
		String query = "select from " + Ad.class.getName()
				+ " order by date desc";
		List<Ad> ads = (List<Ad>) pm.newQuery(query).execute();
		if (ads.isEmpty()) {
	%>
	<p>The adboard has no ad.</p>
	<%
		} else {
			for (Ad g : ads) {
				if (g.getAuthor() != null) {
	%>
	<p>
		<b><%=g.getAuthor().getNickname()%></b> a posté l'annonce suivante :
	</p>
	<table>
		<tr>
			<td><label>Titre</label></td>
			<td><blockquote><%=g.getTitle()%></blockquote></td>
		</tr>
		<tr>
			<td><label>Description</label></td>
			<td><blockquote><%=g.getDescription()%></blockquote></td>
		</tr>
		<tr>
			<td><label>Prix</label></td>
			<td><blockquote><%=g.getPrice()%>
					€
				</blockquote></td>
		</tr>
	</table>
	<p style="font-size: 10px; font-style: italic">
		Posté le
		<%=g.getDate()%></p>
	<hr align="left" width="100px">
	<%
		}
			}
		}
		pm.close();
	%>

</body>
</html>