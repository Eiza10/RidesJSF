package eredua.bean;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Listener que inicializa el sistema cuando la aplicación arranca.
 * Esto evita el retraso en la primera consulta del usuario.
 */
@WebListener
public class AppStartupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("AppStartupListener: Sistemaren pre-karga hasten...");
        try {
            // Forzar la inicialización del FacadeBean y la conexión a BD
            FacadeBean.getBusinessLogic();
            System.out.println("AppStartupListener: Sistema ondo pre-kargatu da");
        } catch (Exception e) {
            System.err.println("AppStartupListener: Error en pre-carga: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("AppStartupListener: Aplikazioa gelditu da");
    }
}
