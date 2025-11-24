 <%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="java.util.*,com.vaporant.model.ProductBean,com.vaporant.model.Cart, com.vaporant.model.UserBean"%>

<%
	request.getSession().setAttribute("tipo", "guest");

	Collection<?> products = (Collection<?>) session.getAttribute("products");
	if(products == null) {
		response.sendRedirect("./product");	
		return;
	}
	ProductBean product = (ProductBean) session.getAttribute("product");
	UserBean user = (UserBean) request.getSession().getAttribute("user");

%>

<!DOCTYPE html>
<html lang = "it">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0"> 
	<link href="css/ProductStyle.css" rel = "stylesheet" type = "text/css">
	<script src = "https://cdnjs.cloudflare.com/ajax/libs/splidejs/4.1.4/js/splide.min.js" integrity="sha512-4TcjHXQMLM7Y6eqfiasrsnRCc8D/unDeY1UGKGgfwyLUCTsHYMxF7/UHayjItKQKIoP6TTQ6AMamb9w2GMAvNg==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
	<link rel="stylesheet" href = "https://cdnjs.cloudflare.com/ajax/libs/splidejs/4.1.4/css/splide.min.css" integrity="sha512-KhFXpe+VJEu5HYbJyKQs9VvwGB+jQepqb4ZnlhUF/jQGxYJcjdxOTf6cr445hOc791FFLs18DKVpfrQnONOB1g==" crossorigin="anonymous" referrerpolicy="no-referrer" />
	<title>Vaporant</title>

</head>

<body>
	<jsp:include page="Header.jsp" />
		<div id="heroSection">
  			<div class="heroVideoContainer">
    			<video src="media/herovideo.mp4" autoplay loop playsinline muted></video>
  			</div>
  			<div class="heroText">
    			<h1>IL PIACERE DELLO SVAPO</h1>
  			</div>
		</div>
		<h1>SCOPRI I NOSTRI PRODOTTI</h1>
		<div class = "slider">
			<section id="image-carousel" class="splide" aria-label="Beautiful Images">
  				<div class="splide__track">
					<ul class="splide__list">
		<%
			if (products != null && products.size() != 0) {
				Iterator<?> it = products.iterator();
				while (it.hasNext()) {
					ProductBean bean = (ProductBean) it.next();
		%>

					<li class="splide__slide">
						<a href = "details?action=read&id=<%=bean.getCode()%>">
						<img src="media/img<%=bean.getCode()%>.jpg" alt="">
						</a>
						<div class = "name">
							<%=bean.getName()%>
						</div>
					</li>
		<% } 
			} else {
		%>
		<h1>Non ci sono prodotti disponibili!</h1>
		<% } %>
					</ul>
  				</div>
			</section>
		</div>
		<br>
		<script>
		document.addEventListener( 'DOMContentLoaded', function () {
			  new Splide( '#image-carousel', {
					perPage    : 4,
					type: "loop",
					breakpoints: {
						640: {
							perPage: 1,
						},
					},
			  } ).mount();
			} );
		</script>
	<jsp:include page="Footer.jsp" />
</body>
</html>