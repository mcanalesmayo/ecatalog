package Client;

import Database.JDBCTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JTable;

/**
 * Controlador de la aplicación
 */
public class Controller {
    
    /**
     * Obtiene un string que representa en SQL una condición para un
     * determinado par clave => valor
     * @return String que representa una condición en SQL
     */
    private static String getSQLOption(String key, String value){
        if (key.endsWith("Min")) return key.substring(0, key.lastIndexOf("Min")) + ">='" + value + "'";
        else if (key.endsWith("Max")) return key.substring(0, key.lastIndexOf("Max")) + "<='" + value + "'";
        //NOTA: no hay problemas con que sea un tipo de dato distinto a string
        else return key + "='" + value + "'";
    }
    
    /**
     * Obtiene un string que representa en SQL un conjunto de condiciones
     * para determinados pares clave => valor
     * @return String que representa un conjunto de condiciones en SQL
     */
    private static String getSQLOptions(Form form){
        if (form.isEmpty()) return "";
        else{
            String res = "WHERE ";
            Map.Entry<String, String> aux;
            
            //condiciones de la forma campo='valor'
            Iterator<Map.Entry<String, String>> iterator = form.getIterator();
            aux = iterator.next();
            res += getSQLOption(aux.getKey(), aux.getValue());
            while (iterator.hasNext()){
                aux = iterator.next();
                res += " AND " + getSQLOption(aux.getKey(), aux.getValue());
            }
            
            return res;
        }
    }
    
    /**
     * Accede al listado de coches
     * @param form Filtro de parámetros
     * @param modelsList Lista que contiene los resultados de la búsqueda
     */
    public static void list(Form form, JTable modelsList){
    	JDBCTemplate template = JDBCTemplate.getJDBCTemplate();
    	String options = getSQLOptions(form);
    	String query = "SELECT * FROM Car "+options;
    	ResultSet rs = template.executeQuery(query).getResultSet();
        ArrayList<CarModel> models = new ArrayList<CarModel>();
    	try {
            while(rs.next()){
                models.add(new CarModel(rs.getString("name"), rs.getString("fuel_type"), rs.getInt("power"), rs.getString("category"),
                        rs.getInt("number_doors"), rs.getInt("cost"), rs.getDouble("consumption"), rs.getInt("number_seats")));
            }
        } catch (SQLException e){}
        
    	//pasamos el vector de modelos a la vista
    	View.list(models.toArray(new CarModel[models.size()]), modelsList);
    }
    
    /**
     * Envía una solicitud de contacto para concertar una cita y ver un coche
     * @param form Filtro de parámetros
     * @param carName Nombre identificador del coche que desea ver el cliente
     */
    public static void contactRequest(Form form, String carName){
        JDBCTemplate template = JDBCTemplate.getJDBCTemplate();
        String insert="INSERT INTO Requests VALUES (requests_id.nextval, ";
        insert += "'" + form.getParam("name") + " " + form.getParam("surname") + "'";
        insert += ", '" + form.getParam("mail") + "'";
        insert += ", '" + carName + "'";
        //momento de la compra
        insert += ", CURRENT_TIMESTAMP";
        insert += ", '" + form.getParam("city") + "')";
        template.executeQuery(insert);
    }
}