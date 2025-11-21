
package com.carnicos.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionPostgres {
    private static final String URL = "jdbc:postgresql://localhost:5432/carnicos";
    private static final String USER = "postgres";
    private static final String PASSWORD = "ADMIN123";
    private static Connection conexion = null;

    public static Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                Class.forName("org.postgresql.Driver");
                conexion = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Conexión exitosa a PostgreSQL");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("❌ Error al conectar: " + e.getMessage());
        }
        return conexion;
    }
    
    public static void main(String[] args) {
    getConexion();
}

}
