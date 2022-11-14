package appagenda;

import entidades.Persona;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * FXML Controller class
 *
 * @author usu2dam
 */
public class AgendaViewController implements Initializable {
    
    private EntityManager entityManager;
    @FXML
    private TableView<Persona> tableViewAgenda;
    @FXML
    private TableColumn<Persona, String> columnNombre;
    @FXML
    private TableColumn<Persona, String> columnApellidos;
    @FXML
    private TableColumn<Persona, String> columnEmail;
    @FXML
    private TableColumn<Persona, String> columnProvincia;
    @FXML
    private TextField textFieldNombre;
    @FXML
    private TextField textFieldApellidos;
    @FXML
    private Button onActionButtonGuardar;
    
    private Persona personaSeleccionada;
    @FXML
    private Button onActionButtonNuevo;
    @FXML
    private Button onActionButtonEditar;
    @FXML
    private Button onActionButtonSuprimir;
    @FXML
    private AnchorPane rootAgendaView;
    
    
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODOs
        columnNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnApellidos.setCellValueFactory(new
        PropertyValueFactory<>("apellidos"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        columnProvincia.setCellValueFactory(cellData-> {
            SimpleStringProperty property=new SimpleStringProperty();
            if (cellData.getValue().getProvincia()!=null){
                property.setValue(cellData.getValue().getProvincia().getNombre());
            }
            return property;
        });
        tableViewAgenda.getSelectionModel().selectedItemProperty().addListener((observable,oldValue,newValue)-> {
            personaSeleccionada=newValue;
            if (personaSeleccionada != null) {
                textFieldNombre.setText(personaSeleccionada.getNombre());
                textFieldApellidos.setText(personaSeleccionada.getApellidos());
            }else {
                textFieldNombre.setText("");
                textFieldApellidos.setText("");
            }
        });
    }
    
    //Metodo cargarTodasPersonas para visualizarlo
    public void cargarTodasPersonas() {
        Query queryPersonaFindAll=
        entityManager.createNamedQuery("Persona.findAll");
        List<Persona> listPersona = queryPersonaFindAll.getResultList();
        tableViewAgenda.setItems(FXCollections.observableArrayList(listPersona));
    }
    
    //Accion del boton cuando se pulse para guardarlo
    @FXML
    public void onActionButtonGuardar(ActionEvent event) {
        //Comprobamos que haya algun registro seleccionado
        if (personaSeleccionada != null) {
            //Actualizar los valores de la propiedad nombre y apellidos
            personaSeleccionada.setNombre(textFieldNombre.getText());
            personaSeleccionada.setApellidos(textFieldApellidos.getText());
            //Actualizar el objecto personaSeleccionada
            entityManager.getTransaction().begin();
            entityManager.merge(personaSeleccionada);
            entityManager.getTransaction().commit();
            //Actualizar los nuevos valores del objeto            
            int numFilaSeleccionada =
            tableViewAgenda.getSelectionModel().getSelectedIndex();
            tableViewAgenda.getItems().set(numFilaSeleccionada,personaSeleccionada);
            //Si no se indica nada, el foco vuelve a TableView
            TablePosition pos = new TablePosition(tableViewAgenda,numFilaSeleccionada,null);
            tableViewAgenda.getFocusModel().focus(pos);
            tableViewAgenda.requestFocus();
        }
    }
    //Accion para poder crear nuevos datos.
    //Se abrira una nueva scene para poder añadir nuevos datos.
    @FXML
    private void onActionButtonNuevo(ActionEvent event){
        try {
            // Cargar la vista de detalle
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PersonaDetalleView.fxml"));
            Parent rootDetalleView = fxmlLoader.load();
            
            PersonaDetalleViewController PersonaDetalleView = (PersonaDetalleViewController) fxmlLoader.getController();
            PersonaDetalleView.setRootAgendaView(rootAgendaView);
            // Ocultar la vista de la lista
            rootAgendaView.setVisible(false);
            
            //Añadir la vista detalle al StackPane principal para que se muestre
            StackPane rootMain = (StackPane) rootAgendaView.getScene().getRoot();
            rootMain.getChildren().add(rootDetalleView);
            
            //Intercambio de datos funcionales con el detalle
            PersonaDetalleView.setTableViewPrevio(tableViewAgenda);
            //Indica si es nuevo o no
            personaSeleccionada = new Persona();
            PersonaDetalleView.setPersona(entityManager, personaSeleccionada,true);
            //Indica que se muestren en la visa de detalle los datos correspondientes
            PersonaDetalleView.mostrarDatos();
            
        }catch (IOException ex) {
            Logger.getLogger(AgendaViewController.class.getName()).log(Level.SEVERE,null,ex);
        }

    }

    @FXML
    private void onActionButtonEditar(ActionEvent event) {
        if(personaSeleccionada != null) {
            try {
                // Cargar la vista de detalle
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PersonaDetalleView.fxml"));
                Parent rootDetalleView = fxmlLoader.load();

                PersonaDetalleViewController PersonaDetalleView = (PersonaDetalleViewController) fxmlLoader.getController();
                PersonaDetalleView.setRootAgendaView(rootAgendaView);
                //Intercambio de datos funcionales con el detalle
                PersonaDetalleView.setTableViewPrevio(tableViewAgenda);
                //Indica si es nuevo o no
                PersonaDetalleView.setPersona(entityManager, personaSeleccionada,false);
                //Indica que se muestren en la visa de detalle los datos correspondientes
                PersonaDetalleView.mostrarDatos();
                // Ocultar la vista de la lista
                rootAgendaView.setVisible(false);

                //Añadir la vista detalle al StackPane principal para que se muestre
                StackPane rootMain = (StackPane) rootAgendaView.getScene().getRoot();
                rootMain.getChildren().add(rootDetalleView);


            }catch (IOException ex) {
                Logger.getLogger(AgendaViewController.class.getName()).log(Level.SEVERE,null,ex);
            }
        }
    }

    @FXML
    private void onActionButtonSuprimir(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmar");
        alert.setHeaderText("¿Desea suprimir el siguiente registro?");
        alert.setContentText(personaSeleccionada.getNombre() + " " + personaSeleccionada.getApellidos());
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            // Acciones a realizar si el usuario acepta
            try {
                entityManager.getTransaction().begin();
                entityManager.merge(personaSeleccionada);
                entityManager.remove(personaSeleccionada);
                entityManager.getTransaction().commit();
                tableViewAgenda.getItems().remove(personaSeleccionada);
                tableViewAgenda.getFocusModel().focus(null);
                tableViewAgenda.requestFocus();
            } catch (Exception e) {
                alert = new Alert(AlertType.WARNING);
                alert.setTitle("ERROR");
                alert.setHeaderText("No se ha podido eliminar correctamente");
            }
        } else {
            // Acciones a realizar si el usuario cancela
            int numFilaSeleccionada = tableViewAgenda.getSelectionModel().getSelectedIndex();
            tableViewAgenda.getItems().set(numFilaSeleccionada,personaSeleccionada);
            TablePosition pos = new TablePosition(tableViewAgenda,
            numFilaSeleccionada,null);
            tableViewAgenda.getFocusModel().focus(pos);
            tableViewAgenda.requestFocus();
        }

    }
    

    
    
}
