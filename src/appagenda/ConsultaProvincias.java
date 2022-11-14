/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package appagenda;

import entidades.Provincia;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author usu2dam
 */
public class ConsultaProvincias {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Map<String,String> emfProperties = new HashMap<>();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AppAgendaPU", emfProperties);
        EntityManager em = emf.createEntityManager();
        Query queryProvincias = em.createNamedQuery("Provincia.findAll");
        List<Provincia> listProvincias = queryProvincias.getResultList();
        for(Provincia provincia : listProvincias){
            System.out.println(provincia.getNombre());
        }

    }
    
}
