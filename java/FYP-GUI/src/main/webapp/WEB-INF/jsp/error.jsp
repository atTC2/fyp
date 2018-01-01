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
	<div class="container">
		<div class="row">
			<div class="col-md-12">
				<div class="error-template">
					<h1>Oops!</h1>
					<h2>${status}&nbsp;-&nbsp;${error}</h2>
					<c:if test="${debug}">
						<h2>${message}</h2>
						<c:if test="${not empty trace}">
							<pre>
							<code>${fn:trim(trace)}</code>
						</pre>
						</c:if>
					</c:if>
				</div>
			</div>
		</div>
	</div>
</body>

</html>