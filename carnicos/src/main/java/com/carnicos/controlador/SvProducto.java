package com.carnicos.controlador;

import com.carnicos.model.Carne;
import com.carnicos.model.CarneDAO;
import com.carnicos.model.Categoria;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet(name = "SvProducto", urlPatterns = {"/SvProducto"})
public class SvProducto extends HttpServlet {

    // 1. Instanciamos el DAO aquí
    private CarneDAO carneDAO = new CarneDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // A. Recibir datos del formulario (vienen del fetch)
        String nombre = request.getParameter("nombre");
        String descripcion = request.getParameter("descripcion");
        String precioStr = request.getParameter("precio");
        String stockStr = request.getParameter("stock");
        String imagenUrl = request.getParameter("imagenUrl");
        String diasMinStr = request.getParameter("diasMin"); // Asegúrate de tener este input
        String diasMaxStr = request.getParameter("diasMax"); // Asegúrate de tener este input
        String sku = request.getParameter("sku");            // Asegúrate de tener este input
        String idCategoriaStr = request.getParameter("categoriaId"); // Input oculto o select

        // B. Convertir datos (Strings a números)
        // Nota: Deberías usar try-catch aquí por si mandan texto vacío, pero para el ejemplo:
        BigDecimal precio = new BigDecimal(precioStr);
        int stock = Integer.parseInt(stockStr);
        int diasMin = Integer.parseInt(diasMinStr);
        int diasMax = Integer.parseInt(diasMaxStr);
        int idCategoria = Integer.parseInt(idCategoriaStr);

        // C. Crear el objeto Carne
        Carne nuevaCarne = new Carne();
        nuevaCarne.setNombre(nombre);
        nuevaCarne.setDescripcion(descripcion);
        nuevaCarne.setPrecio(precio);
        nuevaCarne.setStockActual(stock);
        nuevaCarne.setImagenUrl(imagenUrl);
        nuevaCarne.setDiasEntregaMin(diasMin);
        nuevaCarne.setDiasEntregaMax(diasMax);
        nuevaCarne.setCodigoSku(sku);
        
        // Crear una categoría "falsa" solo con el ID para cumplir con el objeto
        Categoria cat = new Categoria();
        cat.setId(idCategoria);
        nuevaCarne.setCategoria(cat);

        // D. USAR TU DAO (Aquí es donde se usa tu código)
        boolean exito = carneDAO.insertarCarne(nuevaCarne);

        // E. Responder al Javascript
        if (exito) {
            response.setStatus(HttpServletResponse.SC_OK); // 200 OK
        } else {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Error
        }
    }
}
