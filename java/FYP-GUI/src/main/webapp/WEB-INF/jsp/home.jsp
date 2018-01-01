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

	<header class="masthead text-center text-white">
		<div class="masthead-content">
			<div class="container">
				<h1 class="masthead-heading mb-0">Tom Clarke's Final Year
					Project</h1>
				<h2 class="masthead-subheading mb-0">Extracting Key Phrases and
					Relations from Scientific Publications</h2>
				<h3>
					<a href="search" class="btn btn-primary btn-xl rounded-pill mt-5">Search
						the database</a>
				</h3>
				<h3>
					<a href="new" class="btn btn-primary btn-xl rounded-pill mt-5">Add
						a new paper</a>
				</h3>
				<h3>
					<a href="#" class="btn btn-primary btn-xl rounded-pill mt-5">About</a>
				</h3>
			</div>
		</div>
	</header>

	<section class="stats">
		<div class="container">
			<div class="row">
				<div class="col-md-3 col-sm-6">
					<div class="our-stats-box text-center">
						<div class="our-stat-icon">
							<span class="fa fa-briefcase"></span>
						</div>
						<div class="our-stat-info">
							<span class="stats" data-from="0" data-to="${countPaper}"
								data-speed="1000" data-refresh-interval="50">${countPaper}</span>
							<h5>Papers</h5>
						</div>
					</div>
				</div>
				<div class="col-md-3 col-sm-6">
					<div class="our-stats-box text-center">
						<div class="our-stat-icon">
							<span class="fa fa-git-square"></span>
						</div>
						<div class="our-stat-info">
							<span class="stats" data-from="0" data-to="${countKP}"
								data-speed="1000" data-refresh-interval="50">${countKP}</span>
							<h5>Key Phrases Extracted</h5>
						</div>
					</div>
				</div>
				<div class="col-md-3 col-sm-6">
					<div class="our-stats-box text-center">
						<div class="our-stat-icon">
							<span class="fa fa-file-code-o"></span>
						</div>
						<div class="our-stat-info">
							<span class="stats" data-from="0" data-to="${countHyp}"
								data-speed="1000" data-refresh-interval="50">${countHyp}</span>
							<h5>Hyponym Pairs</h5>
						</div>
					</div>
				</div>
				<div class="col-md-3 col-sm-6">
					<div class="our-stats-box text-center">
						<div class="our-stat-icon">
							<span class="fa fa-exclamation-triangle"></span>
						</div>
						<div class="our-stat-info">
							<span id="numCrashes" class="stats" data-from="0"
								data-to="${countSyn}" data-speed="1000"
								data-refresh-interval="50">${countSyn}</span>
							<h5>Synonym Sets</h5>
						</div>
					</div>
				</div>
			</div>
		</div>
	</section>

</body>

</html>