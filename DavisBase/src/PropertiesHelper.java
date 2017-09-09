//import java.io.File;
import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import java.io.*;
import java.util.*;

/**
 * Created by ramya on 10/4/17.
 */
public class PropertiesHelper {
    Properties prop ;
    File propfile;
    String propfilename = "";
    public PropertiesHelper(String filename) {
         prop = new Properties();
         propfile = new File(filename);
         propfilename = filename;

        try {
            if(!propfile.exists())
                propfile.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setProperties(String key, String value){
        OutputStream outputStream = null;
        try {
            FileInputStream in = new FileInputStream(propfilename);
//            Properties props = new Properties();
            prop.load(in);
            in.close();
//            File file = new File(propfilename);
            outputStream = new FileOutputStream(propfilename);
            prop.setProperty(key,value);
            prop.store(outputStream,null);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Set getKeySet(String tableName){
        HashSet<String> keys = new HashSet<>();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(propfilename);
            prop.load(inputStream);
            for (Object o: prop.keySet()) {
                if(o.toString().startsWith(tableName))
                    keys.add(o.toString());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return keys;
    }

    public TreeMap<String,String> getColumnsInOrdinalPositionOrder(String tableName){
        TreeMap<String, String> entrySet = new TreeMap<>();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(propfilename);
            prop.load(inputStream);
            for (Object o: prop.keySet()) {
                if(o.toString().startsWith(tableName))
                    entrySet.put(prop.getProperty(o.toString()),o.toString());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entrySet;

    }
    public String getProperties(String key){
        String value = "";
        InputStream inputStream = null;
        try {
             inputStream = new FileInputStream(propfilename);
            prop.load(inputStream);
            value =  prop.getProperty(key);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    return value;
    }

}

