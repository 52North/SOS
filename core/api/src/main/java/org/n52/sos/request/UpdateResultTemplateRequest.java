/*
 * Copyright (C) 2017 52north.org
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
package org.n52.sos.request;

import com.google.common.base.Strings;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.response.UpdateResultTemplateResponse;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 * J&uuml;rrens</a>
 *
 * @since 4.4.0
 */
public class UpdateResultTemplateRequest extends AbstractServiceRequest<UpdateResultTemplateResponse>{

    private String resultTemplate;
    private String resultStructure;
    private String resultEncoding;

    @Override
    public UpdateResultTemplateResponse getResponse() throws OwsExceptionReport {
        return (UpdateResultTemplateResponse) new UpdateResultTemplateResponse().set(this);
    }

    @Override
    public String getOperationName() {
        return "UpdateResultTemplate";
    }

    public boolean isSetResultTemplate() {
        return !Strings.isNullOrEmpty(resultTemplate);
    }

    public boolean isSetResultStructure() {
        return !Strings.isNullOrEmpty(resultStructure);
    }

    public boolean isSetResultEncoding() {
        return !Strings.isNullOrEmpty(resultEncoding);
    }

    public String getResultEncoding() {
        if (isSetResultEncoding()) {
            return resultEncoding;
        } else {
            return "";
        }
    }

    public String getResultTemplate() {
        if (isSetResultTemplate()) {
            return resultTemplate;
        } else {
            return "";
        }
    }
    
    public String getResultStructure() {
        if (isSetResultStructure()) {
            return resultStructure;
        } else {
            return "";
        }
    }

    public void setResultTemplate(String resultTemplate) {
        this.resultTemplate = resultTemplate;
    }

    public void setResultEncoding(String resultEncoding) {
        this.resultEncoding = resultEncoding;
    }
    
    public void setResultStructure(String resultStructure) {
        this.resultStructure = resultStructure;
    }
    
}
