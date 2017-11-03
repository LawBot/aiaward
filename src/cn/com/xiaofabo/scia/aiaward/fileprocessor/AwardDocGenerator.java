/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.xiaofabo.scia.aiaward.fileprocessor;

import org.apache.log4j.Logger;

/**
 *
 * @author cgx82
 */
public class AwardDocGenerator implements OutputGenerator{
    public static final Logger logger = Logger.getLogger(AwardDocGenerator.class.getName());
    private String outAwardDocUrl;
    
    public AwardDocGenerator(String outAwardDocUrl){
        logger.trace("Constructor: start constructing AwardDocGenerator");
        this.outAwardDocUrl = outAwardDocUrl;
        logger.trace("Constructor: finish constructing AwardDocGenerator");
    }
    
}
