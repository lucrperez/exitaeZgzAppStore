package es.zgzappstore.equipoa.handicapp;

/**
 * Created by Luis on 11/07/2015.
 */
public class SimpleItem {
    private int ID;
    private String title;

    public SimpleItem (int id, String title) {
        this.ID = id;
        this.title = title;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        title = title;
    }
}
