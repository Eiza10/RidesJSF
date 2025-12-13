package eredua.bean;

import businessLogic.BLFacade;
import businessLogic.BLFacadeImplementation;
import dataAccess.HibernateDataAccess;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@Named
@ApplicationScoped
public class FacadeBean {
	private static BLFacade facadeInterface;
	
	// Static initializer - se ejecuta cuando la clase se carga
	static {
		initializeFacade();
	}
	
	private static synchronized void initializeFacade() {
		if (facadeInterface == null) {
			try {
				System.out.println("FacadeBean: Inicializando BLFacade...");
				facadeInterface = new BLFacadeImplementation(new HibernateDataAccess());
				System.out.println("FacadeBean: BLFacade inicializado correctamente");
			} catch (Exception e) {
				System.out.println("FacadeBean: negozioaren logika sortzean errorea: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	@PostConstruct
	public void init() {
		// Asegura inicialización temprana cuando CDI crea el bean
		initializeFacade();
	}
	
	public static BLFacade getBusinessLogic() {
		return facadeInterface;
	}
}
