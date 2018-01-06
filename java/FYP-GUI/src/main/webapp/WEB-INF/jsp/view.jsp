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
		<jsp:param name="active" value="search" />
	</jsp:include>

	<div class="container">
		<c:choose>
			<c:when test="${paper.validPaper}">

				<c:choose>
					<c:when test="${paper.successful}">
						<!-- Don't show anything -->
					</c:when>
					<c:when test="${paper.failure}">
						<div class="panel panel-danger">
							<div class="panel-heading">
								<h3 class="panel-title">This paper failed processing.</h3>
							</div>
							<div class="panel-body">
								<div class="progress progress-striped">
									<div class="progress-bar progress-bar-danger"
										style="width: 100%"></div>
								</div>
							</div>
						</div>
					</c:when>
					<c:otherwise>
						<div class="panel panel-info">
							<div class="panel-heading">
								<h3 class="panel-title">This paper is still processing.</h3>
							</div>
							<div class="panel-body">
								<div class="progress progress-striped active">
									<div class="progress-bar progress-bar-success"
										style="width: ${paper.progress}"></div>
								</div>
							</div>
						</div>
					</c:otherwise>
				</c:choose>

				<c:if test="${not empty paper.text}">
					<div class="jumbotron">
						<h1>${paper.title}</h1>
						<h2>${paper.author}</h2>
						<p>
							<a href="/view/download?paper=${paper.id}"
								class="btn btn-primary">Download Paper</a> <a
								href="/view/extractions?paper=${paper.id}"
								class="btn btn-primary">Download Extractions</a>
						</p>
					</div>
					<div>
						<blockquote>
							<p>${paper.text}</p>
						</blockquote>
					</div>
					<c:if test="${not empty paper.kps}">
						<div>
							<table class="table table-striped table-hover">
								<thead>
									<tr>
										<th>Key Phrases</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="${paper.kps}" var="kp">
										<tr>
											<td><strong>${kp}</strong></td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>
					</c:if>
				</c:if>
			</c:when>
			<c:otherwise>
				<div class="alert alert-danger">
					<strong>Not paper found with ID ${paper.id}.</strong>
				</div>
			</c:otherwise>
		</c:choose>
	</div>
</body>

</html>
