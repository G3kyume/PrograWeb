package com.carnicos.controlador;

import com.carnicos.conexion.ConexionPostgres;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

// --- AQUÍ ESTÁ EL CAMBIO ---
// Asegúrate de que TODAS estas líneas digan 'jakarta', NO 'javax'
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "EliminarProductoServlet", urlPatterns = {"/eliminarProducto"})
public class EliminarProductoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // ... (el resto del código sigue exactamente igual) ...
        response.setContentType("application/json;charset=UTF-8");
        
        String idStr = request.getParameter("id");
        
        try (PrintWriter out = response.getWriter()) {
            if (idStr == null || idStr.isEmpty()) {
                out.print("{\"status\":\"error\", \"message\":\"No se envió el ID\"}");
                return;
            }

            int idProducto = Integer.parseInt(idStr);

            try (Connection conn = ConexionPostgres.getConexion()) {
                String sql = "DELETE FROM productos_carnicos WHERE id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, idProducto);
                
                int filasAfectadas = ps.executeUpdate();
                
                if (filasAfectadas > 0) {
                    out.print("{\"status\":\"ok\", \"message\":\"Producto eliminado\"}");
                } else {
                    out.print("{\"status\":\"error\", \"message\":\"No se encontró el producto\"}");
                }

            } catch (Exception e) {
                e.printStackTrace();
                out.print("{\"status\":\"error\", \"message\":\"" + e.getMessage() + "\"}");
            }
        }
    }
}