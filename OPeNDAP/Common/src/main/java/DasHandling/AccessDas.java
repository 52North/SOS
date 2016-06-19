/*
 * Copyright (C) 2016 52north.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package DasHandling;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import opendap.dap.DConnect2;
import opendap.dap.AttributeTable;
import opendap.dap.DAP2Exception;
import opendap.dap.parser.ParseException;
import opendap.dap.DAS;
import opendap.dap.Attribute;
import opendap.dap.NoSuchAttributeException;
/**
 *
 * @author ankit
 */
public class AccessDas {

    private DConnect2 url = null;
    private String serverVersion = null;
    private Enumeration attribureEnum; // Store elements name -- GLOBAL,LAT,etc
    DAS das = null;
    /*To store lat {
       String units "degrees_north";
       String long_name "Latitude";
       Float64 actual_range 89.5, -89.5;
    }*/
    
    private Vector attributeElements;
    
    public AccessDas(){
        this.url = null;
    }
    
    public AccessDas(DConnect2 url){
        this.url = url;
        try {
            das = url.getDAS();
        } catch (IOException ex) {
            Logger.getLogger(AccessDas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(AccessDas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DAP2Exception ex) {
            Logger.getLogger(AccessDas.class.getName()).log(Level.SEVERE, null, ex);
        }
        InitializeAttribute();
    }
 
    public DAS getAttributeTable() throws DAP2Exception, ParseException{
        return das;
    }
    
    public void InitializeAttribute(){
        serverVersion = url.getServerVersion().toString();
        attribureEnum = das.getNames();
        while(attribureEnum.hasMoreElements()){
            attributeElements.add(das.
                                  getAttribute(
                                  attribureEnum.
                                  nextElement().
                                  toString()));
        }
        // need to cache this
    }
    public boolean checkIfAttributePresent(String name){
        Attribute a = das.getAttribute(name);
        if(a == null)
            return false;
        else
           return true;
    }
    public Enumeration getAttributeValueByName(String name){
        Attribute a = das.getAttribute(name);
        Enumeration e = null;
        if(a == null) return e;
        else{
            try {
                e = a.getValues();
            } catch (NoSuchAttributeException ex) {
                System.out.println(ex.getStackTrace());
            }
            return e;
        }
    }
    public Iterator getIteratorofAttribute(String name){
        Attribute a = das.getAttribute(name);
        return a.getValuesIterator();
    }
    
    public Enumeration getAllAttributeKeys(){
        return attributeElements;
    }
    
}
