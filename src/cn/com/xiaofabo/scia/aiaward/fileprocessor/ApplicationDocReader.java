/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.xiaofabo.scia.aiaward.fileprocessor;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


/**
 *
 * @author cgx82
 */
public class ApplicationDocReader implements InputFileReader{
    static Logger logger = Logger.getLogger(ApplicationDocReader.class.getName());
    
    public ApplicationDocReader(){
        PropertyConfigurator.configure("log/config.txt");
        logger.info("Constructor of ApplicationDocReader");
    }
}
