<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<html lang="en">
<head>

<jsp:include page="header.jsp"></jsp:include>

<title>${status}</title>

</head>
<body>
	<jsp:include page="navbar.jsp">
		<jsp:param name="active" value="error" />
	</jsp:include>
	
	<div class="container">
		<h1 class="text-danger">Oops!</h1>
		<h2 class="text-danger">${status}&nbsp;-&nbsp;${error}</h2>
		<c:if test="${debug}">
			<h2 class="text-danger">${message}</h2>
			<c:if test="${not empty trace}">
				<pre>
					<code>${fn:trim(trace)}</code>
				</pre>
			</c:if>
		</c:if>
	</div>
</body>

</html>