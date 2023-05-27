package com.example.isg3;

public class ReadWriteUserDetails {
    public String fullname , emailing , mdp , birth,departe ;



    public ReadWriteUserDetails(){};


    public ReadWriteUserDetails(String firstname, String email, String password , String birthday,String departement ){
        this.fullname = firstname;
        this.emailing = email;
        this.mdp = password;
        this.birth = birthday;
        this.departe = departement;


    }
}
