package appagenda;

import entidades.Persona;
import entidades.Provincia;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.RollbackException;

/**
 * FXML Controller class
 *
 * @author usu2dam
 */
public class PersonaDetalleViewController implements Initializable {

    @FXML
    private TextField textFieldNombre;
    @FXML
    private TextField textFieldApellidos;
    @FXML
    private TextField textFieldTelefono;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private TextField textFieldNumHijos;
    @FXML
    private TextField textFieldSalario;
    @FXML
    private DatePicker datePickerFechaNacimiento;
    @FXML
    private RadioButton radioButtonSoltero;
    @FXML
    private RadioButton radioButtonCasado;
    @FXML
    private RadioButton radioButtonViudo;
    @FXML
    private ComboBox<Provincia> comboBoxProvincia;
    @FXML
    private CheckBox checkBoxJubilado;
    @FXML
    private ImageView imageViewFoto;
    
    private Pane rootAgendaView;
    @FXML
    private AnchorPane rootPersonaDetalleView;
    @FXML
    private Button onActionButtonGuardar;
    @FXML
    private Button onActionButtonCancelar;
    
    private TableView tableViewPrevio;
    private Persona persona;
    private EntityManager entityManager;
    private boolean nuevaPersona;
    
    public static final char CASADO='C';
    public static final char SOLTERO='S';
    public static final char VIUDO='V';
    
    public static final String CARPETA_FOTOS="src/appagenda/Fotos";
    @FXML
    private Button onActionButtonExaminar;
    @FXML
    private Button onActionSuprimirFoto;



    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    public void setRootAgendaView(Pane rootAgendaView){
        this.rootAgendaView = rootAgendaView;
    }
    @FXML
    private void onActionButtonGuardar(ActionEvent event) {      
    
        //Bolean para formato erroneo
        boolean errorFormato = false;
        
        //Funcion para indicar que tiene que rellenar "Provincia"
        if (comboBoxProvincia.getValue() != null) {
            persona.setProvincia(comboBoxProvincia.getValue());
        }else {
            Alert alert = new Alert(AlertType.INFORMATION,"Debe indicar una Provincia");
            alert.showAndWait();
            errorFormato = true;
            comboBoxProvincia.requestFocus();
        }
        //Funcion para indicar que tiene que rellenar "EMail"
        if(textFieldEmail.getText() != null) {
            persona.setEmail(textFieldEmail.getText());
        }else {
            Alert alert = new Alert(AlertType.INFORMATION,"Debe indicar un EMail");
            alert.showAndWait();
            errorFormato = true;
            textFieldEmail.requestFocus();
        }
        //Funcion para indicar que tiene que rellenar "Apellido"
        if(textFieldApellidos.getText() != null) {
            persona.setApellidos(textFieldApellidos.getText());
        }else {
            Alert alert = new Alert(AlertType.INFORMATION,"Debe indicar un Apellido");
            alert.showAndWait();
            errorFormato = true;
            textFieldApellidos.requestFocus();
        }
        //Funcion para indicar que tiene que rellenar "Nombre"
        if(textFieldNombre.getText() != null) {
            persona.setNombre(textFieldNombre.getText());
        }else {
            Alert alert = new Alert(AlertType.INFORMATION,"Debe indicar un Nombre");
            alert.showAndWait();
            errorFormato = true;
            textFieldNombre.requestFocus();
        }       
        
        
        // Recoger datos de pantalla
        if (!errorFormato) { // Los datos introducidos son correctos
            try {
                persona.setNombre(textFieldNombre.getText());
                persona.setApellidos(textFieldApellidos.getText());
                persona.setTelefono(textFieldTelefono.getText());
                persona.setEmail(textFieldEmail.getText());
                persona.setJubilado(checkBoxJubilado.isSelected());
                persona.setProvincia(comboBoxProvincia.getValue());
                if (nuevaPersona) {
                    entityManager.persist(persona);
                }else {
                    entityManager.merge(persona);
                }
                entityManager.getTransaction().commit();
                //Si el numero de hijos tiene un formato incorrecto
                if (!textFieldNumHijos.getText().isEmpty()) {
                    try {
                        persona.setNumHijos(Short.valueOf(textFieldNumHijos.getText()));
                    }catch(NumberFormatException ex) {
                        errorFormato = true;
                        Alert alert = new Alert(AlertType.INFORMATION, "Número de hijos no válido");           
                        alert.showAndWait();
                        errorFormato = true;
                        textFieldNumHijos.requestFocus();
                    }
                }
                //Si el salario tiene un formato incorrecto
                if (!textFieldSalario.getText().isEmpty()) {
                    try {
                        persona.setSalario(BigDecimal.valueOf(Double.parseDouble(textFieldSalario.getText())));
                    } catch(NumberFormatException ex) {
                        errorFormato = true;
                        Alert alert = new Alert(AlertType.INFORMATION, "Salario no válido");
                        alert.showAndWait();
                        errorFormato = true;
                        textFieldSalario.requestFocus();
                    }
                }                
                int numFilaSeleccionada;
                if (nuevaPersona) {      
                    tableViewPrevio.getItems().add(persona);
                    numFilaSeleccionada = tableViewPrevio.getItems().size()- 1;
                    tableViewPrevio.getSelectionModel().select(numFilaSeleccionada);
                    tableViewPrevio.scrollTo(numFilaSeleccionada);
                }else {
                    numFilaSeleccionada=
                    tableViewPrevio.getSelectionModel().getSelectedIndex();
                    tableViewPrevio.getItems().set(numFilaSeleccionada,persona);
                }
                TablePosition pos = new TablePosition(tableViewPrevio,numFilaSeleccionada,null);
                tableViewPrevio.getFocusModel().focus(pos);
                tableViewPrevio.requestFocus();

                //Seleccion correcta de estado civil
                if (radioButtonCasado.isSelected()) {
                    persona.setEstadoCivil(CASADO);
                }else if (radioButtonSoltero.isSelected()) {
                    persona.setEstadoCivil(SOLTERO);
                }else if (radioButtonViudo.isSelected()) {
                    persona.setEstadoCivil(VIUDO);
                }

                //Mostrar en el control el tipo date correctamente
                if (datePickerFechaNacimiento.getValue() != null) {
                    LocalDate localDate = datePickerFechaNacimiento.getValue();
                    ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
                    Instant instant = zonedDateTime.toInstant();
                    Date date = Date.from(instant);
                    persona.setFechaNacimiento(date);
                }else {
                    persona.setFechaNacimiento(null);
                }
               
                
                StackPane rootMain = (StackPane) rootPersonaDetalleView.getScene().getRoot();
                rootMain.getChildren().remove(rootPersonaDetalleView);
                rootAgendaView.setVisible(true);
            }catch (RollbackException ex) { // Los datos introducidos no cumplen requisitos de BD
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setHeaderText("No se han podido guardar los cambios. " + "Compruebe que los datos cumplen los requisitos");
                alert.setContentText(ex.getLocalizedMessage());
                alert.showAndWait();
            }
        }       

    }
    
        
    @FXML
    private void onActionButtonCancelar(ActionEvent event) {
        StackPane rootMain = (StackPane) rootPersonaDetalleView.getScene().getRoot();
        rootMain.getChildren().remove(rootPersonaDetalleView);
        rootAgendaView.setVisible(true);
        
        entityManager.getTransaction().rollback();
        int numFilaSeleccionada = tableViewPrevio.getSelectionModel().getSelectedIndex();
        TablePosition pos = new TablePosition(tableViewPrevio,numFilaSeleccionada,null);
        tableViewPrevio.getFocusModel().focus(pos);
        tableViewPrevio.requestFocus();
    }
    //Metodo set de TableViewPrevio
    public void setTableViewPrevio(TableView tableViewPrevio) {
        this.tableViewPrevio = tableViewPrevio;
    }
    
    //Para conocer si es una nueva persona o no
    public void setPersona(EntityManager entityManager, Persona persona, boolean nuevaPersona) {
        this.entityManager = entityManager;
        entityManager.getTransaction().begin();
        if (!nuevaPersona) {
            this.persona=entityManager.find(Persona.class,persona.getId());
        } else {
            this.persona=persona;
        }
        this.nuevaPersona=nuevaPersona;
    }
    //Metodo para poder mostrar los datos selecionado
    public void mostrarDatos()
    {
        textFieldNombre.setText(persona.getNombre());
        textFieldApellidos.setText(persona.getApellidos());
        textFieldTelefono.setText(persona.getTelefono());
        textFieldEmail.setText(persona.getEmail());
        
        //Nombre
        if (persona.getNombre() != null) {
            textFieldNombre.setText(persona.getNombre());
        }
        //Numeros de hijos
        if (persona.getNumHijos() != null) {
            textFieldNumHijos.setText(persona.getNumHijos().toString());
        }
        //Salario
        if (persona.getSalario() != null) {
            textFieldSalario.setText(persona.getSalario().toString());
        }
        //Jubiliado
        if (persona.getJubilado() != null) {
            checkBoxJubilado.setSelected(persona.getJubilado());
        }
        
        //Estado Civil
        if (persona.getEstadoCivil() != null) {
            switch(persona.getEstadoCivil()) {
                case CASADO:
                    radioButtonCasado.setSelected(true);
                    break;
                case SOLTERO:
                    radioButtonSoltero.setSelected(true);
                    break;
                case VIUDO:
                    radioButtonViudo.setSelected(true);
                    break;
            }
        }
        
        //Fecha nacimiento
        if (persona.getFechaNacimiento() != null) {  
            Date date=persona.getFechaNacimiento();
            Instant instant=date.toInstant();
            ZonedDateTime zdt=instant.atZone(ZoneId.systemDefault());
            LocalDate localDate=zdt.toLocalDate();
            datePickerFechaNacimiento.setValue(localDate);
        }
        
        //Provincia
        Query queryProvinciaFindAll = entityManager.createNamedQuery("Provincia.findAll");
        List listProvincia = queryProvinciaFindAll.getResultList();
        comboBoxProvincia.setItems(FXCollections.observableList(listProvincia));
        if (persona.getProvincia() != null) {
            comboBoxProvincia.setValue(persona.getProvincia());
        }
        //Hacer que aparezca correcamente en formato string las provincias
        comboBoxProvincia.setCellFactory ((ListView<Provincia> l)-> new ListCell<Provincia>() {
            @Override
            protected void updateItem(Provincia provincia, boolean empty) {
                super.updateItem(provincia, empty);
                if (provincia == null || empty) {
                    setText("");
                }else {
                    setText(provincia.getCodigo()+"-"+provincia.getNombre());
                }
            }
        });
        //Hacer que se muestre correctamente los elementos una vez seleccionado la provincia deseada
        comboBoxProvincia.setConverter(new StringConverter<Provincia>() {   
            @Override
            public String toString(Provincia provincia) {
                if (provincia == null) {
                    return null;
                }else {
                    return provincia.getCodigo()+"-"+provincia.getNombre();
                }
            }
            @Override
            public Provincia fromString(String userId) {
                return null;
            }
        });
        //Compruebe si realmente existe dicho archivo de imagen, si no existe se mostrara un mensaje
        //Si existe se cargara la imagen
        if (persona.getFoto() != null) {
            String imageFileName=persona.getFoto();
            File file = new File(CARPETA_FOTOS+"/"+imageFileName);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                imageViewFoto.setImage(image);
            }else {
                Alert alert=new Alert(AlertType.INFORMATION,"No se encuentra la imagen en "+file.toURI().toString());
                alert.showAndWait();
            }
        }
    }
    //Accion del boton Examinar para cambiar la imagen
    @FXML
    private void onActionButtonExaminar(ActionEvent event) {
        File carpetaFotos = new File(CARPETA_FOTOS);
        if (!carpetaFotos.exists()) {
            carpetaFotos.mkdir();
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Imágenes (jpg, png)", "*.jpg", "*.png"),
                new FileChooser.ExtensionFilter("Todos los archivos","*.*"));
        File file = fileChooser.showOpenDialog(rootPersonaDetalleView.getScene().getWindow());
        if (file != null) {
            try {
                Files.copy(file.toPath(),new File(CARPETA_FOTOS+"/"+file.getName()).toPath());persona.setFoto(file.getName());
                Image image = new Image(file.toURI().toString());
                imageViewFoto.setImage(image);
            }catch (FileAlreadyExistsException ex) {
                Alert alert = new Alert(AlertType.WARNING,"Nombre de archivo duplicado");
                alert.showAndWait();
            }catch (IOException ex) {
                Alert alert = new Alert(AlertType.WARNING,"No se ha podido guardar la imagen");
                alert.showAndWait();
            }
        }
    }
    
    //Boton para eliminar la foto subida
    @FXML
    private void onActionSuprimirFoto(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmar supresión de imagen");
        alert.setHeaderText("¿Desea SUPRIMIR el archivo asociado a la imagen,\n"+ "quitar la foto pero MANTENER el archivo, \no CANCELAR la operación?");
        alert.setContentText("Elija la opción deseada:");
        
        ButtonType buttonTypeEliminar = new ButtonType("Suprimir");
        ButtonType buttonTypeMantener = new ButtonType("Mantener");
        ButtonType buttonTypeCancel = new ButtonType("Cancelar",ButtonData.CANCEL_CLOSE);
        
        alert.getButtonTypes().setAll(buttonTypeEliminar, buttonTypeMantener,buttonTypeCancel);
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeEliminar) {
            String imageFileName = persona.getFoto();
            File file = new File(CARPETA_FOTOS + "/" + imageFileName);
            if (file.exists()) {
                file.delete();
            }
            persona.setFoto(null);
            imageViewFoto.setImage(null);
        }else if (result.get() == buttonTypeMantener) {
            persona.setFoto(null);
            imageViewFoto.setImage(null);
        }
    }
}
