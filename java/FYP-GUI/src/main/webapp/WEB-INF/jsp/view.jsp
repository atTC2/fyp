<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<html lang="en">
<head>

<jsp:include page="header.jsp"></jsp:include>

<title>${title}</title>

</head>
<body>
	<c:choose>
		<c:when test="${validPaper}">
			<h1>Paper found!</h1>
			<h2>${paper.title}</h2>
			<h3>${paper.author}</h3>
			<pre>
				<code>${paper.text}</code>
			</pre>
			<div>
				<a href="download?paper=${id}"
					class="btn btn-primary btn-xl rounded-pill mt-5">Download Paper</a>
				<a href="extractions?paper=${id}"
					class="btn btn-primary btn-xl rounded-pill mt-5">Download
					Extractions</a>
			</div>
		</c:when>
		<c:otherwise>
			<div class="container">Not paper found with ID ${id}.</div>
		</c:otherwise>
	</c:choose>
</body>

</html>
