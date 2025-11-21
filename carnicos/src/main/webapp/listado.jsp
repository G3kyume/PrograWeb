<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, com.carnicos.model.Carne"%>
<!DOCTYPE html>
<html>
<head>
    <title>Listado de Carnes</title>
    <link rel="stylesheet" href="indexEstilos.css">
</head>
<body>
    <div id="listado" class="lista">
        <%
            // Esto es correcto, ya que el Servlet usa request.setAttribute("carnes", lista)
            List<Carne> carnes = (List<Carne>) request.getAttribute("carnes"); 
            
            if (carnes != null && !carnes.isEmpty()) { 
                for (Carne c : carnes) {
        %>
            <div class="carne">
                <img src="<%= c.getImagen_url() %>" alt="<%= c.getNombre() %>" width="150">
                <h3><%= c.getNombre() %></h3>
                <p><%= c.getDescripcion() %></p>
                <p><b>Precio:</b> $<%= c.getPrecio() %></p>
                <p><b>Clasificación:</b> <%= c.getClasificacion() %></p>
            </div>
        <%
                }
            } else {
        %>
            <p>No hay productos disponibles para mostrar.</p>
        <%
            }
        %>
    </div>
</body>
</html>