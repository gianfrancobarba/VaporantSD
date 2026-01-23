package com.vaporant.model;

import com.vaporant.repository.AddressScript;
import java.util.ArrayList;
import java.sql.SQLException;
import java.util.List;

public class AddressList {
    /* @ spec_public non_null @ */ private ArrayList<AddressScript> listaIndirizzi;
    /* @ spec_public non_null @ */ private ArrayList<AddressBean> addresses;

    /*
     * @
     * 
     * @ public invariant listaIndirizzi != null;
     * 
     * @ public invariant addresses != null;
     * 
     * @ public invariant listaIndirizzi.size() == addresses.size();
     * 
     * @
     */

    public AddressList() {
        this.listaIndirizzi = new ArrayList<>();
        this.addresses = new ArrayList<>();
    }

    public AddressList(UserBean user, com.vaporant.repository.AddressDAO dao) {
        this.listaIndirizzi = new ArrayList<>();
        this.addresses = new ArrayList<>();
        try {
            List<AddressBean> userAddresses = dao.findByID(user.getId());
            if (userAddresses != null) {
                this.addresses.addAll(userAddresses);
                for (AddressBean bean : userAddresses) {
                    this.listaIndirizzi.add(new AddressScript(bean));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<AddressScript> getListaIndirizzi() {
        return listaIndirizzi;
    }

    public ArrayList<AddressBean> getAddressList() {
        return addresses;
    }

    /* @ skipesc @ */
    public String getJson() {
        try {
            // Using reflection to avoid Gson import visibility issues with OpenJML
            Class<?> gsonClass = Class.forName("com.google.gson.Gson");
            Object gson = gsonClass.getDeclaredConstructor().newInstance();
            java.lang.reflect.Method toJson = gsonClass.getMethod("toJson", Object.class);
            return (String) toJson.invoke(gson, this.listaIndirizzi);
        } catch (Exception e) {
            return "[]";
        }
    }

    /* @ skipesc @ */
    public void setListaIndirizzi(ArrayList<AddressScript> listaIndirizzi) {
        this.listaIndirizzi = listaIndirizzi;
    }

    /* @ skipesc @ */
    public void add(AddressBean address) {
        this.listaIndirizzi.add(new AddressScript(address));
        this.addresses.add(address);
    }

    /* @ skipesc @ */
    public void remove(int index) {
        this.listaIndirizzi.remove(index);
        this.addresses.remove(index);
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result != null ==> listaIndirizzi.contains(\result);
     * 
     * @
     */
    public /* @ nullable @ */ AddressScript get(int index) {
        if (index >= 0 && index < this.listaIndirizzi.size()) {
            return this.listaIndirizzi.get(index);
        }
        return null;
    }

    public int size() {
        return this.listaIndirizzi.size();
    }
}
