package com.vaporant.model;

import com.vaporant.repository.AddressScript;
import java.util.ArrayList;

import com.vaporant.repository.AddressDaoImpl;
import com.google.gson.Gson;
import java.sql.SQLException;
import java.util.List;

public class AddressList {
    private ArrayList<AddressScript> listaIndirizzi;
    private ArrayList<AddressBean> addresses;

    public AddressList() {
        this.listaIndirizzi = new ArrayList<>();
        this.addresses = new ArrayList<>();
    }

    public AddressList(UserBean user) {
        this.listaIndirizzi = new ArrayList<>();
        this.addresses = new ArrayList<>();
        AddressDaoImpl dao = new AddressDaoImpl();
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

    public String getJson() {
        Gson gson = new Gson();
        return gson.toJson(this.listaIndirizzi);
    }

    public void setListaIndirizzi(ArrayList<AddressScript> listaIndirizzi) {
        this.listaIndirizzi = listaIndirizzi;
    }

    public void add(AddressBean address) {
        this.listaIndirizzi.add(new AddressScript(address));
        this.addresses.add(address);
    }

    public void remove(int index) {
        if(index >= 0 && index < this.listaIndirizzi.size()) {
            this.listaIndirizzi.remove(index);
            if(index < this.addresses.size()) {
                this.addresses.remove(index);
            }
        }
    }

    public AddressScript get(int index) {
        if(index >= 0 && index < this.listaIndirizzi.size()) {
            return this.listaIndirizzi.get(index);
        }
        return null;
    }

    public int size() {
        return this.listaIndirizzi.size();
    }
}
