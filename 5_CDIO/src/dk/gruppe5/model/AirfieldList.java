package dk.gruppe5.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AirfieldList {
	
	static List<Airfield> airfieldList = new ArrayList<Airfield>();
	
	public static void addAirfield(Airfield input){
        int nameFound = 0;
        for (int h = 0;h<airfieldList.size();h++){
        	if(airfieldList.get(h).name == input.name){
        		nameFound++;
        	}
        }
        if(nameFound==0){
        	airfieldList.add(input);
        }
	}
	
	public static List<Airfield> getArray(){
		return airfieldList;
	}
	
}
