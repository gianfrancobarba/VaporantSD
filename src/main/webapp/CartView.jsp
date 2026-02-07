<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

	<% Cart cart=(Cart) request.getSession().getAttribute("cart"); request.getSession().setAttribute("cart", cart);
		UserBean user=(UserBean) session.getAttribute("user"); if(user !=null) {
		request.getSession().setAttribute("user", user); String email=user.getEmail(); } %>

		<!DOCTYPE html>
		<html lang="it">
		<%@ page contentType="text/html; charset=UTF-8"
			import="java.util.*,com.vaporant.model.Cart,com.vaporant.model.ProductBean,com.vaporant.model.UserBean" %>

			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
				<link href="css/CartStyle.css" rel="stylesheet" type="text/css">
				<link href="https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css"
					rel="stylesheet" type="text/css">
				<title>Vaporant Cart</title>
			</head>

			<body>
				<jsp:include page="Header.jsp" />
				<div class="wrapper">
					<h1>CARRELLO</h1>
					<div class="project">
						<div class="shop">
							<% if(cart !=null) { %>
								<% List<ProductBean> prodcart = cart.getProducts();
									for(ProductBean beancart : prodcart) {
									%>

									<div class="box">
										<img src="media/img<%=beancart.getCode()%>.jpg" alt="?">
										<div class="content">
											<h3>
												<%=beancart.getName()%>
											</h3>
											<h4>Prezzo: <%=beancart.getPrice()%>€</h4>
											<form action="cart?action=aggiorna&id=<%=beancart.getCode()%>" method="POST"
												class="none">
												Quantità: <input type="number" name="quantita"
													value="<%=beancart.getQuantity()%>" required min="1"
													max="<%=beancart.getQuantityStorage() %>">
												<button type="submit" class="btn">Aggiorna</button>
											</form>
											<p class="btn-area">
												<a href="cart?action=deleteC&id=<%=beancart.getCode()%>">
													<i aria-hidden="true" class="fa fa-trash"></i> <span
														class="btn2">Rimuovi</span>
												</a>
											</p>
										</div>
									</div>
									<% } } %>
						</div>
						<div class="right-bar">
							<p><span>Spedizione</span> <span>Gratuita</span></p>
							<hr>
							<p><span>Totale</span>
								<% if(cart !=null){ %>
									<span>
										<%=cart.getPrezzoTotale()%> €
									</span>
									<% } else {%> <span>0.00 €</span>
										<%} if(cart !=null) {%>
											<form action="cart?action=checkout" method="POST">
												<button type="submit"><i class="fa fa-shopping-cart"></i><span>Ordina e
														paga</span></button>
											</form>
											<% } %>
						</div>
					</div>
				</div>
				<jsp:include page="Footer.jsp" />
			</body>

		</html>