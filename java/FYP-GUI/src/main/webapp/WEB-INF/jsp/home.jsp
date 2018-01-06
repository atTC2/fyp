<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<html lang="en">
<head>

<jsp:include page="header.jsp"></jsp:include>

<title>TBC452's FYP Home</title>

</head>
<body>
	<jsp:include page="navbar.jsp">
		<jsp:param name="active" value="home" />
	</jsp:include>

	<div class="container">
		<div class="jumbotron">
			<h1 class="masthead-heading mb-0">Tom Clarke's Final Year
				Project</h1>
			<h2 class="masthead-subheading mb-0">Extracting Key Phrases and
				Relations from Scientific Publications</h2>
			<p>
				For more information on what the project is based upon, see the <a
					href="https://scienceie.github.io/">ScienceIE shared task</a>.
			</p>
			<p>
				<a href="search" class="btn btn-primary">Search the database</a> <a
					href="add" class="btn btn-primary">Add a new paper</a> <a href="#"
					class="btn btn-primary">About</a>
			</p>
		</div>
	</div>

	<div class="container">
		<div class="row">
			<div class="col-lg-3 text-center">
				<div class="card">
					<div class="card-block well">
						<h2 class="card-title">${countPaper}</h2>
						<h3 class="card-subtitle mb-2 text-muted">Papers</h3>
					</div>
				</div>
			</div>
			<div class="col-lg-3 text-center">
				<div class="card">
					<div class="card-block well">
						<h2 class="card-title">${countKP}</h2>
						<h3 class="card-subtitle mb-2 text-muted">Key Phrases</h3>
					</div>
				</div>
			</div>
			<div class="col-lg-3 text-center">
				<div class="card">
					<div class="card-block well">
						<h2 class="card-title">${countHyp}</h2>
						<h3 class="card-subtitle mb-2 text-muted">Hyponyms</h3>
					</div>
				</div>
			</div>
			<div class="col-lg-3 text-center">
				<div class="card">
					<div class="card-block well">
						<h2 class="card-title">${countSyn}</h2>
						<h3 class="card-subtitle mb-2 text-muted">Synonyms</h3>
					</div>
				</div>
			</div>
		</div>
	</div>

</body>

</html>