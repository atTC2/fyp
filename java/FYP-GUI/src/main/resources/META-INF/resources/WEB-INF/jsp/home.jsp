<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en">
<head>

<link rel="stylesheet" type="text/css"
	href="webjars/bootstrap/3.3.7/css/bootstrap.min.css" />

<spring:url value="/css/main.css" var="springCss" />
<link href="${springCss}" rel="stylesheet" />
<spring:url value="/css/main.css" var="jstlCss" />
<link href="${jstlCss}" rel="stylesheet" />

<script type="text/javascript"
	src="webjars/bootstrap/3.3.7/js/bootstrap.min.js"></script>

<title>TBC452's FYP Home</title>

</head>
<body>

	<div class="container">

		<div class="starter-template">
			<h1>FYP Home Page</h1>
			<h2>${message}</h2>
		</div>

	</div>

</body>

</html>