\u003c%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %\u003e
\u003c!DOCTYPE html\u003e
\u003chtml lang="it"\u003e

\u003chead\u003e
\u003cmeta charset="UTF-8"\u003e
\u003ctitle\u003eErrore - Vaporant\u003c/title\u003e
\u003cstyle\u003e
body {
font-family: sans-serif;
text-align: center;
padding: 50px;
background-color: #f4f4f4;
}

.error-container {
background: white;
padding: 40px;
border-radius: 10px;
box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
display: inline-block;
}

h1 {
color: #d9534f;
font-size: 48px;
margin-bottom: 10px;
}

p {
font-size: 18px;
color: #666;
}

.btn {
display: inline-block;
margin-top: 20px;
padding: 10px 20px;
background-color: #007bff;
color: white;
text-decoration: none;
border-radius: 5px;
}

.btn:hover {
background-color: #0056b3;
}
\u003c/style\u003e
\u003c/head\u003e

\u003cbody\u003e
\u003cdiv class="error-container"\u003e
\u003ch1\u003eOops!\u003c/h1\u003e
\u003ch2\u003eErrore \u003c%= request.getAttribute("errorCode") !=null ? request.getAttribute("errorCode") :
"Sconosciuto" %\u003e
\u003c/h2\u003e
\u003cp\u003e
\u003c%= request.getAttribute("errorMessage") !=null ? request.getAttribute("errorMessage") : "Qualcosa è andato
storto." %\u003e
\u003c/p\u003e
\u003ca href="ProductView.jsp" class="btn"\u003eTorna alla Home\u003c/a\u003e
\u003c/div\u003e
\u003c/body\u003e

\u003c/html\u003e