package appagenda;


import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Guillermo Benitez
 * @version 1.0
 * Descripcion: 
 */
public class AppAgenda {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Map<String,String> emfProperties = new HashMap<>();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AppAgendaPU", emfProperties);
        EntityManager em = emf.createEntityManager();
        
       
        //Iniciar transaccion
        em.getTransaction().begin();
        //Confirmar cambios
        em.getTransaction().commit();
        
        
        em.close();
        emf.close();
        try{
            DriverManager.getConnection("jdbc:derby:BDAgenda;shutdown=true");
        } catch (SQLException ex){
        }

    }
    
}
