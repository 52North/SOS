package org.n52.sos.inspire.aqd.persistence;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "json_fragment")
public class JSONFragment {

    @Id
    private String id;
    private String json;

    public String getID() {
        return id;
    }

    public JSONFragment setID(String id) {
        this.id = id;
        return this;
    }

    public String getJSON() {
        return json;
    }

    public JSONFragment setJSON(String json) {
        this.json = json;
        return this;
    }

}
