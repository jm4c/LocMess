package pt.ulisboa.tecnico.cmov.locmess.location;

import android.app.Application;

import java.util.ArrayList;


/*Esta actividade serve para nao enviar strings/objectos entre actividades, ou seja cria-se uma estrutura de localizacoes e quando for preciso opera-se sobre a lista de localizacoes */
public class MyApplication extends Application {


    public class Location{


        private String _name;
        private String _longitude;
        private String _latitude;
        private int _radius;

        public Location(String name, String longitude, String latitude, int radius){
            _name = name;
            _longitude = longitude;
            _latitude = latitude;
            _radius = radius;
        }

        public String getName(){
            return _name;
        }

        public void setName(String name){
            _name = name;
        }

        public String getLongitude(){
            return _longitude;
        }

        public void setLongitude(String longitude){
            _longitude = longitude;
        }

        public String getLatitude(){
            return _latitude;
        }

        public void setLatitude(String latitude){
            _latitude = latitude;
        }

        public int getRadius(){
            return _radius;
        }

        public void setRadius(int radius){
            _radius = radius;
        }

    }

    private ArrayList<Location> locationArrayList = new ArrayList<Location>();

    public void addLocation(String name, String longitude, String latitude, int radius){
        locationArrayList.add(new Location(name,longitude,latitude,radius));
    }

    public void removeLocationArray(String name){
        for(Location loc : locationArrayList){
            if (loc.getName().equals(name)) {
                locationArrayList.remove(loc);
            }
        }
    }

    public void listLocation(){ /*TODO Tera que ser alterada dependendo como se quer mostrar a informacao*/
        for(Location loc : locationArrayList){
            System.out.println(loc.getName());
        }
    }

    public ArrayList<String> listLocationString(){
        ArrayList<String> arrayLocal = new ArrayList<String>();
        for(Location local : locationArrayList){
            arrayLocal.add(local.getName());
        }
        return arrayLocal;
    }



}
