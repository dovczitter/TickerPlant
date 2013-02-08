package client;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class HibernateUtil
{
	private static SessionFactory sessionFactory;
	private static int hibernateBatchSize;
	
   	static {
        try {
	    		Configuration config = new Configuration();
	    		config.addAnnotatedClass(Data.class);
	    		config.configure("hibernate.cfg.xml");
	    		new SchemaExport(config).create(true, true);
	    		sessionFactory = config.buildSessionFactory();
	    		hibernateBatchSize = Integer.parseInt(config.getProperty("hibernate.jdbc.batch_size"));
        } catch (Throwable ex) {
            System.err.println("Initial session creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
	public static Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
    
	public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
	public static int getHibernateBatchSize() {
        return hibernateBatchSize;
    }
}
