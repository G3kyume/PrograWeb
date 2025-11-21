package com.carnicos.controlador;

import com.carnicos.model.Carne;
import com.carnicos.model.CarneDAO; // Usamos tu DAO
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

// IMPORTANTE: Asegúrate que esta sea la única clase con urlPatterns = {"/listarCarnes"}
@WebServlet(name = "ProductoServlet", urlPatterns = {"/listarCarnes"})
public class ProductoServlet extends HttpServlet {

    // Ya no usamos @Inject ProductoService, usamos el DAO directo
    private CarneDAO dao = new CarneDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            // 1. Verificar si hay filtro (parámetro "clasificacion" en la URL)
            String filtroClasificacion = request.getParameter("clasificacion");
            List<Carne> lista;

            // 2. Pedir datos al DAO según corresponda
            if (filtroClasificacion != null && !filtroClasificacion.isEmpty() && !filtroClasificacion.equals("Todos")) {
                // Si llega ?clasificacion=Res, filtramos
                lista = dao.obtenerCarnesPorClasificacion(filtroClasificacion);
            } else {
                // Si no, traemos todo
                lista = dao.obtenerCarnes();
            }
            
            // 3. Convertir a JSON
            Jsonb jsonb = JsonbBuilder.create();
            String jsonResultado = jsonb.toJson(lista);
            
            // 4. Enviar
            out.print(jsonResultado);
            
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error en el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}