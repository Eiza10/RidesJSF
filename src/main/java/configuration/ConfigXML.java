package configuration;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ConfigXML {
    
    private String configFile = "config.xml"; 
        
    private String businessLogicNode;
    private String businessLogicPort;
    private String businessLogicName;
    private static String dbFilename;
    private boolean isDatabaseInitialized;
    private boolean businessLogicLocal;
    private boolean databaseLocal;
    private String databaseNode;
    private int databasePort;
    private String user;
    private String password;
    private String locale;

    private static ConfigXML theInstance = new ConfigXML();

    private ConfigXML() {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            
            InputStream inputStream = ConfigXML.class.getClassLoader().getResourceAsStream(configFile);
            
            if (inputStream == null) {
                System.err.println("CRITICAL ERROR: No se encontró el fichero " + configFile + " en el classpath.");
                inputStream = ConfigXML.class.getClassLoader().getResourceAsStream("src/main/resources/config.xml");
                if (inputStream == null) {
                    System.err.println("CRITICAL ERROR: Tampoco se encontró en src/main/resources/config.xml");
                    throw new java.io.FileNotFoundException("No se encuentra config.xml ni en raíz ni en src.");
                }
            }

            Document doc = dBuilder.parse(inputStream);
            
            doc.getDocumentElement().normalize();

            NodeList list = doc.getElementsByTagName("config");
            Element config = (Element) list.item(0);

            String value = ((Element) config.getElementsByTagName("businessLogic").item(0)).getAttribute("local");
            businessLogicLocal = value.equals("true");

            businessLogicNode = getTagValue("businessLogicNode", config);
            businessLogicPort = getTagValue("businessLogicPort", config);
            businessLogicName = getTagValue("businessLogicName", config);
            locale = getTagValue("locale", config);
            
            String rawDbFilename = getTagValue("dbFilename", config);
            java.io.File rawFile = new java.io.File(rawDbFilename);
            String fileNameOnly = rawFile.getName(); 
            String userHome = System.getProperty("user.home");
            String targetDir = userHome + java.io.File.separator + "Rides24_DB";
            java.io.File dir = new java.io.File(targetDir);
            if (!dir.exists()) {
                dir.mkdirs(); 
            }
            dbFilename = targetDir + java.io.File.separator + fileNameOnly;

            System.out.println("=== CONFIGURACIÓN BASE DE DATOS ===");
            System.out.println("Guardando en: " + dbFilename);
            System.out.println("===================================");

            value = ((Element) config.getElementsByTagName("database").item(0)).getAttribute("local");
            databaseLocal = value.equals("true");

            String dbOpenValue = ((Element) config.getElementsByTagName("database").item(0)).getAttribute("initialize");
            isDatabaseInitialized = dbOpenValue.equals("true");

            databaseNode = getTagValue("databaseNode", config);
            databasePort = Integer.parseInt(getTagValue("databasePort", config));
            user = getTagValue("user", config);
            password = getTagValue("password", config);

            System.out.print("Read from config.xml: ");
            System.out.print("\t businessLogicLocal=" + businessLogicLocal);
            System.out.print("\t databaseLocal=" + databaseLocal);
            System.out.println("\t dataBaseInitialized=" + isDatabaseInitialized);

        } catch (Exception e) {
            System.err.println("Error in ConfigXML.java: problems reading " + configFile);
            e.printStackTrace();
        }
    }
    
    private static String getTagValue(String sTag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);
        return nValue.getNodeValue();
    }

    public static ConfigXML getInstance() {
        return theInstance;
    }
    
    public String getBusinessLogicNode() { return businessLogicNode; }
    public String getBusinessLogicPort() { return businessLogicPort; }
    public String getBusinessLogicName() { return businessLogicName; }
    public String getDbFilename() { return dbFilename; }
    public boolean isDatabaseInitialized() { return isDatabaseInitialized; }
    public String getDatabaseNode() { return databaseNode; }
    public int getDatabasePort() { return databasePort; }
    public String getUser() { return user; }
    public String getPassword() { return password; }
    public boolean isDatabaseLocal() { return databaseLocal; }
    public boolean isBusinessLogicLocal() { return businessLogicLocal; }
    public String getLocale() { return locale; }
}
