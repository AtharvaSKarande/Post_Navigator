package com.project.postnav;

import android.widget.ImageView;

import java.util.ArrayList;

public class recyclec_class {

    private String mName;
    private String Status;
    private ImageView imageView;
   // private boolean mOnline;

    public recyclec_class(String name,String status) {
        mName = name;
        Status = status;
       // mOnline = online;
    }

    public String getName() {
        return mName;
    }

    public String getStatus() {
        return Status;
    }

    /*  public static ArrayList<com.project.postnav.recyclec_class> createContactsList(int numContacts) {
        ArrayList<com.project.postnav.recyclec_class> contacts = new ArrayList<com.project.postnav.recyclec_class>();

        for (int i = 1; i <= numContacts; i++) {
            contacts.add(new com.project.postnav.recyclec_class(, i <= numContacts / 2));
        }

        return contacts;
    }*/
}
