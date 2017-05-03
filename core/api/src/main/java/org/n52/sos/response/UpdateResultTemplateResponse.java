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
package org.n52.sos.response;

import com.google.common.base.Strings;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 * J&uuml;rrens</a>
 *
 * @since 4.4.0
 */
public class UpdateResultTemplateResponse extends AbstractServiceResponse {

    private String updatedResultTemplate;

    @Override
    public String getOperationName() {
        return "UpdateResultTemplate";
    }

    public UpdateResultTemplateResponse setUpdatedResultTemplate(String updatedResultTemplate) {
        this.updatedResultTemplate = updatedResultTemplate;
        return this;
    }

    public boolean isSetResultTemplate() {
        return !Strings.isNullOrEmpty(updatedResultTemplate);
    }

    public String getResultTemplate() {
        if (isSetResultTemplate()) {
            return updatedResultTemplate;
        } else {
            return "";
        }
    }
    
}
