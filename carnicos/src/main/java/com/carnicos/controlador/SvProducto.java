package com.carnicos.controlador;

import com.carnicos.model.Carne;
import com.carnicos.model.CarneDAO;
import com.carnicos.model.Categoria;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet(name = "SvProducto", urlPatterns = {"/SvProducto"})
public class SvProducto extends HttpServlet {

    // 1. Instanciamos el DAO aquí
    private CarneDAO carneDAO = new CarneDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        CarneDAO carneDAO = new CarneDAO(); // Asumiendo que esta línea o la inicialización está arriba.
    
        // 1. BÚSQUEDA POR ID (obtenerUnoYenviarJson debe hacer todo el trabajo)
        String idCarneStr = request.getParameter("id");
        if (idCarneStr != null && !idCarneStr.trim().isEmpty()) {
            // Asumiendo que este método existe y maneja la respuesta JSON/Error.
            obtenerUnoYenviarJson(request, response);
            return; // Termina la ejecución aquí
        }
        if (request.getParameter("id") != null && !request.getParameter("id").trim().isEmpty()) {
            obtenerUnoYenviarJson(request, response);
            return; // Termina la ejecución aquí
        }

        // 2. BÚSQUEDA FILTRADA POR NOMBRE/SKU (Solo una vez)
        String filtro = request.getParameter("buscar");

        if (filtro != null && !filtro.trim().isEmpty()) {
            try {
                // Buscamos con el filtro
                List<Carne> listaFiltrada = carneDAO.buscarPorNombre(filtro);

                // Convierte la lista a JSON (usando el método o la lógica que tengas, como Gson)
                // IMPORTANTE: Asegúrate de tener la librería Gson importada.
                Gson gson = new GsonBuilder().setPrettyPrinting().create(); // Inicialización de Gson
                String jsonArray = gson.toJson(listaFiltrada);

                // Envía la respuesta JSON
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(jsonArray);

                return; // Termina la ejecución aquí después de enviar JSON

            } catch (Exception e) {
                System.err.println("❌ Error en SvProducto al buscar: " + e.getMessage());
                // Si hay un error de DAO/DB, se devuelve el error 500 al cliente.
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al procesar la búsqueda.");
                return;
            }
        }

        // 3. LISTADO COMPLETO (Si no hay parámetros 'id' ni 'buscar')
        // El Servlet carga la lista completa y luego redirige al JSP.

        List<Carne> listaCarnes = carneDAO.obtenerCarnes(); // o el método que uses para todo.
        request.setAttribute("listaCarnes", listaCarnes);

        // Redireccionamos a la vista principal (JSP)
        RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
        dispatcher.forward(request, response);
        }
    
    // Obtener Uno
    private void obtenerUnoYenviarJson(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String idCarneStr = request.getParameter("id");
        if (idCarneStr != null && !idCarneStr.trim().isEmpty()) {
            try {
                System.out.println("Carnes");
                int idCarne = Integer.parseInt(idCarneStr);
                Carne carne = carneDAO.obtenerCarnePorId(idCarne);

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                if (carne != null) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String jsonResponse = gson.toJson(carne);
                    response.getWriter().write(jsonResponse);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Producto no encontrado.");
                }
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de producto inválido.");
            }
        } else {
            // 5. ID nulo o vacío (Error 400 - Opcional, dependiendo de la lógica de doGet)
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parámetro 'id' requerido.");
        }
    }
    
    // MÉTODOS AUXILIARES PARA CREAR JSON
    private String convertirCarneAJson(Carne carne) {
        // Asegúrate de escapar cualquier comilla doble si tienes descripciones complejas.
        // Aquí usamos una implementación simple, asumiendo que los campos están "limpios".
        String categoriaJson = (carne.getCategoria() != null) 
                                ? "{\"id\":" + carne.getCategoria().getId() + ",\"nombre\":\"" + carne.getCategoria().getNombre() + "\"}"
                                : "null";
                                
        return "{"  
            + "\"idCarne\":" + carne.getId() + ","
            + "\"nombre\":\"" + carne.getNombre() + "\","
            + "\"descripcion\":\"" + (carne.getDescripcion() != null ? carne.getDescripcion().replace("\n", "\\n") : "") + "\","
            + "\"precio\":" + carne.getPrecio() + ","
            + "\"stockActual\":" + carne.getStockActual() + ","
            + "\"diasEntregaMin\":" + carne.getDiasEntregaMin() + ","
            + "\"diasEntregaMax\":" + carne.getDiasEntregaMax() + ","
            + "\"codigoSku\":\"" + carne.getCodigoSku() + "\","
            + "\"imagenUrl\":\"" + (carne.getImagenUrl() != null ? carne.getImagenUrl() : "") + "\","
            + "\"categoria\":" + categoriaJson
            + "}";
    }
    
    /** Convierte una lista de objetos Carne a un Array JSON (para la búsqueda del modal) */
    private String convertirListaAJson(List<Carne> lista) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            sb.append(convertirCarneAJson(lista.get(i)));
            if (i < lista.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

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
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Lógica para ACTUALIZAR (EDITAR) un producto
        try {
            // Recibimos todos los datos, incluyendo el ID
            String idCarneStr = request.getParameter("idCarne"); // Viene del input hidden
            
            if (idCarneStr == null || idCarneStr.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de producto es requerido para la edición (PUT).");
                return;
            }
            
            // Reutilizamos la lógica del doPost para la recolección de datos
            int idCarne = Integer.parseInt(idCarneStr);
            String nombre = request.getParameter("nombre");
            String descripcion = request.getParameter("descripcion");
            String precioStr = request.getParameter("precio");
            String stockStr = request.getParameter("stock");
            String imagenUrl = request.getParameter("imagenUrl");
            String diasMinStr = request.getParameter("diasMin");
            String diasMaxStr = request.getParameter("diasMax");
            String sku = request.getParameter("sku");
            String idCategoriaStr = request.getParameter("categoriaId");

            // Conversión de datos
            BigDecimal precio = new BigDecimal(precioStr);
            int stock = Integer.parseInt(stockStr);
            int diasMin = Integer.parseInt(diasMinStr);
            int diasMax = Integer.parseInt(diasMaxStr);
            int idCategoria = Integer.parseInt(idCategoriaStr);

            // Crear el objeto Carne (usando el ID)
            Carne carneAEditar = new Carne();
            carneAEditar.setId(idCarne);
            carneAEditar.setNombre(nombre);
            carneAEditar.setDescripcion(descripcion);
            carneAEditar.setPrecio(precio);
            carneAEditar.setStockActual(stock);
            carneAEditar.setImagenUrl(imagenUrl);
            carneAEditar.setDiasEntregaMin(diasMin);
            carneAEditar.setDiasEntregaMax(diasMax);
            carneAEditar.setCodigoSku(sku);

            Categoria cat = new Categoria();
            cat.setId(idCategoria);
            carneAEditar.setCategoria(cat);

            // D. USAR TU DAO para ACTUALIZAR
            boolean exito = carneDAO.actualizarCarne(carneAEditar); // Asumiendo que tienes este método en tu DAO

            // E. Responder al Javascript
            if (exito) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Producto actualizado correctamente.");
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Fallo al actualizar el producto.");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error de formato en un campo numérico: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error en doPut: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error interno del servidor durante la actualización.");
        }
    }
}
