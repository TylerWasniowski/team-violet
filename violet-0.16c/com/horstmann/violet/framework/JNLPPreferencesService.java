package com.horstmann.violet.framework;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.jnlp.BasicService;
import javax.jnlp.FileContents;
import javax.jnlp.ServiceManager;
import javax.jnlp.PersistenceService;
import javax.jnlp.UnavailableServiceException;

/**
 * A preferences service that uses WebStart "muffins".
 */
public class JNLPPreferencesService extends PreferencesService
{
   /**
    * Gets an instance of the service, with storage location derived from the JNLP code base.
    */
   public JNLPPreferencesService()
   {
      try
      {
         service = (PersistenceService) ServiceManager.lookup("javax.jnlp.PersistenceService"); 
      }
      catch (UnavailableServiceException ex)
      {
         ex.printStackTrace();
      }      
   }
   /**
    * Tries to get the preference service at the given key
    * If unable to retrieve the service, just return the given default value
    * @param key the key of the desired preference service
    * @param defval the default value to return if unable to get the desired service
    * @return the preference service
    */
   public String get(String key, String defval)
   {
      try 
      { 
         BasicService basic = (BasicService) ServiceManager.lookup("javax.jnlp.BasicService"); 
         URL codeBase = basic.getCodeBase();

         PersistenceService service 
            = (PersistenceService) ServiceManager.lookup("javax.jnlp.PersistenceService"); 
         URL keyURL = new URL(codeBase, key);

         FileContents contents = service.get(keyURL);
         InputStream in = contents.getInputStream();
         BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
         String r = reader.readLine();
         if (r != null) return r; 
      } 
      catch (UnavailableServiceException e) 
      { 
         e.printStackTrace();
      }
      catch (MalformedURLException e) 
      { 
         e.printStackTrace();
      }
      catch (IOException e) 
      { 
         e.printStackTrace();
      }      
      return defval;
   }
   /**
    * Tries to write the preference service at the given key
    * @param key the key for placing the value
    * @param value the service to write at the given key
    */
   public void put(String key, String value)
   {
      try 
      { 
         BasicService basic = (BasicService) ServiceManager.lookup("javax.jnlp.BasicService"); 
         URL codeBase = basic.getCodeBase();

         PersistenceService service 
            = (PersistenceService) ServiceManager.lookup("javax.jnlp.PersistenceService"); 
         URL keyURL = new URL(codeBase, key);
         try { service.delete(keyURL); } catch (Exception ex) {}
         byte[] bytes = value.getBytes("UTF-8");
         service.create(keyURL, bytes.length);
         FileContents contents = service.get(keyURL);
         OutputStream out = contents.getOutputStream(true);
         out.write(bytes);
         out.close();               
      } 
      catch (UnavailableServiceException e) 
      { 
         e.printStackTrace();
      }
      catch (MalformedURLException e) 
      { 
         e.printStackTrace();
      }
      catch (IOException e) 
      { 
         e.printStackTrace();
      }      
   }
   
   private PersistenceService service;
}
