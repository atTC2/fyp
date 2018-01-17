<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<html lang="en">
<head>

<jsp:include page="header.jsp"></jsp:include>
<script type="text/javascript" src="/js/d3.min.js"></script>
<script type="text/javascript" src="/js/d3pie.min.js"></script>

<title>ExtractorIE</title>

</head>
<body>
	<jsp:include page="navbar.jsp">
		<jsp:param name="active" value="home" />
	</jsp:include>

	<div class="container">
		<div class="jumbotron">
			<h1 class="masthead-heading mb-0">ExtractorIE</h1>
			<h2 class="masthead-subheading mb-0">Extracting Key Phrases and
				Relations from Scientific Publications</h2>
			<p>
				For more information on what the project is based upon, see the <a
					href="https://scienceie.github.io/">ScienceIE shared task</a>.
			</p>
			<p>
				<a href="search" class="btn btn-primary">Search the database</a> <a
					href="add" class="btn btn-primary">Add a new paper</a>
			</p>
		</div>
	</div>

	<div class="container">
		<div id="kpPieChart"></div>
		<input type="hidden" id="countTask" value="${countTask}"> <input
			type="hidden" id="countProcess" value="${countProcess}"> <input
			type="hidden" id="countMaterial" value="${countMaterial}">
		<script>
			// Some code to make it look better on desktop, but still usable on mobile
			var big = {
				"outer" : {
					"format" : "label-percentage1",
					"pieDistance" : 20
				},
				"inner" : {
					"format" : "none"
				}
			};
			var small = {
				"outer" : {
					"format" : "none"
				},
				"inner" : {
					"format" : "label-percentage2"
				}
			};

			var outerToUse = $(window).width() >= 1000 ? big.outer
					: small.outer;
			var innerToUse = $(window).width() >= 1000 ? big.inner
					: small.inner;

			var pie = new d3pie("kpPieChart", {
				"header" : {
					"title" : {
						"text" : "${countKP}",
						"color" : "#c8c8c8",
						"fontSize" : 34
					},
					"subtitle" : {
						"text" : "KPs in ${countPaper} Papers",
						"color" : "#c8c8c8",
						"fontSize" : 15
					},
					"location" : "pie-center",
					"titleSubtitlePadding" : 10
				},
				"size" : {
					"canvasWidth" : $('.container').width(),
					"pieInnerRadius" : "80%",
					"pieOuterRadius" : "90%"
				},
				"data" : {
					"content" : [ {
						"label" : "Tasks",
						"value" : parseInt($('#countTask').val()),
						"color" : "#5bc0de"
					}, {
						"label" : "Processes",
						"value" : parseInt($('#countProcess').val()),
						"color" : "#f89406"
					}, {
						"label" : "Materials",
						"value" : parseInt($('#countMaterial').val()),
						"color" : "#62c462"
					} ]
				},
				"labels" : {
					"outer" : outerToUse,
					"inner" : innerToUse,
					"mainLabel" : {
						"color" : "#c8c8c8",
						"fontSize" : 20
					},
					"percentage" : {
						"color" : "#c8c8c8",
						"fontSize" : 20,
						"decimalPlaces" : 0
					},
					"value" : {
						"fontSize" : 20
					},
					"lines" : {
						"enabled" : true
					},
					"truncation" : {
						"enabled" : true
					}
				},
				"effects" : {
					"pullOutSegmentOnClick" : {
						"effect" : "none"
					}
				},
				"misc" : {
					"colors" : {
						"segmentStroke" : "#000000"
					}
				}
			});
		</script>
	</div>

	<div class="container">
		<div class="row">
			<div class="col-lg-6 text-center">
				<div class="card">
					<div class="card-block well">
						<h2 class="card-title">${countHyp}</h2>
						<h3 class="card-subtitle mb-2 text-muted">Hyponyms</h3>
					</div>
				</div>
			</div>
			<div class="col-lg-6 text-center">
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