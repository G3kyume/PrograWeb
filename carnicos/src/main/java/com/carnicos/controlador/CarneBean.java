package com.carnicos.controlador;

import com.carnicos.model.Carne;
import com.carnicos.model.CarneDAO;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.util.List;

@Named("carneBean")
@RequestScoped
public class CarneBean {

    private List<Carne> listaCarnes;
    private CarneDAO carneDAO = new CarneDAO(); // O inyectado
    
    @PostConstruct
    public void init() {
        CarneDAO dao = new CarneDAO();
        listaCarnes = dao.obtenerCarnes();
    }

    public List<Carne> getListaCarnes() {
        return listaCarnes;
    }

    // Este constructor o un método @PostConstruct carga la lista inicial
    public CarneBean() {
        this.listaCarnes = carneDAO.obtenerCarnes(); // Carga inicial
    }
    
    
    public void filtrarPorClasificacion(String clasificacion) {
        if ("Todos".equalsIgnoreCase(clasificacion)) {
            this.listaCarnes = carneDAO.obtenerCarnes();
        } else {
            // Llama a tu método DAO que ya implementamos
            this.listaCarnes = carneDAO.obtenerCarnesPorClasificacion(clasificacion); 
        }
    }

}

