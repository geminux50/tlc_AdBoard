<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Makes "field" required and a decimal number only.</title>
<link href="/css/site.css" rel="stylesheet">
</head>
<body>
	<form id="myform" novalidate="novalidate">
		<label for="pricemin">Required, decimal number: </label> <input
			id="pricemin" class="left" name="pricemin"><br>
		<input type="submit" value="Validate!">
	</form>
	<script src="/js/jquery-1.9.1.min.js"></script>
	<script src="/js/jquery.validate.js"></script>
	<script src="/js/additional-methods.js"></script>
	<script>
		window.onload = function() {
			// just for the demos, avoids form submit
			jQuery.validator.setDefaults({
				debug : true,
				success : "valid"
			});
			$("#myform").validate({
				rules : {
					pricemin : {
						required : true,
						number : true
					}
				}
			});
		};
	</script>
</body>
</html>