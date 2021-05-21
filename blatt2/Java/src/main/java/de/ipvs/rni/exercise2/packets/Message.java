package de.ipvs.rni.exercise2.packets;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author bibartoo
 */
public class Message implements Serializable{
    
    private final static long serialVersionUID = 1; // See Nick's comment below

    
    String title;
    String body;

    public Message(String title, String body) {
        this.title = title;
        this.body = body;
    }
   
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }



    @Override
    public int hashCode() {
        int hash = title.hashCode() + body.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Message other = (Message) obj;
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        if (!Objects.equals(this.body, other.body)) {
            return false;
        }
        return true;
    }
}
