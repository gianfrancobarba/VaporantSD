<%
    ProductBean product = (ProductBean) request.getSession().getAttribute("product");
%>

<!DOCTYPE html>
<html lang = "it">
<%@ page contentType="text/html; charset=UTF-8" import="java.util.*,com.vaporant.model.ProductBean"%>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link href="css/DetailsStyle.css" rel="stylesheet" type="text/css">
	<title>Pagina del prodotto</title>
</head>
<body>
	<jsp:include page="Header.jsp" />
	
	<main class="product-container">
      <div class="left-column">
        <img src="media/img<%=product.getCode()%>.jpg" alt="">
      </div>

      <div class="right-column">

        <div class="product-description">
          <h1><%=product.getName()%></h1>
          <p><%=product.getDescription()%></p>
        </div>

        <div class="product-price">
          <span><%=product.getPrice()%>€</span>
          <% 
          if(product.getQuantityStorage() == 0) { %>
              <a href="#" class="cart-btn">Prodotto terminato!</a>
          <% } else { %>
              <a href="cart?action=addC&id=<%=product.getCode()%>&user=${user}" class="cart-btn">Aggiungi al carrello</a>
          <%  } %>
        </div>
      </div>
    </main>
    
	<jsp:include page="Footer.jsp" />
</body>
</html>