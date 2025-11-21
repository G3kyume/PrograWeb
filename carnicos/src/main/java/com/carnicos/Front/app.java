///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.carnicos.Front;
//
///**
// *
// * @author Lenovo
// */
//public class app {
// 
//    
//    document.addEventListener("DOMContentLoaded", () => {
//    const contenedor = document.getElementById("listaCarnes");
//    const botonesFiltro = document.querySelectorAll(".filtro-btn");
//
//    // Función principal que hace el FETCH al servidor Java
//    async function cargarCarnes(clasificacion = 'Todos') {
//        let url = 'listarCarnes'; // Endpoint que debe manejar tu Java Controller
//
//            // Añade el filtro como parámetro de consulta si no es 'Todos'
//            if (clasificacion !== 'Todos') {
//                url = `listarCarnes?clasificacion=${clasificacion}`;
//            }
//
//            try {
//                const respuesta = await fetch(url);
//
//                // Verificación de respuesta HTTP exitosa (código 200)
//                if (!respuesta.ok) {
//                    throw new Error(`Error en el servidor: Código ${respuesta.status}`);
//                }
//
//                const carnes = await respuesta.json();
//
//                // Limpia y Renderiza la lista
//                contenedor.innerHTML = ''; 
//
//                if (carnes.length === 0) {
//                    contenedor.innerHTML = '<p>No hay productos disponibles para esta clasificación.</p>';
//                    return;
//                }
//
//                carnes.forEach(c => {
//                    const item = document.createElement("div");
//                    item.classList.add("item");
//
//                    // ATENCIÓN: Asegúrate de que los nombres de las propiedades (c.nombre, c.precio, etc.)
//                    // coincidan con los nombres de las propiedades que tu servidor Java está devolviendo en el JSON.
//                   item.innerHTML = `
//                        <div class="header-item">
//                            <span class="unidades">${c.unidadesDisponibles} UNIDADES DISPONIBLES</span>
//                            <span class="precio">$${c.precio ? c.precio.toFixed(2) : 'Precio no disponible'}</span>
//                        </div>
//                        <div class="body-item">
//                            <h3>${c.nombre}</h3>
//                            <p>${c.descripcion ? c.descripcion : 'Sin descripción disponible'}</p>
//                            <span class="entrega">Entrega: ${c.entrega ? c.entrega : 'No especificada'}</span>
//                            <button>+</button>
//                        </div>
//                    `;
//                    contenedor.appendChild(item);
//                });
//
//            } catch (error) {
//                console.error("❌ Error al cargar o filtrar las carnes:", error);
//                contenedor.innerHTML = '<p>Ocurrió un error de conexión con el servidor.</p>';
//            }
//        }
//
//        // A. Conectar la función de carga a cada botón
//        botonesFiltro.forEach(btn => {
//            btn.addEventListener('click', () => {
//                const clasificacion = btn.getAttribute('data-clasificacion');
//                cargarCarnes(clasificacion);
//            });
//        });
//
//        // B. Carga inicial al iniciar la página (muestra todos)
//        cargarCarnes(); 
//    });
//    
//    
//}
