<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<html lang="en">
<head>

<jsp:include page="header.jsp"></jsp:include>

<title>${paper.title}</title>

</head>
<body>
	<jsp:include page="navbar.jsp">
		<jsp:param name="active" value="blank" />
	</jsp:include>

	<div class="container">
		<div class="page-header">
			<h1>Inspirational Quotes</h1>
		</div>
		<blockquote>
			<p>It's easy, right?</p>
			<small><cite title="Mark Lee">Mark Lee, 2014</cite></small>
		</blockquote>
	</div>
</body>

</html>
