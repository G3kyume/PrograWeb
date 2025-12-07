package com.carnicos.controlador;

import com.carnicos.model.Carne;
import com.carnicos.model.CarneDAO;
import com.carnicos.model.Categoria;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
import java.util.stream.Collectors;

@WebServlet(name = "SvProducto", urlPatterns = {"/SvProducto"})
public class SvProducto extends HttpServlet {

    // 1. Instanciamos el DAO aquí
    private CarneDAO carneDAO = new CarneDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        CarneDAO carneDAO = new CarneDAO(); // Asumiendo que esta línea o la inicialización está arriba.
    
        // 1. BÚSQUEDA POR ID (obtenerUnoYenviarJson debe hacer todo el trabajo)
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
            + "\"id\":" + carne.getId() + ","
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

        try {
            // 1. Leer el JSON enviado por fetch
            String cuerpoJson = request.getReader().lines().collect(Collectors.joining());
            System.out.println("POST JSON recibido: " + cuerpoJson);

            // 2. Convertir JSON -> objeto JSON
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(cuerpoJson, JsonObject.class);

            // 3. Extraer campos del JSON
            String nombre = json.get("nombre").getAsString();
            String descripcion = json.get("descripcion").getAsString();
            BigDecimal precio = new BigDecimal(json.get("precio").getAsString());
            int stock = json.get("stock").getAsInt();
            String imagenUrl = json.get("imagenUrl").getAsString();
            int diasMin = json.get("diasMin").getAsInt();
            int diasMax = json.get("diasMax").getAsInt();
            String sku = json.get("sku").getAsString();
            int idCategoria = json.get("categoriaId").getAsInt();

            // 4. Crear objeto Carne
            Carne nuevaCarne = new Carne();
            nuevaCarne.setNombre(nombre);
            nuevaCarne.setDescripcion(descripcion);
            nuevaCarne.setPrecio(precio);
            nuevaCarne.setStockActual(stock);
            nuevaCarne.setImagenUrl(imagenUrl);
            nuevaCarne.setDiasEntregaMin(diasMin);
            nuevaCarne.setDiasEntregaMax(diasMax);
            nuevaCarne.setCodigoSku(sku);

            Categoria cat = new Categoria();
            cat.setId(idCategoria);
            nuevaCarne.setCategoria(cat);

            // 5. Guardar en BD
            boolean exito = carneDAO.insertarCarne(nuevaCarne);

            if (exito) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al insertar producto");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error procesando JSON en POST.");
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        try {
            // 1. Leer JSON
            String cuerpoJson = request.getReader().lines().collect(Collectors.joining());
            System.out.println("PUT JSON recibido: " + cuerpoJson);

            // 2. Interpretar JSON
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(cuerpoJson, JsonObject.class);

            // 3. Validar ID
            if (!json.has("id")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "El ID es obligatorio para PUT.");
                return;
            }

            int id = json.get("id").getAsInt();

            // 4. Extraer datos
            String nombre = json.get("nombre").getAsString();
            String descripcion = json.get("descripcion").getAsString();
            BigDecimal precio = new BigDecimal(json.get("precio").getAsString());
            int stock = json.get("stock").getAsInt();
            String imagenUrl = json.get("imagenUrl").getAsString();
            int diasMin = json.get("diasMin").getAsInt();
            int diasMax = json.get("diasMax").getAsInt();
            String sku = json.get("sku").getAsString();
            int idCategoria = json.get("categoriaId").getAsInt();

            // 5. Crear objeto actualizado
            Carne carneAEditar = new Carne();
            carneAEditar.setId(id);
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

            // 6. Actualizar BD
            boolean exito = carneDAO.actualizarCarne(carneAEditar);

            if (exito) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Producto actualizado correctamente.");
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No se pudo actualizar.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error procesando JSON en PUT.");
        }
    }
}
