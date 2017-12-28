<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en">
<head>

<link rel="stylesheet" type="text/css"
	href="webjars/bootstrap/3.3.7/css/bootstrap.min.css" />

<spring:url value="/css/main.css" var="springCss" />
<link href="${springCss}" rel="stylesheet" />
<c:url value="/css/main.css" var="jstlCss" />
<link href="${jstlCss}" rel="stylesheet" />

<script type="text/javascript"
	src="webjars/bootstrap/3.3.7/js/bootstrap.min.js"></script>

<title>${status}</title>
</head>
<body>
	<div class="container">
		<div class="row">
			<div class="col-md-12">
				<div class="error-template">
					<h1>Oops!</h1>
					<h2>${status}&nbsp;-&nbsp;${error}</h2>
					<h2>${message}</h2>
					<c:if test="${not empty trace}">
						<pre>
							<code>${trace}</code>
						</pre>
					</c:if>
				</div>
			</div>
		</div>
	</div>
</body>

</html>